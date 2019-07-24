package ch.puzzle.lnd.metricsexporter.common.scrape;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.labels.Labels;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement.Measurement;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement.MeasurementCollector;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement.exception.IncompatibleMeasurementsDetected;
import io.prometheus.client.CollectorRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;

class MetricScraperExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricScraperExecutor.class);

    private final Map<String, MeasurementCollector> measurements;

    private final LndApi api;

    private final Iterable<MetricScraper<?>> scrapers;

    private volatile boolean hasErrors;

    MetricScraperExecutor(Iterable<MetricScraper<?>> scrapers, LndApi api) {
        this.api = api;
        this.scrapers = scrapers;
        measurements = new TreeMap<>();
        hasErrors = false;
    }

    void execute(ExecutorService executorService) {
        for (var scraper : scrapers) {
            executorService.submit(() -> scrape(scraper));
        }
    }

    private void scrape(MetricScraper<?> scraper) {
        Measurement<?, ?> measurement;
        try {
            measurement = scraper.scrape(api);
        } catch (Exception e) {
            reportError(e);
            return;
        }
        addMeasurement(scraper.name(), scraper.description(), measurement);
    }

    private synchronized void addMeasurement(String name, String description, Measurement<?, ?> measurement) {
        if (!measurements.containsKey(name)) {
            measurements.put(name, MeasurementCollector.create(measurement, description));
            return;
        }
        try {
            measurements.get(name).add(measurement);
        } catch (IncompatibleMeasurementsDetected incompatibleMeasurementsDetected) {
            reportError(incompatibleMeasurementsDetected);
        }
    }

    CollectorRegistry collect(Labels globalLabels) {
        // TODO: Check executorservice?
        var registry = new CollectorRegistry();
        for (final var resultEntry : measurements.entrySet()) {
            final var name = resultEntry.getKey();
            final var result = resultEntry.getValue();
            result.collect(name, globalLabels).register(registry);
        }
        return registry;
    }

    public boolean hasErrors() {
        return hasErrors;
    }

    private void reportError(Throwable cause) {
        LOGGER.error("Error detected during metric scrape: {}", cause.getMessage());
        hasErrors = true;
    }
}
