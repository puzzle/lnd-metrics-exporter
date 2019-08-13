package ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement;

import ch.puzzle.lnd.metricsexporter.common.scrape.labels.Labels;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement.exception.IncompatibleMeasurementsDetected;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class CounterTest {

    private static final String METRIC_NAME = "the_name";

    private static final String METRIC_HELP = "the-help";

    private static final String MEASUREMENT_LABEL_NAME = "label";

    private static final String MEASUREMENT_LABEL_VALUE = "label-value-01";

    private static final int MEASUREMENT_VALUE = 1;

    private Counter counter;

    @Before
    public void setup() {
        counter = Counter.create()
                .label(MEASUREMENT_LABEL_NAME, MEASUREMENT_LABEL_VALUE)
                .value(MEASUREMENT_VALUE);
    }

    @Test
    public void testAddAllFromCounter() throws IncompatibleMeasurementsDetected {
        final var additionalCounter = Counter.create()
                .label("label", "value-02")
                .value(2);

        counter.addAll(additionalCounter);
        assertEquals(2, counter.values.size());
    }

    @Test(expected = IncompatibleMeasurementsDetected.class)
    public void testAddAllFromGauge() throws IncompatibleMeasurementsDetected {
        final var gauge = Gauge.create()
                .label("label", "value-02")
                .value(1d);

        this.counter.addAll(gauge);
    }

    @Test
    public void testCollect() {
        final var samples = counter.collect(METRIC_NAME, METRIC_HELP, Labels.create()).collect();
        assertEquals(1, samples.size());

        final var metricFamilySample = samples.get(0);
        assertEquals(METRIC_NAME, metricFamilySample.name);
        assertEquals(METRIC_HELP, metricFamilySample.help);
        assertEquals(1, metricFamilySample.samples.size());

        var sample = metricFamilySample.samples.get(0);
        assertEquals(1d, sample.value, 0);
        assertArrayEquals(new String[]{MEASUREMENT_LABEL_NAME}, sample.labelNames.toArray());
        assertArrayEquals(new String[]{MEASUREMENT_LABEL_VALUE}, sample.labelValues.toArray());
    }

    @Test
    public void testCollectDoesNotOverrideLabels() {
        Labels globalLabels = Labels.create()
                .with(MEASUREMENT_LABEL_NAME, "value-overridden")
                .with("global_label", "global-value-01");
        final var collector = counter.collect(METRIC_NAME, METRIC_HELP, globalLabels);

        final var samples = collector.collect();
        assertEquals(1, samples.size());

        final var metricFamilySample = samples.get(0);
        assertEquals(1, metricFamilySample.samples.size());

        var sample = metricFamilySample.samples.get(0);
        assertArrayEquals(new String[]{"global_label", MEASUREMENT_LABEL_NAME}, sample.labelNames.toArray());
        assertArrayEquals(new String[]{"global-value-01", MEASUREMENT_LABEL_VALUE}, sample.labelValues.toArray());
    }
}
