package no.imr.stox.model;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrbase.util.Workspace;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.datastorage.IDataStorage;
import no.imr.stox.functions.IFunction;
import no.imr.stox.library.IMetaFunction;
import no.imr.stox.library.IMetaParameter;
import no.imr.stox.functions.utils.ReflectionUtil;
import no.imr.stox.exception.UserErrorException;
import no.imr.stox.functions.utils.ProjectUtils;
import org.apache.commons.lang.math.NumberUtils;

/**
 * A process is a step in a model, responsible for parameter actualisation and
 * instantiation of a meta function. Actualisation is performed at "Source"
 * parameters (data out from other processes) and input parameters in process
 * file. This input are merged in a parameter map at the perform call of the
 * instance.
 *
 * @author esmaelmh
 */
public final class Process implements IProcess {

    private String processName;
    private IMetaFunction metaFunction;
    private final Map<String, Object> input = new HashMap<>();
    private Object output;
    private IModel model;
    private Boolean enabled = true;
    private Boolean fileOutput = true;
    private Boolean respondInGUI = false;
    private Boolean breakInGUI = false;
    private IDataStorage dataStorage;

    public Process(IModel model) {
        this.model = model;
    }

    public static IProcess createProcess(IModel model, String processName) {
        IProcess process = new Process(model);
        process.setProcessName(processName);
        process.setRespondInGUI(true); // new processes responds in gui by default
        // Try to find a library function that match the process name and use it as default meta function
        IMetaFunction fnc = model.getLibrary().findMetaFunction(processName);
        if (fnc != null) {
            process.setMetaFunction(fnc);
        }
        return process;
    }

    @Override
    public String getProcessName() {
        return processName;
    }

    @Override
    public void setProcessName(String processName) {
        this.processName = processName;
    }

    public static final String PROCESS_START_LITERAL = "Process("; 

    /**
     * Get input values where referenced datastorage output object from Source
     * processes are reworked to actual parameter values. In this way the
     * outside world doesnt need to know about the datastorage, but a map from
     * datatype to value.
     *
     * @return
     */
    Map<String, Object> actualizeParameters() {
        Map<String, Object> inputValues = new HashMap<>();

        inputValues.put(Functions.PM_LOGGER, getModel().getProcessLog());
        inputValues.put(Functions.PM_RFOLDER, getModel().getProject().getRFolder());
        inputValues.put(Functions.PM_PROJECTFOLDER, model.getProject().getProjectFolder());
        inputValues.put(Functions.PM_MODEL, model);
//        inputValues.put(Functions.PM_RENGINEPROVIDER, REngineProvider.getInstance());

        for (IMetaParameter mp : metaFunction.getMetaParameters()) {
            Object actualValue = getActualValue(mp.getName());
            if (mp.isRequired() && actualValue == null) {
                getModel().getProcessLog().error("Input value is missing for parameter " + mp.getName(), null);
                continue;
            }
            inputValues.put(mp.getName(), actualValue);
        }
        return inputValues;
    } /// getInputValues

    /**
     * get process name from parameter
     *
     * @param mp
     * @return
     */
    @Override
    public String getProcessNameFromParameter(IMetaParameter mp) {
        if (mp.getMetaDataType() != null && mp.getMetaDataType().isReference()) {
            Object inputVal = input.get(mp.getName());
            String pr = (String) inputVal;
            if (pr != null && pr.contains(PROCESS_START_LITERAL)) {
                return pr.substring(PROCESS_START_LITERAL.length(), pr.length() - 1);
            }
        }
        return null;
    }

    /**
     * set parameter
     *
     * @param name
     * @param value
     * @return
     */
    @Override
    public IProcess setParameterValue(String name, Object value) {
        if (getMetaFunction() == null) {
            return null;
        }
        if (name.equalsIgnoreCase("enabled")) {
            Boolean bs = Conversion.safeObjectToBoolean(value);
            if (bs != null) {
                setEnabled(bs);
            }
        }
        if (name.equalsIgnoreCase("fileoutput")) {
            Boolean bs = Conversion.safeObjectToBoolean(value);
            if (bs != null) {
                setFileOutput(bs);
            }
        }
        IMetaParameter mp = getMetaFunction().findMetaParameter(name);
        if (mp == null) {
            getModel().getProcessLog().log("Unspecified or deprecated parameter given: " + name);
            return this;
        }
        if (mp.getValues() != null && !mp.getValues().isEmpty() && value != null) {
            // Translate old aliases to current values.
            value = mp.resolveParameterValue(value.toString());
            if (value == null) {
                return this;
            }
        }
        setValue(mp, value);
        return this;
    }

    /**
     *
     * @param name
     * @param process
     */
    @Override
    public IProcess setParameterProcessValue(String name, IProcess process) {
        setParameterValue(name, process.getProcessName());
        return this;
    }

    @Override
    public IProcess setParameterProcessValue(String name, String processName) {
        setParameterValue(name, "Process(" + processName + ")");
        return this;
    }

    @Override
    public IProcess setParameterProcessValueByFunction(String name, String functionName) {
        IProcess pr = getModel().getProcessByFunctionName(functionName);
        if (pr != null) {
            setParameterProcessValue(name, pr.getProcessName());
        }
        return this;
    }

    /**
     *
     * @param name
     * @return parameter value from parameter name
     */
    @Override
    public Object getParameterValue(String name) {
        IMetaParameter mp = getMetaFunction().findMetaParameter(name);
        if (mp == null) {
            return null;
        }
        return getValue(mp);
    }

    /**
     * Get actual value used in function. Convert data type from string to its
     * object, Process(<process>) converts to output data value from referenced
     * process.
     *
     * @param parameter
     * @param mp
     * @return
     */
    @Override
    public Object getActualValue(String parameter) {
        IMetaParameter mp = getMetaFunction().findMetaParameter(parameter);
        if (mp == null) {
            return null;
        }
        Object formalValue = getParameterValue(parameter);
        Object actualValue = formalValue;
        // Do not transalte null to default value
        /*
        if (actualValue == null) {
            actualValue = mp.getDefaultValue();
        }*/
        if (actualValue != null) {
            if (mp.getMetaDataType().isReference()) {
                String procName = getProcessNameFromParameter(mp);
                IProcess proc = model.getProject().findProcess(procName);
                if (proc != null && proc.getMetaFunction() != null) {
                    if (mp.isFileRef()) {
                        // A process reference can be either file reference 
                        actualValue = proc.getOutputFileName(null, null, null);
                    } else {
                        // or memory reference to current output.
                        actualValue = proc.getOutput();
                    }
                }
            } else {
                // primitive datatypes
                switch (mp.getMetaDataType().getName()) {
                    case Functions.DT_INTEGER:
                        actualValue = Conversion.safeStringtoIntegerNULL(actualValue.toString());
                        break;
                    case Functions.DT_DOUBLE:
                        actualValue = Conversion.safeStringtoDoubleNULL(actualValue.toString());
                        break;
                    case Functions.DT_BOOLEAN:
                        actualValue = Boolean.valueOf(actualValue.toString().trim());
                        break;
                }

            }
        }
        return actualValue;
    }

    /**
     * Check if the process syntax is valid
     *
     * @return
     */
    @Override
    public String validate() {
        if (metaFunction == null) {
            return "Process must refer to function in the library";
        }
        for (IMetaParameter mp : metaFunction.getMetaParameters()) {
            String paramDescr = "Parameter " + mp.getName() + ": ";
            Object pValue = input.get(mp.getName());
            if (mp.isRequired() && pValue == null) {
                return paramDescr + "Input value is missing.";
            }
            if (pValue != null && mp.getMetaDataType().isReference()) {
                // Process(AggregateVertical_2)
                String outputRef = (String) pValue;
                if (!outputRef.contains(PROCESS_START_LITERAL)) {
                    return paramDescr + "Reference parameter must refer to process.";
                }
                String procName = outputRef.substring(PROCESS_START_LITERAL.length(), outputRef.length() - 1);
                IProcess proc = model.isReportModel() ? model.getProject().findProcess(procName)
                        : model.findProcess(procName, model.getProcessList().indexOf(this) - 1);
                if (proc == null) {
                    return paramDescr + "Cannot find any process named '" + procName + "' of the preceding processes.";
                }
                if (proc.getMetaFunction() == null) {
                    return paramDescr + ". The input process '" + procName + "' has not a function set.";
                }
                if (proc.getMetaFunction().getMetaOutput() == null || proc.getMetaFunction().getMetaOutput().getMetaDataType() == null) {
                    return paramDescr + "Input value has no datatype, but datatype " + mp.getMetaDataType() + IS_EXPECTED_LITERAL;
                }
                if (mp.getMetaMatrix() != null) {
                    // Matrix data type check
                    if (proc.getMetaFunction().getMetaOutput().getMetaMatrix() == null) {
                        return paramDescr + "Input value is not a matrix type, a matrix of type " + mp.getMetaMatrix() + IS_EXPECTED_LITERAL;
                    } else if (!mp.getMetaMatrix().toString().equals(proc.getMetaFunction().getMetaOutput().getMetaMatrix().toString())) {
                        return paramDescr + "Input value is a matrix of type " + proc.getMetaFunction().getMetaOutput().getMetaMatrix()
                                + ", but a matrix of type " + mp.getMetaMatrix() + IS_EXPECTED_LITERAL;
                    }
                }
                if (!proc.getMetaFunction().getMetaOutput().getMetaDataType().equals(mp.getMetaDataType())) {
                    return paramDescr + "Input value has datatype " + proc.getMetaFunction().getMetaOutput().getMetaDataType()
                            + ", but datatype " + mp.getMetaDataType() + IS_EXPECTED_LITERAL;
                }
                String procvalid = proc.validate();
                if (procvalid != null) {
                    return paramDescr + "Input process has invalid parameters";
                }
            }
        }
        return null;
    }
    private static final String IS_EXPECTED_LITERAL = " is expected.";

    /**
     * Sets all the attributes needed on its IFunction instance and calls method
     * perform() on it.
     *
     * @throws no.imr.stox.exception.UserErrorException
     */
    @Override
    public void perform() {
        // Perform internal:
        performInternal();
    }

    @Override
    public void performInternal() {
        output = null;
        if (enabled) {
            // Instantiate the function by reflection
            IFunction fnc = getFunction();
            if (fnc == null) {
                return;
            }
            output = fnc.perform(actualizeParameters());
            if (output == null) {
                return;
            }
            if (dataStorage == null) {
                // Get Function dependent ad hoc datastorage from function itself
                dataStorage = fnc.getDataStorage();
                if (dataStorage == null) {
                    return;
                }
                dataStorage.setProcess(this);
            }
            // Write output to file through datastorage
            if (output != null && fileOutput) {
                dataStorage.writeToFile();
            }
        }
    }

    @Override
    public IDataStorage getDataStorage() {
        return dataStorage;
    }

    private IFunction getFunction() {
        return ReflectionUtil.getClassInstance(metaFunction.getClazz());
    }

    @Override
    public Object performFunction() {
        try {
            IFunction fnc = getFunction();
            return fnc.perform(actualizeParameters());
        } catch (UserErrorException ex) {
            getModel().getProcessLog().error("Function " + metaFunction.getName() + " failed with the reason: " + ex.getMessage(), ex);
        }
        return null;
    }

    @Override
    public String toString() {
        return processName;
    }

    @Override
    public IMetaFunction getMetaFunction() {
        return metaFunction;
    }

    @Override
    public void setMetaFunction(IMetaFunction metaFunction) {
        this.metaFunction = metaFunction;
        // Reset old parameters.
        getInput().clear();
        // Set new parameters to default value:
        if (metaFunction != null) {
            for (IMetaParameter mp : metaFunction.getMetaParameters()) {
                setValue(mp, mp.getDefaultValue());
            }
            if (metaFunction.getDataStorage() != null) {
                dataStorage = ReflectionUtil.getClassInstance(metaFunction.getDataStorage());
                if (dataStorage != null) {
                    dataStorage.setProcess(this);
                }
            }
        }
    }

    @Override
    public <T> T getOutput() {
        return (T) output;
    }

    @Override
    public void clearOutput() {
        output = null;
    }

    @Override
    public String getName() {
        return processName;
    }

    @Override
    public Map<String, Object> getInput() {
        return input;
    }

    @Override
    public IModel getModel() {
        return model;
    }

    @Override
    public void setModel(IModel model) {
        this.model = model;
    }

    @Override
    public Boolean isEnabled() {
        return enabled;
    }

    @Override
    public IProcess setEnabled(Boolean enabled) {
        this.enabled = enabled;
        return this;
    }

    @Override
    public Boolean isRespondInGUI() {
        return respondInGUI;
    }

    @Override
    public IProcess setRespondInGUI(Boolean respondInGUI) {
        this.respondInGUI = respondInGUI;
        if (!respondInGUI.equals(this.respondInGUI) && metaFunction != null && !metaFunction.isRespondable()) {
            // Fire response change
            if (getModel().getModellisteners() != null) {
                getModel().getModellisteners().stream().forEach((ml) -> {
                    ml.onProcessChanged(this);
                });
            }
        }
        return this;
    }

    @Override
    public Boolean isBreakInGUI() {
        return breakInGUI;
    }

    @Override
    public IProcess setBreakInGUI(Boolean breakInGUI) {
        if (!breakInGUI.equals(this.breakInGUI)) {
            this.breakInGUI = breakInGUI;
            if (getModel().getModellisteners() != null) {
                getModel().getModellisteners().stream().forEach((ml) -> {
                    ml.onProcessChanged(this);
                });
            }
        }
        return this;
    }

    @Override
    public Integer getProcessStep() {
        return model.getProcessList().indexOf(model.findProcess(processName)) + 1;
    }

    @Override
    public boolean isPerformed() {
        return getModel().getProcessList().indexOf(this) <= getModel().getRunningProcessIdx();
    }

    @Override
    public void setValue(IMetaParameter mp, Object value) {
        if (value != null && !(value instanceof String)) {
            value = value.toString();
        }
        // reject the value if the string is not a number ofr integer and double type
        if (value != null && Stream.of(Functions.DT_INTEGER, Functions.DT_DOUBLE).anyMatch(s -> s.equals(mp.getMetaDataType().getName()))
                && !NumberUtils.isNumber(value.toString())) {
            return;
        }
        // accept the value
        getInput().put(mp.getName(), value);
    }

    @Override
    public Object getValue(IMetaParameter mp) {
        return getInput().get(mp.getName());
    }

    @Override
    public boolean hasError() {
        return validate() != null;
    }

    @Override
    public IProcess setFileOutput(Boolean fileOutput) {
        this.fileOutput = fileOutput;
        return this;
    }

    @Override
    public Boolean getFileOutput() {
        return fileOutput;
    }

    @Override
    public String getOutputFileName() {
        String res = getProcessStep() + "_" + getProcessName();
        if (getMetaFunction().getMetaOutput() != null) {
            res += "_" + getMetaFunction().getMetaOutput().getMetaDataType().getName();
        }
        return res;
    }

    @Override
    public String getOutputFileName(String outputFolder, String outputFileName, String postFix) {
        if (outputFolder == null) {
            outputFolder = ProjectUtils.PROJECT_OUTPUTBASELINE_DATA;
        }
        if (outputFileName == null) {
            outputFileName = getOutputFileName();
        }
        if (postFix == null) {
            postFix = "";
        }
        if (!postFix.isEmpty()) {
            postFix = "_" + postFix;
        }
        return ProjectUtils.txt(Workspace.getDir(getModel().getProject().getProjectFolder(), outputFolder) + "/" + outputFileName + postFix);
    }

    @Override
    public void moveTo(IModel model) {
        getModel().getProcessList().remove(this);
        getModel().setDirty(true);
        model.getProcessList().add(this);
        setModel(model);
        model.setDirty(true);
    }

}
