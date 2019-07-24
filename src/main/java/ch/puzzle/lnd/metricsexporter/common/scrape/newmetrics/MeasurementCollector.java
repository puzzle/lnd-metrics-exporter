package ch.puzzle.lnd.metricsexporter.common.scrape.newmetrics;

import ch.puzzle.lnd.metricsexporter.common.scrape.labels.Labels;
import io.prometheus.client.Collector;

public class MeasurementCollector {

    private final Measurement<?, ?> measurement;

    private MeasurementCollector(Measurement<?, ?> measurement) {
        this.measurement = measurement;
    }

    public static MeasurementCollector using(Measurement<?, ?> measurement) {
        return new MeasurementCollector(measurement);
    }

    public Collector collect(String name, String help, Labels globalLabels) {
        return measurement.collect(name, help, globalLabels);
    }
}
