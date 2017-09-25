package no.imr.stoxmap.handler;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Collections;
import javax.swing.event.MouseInputListener;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.stox.mapgui.StoXMapSetup;
import no.imr.stoxmap.style.Cursors;
import no.imr.sea2data.stox.providers.LFQProvider;
import no.imr.sea2data.stox.providers.ProcessDataProvider;
import no.imr.stox.nodes.StationNode;
import no.imr.stoxmap.utils.FeatureBO;
import no.imr.stoxmap.utils.FeatureUtil;

import org.opengis.feature.Feature;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.gui.swing.render2d.decoration.InformationDecoration;
import org.opengis.filter.FilterFactory2;
import org.openide.util.Lookup;

/**
 * Handler class for all functionality used for transect edition
 *
 * @author Johann Sorel (Geomatys)
 * @author sjurl
 */
public class StationSelectHandler extends BaseHandler {

    /**
     * Mouse picking tolerance in pixels around the mouse cursor.
     */
    private static final int SEARCH_SIZE = 10;
    /**
     * Zoom in and out factor, used in mouse wheel events.
     */
    private static final double ZOOM_FACTOR = 2;
    private final GestureListener gestureListener = new GestureListener();
    //current selected elements
    public static final FilterFactory2 FF = (FilterFactory2) FactoryFinder.getFilterFactory(null);

    /**
     * Constructor used when click on the toolbar button is performed
     *
     * @param map
     */

    public StationSelectHandler(StoXMapSetup setup) {
        super(setup);
    }

    /**
     * Adds the listeners and displays the info message
     *
     * @param component
     */
    @Override
    public void install(Component component) {
        super.install(component);
        component.addMouseListener(gestureListener);
        component.addMouseMotionListener(gestureListener);
        component.addMouseWheelListener(gestureListener);
        component.addKeyListener(gestureListener);
        component.setFocusTraversalKeysEnabled(false);
        component.requestFocus();
        map.getInformationDecoration().displayMessage("Click on a station to see information about it", 8000, InformationDecoration.LEVEL.WARNING);
        updateCursor();
        unSelectedStations();
    }

    @Override
    public void uninstall(Component component) {
        super.uninstall(component);
        component.removeMouseListener(gestureListener);
        component.removeMouseMotionListener(gestureListener);
        component.removeMouseWheelListener(gestureListener);
        component.removeKeyListener(gestureListener);
        component.setFocusTraversalKeysEnabled(true);
        map.setCursor(null);
    }

    private ProcessDataProvider getPD() {
        return Lookup.getDefault().lookup(ProcessDataProvider.class);
    }

    /**
     * Update the cursor depending on the mode you are in
     */
    private void updateCursor() {
        map.setCursor(Cursors.getStationSelectCursor());
    }

    private FeatureBO searchFishStation(MouseEvent me) {
        Feature f = FeatureUtil.searchFeature(setup.getStationLayer().getCollection(), me, SEARCH_SIZE, map);
        if (f == null) {
            return null;
        }
        for (FeatureBO fb : setup.getStationFeatures()) {
            if (fb.getFeature().getIdentifier().getID().equals(f.getIdentifier().getID())) {
                return fb;
            }
        }
        return null;
    }

    private void unSelectedStations() {
        if (setup.getStationFeatures() == null) {
            return;
        }
        // unselect stations
        for (FeatureBO f : setup.getStationFeatures()) {
            if (f.getSelection() >= 1) {
                f.setSelection(0);
            }
        }
        map.getCanvas().repaint();
    }

    //---------------------PRIVATE CLASSES--------------------------------------
    private class GestureListener implements MouseInputListener, MouseWheelListener, KeyListener {

        //mouse informations
        private int startX;
        private int startY;
        private int lastX;
        private int lastY;
        private int mousebutton = 0;
        private boolean drag = false;

        @Override
        public void mouseClicked(final MouseEvent e) {
            startX = e.getX();
            startY = e.getY();
            lastX = startX;
            lastY = startY;
        }

        @Override
        public void mousePressed(final MouseEvent e) {
            startX = e.getX();
            startY = e.getY();
            lastX = 0;
            lastY = 0;
            mousebutton = e.getButton();

            decorationPane.setBuffer(map.getCanvas().getSnapShot());

            drag = true;
            FeatureBO stationF = searchFishStation(e);
            if (stationF != null) {
                unSelectedStations();
                // set selected feature
                if (stationF.getSelection() < 1) {
                    stationF.setSelection(1);
                }
                //final FeatureMapLayer layer = setup.getStationLayer();
                //layer.setSelectionFilter(FF.id(Collections.singleton(stationF.getFeature().getIdentifier())));
                // Set the LFQ
                String stationKey = stationF.getName();
                LFQProvider lfq = Lookup.getDefault().lookup(LFQProvider.class);
                lfq.createStationLFQ(getPD().getModel(), stationKey);
                // Set station node in lookup
                setup.getMapSelection().set(Collections.singleton(new StationNode((FishstationBO) stationF.getUserData())), null);
                // repiant map.
                map.getCanvas().repaint();
            }
            updateCursor();
        }

        @Override
        public void mouseReleased(final MouseEvent e) {
            //ensure component has the focus, to catch keyboard events
            final Component comp = (Component) e.getSource();
            comp.requestFocus();

            int endX = e.getX();
            int endY = e.getY();

            //normal drag event
            if (!isStateFull()) {
                decorationPane.setBuffer(null);
                decorationPane.setFill(false);
                decorationPane.setCoord(-10, -10, -10, -10, false);
                if (drag) {
                    processDrag(startX, startY, endX, endY);
                }
            }

            lastX = 0;
            lastY = 0;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            //ensure component has the focus, to catch keyboard events
            final Component comp = (Component) e.getSource();
            comp.requestFocus();

            updateCursor();
            map.setFocusable(true);
            map.requestFocus();
            map.getComponent().setFocusable(true);
            map.getComponent().requestFocus();
        }

        @Override
        public void mouseExited(final MouseEvent e) {
            //decorationPane.setFill(false);
            //decorationPane.setCoord(-10, -10, -10, -10, true);
        }

        @Override
        public void mouseDragged(final MouseEvent e) {
            int x = e.getX();
            int y = e.getY();

            if (drag && (lastX > 0) && (lastY > 0)) {
                int dx = lastX - startX;
                int dy = lastY - startY;

                if (isStateFull()) {
                    // pan action
                    if (mousebutton == MouseEvent.BUTTON1 || mousebutton == MouseEvent.BUTTON3) {
                        processDrag(lastX, lastY, x, y);
                    }
                } else {
                    decorationPane.setFill(true);
                    decorationPane.setCoord(dx, dy, map.getComponent().getWidth(), map.getComponent().getHeight(), true);
                }
            }

            lastX = x;
            lastY = y;
        }

        @Override
        public void mouseMoved(final MouseEvent e) {
            MoveHandler.mouseMoved(setup, e, map);
        }

        @Override
        public void mouseWheelMoved(final MouseWheelEvent e) {
            int rotate = e.getWheelRotation();

            if (rotate < 0) {
                scale(e.getPoint(), ZOOM_FACTOR);
            } else if (rotate > 0) {
                scale(e.getPoint(), 1d / ZOOM_FACTOR);
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

    }

}
