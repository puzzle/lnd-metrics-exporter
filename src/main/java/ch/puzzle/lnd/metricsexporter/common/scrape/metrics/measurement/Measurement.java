package ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement;

import ch.puzzle.lnd.metricsexporter.common.scrape.labels.Labels;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement.exception.IncompatibleMeasurementsDetected;
import io.prometheus.client.Collector;

import java.util.*;
import java.util.function.Consumer;

public abstract class Measurement<TValue, TMeasurement extends Measurement<TValue, TMeasurement>> {

    final List<MeasurementValue<TValue, TMeasurement>> values;

    Labels allLabels;

    Measurement() {
        values = Collections.synchronizedList(new LinkedList<>());
        allLabels = Labels.create();
    }

    abstract void addAll(Measurement<?, ?> measurement) throws IncompatibleMeasurementsDetected;

    void addTo(Gauge gauge) throws IncompatibleMeasurementsDetected {
        throw new IncompatibleMeasurementsDetected(getClass(), gauge.getClass());
    }

    void addTo(Counter counter) throws IncompatibleMeasurementsDetected {
        throw new IncompatibleMeasurementsDetected(getClass(), counter.getClass());
    }

    public MeasurementValue<TValue, TMeasurement> and() {
        return MeasurementValue.create((TMeasurement) this);
    }

    void doAddTo(TMeasurement measurement) {
        allLabels = allLabels.merge(measurement.allLabels);
        values.addAll(measurement.values);
    }

    String[] labelNames(Labels labels) {
        return labels.merge(allLabels).getNames();
    }

    void collect(Consumer<MeasurementValue<TValue, TMeasurement>> consumer) {
        values.forEach(consumer);
    }

    abstract Collector collect(String name, String help, Labels globalLabels);

}
