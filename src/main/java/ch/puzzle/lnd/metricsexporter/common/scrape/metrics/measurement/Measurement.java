package ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement;

import ch.puzzle.lnd.metricsexporter.common.scrape.labels.Labels;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement.exception.IncompatibleMeasurementsDetected;
import io.prometheus.client.Collector;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

public abstract class Measurement<TValue, TMeasurement extends Measurement<TValue, TMeasurement>> {

    final List<MeasurementValue<TValue, TMeasurement>> values;

    Labels defaultLabels;

    Measurement() {
        values = Collections.synchronizedList(new LinkedList<>());
        defaultLabels = Labels.create();
    }

    @SuppressWarnings("unchecked")
    public final MeasurementValue<TValue, TMeasurement> and() {
        return MeasurementValue.create((TMeasurement) this);
    }

    void addTo(Measurement<?, ?> gauge) throws IncompatibleMeasurementsDetected {
        throw new IncompatibleMeasurementsDetected(getClass(), gauge.getClass());
    }

    void addTo(Gauge gauge) throws IncompatibleMeasurementsDetected {
        throw new IncompatibleMeasurementsDetected(getClass(), gauge.getClass());
    }

    void addTo(Summary summary) throws IncompatibleMeasurementsDetected {
        throw new IncompatibleMeasurementsDetected(getClass(), summary.getClass());
    }

    void addTo(Counter counter) throws IncompatibleMeasurementsDetected {
        throw new IncompatibleMeasurementsDetected(getClass(), counter.getClass());
    }

    final void doAddTo(TMeasurement measurement) {
        measurement.defaultLabels = measurement.defaultLabels.merge(defaultLabels);
        measurement.values.addAll(values);
    }

    final String[] labelNames(Labels labels) {
        return labels.merge(defaultLabels).getNames();
    }

    final void collect(Consumer<MeasurementValue<TValue, TMeasurement>> consumer) {
        values.forEach(consumer);
    }

    abstract void addAll(Measurement<?, ?> measurement) throws IncompatibleMeasurementsDetected;

    abstract Collector collect(String name, String help, Labels globalLabels);

}
