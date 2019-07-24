package ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement;

import ch.puzzle.lnd.metricsexporter.common.scrape.labels.Labels;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement.exception.IncompatibleMeasurementsDetected;
import io.prometheus.client.Collector;

public class Summary extends Measurement<Double, Summary> {

    public static MeasurementValue<Double, Summary> create() {
        return MeasurementValue.create(new Summary());
    }

    public static Summary empty() {
        return new Summary();
    }

    @Override
    void addTo(Summary metric) {
        doAddTo(metric);
    }

    @Override
    void addAll(Measurement<?, ?> measurement) throws IncompatibleMeasurementsDetected {
        measurement.addTo(this);
    }

    @Override
    Collector collect(String name, String help, Labels globalLabels) {
        var summary = io.prometheus.client.Summary.build()
                .name(name)
                .help(help)
                .labelNames(labelNames(globalLabels))
                .create();
        collect(measurementValue -> summary.labels(measurementValue.labels(globalLabels).getValues())
                .observe(measurementValue.value())
        );
        return summary;
    }
}
