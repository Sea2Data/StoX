/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.processdata;

import java.util.Map;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.RUtils;
import no.imr.stox.log.ILogger;

/**
 * TODO: what does this class do?
 *
 * @author aasmunds
 */
public class StratumArea extends AbstractFunction {

    /**
     *
     * @param input contains Polygon file name
     * @return Matrix object of type POLYGONAREA_MATRIX - see
     * DataTypeDescription.txt
     */
    @Override
    public Object perform(Map<String, Object> input) {
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_STRATUMAREA_PROCESSDATA);
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        String rFolder = (String) input.get(Functions.PM_RFOLDER);
        MatrixBO polygons = AbndEstProcessDataUtil.getStratumPolygons(pd);
        String areaMethod = (String) input.get(Functions.PM_STRATUMAREA_AREAMETHOD);
        Boolean accurate = areaMethod != null && areaMethod.equals(Functions.AREAMETHOD_ACCURATE);
        //REngineProvider rEnginePr = (REngineProvider) input.get(Functions.PM_RENGINEPROVIDER);
        //File rBin = null;
        /*if (accurate) {
         rBin = RUtils.getRBinFolder(null);
         if (rBin == null) {
         accurate = false;
         logger.log("Accurate area method requires R properly installed. The simple area method will be used.");
         }
         }*/

        if (accurate/* && rBin != null*/) {
            // Azimutal equal area projections
            try {
                return RUtils.getAccuratePolygons(rFolder, polygons);
            } catch (Exception ex) {
                logger.error("Accurate method failed with the message " + ex.getMessage()
                        + ". Accurate area method requires R properly installed and the R folder given in options. The simple area method will be used.", null);
            }
        }
        // Approximated area with errors - no projection used:
        return AbndEstProcessDataUtil.getPolygonArea(polygons);
    }

}
