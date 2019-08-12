package no.imr.stox.nodes;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.stox.util.math.Calc;
import no.imr.sea2data.stox.providers.ProcessDataProvider;
import no.imr.stox.api.IChildFactorySite;
import no.imr.stox.util.matrix.MatrixBO;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.EchosounderUtils;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.model.IProcess;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.PropertySupport.ReadOnly;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.actions.Presenter;

/**
 *
 * @author Åsmund
 */
public class PSUNode extends AbstractNode {

    private String psu;
    private final ProcessDataProvider pdp;
    private final IChildFactorySite psuNodeFactory;

    public PSUNode(IChildFactorySite psuNodeFactory, Children children, String psu, ProcessDataProvider pdp) {
        super(children);
        setDisplayName(psu);
        this.psuNodeFactory = psuNodeFactory;
        setIconBaseWithExtension("images/nb-variables.png");
        this.psu = psu;
        this.pdp = pdp;
    }

    public String getPSU() {
        return psu;
    }

    class NamePropertySupport extends PropertySupport {

        public NamePropertySupport() {
            super("Name", String.class, "Name", "Name", true, true);
        }

        @Override
        public Object getValue() {
            return psu;
        }

        @Override
        public void setValue(Object t) {
            if (t == null || ((String) t).isEmpty()) {
                return;
            }
            String newPsu = (String) t;
            // Check if psu already exists 
            MatrixBO psuStrata = AbndEstProcessDataUtil.getPSUStrata(pdp.getPd());
            Object o = psuStrata.getRowValue(newPsu);
            if (o != null) {
                return;
            }
            // Change psu name
            if (psuStrata.getRowKeys().contains(psu)) {
                psuStrata.replaceRowKey(psu, newPsu);
                MatrixBO distPSU = AbndEstProcessDataUtil.getEDSUPSUs(pdp.getPd());
                distPSU.replaceRowValue(psu, newPsu);
            }
            // psu used as assignment in gui
            MatrixBO psuAssignment = AbndEstProcessDataUtil.getSUAssignments(pdp.getPd());
            if (psuAssignment.getRowKeys().contains(psu)) {
//                psuAssignment.replaceRowColValue(psu, newPsu);
                psuAssignment.replaceRowKey(psu, newPsu);
                // Distance assignment not used
                // In gui modus, the psu are used as assignment ids, therefore delete by psu in trawl assignment
            }
            /*          MatrixBO trawlAsg = AbndEstProcessDataUtil.getBioticAssignments(pdp.getPd());
             if (trawlAsg.getRowKeys().contains(psu)) {
             trawlAsg.replaceRowKey(psu, newPsu);
             pdp.replacePSU(psu, newPsu);
             }*/
            String oldPsu = psu;
            PSUNode.this.psu = newPsu;
            PSUNode.this.setDisplayName(newPsu);
            PSUNode.this.fireDisplayNameChange(oldPsu, newPsu);
        }
    };

    class PSUPropertySupport extends ReadOnly {

        public static final int PROP_LENGTH = 0;
        private int prop;

        public PSUPropertySupport(String name, int prop) {
            super(name, String.class, name, name);
            this.prop = prop;
        }

        @Override
        public Object getValue() {
            switch (prop) {
                case PROP_LENGTH: {
                    if (pdp.getPd() == null || pdp.getDistances() == null) {
                        return null;
                    }
                    Collection<String> sourceSUs = EchosounderUtils.getSourceSUs(pdp.getPd(), psu, true);
                    // For each source sample unit
                    // Matrix[ROW~SampleUnit / VAR~Weight]
                    MatrixBO sourceWeights = EchosounderUtils.getWeights(sourceSUs, pdp.getDistances(), pdp.getPd(), true, false);
                    return Calc.roundTo(sourceWeights.getSum(), 2);
                }
            }
            return null;
        }
    };

    @Override
    public Action[] getActions(boolean context) {
        if(pdp.getModel() == null) {
            return new Action[]{};
        }
        IProcess p = pdp.getModel().getRunningProcess();
        List<Action> al = new ArrayList<>();
        if (p != null && p.getMetaFunction() != null) {
            switch (p.getMetaFunction().getName()) {
                case Functions.FN_DEFINEACOUSTICPSU:
                    al.add(new DeleteAction("Delete"));
            }
        }
        IProcess pfa = pdp.getModel().getProcessByFunctionName(Functions.FN_FILTERACOUSTIC);
        List<DistanceBO> dl = pfa != null ? (List<DistanceBO>) pfa.getOutput() : null;
        if(dl != null) {
            al.add(new PSUViewRequestAction("Center in map"));
        }
        return al.toArray(new Action[al.size()]);
    }

    @Override
    protected Sheet createSheet() {
        Sheet sh = Sheet.createDefault();

        Sheet.Set propSet = Sheet.createPropertiesSet();
        propSet.setName("PSU");
        propSet.setDisplayName("Properties");
        propSet.put(new NamePropertySupport());
        propSet.put(new PSUPropertySupport("Length", PSUPropertySupport.PROP_LENGTH));
        sh.put(propSet);

        return sh;
    }

    final class DeleteAction extends AbstractAction implements Presenter.Popup {

        private DeleteAction(String psu) {
            super(psu, new ImageIcon(ImageUtilities.loadImage("images/edit-delete-6.png")));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            MatrixBO psuStrata = AbndEstProcessDataUtil.getPSUStrata(pdp.getPd());
            psuStrata.removeRowKey(psu);
            MatrixBO distPSU = AbndEstProcessDataUtil.getEDSUPSUs(pdp.getPd());
            distPSU.removeRowKeyByRowValue(psu);
            MatrixBO psuAssignment = AbndEstProcessDataUtil.getSUAssignments(pdp.getPd());
            psuAssignment.removeRowKey(psu);
            // Distance assignment not used
            // In gui modus, the psu are used as assignment ids, therefore delete by psu in trawl assignment
            //MatrixBO bstAsg = AbndEstProcessDataUtil.getBioticAssignments(pdp.getPd());
            //bstAsg.removeRowKey(psu);
            pdp.removePSU(psu);

            psuNodeFactory.refresh();
            /*Runnable selectNodeAsyncTask = new Runnable() {
             @Override
             public void run() {
             Node n = null;
             try {
             Thread.sleep(20);
             pdp.getEm().setSelectedNodes(new Node[]{});
             } catch (InterruptedException | PropertyVetoException ex) {
             Exceptions.printStackTrace(ex);
             }
             }
             };
             selectNodeAsyncTask.run();*/
        }

        @Override
        public JMenuItem getPopupPresenter() {
            return new JMenuItem(this);
        }
    }

    final class PSUViewRequestAction extends AbstractAction implements Presenter.Popup {

        private PSUViewRequestAction(String psu) {
            super(psu);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            pdp.firePSUViewRequest(psu);
        }

        @Override
        public JMenuItem getPopupPresenter() {
            return new JMenuItem(this);
        }
    }
}
