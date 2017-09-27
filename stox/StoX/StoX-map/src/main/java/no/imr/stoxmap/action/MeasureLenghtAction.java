/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stoxmap.action;

/*
 *    Geotoolkit - An Open Source Java GIS Toolkit
 *    http://www.geotoolkit.org
 *
 *    (C) 2007 - 2008, Open Source Geospatial Foundation (OSGeo)
 *    (C) 2008 - 2009, Johann Sorel
 *
 *    This library is free software; you can redistribute it and/or
 *    modify it under the terms of the GNU Lesser General Public
 *    License as published by the Free Software Foundation;
 *    version 2.1 of the License.
 *
 *    This library is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *    Lesser General Public License for more details.
 */

import no.imr.stoxmap.handler.LenghtHandler;
import java.awt.event.ActionEvent;
import static javax.swing.Action.SHORT_DESCRIPTION;
import static javax.swing.Action.SMALL_ICON;

import org.geotoolkit.gui.swing.render2d.control.AbstractMapAction;
import org.geotoolkit.gui.swing.resource.IconBundle;
import org.geotoolkit.gui.swing.resource.MessageBundle;

/**
 *
 * @author Johann Sorel (Geomatys)
 * @module pending
 */
public class MeasureLenghtAction extends AbstractMapAction {

    public MeasureLenghtAction(){
        putValue(SMALL_ICON, IconBundle.getIcon("16_mesure_lenght"));
        putValue(SHORT_DESCRIPTION, MessageBundle.getString("map_measure_lenght"));
    }

    @Override
    public void actionPerformed(final ActionEvent arg0) {
        if (map != null ) {
            map.setHandler(new LenghtHandler(map));
        }
    }

}
