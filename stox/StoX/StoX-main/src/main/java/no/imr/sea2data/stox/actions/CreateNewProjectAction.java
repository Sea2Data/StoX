package no.imr.sea2data.stox.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import no.imr.sea2data.stox.components.project.ProjectFrameTopComponent;
import no.imr.stox.api.IProjectProvider;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.windows.WindowManager;

/**
 * Creates an empty new project.
 *
 * @author kjetilf
 */
@ActionID(category = "Create new", id = "no.imr.sea2data.stox.actions.CreateNewProjectAction")
@ActionRegistration(iconBase = "no/imr/sea2data/stox/actions/project-new.png", displayName = "#CTL_CreateNewProject")
@ActionReferences({
    @ActionReference(path = "Toolbars/File", position = 20),
    @ActionReference(path = "Menu/File", position = 1)})
public class CreateNewProjectAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent event) {
        IProjectProvider provider = (IProjectProvider) Lookup.getDefault().lookup(IProjectProvider.class);
        provider.newProject();
        ProjectFrameTopComponent component = (ProjectFrameTopComponent) WindowManager.getDefault().findTopComponent("ProjectFrameTopComponent");
        component.refreshProperties();
    }
}
