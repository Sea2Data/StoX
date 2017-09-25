package no.imr.sea2data.stox.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import no.imr.stox.api.IProjectProvider;
import no.imr.stox.dlg.OptionsDlg;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;

/**
 *
 * @author Åsmund
 */
@ActionID(category = "Tools", id = "no.imr.sea2data.stox.actions.StoXOptionsAction")
@ActionRegistration(displayName = "#CTL_StoXOptions")
@ActionReferences({
    @ActionReference(path = "Menu/Tools", position = 200)})
public class StoXOptionsAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent event) {
        IProjectProvider provider = (IProjectProvider) Lookup.getDefault().lookup(IProjectProvider.class);
        OptionsDlg dlg = new OptionsDlg(provider);
        dlg.setVisible(true);
    }
}
