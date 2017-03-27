package org.biins.json4j.reflect;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Martin Janys
 */
public class ReflectionUtils {

    public static <T> T get(Object o, String key, Class<T> cls) {
        return cls.cast(get(o, key));
    }


    public static <T> T get(Object o, String key) {
        T value = invokeGetter(o, key);
        if (value == null) {
            value = getField(o, key);
        }
        return value;
    }

    @SuppressWarnings("unchecked")
    private static <T> T invokeGetter(Object o, String key) {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(key, o.getClass());
            Method getter = pd.getReadMethod();
            return (T) getter.invoke(o);
        } catch (IntrospectionException e) {
            return null;
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> T getField(Object o, String key) {
        try {
            Field field = o.getClass().getDeclaredField(key);
            field.setAccessible(true);
            return (T) field.get(o);
        } catch (NoSuchFieldException e) {
            return null;
        } catch (IllegalAccessException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
