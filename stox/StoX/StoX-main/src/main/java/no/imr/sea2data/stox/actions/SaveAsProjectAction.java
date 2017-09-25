package no.imr.sea2data.stox.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import no.imr.stox.api.IProjectProvider;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;

/**
 *
 *
 * @author kjetilf
 */
@ActionID(category = "Save As", id = "no.imr.sea2data.stox.actions.SaveAsProjectAction")
@ActionRegistration(iconBase = "no/imr/sea2data/stox/actions/project-save-as.png", displayName = "#CTL_SaveAsProject")
@ActionReferences({
//    @ActionReference(path = "Toolbars/File", position = 40),
    @ActionReference(path = "Menu/File", position = 4)})
public class SaveAsProjectAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent e) {
        IProjectProvider provider = (IProjectProvider) Lookup.getDefault().lookup(IProjectProvider.class);
        provider.saveAsProject();
    }

}
