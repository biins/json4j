package org.biins.json4j.jackson;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import org.biins.json4j.reflect.ReflectionUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonReflectionUtils {

    public static Stream<Field> fieldStream(Class<?> cls) {
        return Stream.of(cls.getDeclaredFields())
                .filter(field -> !field.isSynthetic());
    }

    public static Map<String, Object> toMap(String jsonString, ObjectMapper objectMapper)
            throws IOException {
        Map<String, Object> json = objectMapper.readValue(jsonString, HashMap.class);
        return new Json(toMap(json, objectMapper));
    }

    private static Map<String, Object> toMap(Map<String, Object> json, ObjectMapper objectMapper) {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : json.entrySet()) {
            String key = entry.getKey();
            Object value;
            if (Map.class.isAssignableFrom(entry.getValue().getClass())) {
                value = toMap((Map<String, Object>) entry.getValue(), objectMapper);
            } else {
                value = entry.getValue();
            }
            result.put(key, value);
        }
        return result;
    }

    public static Map<String, Object> toLazyMap(Object o, ObjectMapper objectMapper) {
        Map<String, Object> objectMap = new HashMap<>();
        fieldStream(o.getClass()).forEach((field) -> {
            String key = field.getName();
            Supplier<?> value;
            if (Json.class.isAssignableFrom(field.getType())) {
                value = () -> {
                    Object property = ReflectionUtils.get(o, key);
                    return toLazyMap(property, objectMapper);
                };
            } else {
                value = () -> {
                    Object property = ReflectionUtils.get(o, key);
                    return objectMapper.convertValue(property, field.getType());
                };
            }
            objectMap.put(key, value);
        });
        return new Json(objectMap);
    }
}
