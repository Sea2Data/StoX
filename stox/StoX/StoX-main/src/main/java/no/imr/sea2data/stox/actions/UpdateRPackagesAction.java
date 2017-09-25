package no.imr.sea2data.stox.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import no.imr.stox.api.IProjectProvider;
import no.imr.stox.dlg.UpdateRPackagesDlg;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;

/**
 *
 * @author Åsmund
 */
@ActionID(category = "Tools", id = "no.imr.sea2data.stox.actions.UpdateRPackagesAction")
@ActionRegistration(displayName = "#CTL_UpdateRPackages")
@ActionReferences({
    @ActionReference(path = "Menu/Tools", position = 201)})
public class UpdateRPackagesAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent event) {
        IProjectProvider provider = (IProjectProvider) Lookup.getDefault().lookup(IProjectProvider.class);
        UpdateRPackagesDlg dlg = new UpdateRPackagesDlg(provider);
        dlg.setVisible(true);
    }
}
