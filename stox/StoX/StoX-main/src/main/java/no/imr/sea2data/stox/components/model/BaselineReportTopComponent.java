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
        dtd = "-//no.imr.sea2data.stox.components.model//BaselineReport//EN",
        autostore = false)
@TopComponent.Description(
        preferredID = "BaselineReportTopComponent",
        persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "navigator", openAtStartup = true, position = 15)
@ActionID(category = "Window", id = "no.imr.sea2data.stox.components.model.BaselineReportTopComponent")
@ActionReference(path = "Menu/Window")
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_BaselineReportAction",
        preferredID = "BaselineReportTopComponent")
public final class BaselineReportTopComponent extends ModelFrameTopComponent {

    public BaselineReportTopComponent() {
        super(ProjectUtils.BASELINE_REPORT);
        setName(NbBundle.getMessage(BaselineReportTopComponent.class, "CTL_BaselineReportTopComponent"));
        setToolTipText(NbBundle.getMessage(BaselineReportTopComponent.class, "HINT_BaselineReportTopComponent"));
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
