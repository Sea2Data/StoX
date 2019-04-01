/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.sea2data.stox.components.table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;
import no.imr.stox.api.IProcessViewer;
import no.imr.stox.datastorage.IDataStorage;
import no.imr.stox.model.IProcess;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author aasmunds
 */
@ServiceProvider(service = IProcessViewer.class)
public class ProcessViewerImpl implements IProcessViewer {

    JTabbedPane tabbedPane;
    Map<String, Integer> views = new HashMap<>();

    @Override
    public void setTabbedPane(JTabbedPane tabbedPane) {
        this.tabbedPane = tabbedPane;
    }

    @Override
    public List<String> getOutputList(IProcess p) {
        List<String> res = new ArrayList<>();
        IDataStorage ds = OutputPanel.getDataStorage(p);
        if (ds == null) {
            return null;
        }
        int n = ds.getNumDataStorageFiles();
        if (n == 0) {
            return null;
        }
        if (n == 1) {
            res.add(p.getProcessName());
        } else {
            for (int i = 0; i < n; i++) {
                String postfix = n > 1 ? ds.getStorageFileNamePostFix(i + 1) : "";
                res.add(postfix);
            }
        }
        return res;
    }

    @Override
    public void openProcess(IProcess p, boolean activate, int index) {
        if (p == null || tabbedPane == null) {
            return;
        }
        if (p.getOutput() == null) {
            if (activate) {
                JOptionPane.showMessageDialog(tabbedPane, "Output not available");
            }
            return;
        }
        IDataStorage ds = OutputPanel.getDataStorage(p);
        if (ds == null) {
            return;
        }
        int n = ds.getNumDataStorageFiles();
        if (n == 0) {
            return;
        }
        //Map<Integer, OutputPanel> map = new HashMap<>();
        int first = index == -1 ? 0 : index;
        int last = index == -1 ? n - 1 : index;
        for (int i = first; i <= last; i++) {
            String outputPanelKey = p.getProcessName() + "-" + i;
            Integer idx = views.get(outputPanelKey);
            if (idx == null) {
                idx = tabbedPane.getTabCount();
                views.put(outputPanelKey, idx);
                OutputPanel panel = new OutputPanel(p, i);
                //map.put(idx[i], panel);
                //panel.gtadd(panel, BorderLayout.CENTER);
                String postfix = n > 1 ? ds.getStorageFileNamePostFix(i + 1) : "";
                String name = p.getProcessName() + (n > 1 ? "(" + postfix + ")" : "");
                tabbedPane.addTab(name, panel);
                panel.setTabName(name);
                tabbedPane.setToolTipTextAt(idx, ds.getStorageFileName(i + 1));
                String res = ds.asTable(p.getOutput(), i + 1, true);
                panel.setText(res);
                res = FormattedOutput.formatTable(res);
                panel.getTextArea().setText(res);
                panel.getTextArea().setCaretPosition(0);
            }
            if (activate) {
                tabbedPane.setSelectedIndex(idx);
                tabbedPane.requestFocus();
            }
        }
    }

    @Override
    public void clear() {
        if (tabbedPane == null) {
            return;
        }
        for (int tab = tabbedPane.getTabCount() - 1; tab >= 0; tab--) {
            closeTab(tab);
        }
        /*            tabbedPane.removeAll();
            views.clear();*/
    }

    @Override
    public void closeTab(int tab) {
        if (tabbedPane == null) {
            return;
        }
        if (tab <= tabbedPane.getTabCount() - 1) {
            OutputPanel panel = (OutputPanel) tabbedPane.getComponentAt(tab);
            panel.onClose();
            IProcess p = panel.getProcess();
            if (p != null) {
                views.remove(p.getProcessName());
            }
            tabbedPane.removeTabAt(tab);
        }
    }
}
