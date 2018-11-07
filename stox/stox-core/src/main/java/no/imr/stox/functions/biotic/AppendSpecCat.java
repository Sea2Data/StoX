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
import no.imr.sea2data.biotic.bo.MissionBO;
import no.imr.stox.functions.utils.BioticUtils;

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
        List<MissionBO> mList = (List<MissionBO>) input.get(Functions.PM_APPENDSPECCAT_BIOTICDATA);
        List<MissionBO> missions = BioticUtils.copyBioticData(mList);
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
        missions.forEach((ms) -> {
            ms.getFishstationBOs().forEach((fs) -> {
                fs.getCatchSampleBOs().forEach((cs) -> {
                    String spec = null;
                    if (m.isEmpty()) {
                        spec = specCat;
                    } else {
                        if (cs.bo().getCommonname() != null) {
                            String str = m.get(cs.bo().getCommonname().toLowerCase());
                            if (str != null) {
                                spec = str;
                            }
                        }
                    }
                    if (spec != null) {
                        cs.setSpecCat(spec); // Set spec cat to all catches
                    }
                });
            });
        });
        return missions;
    }

}
