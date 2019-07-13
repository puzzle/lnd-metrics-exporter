package ch.puzzle.lnd.metricsexporter.common.scrape.config.exception;

import org.lightningj.lnd.wrapper.ClientSideException;

import java.io.File;

public class InvalidConfigDetected extends ScrapeConfigLookupException {

    public InvalidConfigDetected(String message) {
        super(message);
    }

    private InvalidConfigDetected(String message, Throwable cause) {
        super(message, cause);
    }

    public static InvalidConfigDetected create(Exception cause) {
        return new InvalidConfigDetected(
                "Unable to create SSLContext.",
                cause
        );
    }

    public static InvalidConfigDetected create(ClientSideException cause, File macaroon) {
        return new InvalidConfigDetected(
                String.format(
                        "Unable to create Macaroon context for '%s'",
                        macaroon
                ),
                cause
        );
    }

    public static InvalidConfigDetected noSchuchMacaroon(File macaroonFile) {
        return new InvalidConfigDetected(
                String.format(
                        "Macaroon file '%s' does not exist or is not accessible.",
                        macaroonFile
                )
        );
    }
}
