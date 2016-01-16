package ninja.cero.sqltemplate.core.util;

import org.springframework.util.ClassUtils;

import java.net.URI;
import java.net.URL;
import java.time.temporal.Temporal;
import java.util.Date;
import java.util.Locale;

public class TypeUtils {
    /**
     * Check if the given type represents a "simple" value type:
     * a primitive, a String or other CharSequence, a Number, a Date,
     * a URI, a URL, a Locale or a Class.
     * @param clazz the type to check
     * @return whether the given type represents a "simple" value type
     */
    public static boolean isSimpleValueType(Class<?> clazz) {
        return ClassUtils.isPrimitiveOrWrapper(clazz) || clazz.isEnum() ||
                CharSequence.class.isAssignableFrom(clazz) ||
                Number.class.isAssignableFrom(clazz) ||
                Date.class.isAssignableFrom(clazz) ||
                Temporal.class.isAssignableFrom(clazz) ||
                clazz.equals(URI.class) || clazz.equals(URL.class) ||
                clazz.equals(Locale.class) || clazz.equals(Class.class);
    }
}
