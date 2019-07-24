package ch.puzzle.lnd.metricsexporter.common.scrape.metrics;

public interface MetricScraperRegistry {

    MetricScraper<?> find(String name);
}
