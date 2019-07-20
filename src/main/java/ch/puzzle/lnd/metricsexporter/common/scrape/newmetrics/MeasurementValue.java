package ch.puzzle.lnd.metricsexporter.common.scrape.newmetrics;

import ch.puzzle.lnd.metricsexporter.common.scrape.labels.Labels;

public class MeasurementValue<T, G extends Measurement<T, G>> {

    private T value;

    private final G group;

    private final Labels labels;

    static <T, M extends Measurement<T, M>> MeasurementValue<T, M> create(M metric) {
        var boxed = new MeasurementValue<>(metric);
        metric.values.add(boxed);
        return boxed;
    }

    MeasurementValue(G group) {
        this.group = group;
        labels = Labels.create();
    }

    public MeasurementValue<T, G> label(String name, String value) {
        group.allLabels.set(name, null);
        labels.set(name, value);
        return this;
    }

    public G value(T value) {
        this.value = value;
        return group;
    }

    public T value() {
        return value;
    }

    public Labels labels(Labels defaultLabels) {
        return labels.merge(defaultLabels.merge(group.allLabels));
    }

}
