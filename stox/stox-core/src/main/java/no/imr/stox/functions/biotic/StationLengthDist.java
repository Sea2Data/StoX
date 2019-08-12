package no.imr.stox.functions.biotic;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import no.imr.stox.util.matrix.MatrixBO;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.AbstractFunction;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.biotic.bo.CatchSampleBO;
import no.imr.sea2data.biotic.bo.MissionBO;
import no.imr.stox.util.math.Calc;
import no.imr.stox.util.math.ImrMath;
import no.imr.stox.util.base.Conversion;
import no.imr.stox.bo.BioticData;
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
        BioticData missions = (BioticData) input.get(Functions.PM_STATIONLENGTHDIST_BIOTICDATA);
        LengthDistMatrix result = new LengthDistMatrix();
        // Set the resolution matrix as Observation type and length interval
        String lengthDistType = (String) input.get(Functions.PM_STATIONLENGTHDIST_LENGTHDISTTYPE);
        Double lengthInterval = BioticUtils.getLengthInterval(missions);
        result.getResolutionMatrix().setRowValue(Functions.RES_OBSERVATIONTYPE, Functions.OBSERVATIONTYPE_STATION);
        result.getResolutionMatrix().setRowValue(Functions.RES_LENGTHINTERVAL, lengthInterval);
        result.getResolutionMatrix().setRowValue(Functions.RES_LENGTHDISTTYPE, lengthDistType);
        Boolean normToDist = lengthDistType.equals(Functions.LENGTHDISTTYPE_NORMLENGHTDIST);
        Boolean inPercent = lengthDistType.equals(Functions.LENGTHDISTTYPE_PERCENTLENGHTDIST);
        Double firstLenGrp = Double.MAX_VALUE, lastLenGrp = -Double.MAX_VALUE;
        for (MissionBO ms : missions.getMissions()) {
            for (FishstationBO fs : ms.getFishstationBOs()) {
                // Standardize LFQ to number of trawl depths.
                Double distanceWFac = 1.0;
                // Standardize to 1 NM if NORMLengthDist
                if (normToDist) {
                    distanceWFac = StoXMath.raiseFac(distanceWFac, fs.bo().getDistance());
                }
                String observation = fs.getKey(); // Using fishstation key as row
                Map<String, List<CatchSampleBO>> groups = fs.getCatchSampleBOs().stream()
                        .filter(s -> s.getSpecCat() != null)
                        .collect(Collectors.groupingBy(CatchSampleBO::getSpecCat));
                for (Map.Entry<String, List<CatchSampleBO>> e : groups.entrySet()) {
                    List<CatchSampleBO> csList = e.getValue();
                    Boolean oneSample = csList.size() == 1;
                    for (CatchSampleBO s : csList) {
                        String speciesCat = s.getSpecCat(); // Using taxa as group
                        if (s.getIndividualBOs().isEmpty() && (s.bo().getLengthsampleweight() != null && s.bo().getLengthsampleweight() > 0d || s.bo().getLengthsamplecount() != null && s.bo().getLengthsamplecount() > 0d)) {
                            logger.log("Warning: Length distr. not calculated because of missing length sample individuals in " + s.getKey());
                            continue;
                        }
                        // Standardize sample to total catch
                        Double sampleWFac = StoXMath.raiseFac(s.bo().getCatchweight(), s.bo().getLengthsampleweight());
                        if (sampleWFac == null) {
                            sampleWFac = ImrMath.safeDivide(s.bo().getCatchcount(), s.bo().getLengthsamplecount());
                            if (sampleWFac == null) {
                                if (inPercent && oneSample) {
                                    // If in percent and only one sample (as in pgnapes), the raising factor is not needed.
                                    sampleWFac = 1.0; // not needed
                                } else {
                                    logger.log("Warning: Length distr. not calculated because of missing raising factor catchweight/lengthsampleweight or catchcount/lengthsamplecount in " + s.getKey());
                                    continue;
                                }
                            }
                        }
                        for (IndividualBO i : s.getIndividualBOs()) {
                            Double lengthInCM = i.getLengthCentimeter();
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
            }
        }
        // Zero out missing length groups
        if (firstLenGrp != Double.MAX_VALUE) {
            Integer numLenGroups = (int) ((lastLenGrp - firstLenGrp) / lengthInterval) + 1;
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
