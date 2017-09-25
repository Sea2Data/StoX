package no.imr.sea2data.stox.components.model;

import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import no.imr.sea2data.stox.actions.ActionPopup;
import no.imr.stox.model.IModel;
import no.imr.stox.model.IModelListenerService;
import no.imr.stox.model.IProcess;
import no.imr.stox.model.ModelListenerAdapter;
import no.imr.stox.model.Process;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Index;
import org.openide.nodes.Node;
import org.openide.util.Lookup;

/**
 *
 * @author Åsmund
 */
public class ModelNode extends AbstractNode {

    private final ModelChildFactory modelChildFactory;

    public ModelNode(ModelChildFactory childFactory) {
        super(Children.create(childFactory, false));
        // Register Node as model listener. (Updating state on icon when run)
        IModelListenerService fls = (IModelListenerService) Lookup.getDefault().lookup(IModelListenerService.class);
        fls.getModelListeners().add(new ModelListenerAdapter() {

            @Override
            public void onRunningProcessChanged(IModel model, Integer runningProcess) {
                if (modelChildFactory.getModel().equals(model)) {
                    updateAllNodes();
                }
            }

            @Override
            public void onProcessChanged(Process process) {
                if (modelChildFactory.getModel().equals(process.getModel())) {
                    // Update the icon and properties on the active process:
                    ProcessNode pn = getProcessNodeByProcess(modelChildFactory.getActiveProcess());
                    if (pn != null) {
                        pn.update();
                    }
                }
            }

        });
        modelChildFactory = childFactory;
        getCookieSet().add(new Index.Support() {
            @Override
            public Node[] getNodes() {
                return getChildren().getNodes(true);
            }

            @Override
            public int getNodesCount() {
                return getNodes().length;
            }

            @Override
            public void reorder(int[] perm) {
                modelChildFactory.getModel().reOrder(perm);
                modelChildFactory.refresh();
                updateAllNodes();
            }
        });
    }

    @Override
    public Action[] getActions(boolean context) {
        if (modelChildFactory.getModel() == null) {
            return null;
        }
        List<Action> actions = new ArrayList<>();
        for (Action a : new Action[]{modelChildFactory.getRunAction(), modelChildFactory.getRunNextAction(),
            modelChildFactory.getTriggerRAction(), modelChildFactory.getAddProcessAction()}) {
            if (a.isEnabled()) {
                actions.add(new ActionPopup(a));
            }
        }
        return actions.toArray(new Action[actions.size()]);
    }

    public ProcessNode getProcessNodeByProcess(IProcess p) {
        if (p == null) {
            return null;
        }
        for (Node n : getChildren().getNodes()) {
            if (((ProcessNode) n).getProcess().equals(p)) {
                return (ProcessNode) n;
            }
        }
        return null;
    }

    public void updateAllNodes() {
        for (Node n : getChildren().getNodes()) {
            ((ProcessNode) n).update();
        }
    }

    public void onProcessEnd(IProcess process) {
        if (process.getModel().equals(modelChildFactory.getModel())) {
            updateAllNodes();
        }
    }

}
