/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stoxmap.handler;

import com.vividsolutions.jts.geom.Coordinate;
import java.awt.Component;
import java.awt.Point;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.swing.SwingWorker;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.stox.mapgui.StoXMapSetup;
import no.imr.sea2data.stox.providers.ProcessDataProvider;
import no.imr.stox.api.IProcessDataListener;
import no.imr.stox.api.IProjectProvider;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.EchosounderUtils;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.model.IProcess;
import no.imr.stox.model.IProject;
import no.imr.stox.nodes.PSUNode;
import no.imr.stox.nodes.StrataNode;
import static no.imr.stoxmap.handler.TransectBuildHandler.FF;
import no.imr.stoxmap.utils.FeatureBO;
import no.imr.stoxmap.utils.ICoordinateViewer;
import no.imr.stoxmap.utils.ProjectionUtils;
import org.geotoolkit.gui.swing.render2d.control.navigation.AbstractNavigationHandler;
import org.geotoolkit.map.FeatureMapLayer;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;

/**
 *
 * @author aasmunds
 */
public class BaseHandler extends AbstractNavigationHandler {

    ICoordinateViewer cv;
    StoXMapSetup setup;
    IProcessDataListener pd;

    public BaseHandler(StoXMapSetup setup) {
        super(setup.getMapPanel().getMap());
        pd = new IProcessDataListener() {
            @Override
            public void onPSUViewRequest(String psu) {
                BaseHandler.this.onPSUViewRequest(psu); // wrapper
            }
        };
        this.cv = setup.getMapPanel().getCoordinateViewer();
        this.setup = setup;
    }
    private Lookup.Result<PSUNode> psuResult;
    private Lookup.Result<StrataNode> strataResult;

    @Override
    public void install(final Component component) {
        super.install(component);
//        map.setCursor(CUR_ZOOM_PAN);
        Lookup.getDefault().lookup(ProcessDataProvider.class).addProcessDataListener(pd);
        psuResult = Utilities.actionsGlobalContext().lookupResult(PSUNode.class);
        psuResult.addLookupListener(listener);
        strataResult = Utilities.actionsGlobalContext().lookupResult(StrataNode.class);
        strataResult.addLookupListener(listener);
    }

    @Override
    public void uninstall(final Component component) {
        super.uninstall(component);
        Lookup.getDefault().lookup(ProcessDataProvider.class).removeProcessDataListener(pd);
        psuResult.removeLookupListener(listener);
        strataResult.removeLookupListener(listener);
    }

    public void onPSUViewRequest(String psu) {
        (new SwingWorker() {

            @Override
            protected Object doInBackground() {
                IProject p = Lookup.getDefault().lookup(IProjectProvider.class).getProject();
                Collection<String> edsus = AbndEstProcessDataUtil.getEDSUsByPSU(p.getProcessData(), psu);
                IProcess fa = p.getBaseline().getProcessByFunctionName(Functions.FN_FILTERACOUSTIC);
                List<DistanceBO> dList = fa.getOutput();
                if (dList == null) {
                    return null;
                }
                Collection<DistanceBO> dl = (List<DistanceBO>) EchosounderUtils.findDistances(dList, edsus);
                double minLat = 90;
                double minLon = 180;
                double maxLat = -90;
                double maxLon = -180;
                for (DistanceBO d : dl) {
                    if (d.getStartLat() == null || d.getStartLon() == null) {
                        continue;
                    }
                    minLat = Math.min(minLat, d.getStartLat());
                    minLon = Math.min(minLon, d.getStartLon());
                    maxLat = Math.max(maxLat, d.getStartLat());
                    maxLon = Math.max(maxLon, d.getStartLon());
                }
                zoomToView(minLon, minLat, maxLon, maxLat);
                return null;
            }

            @Override
            protected void done() {
                map.getCanvas().repaint();
            }

        }).execute();

    }

    Point getPointFromCoord(Coordinate c) {
        int vx0 = 0;
        int vy0 = 0;
        int vw = map.getComponent().getWidth();
        int vh = map.getComponent().getHeight();
        int vxm = vw;
        int vym = vh;
        Coordinate p0 = ProjectionUtils.getLatLonFromPoint(map, new Point(vx0, vy0));
        Coordinate pm = ProjectionUtils.getLatLonFromPoint(map, new Point(vxm, vym));
        double pw = pm.x - p0.x;
        double ph = p0.y - pm.y;
        int vx = (int) ((c.x - p0.x) / pw * vw + vx0);
        int vy = (int) ((c.y - p0.y) / ph * vh + vy0);
        return new Point(vx, vy);
    }

    public void zoomToView(double xmin, double ymin, double xmax, double ymax) {
        double xmean = xmin + (xmax - xmin) * 0.5;
        double ymean = ymin + (ymax - ymin) * 0.5;
        /*Coordinate cmin = new Coordinate(xmin, ymin);
        Coordinate cmax = new Coordinate(ymax, ymax);*/
        Coordinate cmean = new Coordinate(xmean, ymean);
        int w = map.getComponent().getWidth();
        int h = map.getComponent().getHeight();

        // Convergance projection based positionate algorithm
        // Finding the zoom factor
        int d = 10;
        /*double F = Math.sqrt(w * w + h * h);
        // The model triangle 10, 10, d0 is used to find the d0/a0 ratio
        Coordinate p1 = ProjectionUtils.getLatLonFromPoint(map, new Point(w / 2, h / 2));
        Coordinate p2 = ProjectionUtils.getLatLonFromPoint(map, new Point(w / 2 + d, h / 2 + d));
        double c0 = p2.x - p1.x;
        double b0 = p2.y - p1.y;
        double a0 = Math.sqrt(c0 * c0 + b0 * b0);
        double d0 = Math.sqrt(2 * d * d);
        // The A is calculated by equal shape equation between triangles
        xmax = 31.88;
        xmin = 28.99;
        ymin = 70.22;
        ymax = 70.85;
        double x = 5.0;
        double y = 4.0;
        double e = Math.sqrt(x * x + y * y);
        double E = d0 * e / a0;
        double zoomFac = F / E * 0.5;
        // The projection will require the step to be repeated 10-20 times to find the position
        if (!Double.isNaN(zoomFac) && zoomFac > 0.001 && zoomFac < 1000) {
            scale(new Point(w / 2 + d, h / 2 + d), zoomFac);
        }*/
        while (processDrag(w, h, d, cmean)) {
        };

    }

    private boolean processDrag(int w, int h, int d, Coordinate cmean) {
        // The model triangle 10, 10, d0 is used to find the d0/a0 ratio
        Coordinate p1 = ProjectionUtils.getLatLonFromPoint(map, new Point(w / 2, h / 2));
        Coordinate p2 = ProjectionUtils.getLatLonFromPoint(map, new Point(w / 2 + d, h / 2 + d));
        double c0 = p2.x - p1.x;
        double b0 = p2.y - p1.y;
        double a0 = Math.sqrt(c0 * c0 + b0 * b0);
        double d0 = Math.sqrt(2 * d * d);
        double c = cmean.x - p1.x;
        double b = cmean.y - p1.y;
        double a = Math.sqrt(c * c + b * b);
        // The A is calculated by equal shape equation between triangles
        double A = a * d0 / a0;
        // down scale A
        if (A > w) {
            A = w;
        }
        int C = (int) (c / a * A);
        int B = (int) (b / a * A);
        if (Double.isNaN(A) || A < d) {
            return false;
        }
        // The projection will require the step to be repeated 10-20 times to find the position
        processDrag(w / 2, h / 2, w / 2 - C, h / 2 + B);
        return true;
    }

    private void onLookupPsuNode(PSUNode psuNode) {
    }

    private void onLookupStrataNode(StrataNode strataNode) {

        if (setup.getStrataFeatures() == null) {
            return;
        }
        for (FeatureBO feature : setup.getStrataFeatures()) {
            if (feature.getName().equals(strataNode.getStratum())) {
                final FeatureMapLayer layer = setup.getStrataLayer();
                layer.setSelectionFilter(FF.id(Collections.singleton(feature.getFeature().getIdentifier())));
                break;
            }
        }
        map.getCanvas().repaint();
    }
    private final LookupListener listener = new LookupListener() {

        @Override
        public void resultChanged(LookupEvent le) {
            Collection c = ((Lookup.Result) le.getSource()).allInstances();
            if (!c.isEmpty()) {
                Object node = c.iterator().next();
                if (node instanceof PSUNode) {
                    onLookupPsuNode((PSUNode) c.iterator().next());
                } else if (node instanceof StrataNode) {
                    onLookupStrataNode((StrataNode) c.iterator().next());
                }
            }
        }
    };

}
