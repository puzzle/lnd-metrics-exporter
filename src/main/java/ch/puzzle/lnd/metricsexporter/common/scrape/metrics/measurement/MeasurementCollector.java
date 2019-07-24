package ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement;

import ch.puzzle.lnd.metricsexporter.common.scrape.labels.Labels;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement.exception.IncompatibleMeasurementsDetected;
import io.prometheus.client.Collector;

public class MeasurementCollector {

    private final Measurement<?, ?> measurement;

    private final String help;

    private MeasurementCollector(Measurement<?, ?> measurement, String help) {
        this.measurement = measurement;
        this.help = help;
    }

    public static MeasurementCollector create(Measurement<?, ?> measurement, String help) {
        return new MeasurementCollector(measurement, help);
    }

    public Collector collect(String name, String help, Labels globalLabels) {
        return measurement.collect(name, help, globalLabels);
    }

    public void add(Measurement<?, ?> measurement) throws IncompatibleMeasurementsDetected {
        measurement.addAll(measurement);
    }

    public Collector collect(String name, Labels globalLabels) {
        return measurement.collect(name, help, globalLabels);
    }
}
