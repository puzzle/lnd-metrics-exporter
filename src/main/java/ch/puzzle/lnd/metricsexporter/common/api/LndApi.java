package ch.puzzle.lnd.metricsexporter.common.api;

import io.netty.handler.ssl.SslContext;
import org.lightningj.lnd.wrapper.AsynchronousLndAPI;
import org.lightningj.lnd.wrapper.MacaroonContext;
import org.lightningj.lnd.wrapper.StatusException;
import org.lightningj.lnd.wrapper.SynchronousLndAPI;

public class LndApi {

    private final String host;

    private final int port;

    private final SslContext sslContext;

    private final MacaroonContext macaroonContext;

    private SynchronousLndAPI synchronousLndAPI;

    private AsynchronousLndAPI asynchronousLndAPI;

    public LndApi(String host, int port, SslContext sslContext, MacaroonContext macaroonContext) {
        this.host = host;
        this.port = port;
        this.sslContext = sslContext;
        this.macaroonContext = macaroonContext;
    }

    public SynchronousLndAPI synchronous() {
        if (null == synchronousLndAPI) {
            synchronousLndAPI = new SynchronousLndAPI(
                    host,
                    port,
                    sslContext,
                    macaroonContext
            );
        }
        return synchronousLndAPI;
    }

    public AsynchronousLndAPI asynchronous() {
        if (null == asynchronousLndAPI) {
            asynchronousLndAPI = new AsynchronousLndAPI(
                    host,
                    port,
                    sslContext,
                    macaroonContext
            );
        }
        return asynchronousLndAPI;
    }

    public void close() throws StatusException {
        StatusException exception = null;
        if (null != synchronousLndAPI) {
            try {
                synchronousLndAPI.close();
            } catch (StatusException e) {
                exception = e;
            }
        }
        if (null != asynchronousLndAPI) {
            try {
                asynchronousLndAPI.close();
            } catch (StatusException e) {
                exception = e;
            }
        }
        if (null != exception) {
            throw exception;
        }
    }
}
