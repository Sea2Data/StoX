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
        dtd = "-//no.imr.sea2data.stox.components.model//RReport//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "RReportTopComponent",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "navigator", openAtStartup = true, position = 35)
@ActionID(category = "Window", id = "no.imr.sea2data.stox.components.model.RReportTopComponent")
@ActionReference(path = "Menu/Window")
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_RReportAction",
        preferredID = "RReportTopComponent")
public final class RReportTopComponent extends ModelFrameTopComponent {

    public RReportTopComponent() {
        super(ProjectUtils.R_REPORT);
        setName(NbBundle.getMessage(RReportTopComponent.class, "CTL_RReportTopComponent"));
        setToolTipText(NbBundle.getMessage(RReportTopComponent.class, "HINT_RReportTopComponent"));
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
