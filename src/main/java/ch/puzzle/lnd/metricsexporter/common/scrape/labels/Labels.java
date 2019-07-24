package ch.puzzle.lnd.metricsexporter.common.scrape.labels;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Labels {

    private final SortedMap<String, String> labels;

    public Labels(SortedMap<String, String> labels) {
        this.labels = labels;
    }

    public static Labels create() {
        return new Labels(new TreeMap<>());
    }

    public Labels with(String name, String value) {
        labels.put(name, value);
        return this;
    }

    public Labels set(String name, String value) {
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
        var mergedLabels = Stream.concat(
                labels.entrySet().stream(),
                otherLabels.labels.entrySet().stream()
                        .filter(entry -> !labels.containsKey(entry.getKey()))
        )
                .collect(TreeMap<String, String>::new, (map, entry) -> map.put(entry.getKey(), entry.getValue()), TreeMap::putAll);
//        Collectors.toMap(this::extractKey, this::extractValue, (first, second) -> second));
        return new Labels((mergedLabels));
    }

    private String extractKey(Map.Entry<String, String> entry) {
        return entry.getKey();
    }


    private String extractValue(Map.Entry<String, String> entry) {
        return entry.getValue();
    }
}
