/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.processdata;

import com.vividsolutions.jts.geom.Coordinate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.CatchSampleBO;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.imrmap.utils.JTSUtils;
import no.imr.stox.bo.LengthDistMatrix;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.bo.NASCMatrix;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.BioticUtils;
import no.imr.stox.functions.utils.EchosounderUtils;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.StoXMath;
import no.imr.stox.log.ILogger;

/**
 *
 * @author aasmunds
 */
public class BioStationWeighting extends AbstractFunction {

    /**
     *
     * @param input
     * @return
     */
    @Override
    public Object perform(Map<String, Object> input) {
        String weightingMethod = (String) input.get(Functions.PM_BIOSTATIONWEIGHTING_WEIGHTINGMETHOD);
        switch (weightingMethod) {
            case Functions.WEIGHTINGMETHOD_EQUAL:
                weightEqual(input);
                break; // Do nothing - its equal weighted by default in assignment method.
            case Functions.WEIGHTINGMETHOD_NUMBEROFLENGTHSAMPLES:
                weightByNumberOfLengthSamples(input);
                break;
            case Functions.WEIGHTINGMETHOD_NORMTOTALCOUNT:
            case Functions.WEIGHTINGMETHOD_NORMTOTALWEIGHT:
                weightByCPUE(input, weightingMethod);
                break;
            case Functions.WEIGHTINGMETHOD_INVSUMWEIGHTEDCOUNT:
                weightByInvSumWeightedCount(input);
                break;
            case Functions.WEIGHTINGMETHOD_SUMWEIGHTEDCOUNT:
                weightBySumWeightedCount(input);
                break;
            case Functions.WEIGHTINGMETHOD_NASC:
                weightByNASC(input);
                break;
        }
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_BIOSTATIONWEIGHTING_PROCESSDATA);
        // Komprimering av assignment med stasjonssett som kriteria bør foregå i BioticAssignment
        // Ekspandering av assignment bør legges inn her i BiostationWeighting når vekting hvor en AsgId per PSU er et krav (Atle/Espen).
        return pd;
    }

    /* List<String> getSUsByPSU(ProcessDataBO pd, String psu) {
        List<String> suKeysAtPSU = new ArrayList<>();
        switch ((String) AbndEstProcessDataUtil.getAssignmentResolutions(pd).getRowValue(Functions.RES_SAMPLEUNITTYPE)) {
            case Functions.SAMPLEUNIT_EDSU:
                suKeysAtPSU.addAll(AbndEstProcessDataUtil.getEDSUsByPSU(pd, psu));
                break;
            case Functions.SAMPLEUNIT_PSU:
                suKeysAtPSU.add(psu);
                break;
        }
        return suKeysAtPSU;
    }*/
    private void weightByNASC(Map<String, Object> input) {
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_BIOSTATIONWEIGHTING_PROCESSDATA);
        List<FishstationBO> fList = (List<FishstationBO>) input.get(Functions.PM_BIOSTATIONWEIGHTING_BIOTICDATA);
        if (fList == null) {
            logger.error("Missing parameter " + Functions.PM_BIOSTATIONWEIGHTING_BIOTICDATA, null);
            return;
        }
        NASCMatrix nascMatrix = (NASCMatrix) input.get(Functions.PM_BIOSTATIONWEIGHTING_NASC);
        if (nascMatrix == null) {
            return;
        }
        List<String> keys = nascMatrix.getData().getKeys();
        String acoCat = keys.size() == 1 ? keys.get(0) : null;
        if (acoCat == null) {
            logger.error("NASC should contain one and only one acoustic category.", null);
        }
        if (!nascMatrix.getResolutionMatrix().getRowValue(Functions.RES_SAMPLEUNITTYPE).equals(Functions.SAMPLEUNIT_EDSU)) {
            logger.error("Weight by NASC requires input NASC sample unit type EDSU", null);
        }
        List<DistanceBO> distances = (List<DistanceBO>) input.get(Functions.PM_BIOSTATIONWEIGHTING_ACOUSTICDATA);
        Double nascWeightingRadius = (Double) input.get(Functions.PM_BIOSTATIONWEIGHTING_RADIUS);
        Double nascWeightingM = (Double) input.get(Functions.PM_BIOSTATIONWEIGHTING_M);
        Double nascWeightingA = (Double) input.get(Functions.PM_BIOSTATIONWEIGHTING_A);

        if (nascWeightingRadius == null) {
            logger.error("Missing parameter NASCweighting radius", null);
        }
        LengthDistMatrix lengthDist = (LengthDistMatrix) input.get(Functions.PM_BIOSTATIONWEIGHTING_LENGTHDIST);
        if (lengthDist == null || lengthDist.getResolutionMatrix() == null) {
            logger.error("Missing parameter lengthdist", null);
            return;
        }
        Double nascWeightingLengthInterval = lengthDist.getResolutionMatrix().getRowValueAsDouble(Functions.RES_LENGTHINTERVAL);
        // trawlAsg = Matrix[ROW~Assignment / COL~Station / VAR~StationWeight]
        MatrixBO bioticAsg = AbndEstProcessDataUtil.getBioticAssignments(pd);

        // For each assignment, calculate the assignment station weights
        // for(asgKey : asgKeys) {...
        // Get the DistanceBO's used as EDSUs in PSUs
        Collection<String> edsuKeys = AbndEstProcessDataUtil.getEDSUPSUs(pd).getRowKeys();
        Set<DistanceBO> dList = distances.parallelStream()
                .filter(d -> edsuKeys.contains(d.getKey()))
                .collect(Collectors.toSet());
        bioticAsg.getRowKeys().stream().forEach((asgKey) -> {
            // Build the trawl assignments
            // Weight by radius
            MatrixBO includedDist = new MatrixBO("Matrix[ROW~Station / COL~Distance / VAR~Included]");
            MatrixBO distStationCount = new MatrixBO("Matrix[ROW~Distance / VAR~Count]");
            for (String fsKey : bioticAsg.getRowColKeys(asgKey)) {
                FishstationBO fs = BioticUtils.findStation(fList, fsKey);
                if (fs == null) {
                    // Some stations may be defined in assignment but filtered out by i.e missing catch
                    continue;
                }
                Coordinate fPos = new Coordinate(fs.getLongitudestart(), fs.getLatitudestart());
                //Double gcDist = Math.sqrt(Math.pow(fPos.x - dPos.x, 2) + Math.pow(fPos.y - dPos.y, 2));
                dList.stream().forEach(d -> {
                    String distKey = d.getKey();
                    Coordinate dPos = new Coordinate(d.getLon_start(), d.getLat_start());
                    Double gcDist = JTSUtils.gcircledist(fPos, dPos);
                    if (gcDist < nascWeightingRadius) {
                        includedDist.setRowColValue(fsKey, distKey, true);
                        distStationCount.addRowValue(distKey, 1);
                    }
                });
            }

            for (String fsKey : bioticAsg.getRowColKeys(asgKey)) {
                // Set default weighting 0 (outside radius)
                bioticAsg.setRowColValue(asgKey, fsKey, 0d);
                // Set the representative weights for the stations that fit into the radius for each distance
                List<String> distKeys = includedDist.getRowColKeys(fsKey);
                MatrixBO nascProportion = new MatrixBO("Matrix[ROW~Distance / VAR~Proportion]");
                if (distKeys.isEmpty()) {
                    continue;
                }
                // Handle overlapping circles:
                for (String distKey : distKeys) {
                    Integer nStationsToDivide = distStationCount.getRowValueAsInteger(distKey);
                    if (nStationsToDivide == null || nStationsToDivide == 0) {
                        continue;
                    }
                    Double nascProp = 1d / nStationsToDivide;
                    nascProportion.setRowValue(distKey, nascProp);
                }
                // Calculate meanNASC for a given station and the set of distances included with overlap handled
                Double meanNASC = EchosounderUtils.aggregateHorizontallyDistanceMatrix(distKeys, nascMatrix, acoCat, distances, nascProportion);
                if (meanNASC != null) {
                    // Calculate the sum density (number per sqm) using the station LFQ and meanNASC.
                    // The idea is that the NASC represents through a LFQ the number of fish (NASC for many small fish = NASC for few big fish) and
                    // number of fish is the best weighting criteria for pelagic accuracy.
                    // get length interval, a and b as parameter?
                    //Matrix[GROUP~Species / ROW~Station / CELL~LengthGroup / VAR~WeightedCount]
                    // Should create a new total LFQ from all species, not only the first one.
                    MatrixBO totlDist = new MatrixBO();
                    if (lengthDist.getData().getKeys() != null) {
                        for (String species : lengthDist.getData().getKeys()) {
                            MatrixBO lDist = lengthDist.getData().getGroupRowDefaultValueAsMatrix(species, fsKey);
                            if (lDist != null) {
                                for (String lenGrp : lDist.getKeys()) {
                                    totlDist.addDoubleValue(lenGrp, lDist.getValueAsDouble(lenGrp));
                                }
                            }
                        }
                    }

                    MatrixBO densDist = StoXMath.getDensityDistribution(meanNASC, totlDist, nascWeightingLengthInterval, null,
                            nascWeightingM, nascWeightingA, 0d);
                    Double sumDens = densDist.getDefaultValueAsMatrix() != null ? densDist.getDefaultValueAsMatrix().getSum() : 0;
                    bioticAsg.setRowColValue(asgKey, fsKey, sumDens);
                }
            }
        });
    }

    /**
     * weight equally
     *
     * @param input
     */
    private void weightEqual(Map<String, Object> input) {
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_BIOSTATIONWEIGHTING_PROCESSDATA);
        // trawlAsg = Matrix[ROW~Assignment / COL~Station / VAR~StationWeight]
        MatrixBO trawlAsg = AbndEstProcessDataUtil.getBioticAssignments(pd);
        //MatrixBO suAsg = AbndEstProcessDataUtil.getSUAssignments(pd);
        // Set assignment weights to 1
        for (String fsKey : trawlAsg.getColKeys()) {
            Double wgt = 1.0;
            for (String asgKey : trawlAsg.getRowKeys()) {
                if (trawlAsg.getRowColValue(asgKey, fsKey) != null) {
                    trawlAsg.setRowColValue(asgKey, fsKey, wgt);
                }
            }
        }
    }

    /**
     * weight by number of length samples
     *
     * @param input
     */
    private void weightByNumberOfLengthSamples(Map<String, Object> input) {
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_BIOSTATIONWEIGHTING_PROCESSDATA);
        List<FishstationBO> fList = (List<FishstationBO>) input.get(Functions.PM_BIOSTATIONWEIGHTING_BIOTICDATA);
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        if (fList == null) {
            logger.error("Missing parameter " + Functions.PM_BIOSTATIONWEIGHTING_BIOTICDATA, null);
            return;
        }
        Integer maxNumlengthSamples = (Integer) input.get(Functions.PM_BIOSTATIONWEIGHTING_MAXNUMLENGTHSAMPLES);
        // trawlAsg = Matrix[ROW~Assignment / COL~Station / VAR~StationWeight]
        MatrixBO trawlAsg = AbndEstProcessDataUtil.getBioticAssignments(pd);
        for (String fsKey : trawlAsg.getColKeys()) {
            FishstationBO fs = BioticUtils.findStation(fList, fsKey);
            if (fs == null) {
                continue;
            }
            Integer numLengthSamples = 0;
            for (CatchSampleBO s : fs.getCatchSampleBOs()) {
                if (s.getIndividualBOs() != null) {
                    numLengthSamples += s.getIndividualBOs().size();
                }
            }
            if (maxNumlengthSamples != null) {
                numLengthSamples = Math.min(maxNumlengthSamples, numLengthSamples);
            }
            for (String asgKey : trawlAsg.getRowKeys()) {
                if (trawlAsg.getRowColValue(asgKey, fsKey) != null) {
                    trawlAsg.setRowColValue(asgKey, fsKey, numLengthSamples);
                }
            }
        }
    }

    private void weightByCPUE(Map<String, Object> input, String weightingMethod) {
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_BIOSTATIONWEIGHTING_PROCESSDATA);
        List<FishstationBO> fList = (List<FishstationBO>) input.get(Functions.PM_BIOSTATIONWEIGHTING_BIOTICDATA);
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        if (fList == null) {
            logger.error("Missing parameter " + Functions.PM_BIOSTATIONWEIGHTING_BIOTICDATA, null);
            return;
        }
        // trawlAsg = Matrix[ROW~Assignment / COL~Station / VAR~StationWeight]
        MatrixBO trawlAsg = AbndEstProcessDataUtil.getBioticAssignments(pd);
        for (String fsKey : trawlAsg.getColKeys()) {
            FishstationBO fs = BioticUtils.findStation(fList, fsKey);
            if (fs == null) {
                continue;
            }
            Double totCatch = 0d;
            for (CatchSampleBO s : fs.getCatchSampleBOs()) {
                if (s.getIndividualBOs() != null) {
                    Double var = null;
                    switch (weightingMethod) {
                        case Functions.WEIGHTINGMETHOD_NORMTOTALWEIGHT:
                            var = s.getCatchweight();
                            break;
                        case Functions.WEIGHTINGMETHOD_NORMTOTALCOUNT:
                            var = Conversion.safeIntegerToDouble(s.getCatchcount());
                            break;
                    }
                    if (var != null) {
                        totCatch += var;
                    }
                }
            }
            Double cpue = totCatch;
            if (fs.getDistance() != null && fs.getDistance() > 0d) {
                cpue = totCatch / fs.getDistance();
            } else {
                logger.log(weightingMethod + " not possible due to missing distance for station " + fsKey + ".");
            }
            for (String asgKey : trawlAsg.getRowKeys()) {
                if (trawlAsg.getRowColValue(asgKey, fsKey) != null) {
                    trawlAsg.setRowColValue(asgKey, fsKey, cpue);
                }
            }
        }
    }

    private void weightByTotalWeightedCount(Map<String, Object> input, Function<Double, Double> totalHandler) {
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_BIOSTATIONWEIGHTING_PROCESSDATA);
        LengthDistMatrix lengthDistMatrix = (LengthDistMatrix) input.get(Functions.PM_TOTALLENGTHDIST_LENGTHDIST);
        MatrixBO lengthDist = lengthDistMatrix.getData();
        if (lengthDist == null) {
            logger.error("Missing parameter lengthdist", null);
            return;
        }
        // trawlAsg = Matrix[ROW~Assignment / COL~Station / VAR~StationWeight]
        MatrixBO trawlAsg = AbndEstProcessDataUtil.getBioticAssignments(pd);
        for (String asgKey : trawlAsg.getRowKeys()) {
            for (String fsKey : trawlAsg.getRowColKeys(asgKey)) {
                Double sumval = 0d;
                for (String species : lengthDist.getKeys()) {
                    MatrixBO m = lengthDist.getGroupRowValueAsMatrix(species, fsKey);
                    if (m != null) {
                        MatrixBO lfq = m.getDefaultValueAsMatrix();
                        for (String lenGrp : lfq.getKeys()) {
                            Double val = lfq.getValueAsDouble(lenGrp);
                            if (val != null) {
                                sumval += val;
                            }
                        }
                    }
                }
                Double invsum = 0d;
                if (sumval > 0) {
                    invsum = totalHandler.apply(sumval);//1 / sumval;
                }
                trawlAsg.setRowColValue(asgKey, fsKey, invsum);
            }
        }
    }

    private void weightByInvSumWeightedCount(Map<String, Object> input) {
        weightByTotalWeightedCount(input, sumval -> 1 / sumval);
    }

    private void weightBySumWeightedCount(Map<String, Object> input) {
        weightByTotalWeightedCount(input, sumval -> sumval);
    }
}
