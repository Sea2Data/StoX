package no.imr.stox.dlg;

import java.io.File;
import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import no.imr.stox.factory.Factory;
import no.imr.stox.functions.utils.ProjectUtils;

/**
 * Does this work? if not, why do we have a static void main method?
 *
 * @author Åsmund
 */
public class NewProjectDialog extends javax.swing.JDialog {

    /**
     * Creates new form NewProjectDialog
     */
    public NewProjectDialog() {
        super((java.awt.Frame) null, true);
        initComponents();
        jDir.setText(ProjectUtils.getSystemProjectRoot());
        setLocationRelativeTo(null);
        setTitle("New project");
        DefaultListModel lm = new DefaultListModel();
        for (String template : Factory.getAvailableTemplates()) {
            String descr = Factory.getTemplateDescription(template);
            lm.addElement(descr);
        }
        jTemplate.setModel(lm);
        jTemplate.setSelectedIndex(0);
    }
    
    public String getProjectName() {
        return jProjectName.getText();
    }
    
    public String getProjectRoot() {
        return jDir.getText();
    }
    
    public String getTemplate() {
        if (jTemplate.getSelectedIndex() == -1) {
            return null;
        }
        return Factory.getAvailableTemplates().get(jTemplate.getSelectedIndex());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel2 = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jProjectName = new javax.swing.JTextField();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTemplate = new javax.swing.JList();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jDir = new javax.swing.JTextField();
        jButton2 = new javax.swing.JButton();

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(NewProjectDialog.class, "NewProjectDialog.jLabel2.text")); // NOI18N

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(NewProjectDialog.class, "NewProjectDialog.jLabel1.text")); // NOI18N

        jProjectName.setText(org.openide.util.NbBundle.getMessage(NewProjectDialog.class, "NewProjectDialog.jProjectName.text")); // NOI18N

        jTemplate.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_INTERVAL_SELECTION);
        jScrollPane1.setViewportView(jTemplate);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(NewProjectDialog.class, "NewProjectDialog.jLabel3.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton1, org.openide.util.NbBundle.getMessage(NewProjectDialog.class, "NewProjectDialog.jButton1.text")); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(NewProjectDialog.class, "NewProjectDialog.jLabel4.text")); // NOI18N

        jDir.setEditable(false);
        jDir.setText(org.openide.util.NbBundle.getMessage(NewProjectDialog.class, "NewProjectDialog.jDir.text")); // NOI18N

        org.openide.awt.Mnemonics.setLocalizedText(jButton2, org.openide.util.NbBundle.getMessage(NewProjectDialog.class, "NewProjectDialog.jButton2.text")); // NOI18N
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 380, Short.MAX_VALUE)
                    .addComponent(jProjectName, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel3)
                            .addComponent(jLabel4))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jDir)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jProjectName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jDir, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 96, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        setVisible(false);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        final JFileChooser fc = new JFileChooser();
        String s = jDir.getText();
        fc.setAcceptAllFileFilterUsed(false);
        fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fc.setCurrentDirectory(new File(s));
        int returnVal = fc.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            jDir.setText(fc.getSelectedFile().getPath());
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(NewProjectDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(NewProjectDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(NewProjectDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(NewProjectDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                NewProjectDialog dialog = new NewProjectDialog();
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JTextField jDir;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JTextField jProjectName;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList jTemplate;
    // End of variables declaration//GEN-END:variables
}
