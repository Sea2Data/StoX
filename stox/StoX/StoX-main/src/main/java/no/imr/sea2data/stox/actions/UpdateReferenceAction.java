package no.imr.sea2data.stox.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JOptionPane;
import no.imr.sea2data.stox.InstallerUtil;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;

/**
 *
 * @author Åsmund
 */
@ActionID(category = "Tools", id = "no.imr.sea2data.stox.actions.UpdateReferenceAction")
@ActionRegistration(displayName = "#CTL_UpdateReference")
@ActionReferences({
    @ActionReference(path = "Menu/Tools", position = 202)})
public class UpdateReferenceAction implements ActionListener {

    @Override
    public void actionPerformed(ActionEvent event) {
        boolean succeeded = InstallerUtil.retrieveReferenceFromFTP();
        JOptionPane.showMessageDialog(null, "Download reference from FTP " + (succeeded ? "succeeded" : "failed"));
    }
}
