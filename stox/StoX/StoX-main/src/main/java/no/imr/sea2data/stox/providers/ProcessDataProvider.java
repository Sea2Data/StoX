package no.imr.sea2data.stox.providers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.imr.guibase.node.INodeProvider;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.imrbase.util.ImrSort;
import no.imr.stox.api.IChildFactorySite;
import no.imr.stox.api.IProcessDataListener;
import no.imr.stox.api.IProjectProvider;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.model.IModel;
import no.imr.stox.model.IModelListenerService;
import no.imr.stox.model.IProcess;
import no.imr.stox.model.ModelListenerAdapter;
import no.imr.stox.nodes.PSUNode;
import no.imr.stox.nodes.ProcessDataNode;
import no.imr.stox.nodes.StrataNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ServiceProvider;

/**
 * Create node service provider for process data.
 *
 * @author Åsmund
 */
@ServiceProvider(service = ProcessDataProvider.class)
public class ProcessDataProvider {

    private ProcessDataNode root;
    private List<DistanceBO> distances;
    private final StrataNodeFactory strataNodeFactory = new StrataNodeFactory();
    private final Map<String, PSUNode> psuNodes = new HashMap<>();
    //private final ExplorerManager em = new ExplorerManager();
    private IModel model;
    List<IProcessDataListener> pdListeners = new ArrayList<>();
    DistancePSUHandler distPSUHandler = new DistancePSUHandler(this);
    IProjectProvider provider = null;

    public ProcessDataProvider() {
        provider = (IProjectProvider) Lookup.getDefault().lookup(IProjectProvider.class);
    }

    public PSUNode getPSUNodeByKey(String psu) {
        return psuNodes.get(psu);
    }

    public void selectPSUNodeByKey(String psu) {
        selectNode(getPSUNodeByKey(psu));
    }

    public static void selectNode(Node n) {
        if (n == null) {
            return;
        }
        INodeProvider opv = Utilities.actionsGlobalContext().lookup(INodeProvider.class);
        if (opv != null) {
            opv.select(n);
        }
    }

    public void onProcessEnd(IProcess process) {
        switch (process.getMetaFunction().getName()) {
            case Functions.FN_READPROCESSDATA:
                model = process.getModel();
            // refresh factory to get children created
            // drop
            case Functions.FN_DEFINESTRATA:
            case Functions.FN_DEFINEACOUSTICPSU:
            case Functions.FN_DEFINESWEPTAREAPSU:
                refreshStrataNodes();
                break;
            case Functions.FN_FILTERACOUSTIC:
                distances = (List<DistanceBO>) process.getOutput();
                distPSUHandler.createDistances();
                break;
        }
    }

    public DistancePSUHandler getDistPSUHandler() {
        return distPSUHandler;
    }

    public ProcessDataNode getRoot() {
        if (root == null) {
            // Initiate the model listener and create the roor node.
            IModelListenerService fls = (IModelListenerService) Lookup.getDefault().lookup(IModelListenerService.class);
            fls.getModelListeners().add(new ModelListenerAdapter() {

                @Override
                public void onProcessEnd(IProcess process) {
                    ProcessDataProvider.this.onProcessEnd(process); //To change body of generated methods, choose Tools | Templates.
                }
            });
            root = new ProcessDataNode(Children.create(strataNodeFactory, false), Lookups.singleton(this));
        }
        return root;
    }

    public ProcessDataBO getPd() {
        return provider.getProject() != null ? provider.getProject().getProcessData() : null;
    }

    public List<DistanceBO> getDistances() {
        return distances;
    }

    public void removePSU(String psu) {
        this.psuNodes.remove(psu);
    }

    public void replacePSU(String psu, String newPsu) {
        PSUNode n = psuNodes.get(psu);
        removePSU(psu);
        psuNodes.put(newPsu, n);
    }

    public StrataNode getStrataNodeByKey(String strata) {
        for (Node n : root.getChildren().getNodes()) {
            if (((StrataNode) n).getStratum().equals(strata)) {
                return (StrataNode) n;
            }
        }
        return null;
    }

    /**
     * Factory class for creating strata nodes.
     */
    class StrataNodeFactory extends ChildFactory<String> implements IChildFactorySite {

        public StrataNodeFactory() {
        }

        @Override
        public void refresh() {
            refresh(true);
        }

        @Override
        protected boolean createKeys(List<String> strata) {
            if (getPd() != null) {
                List<String> stratas = new ArrayList<>(AbndEstProcessDataUtil.getStrata(getPd()));
                Collections.sort(stratas, new ImrSort.TranslativeComparator(true));
                strata.addAll(stratas);
            }
            return true;
        }

        @Override
        protected Node createNodeForKey(String stratum) {
            PSUNodeFactory childrenFactory = new PSUNodeFactory(stratum);
            return new StrataNode(childrenFactory, ProcessDataProvider.this, Children.create(childrenFactory, false), stratum);
        }
    }

    class PSUNodeFactory extends ChildFactory<String> implements IChildFactorySite {

        private final String stratum;

        public PSUNodeFactory(String stratum) {
            this.stratum = stratum;
        }

        @Override
        public void refresh() {
            refresh(true);
        }

        @Override
        protected boolean createKeys(List<String> psus) {
            if (getPd() != null) {
                List<String> s = new ArrayList<>(AbndEstProcessDataUtil.getPSUsByStratum(getPd(), stratum));
                Collections.sort(s, new ImrSort.TranslativeComparator(true, true)); // group mode T2, T11, T20..
                psus.addAll(s);
            }
            return true;
        }

        @Override
        protected Node createNodeForKey(String psu) {
            PSUNode n = new PSUNode(this, Children.LEAF, psu, ProcessDataProvider.this);
            psuNodes.put(psu, n);
            return n;
        }
    }

    public void addProcessDataListener(IProcessDataListener pdl) {
        pdListeners.add(pdl);
    }

    public void removeProcessDataListener(IProcessDataListener pdl) {
        pdListeners.remove(pdl);
    }

    public void fireDistanceTagsChanged(List<String> distances, Boolean on) {
        for (IProcessDataListener pdl : pdListeners) {
            pdl.onDistanceTagsChanged(distances, on);
        }
        // When the process data changes, mark the model dirty for save
        model.setDirty(true);
    }

    public void firePSUViewRequest(String psu) {
        for (IProcessDataListener pdl : pdListeners) {
            pdl.onPSUViewRequest(psu);
        }
    }

    public IModel getModel() {
        return model;
    }

    public void refreshStrataNodes() {
        if (strataNodeFactory != null) {
            strataNodeFactory.refresh();
        }
        for (Node n : getRoot().getChildren().getNodes()) {
            ((StrataNode) n).update();
        }

    }
}
