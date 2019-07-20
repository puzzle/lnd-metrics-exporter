package ch.puzzle.lnd.metricsexporter.common.scrape.newmetrics;

import ch.puzzle.lnd.metricsexporter.common.scrape.labels.Labels;
import io.prometheus.client.Collector;

public class Gauge extends Measurement<Double, Gauge> {

    @Override
    void addTo(Gauge metric) {
        doAddTo(metric);
    }

    @Override
    public void addAll(Measurement<?, ?> measurement) {
        measurement.addTo(this);
    }

    public static MeasurementValue<Double, Gauge> create() {
        return MeasurementValue.create(new Gauge());
    }

    @Override
    public Collector collect(String name, String help, Labels globalLabels) {
        var gauge = io.prometheus.client.Gauge.build()
                .name(name)
                .help(help)
                .labelNames(labelNames(globalLabels))
                .create();
        collect(value -> gauge.labels(value.labels(globalLabels).getValues()).set(value.value()));
        return gauge;
    }
}
