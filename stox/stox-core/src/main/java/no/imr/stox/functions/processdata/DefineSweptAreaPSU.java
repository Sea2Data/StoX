/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.processdata;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiPolygon;
import java.util.List;
import java.util.Map;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.MissionBO;
import no.imr.sea2data.imrmap.utils.JTSUtils;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.bo.BioticData;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.log.ILogger;

/**
 * TODO: what does this class do?
 *
 * @author aasmunds
 */
public class DefineSweptAreaPSU extends AbstractFunction {

    /**
     *
     * @param input contains Polygon file name
     * @return Matrix object of type POLYGON_MATRIX - see
     * DataTypeDescription.txt
     */
    @Override
    public Object perform(Map<String, Object> input) {
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_DEFINESWEPTAREAPSU_PROCESSDATA);
        String method = (String) input.get(Functions.PM_DEFINESWEPTAREAPSU_METHOD);
        BioticData bioticData = (BioticData) input.get(Functions.PM_DEFINESWEPTAREAPSU_BIOTICDATA);
        if (method == null) {
            return pd;
        }
        if (AbndEstProcessDataUtil.getStrata(pd).isEmpty()) {
            logger.error("Strata system is missing, cannot assign PSU to stations", null);
        }

        if (method.equals(Functions.SWEPTAREAPSUMETHOD_STATION)) {
            // Clear the edsu/psu/assignment definitions.
            AbndEstProcessDataUtil.getBioticAssignments(pd).clear();
            AbndEstProcessDataUtil.getSUAssignments(pd).clear();
            AbndEstProcessDataUtil.getEDSUPSUs(pd).clear();
            AbndEstProcessDataUtil.getPSUStrata(pd).clear();
            definePSUAndAssignmentsByBioticData(pd, bioticData);
        }
        // Break in gui at onEndProcess to redefine...
        return pd;
    }

    private void definePSUAndAssignmentsByBioticData(ProcessDataBO pd, BioticData bioticData) {
        // Define station as a PSU with an assignment to itself.
        String estLayer = "1"; // Estimation layer 1 hardcoded when depth doesnt play a role.
        AbndEstProcessDataUtil.setAssignmentResolution(pd, Functions.SAMPLEUNIT_PSU);
        Integer i = 1;
        for (MissionBO ms : bioticData.getMissions()) {
            for (FishstationBO fs : ms.getFishstationBOs()) {
                String station = fs.getKey();
                String edsu = station;
                String psu = "S" + i;//station.replace('/', '-'); // station key as psu will get dash to avoid splitting on output.
                String stratum = getStratumByStation(fs, pd);
                if (stratum == null) {
                    continue;
                }
                String asgKey = i++ + "";
                AbndEstProcessDataUtil.getBioticAssignments(pd).setRowColValue(asgKey, station, 1d);
                AbndEstProcessDataUtil.setSUAssignment(pd, psu, estLayer, asgKey);
                AbndEstProcessDataUtil.setEDSUPSU(pd, edsu, psu);
                AbndEstProcessDataUtil.setPSUStratum(pd, psu, stratum);
            }
        }
    }

    private String getStratumByStation(FishstationBO fs, ProcessDataBO pd) {
        if (fs.bo().getLongitudestart() == null || fs.bo().getLatitudestart() == null) {
            return null;
        }
        Coordinate fPos = new Coordinate(fs.bo().getLongitudestart(), fs.bo().getLatitudestart());
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
