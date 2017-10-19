package no.imr.stox.functions.density;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.imr.sea2data.biotic.bo.CatchBO;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.SampleBO;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.bo.DensityMatrix;
import no.imr.stox.bo.LengthDistMatrix;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.BioticUtils;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.StoXMath;
import no.imr.stox.log.ILogger;

/**
 * This function calculates densities by length.
 *
 * @author reworked to use matrix by aasmunds
 */
public class SweptAreaDensity extends AbstractFunction {

    /**
     * @param input contains a, b, c flags, group interval, layertype,
     * ACOUSTICDATA, PROCESSDATA, matrices STATIONLENGTHDISTand NASC, logger
     * @return Matrix object of type DENSITY_MATRIX - see
     * DataTypeDescription.txt
     */
    @Override
    public Object perform(Map<String, Object> input) {
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_SWEPTAREADENSITY_PROCESSDATA);
        String distanceMethod = (String) input.get(Functions.PM_SWEPTAREADENSITY_DISTANCEMETHOD);
        String sweptAreaMethod = (String) input.get(Functions.PM_SWEPTAREADENSITY_SWEPTAREAMETHOD);
        String catchVariable = (String) input.get(Functions.PM_SWEPTAREADENSITY_CATCHVARIABLE);
        List<FishstationBO> bioticData = (List<FishstationBO>) input.get(Functions.PM_SWEPTAREADENSITY_BIOTICDATA);
        LengthDistMatrix lengthDist = (LengthDistMatrix) input.get(Functions.PM_SWEPTAREADENSITY_LENGTHDIST);
        String lenDistType = lengthDist != null ? (String) lengthDist.getResolutionMatrix().getRowValue(Functions.RES_LENGTHDISTTYPE) : null;
        String sweepWidthMethod = (String) input.get(Functions.PM_SWEPTAREADENSITY_SWEEPWIDTHMETHOD);
        if (sweepWidthMethod == null) {
            return null;
        }
        boolean sweepWidthGivenInLengthDist = false;
        boolean distanceGivenInLengthDist = false;
        switch (sweptAreaMethod) {
            case Functions.SWEPTAREAMETHOD_LENGTHDEPENDENT:
                if (lengthDist == null) {
                    logger.error("Length distribution not set", null);
                    return null;
                }
                if (lenDistType == null) {
                    logger.error("Length distribution type not set", null);
                    return null;
                }
                switch (sweepWidthMethod) {
                    case Functions.SWEEPWIDTH_LENGTHDEPENDENT:
                        logger.log("SweepWidthMethod LengthDependent is deprecated, use Catchability function instead with Length dependent sweep width.");
                        break;
                }

                switch (lenDistType) {
                    case Functions.LENGTHDISTTYPE_SWEEPWLENGHTDIST:
                    case Functions.LENGTHDISTTYPE_SWEEPWNORMLENGHTDIST:
                        sweepWidthGivenInLengthDist = true;
                }
                switch (lenDistType) {
                    case Functions.LENGTHDISTTYPE_NORMLENGHTDIST:
                    case Functions.LENGTHDISTTYPE_SWEEPWNORMLENGHTDIST:
                        distanceGivenInLengthDist = true;
                }
                // Ensure that sweep width method corresponds to the Length Dist type
                boolean preDeterminedSweepWidthMethod = sweepWidthMethod.equals(Functions.SWEEPWIDTH_PREDETERMINED);
                if (preDeterminedSweepWidthMethod ^ sweepWidthGivenInLengthDist) {
                    logger.error("Sweep width " + sweepWidthMethod + " requires that the length distribution is " + (preDeterminedSweepWidthMethod ? "" : "not") + " standardized to sweep width", null);
                }
                break;
            case Functions.SWEPTAREAMETHOD_TOTALCATCH:
                switch (sweepWidthMethod) {
                    case Functions.SWEEPWIDTH_LENGTHDEPENDENT:
                    case Functions.SWEEPWIDTH_PREDETERMINED:
                        logger.error("Total catch sweep width cannot be length dependent", null);
                }
                if (catchVariable == null) {
                    logger.error("Catch variable not set", null);
                }
                break;
        }
        Double sweepWidthInM = (Double) input.get(Functions.PM_SWEPTAREADENSITY_SWEEPWIDTH);
        Double alpha = (Double) input.get(Functions.PM_SWEPTAREADENSITY_ALPHA);
        Double beta = (Double) input.get(Functions.PM_SWEPTAREADENSITY_BETA);
        Double lmin = (Double) input.get(Functions.PM_SWEPTAREADENSITY_LMIN);
        Double lmax = (Double) input.get(Functions.PM_SWEPTAREADENSITY_LMAX);
        String sweepWidthExpr = (String) input.get(Functions.PM_SWEPTAREADENSITY_SWEEPWIDTHEXPR);
        Map<String, Double> sweepWidthMap = new HashMap<>();
        switch (sweepWidthMethod) {
            case Functions.SWEEPWIDTH_PREDETERMINED:
                // Sweep width is included in LFQ
                sweepWidthInM = 1852d;
                break;
            case Functions.SWEEPWIDTH_CONSTANT:
                if (sweepWidthInM == null || sweepWidthInM == 0) {
                    logger.error("Fishing width not given", null);
                }
                break;
            case Functions.SWEEPWIDTH_LENGTHDEPENDENT:
                if (!sweptAreaMethod.equals(Functions.SWEPTAREAMETHOD_LENGTHDEPENDENT)) {
                    logger.error("Length dependent fishing width only possible with length dependent swept area method", null);
                }
                sweepWidthInM = null;
                if (alpha == null || alpha == 0 || beta == null || beta == 0 || lmin == null || lmin == 0 || lmax == null || lmax == 0) {
                    logger.error("Alpha, Beta, LMin and LMax must be spesified", null);
                }
                logger.log("Length dependent fishing width is deprecated, but possible because of backcompability. Use Catchability with adjusted lengthdist instead");
                break;
            case Functions.SWEEPWIDTH_CRUISEDEPENDENT:
                if (sweepWidthExpr == null) {
                    logger.error("Cruise dependent fishing width requires sweepWidthExpr set", null);
                } else {
                    String[] pairs = sweepWidthExpr.split(";");
                    for (String pair : pairs) {
                        String[] elms = pair.split(":");
                        sweepWidthMap.put(elms[0].trim(), Conversion.safeStringtoDouble(elms[1].trim()));
                    }
                }
        }

        String sampleUnitType = (String) AbndEstProcessDataUtil.getAssignmentResolutions(pd).getRowValue(Functions.RES_SAMPLEUNITTYPE);//(String) nascMatrix.getResolutionMatrix().getRowValue(Functions.RES_SAMPLEUNITTYPE);//input.get(Functions.PM_DENSITY_SAMPLEUNITTYPE);
        //String layerType = (String) AbndEstProcessDataUtil.getAssignmentResolutions(pd).getRowValue(Functions.RES_LAYERTYPE);
        if (sampleUnitType == null/* || layerType == null*/) {
            return null;
        }
        DensityMatrix result = new DensityMatrix();

        // Inherit resolution set in definePSUAndAssignmentsByBioticData:
        //result.getResolutionMatrix().setRowValue(Functions.RES_LAYERTYPE, layerType);
        result.getResolutionMatrix().setRowValue(Functions.RES_SAMPLEUNITTYPE, sampleUnitType);

        Double lengthInterval = null;
        if (lengthDist != null) {
            lengthInterval = lengthDist.getResolutionMatrix().getRowValueAsDouble(Functions.RES_LENGTHINTERVAL);
            result.getResolutionMatrix().setRowValue(Functions.RES_LENGTHINTERVAL, lengthInterval);
        }
        // Samplesize 
        MatrixBO lDist = lengthDist != null ? lengthDist.getData() : null;
        // For each sample unit in NASC.
        MatrixBO suAssignments = AbndEstProcessDataUtil.getSUAssignments(pd);
        MatrixBO psuStrata = AbndEstProcessDataUtil.getPSUStrata(pd);
        MatrixBO trawlAsg = AbndEstProcessDataUtil.getBioticAssignments(pd);

        for (String psu : psuStrata.getRowKeys()) {
            List<String> estLayers = suAssignments.getRowColKeys(psu);
            if (estLayers.size() != 1) {
                continue; // Assignments and layers not properly defined
            }
            String estLayer = estLayers.get(0); // Get single layer for swept area. Multilayer not supported.
            Collection<String> edsus = AbndEstProcessDataUtil.getEDSUsByPSU(pd, psu);
            Integer numEDSUs = edsus.size();
            if (edsus.isEmpty()) {
                continue;
            }
            // Get assignment for the psu
            String assignment = AbndEstProcessDataUtil.getSUAssignment(pd, psu, estLayer);
            if (assignment == null) {
                continue;
            }
            // Iterate biotic stations in the assignment
            Double sampleSize = 0d;
            Collection<String> stations = AbndEstProcessDataUtil.getStationsByAssignment(pd, assignment);
            MatrixBO posSampleSize = new MatrixBO();
            for (String station : stations) {
                FishstationBO fs = BioticUtils.findStation(bioticData, station);
                if (fs == null) {
                    logger.error("Station " + station + " not available in Biotic Data.", null);
                    return null;
                }
                sweepWidthInM = getSweepWidthByStation(station, sweepWidthInM, sweepWidthMethod, sweepWidthMap);
                Double sweptDistance = 1.0;
                if (!distanceGivenInLengthDist) {
                    sweptDistance = fs.getDistance();
                    if (sweptDistance == null || sweptDistance == 0) {
                        logger.error("Missing distance in station " + station + ".", null);
                    }
                }
                if (distanceMethod != null && distanceMethod.equals(Functions.DISTANCEMETHOD_BYDEPTH)) {
                    if (fs.getFishingDepthCount() != null && fs.getFishingDepthCount() > 1) {
                        // Standardize the swept distance to one depth unit
                        sweptDistance = sweptDistance / fs.getFishingDepthCount();
                    }
                }
                Double stationWeight = 0d;
                switch (sweptAreaMethod) {
                    // calculate station densities from assignment length distribution
                    case Functions.SWEPTAREAMETHOD_LENGTHDEPENDENT: {
                        stationWeight = numEDSUs.doubleValue();
                        if (lDist == null) {
                            return null;
                        }
                        //String station = stations.size() >= 1 ? stations.iterator().next() : null;
                        for (String specCat : lDist.getKeys()) {
                            MatrixBO row = (MatrixBO) lDist.getGroupRowValue(specCat, assignment);
                            if (row == null) {
                                // No LFQ collected for this station (edsu) and species.
                                continue;
                            }
                            MatrixBO cell = row.getDefaultValueAsMatrix();
                            for (String lenGrp : cell.getKeys()) {
                                Double lGroup = Conversion.safeStringtoDoubleNULL(lenGrp);
                                Double length = StoXMath.getLength(lGroup, lengthInterval);
                                Double variable = cell.getValueAsDouble(lenGrp);
                                if (variable == null) {
                                    continue;
                                }
                                if (sweepWidthMethod.equals(Functions.SWEEPWIDTH_LENGTHDEPENDENT)) {
                                    Double l = length < lmin ? lmin : length > lmax ? lmax : length;
                                    // Implement Formula according to IMR/PINRO Joint Report 2014(2)
                                    //        alpha beta lmin lmax
                                    // Cod     5.91 0.43 15 cm 62 cm
                                    // Haddock 2.08 0.75 15 cm 48 cm
                                    sweepWidthInM = alpha * Math.pow(l, beta);
                                }
                                Double sweptArea = StoXMath.getSweptArea(sweptDistance, sweepWidthInM);

                                Double density = getDensity(variable, sweptArea, numEDSUs);
                                if (density == null) {
                                    logger.error("Swept area not calculated for " + psu, null);
                                }
                                // psu = observation of station (samplesize=1)
                                if (density > 0d) {
                                    posSampleSize.setRowColValue(specCat, station, stationWeight);
                                }
                                result.getData().addGroupRowColCellValue(specCat, psu, estLayer, lenGrp, density);
                            }
                        }
                        break;
                    }
                    case Functions.SWEPTAREAMETHOD_TOTALCATCH: {
                        // accumulate station densities into psu density
                        stationWeight = trawlAsg.getRowColValueAsDouble(assignment, station);
                        // The sample size is the product of station weight and the number of edsu per psu.
                        if (catchVariable == null) {
                            return null;
                        }
                        if (bioticData == null) {
                            logger.error("Biotic Data is not set in parameter.", null);
                            return null;
                        }
                        if (fs == null) {
                            logger.error("Station " + station + " not available in Biotic Data.", null);
                            return null;
                        }
                        for (CatchBO c : fs.getCatchBOCollection()) {
                            String specCat = c.getSpeciesCatTableKey(); // Using taxa as group
                            for (SampleBO s : c.getSampleBOCollection()) {
                                Double variable = null;
                                switch (catchVariable) {
                                    case Functions.CATCHVARIABLE_WEIGHT:
                                        variable = s.getWeight();
                                        if(variable == null){
                                            logger.error("Missing weight at " + s.getKey() + " for psu " + psu, null);
                                        }
                                        break;
                                    case Functions.CATCHVARIABLE_COUNT:
                                        variable = Conversion.safeIntegerToDouble(s.getCount());
                                        break;
                                }
                                switch (catchVariable) {
                                    case Functions.CATCHVARIABLE_WEIGHT:
                                    case Functions.CATCHVARIABLE_COUNT:
                                        if(variable == null){
                                            logger.error("Missing " + catchVariable + " at " + s.getKey() + " for psu " + psu, null);
                                        }
                                }
                                Double sweptArea = StoXMath.getSweptArea(sweptDistance, sweepWidthInM);
                                Double density = getDensity(variable, sweptArea, numEDSUs);
                                if (density == null) {
                                    logger.error("Swept area not calculated for " + psu, null);
                                }
                                if (density > 0d) {
                                    posSampleSize.setRowColValue(specCat, station, stationWeight);
                                }
                                result.getData().addGroupRowColCellValue(specCat, psu, estLayer, null, density);
                            }
                        }
                        break;
                    }
                }
                sampleSize += stationWeight;
            }
            // Set sample size
            result.getSampleSizeMatrix().setRowValue(psu, sampleSize);
            // Set pos sample size
            for (String specCat : posSampleSize.getRowKeys()) {
                result.getPosSampleSizeMatrix().setGroupRowColValue(specCat, psu, estLayer, posSampleSize.getRowValueAsMatrix(specCat).getSum());
            }

        }
        return result;
    }

    /**
     * Return cruise dependent fishing width
     *
     * @param fs
     * @param sweepWidth
     * @param sweepWidthMethod
     * @param sweepWidthMap
     * @return
     */
    private Double getSweepWidthByStation(String station, Double sweepWidth, String sweepWidthMethod, Map<String, Double> sweepWidthMap) {
        switch (sweepWidthMethod) {
            case Functions.SWEEPWIDTH_CRUISEDEPENDENT: {
                String[] tokens = station.split("/");
                String cruise = tokens[0];
                return sweepWidthMap.get(cruise);
            }
        }
        return sweepWidth;
    }

    private Double getDensity(Double n, Double sweptArea, Integer numEDSUs) {
        if (n == null || sweptArea == null || sweptArea <= 0d || numEDSUs == null || numEDSUs <= 0d) {
            return null;
        }
        return n / sweptArea / numEDSUs;

    }
}
