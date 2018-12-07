package no.imr.stox.functions.landing;

import no.imr.stox.functions.utils.FilterUtils;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.bo.LandingData;
import no.imr.stox.bo.landing.LandingsdataBO;
import no.imr.stox.bo.landing.SeddellinjeBO;
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
        LandingData allLandings = (LandingData) input.get(Functions.PM_FILTERLANDING_LANDINGDATA);
        LandingData landings = new LandingData();//FilterUtils.copyBOList((List) allFishstations, null);
        String landingExpr = (String) input.get(Functions.PM_FILTERLANDING_LANDINGEXPR);
        if (landingExpr == null || "".equals(landingExpr)) {
            landingExpr = EXPR_TRUE;
        }
        if (landingExpr == null || "".equals(landingExpr)) {
            landingExpr = EXPR_TRUE;
        }
        JexlEngine engine = new JexlEngine();
        engine.setLenient(false);
        engine.setSilent(false);
        Expression landingExpression = engine.createExpression(landingExpr);
        for (LandingsdataBO la : allLandings) {
            LandingsdataBO laF = new LandingsdataBO(la);
            landings.add(laF);
            List<SeddellinjeBO> sll // fork join - filtering
                    = la.getSeddellinjeBOs().parallelStream()// fork to cpu's
                            .filter(fl -> {
                                JexlContext ctx = new MapContext();
                                FilterUtils.resolveContext(ctx, fl);
                                return FilterUtils.evaluate(ctx, landingExpression);
                            })
                            .map(fl -> new SeddellinjeBO(laF, fl))
                            .collect(Collectors.toList()); // join from cpu's
            laF.setSeddellinjeBOs(sll);
        }
        return landings;
    }

}
