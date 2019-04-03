package no.imr.stox.functions.biotic;

import java.util.ArrayList;
import no.imr.stox.functions.utils.FilterUtils;
import java.util.List;
import java.util.Map;
import no.imr.sea2data.biotic.bo.AgeDeterminationBO;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.AbstractFunction;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.biotic.bo.CatchSampleBO;
import no.imr.sea2data.biotic.bo.MissionBO;
import no.imr.stox.bo.BioticData;
import no.imr.stox.functions.utils.BioticUtils;
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
        BioticData bdata = (BioticData) input.get(Functions.PM_FILTERBIOTIC_BIOTICDATA);
        BioticData missions = BioticUtils.copyBioticData(bdata, BioticUtils.BIOTICDATA_COPY_FLAGS_RESETDATA);
        for (MissionBO ms : bdata.getMissions()) {
            MissionBO msF = new MissionBO(ms);
            missions.getMissions().add(msF);
            for (FishstationBO fs : ms.getFishstationBOs()) {
                FilterUtils.resolveContext(ctx, fs);
                if (!FilterUtils.evaluate(ctx, stationExpression)) {
                    continue;
                }
                FishstationBO fsF = msF.addFishstation(new FishstationBO(ms, fs));
                for (CatchSampleBO cb : fs.getCatchSampleBOs()) {
                    FilterUtils.resolveContext(ctx, cb);
                    // todo join catch and sample expression as input parameter
                    if (!(FilterUtils.evaluate(ctx, catchExpression) && FilterUtils.evaluate(ctx, sampleExpression))) {
                        continue;
                    }
                    CatchSampleBO sampleF = fsF.addCatchSample(new CatchSampleBO(fsF, cb));
                    sampleF.setSpecCat(cb.getSpecCat());
                    for (IndividualBO in : cb.getIndividualBOs()) {
                        FilterUtils.resolveContext(ctx, in);
                        // Individual fields.
                        if (bdata.isLengthCMAdded()) {
                            ctx.set("lengthcm", in.getLengthCM());
                        }
                        if (bdata.isIndividualWeightGAdded()) {
                            ctx.set("individualweightg", in.getIndividualWeightG());
                        }
                        if (bdata.isAgeMerged()) {
                            ctx.set("age", in.getAge());
                        }
                        if (!FilterUtils.evaluate(ctx, individualExpression)) {
                            continue;
                        }
                        IndividualBO inF = sampleF.addIndividual(new IndividualBO(sampleF, in));
                        inF.setIndividualWeightG(in.getIndividualWeightG());
                        inF.setLengthCM(in.getLengthCM());
                        for (AgeDeterminationBO aBO : in.getAgeDeterminationBOs()) {
                            inF.addAgeDetermination(new AgeDeterminationBO(inF, aBO));
                        }
                    }
                }
            }
        }
        return missions;
    }
}
