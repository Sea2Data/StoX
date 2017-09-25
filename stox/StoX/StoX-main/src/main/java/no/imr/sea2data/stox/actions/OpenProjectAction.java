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
 * Opens a new project.
 *
 * @author kjetilf
 */
@ActionID(category = "Open project", id = "no.imr.sea2data.stox.actions.OpenProjectAction")
@ActionRegistration(iconBase = "no/imr/sea2data/stox/actions/project-open.png", displayName = "#CTL_OpenProject")
@ActionReferences({
    @ActionReference(path = "Toolbars/File", position = 30),
    @ActionReference(path = "Menu/File", position = 2)})
public class OpenProjectAction implements ActionListener {

    @Override
    public void actionPerformed(final ActionEvent actionEvent) {
        IProjectProvider provider = (IProjectProvider) Lookup.getDefault().lookup(IProjectProvider.class);
        provider.openProject();
    }
}
