package ch.puzzle.lnd.metricsexporter.common.scrape.config.exception;

public abstract class ScrapeConfigException extends Exception {

    ScrapeConfigException(String message) {
        super(message);
    }

    ScrapeConfigException(String message, Throwable cause) {
        super(message, cause);
    }
}
