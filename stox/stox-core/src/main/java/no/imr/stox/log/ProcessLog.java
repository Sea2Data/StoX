package no.imr.stox.log;

import no.imr.stox.exception.UserErrorException;
import no.imr.stox.model.IModel;
import no.imr.stox.model.IModelListener;

/**
 * TODO: hva er dette for en klasse?
 *
 * @author aasmunds
 */
public class ProcessLog implements ILogger {

    private final IModel model;

    public ProcessLog(final IModel model) {
        this.model = model;
    }

    /**
     *
     * @param msg
     */
    @Override
    public void log(String msg) {
        for (IModelListener listener : model.getModellisteners()) {
            listener.onProcessLog(model, msg);
        }
    }

    @Override
    public void error(String error, Exception e) {
        //log(error);
        if (e == null) {
            throw new UserErrorException(error);
        } else {
            throw new UserErrorException(e);
        }
    }

}
