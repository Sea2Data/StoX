package no.imr.stox.functions.processdata;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiPolygon;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.imrmap.utils.JTSUtils;
import no.imr.stox.functions.utils.AbndEstParamUtil;
import no.imr.stox.functions.utils.EchosounderUtils;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.log.ILogger;

/**
 * Auto assigns stations and weights to length distributions based on the
 * distance between the fishstations and the acoustic mile.
 *
 * @author aasmunds
 */
public class BioStationAssignment extends AbstractFunction {

    /**
     *
     * @param input
     * @return
     */
    @Override
    public Object perform(Map<String, Object> input) {
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_BIOSTATIONASSIGNMENT_PROCESSDATA);
        List<FishstationBO> fList = (List<FishstationBO>) input.get(Functions.PM_BIOSTATIONASSIGNMENT_BIOTICDATA);

        List<DistanceBO> distances = (List<DistanceBO>) input.get(Functions.PM_BIOSTATIONASSIGNMENT_ACOUSTICDATA);
        String assignmentMethod = (String) input.get(Functions.PM_BIOSTATIONASSIGNMENT_ASSIGNMENTMETHOD);
        Double radius = (Double) input.get(Functions.PM_BIOSTATIONASSIGNMENT_RADIUS);
        MatrixBO stratumPlgs = AbndEstProcessDataUtil.getStratumPolygons(pd);
        // Booleans
        Boolean byRadius = assignmentMethod.equals(Functions.ASSIGNMENTMETHOD_RADIUS);
        Boolean useExisting = assignmentMethod.equals(Functions.ASSIGNMENTMETHOD_USEPROCESSDATA);
        String estLayerDef = (String) input.get(Functions.PM_BIOSTATIONASSIGNMENT_ESTLAYERS);
        MatrixBO estLayer = AbndEstParamUtil.getEstLayerMatrixFromEstLayerDef(estLayerDef);
        // Transfer estimation layer table to process data as transient table available for pickup from later processes
        pd.getMatrices().put(Functions.TABLE_ESTLAYERDEF, estLayer);
        // Work on PSU Watercolumn:
        AbndEstProcessDataUtil.setAssignmentResolution(pd, Functions.SAMPLEUNIT_PSU);
        if (estLayerDef != null) {
            String layerType = Functions.LAYERTYPE_PCHANNEL;
            if (estLayerDef.contains(Functions.WATERCOLUMN_PELBOT)) {
                layerType = Functions.LAYERTYPE_WATERCOLUMN;
            } else if (estLayerDef.contains(Functions.DEPTHLAYER_PEL) || estLayerDef.contains(Functions.DEPTHLAYER_BOT)) {
                layerType = Functions.LAYERTYPE_DEPTHLAYER;
            } else if (estLayerDef.isEmpty()) {
                layerType = Functions.LAYERTYPE_WATERCOLUMN;
            }
            AbndEstProcessDataUtil.getAssignmentResolutions(pd).setRowValue(Functions.RES_LAYERTYPE, layerType);
        }
        if (!useExisting) {

            if (byRadius) {
                if (radius == null) {
                    logger.error("Missing parameter assignment radius", null);
                }
                if (distances == null) {
                    logger.error("Missing parameter AcousticData", null);
                }
            }
            // Clear trawl and acoustic assignments
            // trawlAsg = Matrix[ROW~Assignment / COL~Station / VAR~StationWeight]
            // acoAsg = Matrix[ROW~Distance / COL~Layer / VAR~Assignment]
            MatrixBO bsAsg = AbndEstProcessDataUtil.getBioticAssignments(pd);
            bsAsg.clear();

            MatrixBO psuAsg = AbndEstProcessDataUtil.getSUAssignments(pd);
            psuAsg.clear();

            // Use distance-psu and psu-stratum to build the assignments
            Integer asg = 1;
            for (String stratum : AbndEstProcessDataUtil.getStrata(pd)) {
                MultiPolygon stratumPol = (MultiPolygon) stratumPlgs.getRowColValue(stratum, Functions.COL_POLVAR_POLYGON);
                for (String psu : AbndEstProcessDataUtil.getPSUsByStratum(pd, stratum)) {
                    Boolean psuIsAssigned = false;
                    Collection<DistanceBO> distsBOPerPSU = null;
                    if (byRadius) {
                        Collection<String> distsPerPSU = AbndEstProcessDataUtil.getEDSUsByPSU(pd, psu);
                        distsBOPerPSU = EchosounderUtils.findDistances(distances, distsPerPSU);
                    }
                    String asgKey = asg.toString();

                    // Build the trawl assignments
                    for (FishstationBO fs : fList) {
                        if (fs.getLatitudeStart() == null || fs.getLongitudeStart() == null) {
                            logger.error("Missing position at " + fs.getKey(), null);
                        }
                        Coordinate fPos = new Coordinate(fs.getLongitudeStart(), fs.getLatitudeStart());
                        boolean assigned = false;
                        if (byRadius) {
                            // Calculate if the radius is covering some of the distances
                            for (DistanceBO distBO : distsBOPerPSU) {
                                if (distBO == null) {
                                    continue;
                                }
                                Coordinate dPos = new Coordinate(distBO.getLon_start(), distBO.getLat_start());
                                Double gcDist = JTSUtils.gcircledist(fPos, dPos);
                                if (gcDist < radius) {
                                    assigned = true;
                                    break;
                                }
                            }
                        } else {
                            // assign by strata
                            // all stations inside stratum to all transects 
                            assigned = JTSUtils.within(fPos, stratumPol);
                        }
                        if (assigned) {
                            // Define trawl station assignment with default weight=1
                            bsAsg.setRowColValue(asgKey, fs.getKey(), 1d);
                            psuIsAssigned = true;
                        }
                    }
                    if (psuIsAssigned) {
                        // Set sample unit assignment on all estimation layers by default.
                        for (String layer : estLayer.getRowKeys()) {
                            AbndEstProcessDataUtil.setSUAssignment(pd, psu, layer, asgKey);
                        }
                        asg++;
                    }
                }
            }
        }
        AbndEstProcessDataUtil.regroupAssignments(pd); // Optimize assignments
        checkMissingAssignments(logger, pd, estLayer);
        return pd;
    }

    private void checkMissingAssignments(ILogger logger, ProcessDataBO pd, MatrixBO estLayer) {
        String missingAsgError = "";
        MatrixBO bsAsg = AbndEstProcessDataUtil.getBioticAssignments(pd);
        for (String stratum : AbndEstProcessDataUtil.getStrata(pd)) {
            for (String psu : AbndEstProcessDataUtil.getPSUsByStratum(pd, stratum)) {
                Boolean isAssigned = estLayer.getRowKeys().stream()
                        .filter(layer -> {
                            String asg = AbndEstProcessDataUtil.getSUAssignment(pd, psu, layer);
                            // Check if assignment is in BIOTIC assignment also.
                            MatrixBO m = bsAsg.getRowValueAsMatrix(asg);
                            return m != null;
                        })
                        .count() > 0L;
                if (!isAssigned) {
                    if (!missingAsgError.isEmpty()) {
                        missingAsgError += ", ";
                    }
                    missingAsgError += stratum + "." + psu;
                }
            }
        }
        if (!missingAsgError.isEmpty()) {
            logger.log("Warning: Missing assignments for EDSU at the given PSU's:" + missingAsgError + ". Solution: 1) Perform assignments manually or 2) Redefine transects or 3) Change assignment method/parameter");
        }
    }
}
