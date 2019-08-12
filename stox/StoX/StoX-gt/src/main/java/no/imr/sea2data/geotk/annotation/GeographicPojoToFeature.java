package no.imr.sea2data.geotk.annotation;

import com.vividsolutions.jts.geom.Geometry;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.imr.stox.util.jts.FeaturePojo;
import org.apache.sis.storage.DataStoreException;
import org.apache.sis.util.NullArgumentException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.memory.MemoryFeatureStore;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.feature.FeatureUtilities;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.Feature;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.PropertyDescriptor;
import org.opengis.referencing.crs.GeographicCRS;
import org.openide.util.Exceptions;

/**
 * Creates a FeatureCollection out of any class that uses the FeaturePojo
 * annotations.
 *
 * @author sjurl
 */
public class GeographicPojoToFeature {

    private static Method ID_METHOD;
    private static final Logger LOGGER = Logger.getLogger(GeographicPojoToFeature.class.getName());
    private static final String IS_METHOD = "is";
    private static final String GET_METHOD = "get";
    private static final String ORIGINAL_GET_METHOD = "baseGetMethod";
    private static final String ORIGINAL_SET_METHOD = "baseSetMethod";
    private static final String ORIGINAL_CLASS = "baseClass";

    /**
     * Method that creates a FeatureCollection from objects that has the
     * FeaturePojo annotation set
     *
     * @param <T>
     * @param collection collection of pojo objects that should be made into
     * features
     * @param defaultGeometryAttribute if this is set the geometry attribute
     * contained in the annotations will be overridden
     * @return a feature collection made up of all the input pojos
     * @deprecated use createFeatureCollection with class parameter to allow empty feature collections.
     */
    public static <T extends Object> FeatureCollection createFeatureCollection(final Collection<T> collection, String defaultGeometryAttribute) {
        if(collection.isEmpty()) {
            return null;
        }
        Object o = collection.toArray()[0];
        return createFeatureCollection(o.getClass(), collection, defaultGeometryAttribute);
    }

    public static <T extends Object> FeatureCollection createFeatureCollection(Class clazz, final Collection<T> collection, String defaultGeometryAttribute) {
        final FeatureType type = createFeatureType(clazz, defaultGeometryAttribute);
        final MemoryFeatureStore store = new MemoryFeatureStore(type, true);
        final Session session = store.createSession(true);
        final FeatureCollection features = session.getFeatureCollection(QueryBuilder.all(type.getName()));
        try {
            for (final T vo : collection) {
                final Feature f = createFeature(vo, type);
                GeometryAttribute geomatt = f.getDefaultGeometryProperty();
                if (geomatt.getValue() != null) {
                    features.add(f);
                }
            }
            session.commit();
        } catch (IllegalArgumentException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IllegalAccessException ex) {
            Exceptions.printStackTrace(ex);
        } catch (InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
        } catch (DataStoreException ex) {
            Exceptions.printStackTrace(ex);
        }

        return features;
    }
    /**
     * Creates the FeatureType that corresponds to the input class
     *
     * @param clazz class that should be used as building stone for the feature
     * type
     * @return
     */
    private static FeatureType createFeatureType(Class clazz, String defaultGeometryAttribute) {
        Annotation[] annotations = clazz.getAnnotations();
        String idField = null;
        String geometryField = defaultGeometryAttribute;
        List<String> ignoreMethods = null;

        List<String> geoMethodNames = new ArrayList<String>();
        for (Method method : FeaturePojo.class.getDeclaredMethods()) {
            geoMethodNames.add(method.getName());
        }

        for (Annotation annotation : annotations) {
            for (Method method : annotation.getClass().getMethods()) {
                if (geoMethodNames.contains(method.getName())) {
                    try {
                        idField = (String) annotation.getClass().getMethod("idField").invoke(annotation);
                        if (geometryField == null) {
                            geometryField = (String) annotation.getClass().getMethod("geometryField").invoke(annotation);
                        }
                        ignoreMethods = Arrays.asList((String[]) annotation.getClass().getMethod("ignoreMethods").invoke(annotation));
                        break;
                    } catch (IllegalAccessException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (IllegalArgumentException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (InvocationTargetException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (NoSuchMethodException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }

        if (idField == null || geometryField == null) {
            return null;
        }

        getMethodForId(clazz, idField);
        FeatureTypeBuilder ftb = createFeatureFromPojo(clazz, ignoreMethods, geometryField);
        if (defaultGeometryAttribute != null) {
            ftb.setDefaultGeometry(defaultGeometryAttribute);
        } else {
            ftb.setDefaultGeometry(geometryField);
        }
        final FeatureType retur = ftb.buildFeatureType();
        retur.getUserData().put(ORIGINAL_CLASS, clazz);
        return retur;
    }

    /**
     * @param pojo : POJO object to transform
     * @param type : Mapping feature type for this POJO
     * @return Feature, never null
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    private static Feature createFeature(final Object pojo, final FeatureType type) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (pojo == null) {
            throw new NullArgumentException("Feature type can not be null.");
        }
        if (type == null) {
            throw new NullArgumentException("POJO object can not be null.");
        }

        final String id = (String) ID_METHOD.invoke(pojo);
        final Feature feature = FeatureUtilities.defaultFeature(type, id);

        for (final PropertyDescriptor desc : type.getDescriptors()) {
            final Method method = (Method) desc.getUserData().get(ORIGINAL_GET_METHOD);
            final Object value = method.invoke(pojo);
            feature.getProperty(desc.getName()).setValue(value);
        }

        return feature;
    }

    /**
     * Sets the ID_METHOD variable to the Method that was set as the container
     * of the Identifier for the current pojo being handled
     *
     * @param clazz
     * @param idField
     * @throws RuntimeException
     * @throws NoSuchMethodError
     */
    private static void getMethodForId(Class clazz, String idField) throws NoSuchMethodError {
        try {
            Method[] methods = clazz.getMethods();
            boolean found = false;
            for (Method method : methods) {
                if (method.getName().startsWith("get")) {
                    String baseName = method.getName().substring(3);
                    String attributeName = baseName.substring(0, 1).toLowerCase() + baseName.substring(1);
                    if (attributeName.equals(idField)) {
                        ID_METHOD = method;
                        found = true;
                        break;
                    }
                }
            }
            if (!found) {
                throw new NoSuchMethodError("Getter for " + idField + " could not be found on: " + clazz.getName());
            }
        } catch (SecurityException ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Fills a FeatureTypeBuilder with the features contained in the input Pojo
     * class
     *
     * @param clazz class that the feature should be created from
     * @param ignoreMethods methods that will be ignored when creating the
     * feature
     * @param geometryField field that contains the geometry
     * @return
     * @throws SecurityException
     */
    private static FeatureTypeBuilder createFeatureFromPojo(Class clazz, List<String> ignoreMethods, String geometryField) throws SecurityException {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
        final AttributeTypeBuilder atb = new AttributeTypeBuilder();
        for (final Method method : clazz.getMethods()) {
            //we check method and not field to ensure we also take field which may be private            
            final String methodname = method.getName();
            if (methodname.equals("getClass") || methodname.equals("getId")) {
                continue;
            }
            if (method.getParameterTypes().length != 0) {
                //getter with arguments, ignore it.
                continue;
            }

            final Class returnClass = method.getReturnType();
            if (returnClass == null) {
                //method do not return any value, ignore it.
                continue;
            }

            final String baseName;
            final String attributeName;
            if (methodname.startsWith(IS_METHOD)) {
                baseName = methodname.substring(2);
                attributeName = baseName.substring(0, 1).toLowerCase() + baseName.substring(1);
            } else if (methodname.startsWith(GET_METHOD)) {
                baseName = methodname.substring(3);
                attributeName = baseName.substring(0, 1).toLowerCase() + baseName.substring(1);
            } else {
                //not a valid bean getter method
                continue;
            }
            if (ignoreMethods.contains(attributeName)) {
                continue;
            }

            atb.reset();
            atb.setName(attributeName);
            atb.setBinding(returnClass);

            final AttributeType type;
            if (Geometry.class.isAssignableFrom(returnClass) && attributeName.equals(geometryField)) {
                //geometry type, we expect the geometry to have the correct srid set.
                atb.setCRS((GeographicCRS) DefaultGeographicCRS.WGS84);
                type = atb.buildGeometryType();
            } else {
                //basic attribute type
                type = atb.buildType();
            }

            adb.reset();
            adb.setName(attributeName);
            adb.setMinOccurs(1);
            adb.setMaxOccurs(1);
            adb.setNillable(true);
            adb.setType(type);
            final AttributeDescriptor desc = adb.buildDescriptor();
            //store the original method to acces it fsater when we will create features.

            //search for the setter method
            Method setter = null;
            try {
                setter = clazz.getMethod("set" + baseName, returnClass);
            } catch (NoSuchMethodException | SecurityException ex) {
                // NB: Objects can also have getters without setters!
                // Logging here is too timeconsuming for critical runtime processes in stox. Logging missing setters should be a job for a unit test instead.
            }

            desc.getUserData().put(ORIGINAL_GET_METHOD, method);
            if (setter != null) {
                desc.getUserData().put(ORIGINAL_SET_METHOD, setter);
            }

            ftb.add(desc);

        }
        ftb.setDefaultGeometry(geometryField);
        ftb.setName(clazz.getSimpleName());
        return ftb;
    }
}
