/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.sea2data.stox.actions;

import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.openide.util.actions.Presenter;

/**
 * Action UI for presenting UI as a popup on a menu
 *
 * @author aasmunds
 */
public class ActionPopup extends AbstractAction implements Presenter.Popup {

    Action action;
    List<Action> subActions;
    String menuText;

    public ActionPopup(String menuText, List<Action> subActions) {
        this.subActions = subActions;
        this.menuText = menuText;
    }

    public ActionPopup(Action ui) {
        //super((String) ui.getValue(Action.NAME), (Icon) ui.getValue(Action.SMALL_ICON));
        this.action = ui;
    }

    @Override
    public JMenuItem getPopupPresenter() {
        JMenuItem m = null;
        if (subActions != null) {
            m = new JMenu(this);
            m.setText(menuText);
            for (Action act : subActions) {
                ((JMenu) m).add(act);
            }
        } else {
            m = new JMenuItem(action);
        }
        m.setToolTipText(null);
        return m;
    }

    @Override
    public Object getValue(String key) {
        // delegate 
        if (action != null) {
            return action.getValue(key); //To change body of generated methods, choose Tools | Templates.
        } else if (key.equals(Action.NAME)) {
            return menuText;
        }
        return null;
    }

    @Override
    public boolean isEnabled() {
        // delegate 
        if (action != null) {
            return action.isEnabled();
        }
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // delegate 
        if (action != null) {
            action.actionPerformed(e);
        }
    }
}
