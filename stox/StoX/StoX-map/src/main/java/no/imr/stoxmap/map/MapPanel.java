/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stoxmap.map;

import no.imr.stoxmap.bar.JCoordinateBar;
import no.imr.stoxmap.bar.ToolbarFactory;
import java.awt.BorderLayout;
import java.awt.RenderingHints;
import java.awt.geom.NoninvertibleTransformException;
import java.io.IOException;
import java.util.List;
import javax.swing.JPanel;
import no.imr.stoxmap.utils.ICoordinateViewer;
import org.geotoolkit.display2d.GO2Hints;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.gui.swing.render2d.control.AbstractMapControlBar;
import org.geotoolkit.map.MapBuilder;
import org.geotoolkit.map.MapContext;
import org.geotoolkit.map.MapLayer;
import org.opengis.referencing.operation.TransformException;
import org.openide.util.Exceptions;
import no.imr.stoxmap.utils.ProjectionUtils;

/**
 *
 * @author aasmunds
 */
public class MapPanel extends JPanel {

    final MapContext context = MapBuilder.createContext();
    JPanel bottomToolbar;

    final JMap2D map = new JMap2D();

    public MapPanel() {
        super(new BorderLayout());
        try {
            //final JNavigationBar navBar = new JNavigationBar(map);
            map.getContainer().setContext(context);
            // map.addDecoration(new JClassicNavigationDecoration(JClassicNavigationDecoration.THEME.CLASSIC));
            map.getCanvas().setVisibleArea(context.getBounds());
            map.getCanvas().setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            map.getCanvas().setRenderingHint(GO2Hints.KEY_BEHAVIOR_MODE, GO2Hints.BEHAVIOR_KEEP_TILE);

            ProjectionUtils.setLambertProjection(map, 10, 60);
            //panel.add(BorderLayout.NORTH,navBar);
            // Set default handler
            //jmap.getCanvas().setObjectiveCRS((GeographicCRS) DefaultGeographicCRS.WGS84);
///            map.setHandler(new PanHandler(map, false, coordBar));

        } catch (IOException | NoninvertibleTransformException | TransformException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void init(List<AbstractMapControlBar> customBars) {
        bottomToolbar = ToolbarFactory.createBottomToolbar(map);
        add(BorderLayout.SOUTH, bottomToolbar);
        add(BorderLayout.CENTER, map);
        final JPanel topToolbar = ToolbarFactory.createTopToolbar(map, customBars);
        if (topToolbar != null) {
            add(BorderLayout.NORTH, topToolbar);
        }
    }
    
    public ICoordinateViewer getCoordinateViewer() {
        return (JCoordinateBar) bottomToolbar.getComponent(0);
    }

    private static final double CENTRAL_MERIDAN = 10.0;
    private static final double LATITUDE_OF_ORIGIN = -20.0;

    public void setVisibleAreaByLayer(MapLayer layer) {
        try {
            if (map.getContainer().getContext().layers() != null && !map.getContainer().getContext().layers().isEmpty()) {
                map.getCanvas().setVisibleArea(layer.getBounds());
            }
        } catch (NoninvertibleTransformException | TransformException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public MapContext getContext() {
        return context;
    }

    public JMap2D getMap() {
        return map;
    }

}
