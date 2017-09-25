package no.imr.stox.functions.biotic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.AbstractFunction;
import no.imr.sea2data.biotic.bo.CatchBO;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.biotic.bo.SampleBO;

/**
 * This class is used to filter data with special attributes among all biotic
 * data. It uses Java Expression Language (JEXL) to do the filtering.
 *
 * @author atlet
 * @author esmaelmh
 */
public class AppendSpecCat extends AbstractFunction {

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
        List<FishstationBO> fList = (List<FishstationBO>) input.get(Functions.PM_APPENDSPECCAT_BIOTICDATA);
        List<FishstationBO> fishstations = new ArrayList<>();//FilterUtils.copyBOList((List) allFishstations, null);
        String specCat = (String) input.get(Functions.PM_APPENDSPECCAT_SPECCAT);
        for (FishstationBO f : fList) {
            FishstationBO fn = new FishstationBO(f);
            fishstations.add(fn);
            for (CatchBO c : f.getCatchBOCollection()) {
                CatchBO cn = new CatchBO(fn, c);
                fn.getCatchBOCollection().add(cn);
                cn.setSpecCat(specCat); // Set spec cat to all catches
                for (SampleBO s : c.getSampleBOCollection()) {
                    SampleBO sn = new SampleBO(cn, s);
                    cn.getSampleBOCollection().add(sn);
                    for (IndividualBO i : s.getIndividualBOCollection()) {
                        sn.getIndividualBOCollection().add(new IndividualBO(sn, i));
                    }
                }
            }
        }
        return fishstations;
    }

}
