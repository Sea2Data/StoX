package no.imr.stox.functions.utils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.xml.bind.annotation.XmlAttribute;
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
        return invoke(getGetter(f), o, false);
    }

    public static Object invoke(Field f, Object o, Boolean includeCompoundFields) {
        return invoke(getGetter(f), o, includeCompoundFields);
    }

    public static Object invoke(Method getter, Object o) {
        return invoke(getter, o, false);
    }

    public static Object invoke(Method getter, Object o, Boolean includeCompoundFields) {
        try {
            if (o == null || getter == null) {
                return null;
            }
            if ((includeCompoundFields != null && includeCompoundFields) && !o.getClass().equals(getter.getDeclaringClass())) {
                // Transform o to a compound category if possible
                // by searching the o for a field with same type as the getter.
                // using that getter to get the o.
                Field catField = Stream.of(o.getClass().getDeclaredFields())
                        .filter(f -> f.getType().equals(getter.getDeclaringClass())).findFirst().orElse(null);
                Object catObj = invoke(catField, o, false);
                if (catObj != null) {
                    o = catObj;
                }
            }
            return getter.invoke(o);
        } catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException ex) {
            return null;
        }
    }

    public static Boolean isAssignableFromLeafFields(Class cl) {
        return isAssignableFrom(cl, Integer.class, Double.class, Boolean.class, String.class, LocalDate.class, LocalTime.class, LocalDateTime.class);
    }

    public static Boolean isAssignableFrom(Class cl, Class... cv) {
        return Stream.of(cv).filter(c -> cl.isAssignableFrom(c)).count() > 0L;
    }

    public static List<Field> getFields(Class c) {
        return getFields(c, false, null);
    }

    public static List<Field> getFields(Class c, Boolean includeCompundFields, Boolean includeAttributes) {
        return (includeCompundFields != null && includeCompundFields) ? getCompoundFields(c, includeAttributes) : getLeafOrCompoundFields(true, includeAttributes, c);
    }

    public static List<Field> getCompoundFields(Class c, Boolean includeAttributes) {
        return Stream.concat(
                // start with attributes and leaf fields
                ReflectionUtil.getLeafOrCompoundFields(true, includeAttributes, c).stream(),
                // Combine into the stream non leaf category leaf fields
                ReflectionUtil.getLeafOrCompoundFields(false, includeAttributes, c).stream()
                        .flatMap(f -> ReflectionUtil.getLeafOrCompoundFields(true, includeAttributes, f.getType()).stream()))
                .collect(Collectors.toList());
    }

    public static List<Field> getLeafOrCompoundFields(Boolean leaf, Boolean attribute, Class c) {
        return Stream.of(c.getDeclaredFields())
                // filter on leaf fields
                .filter(f
                        -> Modifier.isProtected(f.getModifiers()) // modifier protected
                && !f.getType().isAssignableFrom(java.util.List.class) // not a list
                && (leaf == null || leaf ^ !isAssignableFromLeafFields(f.getType()))  // leaf relevant ? leaf or not leaf 
                && (attribute == null || attribute ^ f.getAnnotation(XmlAttribute.class) == null)  // attribute relevant ? attribute or not attribute
                )
                //                .filter(m -> m.getName().startsWith("set") && m.getParameters().length == 1)
                //               .map(m -> m.getName().substring(3).toLowerCase())
                .sorted((f1, f2) -> {
                    Boolean isAtt1 = f1.getAnnotation(XmlAttribute.class) != null;
                    Boolean isAtt2 = f2.getAnnotation(XmlAttribute.class) != null;
                    return isAtt2.compareTo(isAtt1);
                })
                .collect(Collectors.toList());
    }

    public static List<String> getFieldNames(Class c) {
        return getFields(c).stream().map(f -> f.getName()).collect(Collectors.toList());
    }
    /* List<String> call(Object o, String field, Clas) {
        o.getClass().getm
        return Stream.of(c.getMethods())
                .filter(m -> m.getName().startsWith("set") && m.getParameters().length == 1)
                .map(m -> m.getName().substring(3))
                .collect(Collectors.toList());
    }*/
}
