package ch.puzzle.lnd.metricsexporter.common.scrape.metrics;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class MetricScraperConfigs<T> implements Map<String, T> {

    final Map<String, T> values = new HashMap<>();

    @Override
    public int size() {
        return values.size();
    }

    @Override
    public boolean isEmpty() {
        return values.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return values.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return values.containsValue(value);
    }

    @Override
    public T get(Object key) {
        return values.get(key);
    }

    @Override
    public T put(String key, T value) {
        return values.put(key, value);
    }

    @Override
    public T remove(Object key) {
        return values.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ? extends T> m) {
        values.putAll(m);
    }

    @Override
    public void clear() {
        values.clear();
    }

    @Override
    public Set<String> keySet() {
        return values.keySet();
    }

    @Override
    public Collection<T> values() {
        return values.values();
    }

    @Override
    public Set<Entry<String, T>> entrySet() {
        return values.entrySet();
    }
}
