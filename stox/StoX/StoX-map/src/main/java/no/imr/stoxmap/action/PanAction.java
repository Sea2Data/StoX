/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stoxmap.action;

import no.imr.stoxmap.handler.PanHandler;
import java.awt.event.ActionEvent;
import static javax.swing.Action.SHORT_DESCRIPTION;
import static javax.swing.Action.SMALL_ICON;
import javax.swing.ImageIcon;
import no.imr.sea2data.stox.mapgui.StoXMapSetup;

import org.geotoolkit.gui.swing.render2d.control.AbstractMapAction;
import org.geotoolkit.gui.swing.resource.FontAwesomeIcons;
import org.geotoolkit.gui.swing.resource.IconBuilder;
import org.geotoolkit.gui.swing.resource.MessageBundle;
import org.geotoolkit.gui.swing.render2d.control.navigation.AbstractNavigationHandler;

/**
 * @author johann sorel (Puzzle-GIS)
 * @module pending
 */
public class PanAction extends AbstractMapAction {

    public static final ImageIcon ICON = IconBuilder.createIcon("\uf047", 16, FontAwesomeIcons.DEFAULT_COLOR);

    StoXMapSetup setup;
    AbstractNavigationHandler handler;

    public PanAction(StoXMapSetup setup) {
        this(false, setup);
    }

    public PanAction(boolean infoOnClick, StoXMapSetup setup) {
        this.setup = setup;
        putValue(SMALL_ICON, ICON);
        putValue(SHORT_DESCRIPTION, MessageBundle.getString("map_pan"));
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void actionPerformed(final ActionEvent arg0) {
        if (map != null) {
            if (handler == null) {
                handler = new PanHandler(setup);
            }
            map.setHandler(handler);
        }
    }

}
