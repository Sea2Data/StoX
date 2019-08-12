package no.imr.sea2data.stox.components.model;

import BioticTypes.v3.CatchsampleType;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.PropertyEditor;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.GrayFilter;
import no.imr.sea2data.guibase.utils.IMRtooltip;
import no.imr.stox.util.base.Conversion;
import no.imr.sea2data.stox.actions.ActionPopup;
import no.imr.sea2data.stox.editor.CatchabilityPropertyEditor;
import no.imr.sea2data.stox.editor.ProjectFileNameEditor;
import no.imr.sea2data.stox.editor.ListPropertyEditor;
import no.imr.sea2data.stox.editor.SpeciesTSPropertyEditor;
import no.imr.sea2data.stox.editor.TextPropertyEditor;
import no.imr.stox.api.IProcessViewer;
import no.imr.stox.exception.UserErrorException;
import no.imr.stox.functions.biotic.DefineSpecCat;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.ProjectUtils;
import no.imr.stox.functions.utils.ReflectionUtil;
import no.imr.stox.library.IMetaFunction;
import no.imr.stox.library.IMetaParameter;
import no.imr.stox.model.IModel;
import no.imr.stox.model.IProcess;
import no.imr.stox.model.RModel;
import org.openide.actions.DeleteAction;
import org.openide.actions.MoveDownAction;
import org.openide.actions.MoveUpAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 * Node for function
 *
 * @author aasmunds
 */
public class ProcessNode extends AbstractNode {

    private final Image processIcon;
    private final Image processRespondIcon;
    private final Image runIcon;
    private final Image errorIcon;
    private final Image breakIcon;
    private final IProcess process;
    ModelChildFactory modelChildFactory;

    /**
     * Initialise node
     *
     * @param process
     * @throws IntrospectionException
     */
    public ProcessNode(ModelChildFactory modelChildFactory, IProcess process) {
        super(Children.LEAF, Lookups.singleton(process));
        this.process = process;
        //setIconBaseWithExtension("images/nb-debugging.png");
        processIcon = ImageUtilities.loadImage("images/process.png");
        processRespondIcon = ImageUtilities.loadImage("images/process.png"/*"images/processrespond.png"*/);
        runIcon = ImageUtilities.loadImage("images/nb-editbadge2.png");
        errorIcon = ImageUtilities.loadImage("images/nb-errorbadge.png");
        breakIcon = ImageUtilities.loadImage("images/break.png");
        updateDisplayNameAndTooltip();
        this.modelChildFactory = modelChildFactory;
        /*
         setDisplayName(process.getName());
         setShortDescription(process.getName());
         */
    }

    @Override
    public String getShortDescription() {
        /*if (process.getMetaFunction() != null) {
            return "<html><p width=\"350px\">" + process.getMetaFunction().getDescription() + "</p></html>";
        }*/
        return super.getShortDescription(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public Image getIcon(int type) {
        Image img = process.isRespondInGUI() ? processRespondIcon : processIcon;
        if (type == BeanInfo.ICON_COLOR_16x16 || type == BeanInfo.ICON_MONO_16x16) {
            if (process.isPerformed() && process.isEnabled()) {
                img = ImageUtilities.mergeImages(img, runIcon, 16, 8);
            }
            if (process.hasError()) {
                img = ImageUtilities.mergeImages(img, errorIcon, 15, 0);
            }
            if (process.isBreakInGUI()) {
                img = ImageUtilities.mergeImages(img, breakIcon, 0, 0);
            }
            if (!process.isEnabled()) {
                img = GrayFilter.createDisabledImage(img);
            }
        }
        return img;
    }

    private List<Action> createViewOutputActions() {
        IProcessViewer viewer = (IProcessViewer) Lookup.getDefault().lookup(IProcessViewer.class);
        List<Action> res = new ArrayList<>();
        if (viewer != null) {
            List<String> p = viewer.getOutputList(modelChildFactory.getActiveProcess());
            if (p != null) {
                for (int i = 0; i < p.size(); i++) {
                    String s = p.get(i);
                    final int ifin = i;
                    res.add(new AbstractAction(s) {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            viewer.openProcess(modelChildFactory.getActiveProcess(), true, ifin);
                        }
                    });
                }
                if (p.size() > 1) {
                    res.add(new AbstractAction("All") {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            viewer.openProcess(modelChildFactory.getActiveProcess(), true, -1);
                        }
                    });
                }
            }
        }
        return res;
    }

    @Override
    public Action[] getActions(boolean context) {
        List<Action> actions = new ArrayList<>();
        for (Action a : new Action[]{modelChildFactory.getRunFromHereAction(), modelChildFactory.getRunToHereAction(),
            /*modelChildFactory.getViewOutputAction(),*/
            modelChildFactory.getBreakInGUIAction()}) {
            if (a.isEnabled()) {
                actions.add(new ActionPopup(a));
            }
        }

        if (modelChildFactory.isRunnable() && modelChildFactory.getActiveProcess() != null && modelChildFactory.getActiveProcess().getOutput() != null) {
            actions.add(new ActionPopup("View output", createViewOutputActions()));
        }
        actions.add(DeleteAction.get(DeleteAction.class));
        actions.add(MoveUpAction.get(MoveUpAction.class));
        actions.add(MoveDownAction.get(MoveDownAction.class));
        return actions.toArray(new Action[actions.size()]);
    }

    @Override
    public boolean canCut() {
        return true;
    }

    @Override
    public boolean canDestroy() {
        return true;
    }

    @Override
    public void destroy() {
        process.getModel().setDirty(true);
        process.getModel().getProcessList().remove(process.getModel().getProcessList().indexOf((Object) process));
        modelChildFactory.refresh();
    }

    private void updateDisplayNameAndTooltip() {
        setDisplayName(process.getName());
        setValue("nodeDescription", "<html><p width=\"350px\">"
                + (process.getMetaFunction() != null ? process.getMetaFunction().getDescription() : process.getName()) + "</p></html>");
        /*String s = process.getName();
        String err = process.validate();
        if (err != null) {
            s += ", " + err;
        }
        setShortDescription(s);*/
    }

    public void update() {
        updateDisplayNameAndTooltip();
        fireIconChange();
        // Break in gui may change from outside the property window, hence update th property:
        if (!(process.getModel() instanceof RModel)) {
            firePropertyChange("Break in GUI", !process.isRespondInGUI(), process.isRespondInGUI());
            if (process.getMetaFunction() != null && process.getMetaFunction().isRespondable()) {
                firePropertyChange("Respond in GUI", !process.isBreakInGUI(), process.isBreakInGUI());
            }
        }
    }

    public void updateAllNodes() {
        for (Node n : getParentNode().getChildren().getNodes()) {
            ((ProcessNode) n).update();
        }
    }

    static Class getParamClass(IMetaParameter mp) {
        return mp.getMetaDataType().getName().equalsIgnoreCase(Functions.DT_BOOLEAN) ? Boolean.class : String.class;
    }

    /*Determine if parameter is visible*/
    private boolean isParameterVisibleByProcess(IProcess process, IMetaParameter mp) {
        /*if (mp.isParentParameter()) {
            return true;
        }*/
        if (mp.getParentTags() == null || mp.getParentTags().isEmpty()) {
            /*if (mp.getMetaFunction().getName().equals(Functions.FN_ACOUSTICDENSITY)
                    && mp.getName().equals(Functions.PM_ACOUSTICDENSITY_ACOUSTICDATA)) {
                // Hide acsoustic data parameter if d is missing or 0 (hardcoded rule)
                Double d = Conversion.safeObjectToDouble(process.getParameterValue(Functions.PM_ACOUSTICDENSITY_D));
                return d != null && d != 0d;
            }*/
            if (mp.getMetaFunction().getName().equals(Functions.FN_DEFINESTRATA)
                    && mp.getName().equals(Functions.PM_DEFINESTRATA_FILENAME)) {
                // Hide filename if not use processdata
                // NOTE: replace this with a enum value for strata definition method or source
                Boolean b = Conversion.safeObjectToBoolean(process.getParameterValue(Functions.PM_DEFINESTRATA_USEPROCESSDATA));
                return b == null || !b;
            }
            return !mp.isDeprecated();
        }
        for (String tag : mp.getParentTags()) {
            String[] token = tag.split("\\.");
            if (token.length != 2) {
                continue;
            }
            IMetaParameter parentParameter = mp.getMetaFunction().findMetaParameter(token[0]);
            Object value = process.getParameterValue(parentParameter.getName());
            if (value == null) {
                continue;
            }
            if (value.equals(token[1])) {
                return true;
            }
        }
        return false;
    }

    class ParamPropertySupport extends PropertySupport {

        private final IMetaParameter mp;

        public ParamPropertySupport(IMetaParameter mp) {
            super(mp.getName(), getParamClass(mp), mp.getName(), IMRtooltip.wrap(mp.getDescription()), true, true);
            this.mp = mp;
        }

        boolean isReferencingBaselineFromR() {
            return mp != null && mp.getName().equalsIgnoreCase(Functions.PM_RUNBOOTSTRAP_STARTPROCESS) || mp.getName().equalsIgnoreCase(Functions.PM_RUNBOOTSTRAP_ENDPROCESS);
        }

        @Override
        public Object getValue() {
            Object val = process.getValue(mp);
            if (getValueType().equals(Boolean.class)) {
                val = val != null && val instanceof String ? Boolean.valueOf((String) val) : false;
            } else if (mp.getMetaDataType() != null && mp.getMetaDataType().isReference()) {
                val = process.getModel().getProject().findProcess(process.getProcessNameFromParameter(mp));
            } else if (isReferencingBaselineFromR()) {
                val = process.getModel().getProject().findProcess(ProjectUtils.getProcessNameFromParameter((String) val));
            }
            if (val == null) {
                val = ""; // null represented as empty string
            }
            return val;
        }

        @Override
        public void setValue(Object t) {
            if (t != null) {
                if (t instanceof String) {
                    if (((String) t).isEmpty()) {
                        t = null;
                    }
                } else if (t instanceof Boolean) {
                    t = String.valueOf((Boolean) t);
                } else if (t instanceof IProcess) {
                    t = ProjectUtils.PROCESS_START_LITERAL + ((IProcess) t).getName() + ")";
                }
            }
            process.setValue(mp, t);
            // Set the model dirty state - can be i.e. used to select save before exit.
            process.getModel().setDirty(true);
            if (mp.isParentParameter()
                    || mp.getMetaFunction().getName().equals(Functions.FN_ACOUSTICDENSITY) && mp.getName().equals(Functions.PM_ACOUSTICDENSITY_D)
                    || mp.getMetaFunction().getName().equals(Functions.FN_DEFINESTRATA) && mp.getName().equals(Functions.PM_DEFINESTRATA_USEPROCESSDATA)) {
                // Need to refresh if the parameter is a parent (owner) to other parameters
                setSheet(createSheet());
                firePropertySetsChange(null, getPropertySets());
            }
            updateAllNodes();
        }

        @Override
        public PropertyEditor getPropertyEditor() {
            if (mp.getMetaFunction().getName().equals(Functions.FN_SPLITNASC) && mp.getName().equals(Functions.PM_SPLITNASC_SPECIESTS)) {
                return new SpeciesTSPropertyEditor((String) process.getActualValue(Functions.PM_SPLITNASC_SPECIESTS));
            } else if (mp.getMetaFunction().getName().equals(Functions.FN_CATCHABILITY)
                    && (mp.getName().equals(Functions.PM_CATCHABILITY_PARLENGTHDEPENDENTSWEEPWIDTH) || mp.getName().equals(Functions.PM_CATCHABILITY_PARLENGTHDEPENDENTSELECTIVITY))) {
                return new CatchabilityPropertyEditor((String) process.getActualValue(Functions.PM_CATCHABILITY_PARLENGTHDEPENDENTSWEEPWIDTH));
            }
            if (mp.getName().toLowerCase().startsWith("filename") || mp.getName().toLowerCase().startsWith("directory")) {
                String defPath = process.getModel().getProject().getProjectFolder() + "/"
                        + ProjectUtils.PROJECT_INPUT_FOLDER;
                Boolean dirOnly = false;
                switch (process.getMetaFunction().getName()) {
                    case Functions.FN_DEFINESTRATA:
                        defPath = ProjectUtils.getSystemStratumFolder();
                        break;
                    case Functions.FN_APPLYPOSTODATA:
                    case Functions.FN_DEFINETEMPORAL:
                    case Functions.FN_DEFINEGEARFACTOR:
                    case Functions.FN_DEFINESPATIAL:
                    case Functions.FN_DEFINEAGEERRORMATRIX:
                        defPath = ProjectUtils.getSystemReferenceFolder();
                        break;
                    case Functions.FN_WRITEACOUSTICDATATOXML:
                        defPath = process.getModel().getProject().getProjectFolder();
                        dirOnly = true;
                        break;
                }
                return new ProjectFileNameEditor(process.getModel().getProject(), defPath, dirOnly);
            } else if (mp.getValues() != null && !mp.getValues().isEmpty()) {
                return new ListPropertyEditor(mp.getValues());
            } else if (mp.getMetaDataType().isReference() || isReferencingBaselineFromR()) {
                return new ListPropertyEditor(getBackwardCompatibleProcesses(process, mp));
            } else if (process.getMetaFunction().getName().equals(Functions.FN_REDEFINESPECCAT)) {
                switch (mp.getName()) {
                    /*case Functions.PM_REDEFINESPECCAT_SPECVARBIOTIC:
                        return new ListPropertyEditor(ReflectionUtil.getFieldNames(CatchsampleType.class));*/
                    case Functions.PM_REDEFINESPECCAT_SPECVARREF:
                    case Functions.PM_REDEFINESPECCAT_SPECCATREF: {
                        List<String> ls = DefineSpecCat.getHeader(process);
                        if (ls != null) {
                            return new ListPropertyEditor(ls);
                        }
                    }
                }
            }
            PropertyEditor pe = new TextPropertyEditor();
            return pe;

        }

        /**
         * create process where output parameters are compatible with the
         * current process parameter
         *
         * @param process
         * @param mp
         * @return
         */
        private List<IProcess> getBackwardCompatibleProcesses(IProcess process, IMetaParameter mp) {
            List<IProcess> procList = new ArrayList<>();
            if (isReferencingBaselineFromR()) {
                // Refer to baseline processes
                procList.addAll(process.getModel().getProject().getBaseline().getProcessList());
                return procList;
            }

            Boolean isBaselineReport = process.getModel().equals(process.getModel().getProject().getBaselineReport());
            if (isBaselineReport) {
                // Add from the baseline model into the report
                IModel targetmodel = process.getModel().getProject().getBaseline();
                addCompatibleProcesses(targetmodel, process, procList);
            }
            // Add from the process model
            IModel targetmodel = process.getModel();
            addCompatibleProcesses(targetmodel, process, procList);
            return procList;
        }

        private void addCompatibleProcesses(IModel targetmodel, IProcess process, List<IProcess> procList) {
            Integer i = targetmodel.getProcessList().indexOf(process);
            if (i == -1) {
                i = targetmodel.getProcessList().size() - 1;
            }
            for (int j = i - 1; j >= 0; j--) {
                IProcess p = targetmodel.getProcessList().get(j);
                if (p.getMetaFunction() != null && p.getMetaFunction().getMetaOutput() != null
                        && p.getMetaFunction().getMetaOutput().isCompatible(mp)) {
                    procList.add(p);
                }
            }
        }
    };

    static Class getProcessPropertyClass(int prop) {
        switch (prop) {
            case ProcessPropertySupport.PROP_ENABLED:
            case ProcessPropertySupport.PROP_BREAKINGUI:
            case ProcessPropertySupport.PROP_RESPONDINGUI:
            case ProcessPropertySupport.PROP_WRITOUTPUT:
                return Boolean.class;
            default:
                return String.class;

        }
    }

    class ProcessPropertySupport extends PropertySupport {

        public static final int PROP_NAME = 0;
        public static final int PROP_FUNCTION = 1;
        public static final int PROP_FUNCDESC = 2;
        public static final int PROP_FUNCDATATYPE = 3;
        public static final int PROP_FUNCCATEGORY = 4;
        public static final int PROP_ENABLED = 5;
        public static final int PROP_BREAKINGUI = 6;
        public static final int PROP_RESPONDINGUI = 7;
        public static final int PROP_WRITOUTPUT = 8;
        private final IProcess process;
        private final int prop;

        public ProcessPropertySupport(IProcess process, String name, String description, int prop) {
            super(name, getProcessPropertyClass(prop), name, IMRtooltip.wrap(description), true, prop != PROP_FUNCDESC);
            this.process = process;
            this.prop = prop;
        }

        @Override
        public Object getValue() {
            switch (prop) {
                case PROP_NAME:
                    return process.getName();
                case PROP_FUNCTION:
                    return process.getMetaFunction();
                case PROP_FUNCDESC:
                    return process.getMetaFunction() != null ? process.getMetaFunction().getDescription() : null;
                case PROP_FUNCDATATYPE:
                    return process.getMetaFunction() != null ? process.getMetaFunction().getOutputDataTypeName() : null;
                case PROP_FUNCCATEGORY:
                    return process.getMetaFunction() != null ? process.getMetaFunction().getCategory() : null;
                case PROP_ENABLED:
                    return process.isEnabled();
                case PROP_WRITOUTPUT:
                    return process.getFileOutput();
                case PROP_BREAKINGUI:
                    return process.isBreakInGUI();
                case PROP_RESPONDINGUI:
                    return process.isRespondInGUI();
            }
            return null;
        }

        /**
         * Change process parameters by a given process and new process
         * reference
         *
         * @param process
         * @param newProcessRef
         */
        private void changeProcessParametersByProcess(IProcess process, String newProcessRef) {
            String sourceName = process.getProcessName();
            // Fix other parameter references to this process within the project.
            for (IModel m : process.getModel().getProject().getModels().values()) {
                for (IProcess p : m.getProcessList()) {
                    if (p == null || p.equals(this) || p.getMetaFunction() == null) {
                        continue;
                    }
                    for (IMetaParameter mp : p.getMetaFunction().getMetaParameters()) {
                        String pn = p.getProcessNameFromParameter(mp);
                        if (pn != null && pn.equalsIgnoreCase(sourceName)) {
                            p.setValue(mp, ProjectUtils.PROCESS_START_LITERAL + newProcessRef + ")");
                        }
                    }
                }
            }
        }

        @Override
        public void setValue(Object t) {
            switch (prop) {
                case PROP_NAME:
                    if (t == null || t.toString().isEmpty()) {
                        break;
                    }
                    String processName = t.toString();
                    IProcess inUse = process.getModel().getProcessFromName(processName);
                    if (inUse != null) {
                        if (inUse.equals(process)) {
                            return;
                        }
                        try {
                            process.getModel().getProcessLog().error("Cannot rename " + process.getName() + " to " + processName + ". Name in use.", null);
                        } catch (UserErrorException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                    // Update parameter references
                    changeProcessParametersByProcess(process, processName);
                    // Update process name
                    process.setProcessName(processName);
                    // Dirty is set below.
                    updateAllNodes();
                    break;
                case PROP_ENABLED:
                    process.setEnabled((Boolean) t);
                    break;
                case PROP_WRITOUTPUT:
                    process.setFileOutput((Boolean) t);
                    break;
                case PROP_BREAKINGUI:
                    setBreakInGUI((Boolean) t);
                    break;
                case PROP_RESPONDINGUI:
                    process.setRespondInGUI((Boolean) t);
                    break;
                case PROP_FUNCTION:
                    if (t == null || t instanceof IMetaFunction) {
                        process.setMetaFunction((IMetaFunction) t);
                        setSheet(createSheet());
                        firePropertySetsChange(null, getPropertySets());
                        updateAllNodes();
                    }
                    break;
                // case PROP_FUNCDATATYPE: implement!
            }
            process.getModel().setDirty(true);
            fireIconChange();
        }

        @Override
        public PropertyEditor getPropertyEditor() {
            switch (prop) {
                case PROP_FUNCTION:
                    List list = process.getModel().getLibrary().getMetaFunctionsByCategory(process.getModel().getModelName());
                    return new ListPropertyEditor(list);
                default:
                    return super.getPropertyEditor();
            }
        }

        private void setBreakInGUI(Boolean value) {
            process.setBreakInGUI(value);
        }
    };

    @Override
    protected Sheet createSheet() {
        if (process == null) {
            return null;
        }
        Sheet sh = Sheet.createDefault();
        Sheet.Set processPropSet = Sheet.createPropertiesSet();
        processPropSet.setName("Process");
        processPropSet.setDisplayName("Process");
        processPropSet.put(new ProcessPropertySupport(process, "Process name",
                "The user defined name of the current instance of a Function execution.<BR>"
                + "<BR> Note that a Function may be used in several instances within one Model.",
                ProcessPropertySupport.PROP_NAME));
        processPropSet.put(new ProcessPropertySupport(process, "Function", "The formal name of a Function which is utilized as a Process in the current Model.", ProcessPropertySupport.PROP_FUNCTION));
        if (process.getMetaFunction() != null) {
            // processPropSet.put(new ProcessPropertySupport(process, "Function description", ProcessPropertySupport.PROP_FUNCDESC));
            processPropSet.put(new ProcessPropertySupport(process, "Function datatype", "The output data type from this Function.<BR><BR> This output data set is stored in StoX memory and may be used as input data to one or more subsequent Processes.", ProcessPropertySupport.PROP_FUNCDATATYPE));
            processPropSet.put(new ProcessPropertySupport(process, "Function category", "The category of this function in the function library.", ProcessPropertySupport.PROP_FUNCCATEGORY));
        }
        processPropSet.put(new ProcessPropertySupport(process, "Enabled", "Whether to execute this Process or not.", ProcessPropertySupport.PROP_ENABLED));
        if (process.getMetaFunction() != null && process.getMetaFunction().getDataStorage() != null) {
            processPropSet.put(new ProcessPropertySupport(process, "Write to output", "Whether the output data from this Process should be written to one or more files in the Projects &#34;output&#34; folder.<BR><BR> The file content will be a complete or partial representation of the StoX memory output data produced by this Process.", ProcessPropertySupport.PROP_WRITOUTPUT));
        }
        sh.put(processPropSet);
        if (!(process.getModel() instanceof RModel)) {
            Sheet.Set guiPropSet = Sheet.createPropertiesSet();
            guiPropSet.setName("GUI");
            guiPropSet.setDisplayName("GUI");
            guiPropSet.put(new ProcessPropertySupport(process, "Break in GUI", "Whether to temporarily halt execution of the Model after this function has been executed.", ProcessPropertySupport.PROP_BREAKINGUI));
            if (process.getMetaFunction() != null && process.getMetaFunction().isRespondable()) {
                guiPropSet.put(new ProcessPropertySupport(process, "Respond in GUI", "Some Functions produce output data that can be visualized in the GIS window. This option determines whether to display the data in the GIS map or not.", ProcessPropertySupport.PROP_RESPONDINGUI));
            }
            sh.put(guiPropSet);
        }
        Sheet.Set parametersPropSet = Sheet.createPropertiesSet();
        parametersPropSet.setName("Parameters");
        parametersPropSet.setDisplayName("Parameters");
        if (process.getMetaFunction() != null) {
            for (IMetaParameter mp : process.getMetaFunction().getMetaParameters()) {
                if (isParameterVisibleByProcess(process, mp)) {
                    parametersPropSet.put(new ParamPropertySupport(mp));
                }
            }
        }
        sh.put(parametersPropSet);

        return sh;
    }

    public IProcess getProcess() {
        return process;
    }

}
