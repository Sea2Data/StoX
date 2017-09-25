package no.imr.stox.functions.landing;

import no.imr.stox.functions.utils.FilterUtils;
import java.util.List;
import java.util.Map;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.bo.LandingData;
import no.imr.stox.bo.landing.FiskeLinje;
import no.imr.stox.bo.landing.SluttSeddel;
import org.apache.commons.jexl2.Expression;
import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.MapContext;

/**
 * This class is used to filter data with special attributes among all biotic
 * data. It uses Java Expression Language (JEXL) to do the filtering.
 *
 * @author atlet
 * @author esmaelmh
 */
public class FilterLanding extends AbstractFunction {

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
        List<SluttSeddel> allLandings = (List<SluttSeddel>) input.get(Functions.PM_FILTERLANDING_LANDINGDATA);
        List<SluttSeddel> landings = new LandingData();//FilterUtils.copyBOList((List) allFishstations, null);
        String landingExpr = (String) input.get(Functions.PM_FILTERLANDING_SLUTTSEDDELEXPR);
        String fiskeLinjeExpr = (String) input.get(Functions.PM_FILTERLANDING_FISKELINJEEXPR);
        if (landingExpr == null || "".equals(landingExpr)) {
            landingExpr = EXPR_TRUE;
        }
        if (fiskeLinjeExpr == null || "".equals(fiskeLinjeExpr)) {
            fiskeLinjeExpr = EXPR_TRUE;
        }
        JexlEngine engine = new JexlEngine();
        engine.setLenient(false);
        engine.setSilent(false);
        Expression landingExpression = engine.createExpression(landingExpr);
        Expression fiskelinjeExpression = engine.createExpression(fiskeLinjeExpr);
        JexlContext ctx = new MapContext();
        for (SluttSeddel sl : allLandings) {
            FilterUtils.resolveContext(ctx, sl);
            if (!FilterUtils.evaluate(ctx, landingExpression)) {
                continue;
            }
            SluttSeddel slF = new SluttSeddel(sl);
            landings.add(slF);
            for (FiskeLinje fl : sl.getFiskelinjer()) {
                FilterUtils.resolveContext(ctx, fl);
                if (!FilterUtils.evaluate(ctx, fiskelinjeExpression)) {
                    continue;
                }
                FiskeLinje flF = new FiskeLinje(slF, fl);
                slF.getFiskelinjer().add(flF);
            }
        }
        return landings;
    }

}
