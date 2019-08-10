package ch.puzzle.lnd.metricsexporter.common.scrape.metrics;

import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ParameterizedMetricScraperRegistry implements MetricScraperRegistry {

    private volatile Map<String, MetricScraper> scrapers;

    public ParameterizedMetricScraperRegistry() {
        scrapers = new HashMap<>();
    }

    @Override
    public MetricScraper find(String name) {
        return scrapers.get(name); // FIXME - > not found?
    }

    public void register(String parametrizationName, MetricScraper metricScraper) {
        scrapers.put(
                String.format("%s.%s", metricScraper.name(), parametrizationName),
                metricScraper
        );
    }

}
