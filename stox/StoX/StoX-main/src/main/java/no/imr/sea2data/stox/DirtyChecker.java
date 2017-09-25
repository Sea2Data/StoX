/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.sea2data.stox;

import no.imr.stox.api.IProjectProvider;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Lookup;

/**
 *
 * @author aasmunds
 */
public class DirtyChecker {

    public static boolean canContinueIfDirty() {
        IProjectProvider provider = (IProjectProvider) Lookup.getDefault().lookup(IProjectProvider.class);
        if (provider != null && provider.getProject() != null && provider.getProject().isDirty()) {
            NotifyDescriptor nd = new NotifyDescriptor(
                    "You have changed the model. Press <Yes> to save. Press <No> to continue without saving.", // instance of your panel
                    "Save project", // title of the dialog
                    NotifyDescriptor.YES_NO_CANCEL_OPTION, // it is Yes/No dialog ...
                    NotifyDescriptor.QUESTION_MESSAGE, // ... of a question type => a question mark icon
                    null,
                    NotifyDescriptor.YES_OPTION // default option is "Yes"
            );

            Object res = DialogDisplayer.getDefault().notify(nd);
            if (res == NotifyDescriptor.YES_OPTION) {
                provider.getProject().save();
            } else {
                return res == NotifyDescriptor.NO_OPTION;
            }
        }
        return true;

    }
}
