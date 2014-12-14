package springboot.sqltemplate.core.util;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * The fields cache of the value object classes.
 */
public class BeanFields {
    /**
     * The cache of the fields of classes.
     */
    protected static final ConcurrentMap<Class<?>, Field[]> cachedFields = new ConcurrentHashMap<>();

    /**
     * Get fields of the given class.
     * @param clazz the class
     * @return the fields of the given class
     */
    public static Field[] get(Class<?> clazz) {
        Field[] fields = cachedFields.get(clazz);
        if (fields != null) {
            return fields;
        }

        cachedFields.putIfAbsent(clazz, clazz.getFields());
        return cachedFields.get(clazz);
    }
}
