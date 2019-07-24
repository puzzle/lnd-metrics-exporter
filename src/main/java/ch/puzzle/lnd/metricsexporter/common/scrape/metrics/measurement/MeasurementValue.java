package ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement;

import ch.puzzle.lnd.metricsexporter.common.scrape.labels.Labels;

public class MeasurementValue<TValue, TMeasurement extends Measurement<TValue, TMeasurement>> {

    private TValue value;

    private final TMeasurement measurement;

    private final Labels labels;

    private MeasurementValue(TMeasurement measurement) {
        this.measurement = measurement;
        labels = Labels.create();
    }

    static <TValue, TMeasurement extends Measurement<TValue, TMeasurement>> MeasurementValue<TValue, TMeasurement> create(
            TMeasurement measurement
    ) {
        var measurementValue = new MeasurementValue<>(measurement);
        measurement.values.add(measurementValue);
        return measurementValue;
    }

    public MeasurementValue<TValue, TMeasurement> label(String name, String value) {
        measurement.allLabels.set(name, null);
        labels.set(name, value);
        return this;
    }

    public TMeasurement value(TValue value) {
        this.value = value;
        return measurement;
    }

    TValue value() {
        return value;
    }

    Labels labels(Labels defaultLabels) {
        return labels.merge(defaultLabels.merge(measurement.allLabels));
    }
}
