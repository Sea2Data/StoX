/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.sea2data.stox.components.model;

import no.imr.stox.functions.utils.ProjectUtils;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;

/**
 *
 * @author aasmunds
 */
@ConvertAsProperties(
        dtd = "-//no.imr.sea2data.stox.components.model//R//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "RTopComponent",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "navigator", openAtStartup = true, position = 25)
@ActionID(category = "Window", id = "no.imr.sea2data.stox.components.model.RTopComponent")
@ActionReference(path = "Menu/Window")
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_RAction",
        preferredID = "RTopComponent")
public final class RTopComponent extends ModelFrameTopComponent {

    public RTopComponent() {
        super(ProjectUtils.R);
        setName(NbBundle.getMessage(RTopComponent.class, "CTL_RTopComponent"));
        setToolTipText(NbBundle.getMessage(RTopComponent.class, "HINT_RTopComponent"));
    }

    /**
     * Required top component method.
     *
     * @param p Property object.
     */
    void writeProperties(final java.util.Properties p) {
    }

    /**
     * Required top component method.
     *
     * @param p Property object.
     */
    void readProperties(final java.util.Properties p) {
    }

}
