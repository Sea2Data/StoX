package no.imr.stox.model;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.SwingWorker;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import no.imr.sea2data.imrbase.util.XMLWriter;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.ProjectUtils;
import no.imr.stox.library.ILibrary;
import no.imr.stox.library.IMetaFunction;
import no.imr.stox.library.IMetaParameter;
import no.imr.stox.log.ILogger;
import no.imr.stox.log.ProcessLog;

/**
 * Is implementation of ISystemRunner interface and instance of this class is us
 * to call method perform() on instances of BeamPrProcessquence given in the
 * pcess file, and which is read by instance of ProcessFileReader.
 *
 * @author aasmunds
 */
public class Model implements IModel {

    /**
     * Logger
     */
    private final List<IProcess> processList = new ArrayList<>();
    private List<IModelListener> modelListeners = new ArrayList<>();
    private Integer runningProcess = -1;
    private Integer runState = RUNSTATE_STOPPED;
    private Integer firstProcess;
    private Integer lastProcess;
    private final IProject project;
    private String modelName;
    private String description;
    private Boolean breakable = true;
    private Boolean exportCSV = true;
    private Integer warningLevel = Functions.WARNINGLEVEL_STRICT; // 0=strict,1=medium
    private final String processFileName;
    private Boolean dirty = false;
    private boolean readingModel = false;
    private final ILogger processLog;

    public Model(IProject project, String modelName, String processFileName) {
        this.project = project;
        this.processFileName = processFileName;
        this.modelName = modelName;
        processLog = new ProcessLog(this);
    }

    @Override
    public IProcess findProcess(String procName) {
        return findProcess(procName, getProcessList().size() - 1);
    }

    @Override
    public IProcess findProcess(String procName, int endIdx) {
        for (int i = 0; i <= endIdx; i++) {
            IProcess process = getProcessList().get(i);
            if (process.getProcessName().equals(procName)) {
                return process;
            }
        }
        return null;
    }

    @Override
    public IProcess findProcessByFunction(String fnc) {
        for (IProcess process : getProcessList()) {
            if (process.getMetaFunction() != null && process.getMetaFunction().getName().equals(fnc)) {
                return process;
            }
        }
        return null;
    }

    void start() {
        setRunState(RUNSTATE_RUNNING);
        getModellisteners().stream().forEach((ml) -> {
            ml.onModelStart(this);
        });
        if (firstProcess == 0) {
            // Trash
            // Implement reset trashing
            String outputFolder = getOutputFolder();
            String trashFolder = getTrashFolder();
            Boolean doTrash = isExportCSV();
            if (doTrash && outputFolder != null && trashFolder != null) {
                // Move txt/png files from output folder to trash folder
                outputFolder = getProject().getProjectFolder() + "/" + outputFolder;
                trashFolder = getProject().getProjectFolder() + "/" + trashFolder;
                File of = new File(outputFolder);
                File tf = new File(trashFolder);
                if (of.exists() && tf.exists() && of.canWrite() && tf.canWrite()) {
                    Arrays.stream(of.listFiles(f -> f.getName().endsWith("txt") || f.getName().endsWith("png"))).
                            forEach(f -> {
                                String fn = f.getName();
                                File dest = new File(tf.getPath() + "/" + fn);
                                if (dest.exists()) {
                                    dest.delete();
                                }
                                f.renameTo(dest);
                            });
                }

            }
        }
    }

    void stop() {
        setRunState(RUNSTATE_STOPPED);
        IProcess p = getProcessList().get(getRunningProcessIdx());
        getModellisteners().stream().forEach((ml) -> {
            ml.onModelStop(this);
        });
    }

    protected void runModel() {
        if (firstProcess < 0 && lastProcess > getProcessList().size() - 1 || firstProcess > lastProcess) {
            getProcessLog().error("Cant run the model with index " + (firstProcess + 1) + ".." + (lastProcess + 1), null);
        }
        start();
        try {
            for (int prIdx = firstProcess; prIdx <= lastProcess; prIdx++) {
                IProcess p = getProcessList().get(prIdx);
                try {
                    // Tell the listeners
                    onStart(p);
                    if (p.isEnabled()) {
                        getProcessLog().log("Process " + p.getName() + (p.isRespondInGUI() ? " / Respond in gui" : ""));
                    }
                    p.perform();
                    setRunningProcess(prIdx);
                    onEnd(p);
                    if (breakable && p.isBreakInGUI()) {
                        getProcessLog().log("Break in gui");
                        break;
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    getProcessLog().log("Error at " + p.getName() + ": " + ex.getMessage());
                    break;
                }
            }
        } finally {
            stop();
        }
    }

    @Override
    public String getOutputFolder() {
        switch (getModelName()) {
            case ProjectUtils.BASELINE:
                return ProjectUtils.PROJECT_OUTPUTBASELINE_DATA;
            case ProjectUtils.BASELINE_REPORT:
                return ProjectUtils.PROJECT_OUTPUTBASELINE_REPORT;
            case ProjectUtils.R:
                return ProjectUtils.PROJECT_OUTPUTR_DATA;
            case ProjectUtils.R_REPORT:
                return ProjectUtils.PROJECT_OUTPUTR_REPORT;
        }
        return null;
    }

    String getTrashFolder() {
        switch (getModelName()) {
            case ProjectUtils.BASELINE:
                return ProjectUtils.PROJECT_OUTPUTBASELINE_DATA_TRASH;
            case ProjectUtils.BASELINE_REPORT:
                return ProjectUtils.PROJECT_OUTPUTBASELINE_REPORT_TRASH;
            case ProjectUtils.R:
                return ProjectUtils.PROJECT_OUTPUTR_DATA_TRASH;
            case ProjectUtils.R_REPORT:
                return ProjectUtils.PROJECT_OUTPUTR_REPORT_TRASH;
        }
        return null;
    }

    @Override
    public void reset() {
        System.gc();
        setRunningProcess(-1);
    }

    /**
     * Call this function at start
     *
     */
    public void onStart(IProcess pr) {
        // Notify onProcessBegin
        for (IModelListener ml : getModellisteners()) {
            ml.onProcessBegin(pr);
        }
    }

    /**
     * Called in the end of the function
     *
     */
    protected void onEnd(IProcess pr) {
        for (IModelListener ml : getModellisteners()) {
            ml.onProcessEnd(pr);
        }
    }

    @Override
    public int getRunState() {
        return runState;
    }

    @Override
    public void setRunState(int runState) {
        if (this.runState != runState) {
            this.runState = runState;
        }
    }

    protected void run(Boolean inBackGround) {
        if (dirty) {
            // Save when rerunning model if dirty:
            project.save();
            dirty = false;
        }
        if (inBackGround) {
            // A SwingWoker thread is designed to run once, hence instantiate inner class here
            (new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() {
                    runModel();
                    return null;
                }
            }).execute();
        } else {
            runModel();
        }
    }

    @Override
    public boolean isFinished() {
        return getRunningProcessIdx() == getProcessList().size() - 1;
    }

    @Override
    public void run(Integer firstProcess, Integer lastProcess, Boolean inBackGround) {
        this.firstProcess = firstProcess == null ? 0 : firstProcess - 1;
        this.lastProcess = lastProcess == null ? getProcessList().size() - 1 : lastProcess - 1;
        run(inBackGround);
    }

    @Override
    public void save(int level, XMLStreamWriter xmlsw) {
        try {
            Map<String, String> attr = new HashMap<>();
            attr.put("name", getModelName());
            XMLWriter.writeXMLElementStart(level++, xmlsw, "model", attr);
            for (IProcess process : getProcessList()) {
                Map<String, String> attr2 = new HashMap<>();
                attr2.put("name", process.getName());
                XMLWriter.writeXMLElementStart(level++, xmlsw, "process", attr2);
                XMLWriter.writeXMLElement(level, xmlsw, "function", process.getMetaFunction() != null
                        ? process.getMetaFunction().getName() : null);
                XMLWriter.writeXMLElement(level, xmlsw, "enabled", process.isEnabled() != null
                        ? process.isEnabled().toString() : "");
                XMLWriter.writeXMLElement(level, xmlsw, "respondingui", process.isRespondInGUI() != null
                        ? process.isRespondInGUI().toString() : "");
                XMLWriter.writeXMLElement(level, xmlsw, "breakingui", process.isBreakInGUI() != null
                        ? process.isBreakInGUI().toString() : "");
                if (process.getMetaFunction() != null) {
                    if (process.getMetaFunction().getDataStorage() != null) {
                        XMLWriter.writeXMLElement(level, xmlsw, "fileoutput", process.getFileOutput() != null
                                ? process.getFileOutput().toString() : "");
                    }
                    for (IMetaParameter mp : process.getMetaFunction().getMetaParameters()) {
                        Object value = process.getValue(mp);
                        if (value == null && Functions.DT_STRING.equalsIgnoreCase(mp.getDataTypeName())) {
                            continue;
                        }
                        Map<String, String> attr3 = new HashMap<>();
                        attr3.put("name", mp.getName());
                        // Write parameter value as character data with name attribute
                        XMLWriter.writeXMLElementStartWithoutNewline(level, xmlsw, "parameter", attr3);
                        if (value != null) {
                            xmlsw.writeCharacters(value.toString());
                        }
                        xmlsw.writeEndElement();
                        xmlsw.writeCharacters("\n");
                    }
                    if (process.getMetaFunction().getMetaOutput() != null) {
                        XMLWriter.writeXMLElement(level, xmlsw, "output", process.getMetaFunction().getMetaOutput().getDataTypeName());
                    }
                }
                XMLWriter.writeXMLElementEnd(--level, xmlsw); // item
            }
            XMLWriter.writeXMLElementEnd(--level, xmlsw);
            setDirty(false);
        } catch (XMLWriter.StAXWriterException | XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public ILibrary getLibrary() {
        return getProject().getLibrary();
    }

    @Override
    public List<IProcess> getProcessList() {
        return processList;
    }

    @Override
    public void addModellistener(IModelListener listener) {
        modelListeners.add(listener);
    }

    @Override
    public List<IModelListener> getModellisteners() {
        return modelListeners;
    }

    @Override
    public void setModellisteners(List<IModelListener> listeners) {
        modelListeners = listeners;
    }

    @Override
    public Integer getRunningProcessIdx() {
        return runningProcess;
    }

    @Override
    public Integer getNextRunningProcessIdx() {
        for (Integer i = runningProcess + 1; i <= getProcessList().size() - 1; i++) {
            if (getProcessList().get(i).isEnabled()) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public void setRunningProcess(int idx) {
        if (runningProcess != idx) {
            if (idx < runningProcess) {
                // Clear output:
                for (int i = Math.min(runningProcess, getProcessList().size() - 1); i >= Math.max(0, idx); i--) {
                    getProcessList().get(i).clearOutput();
                }
            }
            runningProcess = idx;
            for (IModelListener ml : getModellisteners()) {
                ml.onRunningProcessChanged(this, runningProcess);
            }
        }
    }

    @Override
    public IProcess getRunningProcess() {
        if (runningProcess >= 0 && runningProcess < getProcessList().size()) {
            return getProcessList().get(runningProcess);
        }
        return null;
    }

    @Override
    public String validate() {
        for (IProcess p : getProcessList()) {
            String s = p.validate();
            if (s != null) {
                return p.getName() + ": " + s;
            }
        }
        return null;
    }

    @Override
    public IProcess getProcessByFunctionName(String functionName) {
        for (IProcess p : getProcessList()) {
            if (p.getMetaFunction() != null && p.getMetaFunction().getName().equals(functionName)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public List<IProcess> getProcessesByFunctionName(String functionName) {
        return getProcessList().stream()
                .filter(p -> p.getMetaFunction() != null && p.getMetaFunction().getName().equals(functionName))
                .collect(Collectors.toList());
    }

    @Override
    public String getModelName() {
        return modelName;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public Boolean isBreakable() {
        return breakable;
    }

    @Override
    public void setBreakable(Boolean breakable) {
        this.breakable = breakable;
    }

    @Override
    public boolean isExportCSV() {
        return exportCSV;
    }

    @Override
    public void setExportCSV(Boolean exportCSV) {
        this.exportCSV = exportCSV;
    }

    @Override
    public IProcess getProcessFromName(String n) {
        for (IProcess p : getProcessList()) {
            if (p.getName() != null && p.getName().equals(n)) {
                return p;
            }
        }
        return null;
    }

    @Override
    public IProject getProject() {
        return project;
    }

    @Override
    public void setDirty(boolean dirty) {
        if (readingModel) {
            return;
        }
        if (dirty == this.dirty) {
            return;
        }
        this.dirty = dirty;
    }

    @Override
    public Boolean isDirty() {
        return dirty;
    }

    /*
     Reordering process from a move (cut+paste) operation.
     */
    @Override
    public void reOrder(int[] perm) {
        IProcess[] reordered = new IProcess[processList.size()];
        for (int i = 0; i < perm.length; i++) {
            int j = perm[i];
            IProcess c = processList.get(i);
            reordered[j] = c;
        }
        processList.clear();
        processList.addAll(Arrays.asList(reordered));
        setDirty(true);
    }

    //@Override
    public IProcess createProcess(String processName, String functionName) {
        IProcess pr = new Process(this);
        pr.setProcessName(processName);
        IMetaFunction fnc = getLibrary().findMetaFunction(functionName);
        if (fnc != null) {
            pr.setMetaFunction(fnc);
        }
        return pr;
    }

    @Override
    public IProcess addProcess(String processName, String functionName) {
        return insertProcess(processName, functionName, -1);
    }

    public IProcess insertProcess(String processName, String functionName, Integer iBefore) {
        IProcess pr = createProcess(processName, functionName);
        if (iBefore == -1) {
            getProcessList().add(pr);
        } else {
            getProcessList().add(iBefore, pr);
        }
        return pr;
    }

    @Override
    public boolean isReportModel() {
        return modelName.contains("report");
    }

    @Override
    public String toString() {
        String res = "";
        res += getModelName();
        for (IProcess p : getProcessList()) {
            res += "\n\t" + p.getName();
        }
        return res;
    }

    /**
     * Get all functions tagged with the model name
     *
     * @return
     */
    @Override
    public List<IMetaFunction> getMetaFunctions() {
        List<IMetaFunction> res = new ArrayList<>();
        for (IMetaFunction fnc : getProject().getLibrary().getMetaFunctions()) {
            if (fnc.getCategory().contains(modelName)) {
                res.add(fnc);
            }
        }
        return res;
    }

    public ILogger getProcessLog() {
        return processLog;
    }

    public Integer getWarningLevel() {
        return warningLevel;
    }

    public void setWarningLevel(Integer warningLevel) {
        this.warningLevel = warningLevel;
    }

}
