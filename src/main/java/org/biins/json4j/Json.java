package org.biins.json4j;

import java.util.Map;

public interface Json extends Map<String, Object> {

    Integer getInteger(String key);

    Double getDouble(String key);

    String getString(String key);

    Json getJson(String key);

    <T> T get(String key);

    <T> T get(String key, boolean byPath);

    <T> T get(String key, Class<T> cls);

    String asString();
}
