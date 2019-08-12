/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.processdata;

import java.util.Map;
import no.imr.stox.util.matrix.MatrixBO;
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
        SampleUnitBO sampleUnit= new SampleUnitBO();
        // Transfer 
        sampleUnit.setMatrix(AbndEstProcessDataUtil.TABLE_EDSUPSU, pd.getMatrix(AbndEstProcessDataUtil.TABLE_EDSUPSU));
        sampleUnit.setMatrix(AbndEstProcessDataUtil.TABLE_PSUSTRATUM, pd.getMatrix(AbndEstProcessDataUtil.TABLE_PSUSTRATUM));
        return sampleUnit; 
    }

}
