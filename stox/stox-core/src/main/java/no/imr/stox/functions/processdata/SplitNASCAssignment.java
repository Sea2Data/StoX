/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.processdata;

import com.vividsolutions.jts.geom.Coordinate;
import java.util.List;
import java.util.Map;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.MissionBO;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.stox.util.math.Calc;
import no.imr.sea2data.imrmap.utils.JTSUtils;
import no.imr.stox.util.matrix.MatrixBO;
import no.imr.stox.bo.BioticData;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.AbndEstParamUtil;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.log.ILogger;
import no.imr.stox.model.IModel;

/**
 *
 * @author aasmunds
 */
public class SplitNASCAssignment extends AbstractFunction {

    /**
     *
     * @param input
     * @return
     */
    @Override
    public Object perform(Map<String, Object> input) {
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        IModel m = (IModel) input.get(Functions.PM_MODEL);
        ProcessDataBO pd = m.getProject().getProcessData();
        BioticData missions = (BioticData) input.get(Functions.PM_SPLITNASCASSIGNMENT_BIOTICDATA);
        List<DistanceBO> distances = (List<DistanceBO>) input.get(Functions.PM_SPLITNASCASSIGNMENT_ACOUSTICDATA);
        Double radius = (Double) input.get(Functions.PM_SPLITNASCASSIGNMENT_RADIUS);
        // Transfer the nasc resolution to assignment resolution:
        AbndEstProcessDataUtil.setAssignmentResolution(pd, Functions.SAMPLEUNIT_EDSU);
        String estLayer = "1";
        MatrixBO estLayerM = AbndEstParamUtil.getEstLayerMatrixFromEstLayerDef(estLayer + "~" + Functions.WATERCOLUMN_PELBOT); // Set the connection between estlayer and acoustic layer
        // Transfer estimation layer table to process data as transient table available for pickup from later processes
        pd.getMatrices().put(Functions.TABLE_ESTLAYERDEF, estLayerM);

        // Clear trawl and acoustic assignments
        // trawlAsg = Matrix[ROW~Assignment / COL~Station / VAR~StationWeight]
        // acoAsg = Matrix[ROW~Distance / COL~Layer / VAR~Assignment]
        MatrixBO bsAsg = AbndEstProcessDataUtil.getBioticAssignments(pd);
        bsAsg.clear();

        MatrixBO suAsg = AbndEstProcessDataUtil.getSUAssignments(pd);
        suAsg.clear();
        Integer asg = 0;
        Boolean edsuIsAsigned;
        if (missions.getMissions().isEmpty()) {
            return pd;
        }
        for (DistanceBO distBO : distances) {
            Coordinate dPos = new Coordinate(distBO.getLon_start(), distBO.getLat_start());
            String asgKey = (++asg).toString();
            String edsu = distBO.getKey();
            edsuIsAsigned = false;
            Double minDist = Double.MAX_VALUE;
            for (MissionBO ms : missions.getMissions()) {
                for (FishstationBO fs : ms.getFishstationBOs()) {
                    Coordinate fPos = new Coordinate(fs.bo().getLongitudestart(), fs.bo().getLatitudestart());
                    Double gcDist = JTSUtils.gcircledist(fPos, dPos);
                    minDist = Math.min(minDist, gcDist);
                    if (gcDist <= radius) {
                        bsAsg.setRowColValue(asgKey, fs.getKey(), 1d);
                        edsuIsAsigned = true;
                    }
                }
            }
            if (!edsuIsAsigned) {
                logger.error("Assignment radius should be min " + Calc.roundTo(minDist, 2) + " for " + edsu + " to be assigned.", null);
            }
            AbndEstProcessDataUtil.setSUAssignment(pd, edsu, estLayer, asgKey);
        }
        AbndEstProcessDataUtil.regroupAssignments(pd);

        return pd;
    }
}
