package ch.puzzle.lnd.metricsexporter.common.scrape.metrics.scraperregistry;

import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Primary
public class RootMetricScraperRegistry implements MetricScraperRegistry {

    private final List<MetricScraperRegistry> registries;

    public RootMetricScraperRegistry(List<MetricScraperRegistry> registries) {
        this.registries = registries;
    }

    @Override
    public MetricScraper lookup(String name) throws NoSuchMetricScraperException {
        for (MetricScraperRegistry registry : registries) {
            try {
                return registry.lookup(name);
            } catch (NoSuchMetricScraperException e) {
                // Try next registry
            }
        }
        throw new NoSuchMetricScraperException(name);
    }
}
