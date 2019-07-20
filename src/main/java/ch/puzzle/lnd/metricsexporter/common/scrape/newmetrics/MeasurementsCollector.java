package ch.puzzle.lnd.metricsexporter.common.scrape.newmetrics;

import ch.puzzle.lnd.metricsexporter.common.scrape.labels.Labels;
import io.prometheus.client.Collector;

public class MeasurementsCollector {

    private final Measurement<?, ?> measurement;

    private MeasurementsCollector(Measurement<?, ?> measurement) {
        this.measurement = measurement;
    }

    public static MeasurementsCollector using(Measurement<?, ?> measurement) {
        return new MeasurementsCollector(measurement);
    }

    public Collector collect(String name, String help, Labels globalLabels) {
        return measurement.collect(name, help, globalLabels);
    }
}
