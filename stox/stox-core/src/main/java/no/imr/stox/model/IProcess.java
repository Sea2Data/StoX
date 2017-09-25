package no.imr.stox.model;

import java.util.Map;
import no.imr.stox.datastorage.IDataStorage;
import no.imr.stox.library.IMetaFunction;
import no.imr.stox.library.IMetaParameter;

/**
 * TODO: hva er funksjonen til dette interfacet?
 *
 * @author aasmunds
 */
public interface IProcess {

    /**
     * Get meta function reference
     *
     * @return
     */
    IMetaFunction getMetaFunction();

    void setMetaFunction(IMetaFunction metaFunction);

    /**
     * Get process step
     *
     * @return
     */
    String getProcessName();

    /**
     * Set process step
     *
     * @param processName
     */
    void setProcessName(String processName);

    String validate();

    void perform();

    void performInternal();

    Object performFunction();

    IDataStorage getDataStorage();

    <T> T getOutput();

    void clearOutput();

    String getName();

    Map<String, Object> getInput();

    IModel getModel();

    void setModel(IModel model);

    Boolean isEnabled();

    Boolean isRespondInGUI();

    IProcess setRespondInGUI(Boolean respondInGUI);

    Boolean isBreakInGUI();

    IProcess setBreakInGUI(Boolean breakInGUI);

    IProcess setEnabled(Boolean enabled);

    Integer getProcessStep();

    boolean isPerformed();

    void setValue(IMetaParameter mp, Object value);

    Object getValue(IMetaParameter mp);

    Object getActualValue(String parameter);

    IProcess setFileOutput(Boolean fileOutput);

    Boolean getFileOutput();

    boolean hasError();

    String getProcessNameFromParameter(IMetaParameter mp);

    IProcess setParameterValue(String name, Object value);

    IProcess setParameterProcessValue(String name, IProcess process);

    IProcess setParameterProcessValue(String name, String processName);

    IProcess setParameterProcessValueByFunction(String name, String functionName);

    Object getParameterValue(String name);

    String getOutputFileName();

    String getOutputFileName(String outputFolder, String outputFileName, String postFix);

    void moveTo(IModel model);
}
