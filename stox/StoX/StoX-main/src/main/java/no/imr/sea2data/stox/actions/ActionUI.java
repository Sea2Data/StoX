/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.sea2data.stox.actions;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.KeyStroke;
import org.openide.util.ImageUtilities;

/**
 *
 * @author aasmunds
 */
public class ActionUI extends AbstractAction {

    Consumer<ActionEvent> action;
    Supplier<Boolean> enableSup;
    Supplier<String> nameSup; // Dynamic supplier to supply the action with a dynamic name.
    Supplier<String> descrSup; // Dynamic supplier to supply the action with a dynamic name.

    public ActionUI(Consumer<ActionEvent> action, Supplier<Boolean> enableSup, Supplier<String> nameSup, Supplier<String> descrSup) {
        this.action = action;
        this.enableSup = enableSup;
        this.nameSup = nameSup;
        this.descrSup = descrSup;
    }

    public static ActionUI get(String actionCommandKey, String name, String smallIcon, String largeIcon, JComponent anc,
            KeyStroke ks, Integer mnemonic, String descr, Consumer<ActionEvent> action, Supplier<Boolean> enableSup) {
        return get(actionCommandKey, name, smallIcon, largeIcon, anc, ks, mnemonic, descr, action, enableSup, null, null);
    }

    /// new ActionUI("test", "Test", "images/run16.png", "images/run24.png", anc,
    //        KeyStroke.getKeyStroke(KeyEvent.VK_F8, 0), KeyEvent.VK_T, "Testing",
    //        ae -> {/*handle*/}, () -> {return true;}, null)
    public static ActionUI get(String actionCommandKey, String name, String smallIcon, String largeIcon, JComponent anc,
            KeyStroke ks, Integer mnemonic, String descr, Consumer<ActionEvent> action, Supplier<Boolean> enableSup,
            Supplier<String> nameSup, Supplier<String> descrSup) {
        ActionUI ui = new ActionUI(action, enableSup, nameSup, descrSup);
        ui.putValue(Action.ACTION_COMMAND_KEY, actionCommandKey);
        if (name != null) {
            ui.putValue(Action.NAME, name);
        }
        if (smallIcon != null) {
            ui.putValue(Action.SMALL_ICON, (Icon) ImageUtilities.loadImage(smallIcon));
        }
        if (largeIcon != null) {
            ui.putValue(Action.LARGE_ICON_KEY, (Icon) ImageUtilities.loadImage(largeIcon));
        }
        if (ks != null) {
            ui.putValue(Action.ACCELERATOR_KEY, ks);
            anc.getActionMap().put(ui.getValue(Action.ACTION_COMMAND_KEY), ui);
            anc.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT).put(ks, ui.getValue(Action.ACTION_COMMAND_KEY));
        }
        if (mnemonic != null) {
            ui.putValue(Action.MNEMONIC_KEY, KeyEvent.VK_T);
        }
        if (descr != null) {
            ui.putValue(Action.SHORT_DESCRIPTION, descr);
        }
        return ui;
    }

    @Override
    public boolean isEnabled() {
        return enableSup.get();
    }

    @Override
    public Object getValue(String key) {
        switch (key) {
            case Action.NAME:
                if (nameSup != null) {
                    return nameSup.get(); // Delegate to name supplier
                }
            case Action.SHORT_DESCRIPTION:
                if (descrSup != null) {
                    return descrSup.get(); // Delegate to name supplier
                }
        }
        return super.getValue(key); // Use action value.
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        action.accept(e);
    }

    // Call this when run state or selected node state changes, fire prop change to action clients to update dynamic properties.
    public void updateActionFromHandler() {
        boolean ien = isEnabled();
        setEnabled(!ien);
        setEnabled(ien);
        putValue(Action.NAME, getValue(Action.NAME));
        putValue(Action.SHORT_DESCRIPTION, getValue(Action.SHORT_DESCRIPTION));
        //super.firePropertyChange("enabled", null, isEnabled());
        //super.firePropertyChange(Action.NAME, null, getValue(Action.NAME));
    }
}
