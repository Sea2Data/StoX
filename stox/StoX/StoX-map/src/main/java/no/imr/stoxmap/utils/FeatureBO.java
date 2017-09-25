/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stoxmap.utils;

import com.vividsolutions.jts.geom.Geometry;
import org.geotoolkit.feature.FeatureUtilities;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.feature.type.FeatureType;

/**
 * Feature wrapper object with name, geometry and selection for geometry objects
 * (polygons, lines and points)
 *
 * @author aasmunds
 */
public class FeatureBO {

    private static final FeatureType FeatureBO_Type;

    static {
        FeatureBO_Type = FeatureUtil.createFeatureType(FeatureBO.class);
    }
    Feature feature; // Feature for map
    Property name;
    Property geometry;
    Property selection;
    Property presencelevel;
    Object userData;

    public FeatureBO(String name, Geometry geometry) {
        this(name, geometry, 0);
    }

    public FeatureBO(Feature feature, String name, Geometry geometry, Integer selection, Integer presencelevel) {
        setFeature(feature);
        setName(name);
        setGeometry(geometry);
        setSelection(selection);
        setPresencelevel(presencelevel);
    }

    public FeatureBO(String name, Geometry geometry, Integer selection) {
        this(FeatureUtilities.defaultFeature(FeatureBO_Type, name), name, geometry, selection, 1);
    }

    public void setFeature(Feature f) {
        feature = f;
        this.name = feature.getProperty("name");
        this.geometry = feature.getProperty("geometry");
        this.selection = feature.getProperty("selection");
        this.presencelevel = feature.getProperty("presencelevel");
    }

    public String getName() {
        return (String) name.getValue();
    }

    public void setName(String name) {
        this.name.setValue(name);
    }

    public Geometry getGeometry() {
        return (Geometry) geometry.getValue();
    }

    public void setGeometry(Geometry geometry) {
        this.geometry.setValue(geometry);
    }

    public Integer getSelection() {
        return (Integer) selection.getValue();
    }

    public void setSelection(Integer selection) {
        this.selection.setValue(selection);
    }

    public Integer getPresencelevel() {
        return (Integer) presencelevel.getValue();
    }

    public void setPresencelevel(Integer presencelevel) {
        this.presencelevel.setValue(presencelevel);
    }

    public Feature getFeature() {
        return feature;
    }

    @Override
    public String toString() {
        return getName() + " geometry: " + getGeometry().toText(); //To change body of generated methods, choose Tools | Templates.
    }

    public Object getUserData() {
        return userData;
    }

    public void setUserData(Object userData) {
        this.userData = userData;
    }

}
