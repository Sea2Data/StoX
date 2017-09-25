package no.imr.sea2data.stox.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import no.imr.stox.api.IRunnable;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;

/**
 *
 * @author Åsmund
 */
@ActionID(category = "Run model", id = "no.imr.sea2data.stox.actions.RunAction")
@ActionRegistration(iconBase = "images/run24.png", displayName = "#CTL_RunModel")
@ActionReferences({
    @ActionReference(path = "Toolbars/File", position = 60),
    @ActionReference(path = "Menu/Project", position = 5)})
public class RunAction implements ActionListener {

    private final IRunnable runnable;

    public RunAction(IRunnable runnable) {
        this.runnable = runnable;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        runnable.run();
    }
}
