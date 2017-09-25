package no.imr.sea2data.stox.actions;

import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.awt.DynamicMenuContent;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.CallableSystemAction;

/**
 *
 * @author Åsmund
 */
@ActionID(category = "Recent projects", id = "no.imr.sea2data.stox.actions.MRUFilesAction")
@ActionRegistration(displayName = "#CTL_MRUFiles")
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 300)})
public final class MRUFilesAction extends CallableSystemAction {

    /**
     * {@inheritDoc} do nothing
     */
    @Override
    public void performAction() {
        // do nothing
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean asynchronous() {
        return false;
    }

    /**
     * {@inheritDoc} Overide to provide SubMenu for MRUFiles (Most Recently Used
     * Files)
     */
    @Override
    public JMenuItem getMenuPresenter() {
        return new MRUFilesMenu(getName());
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(MRUFilesAction.class, "CTL_MRUFilesAction");
    }

    @Override
    public HelpCtx getHelpCtx() {
        return HelpCtx.DEFAULT_HELP;
    }

    class MRUFilesMenu extends JMenu implements DynamicMenuContent {

        public MRUFilesMenu(String s) {
            super(s);

            MRUFilesOptions opts = MRUFilesOptions.getInstance();
            opts.addPropertyChangeListener(new PropertyChangeListener() {

                @Override
                public void propertyChange(PropertyChangeEvent evt) {
                    if (!evt.getPropertyName().equals(MRUFilesOptions.MRU_FILE_LIST_PROPERTY)) {
                        return;
                    }
                    updateMenu();
                }
            });

            updateMenu();
        }

        @Override
        public JComponent[] getMenuPresenters() {
            return getMenuPresenters();
        }

        @Override
        public JComponent[] synchMenuPresenters(JComponent[] items) {
            return getMenuPresenters();
        }

        private void updateMenu() {
            removeAll();
            MRUFilesOptions opts = MRUFilesOptions.getInstance();
            List<String> list = opts.getMRUFileList();
            for (String name : list) {
                Action action = createAction(name);
                action.putValue(Action.NAME, name);
                JMenuItem menuItem = new JMenuItem(action);
                add(menuItem);
            }
        }

        private Action createAction(String actionCommand) {
            Action action = new AbstractAction() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    menuItemActionPerformed(e);
                }
            };

            action.putValue(Action.ACTION_COMMAND_KEY, actionCommand);
            return action;
        }

        private void menuItemActionPerformed(ActionEvent evt) {
            String command = evt.getActionCommand();
            File file = new File(command);
        }

    }
}
