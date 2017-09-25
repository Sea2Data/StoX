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
        dtd = "-//no.imr.sea2data.stox.components.model//Baseline//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "BaselineTopComponent",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "navigator", openAtStartup = true, position = 10)
@ActionID(category = "Window", id = "no.imr.sea2data.stox.components.model.BaselineTopComponent")
@ActionReference(path = "Menu/Window")
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_BaselineAction",
        preferredID = "BaselineTopComponent")
public final class BaselineTopComponent extends ModelFrameTopComponent {

    public BaselineTopComponent() {
        super(ProjectUtils.BASELINE);
        setName(NbBundle.getMessage(BaselineTopComponent.class, "CTL_BaselineTopComponent"));
        setToolTipText(NbBundle.getMessage(BaselineTopComponent.class, "HINT_BaselineTopComponent"));
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
