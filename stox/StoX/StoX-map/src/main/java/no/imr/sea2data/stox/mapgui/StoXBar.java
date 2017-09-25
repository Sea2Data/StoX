package no.imr.sea2data.stox.mapgui;

import no.imr.stoxmap.action.AcousticAbsenceAction;
import no.imr.stoxmap.action.BioStationAssignmentAction;
import no.imr.stoxmap.action.StationSelectAction;
import no.imr.stoxmap.action.TransectBuildAction;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.gui.swing.render2d.control.AbstractMapControlBar;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author aasmunds
 */
public class StoXBar extends AbstractMapControlBar {

    StoXMapSetup setup;
    private final BioStationAssignmentAction bioAction;
    private final TransectBuildAction transectAction;
    private final StationSelectAction stationSelectAction;
    private final AcousticAbsenceAction acousticAbsenceAction;

    public StoXBar(final StoXMapSetup setup) {
        super(setup.getMapPanel().getMap());
        setEnabled(true);
        this.setup = setup;
        transectAction = new TransectBuildAction(setup);
        add(transectAction);
        bioAction = new BioStationAssignmentAction(setup);
        add(bioAction);
        stationSelectAction = new StationSelectAction(setup);
        add(stationSelectAction);
        acousticAbsenceAction = new AcousticAbsenceAction(setup);
        add(acousticAbsenceAction);
        setMap(setup.getMapPanel().getMap());
    }

    public BioStationAssignmentAction getBioAction() {
        return bioAction;
    }

    public AcousticAbsenceAction getAcousticAbsenceAction() {
        return acousticAbsenceAction;
    }

    public TransectBuildAction getTransectAction() {
        return transectAction;
    }

    public StationSelectAction getStationSelectAction() {
        return stationSelectAction;
    }

    @Override
    public final void setMap(final JMap2D map2d) {
        super.setMap(map2d);
        bioAction.setMap(map);
        transectAction.setMap(map);
        stationSelectAction.setMap(map);
        acousticAbsenceAction.setMap(map);
    }

}
