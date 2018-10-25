package no.imr.stox.functions.biotic;

import no.imr.stox.functions.utils.FilterUtils;
import java.util.List;
import java.util.Map;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.AbstractFunction;
import no.imr.sea2data.biotic.bo.CatchBO;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.biotic.bo.SampleBO;
import no.imr.stox.bo.BioticData;
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
public class FilterBiotic extends AbstractFunction {

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
        String stationExpr = (String) input.get(Functions.PM_FILTERBIOTIC_FISHSTATIONEXPR);
        String catchExpr = (String) input.get(Functions.PM_FILTERBIOTIC_CATCHEXPR);
        String sampleExpr = (String) input.get(Functions.PM_FILTERBIOTIC_SAMPLEEXPR);
        String individualExpr = (String) input.get(Functions.PM_FILTERBIOTIC_INDEXPR);
        if (stationExpr == null || "".equals(stationExpr)) {
            stationExpr = EXPR_TRUE;
        }
        if (catchExpr == null || "".equals(catchExpr)) {
            catchExpr = EXPR_TRUE;
        }
        if (sampleExpr == null || "".equals(sampleExpr)) {
            sampleExpr = EXPR_TRUE;
        }
        if (individualExpr == null || "".equals(individualExpr)) {
            individualExpr = EXPR_TRUE;
        }
        JexlEngine engine = new JexlEngine();
        engine.setLenient(false);
        engine.setSilent(false);
        Expression stationExpression = engine.createExpression(stationExpr);
        Expression catchExpression = engine.createExpression(catchExpr.toLowerCase()); // species incasesensitive
        Expression sampleExpression = engine.createExpression(sampleExpr.toLowerCase());
        Expression individualExpression = engine.createExpression(individualExpr.toLowerCase());
        JexlContext ctx = new MapContext();
        // Old structures:
        List<FishstationBO> allFishstations = (List<FishstationBO>) input.get(Functions.PM_FILTERBIOTIC_BIOTICDATA);
        List<FishstationBO> fishstations = new BioticData();//FilterUtils.copyBOList((List) allFishstations, null);
        for (FishstationBO fs : allFishstations) {
            FilterUtils.resolveContext(ctx, fs);
            if (!FilterUtils.evaluate(ctx, stationExpression)) {
                continue;
            }
            FishstationBO fsF = new FishstationBO(fs);
            fishstations.add(fsF);
            for (CatchBO cb : fs.getCatchBOs()) {
                FilterUtils.resolveContext(ctx, cb);
                if (!FilterUtils.evaluate(ctx, catchExpression)) {
                    continue;
                }
                CatchBO cbF = new CatchBO(fsF, cb);
                fsF.getCatchBOs().add(cbF);
                for (SampleBO sample : cb.getSampleBOs()) {
                    FilterUtils.resolveContext(ctx, sample);
                    if (!FilterUtils.evaluate(ctx, sampleExpression)) {
                        continue;
                    }
                    SampleBO sampleF = new SampleBO(cbF, sample);
                    cbF.getSampleBOs().add(sampleF);
                    for (IndividualBO in : sample.getIndividualBOs()) {
                        FilterUtils.resolveContext(ctx, in);
                        if (!FilterUtils.evaluate(ctx, individualExpression)) {
                            continue;
                        }
                        IndividualBO inF = new IndividualBO(sampleF, in);
                        sampleF.getIndividualBOs().add(inF);
                    }
                }
            }
        }
        return fishstations;
    }

}
