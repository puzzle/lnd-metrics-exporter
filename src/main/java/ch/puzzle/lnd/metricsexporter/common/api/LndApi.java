package ch.puzzle.lnd.metricsexporter.common.api;

import io.netty.handler.ssl.SslContext;
import org.lightningj.lnd.wrapper.AsynchronousLndAPI;
import org.lightningj.lnd.wrapper.MacaroonContext;
import org.lightningj.lnd.wrapper.StatusException;
import org.lightningj.lnd.wrapper.SynchronousLndAPI;

public class LndApi {

    private final Object SYNCHRONOUS_API_INIT_LOCK = new Object();

    private final Object ASYNCHRONOUS_API_INIT_LOCK = new Object();

    private final String host;

    private final int port;

    private final SslContext sslContext;

    private final MacaroonContext macaroonContext;

    private volatile SynchronousLndAPI synchronousLndAPI;

    private volatile AsynchronousLndAPI asynchronousLndAPI;

    public LndApi(String host, int port, SslContext sslContext, MacaroonContext macaroonContext) {
        this.host = host;
        this.port = port;
        this.sslContext = sslContext;
        this.macaroonContext = macaroonContext;
    }

    public SynchronousLndAPI synchronous() {
        if (null == synchronousLndAPI) {
            initSynchronousApi();
        }
        return synchronousLndAPI;
    }

    public AsynchronousLndAPI asynchronous() {
        if (null == asynchronousLndAPI) {
            initAsynchronousApi();
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

    private void initSynchronousApi() {
        synchronized (SYNCHRONOUS_API_INIT_LOCK) {
            if (null != synchronousLndAPI) {
                return; // Another thread was faster
            }
            synchronousLndAPI = new SynchronousLndAPI(
                    host,
                    port,
                    sslContext,
                    macaroonContext
            );
        }
    }

    private void initAsynchronousApi() {
        synchronized (ASYNCHRONOUS_API_INIT_LOCK) {
            if (null != asynchronousLndAPI) {
                return; // Another thread was faster
            }
            asynchronousLndAPI = new AsynchronousLndAPI(
                    host,
                    port,
                    sslContext,
                    macaroonContext
            );
        }
    }
}
