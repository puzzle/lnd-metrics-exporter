package ch.puzzle.lnd.metricsexporter.common.scrape.metrics;

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
    public MetricScraper find(String name) {
        for (MetricScraperRegistry registry : registries) {
            if (registry.find(name) != null) {
                return registry.find(name);
            }
        }
        return null; // TODO: Throw invalid config error?
    }
}
