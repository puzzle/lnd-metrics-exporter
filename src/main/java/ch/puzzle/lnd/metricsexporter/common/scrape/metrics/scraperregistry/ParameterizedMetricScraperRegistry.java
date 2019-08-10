package ch.puzzle.lnd.metricsexporter.common.scrape.metrics.scraperregistry;

import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
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
    public MetricScraper lookup(String name) throws NoSuchMetricScraperException {
        if (!scrapers.containsKey(name)) {
            throw new NoSuchMetricScraperException(name);
        }
        return scrapers.get(name);
    }

    public void register(String parametrizationName, MetricScraper metricScraper) {
        scrapers.put(
                String.format("%s.%s", metricScraper.name(), parametrizationName),
                metricScraper
        );
    }

}
