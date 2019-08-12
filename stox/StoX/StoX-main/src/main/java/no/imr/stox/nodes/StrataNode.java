package no.imr.stox.nodes;

import java.awt.Image;
import java.awt.event.ActionEvent;
import java.beans.BeanInfo;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import no.imr.stox.util.base.Conversion;
import no.imr.sea2data.stox.providers.ProcessDataProvider;
import no.imr.stox.api.IChildFactorySite;
import no.imr.stox.util.matrix.MatrixBO;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.model.IModel;
import no.imr.stox.model.IProcess;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.actions.Presenter;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Åsmund
 */
public class StrataNode extends AbstractNode {

    private final ProcessDataProvider pdp;
    private final String stratum;
    private final IChildFactorySite psuNodeFactory;
    private final Image errorIcon;

    public StrataNode(IChildFactorySite psuNodeFactory, ProcessDataProvider pdp, Children children, String stratum) {
        super(children, Lookups.singleton(stratum));
        this.pdp = pdp;
        this.stratum = stratum;
        this.psuNodeFactory = psuNodeFactory;
        errorIcon = ImageUtilities.loadImage("images/nb-errorbadge.png");
        setDisplayName(stratum);
        setIconBaseWithExtension("images/nb-sessions.png");
    }

    public void update() {
        setDisplayName(stratum + " (" + getNumberOfPSUs() + ")");
        fireIconChange();
        psuNodeFactory.refresh();
    }

    private int getNumberOfPSUs() {
        MatrixBO psuStrata = AbndEstProcessDataUtil.getPSUStrata(pdp.getPd());
        List<String> keys = psuStrata.getRowKeys();
        return (int) psuStrata.getRowKeys().stream().filter(psu -> stratum.equals(psuStrata.getRowValue(psu))).count();
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public Image getIcon(int type) {
        Image icon = super.getIcon(type);
        if (type == BeanInfo.ICON_COLOR_16x16 || type == BeanInfo.ICON_MONO_16x16) {
            if (!isStrataIncluded()) {
                icon = ImageUtilities.mergeImages(icon, errorIcon, 15, -1);
            }
        }
        return icon;
    }

    @Override
    public Action[] getActions(boolean context) {
        List<Action> actions = new ArrayList<>();
        IProcess p = pdp.getModel() != null ? pdp.getModel().getRunningProcess() : null;
        if (p != null && p.getMetaFunction() != null) {
            switch (p.getMetaFunction().getName()) {
                case Functions.FN_DEFINEACOUSTICPSU:
                    actions.add(new AddTransectAction("Add Transect"));
                    break;
                case Functions.FN_DEFINESTRATA:
                    Boolean include = isStrataIncluded();
                    actions.add(new ToggleIncludeAction((include ? "Ex" : "In") + "clude in total estimate", include));
                    break;
            }
        }
        return actions.toArray(new Action[actions.size()]);
    }

    Boolean isStrataIncluded() {
        String s = (String) AbndEstProcessDataUtil.getStratumPolygons(pdp.getPd()).getRowColValue(stratum, Functions.COL_POLVAR_INCLUDEINTOTAL);
        return s == null ? true : Boolean.valueOf(s);
    }

    final class ToggleIncludeAction extends AbstractAction implements Presenter.Popup {

        Boolean include = false;

        public ToggleIncludeAction(String name, Boolean include) {
            super(name, new ImageIcon(ImageUtilities.loadImage("images/edit-delete-6.png")));
            this.include = include;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            AbndEstProcessDataUtil.getStratumPolygons(pdp.getPd()).setRowColValue(stratum, Functions.COL_POLVAR_INCLUDEINTOTAL, String.valueOf(!include).toLowerCase());
            fireIconChange();
            onFunctionEditedUseProcessData(Functions.FN_DEFINESTRATA, Functions.PM_DEFINESTRATA_USEPROCESSDATA);
        }

        public void onFunctionEditedUseProcessData(String functionName, String parameterName) {
            IModel model = pdp.getModel();
            if (model == null) {
                return;
            }
            IProcess p = model.getProcessByFunctionName(functionName);
            if (p == null) {
                return;
            }
            Boolean useProcessData = (Boolean) p.getActualValue(parameterName);
            if (!useProcessData) {
                p.setParameterValue(parameterName, String.valueOf(true));
            }
        }

        @Override
        public JMenuItem getPopupPresenter() {
            return new JMenuItem(this);
        }

    }

    final class AddTransectAction extends AbstractAction implements Presenter.Popup {

        private AddTransectAction(String name) {
            super(name, new ImageIcon(ImageUtilities.loadImage("images/nb-variables.png")));
        }

        public Integer getLastTransectNumber(List<String> keys) {
            Integer res = 0;
            for (String key : keys) {
                if (key.startsWith("T") && key.length() > 1) {
                    res = Math.max(res, Conversion.safeStringtoIntegerDef(key.substring(1), 0));
                }
            }
            return res;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            MatrixBO psuStrata = AbndEstProcessDataUtil.getPSUStrata(pdp.getPd());
            String psu = "T1";
            List<String> keys = psuStrata.getSortedRowKeys();
            if (keys.size() > 0) {
                psu = "T" + (1 + getLastTransectNumber(keys)); // Skip T in T1, T2, T3...
            }
            final String psutoUse = psu;
            AbndEstProcessDataUtil.setPSUStratum(pdp.getPd(), psu, stratum);
            psuNodeFactory.refresh();
            Node n = pdp.getPSUNodeByKey(psutoUse);
            ProcessDataProvider.selectNode(n);
            update();
            /*if (n != null) {
             try {
             pdp.getEm().setSelectedNodes(new Node[]{n});
             } catch (PropertyVetoException ex) {
             Exceptions.printStackTrace(ex);
             }
             }*/
        }

        @Override
        public JMenuItem getPopupPresenter() {
            return new JMenuItem(this);
        }
    }

    public String getStratum() {
        return stratum;
    }

    class NamePropertySupport extends PropertySupport {

        public NamePropertySupport() {
            super("Name", String.class, "Name", "Name", true, false);
        }

        @Override
        public Object getValue() {
            return stratum;
        }

        @Override
        public void setValue(Object val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        }

    };

    @Override
    protected Sheet createSheet() {
        Sheet sh = Sheet.createDefault();
        Sheet.Set propSet = Sheet.createPropertiesSet();
        propSet.setName("Stratum");
        propSet.setDisplayName("Properties");
        propSet.put(new StrataNode.NamePropertySupport());
        sh.put(propSet);
        return sh;
    }
}
