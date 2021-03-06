/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.sea2data.stox.components.table;

import java.awt.Desktop;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import no.imr.stox.util.base.Conversion;
import no.imr.stox.util.base.IMRdate;
import no.imr.stox.api.IProcessViewer;
import no.imr.stox.api.IProjectProvider;
import no.imr.stox.model.IProject;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//no.imr.sea2data.stox.components.table//Output//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "OutputTopComponent",
        iconBase = "images/2-Documents-icon-16.png",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "output", openAtStartup = true)
@ActionID(category = "Window", id = "no.imr.sea2data.stox.components.table.OutputTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_OutputAction",
        preferredID = "OutputTopComponent"
)
@Messages({
    "CTL_OutputAction=Output",
    "CTL_OutputTopComponent=Output Window",
    "HINT_OutputTopComponent=This is a Output window"
})
public final class OutputTopComponent extends TopComponent {

    //Set<String> processViews = new HashSet<>();
    public OutputTopComponent() {
        initComponents();
        setName(Bundle.CTL_OutputTopComponent());
        setToolTipText(Bundle.HINT_OutputTopComponent());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        jToolBar1 = new javax.swing.JToolBar();
        jExcel = new javax.swing.JButton();

        jToolBar1.setFloatable(false);
        jToolBar1.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jToolBar1.setRollover(true);
        jToolBar1.setMinimumSize(new java.awt.Dimension(24, 24));

        jExcel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/Excel-icon.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jExcel, org.openide.util.NbBundle.getMessage(OutputTopComponent.class, "OutputTopComponent.jExcel.text")); // NOI18N
        jExcel.setFocusable(false);
        jExcel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jExcel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jExcel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jExcelActionPerformed(evt);
            }
        });
        jToolBar1.add(jExcel);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jExcelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jExcelActionPerformed
        FileOutputStream out = null;
        try {
            Workbook wb = new HSSFWorkbook();
            for (int iPage = 0; iPage < jTabbedPane1.getTabCount(); iPage++) {
                OutputPanel panel = (OutputPanel) jTabbedPane1.getComponentAt(iPage);
                String text = panel.getText();
                String[] lines = text.split("\n");
                if (lines.length < 60000) {
                    Sheet sh = wb.createSheet(panel.getTabName());
                    for (int i = 0; i < lines.length; i++) {
                        String line = lines[i];
                        String[] cells = line.split("\t");
                        for (int j = 0; j < cells.length; j++) {
                            Cell c = safeCell(sh, i, j);
                            String cell = cells[j];
                            Double d = Conversion.safeStringtoDoubleNULL(cell);
                            if (d != null) {
                                c.setCellValue(d);
                            } else {
                                if (cell.length() > 1000) {
                                    cell = cell.substring(0, 1000);
                                }
                                c.setCellValue(cell);
                            }
                        } // for
                    } // for
                }
            } // for
            String outFile = System.getProperty("java.io.tmpdir") + "/stox_output_"
                    + IMRdate.formatDate(new Date(), "dd-MM-yyyy HH-mm-ss", false) + ".xls";
            out = new FileOutputStream(new File(outFile));
            wb.write(out);
            out.close();
            Desktop.getDesktop().open(new File(outFile));
        } catch (FileNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

    }//GEN-LAST:event_jExcelActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jExcel;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
    private Lookup.Result<IProject> prresult = null;
    private final LookupListener projectListener = new LookupListener() {
        @Override
        public void resultChanged(LookupEvent le) {
            // Refresh the strata nodes
            IProcessViewer pv = Lookup.getDefault().lookup(IProcessViewer.class);
            if (pv != null) {
                pv.clear();
            }
        }
    };

    @Override
    public void componentOpened() {
        // TODO add custom code on component opening
        IProcessViewer pv = Lookup.getDefault().lookup(IProcessViewer.class);
        if (pv != null) {
            pv.setTabbedPane(jTabbedPane1);
        }
        jTabbedPane1.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    JPopupMenu popup = new JPopupMenu();
                    addMenuItem(popup, "Close", (ActionEvent ev) -> {
                        if (jTabbedPane1.getSelectedIndex() >= 0) {
                            pv.closeTab(jTabbedPane1.getSelectedIndex());
                        }
                    });
                    addMenuItem(popup, "Close all", (ActionEvent ev) -> {
                        for (int j = jTabbedPane1.getTabCount() - 1; j >= 0; j--) {
                            pv.closeTab(j);
                        }
                    });
                    addMenuItem(popup, "Close other", (ActionEvent ev) -> {
                        for (int j = jTabbedPane1.getTabCount() - 1; j >= 0; j--) {
                            if (j != jTabbedPane1.getSelectedIndex()) {
                                pv.closeTab(j);
                            }
                        }
                    });
                    popup.show(jTabbedPane1, e.getX(), e.getY());
                }
            }
        });
        IProjectProvider provider = (IProjectProvider) Lookup.getDefault().lookup(IProjectProvider.class);
        prresult = provider.getProjectLookup().lookup(new Lookup.Template(IProject.class));
        prresult.addLookupListener(projectListener);

    }

    public static void addMenuItem(JPopupMenu popup, String item, ActionListener al) {
        JMenuItem menuItem = new JMenuItem(item);
        menuItem.addActionListener(al);
        popup.add(menuItem);
    }

    @Override
    public void componentClosed() {
        // TODO add custom code on component closing
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        /*   p.setProperty("version", "1.0");
         String r = "";
         List<String> l = new ArrayList(processViews);
         for (int i = 0; i < l.size(); i++) {
         r += l.get(i);
         if (i < l.size() - 1) {
         r += ",";
         }
         }
         p.setProperty("view", r);*/

        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        /*String version = p.getProperty("version");
         String r = p.getProperty("view");
         String[] s = r.split(",");
         for (String s1 : s) {
         processViews.add(s1);
         }*/
    }

    protected Row safeRow(Sheet sh, int row) {
        Row r = sh.getRow(row);
        if (r == null) {
            r = sh.createRow(row);
        }
        return r;
    }

    /**
     * <p>
     * safeCell.</p>
     *
     * @param r a {@link org.apache.poi.ss.usermodel.Row} object.
     * @param col a int.
     * @return a {@link org.apache.poi.ss.usermodel.Cell} object.
     */
    protected Cell safeCell(Row r, int col) {
        Cell c = r.getCell(col);
        if (c == null) {
            c = r.createCell(col);
        }
        return c;
    }

    /**
     * <p>
     * safeCell.</p>
     *
     * @param sh a {@link org.apache.poi.ss.usermodel.Sheet} object.
     * @param row a int.
     * @param col a int.
     * @return a {@link org.apache.poi.ss.usermodel.Cell} object.
     */
    protected Cell safeCell(Sheet sh, int row, int col) {
        return safeCell(safeRow(sh, row), col);
    }
}
