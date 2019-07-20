package ch.puzzle.lnd.metricsexporter.common.scrape;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.labels.LabelProvider;
import ch.puzzle.lnd.metricsexporter.common.scrape.labels.Labels;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import ch.puzzle.lnd.metricsexporter.common.scrape.newmetrics.Measurement;
import ch.puzzle.lnd.metricsexporter.common.scrape.newmetrics.MeasurementsCollector;
import io.prometheus.client.CollectorRegistry;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Scrape {

    private final LndApi api;

    private final Iterable<MetricScraper> scrapers;

    private final Iterable<LabelProvider> labelProviders;

    private Labels labels;

    private Map<String, Measurement<?, ?>> measurements;

    private AtomicInteger errorCount;

    private ExecutorService executorService;

    Scrape(Iterable<MetricScraper> scrapers, Iterable<LabelProvider> labelProviders, LndApi api) {
        this.scrapers = scrapers;
        this.labelProviders = labelProviders;
        this.api = api;
    }

    public void start(int numberOfThreads) {
        labels = Labels.create();
        measurements = Collections.synchronizedMap(new HashMap<>());
        errorCount = new AtomicInteger(0);
        executorService = Executors.newFixedThreadPool(numberOfThreads);
        scrapers.forEach(this::scrape);
        labelProviders.forEach(this::loadLabels);
    }

    public CollectorRegistry awaitTermination(int timeout, TimeUnit unit) {
        executorService.shutdown();
        try {
            executorService.awaitTermination(timeout, unit);
        } catch (InterruptedException e) {
            e.printStackTrace(); // FIXME
        }
        var registry = new CollectorRegistry();
        measurements.forEach((name, measurement) -> MeasurementsCollector.using(measurement).collect(
                name,
                "", // FIXME
                labels
        ).register(registry));
        return registry;
    }

    private void scrape(MetricScraper scraper) {
        executorService.submit(() -> {
            try {
                var name = scraper.name();
                if (measurements.containsKey(name)) {
                    measurements.get(name).addAll(scraper.scrape(api));
                } else {
                    measurements.put(name, scraper.scrape(api));
                }
            } catch (Exception e) {
                errorCount.incrementAndGet();
            }
        });
    }

    private void loadLabels(LabelProvider labelProvider) {
        executorService.submit(() -> {
            Labels labels = null;
            try {
                labels = labelProvider.provide(api);
            } catch (Exception e) {
                errorCount.incrementAndGet(); // FIXME: separate counter
            }
            synchronized (this) {
                this.labels = this.labels.merge(labels);
            }
        });
    }
}
