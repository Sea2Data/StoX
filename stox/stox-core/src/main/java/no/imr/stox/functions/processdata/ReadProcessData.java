package no.imr.stox.functions.processdata;

import java.util.Map;
import no.imr.stox.util.matrix.MatrixBO;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.model.IModel;

/**
 * Read process data from file into memory.
 *
 * @author aasmunds
 */
public class ReadProcessData extends AbstractFunction {

    /**
     * Read process data from project folder process 
     * @param input Contains Working directory and logger
     * @return Matrix object of type PROCESSDATA - see DataTypeDescription.txt
     */
    @Override
    public Object perform(Map<String, Object> input) {
        IModel m = (IModel) input.get(Functions.PM_MODEL);
        ProcessDataBO pd = m.getProject().getProcessData();
        
        // Clear on the fly tables:
        MatrixBO covParam = AbndEstProcessDataUtil.getCovParam(pd);
        covParam.clear();
        
        return m.getProject().getProcessData();
    }
}
