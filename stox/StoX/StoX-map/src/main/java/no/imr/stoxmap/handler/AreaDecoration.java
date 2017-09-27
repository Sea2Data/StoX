/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stoxmap.handler;

import org.geotoolkit.gui.swing.render2d.control.information.MeasureUtilities;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;
import java.util.Collection;
import javax.measure.unit.Unit;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.geotoolkit.display2d.canvas.RenderingContext2D;
import org.geotoolkit.display2d.primitive.ProjectedGeometry;
import org.geotoolkit.gui.swing.render2d.control.information.JUOMChooser;
import org.geotoolkit.gui.swing.util.RoundedBorder;
import org.geotoolkit.gui.swing.render2d.decoration.AbstractGeometryDecoration;

import org.opengis.referencing.operation.TransformException;

/**
 *
 * @author Johann Sorel (Puzzle-GIS)
 * @module pending
 */
public class AreaDecoration extends AbstractGeometryDecoration {

    private static final Color MAIN_COLOR = Color.ORANGE;
    private static final Color SHADOW_COLOR = new Color(0f, 0f, 0f, 0.5f);
    private static final int SHADOW_STEP = 2;

    private final JUOMChooser guiUOM = new JUOMChooser(AreaHandler.UNITS);
    private final JLabel guiLbl = new JLabel();

    AreaDecoration() {
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setOpaque(false);
        JPanel sub = new JPanel(new FlowLayout());
        sub.setOpaque(false);
        sub.add(guiLbl);
        sub.add(guiUOM);
        sub.setBorder(new RoundedBorder());
        panel.add(sub);

        add(BorderLayout.NORTH,panel);

        guiUOM.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                updateArea();
            }
        });
    }

    private void updateArea() {
        if (map == null) return;
        if (geometries.isEmpty()) return;
        
        double d = MeasureUtilities.calculateArea(geometries.get(0), map.getCanvas().getObjectiveCRS(), (Unit) guiUOM.getSelectedItem());
        guiLbl.setText(NumberFormat.getNumberInstance().format(d));
    }

    @Override
    public void setGeometries(final Collection<? extends Geometry> geoms) {
        super.setGeometries(geoms);
        updateArea();
    }

    @Override
    protected void paintGeometry(final Graphics2D g2, final RenderingContext2D context, final ProjectedGeometry projectedGeom) throws TransformException {
        context.switchToDisplayCRS();

        final Geometry[] objectiveGeoms = projectedGeom.getObjectiveGeometryJTS();
        final Shape[] displayGeoms = projectedGeom.getDisplayShape();

        for(int i=0;i<objectiveGeoms.length;i++){
            
            final Geometry objectiveGeom = objectiveGeoms[i];
            final Shape displayGeom = displayGeoms[i];
            
            if(objectiveGeom instanceof Point){
                //draw a single cross
                final Point p = (Point) objectiveGeom;
                final double[] crds = toDisplay(p.getCoordinate());
                paintCross(g2, crds);

            }else if(objectiveGeom instanceof LineString){
                final LineString line = (LineString)objectiveGeom;

                g2.setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
                //draw a shadow
                g2.translate(SHADOW_STEP,SHADOW_STEP);
                g2.setColor(SHADOW_COLOR);
                g2.draw(displayGeom);
                //draw the lines
                g2.translate(-SHADOW_STEP, -SHADOW_STEP);
                g2.setColor(MAIN_COLOR);
                g2.draw(displayGeom);

                //draw start cross
                Point p = line.getStartPoint();
                double[] crds = toDisplay(p.getCoordinate());
                paintCross(g2, crds);

                //draw end cross
                p = line.getEndPoint();
                crds = toDisplay(p.getCoordinate());
                paintCross(g2, crds);
            }else if(objectiveGeom instanceof Polygon){
                final Polygon poly = (Polygon)objectiveGeom;

                g2.setStroke(new BasicStroke(2,BasicStroke.CAP_ROUND,BasicStroke.JOIN_ROUND));
                //draw a shadow
                g2.translate(SHADOW_STEP, SHADOW_STEP);
                g2.setColor(SHADOW_COLOR);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                g2.fill(displayGeom);

                //draw the lines
                g2.translate(-SHADOW_STEP, -SHADOW_STEP);
                g2.setColor(MAIN_COLOR);
                g2.fill(displayGeom);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                g2.draw(displayGeom);

                //draw start cross
                Point p = poly.getExteriorRing().getStartPoint();
                double[] crds = toDisplay(p.getCoordinate());
                paintCross(g2, crds);

                //draw end cross
                p = poly.getExteriorRing().getPointN(poly.getExteriorRing().getNumPoints()-2);
                crds = toDisplay(p.getCoordinate());
                paintCross(g2, crds);
            }
        }

    }

    private void paintCross(final Graphics2D g2, final double[] crds){
        g2.setStroke(new BasicStroke(3,BasicStroke.CAP_BUTT,BasicStroke.JOIN_MITER));
        //draw a shadow
        crds[0] +=SHADOW_STEP;
        crds[1] +=SHADOW_STEP;
        g2.setColor(SHADOW_COLOR);
        g2.drawLine((int)crds[0], (int)crds[1]-6, (int)crds[0], (int)crds[1]+6);
        g2.drawLine((int)crds[0]-6, (int)crds[1], (int)crds[0]+6, (int)crds[1]);
        ///draw the start cross
        crds[0] -=SHADOW_STEP;
        crds[1] -=SHADOW_STEP;
        g2.setColor(MAIN_COLOR);
        g2.drawLine((int)crds[0], (int)crds[1]-6, (int)crds[0], (int)crds[1]+6);
        g2.drawLine((int)crds[0]-6, (int)crds[1], (int)crds[0]+6, (int)crds[1]);
    }

}
