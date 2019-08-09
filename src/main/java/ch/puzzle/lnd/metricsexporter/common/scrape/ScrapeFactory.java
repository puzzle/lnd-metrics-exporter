package ch.puzzle.lnd.metricsexporter.common.scrape;

import ch.puzzle.lnd.metricsexporter.common.api.ApiFactory;
import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.config.ScrapeConfig;
import ch.puzzle.lnd.metricsexporter.common.scrape.labels.LabelProvider;
import ch.puzzle.lnd.metricsexporter.common.scrape.labels.LabelProviderRegistry;
import ch.puzzle.lnd.metricsexporter.common.scrape.labels.Labels;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraperRegistry;
import io.prometheus.client.Counter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ScrapeFactory {

    private static final String SCRAPE_SUCCESSFUL_METRIC_HELP =
            "Shows whether the scrape was successful (1) or not (0).";

    private final ApiFactory apiFactory;

    private final MetricScraperRegistry metricScraperRegistry;

    private final LabelProviderRegistry labelProviderRegistry;

    @Value("${lnd.scraping.successMetricName}")
    private String scrapeSuccessfulMetricName;

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
        var api = apiFactory.create(config);
        return new Scrape(
                createMetricScraperExecutor(config.getMetricNames(), api),
                createLabelProviderExecutor(config.getLabelProviderNames(), api),
                createScrapeSuccessfulCounter(),
                api
        );
    }

    private Scrape.ScrapeSuccessfulCollectorFactory createScrapeSuccessfulCounter() {
        return (Labels labels) -> Counter.build()
                .name(scrapeSuccessfulMetricName)
                .help(SCRAPE_SUCCESSFUL_METRIC_HELP)
                .labelNames(labels.getNames())
                .create();
    }

    private LabelProviderExecutor createLabelProviderExecutor(List<String> labelProviders, LndApi api) {
        return new LabelProviderExecutor(findLabelProviders(labelProviders), api);
    }

    private MetricScraperExecutor createMetricScraperExecutor(List<String> metrics, LndApi api) {
        return new MetricScraperExecutor(findMetricScrapers(metrics), api);
    }

    private Iterable<MetricScraper<?>> findMetricScrapers(List<String> metrics) {
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
