/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.biotic;

import java.util.List;
import java.util.Map;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.MissionBO;
import no.imr.stox.bo.BioticData;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.StoXMath;
import no.imr.stox.log.ILogger;

/**
 * Take in the the biotic data and return it to be used by the data storage for
 * reporting.
 *
 * @author aasmunds
 */
public class DATRASConvert extends AbstractFunction {

    @Override
    public Object perform(Map<String, Object> input) {
        BioticData data = (BioticData)input.get(Functions.PM_DATRAS_BIOTICDATA);;
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        if (data != null && !(data.isLengthCMAdded() || data.isIndividualWeightGAdded() || data.isAgeAdded())) {
            logger.error("LengthCM/IndividualWeightG/Age not defined. Add DefineIndMeasurement and DefineIndAge to model.", null);
        }
        return data;
    }

}
