/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stoxmap.action;

import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import no.imr.sea2data.stox.mapgui.StoXMapSetup;
import org.geotoolkit.gui.swing.render2d.control.AbstractMapAction;

/**
 *
 * @author aasmunds
 */
public class AcousticAbsenceAction extends AbstractMapAction {

    StoXMapSetup setup;
    boolean absence = false;

    public AcousticAbsenceAction(StoXMapSetup setup) {
        this.setup = setup;
        updateImage();
        putValue(SHORT_DESCRIPTION, "Select distinct symbols for acoustic absence/presence");
        setEnabled(false);
    }

    private void updateImage() {
        putValue(SMALL_ICON, new ImageIcon(AcousticAbsenceAction.class.getResource("/no/imr/sea2data/stox/icon/Acoustic" + (absence ? "Pre" : "Ab") + "sence.png")));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        absence = !absence;
        updateImage();
        setup.updateAcousticFeatures(absence);
        map.getCanvas().repaint();
    }

}
