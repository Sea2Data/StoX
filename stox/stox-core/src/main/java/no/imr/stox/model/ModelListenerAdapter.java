package no.imr.stox.model;

/**
 * TODO: What is this interface supposed to do?
 *
 * @author Åsmund
 */
public class ModelListenerAdapter implements IModelListener {

    @Override
    public void onProcessBegin(IProcess process) {
    }

    @Override
    public void onProcessEnd(IProcess process) {
    }

    @Override
    public void onProcessLog(IModel model, String msg) {
    }

    @Override
    public void onModelStart(IModel model) {
    }

    @Override
    public void onModelStop(IModel model) {
    }

    public void onReset(IModel m) {
    }

    @Override
    public void onRunningProcessChanged(IModel model, Integer runningProcess) {
        if (-1 == runningProcess) {
            onReset(model);
        }
    }

    @Override
    public void onProcessChanged(Process process) {
    }

}
