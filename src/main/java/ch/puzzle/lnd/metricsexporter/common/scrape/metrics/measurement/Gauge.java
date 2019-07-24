package ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement;

import ch.puzzle.lnd.metricsexporter.common.scrape.labels.Labels;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement.exception.IncompatibleMeasurementsDetected;
import io.prometheus.client.Collector;

public class Gauge extends Measurement<Double, Gauge> {

    public static MeasurementValue<Double, Gauge> create() {
        return MeasurementValue.create(new Gauge());
    }

    @Override
    void addTo(Gauge metric) {
        doAddTo(metric);
    }

    @Override
    void addAll(Measurement<?, ?> measurement) throws IncompatibleMeasurementsDetected {
        measurement.addTo(this);
    }

    @Override
    Collector collect(String name, String help, Labels globalLabels) {
        var gauge = io.prometheus.client.Gauge.build()
                .name(name)
                .help(help)
                .labelNames(labelNames(globalLabels))
                .create();
        collect(measurementValue -> gauge.labels(measurementValue.labels(globalLabels).getValues())
                .set(measurementValue.value())
        );
        return gauge;
    }
}
