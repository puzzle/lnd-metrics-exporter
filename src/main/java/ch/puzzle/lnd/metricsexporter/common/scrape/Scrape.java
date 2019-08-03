package ch.puzzle.lnd.metricsexporter.common.scrape;

import ch.puzzle.lnd.metricsexporter.common.scrape.labels.Labels;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.Counter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Scrape {

    private static Logger LOGGER = LoggerFactory.getLogger(Scrape.class);

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
            return handleThreadInterrupt();
        }
        var globalLabels = labelProviderExecutor.collect();
        var registry = metricScraperExecutor.collect(globalLabels);
        var scrapeSuccessfulCollector = scrapeSuccessfulCollectorFactory.create(globalLabels);
        registry.register(scrapeSuccessfulCollector);
        if (wasSuccessful()) {
            scrapeSuccessfulCollector.labels(globalLabels.getValues()).inc();
        }
        return registry;
    }

    private CollectorRegistry handleThreadInterrupt() {
        var registry = new CollectorRegistry();
        LOGGER.error("Thread was interrupted. Returning empty result.");
        scrapeSuccessfulCollectorFactory.create(Labels.create()).register(registry);
        return registry;
    }

    private boolean wasSuccessful() {
        if (!executorService.isTerminated()) {
            LOGGER.error("Unable to perform all configured scrapes. You should consider increasing the scrape timeout.");
            return false;
        }
        return !labelProviderExecutor.hasErrors() && !metricScraperExecutor.hasErrors();
    }

    static interface ScrapeSuccessfulCollectorFactory {

        Counter create(Labels labels);

    }
}
