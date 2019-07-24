package ch.puzzle.lnd.metricsexporter.common.scrape;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.labels.LabelProvider;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import io.prometheus.client.Collector;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Scrape {

    private final LabelProviderExecutor labelProviderExecutor;

    private ExecutorService executorService;

    private MetricScraperExecutor metricScraperExecutor;

    private Counter scrapeSuccessfulCollector;

    Scrape(
            MetricScraperExecutor metricScraperExecutor,
            LabelProviderExecutor labelProviderExecutor,
            Counter scrapeSuccessfulCollector
    ) {
        this.metricScraperExecutor = metricScraperExecutor;
        this.labelProviderExecutor = labelProviderExecutor;
        this.scrapeSuccessfulCollector = scrapeSuccessfulCollector;
    }

    public void start(int numberOfThreads) {
        if (executorService != null) {
            throw new IllegalStateException("Scrape already started.");
        }
        executorService = Executors.newFixedThreadPool(numberOfThreads);
        metricScraperExecutor.execute(executorService);
        labelProviderExecutor.execute(executorService);
    }

    public CollectorRegistry collect(int timeout, TimeUnit unit) {
        executorService.shutdown();
        try {
            executorService.awaitTermination(timeout, unit);
        } catch (InterruptedException e) {
            e.printStackTrace(); // FIXME
        }
        var registry = metricScraperExecutor.collect(labelProviderExecutor.collect());
        registry.register(scrapeSuccessfulCollector);
        if (labelProviderExecutor.hasErrors() || metricScraperExecutor.hasErrors()) {
            scrapeSuccessfulCollector.inc();
        }
        return registry;
    }

}
