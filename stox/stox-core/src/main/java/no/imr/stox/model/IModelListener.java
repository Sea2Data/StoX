package no.imr.stox.model;

/**
 * TODO: What is the purpouse of this interface?
 *
 * @author Åsmund
 */
public interface IModelListener {

    void onProcessBegin(IProcess process);

    void onProcessEnd(IProcess process);

    void onProcessLog(IModel model, String msg);

    void onModelStart(IModel model);

    void onModelStop(IModel model);

    void onRunningProcessChanged(IModel model, Integer runningProcess);

    void onProcessChanged(Process process);
}
