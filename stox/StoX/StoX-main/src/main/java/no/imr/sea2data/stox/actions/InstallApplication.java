/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.sea2data.stox.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import no.imr.guibase.dialogs.AppCatDlg;
import no.imr.sea2data.stox.Installer;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;

/**
 *
 * @author aasmunds
 */
@ActionID(category = "Tools",
        id = "no.imr.sea2data.stox.actions.InstallApplication")
@ActionRegistration(displayName = "#CTL_InstallApplication")
@ActionReferences({ 
    @ActionReference(path = "Menu/Tools", position = 203)//,
    //@ActionReference(path = "Shortcuts", name = "CA-M")
})

public class InstallApplication implements ActionListener {


    public InstallApplication() {
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        AppCatDlg dlg = new AppCatDlg("stox", Installer.APPVERSION);
        dlg.setVisible(true);
    }
}
