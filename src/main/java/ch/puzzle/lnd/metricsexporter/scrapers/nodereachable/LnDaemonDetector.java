package ch.puzzle.lnd.metricsexporter.scrapers.nodereachable;

import com.southernstorm.noise.protocol.Noise;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.generators.ECKeyPairGenerator;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.params.*;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.ShortBufferException;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import static com.google.common.primitives.Bytes.concat;

/**
 * Checks whether a LN daemon ist listening under a provided URI.
 * <p>
 * In order to achieve this, the first act of a LN Connect Handshake according to BOLT-8 is performed.
 */
class LnDaemonDetector {

    private static final Logger LOGGER = LoggerFactory.getLogger(LnDaemonDetector.class);

    private static final byte[] PROTOCOL_NAME = "Noise_XK_secp256k1_ChaChaPoly_SHA256".getBytes(StandardCharsets.US_ASCII);

    private static final byte[] PROLOGUE = "lightning".getBytes(StandardCharsets.US_ASCII);

    private static final String DIGEST_ALGORITHM = "SHA-256";

    private static final String ELLIPTIC_CURVE = "secp256k1";

    private static final String NOISE_CIPHER_ALGORITHM = "ChaChaPoly";

    private static final int HANDSHAKE_ACT_ONE_RESPONSE_SIZE = 50;

    private final Socket socket;

    LnDaemonDetector() {
        this(new Socket());
    }

    LnDaemonDetector(Socket socket) {
        this.socket = socket;
    }

    boolean isLnDaemonRunning(String uri) {
        try {
            return isLnDaemonRunning(new URI(String.format("lnd://%s", uri)));
        } catch (URISyntaxException e) {
            LOGGER.error("Invalid LN URI provided: {}", uri);
            return false;
        }
    }

    boolean isLnDaemonRunning(URI uri) {
        byte[] payload;
        try {
            payload = generatePayload(DatatypeConverter.parseHexBinary(uri.getUserInfo()));
        } catch (ShortBufferException | NoSuchAlgorithmException e) {
            LOGGER.error("Unable to generate LN handshake payload: {}", e.getMessage());
            return false;
        }
        try {
            socket.connect(new InetSocketAddress(uri.getHost(), uri.getPort()));
            var out = socket.getOutputStream();
            out.write(payload);
            out.flush();
        } catch (IOException e) {
            LOGGER.error("Unable to send LN handshake payload: {}", e.getMessage());
            closeSocket();
            return false;
        }
        byte[] response = new byte[0];
        try {
            response = socket.getInputStream().readAllBytes();
        } catch (IOException e) {
            LOGGER.error("Unable to read response: {}", e.getMessage());
            e.printStackTrace();
        }
        closeSocket();
        return isValidLnDaemonResponse(response);
    }

    private void closeSocket() {
        try {
            socket.close();
        } catch (IOException e) {
            LOGGER.error("Unable to close socket: {}", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Generates a LND Handshake Act One Payload according to BOLT-8.
     * For a detailed explanation of the algorithm and the variables, please have a look at BOLT-8.
     *
     * @param rs The node's public key
     * @return A LND Handshake Act One Payload according to BOLT-8
     * @throws ShortBufferException
     * @throws NoSuchAlgorithmException
     * @link https://github.com/lightningnetwork/lightning-rfc/blob/master/08-transport.md#handshake-state
     */
    private byte[] generatePayload(byte[] rs) throws ShortBufferException, NoSuchAlgorithmException {
        var digest = MessageDigest.getInstance(DIGEST_ALGORITHM);

        // BOLT-8 Handshake State Initialization
        var h = digest.digest(PROTOCOL_NAME);
        var ck = h;

        digest.update(h);
        h = digest.digest(PROLOGUE);

        digest.update(h);
        h = digest.digest(rs);

        // BOLT-8 Handshake Exchange
        var e = generateKeyPair();
        var ePublicKey = e.pub.getQ().getEncoded(true);

        digest.update(h);
        h = digest.digest(ePublicKey);

        var es = ecdh(e.priv, rs);
        var tempIntermediateKey = new byte[32];
        var hkdf = hkdf(ck, es);
        System.arraycopy(hkdf, 32, tempIntermediateKey, 0, 32);

        var c = encryptWithAd(tempIntermediateKey, h);

        return concat(new byte[]{0}, ePublicKey, c);
    }

    private KeyPair generateKeyPair() {
        var curve = ECNamedCurveTable.getParameterSpec(ELLIPTIC_CURVE);
        var domainParams = new ECDomainParameters(curve.getCurve(), curve.getG(), curve.getN(), curve.getH(), curve.getSeed());
        var secureRandom = new SecureRandom();
        var keyParams = new ECKeyGenerationParameters(domainParams, secureRandom);
        var generator = new ECKeyPairGenerator();
        generator.init(keyParams);
        var keyPair = generator.generateKeyPair();
        var privateKey = (ECPrivateKeyParameters) keyPair.getPrivate();
        var publicKey = (ECPublicKeyParameters) keyPair.getPublic();
        return new KeyPair(publicKey, privateKey);
    }

    private byte[] ecdh(ECPrivateKeyParameters priv, byte[] rs) throws NoSuchAlgorithmException {
        final var spec = ECNamedCurveTable.getParameterSpec(ELLIPTIC_CURVE);
        final var point = spec.getCurve().decodePoint(rs);
        final var scalar = priv.getD();
        final var dotProduct = point.multiply(scalar).normalize();
        final var bytes = dotProduct.getEncoded(true);
        return MessageDigest.getInstance(DIGEST_ALGORITHM).digest(bytes);
    }

    private byte[] hkdf(byte[] salt, byte[] ikm) {
        var params = new HKDFParameters(ikm, salt, new byte[]{});
        var hkdf = new HKDFBytesGenerator(new SHA256Digest());
        hkdf.init(params);
        int keyLengthBytes = 64;
        byte[] okm = new byte[keyLengthBytes];
        hkdf.generateBytes(okm, 0, keyLengthBytes);
        return okm;
    }

    private byte[] encryptWithAd(byte[] key, byte[] ad) throws NoSuchAlgorithmException, ShortBufferException {
        final var cipher = Noise.createCipher(NOISE_CIPHER_ALGORITHM);
        cipher.setNonce(0);
        cipher.initializeKey(key, 0);
        final var plaintext = new byte[]{}; // zero length plain text, see BOLT-8
        final var cipherText = new byte[cipher.getMACLength()];
        cipher.encryptWithAd(ad, plaintext, 0, cipherText, 0, plaintext.length);
        return cipherText;
    }

    private boolean isValidLnDaemonResponse(byte[] payload) {
        return HANDSHAKE_ACT_ONE_RESPONSE_SIZE == payload.length;
    }

    private static class KeyPair {

        private final ECPublicKeyParameters pub;
        private final ECPrivateKeyParameters priv;

        private KeyPair(ECPublicKeyParameters pub, ECPrivateKeyParameters priv) {
            this.pub = pub;
            this.priv = priv;
        }
    }
}
