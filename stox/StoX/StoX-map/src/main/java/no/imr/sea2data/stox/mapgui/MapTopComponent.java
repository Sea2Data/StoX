/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.sea2data.stox.mapgui;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.MissionBO;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.imrbase.map.ILatLonEvent;
import no.imr.stoxmap.handler.BioStationAssignmentHandler;
import no.imr.stoxmap.handler.TransectBuildHandler;
import no.imr.sea2data.stox.providers.LFQProvider;
import no.imr.sea2data.stox.providers.ProcessDataProvider;
import no.imr.stox.api.IProjectProvider;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;
import no.imr.stoxmap.strataedit.StrataEditBar;
import no.imr.stox.model.IModel;
import no.imr.stox.model.IModelListenerService;
import no.imr.stox.model.IProcess;
import no.imr.stox.model.IProject;
import no.imr.stox.model.ModelListenerAdapter;
import no.imr.stoxmap.bar.JInformationBar;
import no.imr.stoxmap.bar.JNavigationBar;
import no.imr.stoxmap.map.MapPanel;
import no.imr.stoxmap.utils.FeatureUtil;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.ProxyLookup;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//no.imr.sea2data.stox.mapgui//Map//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "MapTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "editor", openAtStartup = true, position = 10)
@ActionID(category = "Window", id = "no.imr.sea2data.stox.mapgui.MapTopComponent")
@ActionReference(path = "Menu/Window")
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_MapAction",
        preferredID = "MapTopComponent"
)
@Messages({
    "CTL_MapAction=Map",
    "CTL_MapTopComponent=Map Window",
    "HINT_MapTopComponent=This is a Map window"
})
public final class MapTopComponent extends TopComponent implements LookupListener {

    final MapPanel mapPanel = new MapPanel();
    StoXMapSetup setup = new StoXMapSetup(mapPanel);
    StoXBar stoxBar = new StoXBar(setup);

    StrataEditBar strataEditBar = new StrataEditBar(setup.getMapPanel().getMap());
    List<ILatLonEvent> acousticData = null;
    List<ILatLonEvent> acousticAbsenceData = null;
    List<ILatLonEvent> bioticAbsenceData = null;
    List<ILatLonEvent> bioticData = null;

    public MapTopComponent() {
        initComponents();
        setName(Bundle.CTL_MapTopComponent());
        setToolTipText(Bundle.HINT_MapTopComponent());
        //create map.
        JNavigationBar navBar = new JNavigationBar(mapPanel.getMap(), setup);
        JInformationBar infoBar = new JInformationBar(mapPanel.getMap());
        mapPanel.init((List) Arrays.asList(navBar, infoBar, strataEditBar, stoxBar));
        //navBar.setCoordinateViewer(mapPanel.getCoordinateViewer());
        // Set the earth grid.
        setup.addEarthGridLayer();
        // Set coastline map
        //URL url = Thread.currentThread().getContextClassLoader().getResource("no/imr/sea2data/stox/map/earth.txt"); // multi line string
        //URL url = Thread.currentThread().getContextClassLoader().getResource("no/imr/sea2data/stox/map/europe_map.txt"); // multipolygon
        URL url = Thread.currentThread().getContextClassLoader().getResource("no/imr/sea2data/stox/map/europe_countries.txt"); // multipolygon
        try {
            //setup.addCoastLineLayer(url.openStream());
            setup.addCoastPolygonLayer(url.openStream());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        /*setup.addStrataLayer(getTestStrata());
         setup.addAcousticLayer(getTestAcoustic());
         setup.addStationLayer(getTestStations());*/
        // Set visible area to coast line layer
        mapPanel.setVisibleAreaByLayer(setup.getCoastLayer());
        //mapPanel.getMap().setHandler(new PanHandler(setup, false, ));
        add(mapPanel);

        associateLookup(getMapLookup());

    }

    private Lookup getMapLookup() {
        LFQProvider lfp = (LFQProvider) Lookup.getDefault().lookup(LFQProvider.class);
        return new ProxyLookup(new AbstractLookup(setup.getMapSelection()), new AbstractLookup(lfp.createLfqSelection()));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    private Lookup.Result<IProject> result = null;

    @Override
    public void resultChanged(LookupEvent le) {
        Collection<? extends IProject> c = result.allInstances();
        if (!c.isEmpty()) {
            resetMap();
        }
    }

    @Override
    public void componentOpened() {
        IProjectProvider provider = (IProjectProvider) Lookup.getDefault().lookup(IProjectProvider.class);
        result = provider.getProjectLookup().lookup(new Lookup.Template(IProject.class));
        result.addLookupListener(this);

        final IModelListenerService fls = (IModelListenerService) Lookup.getDefault().lookup(IModelListenerService.class);
        fls.getModelListeners().add(new ModelListenerAdapter() {
            @Override
            public void onModelStart(IModel model) {
                MapTopComponent.this.onStartModel(model); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onModelStop(IModel model) {
                MapTopComponent.this.onStopModel(model); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onProcessEnd(IProcess process) {
                MapTopComponent.this.onProcessEnd(process); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onProcessBegin(IProcess process) {
                MapTopComponent.this.onProcessBegin(process); //To change body of generated methods, choose Tools | Templates.
            }

            @Override
            public void onReset(IModel m) {
                resetMap();
            }

        });
    }

    @Override
    public void componentClosed() {
        result.removeLookupListener(this);
        // TODO add custom code on component closing
        IModelListenerService fls = (IModelListenerService) Lookup.getDefault().lookup(IModelListenerService.class);
        fls.getModelListeners().remove(this);
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    private void resetMap() {
        setup.clean();
        acousticData = null;
        bioticData = null;
        strataEditBar.setEnabled(false);

    }

    public void onProcessBegin(IProcess process) {
        if (process.getMetaFunction().getName().equals(Functions.FN_READPROCESSDATA)) {
            resetMap();
        }
    }

    private void onStopModel(IModel model) {
        IProcess process = model.getRunningProcess();
        if (process == null) {
            return;
        }
        try {
            switch (process.getMetaFunction().getName()) {
                case Functions.FN_DEFINESTRATA:
                    strataEditBar.enableControl(true);
                    break;
                case Functions.FN_FILTERBIOTIC:
                    stoxBar.getStationSelectAction().setEnabled(true);
                    break;
                case Functions.FN_FILTERACOUSTIC:
                    stoxBar.getAcousticAbsenceAction().setEnabled(true);
                    break;
/*                case Functions.FN_DEFINERECTANGLE:
                    checkRectangleLayer();*/
                // Drop to set handler
                case Functions.FN_DEFINEACOUSTICPSU:
                    checkAcousticLayer();
                    setup.getMapPanel().getMap().setHandler(new TransectBuildHandler(setup));
                    stoxBar.getStationSelectAction().setEnabled(false);
                    stoxBar.getTransectAction().setEnabled(true);
                    break;
                /*case Functions.FN_RECTANGLEASSIGNMENT:
                    checkRectangleLayer();*/
                // Drop to set handler
                case Functions.FN_BIOSTATIONASSIGNMENT:
                    checkAcousticLayer();
                    checkStationLayer(false);
                    setup.getMapPanel().getMap().setHandler(new BioStationAssignmentHandler(setup));
                    stoxBar.getStationSelectAction().setEnabled(true);
                    stoxBar.getBioAction().setEnabled(true);
                    stoxBar.getTransectAction().setEnabled(false);
                    break;
                case Functions.FN_WRITEPROCESSDATA:
                    // Finish model - final gui model response
                    strataEditBar.enableControl(false);
                    stoxBar.getAcousticAbsenceAction().setEnabled(true);
                    stoxBar.getBioAction().setEnabled(false);
                    stoxBar.getTransectAction().setEnabled(false);
                    stoxBar.getStationSelectAction().setEnabled(false);
                    break;
            }
        } catch (Exception ex) {
            // stop model because of error
//            model.setRunState(IModel.RUNSTATE_STOPPED);
            Exceptions.printStackTrace(ex);
        }
    }

    public void onProcessEnd(IProcess process) {
        Object ds = process.getOutput();
        try {
            switch (process.getMetaFunction().getName()) {
                case Functions.FN_DEFINESTRATA:
                    /*if (process.isBreakInGUI()) {
                        strataEditBar.enableControl(true);
                    }*/
                    if (process.isRespondInGUI()) {
                        MatrixBO m = AbndEstProcessDataUtil.getStratumPolygons(Lookup.getDefault().lookup(ProcessDataProvider.class).getPd());
                        setup.addStrataLayer(m);
                    }
                    break;
                case Functions.FN_STRATUMAREA:
                    strataEditBar.enableControl(false);
                    break;
                case Functions.FN_FILTERACOUSTIC:
                    acousticData = (List) ds;
                    findAcousticAbsenceData(process);
                    if (process.isRespondInGUI()) {
                        checkAcousticLayer();
                    }
                    break;
                case Functions.FN_FILTERBIOTIC:
                    bioticData = ds != null ? ((List<MissionBO>) ds).stream().flatMap(m -> m.getFishstationBOs().stream()).collect(Collectors.toList()) : null;
                    findBioticAbsenceData();
                    if (process.isRespondInGUI()) {
                        checkAcousticLayer();
                        checkStationLayer(true);
                    }
                    /*if (process.isBreakInGUI()) {
                        stoxBar.getStationSelectAction().setEnabled(true);
                    }*/
                    break;
                /*case Functions.FN_DEFINERECTANGLE:
                    if (process.isBreakInGUI()) {
                        checkRectangleLayer();
                    }
                // Drop to set handler
                case Functions.FN_DEFINEACOUSTICTRANSECT:
                    if (process.isBreakInGUI()) {
                        checkAcousticLayer();
                        setup.getMapPanel().getMap().setHandler(new TransectBuildHandler(setup));
                        stoxBar.getStationSelectAction().setEnabled(false);
                        stoxBar.getTransectAction().setEnabled(true);
                    }
                    break;
                case Functions.FN_RECTANGLEASSIGNMENT:
                    if (process.isBreakInGUI()) {
                        checkRectangleLayer();
                    }
                // Drop to set handler
                case Functions.FN_BIOSTATIONASSIGNMENT:
                    if (process.isBreakInGUI()) {
                        checkAcousticLayer();
                        checkStationLayer();
                        setup.getMapPanel().getMap().setHandler(new BioStationAssignmentHandler(setup));
                        stoxBar.getStationSelectAction().setEnabled(true);
                        stoxBar.getBioAction().setEnabled(true);
                        stoxBar.getTransectAction().setEnabled(false);
                    }
                    break;
                case Functions.FN_WRITEPROCESSDATA:
                    // Finish model - final gui model response
                    strataEditBar.enableControl(false);
                    stoxBar.getBioAction().setEnabled(false);
                    stoxBar.getTransectAction().setEnabled(false);
                    stoxBar.getStationSelectAction().setEnabled(false);
                    break;*/
            }
        } catch (Exception ex) {
            // stop model because of error
            //          model.setRunState(IModel.RUNSTATE_STOPPED);
            Exceptions.printStackTrace(ex);
        }
    }

    // In the pause the gui controls has been enabled like i.e. strata editing or map editing.
    // When continuing from pause the gui controls must be disabled again to prevent editing.
    private void onStartModel(IModel model) {
        IProcess process = model.getRunningProcess();
        if (process == null) {
            return;
        }
        try {
            switch (process.getMetaFunction().getName()) {
                case Functions.FN_DEFINESTRATA:
                    strataEditBar.getGuiEdit().setEnabled(false);
                    break;
                case Functions.FN_FILTERBIOTIC:
                    stoxBar.getStationSelectAction().setEnabled(false);
                    break;
//                case Functions.FN_DEFINERECTANGLE:
                // Drop to set handler
                case Functions.FN_DEFINEACOUSTICPSU:
                    stoxBar.getTransectAction().setEnabled(false);
                    setup.getMapPanel().getMap().setHandler(null);
                    // Clear selection in acoustic layer
                    FeatureUtil.clearLayerSelection(setup.getAcousticFeatures(), setup.getAcousticLayer());
                    break;
  //              case Functions.FN_RECTANGLEASSIGNMENT:
                // Drop to set handler
                case Functions.FN_BIOSTATIONASSIGNMENT:
                    stoxBar.getStationSelectAction().setEnabled(false);
                    stoxBar.getBioAction().setEnabled(false);
                    setup.getMapPanel().getMap().setHandler(null);
                    // Clear selection in acoustic and fish station layer.
                    FeatureUtil.clearLayerSelection(setup.getAcousticFeatures(), setup.getAcousticLayer());
                    FeatureUtil.clearLayerSelection(setup.getStationFeatures(), setup.getStationLayer());
            }
        } catch (Exception ex) {
            // stop model because of error
            //model.setRunState(IModel.RUNSTATE_STOPPED);
            Exceptions.printStackTrace(ex);
        }
    }

    private void checkAcousticLayer() {
        if (setup.getAcousticFeatures() == null && acousticData != null) {
            setup.addAcousticLayer(acousticData, acousticAbsenceData);
        }
    }

    private void checkStationLayer(boolean force) {
        if ((force || setup.getStationLayer() == null) && bioticData != null) {
            setup.addStationLayer(bioticData, bioticAbsenceData);
        }
    }

    /*private void checkRectangleLayer() {
        if (setup.getRectangleLayer() == null) {
            setup.addRectangleLayer(AbndEstProcessDataUtil.getPSUStrata(
                    Lookup.getDefault().lookup(ProcessDataProvider.class).getPd()).getRowKeys());
        }
    }*/

    List<DistanceBO> searchDistances(List<DistanceBO> dist, boolean absence, String grp) {
        return dist.stream().parallel().filter(d -> {
            return absence == (d.getFrequencies().stream()
                    .map(f -> f.getSa()).flatMap(l -> l.stream())
                    .filter(sa -> sa.getAcoustic_category().equals(grp))
                    .count() == 0L);
        }).collect(Collectors.toList());
    }

    private void findAcousticAbsenceData(IProcess process) {
        if (acousticData == null) {
            return;
        }
        acousticAbsenceData = null;
        String s = (String) process.getParameterValue(Functions.PM_FILTERACOUSTIC_NASCEXPR);
        if (s == null) {
            return;
        }
        Pattern p = Pattern.compile("(acocat)\\s*(==|eq)\\s*(\\d+)");
        Matcher m = p.matcher(s);
        String grp = m.find() ? m.group(3) : null;
        if (grp == null) {
            return;
        }
        List<DistanceBO> d = (List) acousticData;
        acousticAbsenceData = (List) searchDistances(d, true, grp);
    }

    private void findBioticAbsenceData() {
        if (bioticData == null) {
            return;
        }
        bioticAbsenceData
                = bioticData.stream()
                        .map(d -> (FishstationBO) d)
                        .filter(fs -> {
                            // Empty if no samples exists:
                            return fs.getCatchSampleBOs().isEmpty();
                        })
                        .collect(Collectors.toList());
    }

}
