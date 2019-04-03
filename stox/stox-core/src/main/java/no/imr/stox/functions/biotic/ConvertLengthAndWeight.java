package no.imr.stox.functions.biotic;

import java.util.List;
import java.util.Map;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.AbstractFunction;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.biotic.bo.CatchSampleBO;
import no.imr.sea2data.biotic.bo.MissionBO;
import no.imr.sea2data.imrbase.math.ImrMath;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.stox.bo.BioticData;
import no.imr.stox.functions.utils.BioticUtils;
import no.imr.stox.log.ILogger;

/**
 * This class is used to filter data with special attributes among all biotic
 * data. It uses Java Expression Language (JEXL) to do the filtering.
 *
 * @author atlet
 * @author esmaelmh
 */
public class ConvertLengthAndWeight extends AbstractFunction {

    /**
     * Used when checking if expression is to be used on separate levels like
     * station, catch and sample.
     */
    private static final String EXPR_TRUE = "true";

    /**
     * This is the method that performs the filtering task. TODO Fix cyclic
     * complexity
     *
     * @param input
     * @return
     * @throws no.imr.stox.exception.UserErrorException
     */
    @Override
    public Object perform(Map<String, Object> input) {
        BioticData bioticData = (BioticData) input.get(Functions.PM_CONVERTLENGTHANDWEIGHT_BIOTICDATA);
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        if (bioticData != null && !(bioticData.isLengthCMAdded() || bioticData.isIndividualWeightGAdded())) {
            logger.error("LengthCM not defined. Add DefineIndMeasurement to model.", null);
        }
        // product type 3 = Gutted without head
        // product type 4 = Gutted with head
        Double hCutFacA = (Double) input.get(Functions.PM_CONVERTLENGTHANDWEIGHT_HEADCUTFACA); // product type 3
        Double hCutFacB = (Double) input.get(Functions.PM_CONVERTLENGTHANDWEIGHT_HEADCUTFACB); // product type 3
        Double wGutHeadOff = (Double) input.get(Functions.PM_CONVERTLENGTHANDWEIGHT_WGUTHEADOFF); // product type 3,4
        Double wGutHeadOn = (Double) input.get(Functions.PM_CONVERTLENGTHANDWEIGHT_WGUTHEADON); // product type 3,4
        // if 
        for (MissionBO ms : bioticData.getMissions()) {
            for (FishstationBO f : ms.getFishstationBOs()) {
                for (CatchSampleBO c : f.getCatchSampleBOs()) {
                    for (IndividualBO i : c.getIndividualBOs()) {
                        if (i.bo().getIndividualproducttype() == null) {
                            continue;
                        }
                        Double w = null;
                        Double l = null;
                        switch (i.bo().getIndividualproducttype()) {
                            case "3":
                                // Gutted without head - correct length
                                Double le = i.getLengthCM();
                                Integer lu = Conversion.safeStringtoIntegerNULL(i.bo().getLengthresolution());
                                if (le != null && lu != null && hCutFacA != null && hCutFacB != null) {
                                    Double lucm = BioticUtils.getLengthInterval(lu);
                                    l = hCutFacA * (le + 0.5 * lucm) + hCutFacB;
                                }
                            // Drop - to correct weight
                            case "4":
                                // Gutted with head - correct weight
                                w = i.getIndividualWeightG();
                                Double wFac = i.bo().getIndividualproducttype().equals("3") ? wGutHeadOff : wGutHeadOn;
                                if (w != null && wFac != null) {
                                    w = w * wFac;
                                }
                        }
                        if (w != null) {
                            i.setIndividualWeightG(w); // Set weight in g
                        }
                        if (l != null) {
                            i.setLengthCM(l); // set length in cm
                        }
                        if (w != null || l != null) {
                            i.bo().setIndividualproducttype(1 + "");
                        }
                    }
                }
            }
        }
        return bioticData;
    }

}
