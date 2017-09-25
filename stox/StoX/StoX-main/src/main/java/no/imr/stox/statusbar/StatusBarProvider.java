/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.statusbar;

import java.awt.Component;
import java.util.Collection;
import no.imr.stox.model.IModel;
import org.openide.awt.StatusLineElementProvider;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author aasmunds
 */
@ServiceProvider(service = StatusLineElementProvider.class, position=1/*, supersedes = {
   "org.netbeans.progress.module.ProgressVisualizerProvider",
   "org.netbeans.core.ui.notifications.StatusLineElement",
   "org.netbeans.modules.editor.impl.StatusLineFactories$LineColumn",
   "org.netbeans.modules.editor.impl.StatusLineFactories$TypingMode"
        }*/)
public class StatusBarProvider implements StatusLineElementProvider {

    private StatusBarPanel panel;

    public StatusBarProvider() {
        this.panel = new StatusBarPanel();
    }

    @Override
    public Component getStatusLineElement() {
        return panel;
    }

    public static void updateBymodel(IModel model) {
        Collection<? extends StatusLineElementProvider> all
                = Lookup.getDefault().lookupAll(StatusLineElementProvider.class);
        for (StatusLineElementProvider a : all) {
            if (a instanceof StatusBarProvider) {
                StatusBarPanel ele = (StatusBarPanel) ((StatusBarProvider) a).getStatusLineElement();
                ele.doUpdateByModel(model); // or whatever method you need to call
            }
        }
    }
}
