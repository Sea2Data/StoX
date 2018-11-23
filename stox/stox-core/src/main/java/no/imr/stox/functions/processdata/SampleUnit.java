/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.processdata;

import java.util.Map;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.bo.SampleUnitBO;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.RUtils;
import no.imr.stox.log.ILogger;

/**
 *
 * @author aasmunds
 */
public class SampleUnit extends AbstractFunction {

    @Override
    public Object perform(Map<String, Object> input) {
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_STRATUMAREA_PROCESSDATA);
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        return pd; // forward output to datastorage
    }

}
