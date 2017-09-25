package no.imr.stox.functions.utils;

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
     * @throws RuntimeException
     * this class
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

}
