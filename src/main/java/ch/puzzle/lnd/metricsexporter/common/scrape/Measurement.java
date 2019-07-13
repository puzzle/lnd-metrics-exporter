package ch.puzzle.lnd.metricsexporter.common.scrape;

import ch.puzzle.lnd.metricsexporter.common.scrape.labels.Labels;
import io.prometheus.client.*;

import java.util.function.Consumer;

public class Measurement<TBuilder extends SimpleCollector.Builder<TBuilder, TCollector>, TChild, TCollector extends SimpleCollector<TChild>> {

    private final Labels labels;

    private final TBuilder builder;

    private final Consumer<TChild> collectorConsumer;

    private Measurement(TBuilder builder, Consumer<TChild> collectorConsumer) {
        labels = Labels.create();
        this.builder = builder;
        this.collectorConsumer = collectorConsumer;
    }

    public static Measurement gauge(double value) {
        return new Measurement<>(
                Gauge.build(),
                gauge -> gauge.inc(value)
        );
    }

    public static Measurement counter(double value) {
        return new Measurement<>(
                Counter.build(),
                counter -> counter.inc(value)
        );
    }

    public Measurement<TBuilder, TChild, TCollector> label(String name, String value) {
        labels.set(name, value);
        return this;
    }

    void register(String name, String help, Labels globalLabels, CollectorRegistry registry) {
        var labels = this.labels.merge(globalLabels);
        var collector = builder.name(name)
                .help(help)
                .labelNames(labels.getNames())
                .create();
        collectorConsumer.accept(collector.labels(labels.getValues()));
        collector.register(registry);
    }
}
