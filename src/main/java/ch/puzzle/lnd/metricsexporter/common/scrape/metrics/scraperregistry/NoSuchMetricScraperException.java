package ch.puzzle.lnd.metricsexporter.common.scrape.metrics.scraperregistry;

public class NoSuchMetricScraperException extends Exception {

    public NoSuchMetricScraperException(String name) {
        super(String.format("No metric scraper with name '%s' found.", name));
    }

}
