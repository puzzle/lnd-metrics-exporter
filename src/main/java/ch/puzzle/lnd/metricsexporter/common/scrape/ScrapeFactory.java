package ch.puzzle.lnd.metricsexporter.common.scrape;

import ch.puzzle.lnd.metricsexporter.common.api.ApiFactory;
import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.config.ScrapeConfig;
import ch.puzzle.lnd.metricsexporter.common.scrape.config.exception.InvalidScrapeConfigException;
import ch.puzzle.lnd.metricsexporter.common.scrape.labels.LabelProvider;
import ch.puzzle.lnd.metricsexporter.common.scrape.labels.Labels;
import ch.puzzle.lnd.metricsexporter.common.scrape.labels.providerregistry.LabelProviderRegistry;
import ch.puzzle.lnd.metricsexporter.common.scrape.labels.providerregistry.NoSuchLabelProviderException;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.scraperregistry.MetricScraperRegistry;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.scraperregistry.NoSuchMetricScraperException;
import io.prometheus.client.Counter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

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

    public Scrape create(ScrapeConfig config) throws InvalidScrapeConfigException {
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

    private LabelProviderExecutor createLabelProviderExecutor(
            List<String> labelProviders,
            LndApi api
    ) throws InvalidScrapeConfigException {
        return new LabelProviderExecutor(findLabelProviders(labelProviders), api);
    }

    private MetricScraperExecutor createMetricScraperExecutor(
            List<String> metrics,
            LndApi api
    ) throws InvalidScrapeConfigException {
        return new MetricScraperExecutor(findMetricScrapers(metrics), api);
    }

    private Iterable<MetricScraper<?>> findMetricScrapers(List<String> names) throws InvalidScrapeConfigException {
        var metricScrapers = new LinkedList<MetricScraper<?>>();
        for (var name : names) {
            try {
                metricScrapers.add(metricScraperRegistry.lookup(name));
            } catch (NoSuchMetricScraperException e) {
                throw InvalidScrapeConfigException.invalidConfig(e);
            }
        }
        return metricScrapers;
    }

    private Iterable<LabelProvider> findLabelProviders(List<String> names) throws InvalidScrapeConfigException {
        var labelProviders = new LinkedList<LabelProvider>();
        for (var name : names) {
            try {
                labelProviders.add(labelProviderRegistry.lookup(name));
            } catch (NoSuchLabelProviderException e) {
                throw InvalidScrapeConfigException.invalidConfig(e);
            }
        }
        return labelProviders;
    }
}
