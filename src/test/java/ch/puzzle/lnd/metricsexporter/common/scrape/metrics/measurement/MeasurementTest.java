package ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement;

import ch.puzzle.lnd.metricsexporter.common.scrape.labels.Labels;
import ch.puzzle.lnd.metricsexporter.common.scrape.metrics.measurement.exception.IncompatibleMeasurementsDetected;
import io.prometheus.client.Collector;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MeasurementTest {

    private static final String MEASUREMENT_LABEL_NAME = "label";

    private static final String MEASUREMENT_LABEL_VALUE = "label-value-01";

    private static final String MEASUREMENT_VALUE = "the-value";

    private TestMeasurement measurement;

    @Before
    public void setup() {
        measurement = TestMeasurement.create()
                .label(MEASUREMENT_LABEL_NAME, MEASUREMENT_LABEL_VALUE)
                .value(MEASUREMENT_VALUE);
    }

    @Test
    public void testLabelNamesAreTracked() {
        assertArrayEquals(new String[]{MEASUREMENT_LABEL_NAME}, measurement.defaultLabels.getNames());

        measurement.and() // No additional label added -> nothing changed?
                .label(MEASUREMENT_LABEL_NAME, MEASUREMENT_LABEL_VALUE)
                .value("another-value");
        assertArrayEquals(new String[]{MEASUREMENT_LABEL_NAME}, measurement.defaultLabels.getNames());
        assertArrayEquals(new String[]{MEASUREMENT_LABEL_NAME}, measurement.labelNames(Labels.create()));

        var additionalLabelName = "another_label";
        measurement.and()
                .label(MEASUREMENT_LABEL_NAME, MEASUREMENT_LABEL_VALUE)
                .label(additionalLabelName, MEASUREMENT_LABEL_VALUE)
                .value("yet-another-value");
        assertArrayEquals(new String[]{additionalLabelName, MEASUREMENT_LABEL_NAME}, measurement.defaultLabels.getNames());
        assertArrayEquals(new String[]{additionalLabelName, MEASUREMENT_LABEL_NAME}, measurement.labelNames(Labels.create()));
    }

    @Test
    public void testLabelNamesAreMerged() {
        var globalLabels = Labels.create()
                .with("global-label", "global-value");
        var labelNames = measurement.labelNames(globalLabels);
        assertArrayEquals(new String[]{"global-label", MEASUREMENT_LABEL_NAME}, labelNames);
    }

    @Test(expected = IncompatibleMeasurementsDetected.class)
    public void testAddGaugeValuesThrowsException() throws IncompatibleMeasurementsDetected {
        measurement.addAll(Gauge.create().value(0d));
    }

    @Test(expected = IncompatibleMeasurementsDetected.class)
    public void testAddCounterValuesThrowsException() throws IncompatibleMeasurementsDetected {
        measurement.addAll(Counter.create().value(0));
    }

    @Test
    public void testDoAddTo() {
        var anotherMeasurement = TestMeasurement.create()
                .label("another_label", "the-value")
                .value("the-value");

        measurement.doAddTo(anotherMeasurement);

        assertEquals(1, measurement.values.size());
        assertEquals(MEASUREMENT_VALUE, measurement.values.get(0).value());
        assertArrayEquals(new String[]{MEASUREMENT_LABEL_NAME}, measurement.defaultLabels.getNames());

        assertEquals(2, anotherMeasurement.values.size());
        assertTrue(anotherMeasurement.values.contains(measurement.values.get(0)));
        assertArrayEquals(new String[]{"another_label", MEASUREMENT_LABEL_NAME}, anotherMeasurement.defaultLabels.getNames());
    }

    private static class TestMeasurement extends Measurement<String, TestMeasurement> {

        private static MeasurementValue<String, TestMeasurement> create() {
            return MeasurementValue.create(new TestMeasurement());
        }

        @Override
        void addAll(Measurement<?, ?> measurement) throws IncompatibleMeasurementsDetected {
            measurement.addTo(this);
        }

        @Override
        Collector collect(String name, String help, Labels globalLabels) {
            return null;
        }
    }
}
