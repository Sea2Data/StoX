package no.imr.sea2data.jts;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation that is to be used on any class that has should be available in
 * the map. The idField should be set to a unique field of the class while the
 * geometryField should be set to a field that is of type Geometry
 *
 * @author sjurl
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FeaturePojo {

    /**
     * Name of the id field for the geographic feature
     *
     * @return String
     */
    String idField();

    /**
     * Name of the geometry field of this geopgraphic feature
     *
     * @return String
     */
    String geometryField();

    /**
     * Parameters that should not be part of the FeatureType object
     *
     * @return String
     */
    String[] ignoreMethods();
}
