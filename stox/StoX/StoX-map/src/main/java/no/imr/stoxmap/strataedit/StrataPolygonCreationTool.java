/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stoxmap.strataedit;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.MultiPolygon;
import org.apache.sis.util.iso.SimpleInternationalString;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.gui.swing.render2d.control.edition.AbstractEditionTool;
import org.geotoolkit.gui.swing.render2d.control.edition.EditionDelegate;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.map.FeatureMapLayer;
import org.opengis.feature.type.FeatureType;
import org.opengis.feature.type.GeometryDescriptor;

/**
 * Adapted from MultiPolygonCreationTool to control the delegate factory.
 * @author aasmunds
 */
public class StrataPolygonCreationTool extends AbstractEditionTool {

    public StrataPolygonCreationTool() {
        super(1500,"polygonCreation",MessageBundle.getI18NString("createPolygon"),
             new SimpleInternationalString("Tool for creating polygons."), 
             IconBundle.getIcon("16_newgeometry"), FeatureMapLayer.class);
    }

    @Override
    public boolean canHandle(final Object candidate) {
        if(!super.canHandle(candidate)){
            return false;
        }

        //check the geometry type is type Point
        final FeatureMapLayer layer = (FeatureMapLayer) candidate;
        final FeatureType ft = layer.getCollection().getFeatureType();

        final GeometryDescriptor desc = ft.getGeometryDescriptor();

        if(desc == null){
            return false;
        }

        return MultiPolygon.class.isAssignableFrom(desc.getType().getBinding())
            || Geometry.class.equals(desc.getType().getBinding());
    }

    @Override
    public EditionDelegate createDelegate(final JMap2D map, final Object candidate) {
        return new StrataPolygonCreationDelegate(map, (FeatureMapLayer) candidate);
    }

}
