package no.imr.stoxmap.handler;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.event.EventListenerList;
import javax.swing.event.MouseInputListener;
import no.imr.sea2data.stox.mapgui.StoXMapSetup;
import no.imr.stoxmap.style.Cursors;
import no.imr.stoxmap.utils.MapUtils;
import no.imr.sea2data.stox.providers.DistancePSUHandler;
import no.imr.sea2data.stox.providers.ProcessDataProvider;
import no.imr.stox.api.IProcessDataListener;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.nodes.PSUNode;
import no.imr.stox.nodes.StrataNode;
import no.imr.stoxmap.utils.FeatureBO;
import no.imr.stoxmap.utils.FeatureUtil;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.map.FeatureMapLayer;
import org.opengis.feature.Feature;
import org.opengis.filter.FilterFactory2;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;

/**
 * Handler class for all functionality used for transect edition
 *
 * @author Johann Sorel (Geomatys)
 * @author sjurl
 */
public class TransectBuildHandler extends BaseHandler implements IProcessDataListener {

    //private static Set<Feature> selectedFeatures;
    /**
     * Mouse picking tolerance in pixels around the mouse cursor.
     */
    public static final int SEARCH_SIZE = 4;
    /**
     * Zoom in and out factor, used in mouse wheel events.
     */
    private static final double ZOOM_FACTOR = 2;
    private final GestureListener gestureListener = new GestureListener();
    private final EventListenerList listeners = new EventListenerList();
    //current selected elements
    private final Map<String, FeatureBO> distFeatures;
    public static final FilterFactory2 FF = (FilterFactory2) FactoryFinder.getFilterFactory(null);

    private Lookup.Result<PSUNode> psuResult;
    private Lookup.Result<StrataNode> strataResult;

    //private static final Map<String, Set<Feature>> FEATURE_SET_CACHE = new HashMap<String, Set<Feature>>();
    /**
     * Constructor used when click on the toolbar button is performed
     *
     * @param setup
     */
    public TransectBuildHandler(StoXMapSetup setup) {
        super(setup);
        this.distFeatures = new HashMap<>();
    }

    public ProcessDataProvider getPDP() {
        return ((ProcessDataProvider) Lookup.getDefault().lookup(ProcessDataProvider.class));
    }

    public DistancePSUHandler getDistPSUHandler() {
        return getPDP().getDistPSUHandler();
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

        map.setCursor(Cursors.getTransectStartCursor());
        psuResult = Utilities.actionsGlobalContext().lookupResult(PSUNode.class);
        psuResult.addLookupListener(listener);
        strataResult = Utilities.actionsGlobalContext().lookupResult(StrataNode.class);
        strataResult.addLookupListener(listener);
        initAcousticFeaturesByPSU();
        createDistanceFeatureMap();
        Lookup.getDefault().lookup(ProcessDataProvider.class).addProcessDataListener(this);
    }

    private void initAcousticFeaturesByPSU() {
        //MapUtils.initAcousticFeaturesByPSU(AbndEstProcessDataUtil.getEDSUPSUs(getPDP().getPd()), setup.getAcousticLayer().getCollection());
        MapUtils.initAcousticFeaturesByPSU(AbndEstProcessDataUtil.getEDSUPSUs(getPDP().getPd()), setup.getAcousticFeatures());
        map.getCanvas().repaint();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        listeners.add(PropertyChangeListener.class, listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.remove(PropertyChangeListener.class, listener);
    }

    private void createDistanceFeatureMap() {
        distFeatures.clear();
        for (FeatureBO distFeat : setup.getAcousticFeatures()) {
            distFeatures.put(distFeat.getName(), distFeat);
        }
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
        psuResult.removeLookupListener(listener);
        strataResult.removeLookupListener(listener);
        Lookup.getDefault().lookup(ProcessDataProvider.class).removeProcessDataListener(this);
    }

    /**
     * When a strata node is clicked in the strata window this method should
     * find the strata in the map and select it
     *
     * @param strataNode Node that is clicked
     */
    private void onLookupStrataNode(StrataNode strataNode) {

        MapUtils.unSelectAcousticFeatures(setup.getAcousticFeatures());
        for (FeatureBO feature : setup.getStrataFeatures()) {
            if (feature.getName().equals(strataNode.getStratum())) {
                final FeatureMapLayer layer = setup.getStrataLayer();
                layer.setSelectionFilter(FF.id(Collections.singleton(feature.getFeature().getIdentifier())));
                break;
            }
        }
        map.getCanvas().repaint();
    }

    /**
     * Method that handles actions to be taken when a PSU is selected
     *
     * @param psuNode
     */
    private void onLookupPsuNode(PSUNode psuNode) {
        selectPSU(psuNode.getPSU());
    }

    private void selectPSU(String psu) {
        getDistPSUHandler().setSelectedPSU(psu);
        Collection<String> selectedDistKeys = getDistPSUHandler().getSelectedDistances();
        MapUtils.unSelectAcousticFeatures(setup.getAcousticFeatures());
        updateSelection(selectedDistKeys, MapUtils.SELECTEDINPSU);
        // Set selection filter for layer psu. When the psu is a rectangle polygon, it is selected in the map
        MapUtils.selectRectangleLayer(setup, psu);
    }

    private void updateSelection(Collection<String> distKeys, int selValue) {
        if (distKeys == null) {
            return;
        }
        for (String distKey : distKeys) {
            FeatureBO f = getFeatureByDistKey(distKey);
            if (f == null) {
                continue;
            }
            f.setSelection(selValue);
        }
        // The layer selection must be updated based on the new selectio values for the distance keys:
        //updateSelectedDistancesInLayer();
        map.getCanvas().repaint();
    }

    /**
     * Update the map distance layer selection
     */
/*    private void updateSelectedDistancesInLayer() {
        Collection<String> selectedDistKeys = getDistPSUHandler().getSelectedDistances();
        if (selectedDistKeys == null) {
            return;
        }
        Set<FeatureId> ids = new HashSet<>(); // ids of feature.getIdentifier()
        for (String distKey : selectedDistKeys) {
            FeatureBO selFeat = getFeatureByDistKey(distKey);
            if (selFeat == null) {
                continue;
            }
            ids.add(selFeat.getFeature().getIdentifier());
        }
        FeatureMapLayer layer = setup.getAcousticPresenceLayer();
        if (layer == null) {
            return;
        }
        layer.setSelectionFilter(FF.id(ids));
    }*/

    /**
     * Search for a distance feature at given mouse position. This may be to
     * slow? test.
     *
     * @param me MouseEvent with the position of the click
     * @return Feature with the distance that was found or null if no distance
     * was found
     */
    private Feature searchDistance(MouseEvent me) {
        return FeatureUtil.searchFeature(setup.getAcousticLayer().getCollection(), me, SEARCH_SIZE, map);
    }

    private FeatureBO getFeatureByDistKey(String distKey) {
        return distFeatures.get(distKey);
    }

    @Override
    public void onDistanceTagsChanged(List<String> distKeys, Boolean on) {
        updateSelection(distKeys, on ? MapUtils.SELECTEDINPSU : MapUtils.UNSELECTED);
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
            /*Coordinate pos = getPointInvTransformed(e.getX(), e.getY());
             Coordinate posx = getPointInvTransformed(e.getX() - SEARCH_SIZE, e.getY());
             Coordinate posy = getPointInvTransformed(e.getX(), e.getY() - SEARCH_SIZE);
             System.out.println("dx = " + (pos.x - posx.x) + "\n");
             System.out.println("dy = " + (pos.y - posy.y) + "\n");*/
            decorationPane.setBuffer(map.getCanvas().getSnapShot());

            drag = true;
            if (e.isPopupTrigger()) {
                return;
            }
            FeatureBO selectedFeature = MapUtils.searchDistance(setup.getAcousticFeatures(), e.getX(), e.getY(), SEARCH_SIZE, map.getCanvas());
            if (selectedFeature == null) {
                return;
            }
            Boolean multipleSelect = e.isShiftDown();
            getDistPSUHandler().handleDistanceClick(MapUtils.getFKey(selectedFeature), multipleSelect);
            updateCursor(multipleSelect);
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

            updateCursor(e.isShiftDown());
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

        /**
         * Update the cursor depending on the mode you are in
         */
        private void updateCursor(boolean multipleSelect) {
            if (multipleSelect) {
                map.setCursor(Cursors.getMultipleSelectCursor());
            } else {
                map.setCursor(Cursors.getTransectStartCursor());
            }
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (KeyEvent.VK_SHIFT == e.getKeyCode()) {
                updateCursor(true);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            if (KeyEvent.VK_SHIFT == e.getKeyCode()) {
                updateCursor(false);
            }
        }
    }
    /**
     * Private listener that is triggered when changes happends in PSUNode or
     * StrataNode
     */
    private final LookupListener listener = new LookupListener() {

        @Override
        public void resultChanged(LookupEvent le) {
            Collection c = ((Lookup.Result) le.getSource()).allInstances();
            if (!c.isEmpty()) {
                Object node = c.iterator().next();
                if (node instanceof PSUNode) {
                    onLookupPsuNode((PSUNode) c.iterator().next());
                } else if (node instanceof StrataNode) {
                    onLookupStrataNode((StrataNode) c.iterator().next());
                }
            }
        }
    };

}
