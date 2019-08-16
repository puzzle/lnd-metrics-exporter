package ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement;

import ch.puzzle.lnd.metricsexporter.common.scrape.labels.Labels;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement.exception.IncompatibleMeasurementsDetected;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class SummaryTest {

    private static final String METRIC_NAME = "the_name";

    private static final String METRIC_HELP = "the-help";

    private static final String MEASUREMENT_LABEL_NAME = "label";

    private static final String MEASUREMENT_LABEL_VALUE = "label-value-01";

    private static final double MEASUREMENT_VALUE = 1d;

    private Summary summary;

    @Before
    public void setup() {
        summary = Summary.create()
                .label(MEASUREMENT_LABEL_NAME, MEASUREMENT_LABEL_VALUE)
                .value(MEASUREMENT_VALUE);
    }

    @Test
    public void testAddAllFromSummary() throws IncompatibleMeasurementsDetected {
        final var additionalSummary = Summary.create()
                .label("label", "value-02")
                .value(2d);

        summary.addAll(additionalSummary);
        assertEquals(2, summary.values.size());
    }

    @Test(expected = IncompatibleMeasurementsDetected.class)
    public void testAddAllFromGauge() throws IncompatibleMeasurementsDetected {
        final var gauge = Gauge.create()
                .label("label", "value-02")
                .value(2.0);

        summary.addAll(gauge);
    }

    @Test(expected = IncompatibleMeasurementsDetected.class)
    public void testAddAllFromCounter() throws IncompatibleMeasurementsDetected {
        final var counter = Counter.create()
                .label("label", "value-02")
                .value(2);

        summary.addAll(counter);
    }

    @Test
    public void testCollect() {
        final var samples = summary.collect(METRIC_NAME, METRIC_HELP, Labels.create()).collect();
        assertEquals(1, samples.size());

        final var metricFamilySample = samples.get(0);
        assertEquals(METRIC_NAME, metricFamilySample.name);
        assertEquals(METRIC_HELP, metricFamilySample.help);
        assertEquals(2, metricFamilySample.samples.size());

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
        final var collector = summary.collect(METRIC_NAME, METRIC_HELP, globalLabels);

        final var samples = collector.collect();
        assertEquals(1, samples.size());

        final var metricFamilySample = samples.get(0);
        assertEquals(2, metricFamilySample.samples.size());

        var sample = metricFamilySample.samples.get(0);
        assertArrayEquals(new String[]{"global_label", MEASUREMENT_LABEL_NAME}, sample.labelNames.toArray());
        assertArrayEquals(new String[]{"global-value-01", MEASUREMENT_LABEL_VALUE}, sample.labelValues.toArray());
    }
}
