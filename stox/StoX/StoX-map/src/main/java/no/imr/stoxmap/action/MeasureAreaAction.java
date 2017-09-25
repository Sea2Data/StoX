/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stoxmap.action;

import no.imr.stoxmap.handler.AreaHandler;
import java.awt.event.ActionEvent;
import static javax.swing.Action.SHORT_DESCRIPTION;
import static javax.swing.Action.SMALL_ICON;
import org.geotoolkit.gui.swing.render2d.control.AbstractMapAction;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;

public class MeasureAreaAction extends AbstractMapAction {

    public MeasureAreaAction(){
        putValue(SMALL_ICON, IconBundle.getIcon("16_mesure_area"));
        putValue(SHORT_DESCRIPTION, MessageBundle.getString("map_measure_area"));
    }

    @Override
    public void actionPerformed(final ActionEvent arg0) {
        if (map != null ) {
            map.setHandler(new AreaHandler(map));
        }
    }

}
