/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.sea2data.stox.components.table;

import java.util.List;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.table.DefaultTableModel;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.api.IProjectProvider;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.model.IModelListenerService;
import no.imr.stox.model.IProcess;
import no.imr.stox.model.ModelListenerAdapter;
import org.openide.util.Lookup;

/**
 *
 * @author aasmunds
 */
public class CovariatePanel extends javax.swing.JPanel {

    String covFnc, covSourceType;
    String viewCode;

    /**
     * Creates new form CovariatePanel
     */
    public CovariatePanel(String covFnc, String covSourceType) {
        initComponents();
        this.covFnc = covFnc;
        this.covSourceType = covSourceType;
        viewCode = covFnc + "-" + covSourceType;
        IModelListenerService fls = (IModelListenerService) Lookup.getDefault().lookup(IModelListenerService.class);
        fls.getModelListeners().add(new ModelListenerAdapter() {
            @Override
            public void onProcessEnd(IProcess process) {
                switch (process.getMetaFunction().getName()) {
                    case Functions.FN_DEFINETEMPORAL:
                    case Functions.FN_DEFINEGEARFACTOR:
                    case Functions.FN_DEFINESPATIAL:
                        String viewCodeP = process.getMetaFunction().getName() + "-" + process.getParameterValue("SourceType");
                        if (viewCode.equals(viewCodeP)) {
                            fillInCovariates(process.getOutput());
                        }
                }
            }
        });
        /*jTable.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                TableCellEditor tce = jTable.getCellEditor();
                if (tce != null) {
                    tce.stopCellEditing();
                }
            }
        });*/
        //jTable.putClientProperty("terminateEditOnFocusLost", true);
        jTable.getDefaultEditor(String.class).addCellEditorListener(new CellEditorListener() {
            @Override
            public void editingCanceled(ChangeEvent e) {
                editingStopped(e);
            }

            @Override
            public void editingStopped(ChangeEvent e) {
                // Put the table content back to processdata
                updateProcessDataFromTable();
            }

        });
    }

    public String getViewCode() {
        return viewCode;
    }

    private void fillInCovariates(ProcessDataBO pd) {
        DefaultTableModel dt = (DefaultTableModel) jTable.getModel();
        MatrixBO m = null;
        switch (covFnc) {
            case Functions.FN_DEFINETEMPORAL:
                m = AbndEstProcessDataUtil.getTemporal(pd);
                break;
            case Functions.FN_DEFINEGEARFACTOR:
                m = AbndEstProcessDataUtil.getGear(pd);
                break;
            case Functions.FN_DEFINESPATIAL:
                m = AbndEstProcessDataUtil.getSpatial(pd);
                break;
        }
        if (m == null) {
            return;
        }
        MatrixBO mc = m.getRowValueAsMatrix(covSourceType);
        if(mc == null) {
            return;
        }
        dt.setRowCount(mc.getKeys().size());
        List<String> covKeys = m.getSortedRowColKeys(covSourceType);
        for (int iCov = 0; iCov < covKeys.size(); iCov++) {
            String cov = covKeys.get(iCov);
            String def = (String) m.getRowColValue(covSourceType, cov);
            dt.setValueAt(cov, iCov, 0);
            dt.setValueAt(def, iCov, 1);
        }
        /*class CovDefCellRenderer extends DefaultTableCellRenderer {

            @Override
            public Component getTableCellRendererComponent(
                    JTable table, Object value,
                    boolean isSelected, boolean hasFocus,
                    int row, int column) {
                JLabel c = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column); // params from above );
                // This...
                String val = (String) c.getText();
                if (val != null) {
                    c.setToolTipText(IMRdate.formatDate(IMRdate.getPeriodMinMaxDate(val, true)) + " - " + IMRdate.formatDate(IMRdate.getPeriodMinMaxDate(val, false)));
                }
                return c;
            }
        }
        if (covFnc.equals(Functions.COVARIATETYPE_TEMPORAL)) {
            jTable.getColumnModel().getColumn(1).setCellRenderer(new CovDefCellRenderer()); // If your path is of specific class (e.g. java.io.File) you could set the renderer for that type
        }*/
    }

    private void updateProcessDataFromTable() {
        IProjectProvider pp = Lookup.getDefault().lookup(IProjectProvider.class);
        if (pp.getProject() == null) {
            return;
        }
        ProcessDataBO pd = pp.getProject().getProcessData();
        DefaultTableModel dt = (DefaultTableModel) jTable.getModel();
        MatrixBO m = null;
        switch (covFnc) {
            case Functions.FN_DEFINETEMPORAL:
                m = AbndEstProcessDataUtil.getTemporal(pd);
                break;
            case Functions.FN_DEFINEGEARFACTOR:
                m = AbndEstProcessDataUtil.getGear(pd);
                break;
            case Functions.FN_DEFINESPATIAL:
                m = AbndEstProcessDataUtil.getSpatial(pd);
                break;
        }
        if (m == null) {
            return;
        }
        MatrixBO mc = m.getRowValueAsMatrix(covSourceType);
        if (mc != null) {
            mc.clear();
        }
        for (int row = 0; row < dt.getRowCount(); row++) {
            String cov = (String) dt.getValueAt(row, 0);
            String def = (String) dt.getValueAt(row, 1);
            if (cov != null && cov.length() > 0 && def != null && def.length() > 0) {
                m.setRowColValue(covSourceType, cov, def);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable = new javax.swing.JTable();

        jTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null},
                {null, null}
            },
            new String [] {
                "CovariateID", "Definition"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTable.setColumnSelectionAllowed(true);
        jScrollPane1.setViewportView(jTable);
        jTable.getColumnModel().getSelectionModel().setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        if (jTable.getColumnModel().getColumnCount() > 0) {
            jTable.getColumnModel().getColumn(0).setMinWidth(100);
            jTable.getColumnModel().getColumn(0).setPreferredWidth(100);
            jTable.getColumnModel().getColumn(0).setMaxWidth(150);
            jTable.getColumnModel().getColumn(0).setHeaderValue(org.openide.util.NbBundle.getMessage(CovariatePanel.class, "CovariatePanel.jTable.columnModel.title0")); // NOI18N
            jTable.getColumnModel().getColumn(1).setHeaderValue(org.openide.util.NbBundle.getMessage(CovariatePanel.class, "CovariatePanel.jTable.columnModel.title1")); // NOI18N
        }

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTable jTable;
    // End of variables declaration//GEN-END:variables

    void addRow() {
        DefaultTableModel model = (DefaultTableModel) jTable.getModel();
        model.addRow(new Object[]{"", ""});
    }
}
