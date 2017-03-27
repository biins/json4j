package org.biins.json4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Martin Janys
 */
public abstract class BaseJson<JSON extends BaseJson<JSON>> implements Json {

    private static final String DOT = "\\.";
    private final Map<String, Object> jsonData;

    public BaseJson(Function<Object, Map<String, Object>> converter) {
        this.jsonData = new HashMap<>(converter.apply(this));
    }

    public BaseJson(String json, BiFunction<String, Class<? extends Map>, Map<String, Object>> converter) {
        this.jsonData = new HashMap<>(converter.apply(json, getClass()));
    }

    public BaseJson(Map<String, Object> objectMap) {
        this.jsonData = objectMap;
    }

    public abstract JSON load(String jsonString);

    @Override
    public Integer getInteger(String key) {
        return get(key, Integer.class);
    }

    @Override
    public Double getDouble(String key) {
        return get(key, Double.class);
    }

    @Override
    public String getString(String key) {
        return get(key, String.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public JSON getJson(String key) {
        return (JSON) get(key);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) get(key, true);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, boolean byPath) {
        if (byPath) {
            return (T) getByPath(key);
        } else {
            return (T) get(key, Object.class);
        }
    }

    @Override
    public <T> T get(String key, Class<T> cls) {
        Object value = getJsonDataOrSupply(jsonData, key);
        return cls.cast(value);
    }

    private Object getJsonDataOrSupply(Map<String, Object> data, String key) {
        Object value = data.get(key);
        if (value instanceof Supplier) {
            return ((Supplier)value).get();
        } else {
            return value;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T getByPath(String path) {
        Iterator<String> iterator = Stream.of(path.split(DOT)).iterator();

        Object value = get(iterator.next(), Object.class);
        while (iterator.hasNext()) {
            String key = iterator.next();
            boolean last = !iterator.hasNext();

            if (last) {
                return (T) ((Map) value).get(key);
            }
            else {
                value = ((Map) value).get(key);
            }
        }

        return (T) value;
    }

    public Map<String, Object> getJsonData() {
        return jsonData;
    }

    @Override
    public int size() {
        return jsonData.size();
    }

    @Override
    public boolean isEmpty() {
        return jsonData.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return jsonData.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return jsonData.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return get((String) key);
    }

    @Override
    public Object put(String key, Object value) {
        return jsonData.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return jsonData.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        jsonData.putAll(m);
    }

    @Override
    public void clear() {
        jsonData.clear();
    }

    @Override
    public Set<String> keySet() {
        return jsonData.keySet();
    }

    @Override
    public Collection<Object> values() {
        return jsonData.values()
                .stream()
                .map((o) -> o instanceof Supplier ? ((Supplier)o).get() : o)
                .collect(Collectors.toList());
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return jsonData.entrySet();
    }

    @Override
    public String toString() {
        return asString();
    }

}
