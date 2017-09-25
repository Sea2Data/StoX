/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stoxmap.utils;

import java.util.List;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.style.DefaultDescription;
import org.geotoolkit.style.MutableStyle;
import org.opengis.feature.Feature;

/**
 *
 * @author aasmunds
 */
public class LayerUtil {

    /**
     * Create a layer from FeatureBO list, style and name
     *
     * @param name a layer name
     * @param fs a list of FeatureBO with name/geoshape objects
     * @param style, MutableStyle selectionStyle a mutable style for the layer
     * @param selectionStyle
     * @return
     */
    public static FeatureMapLayer createLayer(String name, List<FeatureBO> fs, MutableStyle style, MutableStyle selectionStyle, Boolean updateFeatures) {
        FeatureCollection fCol = FeatureUtil.createFeatureCollection(name, fs);

        try (FeatureIterator fi = fCol.iterator()) {
            // The features in the created feature collection must be transferred back to the FeatureBO collection:
            while (fi.hasNext()) {
                Feature f = fi.next();
                FeatureBO fb = FeatureUtil.getFeatureByName(fs, f.getIdentifier().getID());
                if (fb != null) {
                    fb.setFeature(f);
                }
            }
        }

        FeatureMapLayer mapLayer = MapBuilder.createFeatureLayer(fCol, style);
        mapLayer.setName(name);
        mapLayer.setDescription(new DefaultDescription(new SimpleInternationalString(name), new SimpleInternationalString("")));
        if (selectionStyle != null) {
            mapLayer.setSelectionStyle(selectionStyle);
        }
        return mapLayer;
    }
}
