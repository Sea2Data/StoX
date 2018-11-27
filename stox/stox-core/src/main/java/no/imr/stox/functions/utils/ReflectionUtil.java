package no.imr.stox.functions.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang.StringUtils;

/**
 * Contains simple utility methods for reflection.
 *
 * @author kjetilf
 */
public final class ReflectionUtil {

    /**
     * Constructor should not be callable for utility classes.
     */
    private ReflectionUtil() {
        // Not needed
    }

    /**
     * Creates instance of a class. Full namespace is required.
     *
     * @param <T> Class type to return
     * @param className Full class namespace
     * @return Class instance of T
     * @throws RuntimeException this class
     */
    public static <T> T getClassInstance(final String className) {
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            Class<?> clazz = classLoader.loadClass(className);
            return (T) clazz.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Reflection to extract member info.
     *
     * @param c
     * @return
     */
    public static List<Method> getMembers(Class c) {
        return getFields(c).stream()
                .map(m -> getGetter(m))
                .collect(Collectors.toList());
    }

    public static Method getGetter(Class clz, String fName) {
        try {
            return clz.getMethod("get" + StringUtils.capitalize(fName));
        } catch (NoSuchMethodException | SecurityException ex) {
            return null;
        }
    }

    public static Method getGetter(Field f) {
        return getGetter(f.getDeclaringClass(), f.getName());
    }

    public static Object invoke(Field f, Object o) {
        return invoke(getGetter(f), o);
    }

    public static Object invoke(Method getter, Object o) {
        try {
            return getter.invoke(o);
        } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException ex) {
            return null;
        }
    }

    public static List<Field> getFields(Class c) {
        return Stream.of(c.getDeclaredFields())
                .filter(f -> Modifier.isProtected(f.getModifiers()) && !f.getType().isAssignableFrom(List.class))
                //                .filter(m -> m.getName().startsWith("set") && m.getParameters().length == 1)
                //               .map(m -> m.getName().substring(3).toLowerCase())
                .collect(Collectors.toList());
    }
    /* List<String> call(Object o, String field, Clas) {
        o.getClass().getm
        return Stream.of(c.getMethods())
                .filter(m -> m.getName().startsWith("set") && m.getParameters().length == 1)
                .map(m -> m.getName().substring(3))
                .collect(Collectors.toList());
    }*/
}
