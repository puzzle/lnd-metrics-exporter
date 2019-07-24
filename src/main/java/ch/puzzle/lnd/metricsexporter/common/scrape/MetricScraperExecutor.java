package ch.puzzle.lnd.metricsexporter.common.scrape;

import ch.puzzle.lnd.metricsexporter.common.api.LndApi;
import ch.puzzle.lnd.metricsexporter.common.scrape.labels.Labels;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.MetricScraper;
import ch.puzzle.lnd.metricsexporter.common.scrape.newmetrics.IncompatibleMeasurementsDetected;
import ch.puzzle.lnd.metricsexporter.common.scrape.newmetrics.Measurement;
import io.prometheus.client.CollectorRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutorService;

class MetricScraperExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricScraperExecutor.class);

    private final Map<String, Result> measurements;

    private final LndApi api;

    private final Iterable<MetricScraper<?>> scrapers;

    private volatile boolean hasErrors;

    MetricScraperExecutor(Iterable<MetricScraper<?>> scrapers, LndApi api) {
        this.api = api;
        this.scrapers = scrapers;
        measurements = Collections.synchronizedMap(new TreeMap<>());
        hasErrors = false;
    }

    void execute(ExecutorService executorService) {
        for (var scraper : scrapers) {
            executorService.submit(() -> scrape(scraper));
        }
    }

    private void scrape(MetricScraper<?> scraper) {
        var name = scraper.name();
        Measurement<?, ?> measurement;
        try {
            measurement = scraper.scrape(api);
        } catch (Exception e) {
            reportError(e);
            return;
        }
        if (!measurements.containsKey(name)) {
            measurements.put(name, new Result(scraper.description(), measurement));
            return;
        }
        try {
            measurements.get(name).measurement.addAll(measurement);
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
            result.measurement.collect(name, result.description, globalLabels)
                    .register(registry);
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

    private static class Result {

        private final String description;

        private final Measurement<?, ?> measurement;

        private Result(String description, Measurement measurement) {
            this.description = description;
            this.measurement = measurement;
        }
    }

}
