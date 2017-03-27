package org.biins.json4j;

import java.util.Iterator;
import java.util.stream.Stream;

/**
 * @author Martin Janys
 */
public abstract class BaseJsonArray<JSON extends BaseJsonArray<JSON>> {

    private static final String DOT = "\\.";

    @SuppressWarnings("unchecked")
    public JSON getObject(String key) {
        return (JSON) get(key);
    }

    @SuppressWarnings("unchecked")
    public  <T> T getByPath(String path) {
        Iterator<String> iterator = Stream.of(path.split(DOT)).iterator();

        JSON json = get(iterator.next());
        while (iterator.hasNext()) {
            String key = iterator.next();
            boolean last = !iterator.hasNext();

            if (last) {
                return json.get(key);
            }
            else {
                json = json.get(key);
            }
        }

        return (T) json;
    }

    public Integer getInteger(String key) {
        return get(key, Integer.class);
    }

    public Double getDouble(String key) {
        return get(key, Double.class);
    }

    public String getString(String key) {
        return get(key, String.class);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) get(key, Object.class);
    }

    public <T> T get(String key, Class<T> cls) {
//        return JacksonReflectionUtils.get(this, key, cls);
        return null;
    }

    public abstract String asString();

    @Override
    public String toString() {
        return asString();
    }
}
