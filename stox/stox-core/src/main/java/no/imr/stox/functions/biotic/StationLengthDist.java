package no.imr.stox.functions.biotic;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.AbstractFunction;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.biotic.bo.CatchSampleBO;
import no.imr.sea2data.imrbase.math.Calc;
import no.imr.sea2data.imrbase.math.ImrMath;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.stox.bo.LengthDistMatrix;
import no.imr.stox.functions.utils.BioticUtils;
import no.imr.stox.functions.utils.StoXMath;
import no.imr.stox.log.ILogger;

/**
 * Station length distribution
 *
 * @author aasmunds
 */
public class StationLengthDist extends AbstractFunction {

    /**
     * @param input contains matrix FISHSTATIONS, lengthinterval, inpercent and
     * normtodist, logger
     * @note normtodist: # fish as if you trawl 1 nm and take lmeas. of whole
     * catch. inpercent: sum of % for all length groups is 100%
     * @return Matrix object of type STATIONLENGTHDIST_MATRIX - see
     * DataTypeDescription.txt
     */
    @Override
    public Object perform(Map<String, Object> input) {
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        List<FishstationBO> fishStations = (List<FishstationBO>) input.get(Functions.PM_STATIONLENGTHDIST_BIOTICDATA);
        LengthDistMatrix result = new LengthDistMatrix();
        // Set the resolution matrix as Observation type and length interval
        String lengthDistType = (String) input.get(Functions.PM_STATIONLENGTHDIST_LENGTHDISTTYPE);
        Double lengthInterval = BioticUtils.getLengthInterval(fishStations);
        result.getResolutionMatrix().setRowValue(Functions.RES_OBSERVATIONTYPE, Functions.OBSERVATIONTYPE_STATION);
        result.getResolutionMatrix().setRowValue(Functions.RES_LENGTHINTERVAL, lengthInterval);
        result.getResolutionMatrix().setRowValue(Functions.RES_LENGTHDISTTYPE, lengthDistType);
        Boolean normToDist = lengthDistType.equals(Functions.LENGTHDISTTYPE_NORMLENGHTDIST);
        Boolean inPercent = lengthDistType.equals(Functions.LENGTHDISTTYPE_PERCENTLENGHTDIST);
        Double firstLenGrp = Double.MAX_VALUE, lastLenGrp = -Double.MAX_VALUE;
        for (FishstationBO fs : fishStations) {
            // Standardize LFQ to number of trawl depths.
            Double distanceWFac = 1.0;
            // Standardize to 1 NM if NORMLengthDist
            if (normToDist) {
                distanceWFac = StoXMath.raiseFac(distanceWFac, fs.getDistance());
            }
            String observation = fs.getKey(); // Using fishstation key as row
            for (CatchSampleBO s : fs.getCatchSampleBOs()) {
                String speciesCat = s.getSpeciesCatTableKey(); // Using taxa as group
                // Standardize sample to total catch
                Double sampleWFac = StoXMath.raiseFac(s.getCatchweight(), s.getlengthsampleweight());
                if (s.getIndividualBOs().isEmpty() && (s.getlengthsampleweight() != null && s.getlengthsampleweight() > 0d || s.getLengthsamplecount() != null && s.getLengthsamplecount() > 0d)) {
                    logger.log("Warning: Length distr. not calculated because of missing length sample individuals in " + s.getKey());
                    continue;
                }
                if (sampleWFac == null) {
                    if (inPercent) {
                        // Standardize sample to total catch is not needed, the percent (shape) of the LFQ is given.
                        sampleWFac = 1.0; // not needed
                    } else {
                        sampleWFac = ImrMath.safeDivide(s.getCatchcount(), s.getLengthsamplecount());
                        if (sampleWFac == null) {
                            logger.log("Warning: Length distr. not calculated because of missing weight or sample weight in " + s.getKey());
                            continue;
                        }
                    }
                }
                for (IndividualBO i : s.getIndividualBOs()) {
                    Double lengthInCM = i.getLengthCM();
                    String lenGrp = BioticUtils.getLenGrp(lengthInCM, lengthInterval);
                    Double lengthGroupInCM = ImrMath.trunc(lengthInCM, lengthInterval);
                    if (lengthGroupInCM != null) {
                        firstLenGrp = Math.min(firstLenGrp, lengthGroupInCM);
                        lastLenGrp = Math.max(lastLenGrp, lengthGroupInCM);
                        Double v = StoXMath.combineWFac(sampleWFac, distanceWFac);
                        result.getData().addGroupRowCellValue(speciesCat, observation, lenGrp, v);
                    }
                }
            }
        }
        // Zero out missing length groups
        if (firstLenGrp != Double.MAX_VALUE) {
            Integer numLenGroups = (int) ((lastLenGrp - firstLenGrp) / lengthInterval);
            for (String specCatKey : result.getData().getKeys()) {
                MatrixBO specCat = result.getData().getValueAsMatrix(specCatKey);
                for (String obsKey : specCat.getKeys()) {
                    MatrixBO obs = specCat.getValueAsMatrix(obsKey);
                    MatrixBO lfq = obs.getDefaultValueAsMatrix();
                    for (int i = 0; i < numLenGroups - 1; i++) {
                        Double lGrp = Calc.roundTo(firstLenGrp + i * lengthInterval, 8);
                        String lenGrp = BioticUtils.getLenGrp(lGrp, lengthInterval);
                        // Register the length group as intermediate, better for reporting.
                        if (lfq.getValue(lenGrp) == null) {
                            lfq.setValue(lenGrp, null);
                        }
                    }
                }
            }
        }
        if (inPercent) {
            BioticUtils.toPercent(result);
        }
        return result;
    }
}
