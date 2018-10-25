package no.imr.stox.functions.biotic;

import java.util.List;
import java.util.Map;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.AbstractFunction;
import no.imr.sea2data.biotic.bo.CatchBO;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.biotic.bo.SampleBO;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.stox.functions.utils.BioticUtils;

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
        List<FishstationBO> bioticData = (List<FishstationBO>) input.get(Functions.PM_CONVERTLENGTHANDWEIGHT_BIOTICDATA);
        // product type 3 = Gutted without head
        // product type 4 = Gutted with head
        Double hCutFacA = (Double) input.get(Functions.PM_CONVERTLENGTHANDWEIGHT_HEADCUTFACA); // product type 3
        Double hCutFacB = (Double) input.get(Functions.PM_CONVERTLENGTHANDWEIGHT_HEADCUTFACB); // product type 3
        Double wGutHeadOff = (Double) input.get(Functions.PM_CONVERTLENGTHANDWEIGHT_WGUTHEADOFF); // product type 3,4
        Double wGutHeadOn = (Double) input.get(Functions.PM_CONVERTLENGTHANDWEIGHT_WGUTHEADON); // product type 3,4
        // if 
        for (FishstationBO f : bioticData) {
            for (CatchBO c : f.getCatchBOs()) {
                for (SampleBO s : c.getSampleBOs()) {
                    for (IndividualBO i : s.getIndividualBOs()) {
                        if (i.getIndividualproducttype()== null) {
                            continue;
                        }
                        Double w = null;
                        Double l = null;
                        switch (i.getIndividualproducttype()) {
                            case "3":
                                // Gutted without head - correct length
                                Double le = i.getLength();
                                Integer lu = Conversion.safeStringtoIntegerNULL(i.getLengthresolution());
                                if (le != null && lu != null && hCutFacA != null && hCutFacB != null) {
                                    Double lucm = BioticUtils.getLengthInterval(lu);
                                    l = hCutFacA * (le + 0.5 * lucm) + hCutFacB;
                                }
                            // Drop - to correct weight
                            case "4":
                                // Gutted with head - correct weight
                                w = i.getWeight();
                                Double wFac = i.getIndividualproducttype().equals("3") ? wGutHeadOff : wGutHeadOn;
                                if (w != null && wFac != null) {
                                    w = w * wFac;
                                }
                        }
                        if (w != null) {
                            i.setIndividualweight(w);
                        }
                        if (l != null) {
                            i.setIndividualweight(l);
                        }
                        if (w != null || l != null) {
                            i.setIndividualproducttype(1 + "");
                        }
                    }
                }
            }
        }
        return bioticData;
    }

}
