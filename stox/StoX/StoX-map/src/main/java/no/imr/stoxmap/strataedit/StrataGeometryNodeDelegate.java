/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stoxmap.strataedit;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.logging.Level;
import no.imr.stoxmap.utils.MapUtils;
import static no.imr.stoxmap.handler.BioStationAssignmentHandler.FF;
import no.imr.sea2data.stox.providers.ProcessDataProvider;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import static org.apache.sis.util.ArgumentChecks.ensureNonNull;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.gui.swing.render2d.control.edition.AbstractFeatureEditionDelegate;
import org.geotoolkit.gui.swing.render2d.control.edition.EditionHelper;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.referencing.CRS;
import org.opengis.feature.Feature;
import org.opengis.feature.type.AttributeDescriptor;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.openide.util.Lookup;

/**
 *
 * @author aasmunds
 */
public class StrataGeometryNodeDelegate extends AbstractFeatureEditionDelegate {

    private Feature feature = null;
    private final EditionHelper.EditionGeometry selection = new EditionHelper.EditionGeometry();
    private boolean modified = false;
    private final ProcessDataBO pd;
    private final FeatureMapLayer editedLayer;
    private final JMap2D map;

    private int pressed = -1;

    public StrataGeometryNodeDelegate(final JMap2D map, final FeatureMapLayer candidate) {
        super(map, candidate);
        this.pd = Lookup.getDefault().lookup(ProcessDataProvider.class).getPd();
        this.editedLayer = candidate;
        this.map = map;
    }

    private void reset() {
        feature = null;
        selection.reset();
        decoration.setGeometries(null);
        decoration.setNodeSelection(null);
    }

    private void refreshDecoration() {
        decoration.setGeometries(Collections.singleton(this.selection.geometry));
        decoration.setNodeSelection(this.selection);
    }

    public void setCurrentFeature(final Feature feature) {
        this.feature = feature;
        if (feature != null) {
            this.selection.geometry = helper.toObjectiveCRS(feature);
        } else {
            this.selection.geometry = null;
        }
        refreshDecoration();
    }

    @Override
    public void mouseClicked(final MouseEvent e) {

        final int button = e.getButton();

        if (button == MouseEvent.BUTTON1) {
            if (selection.geometry == null) {
                setCurrentFeature(helper.grabFeature(e.getX(), e.getY(), false));
            } else if (e.getClickCount() >= 2) {
                //double click = add a node
                final Geometry result;
                if (selection.geometry instanceof LineString) {
                    result = helper.insertNode((LineString) selection.geometry, e.getX(), e.getY());
                } else if (selection.geometry instanceof Polygon) {
                    result = helper.insertNode((Polygon) selection.geometry, e.getX(), e.getY());
                } else if (selection.geometry instanceof GeometryCollection) {
                    result = helper.insertNode((GeometryCollection) selection.geometry, e.getX(), e.getY());
                } else {
                    result = selection.geometry;
                }
                modified = modified || result != selection.geometry;
                selection.geometry = result;
                decoration.setGeometries(Collections.singleton(selection.geometry));
            } else if (e.getClickCount() == 1) {
                //single click with a geometry = select a node
                helper.grabGeometryNode(e.getX(), e.getY(), selection);
                decoration.setNodeSelection(selection);
            }
        } else if (button == MouseEvent.BUTTON3) {
            sourceModifyFeature(feature, selection.geometry, true);
            reset();
        }

    }

    @Override
    public void mousePressed(final MouseEvent e) {
        pressed = e.getButton();

        if (pressed == MouseEvent.BUTTON1) {
            if (selection.geometry == null) {
                setCurrentFeature(helper.grabFeature(e.getX(), e.getY(), false));
            } else if (e.getClickCount() == 1) {
                //single click with a geometry = select a node
                helper.grabGeometryNode(e.getX(), e.getY(), selection);
                decoration.setNodeSelection(selection);
            }
        }

        super.mousePressed(e);
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        super.mouseReleased(e);
    }

    @Override
    public void mouseDragged(final MouseEvent e) {

        if (pressed == MouseEvent.BUTTON1 && selection != null) {
            //dragging node
            selection.moveSelectedNode(helper.toCoord(e.getX(), e.getY()));
            refreshDecoration();
            modified = true;
            return;
        }

        super.mouseDragged(e);
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        super.mouseMoved(e);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (KeyEvent.VK_DELETE == e.getKeyCode() && selection != null) {
            //delete node
            selection.deleteSelectedNode();
            refreshDecoration();
            modified = true;
            return;
        }
    }

/*    private void selectionChanged() {
        if (feature != null && pd != null) {
            String name = (String) feature.getProperty("name").getValue();
            Polygon pol = (Polygon) feature.getProperty("geoshape").getValue();
            AbndEstProcessDataUtil.setStratumPolygon(pd, name, pol);
        }
    }
*/
    public void sourceModifyFeature(final Feature feature, final Geometry geo, boolean reprojectToDataCRS) {

        if (feature == null || geo == null) {
            //nothing to do
            return;
        }

        final String ID = feature.getIdentifier().getID();

        ensureNonNull("geometry", geo);
        ensureNonNull("id", ID);

        if (editedLayer != null && editedLayer.getCollection().isWritable()) {

            final Filter filter = FF.id(Collections.singleton(FF.featureId(ID)));
            final FeatureType featureType = editedLayer.getCollection().getFeatureType();
            final AttributeDescriptor geomAttribut = featureType.getGeometryDescriptor();
            final CoordinateReferenceSystem dataCrs = featureType.getCoordinateReferenceSystem();

            try {
                final Geometry geom;
                if (reprojectToDataCRS) {
                    geom = JTS.transform(geo,
                            CRS.findMathTransform(map.getCanvas().getObjectiveCRS(), dataCrs, true));
                    JTS.setCRS(geom, dataCrs);
                    AbndEstProcessDataUtil.setStratumPolygon(pd, (String)feature.getProperty("name").getValue(), (MultiPolygon)geom);
                    MapUtils.onStrataEdited();
              } else {
                    geom = geo;
                }
                editedLayer.getCollection().update(filter, geomAttribut, geom);
            } catch (final Exception ex) {
                LOGGER.log(Level.WARNING, ex.getLocalizedMessage(), ex);
            } finally {
                map.getCanvas().repaint();
            }

        }

    }

}
