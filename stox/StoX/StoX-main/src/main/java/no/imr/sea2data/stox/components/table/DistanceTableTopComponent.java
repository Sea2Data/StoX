/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.sea2data.stox.components.table;

import no.imr.sea2data.stox.providers.DistancePSUHandler;
import java.awt.Color;
import java.awt.Component;
import java.awt.SystemColor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import no.imr.sea2data.stox.providers.ProcessDataProvider;
import no.imr.stox.api.IProcessDataListener;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.model.IModelListenerService;
import no.imr.stox.model.IProcess;
import no.imr.stox.model.ModelListenerAdapter;
import no.imr.stox.nodes.PSUNode;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//no.imr.sea2data.stox.components.table//DistanceTable//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "DistanceTableTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "properties", openAtStartup = true)
@ActionID(category = "Window", id = "no.imr.sea2data.stox.components.table.DistanceTableTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_DistanceTableAction",
        preferredID = "DistanceTableTopComponent"
)
@Messages({
    "CTL_DistanceTableAction=Distance table",
    "CTL_DistanceTableTopComponent=Distance table",
    "HINT_DistanceTableTopComponent=Distance table window"
})
public final class DistanceTableTopComponent extends TopComponent implements IProcessDataListener {

    private Lookup.Result<PSUNode> psuResult;
    DistanceTable table;
    Boolean definingTransect = false;

    public ProcessDataProvider getPDP() {
        return ((ProcessDataProvider) Lookup.getDefault().lookup(ProcessDataProvider.class));
    }

    public DistancePSUHandler getDistPSUHandler() {
        return getPDP().getDistPSUHandler();
    }

    public DistanceTableTopComponent() {
        initComponents();
        setName(Bundle.CTL_DistanceTableTopComponent());
        setToolTipText(Bundle.HINT_DistanceTableTopComponent());
        table = new DistanceTable();
        add(new JScrollPane(table));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        psuResult = Utilities.actionsGlobalContext().lookupResult(PSUNode.class);
        psuResult.addLookupListener(listener);
        Lookup.getDefault().lookup(ProcessDataProvider.class).addProcessDataListener(this);
        // Register Node as model listener. (Updating state on icon when run)
        IModelListenerService fls = (IModelListenerService) Lookup.getDefault().lookup(IModelListenerService.class);
        fls.getModelListeners().add(new ModelListenerAdapter(){

            @Override
            public void onProcessEnd(IProcess process) {
                DistanceTableTopComponent.this.onProcessEnd(process); //To change body of generated methods, choose Tools | Templates.
            }
        });
    }

    @Override
    public void componentClosed() {
        psuResult.removeLookupListener(listener);
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    /**
     * Method that handles actions to be taken when a PSU is selected
     *
     * @param psuNode
     */
    private void onLookupPsuNode(PSUNode psuNode) {
        if(!definingTransect) {
            return;
        }
        String psu = psuNode.getPSU();
        getDistPSUHandler().setSelectedPSU(psu);
        scrollToLastSelected();
        // Repaint table to reflect the state of the selected
        table.repaint();
    }

    /**
     * Private listener that is triggered when changes happends in PSUNode or
     * StrataNode
     */
    private final LookupListener listener = new LookupListener() {

        @Override
        public void resultChanged(LookupEvent le) {
            Collection c = ((Lookup.Result) le.getSource()).allInstances();
            if (!c.isEmpty()) {
                Object node = c.iterator().next();
                if (node instanceof PSUNode) {
                    onLookupPsuNode((PSUNode) c.iterator().next());
                }
            }
        }
    };


    public void onProcessEnd(IProcess process) {
        if (process.isRespondInGUI() && process.getMetaFunction().getName().equals(Functions.FN_DEFINEACOUSTICPSU)) {
            definingTransect = true;
            table.updateTable();
        } else if(definingTransect) {
            definingTransect = false;
            table.updateTable();
        }
    }

    private void scrollToLastSelected() {
        // Scroll table to last item of selected range in the ordered list.
        List<String> dList = getDistPSUHandler().getSelectedDistances(true);
        if (dList.size() > 0) {
            table.scrollToVisible(dList.get(dList.size() - 1));
        }
    }


    @Override
    public void onDistanceTagsChanged(List<String> distances, Boolean on) {
        // Repaint table to reflect the state of the changed items.
        table.repaint();
        List<String> dList = getDistPSUHandler().getSelectedDistances();
        if(dList.size() == 1 && distances.contains(dList.get(0))) {
            // The user has tagged the first distance in a new psu, then scroll.
            scrollToLastSelected();
        }
    }

    private class DistanceTable extends JTable {

        public void updateTable() {
            DefaultTableModel dm = new CustomDefaultTableModel();
            dm.addColumn("Distance");
            dm.addColumn("PSU");
            if (definingTransect) {
                for (Object distance : getDistPSUHandler().getDistanceKeys()) {
                    dm.addRow(new Object[]{distance});
                }
            }
            setModel(dm);
            getColumnModel().getColumn(1).setPreferredWidth(20);
        }

        public DistanceTable() {
            super();
            setDefaultRenderer(Object.class, new CustomTableCellRenderer());

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    if (!e.isPopupTrigger() && definingTransect) {
                        Boolean multiSelect = e.isShiftDown();
                        int clickedDistance = rowAtPoint(e.getPoint());
                        getDistPSUHandler().handleDistanceClick(clickedDistance, multiSelect);
                    }
                }
            });
        }

        private class CustomDefaultTableModel extends DefaultTableModel {

            @Override
            public Class getColumnClass(int mColIndex) {
                int rowIndex = 0;
                Object o = getValueAt(rowIndex, mColIndex);
                if (o == null) {
                    return Object.class;
                } else {
                    return o.getClass();
                }
            }

            @Override
            public Object getValueAt(int row, int column) {
                if (column == 1) {
                    return getDistPSUHandler().getPSUByRow(row);
                }
                return super.getValueAt(row, column); //To change body of generated methods, choose Tools | Templates.
            }
        }

        class CustomTableCellRenderer extends DefaultTableCellRenderer {

            Color selColor;

            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                    boolean hasFocus, int rowIndex, int vColIndex) {
                Component cell = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, rowIndex, vColIndex);
                Boolean selected = getDistPSUHandler().isSelected(rowIndex);//distancePsu.contains((String) value);
                selColor = selected ? new Color(225, 213, 216) : table.getBackground();
                cell.setBackground(selColor);
                // Since the background selectio is overridden, override the foreground to permanent control text color
                cell.setForeground(SystemColor.controlText); 
                return this;
            }
        }

        public void scrollToVisible(String distKey) {
            int rowIndex = getDistPSUHandler().getDistanceIdxByKey(distKey);;
            if (rowIndex == -1) {
                return;
            }
            getDistPSUHandler().setLastClickedDistance(rowIndex);
            setRowSelectionInterval(rowIndex, rowIndex);
            int numRowsAtHalfPage = (int) (getParent().getSize().getHeight() / getRowHeight() * 0.5);
            rowIndex += numRowsAtHalfPage;
            if (rowIndex > getDistPSUHandler().getDistanceKeys().size() - 1) {
                rowIndex = getDistPSUHandler().getDistanceKeys().size() - 1;
            }
            if (rowIndex - numRowsAtHalfPage * 2 >= 0) {
                scrollRectToVisible(getCellRect(rowIndex - numRowsAtHalfPage * 2, 0, true));
            }
            scrollRectToVisible(getCellRect(rowIndex, 0, true));
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    }
}
