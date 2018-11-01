/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.data;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiPolygon;
import java.util.List;
import java.util.Map;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.MissionBO;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.sea2data.imrmap.utils.JTSUtils;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.bo.landing.SluttSeddel;
import no.imr.stox.datastorage.BioticDataStorage;
import no.imr.stox.datastorage.IDataStorage;
import no.imr.stox.datastorage.LandingDataStorage;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;

/**
 *
 * @author aasmunds
 */
public class AssignDataToStratum extends AbstractFunction {

    String dataSource;

    @Override
    public Object perform(Map<String, Object> input) {
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_ASSIGNDATATOSTRATUM_PROCESSDATA);
        if (pd == null) {
            return null;
        }
        dataSource = (String) input.get(Functions.PM_ASSIGNDATATOSTRATUM_SOURCETYPE);
        if (dataSource == null) {
            return null;
        }
        List<SluttSeddel> landing = (List<SluttSeddel>) input.get(Functions.PM_ASSIGNDATATOSTRATUM_LANDINGDATA);
        List<MissionBO> biotic = (List) input.get(Functions.PM_ASSIGNDATATOSTRATUM_BIOTICDATA);
        switch (dataSource) {
            case Functions.SOURCETYPE_LANDING:
                for (SluttSeddel sl : landing) {
                    if (sl.getLongitude() == null || sl.getLatitude() == null) {
                        continue;
                    }
                    sl.setStratum(getStratumFromPosition(pd, sl.getLongitude(), sl.getLatitude()));
                }
                return landing;
            case Functions.SOURCETYPE_BIOTIC:
                for (MissionBO ms : biotic) {
                    for (FishstationBO fs : ms.getFishstationBOs()) {
                        if (fs.getFs().getLatitudestart() == null && fs.getFs().getLongitudestart() == null) {
                            continue;
                        }
                        fs.setStratum(getStratumFromPosition(pd, fs.getFs().getLongitudestart(), fs.getFs().getLatitudestart()));
                    }
                }
                return biotic;
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

    public static String getStratumFromPosition(ProcessDataBO pd, Double lon, Double lat) {
        Coordinate fPos = new Coordinate(lon, lat);
        MatrixBO stratumPlgs = AbndEstProcessDataUtil.getStratumPolygons(pd);
        for (String stratum : AbndEstProcessDataUtil.getStrata(pd)) {
            MultiPolygon stratumPol = (MultiPolygon) stratumPlgs.getRowColValue(stratum, Functions.COL_POLVAR_POLYGON);
            if (JTSUtils.within(fPos, stratumPol)) {
                return stratum;
            }
        }
        return null;
    }
}
