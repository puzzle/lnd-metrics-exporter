package ch.puzzle.lnd.metricsexporter.common.scrape.config.exception;

import org.lightningj.lnd.wrapper.ClientSideException;

import java.io.File;

public class InvalidScrapeConfigException extends ScrapeConfigException {

    public InvalidScrapeConfigException(String message) {
        super(message);
    }

    private InvalidScrapeConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public static InvalidScrapeConfigException invalidCertificate(Exception cause) {
        return new InvalidScrapeConfigException(
                "Unable to create SSL context.",
                cause
        );
    }

    public static InvalidScrapeConfigException invalidMacaroon(ClientSideException cause, File macaroon) {
        return new InvalidScrapeConfigException(
                String.format(
                        "Unable to create Macaroon context for '%s'",
                        macaroon
                ),
                cause
        );
    }

    public static InvalidScrapeConfigException invalidMacaroon(File macaroonFile) {
        return new InvalidScrapeConfigException(
                String.format(
                        "Macaroon file '%s' does not exist or is not accessible.",
                        macaroonFile
                )
        );
    }

    public static InvalidScrapeConfigException invalidConfig(Exception cause) {
        return new InvalidScrapeConfigException(
                String.format(
                        "Invalid config detected: %s",
                        cause.getMessage()
                )
        );
    }
}
