package ch.puzzle.lnd.metricsexporter.common.scrape.labels;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.SortedMap;
import java.util.TreeMap;

@EqualsAndHashCode
public final class Labels {

    private final SortedMap<String, String> labels;

    private Labels(SortedMap<String, String> labels) {
        this.labels = labels;
    }

    public static Labels create() {
        return new Labels(new TreeMap<>());
    }

    public Labels with(@NonNull String name, @NonNull String value) {
        labels.put(name, value);
        return this;
    }

    public String[] getNames() {
        return labels.keySet().toArray(new String[0]);
    }

    public String[] getValues() {
        return labels.values().toArray(new String[0]);
    }

    public Labels merge(Labels otherLabels) {
        var mergedLabels = new TreeMap<>(labels);
        for (var labelEntry : otherLabels.labels.entrySet()) {
            if (mergedLabels.containsKey(labelEntry.getKey())) {
                continue;
            }
            mergedLabels.put(labelEntry.getKey(), labelEntry.getValue());
        }
        return new Labels(mergedLabels);
    }
}
