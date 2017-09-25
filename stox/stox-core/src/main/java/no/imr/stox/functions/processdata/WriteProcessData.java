package no.imr.stox.functions.processdata;

import java.util.Map;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.model.IModel;

/**
 * TODO: what does this class do?
 *
 * @author aasmunds
 */
public class WriteProcessData extends AbstractFunction {

    /**
     *
     * @param input Contains Working directory and logger
     * @return null
     */
    @Override
    public Object perform(Map<String, Object> input) {
        IModel m = (IModel) input.get(Functions.PM_MODEL);
        m.getProject().save();
        //ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_SAVEPROJECT_PROCESSDATA);
        // No stored in datastorage..See processdatastorage
        return null;
    }
}
