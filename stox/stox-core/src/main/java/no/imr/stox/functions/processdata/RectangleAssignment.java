/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.processdata;

import com.vividsolutions.jts.geom.Coordinate;
import java.util.List;
import java.util.Map;
import no.imr.stox.functions.AbstractFunction;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.imrmap.utils.JTSUtils;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.utils.AbndEstParamUtil;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.RectangleUtil;

/**
 * TODO: what does this class do?
 *
 * @author Åsmund
 */
public class RectangleAssignment extends AbstractFunction {

    /**
     *
     * @param input
     * @return
     */
    @Override
    public Object perform(Map<String, Object> input) {
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_RECTANGLEASSIGNMENT_PROCESSDATA);
        List<FishstationBO> fs = (List<FishstationBO>) input.get(Functions.PM_RECTANGLEASSIGNMENT_BIOTICDATA);
        Boolean useEx = (Boolean) input.get(Functions.PM_RECTANGLEASSIGNMENT_USEPROCESSDATA);
        String estLayerDef = (String) input.get(Functions.PM_RECTANGLEASSIGNMENT_ESTLAYERS);
        MatrixBO estLayer = AbndEstParamUtil.getEstLayerMatrixFromEstLayerDef(estLayerDef);
        // Transfer estimation layer table to process data as transient table available for pickup from later processes
        pd.getMatrices().put(Functions.TABLE_ESTLAYERDEF, estLayer);
        if (useEx != null && useEx) {
            return pd;
        }
        // Clear trawl and acoustic assignments
        // trawlAsg = Matrix[ROW~Assignment / COL~Station / VAR~StationWeight]
        // acoAsg = Matrix[ROW~Distance / COL~Layer / VAR~Assignment]
        MatrixBO trawlAsg = AbndEstProcessDataUtil.getBioticAssignments(pd);
        trawlAsg.clear();
        MatrixBO psuAsg = AbndEstProcessDataUtil.getSUAssignments(pd);
        psuAsg.clear();

        // Work on PSU Watercolumn:
        AbndEstProcessDataUtil.setAssignmentResolution(pd, Functions.SAMPLEUNIT_PSU);

        // Use distance-psu and psu-stratum to build the assignments
        Integer asg = 1;
        for (String stratum : AbndEstProcessDataUtil.getStrata(pd)) {
            for (String psu : AbndEstProcessDataUtil.getPSUsByStratum(pd, stratum)) {
                Coordinate[] psuCoords = RectangleUtil.getCoordsByRectangleKey(psu);
                String asgKey = asg.toString();
                Boolean psuIsAssigned = false;
                for (FishstationBO f : fs) {
                    Coordinate fPos = new Coordinate(f.getLongitudeStart(), f.getLatitudeStart());
                    if (!JTSUtils.within(fPos, psuCoords)) {
                        continue;
                    }
                    psuIsAssigned = true;
                    // Assign trawlstation
                    trawlAsg.setRowColValue(asgKey, f.getKey(), 1.0d);
                }
                if (psuIsAssigned) {
                    // Set sample unit assignment on all estimation layers by default.
                    for (String layer : estLayer.getRowKeys()) {
                        AbndEstProcessDataUtil.setSUAssignment(pd, psu, layer, asgKey);
                    }
                }
                asg++;
            }
        }
        return pd;
    }
}
