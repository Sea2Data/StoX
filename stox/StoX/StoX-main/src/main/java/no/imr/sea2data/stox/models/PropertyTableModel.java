package no.imr.sea2data.stox.models;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.swing.table.AbstractTableModel;
import org.openide.util.NbBundle;

/**
 * Table model for property data. Thiss just a simple implementation for this
 * specific case.
 *
 * @author kjetilf
 */
public class PropertyTableModel extends AbstractTableModel {

    /**
     * Properties object.
     */
    private Properties properties;

    /**
     * All keys in the properties object.
     */
    private final List<String> propertyKey = new ArrayList<String>(100);

    /**
     * Initialize.
     */
    public PropertyTableModel() {
        refresh();
    }

    @Override
    public int getRowCount() {
        return this.properties.size();
    }

    @Override
    public int getColumnCount() {
        return 2;
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columnIndex != 0;
    }

    @Override
    public String getColumnName(int column) {
        String colName;
        if (column == 0) {
            colName = NbBundle.getMessage(PropertyTableModel.class, "property.key");
        } else {
            colName = NbBundle.getMessage(PropertyTableModel.class, "property.value");
        }
        return colName;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (columnIndex == 0) {
            return this.propertyKey.get(rowIndex);
        } else {
            return this.properties.get(this.propertyKey.get(rowIndex));
        }
    }

    /**
     * Refresh the data.
     */
    private void refresh() {
        // refresh data
    }

}
