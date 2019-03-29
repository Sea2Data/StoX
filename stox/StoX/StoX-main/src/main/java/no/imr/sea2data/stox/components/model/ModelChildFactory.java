package no.imr.sea2data.stox.components.model;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import no.imr.sea2data.stox.actions.ActionUI;
import no.imr.stox.api.IProcessViewer;
import no.imr.stox.api.IProjectProvider;
import no.imr.stox.exception.UserErrorException;
import no.imr.stox.functions.utils.ProjectUtils;
import no.imr.stox.model.IModel;
import no.imr.stox.model.IProcess;
import no.imr.stox.model.RModel;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 * Child factory for process nodes
 *
 * @author aasmunds
 */
public class ModelChildFactory extends ChildFactory<IProcess> {

    String modelName;
    ActionUI resetAction;
    ActionUI runAction;
    ActionUI runNextAction;
    ActionUI runFromHereAction;
    ActionUI runToHereAction;
//    ActionUI runThisAction;
    ActionUI triggerRAction;
    ActionUI addProcessAction;
//    ActionUI viewOutputAction;
    ActionUI breakInGUIAction;
    JComponent anc;
    private IProcess activeProcess = null;

    public ModelChildFactory(String modelName, JComponent anc) {
        this.modelName = modelName;
        this.anc = anc;
        setupActions();
    }

    public String getModelName() {
        return modelName;
    }

    /**
     * Create keys
     *
     * @param keys keys
     * @return true if succeed
     */
    @Override
    protected boolean createKeys(List<IProcess> keys) {
        if (getModel() != null) {
            keys.addAll(getModel().getProcessList());
        }
        return true;
    }

    public void refresh() {
        this.refresh(true);
    }

    /**
     * create nodes for key.
     *
     * @param process
     * @return node for key.
     */
    @Override
    protected Node createNodeForKey(IProcess process) {
        ProcessNode node = null;
        node = new ProcessNode(this, process);
        return node;
    }

    public IModel getModel() {
        IProjectProvider provider = (IProjectProvider) Lookup.getDefault().lookup(IProjectProvider.class);
        if (provider.getProject() != null) {
            return provider.getProject().getModel(modelName);
        }
        return null;
    }

    private void setupActions() {
        runAction = ActionUI.get("run", null, "images/run16.png", "images/run24.png", anc,
                KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), KeyEvent.VK_R, null,
                ae -> {
                    runAction(null, null, null);
                }, () -> {
                    return isRunnable();
                }, () -> {
                    return getModel() != null && getModel().getRunningProcessIdx() >= 0 && getModel().getRunningProcessIdx()
                    < getModel().getProcessList().size() - 1 ? "Continue" : "Run";
                }, () -> {
                    return getModel() != null && getModel().getRunningProcessIdx() >= 0 && getModel().getRunningProcessIdx()
                    < getModel().getProcessList().size() - 1 ? "Continue model" : "Run model";
                });
        runNextAction = ActionUI.get("runnext", "Run next", "images/runnext16.png", "images/runnext24.png", anc,
                KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0), KeyEvent.VK_U, "Run next process",
                ae -> {
                    runAction(getModel().getNextRunningProcessIdx(), getModel().getNextRunningProcessIdx(), true);
                }, () -> {
                    return isRunnable() && !isRModel()
                    && getModel().getNextRunningProcessIdx() >= 0;
                });
        runFromHereAction = ActionUI.get("runfromhere", "Run from here", "images/runfromhere16.png", "images/runfromhere24.png", anc,
                KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.CTRL_DOWN_MASK), KeyEvent.VK_F, "Run from here",
                ae -> {
                    if (getActiveProcess() != null) {
                        runAction(getActiveProcess().getProcessStep() - 1, getModel().getProcessList().size() - 1, true);
                    }
                }, () -> {
                    return isRunnable() && !isRModel()
                    && getActiveProcess() != null && getActiveProcess().getProcessStep() - 1 < getModel().getRunningProcessIdx() + 2;
                });
        runToHereAction = ActionUI.get("runtohere", null, "images/runto16.png", "images/runto24.png", anc,
                KeyStroke.getKeyStroke(KeyEvent.VK_F4, 0), KeyEvent.VK_T, null,
                ae -> {
                    if (getActiveProcess() != null) {
                        runAction(getActiveProcess().getProcessStep() - 1 > getModel().getRunningProcessIdx() + 1
                                ? getModel().getRunningProcessIdx() + 1 : getActiveProcess().getProcessStep() - 1,
                                getActiveProcess().getProcessStep() - 1, false);
                    }
                }, () -> {
                    return isRunnable() && !isRModel()
                    && getActiveProcess() != null;
                }, () -> {
                    return getActiveProcess() == null || getActiveProcess().getProcessStep() - 1
                    > getModel().getRunningProcessIdx() + 1 ? "Run to here" : "Run this";
                },
                () -> {
                    return getActiveProcess() == null || getActiveProcess().getProcessStep() - 1
                    > getModel().getRunningProcessIdx() + 1 ? "Run to here" : "Run this";
                });
        /*runThisAction = ActionUI.get("runthis", "Run this", null, null, anc,
                null, KeyEvent.VK_H, "Run this",
                ae -> {
                    if (getActiveProcess() != null) {
                        runAction(getActiveProcess().getProcessStep() - 1, getActiveProcess().getProcessStep() - 1, true);
                    }
                }, () -> {
                    return isRunnable() && !isRModel()
                    && getActiveProcess() != null && getActiveProcess().getProcessStep() - 1 <= getModel().getRunningProcessIdx() + 1;
                });*/
        resetAction = ActionUI.get("resetmodel", "Reset", "images/reset16.png", "images/reset24.png", anc,
                KeyStroke.getKeyStroke(KeyEvent.VK_F5, InputEvent.CTRL_DOWN_MASK), KeyEvent.VK_E, "Reset model",
                ae -> {
                    resetmodel();
                }, () -> {
                    return isRunnable()
                    && getModel().getRunningProcessIdx() >= 0;
                });
        breakInGUIAction = ActionUI.get("breakingui", null, "images/processbreaked.png", null, anc,
                KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0), KeyEvent.VK_B, null,
                ae -> {
                    getActiveProcess().setBreakInGUI(!getActiveProcess().isBreakInGUI());
                }, () -> {
                    return isRunnable() && !isRModel() && getActiveProcess() != null;
                },
                () -> {
                    return getActiveProcess().isBreakInGUI() ? "Remove Break in GUI" : "Break in GUI";
                }, null);
        triggerRAction = ActionUI.get("triggerRScript", "Generate R trigger script", null, null, null,
                null, KeyEvent.VK_G, "Generate R trigger script",
                ae -> {
                    generateTriggerAction(ae);
                }, () -> {
                    switch (getModel().getModelName()) {
                        case ProjectUtils.R:
                        case ProjectUtils.R_REPORT:
                            return isRunnable();
                        default:
                            return false;
                    }
                });
        addProcessAction = ActionUI.get("addprocess", "Add process", "images/nb-debugging.png", null, null, null,
                KeyEvent.VK_A, "Add process",
                ae -> {
                    addProcessAction(ae);
                }, () -> {
                    return isRunnable();
                });
        /*viewOutputAction = ActionUI.get("viewoutput", "View output", "images/nb-debugging.png", null, null, null,
                KeyEvent.VK_A, "View output",
                ae -> {
                    viewAction(ae);
                }, () -> {
                    return isRunnable() && getActiveProcess() != null && getActiveProcess().getOutput() != null;
                });*/
    }

    private void runAction(Integer iStart, Integer iStop, Boolean breakable) {
        IModel m = getModel();
        IProjectProvider provider = (IProjectProvider) Lookup.getDefault().lookup(IProjectProvider.class);
        if (m != null && provider != null) {
            try {
                provider.runProject(m, iStart, iStop, breakable);
            } catch (UserErrorException ex) {
                JOptionPane.showMessageDialog(null, "Error when running model: " + ex.getMessage());
            }
        }
    }

    public void generateTriggerAction(ActionEvent e) {
        IProjectProvider provider = (IProjectProvider) Lookup.getDefault().lookup(IProjectProvider.class);
        if (provider.getProject() != null) {
            JFileChooser fileChooser = new JFileChooser();
            if (fileChooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                RModel.generateRTriggerByFilename(getModel(), file.getPath());
            }
        }
    }

    public void addProcessAction(ActionEvent e) {
        IProjectProvider provider = (IProjectProvider) Lookup.getDefault().lookup(IProjectProvider.class);
        if (provider.getProject() != null) {
            int i = 1;
            String n = "";
            do {
                n = "Process_" + i++;
                IProcess pr = getModel().getProcessFromName(n);
                if (pr == null) {
                    break;
                }
            } while (true);
            try {
                IProcess p = no.imr.stox.model.Process.createProcess(getModel(), n);
                getModel().getProcessList().add(p);
                refresh();
            } catch (UserErrorException ex) {
            }
        }
    }

/*    public void viewAction(ActionEvent e) {
        if (getActiveProcess() == null) {
            return;
        }
        IProcessViewer viewer = (IProcessViewer) Lookup.getDefault().lookup(IProcessViewer.class);
        if (viewer != null) {
            viewer.openProcess(getActiveProcess(), true);
        }
    }*/

    public ActionUI getRunAction() {
        return runAction;
    }

    public ActionUI getResetAction() {
        return resetAction;
    }

    public ActionUI getRunNextAction() {
        return runNextAction;
    }

    public ActionUI getRunFromHereAction() {
        return runFromHereAction;
    }

    /*    public ActionUI getRunThisAction() {
        return runThisAction;
    }*/
    public ActionUI getRunToHereAction() {
        return runToHereAction;
    }

    public ActionUI getTriggerRAction() {
        return triggerRAction;
    }

    public ActionUI getAddProcessAction() {
        return addProcessAction;
    }

  /*  public ActionUI getViewOutputAction() {
        return viewOutputAction;
    }*/

    public ActionUI getBreakInGUIAction() {
        return breakInGUIAction;
    }

    void setActiveProcess(IProcess process) {
        activeProcess = process;
    }

    public IProcess getActiveProcess() {
        return activeProcess;
    }

    private void resetmodel() {
        getModel().reset();
    }

    public boolean isRunnable() {
        return getModel() != null && getModel().getRunState() == IModel.RUNSTATE_STOPPED;
    }

    private boolean isRModel() {
        return getModel() instanceof RModel;
    }

}
