package ch.puzzle.lnd.metricsexporter.common.scrape.metrics.scraperregistry;

import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;

public interface MetricScraperRegistry {

    MetricScraper<?> lookup(String name) throws NoSuchMetricScraperException;
}
