/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.biotic;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.biotic.bo.MissionBO;
import no.imr.sea2data.imrbase.math.LWRelationship;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.stox.bo.BioticData;
import no.imr.stox.bo.LengthWeightRelationshipMatrix;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.BioticUtils;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.StoXMath;
import no.imr.stox.log.ILogger;

/**
 *
 * @author aasmunds
 */
public class LengthWeightRelationship extends AbstractFunction {

    /**
     * @param input contains matrix STATIONLENGTHDIST, lengthinterval and logger
     * @return Matrix object of type STATIONLENGTHDIST_MATRIX - see
     * DataTypeDescription.txt
     */
    @Override
    public Object perform(Map<String, Object> input) {
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_LENGTHWEIGHTRELATIONSHIP_PROCESSDATA);
        BioticData bioticData = (BioticData) input.get(Functions.PM_LENGTHWEIGHTRELATIONSHIP_BIOTICDATA);
        if (bioticData != null && !(bioticData.isLengthCMAdded() || bioticData.isIndividualWeightGAdded())) {
            logger.error("LengthCM and IndividualWeight is not defined. Add DefineIndMeasurement to model.", null);
        }
        //logger.error("Length interval " + lenInterval + " must be a multiple integer factor of " + prevLenInterval + ".", null);

        LengthWeightRelationshipMatrix result = new LengthWeightRelationshipMatrix();
        List<String> layers = AbndEstProcessDataUtil.getSUAssignments(pd).getColKeys();

        Set<String> totalStations = new HashSet<>();
        for (String stratum : AbndEstProcessDataUtil.getStrata(pd)) {
            Collection<String> psus = AbndEstProcessDataUtil.getPSUsByStratum(pd, stratum);
            Set<String> stratumStations = new HashSet<>();
            for (String psu : psus) {
                for (String layer : layers) {
                    String asgId = AbndEstProcessDataUtil.getSUAssignment(pd, psu, layer);
                    List<String> stationIds = AbndEstProcessDataUtil.getBioticAssignments(pd).getRowColKeys(asgId);
                    stratumStations.addAll(stationIds);
                }
            }
            totalStations.addAll(stratumStations);
            Map<String, List<IndividualBO>> inds = getIndividuals(stratumStations, bioticData);
            inds.entrySet().forEach(es -> {
                appendCoefficients(es.getKey(), stratum, es.getValue(), result);
            });
        }
        Map<String, List<IndividualBO>> inds = getIndividuals(totalStations, bioticData);
        inds.entrySet().forEach(es -> {
            appendCoefficients(es.getKey(), "TOTAL", es.getValue(), result);
        });
        return result;
    }

    private void appendCoefficients(String aphia, String stratum, List<IndividualBO> indList, LengthWeightRelationshipMatrix result) {
        if (indList.size() > 2) {
            Double[] lenInCM = new Double[indList.size()];
            Double[] wInGrams = new Double[indList.size()];
            for (int i = 0; i < indList.size(); i++) {
                IndividualBO row = indList.get(i);
                Double lengthInterval = BioticUtils.getLengthInterval(Conversion.safeStringtoIntegerNULL(row.bo().getLengthresolution()));
                Double length = StoXMath.getLength(row.getLengthCentimeter(), lengthInterval);
                lenInCM[i] = length;
                wInGrams[i] = row.getIndividualWeightGram();
            }
            LWRelationship lwr = LWRelationship.getLWRelationship(lenInCM, wInGrams);
            //lwr.get
            result.getData().setGroupRowColValue(aphia, stratum, Functions.LENGTHWEIGHT_COEFF_A, lwr.getA());
            result.getData().setGroupRowColValue(aphia, stratum, Functions.LENGTHWEIGHT_COEFF_B, lwr.getB());
            result.getData().setGroupRowColValue(aphia, stratum, Functions.LENGTHWEIGHT_COEFF_R2, lwr.getR2());
        }
    }

    private Map<String, List<IndividualBO>> getIndividuals(Set<String> stratumStations, BioticData bioticData) {
        return stratumStations.parallelStream()
                .map(s -> BioticUtils.findStation(bioticData, s))
                .filter(b -> b != null)
                .flatMap(b -> b.getCatchSampleBOs().stream())
                .filter(c -> c.bo().getAphia() != null)
                .flatMap(b -> b.getIndividualBOs().stream())
                .filter(i -> i.getIndividualWeightGram() != null && i.getIndividualWeightGram() > 0d && i.getLengthCentimeter() != null && i.getLengthCentimeter() > 0d)
                .collect(Collectors.groupingBy(i -> i.getCatchSample().bo().getAphia(), Collectors.toList()));

    }
}
