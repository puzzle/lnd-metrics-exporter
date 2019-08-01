package ch.puzzle.lnd.metricsexporter.common.scrape;

import ch.puzzle.lnd.metricsexporter.common.scrape.labels.Labels;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class Scrape {

    private final LabelProviderExecutor labelProviderExecutor;

    private ExecutorService executorService;

    private MetricScraperExecutor metricScraperExecutor;

    private ScrapeSuccessfulCollectorFactory scrapeSuccessfulCollectorFactory;

    Scrape(
            MetricScraperExecutor metricScraperExecutor,
            LabelProviderExecutor labelProviderExecutor,
            ScrapeSuccessfulCollectorFactory scrapeSuccessfulCollectorFactory
    ) {
        this.metricScraperExecutor = metricScraperExecutor;
        this.labelProviderExecutor = labelProviderExecutor;
        this.scrapeSuccessfulCollectorFactory = scrapeSuccessfulCollectorFactory;
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
        var globalLabels = labelProviderExecutor.collect();
        var registry = metricScraperExecutor.collect(globalLabels);
        var scrapeSuccessfulCollector = scrapeSuccessfulCollectorFactory.create(globalLabels);
        registry.register(scrapeSuccessfulCollector);
        if (!labelProviderExecutor.hasErrors() && !metricScraperExecutor.hasErrors()) {
            scrapeSuccessfulCollector.labels(globalLabels.getValues()).inc();
        }
        return registry;
    }

    static interface ScrapeSuccessfulCollectorFactory {

        Counter create(Labels labels);

    }
}
