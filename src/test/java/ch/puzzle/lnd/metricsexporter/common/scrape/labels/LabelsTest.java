package ch.puzzle.lnd.metricsexporter.common.scrape.labels;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class LabelsTest {

    private Labels labels;

    @Before
    public void setup() {
        labels = Labels.create()
                .with("a", "value-01-a")
                .with("c", "value-02-c")
                .with("b", "value-03-b");
    }

    @Test
    public void testLabelsAreOrderedByName() {
        assertArrayEquals(new String[]{"a", "b", "c"}, labels.getNames());
        assertArrayEquals(new String[]{"value-01-a", "value-03-b", "value-02-c"}, labels.getValues());
    }

    @Test
    public void testMergeDoesNotOverride() {
        var additionalLabels = Labels.create()
                .with("a", "additional-value-01-a")
                .with("d", "value-02-d");
        var merged = labels.merge(additionalLabels);

        assertArrayEquals(new String[]{"a", "b", "c", "d"}, merged.getNames());
        assertArrayEquals(new String[]{"value-01-a", "value-03-b", "value-02-c", "value-02-d"}, merged.getValues());
    }
}
