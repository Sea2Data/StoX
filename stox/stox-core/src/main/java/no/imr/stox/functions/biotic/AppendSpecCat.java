package no.imr.stox.functions.biotic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.AbstractFunction;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.biotic.bo.CatchSampleBO;

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
        Map<String, String> m = new HashMap<>();
        if (specCat != null) {
            String[] lines = specCat.split("/");
            Arrays.stream(lines)
                    .forEach(l -> {
                        String[] groups = l.split(":");
                        if (groups.length == 2) {
                            String[] elms = groups[1].split(",");
                            Arrays.stream(elms)
                                    .forEach(e -> {
                                        m.put(e.toLowerCase(), groups[0]);
                                    });
                        }
                    });
        }
        for (FishstationBO f : fList) {
            FishstationBO fn = new FishstationBO(f);
            fishstations.add(fn);
            for (CatchSampleBO c : f.getCatchSampleBOs()) {
                CatchSampleBO cn = new CatchSampleBO(fn, c);
                fn.getCatchSampleBOs().add(cn);

                String spec = null;
                if (m.isEmpty()) {
                    spec = specCat;
                } else {
                    if (c.getCs().getCommonname() != null) {
                        String str = m.get(c.getCs().getCommonname().toLowerCase());
                        if (str != null) {
                            spec = str;
                        }
                    }
                }
                if (spec != null) {
                    cn.setSpecCat(spec); // Set spec cat to all catches
                }
                for (IndividualBO i : c.getIndividualBOs()) {
                    c.getIndividualBOs().add(new IndividualBO(c, i));
                }
            }
        }
        return fishstations;
    }

}
