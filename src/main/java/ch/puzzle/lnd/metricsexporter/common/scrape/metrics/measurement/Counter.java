package ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement;

import ch.puzzle.lnd.metricsexporter.common.scrape.labels.Labels;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement.exception.IncompatibleMeasurementsDetected;
import io.prometheus.client.Collector;

public class Counter extends Measurement<Integer, Counter> {

    public static MeasurementValue<Integer, Counter> create() {
        return MeasurementValue.create(new Counter());
    }

    @Override
    void addAll(Measurement<?, ?> measurement) throws IncompatibleMeasurementsDetected {
        measurement.addTo(this);
    }

    @Override
    void addTo(Counter counter) {
        doAddTo(counter);
    }

    @Override
    Collector collect(String name, String help, Labels globalLabels) {
        var counter = io.prometheus.client.Counter.build()
                .name(name)
                .help(help)
                .labelNames(labelNames(globalLabels))
                .create();
        collect(value -> counter.labels(labelNames(globalLabels)).inc(value.value()));
        return counter;
    }
}
