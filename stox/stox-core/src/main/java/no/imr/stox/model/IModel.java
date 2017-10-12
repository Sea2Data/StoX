package no.imr.stox.model;

import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import no.imr.stox.library.ILibrary;
import no.imr.stox.library.IMetaFunction;
import no.imr.stox.log.ILogger;

/**
 * Class that handles running of the different functions in sequence
 *
 * @author aasmunds
 */
public interface IModel {

    final int RUNSTATE_STOPPED = 0; // Ready to start
    final int RUNSTATE_RUNNING = 1; // Running

    boolean isFinished();

    /**
     * @param firstProcess
     * @param lastProcess
     * @param inBackground
     * @throws
     * no.imr.sea2data.beam.base.exceptions.runner.ModelExceptionModelExceptionInSystem(Integer
     * firstProcess, Integer lastProcess) throws ModelException;
     */
    void run(Integer firstProcess, Integer lastProcess, Boolean inBackground);

    /**
     * Set run state
     *
     * @param runState
     */
    void setRunState(int runState);

    /**
     * Set run state of the system runner
     *
     * @return
     */
    int getRunState();

    /**
     * Save model and processes with datastorages built on run and save process
     * file as well.
     */
    //void save();
    /**
     * Save model in xml stream
     *
     * @param level
     * @param xmlsw
     * @throws no.imr.sea2data.imrbase.util.XMLWriter.StAXWriterException
     * @throws XMLStreamException
     */
    void save(int level, XMLStreamWriter xmlsw);

    /**
     * get library
     *
     * @return
     */
    ILibrary getLibrary();

    /**
     * Get Beam Process list
     *
     * @return
     */
    List<IProcess> getProcessList();

    /**
     * Find process by processstep
     *
     * @param procName
     * @return
     */
    IProcess findProcess(String procName);

    IProcess findProcess(String procName, int endIdx);

    IProcess findProcessByFunction(String fnc);

    /**
     * Add model listener
     *
     * @param listener
     */
    void addModellistener(IModelListener listener);

    /**
     * Get model listeners. use this when sending events
     *
     * @return
     */
    List<IModelListener> getModellisteners();

    void setModellisteners(List<IModelListener> listeners);

    Integer getRunningProcessIdx();

    Integer getNextRunningProcessIdx();

    void setRunningProcess(int idx);

    IProcess getRunningProcess();

    String validate();

    IProcess getProcessByFunctionName(String functionName);

    List<IProcess> getProcessesByFunctionName(String functionName);

    String getModelName();

    String getDescription();

    void setDescription(String description);

    Boolean isBreakable();

    void setBreakable(Boolean breakable);

    boolean isExportCSV();

    void setExportCSV(Boolean exportCSV);

    /**
     *
     * @param n - the process name to search for
     * @return process from name
     */
    IProcess getProcessFromName(String n);

    IProject getProject();

    void setDirty(boolean b);

    Boolean isDirty();

    void reOrder(int[] perm);

    IProcess addProcess(String processName, String functionName);

    public IProcess insertProcess(String processName, String functionName, Integer iBefore);

    boolean isReportModel();

    List<IMetaFunction> getMetaFunctions();

    ILogger getProcessLog();

    void reset();

    Integer getWarningLevel();

    void setWarningLevel(Integer warningLevel);

    String getOutputFolder();

}
