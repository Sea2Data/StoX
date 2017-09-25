package no.imr.stox.functions.acoustic;

import java.util.ArrayList;
import no.imr.stox.functions.utils.FilterUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.AbstractFunction;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.echosounderbo.FrequencyBO;
import no.imr.sea2data.echosounderbo.SABO;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;

/**
 * This class is used to filter data with special attributes in an echosounder
 * dataset. It uses Java Expression Language (JEXL) to do the filtering.
 *
 * @author esmaelmh
 */
public class FilterAcoustic extends AbstractFunction {

    @Override
    public Object perform(Map<String, Object> input) {
        String distanceExpr = (String) input.get(Functions.PM_FILTERACOUSTIC_DISTANCEEXPR);
        String nascExpr = (String) input.get(Functions.PM_FILTERACOUSTIC_NASCEXPR);
        String frequenciesExpr = (String) input.get(Functions.PM_FILTERACOUSTIC_FREQEXPR);
        if (distanceExpr == null || "".equals(distanceExpr)) {
            distanceExpr = "true"; // no distance is to be excluded
        }
        if (nascExpr == null || "".equals(nascExpr)) {
            nascExpr = "true";  // include all species
        }
        if (frequenciesExpr == null || "".equals(frequenciesExpr)) {
            frequenciesExpr = "true";   // include all frequencies
        }
        JexlEngine engine = new JexlEngine();
        engine.setLenient(false);
        engine.setSilent(false);
        Expression distE = engine.createExpression(distanceExpr);
        Expression freqE = engine.createExpression(frequenciesExpr);
        Expression nascE = engine.createExpression(nascExpr);
        JexlContext ctx = new MapContext();
        if(Functions.XMLDATA) {
            
            return null;
        }
        List<DistanceBO> allDistances = (List<DistanceBO>) input.get(Functions.PM_FILTERACOUSTIC_ACOUSTICDATA);
        List<DistanceBO> distances = new ArrayList<>();//FilterUtils.copyBOList((List)allDistances, null);
        for (DistanceBO ds : allDistances) {
            FilterUtils.resolveContext(ctx, ds);
            if (!FilterUtils.evaluate(ctx, distE)) {
                continue;
            }
            DistanceBO dsF = new DistanceBO(ds);
            distances.add(dsF);
            for (FrequencyBO fr : ds.getFrequencies()) {
                FilterUtils.resolveContext(ctx, fr);
                if (!FilterUtils.evaluate(ctx, freqE)) {
                    continue;
                }
                FrequencyBO frF = new FrequencyBO(dsF, fr);
                dsF.getFrequencies().add(frF);
                for (SABO nasc : fr.getSa()) {
                    FilterUtils.resolveContext(ctx, nasc);
                    if (!FilterUtils.evaluate(ctx, nascE)) {
                        continue;
                    }
                    SABO nascF = new SABO(frF, nasc);
                    frF.getSa().add(nascF);
                }
            }
        }
        return distances;
    }

    // Helper function
    public static List<DistanceBO> perform(List<DistanceBO> dist, Integer freq, Integer transceiver, Integer acoCat, String chType) {
        Map<String, Object> input = new HashMap<>();
        input.put(Functions.PM_FILTERACOUSTIC_ACOUSTICDATA, dist);
        input.put(Functions.PM_FILTERACOUSTIC_FREQEXPR, "frequency eq " + freq + " && tranceiver eq " + transceiver);
        input.put(Functions.PM_FILTERACOUSTIC_NASCEXPR, "acocat eq " + acoCat + " && chtype eq '" + chType + "'");
        return (List<DistanceBO>) (new FilterAcoustic()).perform(input);
    }
}
