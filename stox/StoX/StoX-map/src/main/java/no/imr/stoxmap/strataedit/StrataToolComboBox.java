/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stoxmap.strataedit;

import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import org.geotoolkit.gui.swing.render2d.control.edition.EditionTool;
import org.geotoolkit.gui.swing.render2d.control.edition.PolygonHoleCreationTool;
import org.geotoolkit.gui.swing.render2d.control.edition.PolygonHoleDeleteTool;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

/**
 *
 * @author aasmunds
 */
public class StrataToolComboBox extends JList {

    private Object edited = null;

    public StrataToolComboBox() {
        setCellRenderer(new ToolRenderer());
    }

    public Object getEdited() {
        return edited;
    }

    public void setEdited(Object edited) {
        this.edited = edited;
        reloadModel();
    }

    public EditionTool getSelectedItem() {
        return (EditionTool) super.getSelectedValue();
    }

    private void reloadModel() {
        final List<EditionTool> tools = (List) Arrays.asList(
                new StrataPolygonCreationTool(),
                new StrataGeometryNodeTool(),
                new PolygonHoleCreationTool(),
                new PolygonHoleDeleteTool());//EditionTools.getTools();
        final List<EditionTool> validTools = new ArrayList<>();

        for (final EditionTool candidate : tools) {
            if (candidate.canHandle(edited)) {
                validTools.add(candidate);
            }
        }

        setModel(new ListComboBoxModel(validTools));
    }

    public class ToolRenderer extends DefaultListCellRenderer {

        @Override
        public Component getListCellRendererComponent(final JList list, final Object value, final int index, final boolean isSelected, final boolean cellHasFocus) {
            final JLabel lbl = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            lbl.setIcon(null);

            if (value instanceof EditionTool) {
                final EditionTool tool = (EditionTool) value;
                lbl.setText(tool.getTitle().toString());
                lbl.setToolTipText(tool.getAbstract().toString());
                lbl.setIcon(tool.getIcon());
            }

            return lbl;
        }

    }

}
