/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stoxmap.handler;

import java.awt.event.MouseEvent;
import javax.swing.ToolTipManager;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.imrbase.util.IMRdate;
import no.imr.sea2data.stox.mapgui.StoXMapSetup;
import no.imr.stox.api.IProjectProvider;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.model.IProject;
import no.imr.stoxmap.utils.FeatureBO;
import no.imr.stoxmap.utils.FeatureUtil;
import no.imr.stoxmap.utils.MapUtils;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.opengis.feature.Feature;
import org.openide.util.Lookup;

/**
 *
 * @author aasmunds
 */
public class MoveHandler {

    private static FeatureBO searchFishStation(StoXMapSetup setup, MouseEvent me, JMap2D map) {
        if (setup.getStationFeatures() == null) {
            return null;
        }
        Feature f = FeatureUtil.searchFeature(setup.getStationLayer().getCollection(), me, 10 + 1, map);
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

    private static FeatureBO searchStratum(StoXMapSetup setup, MouseEvent me, JMap2D map) {
        if (setup.getStrataFeatures() == null) {
            return null;
        }
        Feature f = FeatureUtil.searchFeature(setup.getStrataLayer().getCollection(), me, 0 + 1, map);
        if (f == null) {
            return null;
        }
        for (FeatureBO fb : setup.getStrataFeatures()) {
            if (fb.getFeature().getIdentifier().getID().equals(f.getIdentifier().getID())) {
                return fb;
            }
        }
        return null;
    }

    public static void mouseMoved(StoXMapSetup setup, final MouseEvent e, JMap2D map) {
        if (e.isControlDown()) {
            // Show information in an tool tip centered at top.
            FeatureBO sf = searchFishStation(setup, e, map);
            if (sf == null) {
                sf = MapUtils.searchDistance(setup.getAcousticFeatures(), e.getX(), e.getY(), 4 + 1, map.getCanvas());
                if (sf == null) {
                    sf = searchStratum(setup, e, map);
                }
            }
            if (sf == null) {
                return;
            }
            String s;
            if (sf.getUserData() != null && sf.getUserData() instanceof DistanceBO) {
                DistanceBO d = (DistanceBO) sf.getUserData();
                s = "<html>Cruise: " + d.getCruise() + "<br>Log: " + d.getLog_start() + "<br>Date: " + IMRdate.getLocalDate(d.getStart_time()).toString()
                        + "<br>Time: " + IMRdate.getLocalTime(d.getStart_time());
                IProject p = Lookup.getDefault().lookup(IProjectProvider.class).getProject();
                String psu = AbndEstProcessDataUtil.getEDSUPSU(p.getProcessData(), d.getKey());
                if (psu != null) {
                    s = s + "<br>PSU: " + psu;
                    String stratum = AbndEstProcessDataUtil.getPSUStratum(p.getProcessData(), psu);
                    if (stratum != null) {
                        s = s + "<br>Stratum: " + stratum;
                    }
                }
                s = s + "</html>";
            } else if (sf.getUserData() != null && sf.getUserData() instanceof FishstationBO) {
                FishstationBO fs = (FishstationBO) sf.getUserData();
                s = "<html>Cruise: " + fs.getCruise() + "<br>Serialno: " + fs.getSerialNo() + "</html>";
            } else {
                s = "<html>Stratum: " + sf.getName() + "</html>";
            }
            if (s == null) {
                return;
            }
            map.setToolTipText(s);
            MouseEvent phantom = new MouseEvent(map, MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0, e.getX() + 20, e.getY() - 40, 0, false);
            ToolTipManager.sharedInstance().setDismissDelay(2000);
            ToolTipManager.sharedInstance().setInitialDelay(0);
            ToolTipManager.sharedInstance().mouseMoved(phantom);
            map.setToolTipText("");
        }
    }

}
