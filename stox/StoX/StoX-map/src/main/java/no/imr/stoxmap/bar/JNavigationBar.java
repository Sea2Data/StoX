/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stoxmap.bar;
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

import no.imr.sea2data.stox.mapgui.StoXMapSetup;
import no.imr.stoxmap.action.PanAction;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.gui.swing.render2d.control.AbstractMapControlBar;
import org.geotoolkit.gui.swing.render2d.control.navigation.NavigateToAction;
import org.geotoolkit.gui.swing.render2d.control.navigation.ZoomAllAction;
import org.geotoolkit.gui.swing.render2d.control.navigation.ZoomInAction;
import org.geotoolkit.gui.swing.render2d.control.navigation.ZoomOutAction;
import org.geotoolkit.gui.swing.render2d.control.navigation.RefreshAction;
import no.imr.stoxmap.utils.ICoordinateViewer;

/**
 * JMap2DControlBar is a JPanel to handle Navigation state for a NavigableMap2D
 * ZoomIn/Out, pan, selection, refresh ...
 *
 * @author johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class JNavigationBar extends AbstractMapControlBar{

    private final ZoomAllAction actionZoomAll;
    private final ZoomInAction actionZoomIn;
    private final ZoomOutAction actionZoomOut;
    private final PanAction actionZoomPan;
    private final RefreshAction actionRefresh;
    private final NavigateToAction actionNavto;
    ICoordinateViewer cv;
    StoXMapSetup setup;
    /**
     * Creates a new instance of JMap2DControlBar
     */
    public JNavigationBar(StoXMapSetup setup) {
        this(null, setup);
    }

    /**
     * Creates a new instance of JMap2DControlBar
     * @param pane : related Map2D or null
     */
    public JNavigationBar(final JMap2D pane,StoXMapSetup setup) {
        actionZoomAll = new ZoomAllAction();
        actionZoomIn = new ZoomInAction();
        actionZoomOut = new ZoomOutAction();
        actionZoomPan = new PanAction(setup);
        actionRefresh = new RefreshAction();
        actionNavto = new NavigateToAction();
        
       // add(actionZoomAll);
      //  add(actionRefresh);
        add(actionZoomPan);
        add(actionZoomIn);
        add(actionZoomOut);
        //add(actionNavto);
        setMap(pane);
    }

    /**
     * set the related Map2D
     * @param map2d : related Map2D
     */
    @Override
    public void setMap(final JMap2D map2d) {
        super.setMap(map2d);
        actionRefresh.setMap(map);
        actionZoomAll.setMap(map);
        actionZoomIn.setMap(map);
        actionZoomOut.setMap(map);
        actionZoomPan.setMap(map);
        actionNavto.setMap(map);
    }
    public void setCoordinateViewer(ICoordinateViewer cv) {
        this.cv = cv;
    }
}
