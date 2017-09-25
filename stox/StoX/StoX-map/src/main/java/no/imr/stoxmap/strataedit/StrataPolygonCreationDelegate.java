/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stoxmap.strataedit;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiPolygon;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import no.imr.stoxmap.utils.MapUtils;
import no.imr.sea2data.stox.providers.ProcessDataProvider;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.gui.swing.render2d.control.edition.AbstractFeatureEditionDelegate;
import org.geotoolkit.gui.swing.render2d.control.edition.EditionHelper;
import org.geotoolkit.map.FeatureMapLayer;
import org.opengis.feature.Feature;
import org.openide.util.Lookup;

/**
 *
 * @author aasmunds
 */
public class StrataPolygonCreationDelegate extends AbstractFeatureEditionDelegate {

    private MultiPolygon geometry = null;
    private final List<Coordinate> coords = new ArrayList<>();
    private boolean justCreated = false;
    private final ProcessDataBO pd;

    public StrataPolygonCreationDelegate(final JMap2D map, final FeatureMapLayer candidate) {
        super(map, candidate);
        this.pd = Lookup.getDefault().lookup(ProcessDataProvider.class).getPd();
    }

    private void reset() {
        geometry = null;
        coords.clear();
        justCreated = false;
        decoration.setGeometries(null);
    }

    @Override
    public void mouseClicked(final MouseEvent e) {

        final int button = e.getButton();

        if (button == MouseEvent.BUTTON1) {

            if (justCreated) {
                justCreated = false;
                //we must modify the second point since two point where added at the start
                coords.remove(2);
                coords.remove(1);
                coords.add(helper.toCoord(e.getX(), e.getY()));
                coords.add(helper.toCoord(e.getX(), e.getY()));

            } else if (coords.isEmpty()) {
                justCreated = true;
                //this is the first point of the geometry we create
                //add 3 points that will be used when moving the mouse around
                coords.add(helper.toCoord(e.getX(), e.getY()));
                coords.add(helper.toCoord(e.getX(), e.getY()));
                coords.add(helper.toCoord(e.getX(), e.getY()));
            } else {
                justCreated = false;
                coords.add(helper.toCoord(e.getX(), e.getY()));
            }

            geometry = EditionHelper.createMultiPolygon(Arrays.asList(EditionHelper.createPolygon(coords)));
            decoration.setGeometries(Collections.singleton(geometry));

        } else if (button == MouseEvent.BUTTON3) {

            justCreated = false;
            Feature f = helper.sourceAddGeometry(geometry);
            if (f != null) {
                String name = (String) f.getProperty("name").getValue();
                MultiPolygon pol = (MultiPolygon) f.getProperty("geometry").getValue();
                //MultiPolygon mPol = EditionHelper.createMultiPolygon(Arrays.asList(pol));
                AbndEstProcessDataUtil.setStratumPolygon(pd, name, true, pol);
                MapUtils.onStrataEdited();
                // Refresh the strata psu window

                ProcessDataProvider pdp = Lookup.getDefault().lookup(ProcessDataProvider.class);
                if (pdp == null) {
                    return;
                }
                pdp.refreshStrataNodes();

            }
            reset();
            decoration.setGeometries(null);
            coords.clear();
        }
    }

    int pressed = -1;
    int lastX = 0;
    int lastY = 0;

    @Override
    public void mousePressed(final MouseEvent e) {
        pressed = e.getButton();
        lastX = e.getX();
        lastY = e.getY();
        super.mousePressed(e);
    }

    @Override
    public void mouseReleased(final MouseEvent e) {
        super.mouseReleased(e);
    }

    @Override
    public void mouseDragged(final MouseEvent e) {
        super.mouseDragged(e);
    }

    @Override
    public void mouseMoved(final MouseEvent e) {
        if (coords.size() > 2) {
            if (justCreated) {
                coords.remove(coords.size() - 1);
                coords.remove(coords.size() - 1);
                coords.add(helper.toCoord(e.getX(), e.getY()));
                coords.add(helper.toCoord(e.getX(), e.getY()));
            } else {
                coords.remove(coords.size() - 1);
                coords.add(helper.toCoord(e.getX(), e.getY()));
            }
            geometry = EditionHelper.createMultiPolygon(Arrays.asList(EditionHelper.createPolygon(coords)));
            decoration.setGeometries(Collections.singleton(geometry));
            return;
        }
        super.mouseMoved(e);
    }

}
