/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.sea2data.stox.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenuItem;
import org.openide.util.actions.Presenter;

/**
 * Action UI for presenting UI as a popup on a menu
 *
 * @author aasmunds
 */
public class ActionPopup extends AbstractAction implements Presenter.Popup {
    
    Action action;
    
    public ActionPopup(Action ui) {
        //super((String) ui.getValue(Action.NAME), (Icon) ui.getValue(Action.SMALL_ICON));
        this.action = ui;
    }
    
    @Override
    public JMenuItem getPopupPresenter() {
        JMenuItem i = new JMenuItem(action);
        i.setToolTipText(null);
        return i;
    }
    
    @Override
    public Object getValue(String key) {
        // delegate 
        return action.getValue(key); //To change body of generated methods, choose Tools | Templates.
    }
    
    @Override
    public boolean isEnabled() {
        // delegate 
        return action.isEnabled();        
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // delegate 
        action.actionPerformed(e);
    }
    
}
