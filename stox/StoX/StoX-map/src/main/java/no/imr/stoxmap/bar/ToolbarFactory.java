/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stoxmap.bar;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.gui.swing.render2d.control.AbstractMapControlBar;

/**
 *
 * @author aasmunds
 */
public class ToolbarFactory {

    public static JPanel createTopToolbar(JPanel guiMap, List<AbstractMapControlBar> customBars) {
        if (!(guiMap instanceof JMap2D)) {
            return null;
        }
        final GridBagConstraints constraints = new GridBagConstraints();
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 0.0;
        final JPanel panel = new JPanel(new GridBagLayout());

        List<Class> tools = new ArrayList<>();
        tools.add(JNavigationBar.class);
        //tools.add(JSelectionBar.class);
        //tools.add(JEditionBar.class);
        tools.add(JInformationBar.class);
        /*ret.add(JLayerAddBar.class);
         ret.add(PolygonSelectAddBar.class);
         ret.add(TransectBuildBar.class);
         ret.add(BioStationAssignmentBar.class);
         ret.add(StationSelectBar.class);*/
        boolean addedTools = false;
        /*for (Class toolclz : tools) {
            try {
                Object instance = ToolbarFactory.class.getClassLoader().loadClass(toolclz.getName()).newInstance();
                if (instance instanceof AbstractMapControlBar) {
                    AbstractMapControlBar cb = (AbstractMapControlBar) ToolbarFactory.class.getClassLoader().loadClass(toolclz.getName()).getConstructor(JMap2D.class).newInstance((JMap2D) guiMap);
                    if (cb instanceof JNavigationBar) {
                        ((JNavigationBar) cb).setCoordinateViewer(coordViewer);
                    }
                    cb.setFloatable(false);
                    cb.add(new JSeparator(SwingConstants.VERTICAL));
                    panel.add(cb, constraints);
                    addedTools = true;
                }
            } catch (InstantiationException | IllegalAccessException | ClassNotFoundException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
                Exceptions.printStackTrace(ex);
            }
        }*/
        if (customBars != null) {
            for (int i = 0; i < customBars.size(); i++) {
                AbstractMapControlBar bar = customBars.get(i);
                bar.setFloatable(false);
                if (i < customBars.size() - 1) {
                    bar.add(new JSeparator(SwingConstants.VERTICAL));
                }
                panel.add(bar, constraints);
                addedTools = true;
            }
        }

        if (!addedTools) {
            return null;
        }
        constraints.fill = GridBagConstraints.BOTH;
        constraints.anchor = GridBagConstraints.WEST;
        constraints.weightx = 1.0;
        final JToolBar glue = new JToolBar();
        glue.setFloatable(false);
        panel.add(glue, constraints);
        return panel;
    }

    /**
     * Creates a bottom tool bar that contains a JCoordinateBar if guiMap is an
     * instance of JMap2D else it returns null
     *
     * @param guiMap
     * @return
     */
    public static JPanel createBottomToolbar(JPanel guiMap) {
        if (guiMap instanceof JMap2D) {
            final JCoordinateBar coordBar = new JCoordinateBar((JMap2D) guiMap);
            coordBar.setFloatable(false);
            final JPanel panel = new JPanel(new GridBagLayout());
            final GridBagConstraints gbc = new GridBagConstraints();
            gbc.fill = GridBagConstraints.BOTH;
            gbc.weightx = 1;
            gbc.weighty = 1;
            panel.add(coordBar, gbc);
            return panel;
        }
        return null;
    }

}
