/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.processdata;

import java.util.List;
import java.util.Map;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.EchosounderUtils;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.log.ILogger;

/**
 * TODO: what does this class do?
 *
 * @author aasmunds
 */
public class DefineTransect extends AbstractFunction {

    /**
     *
     * @param input contains Polygon file name
     * @return Matrix object of type POLYGON_MATRIX - see
     * DataTypeDescription.txt
     */
    @Override
    public Object perform(Map<String, Object> input) {
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_DEFINEACOUSTICTRANSECT_PROCESSDATA);
        List<DistanceBO> acousticData = (List<DistanceBO>) input.get(Functions.PM_DEFINEACOUSTICTRANSECT_ACOUSTICDATA);
        Boolean useEx = (Boolean) input.get(Functions.PM_DEFINEACOUSTICTRANSECT_USEPROCESSDATA);
        if (pd != null && useEx != null && !useEx) {
            // Clear the psu definitions.
            AbndEstProcessDataUtil.getEDSUPSUs(pd).clear();
            AbndEstProcessDataUtil.getPSUStrata(pd).clear();
        }
        // check EDSU-PSU keys up against acoustic data.
        String errorStr = AbndEstProcessDataUtil.getEDSUPSUs(pd).getRowKeys().parallelStream()
                .map(edsu -> EchosounderUtils.findDistance(acousticData, edsu) == null ? edsu : null)
                .filter(d -> d != null).findFirst().orElse(null);
        if (errorStr != null && !errorStr.isEmpty()) {
            logger.log("Warning: The following EDSU in the EDSUPSU processdata table is not found in the data:\n" + errorStr + ".");
        }
        // Break in gui at onEndProcess to redefine...
        return pd;
    }
}
