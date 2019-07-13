package ch.puzzle.lnd.metricsexporter.common.scrape;

import ch.puzzle.lnd.metricsexporter.common.api.ApiFactory;
import ch.puzzle.lnd.metricsexporter.common.scrape.config.ScrapeConfig;
import ch.puzzle.lnd.metricsexporter.common.scrape.labels.LabelProvider;
import ch.puzzle.lnd.metricsexporter.common.scrape.labels.LabelProviderRegistry;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraperRegistry;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ScrapeFactory {

    private final ApiFactory apiFactory;

    private final MetricScraperRegistry metricScraperRegistry;

    private final LabelProviderRegistry labelProviderRegistry;

    public ScrapeFactory(
            ApiFactory apiFactory,
            MetricScraperRegistry metricScraperRegistry,
            LabelProviderRegistry labelProviderRegistry
    ) {
        this.apiFactory = apiFactory;
        this.metricScraperRegistry = metricScraperRegistry;
        this.labelProviderRegistry = labelProviderRegistry;
    }

    public Scrape create(ScrapeConfig config) {
        return new Scrape(
                findMetricScrapers(config.getMetricNames()),
                findLabelProviders(config.getLabelProviderNames()),
                apiFactory.create(config)
        );
    }

    private Iterable<MetricScraper> findMetricScrapers(List<String> metrics) {
        return metrics.stream()
                .map(metricScraperRegistry::find)
                .collect(Collectors.toList());
    }

    private Iterable<LabelProvider> findLabelProviders(List<String> labelProviders) {
        return labelProviders.stream()
                .map(labelProviderRegistry::find)
                .collect(Collectors.toList());
    }
}
