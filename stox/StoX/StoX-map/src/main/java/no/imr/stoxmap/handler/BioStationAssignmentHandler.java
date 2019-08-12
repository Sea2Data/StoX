package no.imr.stoxmap.handler;

import java.awt.Component;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import javax.swing.event.MouseInputListener;
import no.imr.sea2data.stox.mapgui.StoXMapSetup;
import no.imr.stoxmap.style.Cursors;
import no.imr.stoxmap.utils.MapUtils;
import no.imr.sea2data.stox.providers.LFQProvider;
import no.imr.sea2data.stox.providers.ProcessDataProvider;
import no.imr.stox.util.matrix.MatrixBO;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.exception.UserErrorException;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.nodes.PSUNode;
import no.imr.stoxmap.utils.FeatureBO;
import no.imr.stoxmap.utils.FeatureUtil;
import org.geotoolkit.factory.FactoryFinder;
import org.opengis.feature.Feature;
import org.opengis.filter.FilterFactory2;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;

/**
 * Class that handles assignment of stations to transects
 *
 * @author sjurl
 */
public class BioStationAssignmentHandler extends BaseHandler {

    /**
     * Zoom in and out factor, used in mouse wheel events.
     */
    private static final double ZOOM_FACTOR = 2;
    private final GestureListener gestureListener = new GestureListener();
    private Lookup.Result<PSUNode> psuResult;
    private String selectedPSU;
    private Integer selectedAssignment;
    private static Set<FeatureBO> selectedFeatures;
    private final ProcessDataBO pd;
    public static final FilterFactory2 FF = (FilterFactory2) FactoryFinder.getFilterFactory(null);
    StoXMapSetup setup;
    boolean isSelectingPSUAtPD = false;
    /**
     * Mouse picking tolerance in pixels around the mouse cursor.
     */
    private static final int SEARCH_SIZE = 10;
    private static final int RECTANGLE_SEARCH_SIZE = 3;

    public BioStationAssignmentHandler(StoXMapSetup setup) {
        super(setup);
        this.setup = setup;
        this.pd = getPD().getPd();
    }

    @Override
    public void install(Component component) {
        super.install(component);
        component.addMouseListener(gestureListener);
        component.addMouseMotionListener(gestureListener);
        component.addMouseWheelListener(gestureListener);
        component.addKeyListener(gestureListener);
        component.setFocusTraversalKeysEnabled(false);
        component.requestFocus();
        psuResult = Utilities.actionsGlobalContext().lookupResult(PSUNode.class);
        psuResult.addLookupListener(listener);
        initAcousticFeaturesByPSU();
    }

    private void initAcousticFeaturesByPSU() {
        //MapUtils.initAcousticFeaturesByPSU(AbndEstProcessDataUtil.getEDSUPSUs(getPDP().getPd()), setup.getAcousticLayer().getCollection());
        MapUtils.initAcousticFeaturesByPSU(AbndEstProcessDataUtil.getEDSUPSUs(getPDP().getPd()), setup.getAcousticFeatures());
        map.getCanvas().repaint();
    }

    public ProcessDataProvider getPDP() {
        return ((ProcessDataProvider) Lookup.getDefault().lookup(ProcessDataProvider.class));
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
    }

    /**
     * When a node is selected in the PSUNode this method will be run
     *
     * @param node the node that was selected
     */
    private void onLookupPsuNode(PSUNode node) {
        setSelectedPSU(node.getPSU());
        MapUtils.selectRectangleLayer(setup, selectedPSU);
    }

    /**
     * Selects the features contained in the input set in the map
     *
     */
    private void setSelectedAcousticFeatures() {
        // unselect acoustic feature selection.
        MapUtils.unSelectAcousticFeatures(setup.getAcousticFeatures());

        MatrixBO distPSU = AbndEstProcessDataUtil.getEDSUPSUs(pd);
        selectedFeatures = MapUtils.findFeaturesForPSU(distPSU, selectedPSU, setup.getAcousticFeatures());

        if (selectedFeatures != null) {
            for (FeatureBO f : selectedFeatures) {
                f.setSelection(MapUtils.SELECTEDINPSU);
            }
            /*Set<FeatureId> ids = new HashSet<>();
             for (FeatureBO identifier : selectedFeatures) {
             ids.add(identifier.getFeature().getIdentifier());
             }
             setup.getAcousticLayer().setSelectionFilter(FF.id(ids));*/
            map.getCanvas().repaint();

        }
    }

    /**
     * Selects the fishstations that are connected to selectedPSU in pd
     */
    private void selectFishStationsForSelectedPSU() {
        List<FeatureBO> fishStationFeatures = setup.getStationFeatures();

        Set<FeatureBO> selectedFishStations = new HashSet<>();
        if (fishStationFeatures != null) {
            // Clear the selection flag of all fish stations
            for (FeatureBO f : fishStationFeatures) {
                f.setSelection(0);
            }
            // Find the features of the selcted fish stations.
            Collection<String> fishStationKeys = AbndEstProcessDataUtil.getStationsByPSUAndLayer(pd, selectedPSU, "1");
            for (String string : fishStationKeys) {
                for (FeatureBO fishStation : fishStationFeatures) {
                    String featureKey = fishStation.getName();
                    if (featureKey != null && featureKey.equals(string)) {
                        selectedFishStations.add(fishStation);
                    }
                }
            }
        }
        setSelectedFishStations(selectedFishStations);
    }

    /**
     * Shows teh selected fish stations in the map
     *
     * @param selectedFishStations set of Features to select in the map
     */
    private void setSelectedFishStations(Set<FeatureBO> selectedFishStations) {
        //Set<FeatureId> ids = new HashSet<>();
        for (FeatureBO f : selectedFishStations) {
            // ids.add(f.getFeature().getIdentifier());
            if (f.getSelection().equals(0)) {
                f.setSelection(1);
            }
        }
        //setup.getStationLayer().setSelectionFilter(FF.id(ids));
        map.getCanvas().repaint();
    }

    private void setSelectedPSU(String selectedPSU) {
        if (isSelectingPSUAtPD || Objects.equals(this.selectedPSU, selectedPSU)) {
            return;
        }
        this.selectedPSU = selectedPSU;
        selectedAssignment = AbndEstProcessDataUtil.getBioticAssignments(pd).getRowKeyGroupValues().getRowValueAsInteger("max");
        if (selectedAssignment == null) {
            selectedAssignment = 1;
        } else {
            selectedAssignment++;
        }
        setSelectedAcousticFeatures();
        selectFishStationsForSelectedPSU();
        try {
            // The Utilities.actionsGlobalContext().lookup generates checkLookup and resultChanged with the old PSU selected in the view/lookup
            // In future: remove the use of lookup and use spesific listener classes to remove the magic complexity.
            isSelectingPSUAtPD = true;
            getPD().selectPSUNodeByKey(selectedPSU);
            createLengthFrequencyGraphics();
        } finally {
            isSelectingPSUAtPD = false;
        }
    }

    /**
     * Adds or removes a station from assignment to the selected psu
     *
     * @param selectedFishStation
     */
    private void addRemoveStationAssignment(FeatureBO selectedFishStation) {
        //setFishStation to transect
        Collection<String> fishStationKeys = AbndEstProcessDataUtil.getStationsByPSUAndLayer(pd, selectedPSU, "1");
        if (fishStationKeys.contains(selectedFishStation.getName())) {
            // remove fishstation from pd
            fishStationKeys.remove(selectedFishStation.getName());
            if (selectedFishStation.getSelection() >= 1) {
                selectedFishStation.setSelection(0);
            }
        } else {
            // add fishstation to pd
            fishStationKeys.add(selectedFishStation.getName());
            if (selectedFishStation.getSelection() < 1) {
                selectedFishStation.setSelection(1);
            }
        }
        ////////////////////
        // Update process data:
        String asgKey = selectedAssignment.toString();
        //AbndEstProcessDataUtil.setSUAssignment(pd, selectedPSU, "1", asgKey);
        // Allocate to all layers in the process data estimation layer definition.
        MatrixBO estLayer = pd.getMatrices().get(Functions.TABLE_ESTLAYERDEF);
        for (String layer : estLayer.getRowKeys()) {
            AbndEstProcessDataUtil.setSUAssignment(pd, selectedPSU, layer, asgKey);
        }

        MatrixBO trawlAsg = AbndEstProcessDataUtil.getBioticAssignments(pd);
        // remove all currently assigned stations
        trawlAsg.removeRowKey(asgKey);

        // add list of stations
        for (String fishStationKey : fishStationKeys) {
            trawlAsg.setRowColValue(asgKey, fishStationKey, 1.0);
        }
        //Mark the model dirty (ready for save):
        getPD().getModel().setDirty(true);
        /////////////////////
        MapUtils.onAssignmentEdited();
        map.getCanvas().repaint();
        LFQProvider lfq = Lookup.getDefault().lookup(LFQProvider.class);
        try {
            // The Utilities.actionsGlobalContext().lookup generates checkLookup and resultChanged with the old PSU selected in the view/lookup
            // In future: remove the use of lookup and use spesific listener classes to remove the magic complexity.
            isSelectingPSUAtPD = true;
            lfq.createPSULFQ(getPD().getModel(), pd, selectedPSU);
        } catch (UserErrorException ex) {
            Exceptions.attachLocalizedMessage(ex, "Unable to show the length frequency for this psu");
        } finally {
            isSelectingPSUAtPD = false;
        }
    }

    /**
     * Select a psu in the map
     *
     * @param selectedDistance distance that is contained in the psu
     */
    private void selectPSUinMap(final FeatureBO selectedDistance) {
        if (selectedDistance != null) {
            // if feature part of transect show transect
            MatrixBO distPSU = AbndEstProcessDataUtil.getEDSUPSUs(pd);

            for (String rowKey : distPSU.getRowKeys()) {
                String featureKey = selectedDistance.getName();
                if (featureKey != null && featureKey.equals(rowKey)) {
                    setSelectedPSU((String) distPSU.getRowValue(rowKey));
                }
            }
            activatePSUNodeInTopComponentLookup();
        }
    }

    /**
     * Update the cursor depending on the mode you are in
     */
    private void updateCursor() {
        map.setCursor(Cursors.getBioStationAssignmentCursor());
    }

    private void activatePSUNodeInTopComponentLookup() {
        PSUNode psuNode = getPD().getPSUNodeByKey(selectedPSU);
        // let the lookup instance associated with TC hold the selected psu node:
        if (psuNode != null) {
            setup.getMapSelection().set(Collections.singleton(psuNode), null);
        }
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

        /**
         * Search for a distance feature at given mouse position.
         *
         * @param me MouseEvent with the position of the click
         * @return Feature with the distance that was found or null if no
         * distance was found
         */
        private FeatureBO searchDistance(MouseEvent me) {
            Feature fe = FeatureUtil.searchFeature(setup.getAcousticLayer().getCollection(), me, SEARCH_SIZE, map);
            if (fe == null) {
                return null;
            }
            for (FeatureBO f : setup.getAcousticFeatures()) {
                if (f.getFeature().getIdentifier().getID().equals(fe.getIdentifier().getID())) {
                    return f;
                }
            }
            return null;
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
            FeatureBO selectedFishStation = searchFishStation(e);
            if (selectedFishStation != null) {
                // Fishstation assignment
                if (selectedPSU != null) {
                    addRemoveStationAssignment(selectedFishStation);
                }
            } else if (setup.getRectangleLayer() != null) {
                // PSU Rectangle selection
                selectRectangleByMouseClick(e);
            } else if (setup.getAcousticFeatures() != null) {
                // PSU transect selection
                FeatureBO selectedDistance = searchDistance(e);
                if (selectedDistance != null) {
                    selectPSUinMap(selectedDistance);
                }
            }
            updateCursor();
        }

        /**
         * Select a rectangle (if the layer is available) based on the mouse
         * click of the user
         *
         * @param e the click in the map
         */
        private void selectRectangleByMouseClick(final MouseEvent e) {
            if (setup.getRectangleLayer() != null) {
                Feature selecteRectangle = FeatureUtil.searchFeature(setup.getRectangleLayer().getCollection(), e, RECTANGLE_SEARCH_SIZE, map);
                if (selecteRectangle != null) {
                    setup.getRectangleLayer().setSelectionFilter(FF.id(Collections.singleton(selecteRectangle.getIdentifier())));
                    setSelectedPSU(selecteRectangle.getIdentifier().getID());
                }
            }
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

        /**
         * Find the fishstation that is within search_distance to the mouse
         * click
         *
         * @param me the mouse click event
         * @return
         */
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

    }

    private ProcessDataProvider getPD() {
        return Lookup.getDefault().lookup(ProcessDataProvider.class);
    }

    /**
     * Updates the lengthFrequency graphics
     */
    private void createLengthFrequencyGraphics() {
        LFQProvider lfq = Lookup.getDefault().lookup(LFQProvider.class);
        try {
            lfq.createPSULFQ(getPD().getModel(), pd, selectedPSU);
        } catch (UserErrorException ex) {
            Exceptions.attachLocalizedMessage(ex, "Unable to show the length frequency for this psu");
        }
    }

    /**
     * Private listener that is used to listen on changes in the PSUNode
     */
    private final LookupListener listener = new LookupListener() {

        @Override
        public void resultChanged(LookupEvent le) {
            Collection c = ((Lookup.Result) le.getSource()).allInstances();
            if (!c.isEmpty()) {
                onLookupPsuNode((PSUNode) c.iterator().next());
            }
        }

    };
}
