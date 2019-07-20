package ch.puzzle.lnd.metricsexporter.common.scrape.newmetrics;

import ch.puzzle.lnd.metricsexporter.common.scrape.labels.Labels;
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

    public abstract void addAll(Measurement<?, ?> measurement);

    void addTo(Gauge gauge) {
        throw new IllegalStateException("Incompatible measurement / multiple measurement types per name detected.");
    }

    void addTo(Counter counter) {
        throw new IllegalStateException("Incompatible measurement / multiple measurement types per name detected.");
    }


    abstract Collector collect(String name, String help, Labels globalLabels);


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

}
