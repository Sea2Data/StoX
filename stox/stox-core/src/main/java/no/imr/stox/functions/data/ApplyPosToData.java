/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.data;

import LandingsTypes.v2.LandingsdataType;
import LandingsTypes.v2.SeddellinjeType;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.MissionBO;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.bo.LandingData;
import no.imr.stox.bo.landing.LandingsdataBO;
import no.imr.stox.bo.landing.SeddellinjeBO;
import no.imr.stox.datastorage.BioticDataStorage;
import no.imr.stox.datastorage.IDataStorage;
import no.imr.stox.datastorage.LandingDataStorage;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.ProjectUtils;
import no.imr.stox.functions.utils.StratumUtils;

/**
 *
 * @author aasmunds
 */
public class ApplyPosToData extends AbstractFunction {

    String dataSource;

    @Override
    public Object perform(Map<String, Object> input) {
        String fileName = ProjectUtils.resolveParameterFileName((String) input.get(Functions.PM_APPLYPOSTODATA_FILENAME),
                (String) input.get(Functions.PM_PROJECTFOLDER));
        if (fileName == null) {
            return null;
        }
        String areaCoding = (String) input.get(Functions.PM_APPLYPOSTODATA_AREACODING);
        MatrixBO posMap = StratumUtils.getAreaLocationPositionByFile(fileName, areaCoding);
        if (posMap == null) {
            return null;
        }
        dataSource = (String) input.get(Functions.PM_APPLYPOSTODATA_SOURCETYPE);
        if (dataSource == null) {
            return null;
        }
        LandingData landing = (LandingData) input.get(Functions.PM_APPLYPOSTODATA_LANDINGDATA);
        List<MissionBO> biotic = (List) input.get(Functions.PM_APPLYPOSTODATA_BIOTICDATA);
        switch (dataSource) {
            case Functions.SOURCETYPE_LANDING:
                LandingData landing2 = landing;//LandingUtils.copyLandingData(landing);
                for (LandingsdataBO l : landing2) {
                    for (SeddellinjeBO sl : l.getSeddellinjeBOs()) {
                        if (sl.getLongitude() != null && sl.getLatitude() != null) {
                            continue;
                        }
                        String stratum = StratumUtils.getStratumName(areaCoding, sl.bo().getHovedområdeKode(), sl.bo().getLokasjonKode());
                        Point2D.Double pt = (Point2D.Double) posMap.getRowValue(stratum);
                        if (pt == null) {
                            continue;
                        }
                        sl.setLongitude(pt.x);
                        sl.setLatitude(pt.y);
                    }
                }
                return landing2;
            case Functions.SOURCETYPE_BIOTIC:
                List<MissionBO> biotic2 = biotic;//BioticUtils.copyBioticData(biotic); cannot copy data 
                for (MissionBO ms : biotic2) {
                    for (FishstationBO fs : ms.getFishstationBOs()) {
                        if (fs.bo().getLatitudestart() != null && fs.bo().getLongitudestart() != null) {
                            continue;
                        }
                        String stratum = StratumUtils.getStratumName(areaCoding, fs.bo().getArea(), fs.bo().getLocation());
                        Point2D.Double pt = (Point2D.Double) posMap.getRowValue(stratum);
                        if (pt == null) {
                            continue;
                        }
                        fs.bo().setLatitudestart(pt.y);
                        fs.bo().setLongitudestart(pt.x);
                    }
                }
                return biotic2;
        }

        return null;
    }

    @Override
    public IDataStorage getDataStorage() {
        if (dataSource == null) {
            return null;
        }
        switch (dataSource) {
            case Functions.SOURCETYPE_LANDING:
                return new LandingDataStorage();
            case Functions.SOURCETYPE_BIOTIC:
                return new BioticDataStorage();
        }

        return null;
    }
}
