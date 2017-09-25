/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stoxmap.strataedit;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.Point;
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
 *
 * @author aasmunds
 */
public class StrataGeometryNodeTool extends AbstractEditionTool {

    public StrataGeometryNodeTool() {
        super(300, "geometryNodes", MessageBundle.getI18NString("editNode"),
                new SimpleInternationalString("Tool for editing geometry nodes."),
                IconBundle.getIcon("16_multi_point"), FeatureMapLayer.class);
    }

    @Override
    public boolean canHandle(final Object candidate) {
        if (!super.canHandle(candidate)) {
            return false;
        }

        //check the geometry type is type Point
        final FeatureMapLayer layer = (FeatureMapLayer) candidate;
        final FeatureType ft = layer.getCollection().getFeatureType();

        final GeometryDescriptor desc = ft.getGeometryDescriptor();

        if (desc == null) {
            return false;
        }

        if (Point.class.isAssignableFrom(desc.getType().getBinding())) {
            //moving node on a Point type is the same as moving full geometry.
            //avoid duplicating the same purpose tool.
            return false;
        }

        return Geometry.class.isAssignableFrom(desc.getType().getBinding());
    }

    @Override
    public EditionDelegate createDelegate(final JMap2D map, final Object candidate) {
        return new StrataGeometryNodeDelegate(map, (FeatureMapLayer) candidate);
    }

}
