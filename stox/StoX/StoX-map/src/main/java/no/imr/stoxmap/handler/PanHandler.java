/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stoxmap.handler;

/**
 *
 * @author aasmunds /* Geotoolkit - An Open Source Java GIS Toolkit
 * http://www.geotoolkit.org
 *
 * (C) 2014, Geomatys
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; version 2.1 of the License.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
import com.vividsolutions.jts.geom.Coordinate;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.event.MouseInputListener;
import no.imr.sea2data.stox.mapgui.StoXMapSetup;
import org.geotoolkit.display.SearchArea;
import org.geotoolkit.display.canvas.RenderingContext;
import org.geotoolkit.display2d.GraphicVisitor;
import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.SearchAreaJ2D;
import org.geotoolkit.gui.swing.render2d.control.information.presenter.TreeFeaturePresenter;
import org.geotoolkit.gui.swing.render2d.control.navigation.ZoomDecoration;
import no.imr.stoxmap.utils.ProjectionUtils;

/**
 * Panoramic handler
 *
 * @author Johann Sorel
 * @module pending
 */
public class PanHandler extends BaseHandler {

    //we could use this cursor, but java do not handle translucent cursor correctly on every platform
    //private static  final Cursor CUR_ZOOM_PAN = cleanCursor(PanAction.ICON.getImage(),new Point(8, 8),"zoompan");
    private static final Cursor CUR_ZOOM_PAN = Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR);
    private final MouseListen mouseInputListener = new MouseListen();
    private final double zoomFactor = 1.5;
    //private final boolean infoOnRightClick;
    private final TreeFeaturePresenter presenter = new TreeFeaturePresenter();

    public PanHandler(StoXMapSetup setup) {
        super(setup);
        //this.infoOnRightClick = infoOnRightClick;
    }

    /**
     * {@inheritDoc }
     */

    @Override
    public void install(final Component component) {
        super.install(component);
        component.addMouseListener(mouseInputListener);
        component.addMouseMotionListener(mouseInputListener);
        component.addMouseWheelListener(mouseInputListener);
//        map.setCursor(CUR_ZOOM_PAN);
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public void uninstall(final Component component) {
        super.uninstall(component);
        component.removeMouseListener(mouseInputListener);
        component.removeMouseMotionListener(mouseInputListener);
        component.removeMouseWheelListener(mouseInputListener);
//        map.setCursor(null);
    }

    //---------------------PRIVATE CLASSES--------------------------------------
    private class MouseListen implements MouseInputListener, MouseWheelListener {

        private int startX;
        private int startY;
        private int lastX;
        private int lastY;
        private int mousebutton = 0;

        @Override
        public void mouseClicked(final MouseEvent e) {
            startX = e.getX();
            startY = e.getY();
            lastX = startX;
            lastY = startY;

            /*if (infoOnRightClick && MouseEvent.BUTTON3 == e.getButton()) {
                final Area searchArea = new Area(new Rectangle(e.getPoint().x - 2, e.getPoint().y - 2, 4, 4));
                final InformationVisitor visitor = new InformationVisitor();
                map.getCanvas().getGraphicsIn(searchArea, visitor, VisitFilter.INTERSECTS);

                if (!visitor.graphics.isEmpty()) {
                    final JInformationDialog dialog = new JInformationDialog(map);
                    dialog.display(visitor.graphics, presenter, e.getLocationOnScreen(), visitor.ctx, visitor.area);
                }
            }*/

        }

        @Override
        public void mousePressed(final MouseEvent e) {
            startX = e.getX();
            startY = e.getY();
            lastX = 0;
            lastY = 0;
            mousebutton = e.getButton();

            if (!isStateFull()) {
                decorationPane.setBuffer(map.getCanvas().getSnapShot());
                decorationPane.setCoord(0, 0, map.getComponent().getWidth(), map.getComponent().getHeight(), true);
            }
        }

        @Override
        public void mouseReleased(final MouseEvent e) {
            int endX = e.getX();
            int endY = e.getY();

            if (!isStateFull()) {
                decorationPane.setBuffer(null);

                if (mousebutton == MouseEvent.BUTTON1) {
                    decorationPane.setFill(false);
                    decorationPane.setCoord(-10, -10, -10, -10, false);
                    processDrag(startX, startY, endX, endY);

                } //right mouse button : pan action
                else if (mousebutton == MouseEvent.BUTTON3) {
                    decorationPane.setFill(false);
                    decorationPane.setCoord(-10, -10, -10, -10, false);
                    processDrag(startX, startY, endX, endY);
                    // Test Try to switch projection
                    Coordinate c = ProjectionUtils.getLatLonFromPoint(map, e.getPoint());
                    ProjectionUtils.setLambertProjection(map, c.x, c.y);
                }
            }

            lastX = 0;
            lastY = 0;
        }

        @Override
        public void mouseEntered(final MouseEvent e) {
        }

        @Override
        public void mouseExited(final MouseEvent e) {
            decorationPane.setFill(false);
            decorationPane.setCoord(-10, -10, -10, -10, true);
        }

        @Override
        public void mouseDragged(final MouseEvent e) {
            int x = e.getX();
            int y = e.getY();

            if ((lastX > 0) && (lastY > 0)) {
                int dx = lastX - startX;
                int dy = lastY - startY;

                if (isStateFull()) {

                    if (mousebutton == MouseEvent.BUTTON1) {
                        processDrag(lastX, lastY, x, y);

                    } //right mouse button : pan action
                    else if (mousebutton == MouseEvent.BUTTON3) {
                        processDrag(lastX, lastY, x, y);
                    }
                } else {
                    decorationPane.setFill(true);
                    decorationPane.setCoord(dx, dy, map.getComponent().getWidth(), map.getComponent().getHeight(), true);
                }
            }

            lastX = x;
            lastY = y;
        }

        @Override
        public void mouseMoved(final MouseEvent e) {
            if (cv != null) {
                cv.updateCoord(e);
            }
            MoveHandler.mouseMoved(setup, e, map);
        }

        @Override
        public void mouseWheelMoved(final MouseWheelEvent e) {
            int rotate = e.getWheelRotation();

            if (rotate < 0) {
                scale(e.getPoint(), zoomFactor);
            } else if (rotate > 0) {
                scale(e.getPoint(), 1d / zoomFactor);
            }
        }
    }

    private static class InformationVisitor implements GraphicVisitor {

        private final List<org.opengis.display.primitive.Graphic> graphics = new ArrayList<>();
        private RenderingContext2D ctx = null;
        private SearchAreaJ2D area = null;

        @Override
        public void startVisit() {
        }

        @Override
        public void endVisit() {
        }

        @Override
        public boolean isStopRequested() {
            return false;
        }

        @Override
        public void visit(org.opengis.display.primitive.Graphic graphic, RenderingContext context, SearchArea area) {
            this.graphics.add(graphic);
            this.ctx = (RenderingContext2D) context;
            this.area = (SearchAreaJ2D) area;
        }
    }

    public ZoomDecoration getDecorationPane() {
        return decorationPane;
    }

}
