package no.imr.sea2data.stox.editor;

import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;
import ca.odell.glazedlists.swing.AutoCompleteSupport;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.openide.explorer.propertysheet.ExPropertyEditor;
import org.openide.explorer.propertysheet.InplaceEditor;
import org.openide.explorer.propertysheet.PropertyEnv;
import org.openide.explorer.propertysheet.PropertyModel;
import org.openide.util.Exceptions;

/**
 * TODO: what does this class do?
 *
 * @author aasmunds
 */
public class ListPropertyEditor extends PropertyEditorSupport implements ExPropertyEditor, InplaceEditor.Factory {

    private ListPropertyInplaceEditor listPropertyInplaceEditor = null;

    public ListPropertyEditor(List list) {
        listPropertyInplaceEditor = new ListPropertyInplaceEditor(list);
    }

    @Override
    public String getAsText() {
        return getValue() != null ? (getValue()).toString() : "";
    }

    @Override
    public void setAsText(String value) {
        setValue(value);
    }

    @Override
    public void attachEnv(PropertyEnv propertyEnv) {
        propertyEnv.registerInplaceEditorFactory(this);
    }

    @Override
    public InplaceEditor getInplaceEditor() {
        return listPropertyInplaceEditor;
    }

    class ListPropertyInplaceEditor implements InplaceEditor {

        private PropertyEditor propertyEditor;
        private PropertyModel propertyModel;
        private final List<ActionListener> actionListeners;
        private final AutoCompleteSupport.AutoCompleteCellEditor<Object> acce;
        private static final int MAXIMUM_ROW_COUNT = 20;

        public ListPropertyInplaceEditor(List list) {
            this.actionListeners = new ArrayList<>();
            EventList<Object> itemList = GlazedLists.eventList(list);
            acce = AutoCompleteSupport.createTableCellEditor(itemList);
            acce.setClickCountToStart(1);
            acce.getAutoCompleteSupport().getComboBox().setBorder(BorderFactory.createEmptyBorder());
            acce.getAutoCompleteSupport().getComboBox().setMaximumRowCount(MAXIMUM_ROW_COUNT);

            acce.getAutoCompleteSupport().getComboBox().addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Object oldValue = ListPropertyEditor.this.getValue();
                    Object newValue = ListPropertyInplaceEditor.this.getValue();
                    if (newValue != oldValue) {
                        setValue(newValue);
                        try {
                            if (getPropertyModel() != null) {
                                getPropertyModel().setValue(newValue);
                            }
                            firePropertyChange();
                        } catch (InvocationTargetException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
            });
        }

        @Override
        public void connect(PropertyEditor propertyEditor, PropertyEnv propertyEnv) {
            this.propertyEditor = propertyEditor;
            reset();
        }

        @Override
        public JComponent getComponent() {
            return acce.getAutoCompleteSupport().getComboBox();
        }

        @Override
        public void clear() {
            propertyEditor = null;
            propertyModel = null;
        }

        @Override
        public Object getValue() {
            return acce.getAutoCompleteSupport().getComboBox().getSelectedItem();
        }

        @Override
        public void setValue(Object object) {
            acce.getAutoCompleteSupport().getComboBox().setSelectedItem(object);
        }

        @Override
        public boolean supportsTextEntry() {
            return false;
        }

        @Override
        public void reset() {
            Object selected = (Object) propertyEditor.getValue();
            if (selected != null) {
                Object sel = acce.getAutoCompleteSupport().getComboBox().getSelectedItem();
                if (sel != selected) {
                    acce.getAutoCompleteSupport().getComboBox().setSelectedItem(selected);
                }
            }
        }

        @Override
        public void addActionListener(ActionListener actionListener) {
            actionListeners.add(actionListener);
        }

        @Override
        public void removeActionListener(ActionListener actionListener) {
            actionListeners.remove(actionListener);
        }

        @Override
        public KeyStroke[] getKeyStrokes() {
            return new KeyStroke[]{};
        }

        @Override
        public PropertyEditor getPropertyEditor() {
            return propertyEditor;
        }

        @Override
        public PropertyModel getPropertyModel() {
            return propertyModel;
        }

        @Override
        public void setPropertyModel(PropertyModel propertyModel) {
            this.propertyModel = propertyModel;
        }

        @Override
        public boolean isKnownComponent(Component component) {
            return component == acce.getAutoCompleteSupport().getComboBox() || acce.getAutoCompleteSupport().getComboBox().isAncestorOf(component);
        }
    }
}
