package ch.puzzle.lnd.metricsexporter.common.scrape.config.exception;

public abstract class ScrapeConfigLookupException extends Exception{

    ScrapeConfigLookupException(String message) {
        super(message);
    }

    ScrapeConfigLookupException(String message, Throwable cause) {
        super(message, cause);
    }
}
