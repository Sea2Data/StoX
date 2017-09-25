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
 * @author kjetilf
 */
@ActionID(category = "Save", id = "no.imr.sea2data.stox.actions.SaveProjectAction")
@ActionRegistration(iconBase = "no/imr/sea2data/stox/actions/project-save.png", displayName = "#CTL_SaveProject")
@ActionReferences({
    @ActionReference(path = "Toolbars/File", position = 50),
    @ActionReference(path = "Menu/File", position = 3)})
public class SaveProjectAction implements ActionListener {

    @Override
    public void actionPerformed(final ActionEvent event) {
        IProjectProvider provider = (IProjectProvider) Lookup.getDefault().lookup(IProjectProvider.class);
        provider.saveProject();
    }
}
