/*
 */
package no.imr.stoxmap.utils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.LineString;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import no.imr.sea2data.stox.mapgui.StoXMapSetup;
import no.imr.sea2data.stox.providers.ProcessDataProvider;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.model.IModel;
import no.imr.stox.model.IProcess;
import no.imr.stoxmap.utils.FeatureBO;
import org.apache.sis.geometry.DirectPosition2D;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.display2d.canvas.J2DCanvas;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.Feature;
import org.opengis.feature.Property;
import org.opengis.filter.FilterFactory2;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 * Class that contains utility methods for map actions
 *
 * @author sjurl
 */
public final class MapUtils {

    /**
     * Private constructor so that the class can't be instantiated
     */
    private MapUtils() {
    }

    // Distance selection
    public static final int UNSELECTED = 0; // Not selected
    public static final int SELECTEDANYWHERE = 1; // Selected in a psu anywhere
    public static final int SELECTEDINPSU = 2; // Selected in the psu.

    /**
     * Name of the feature type used for acoustic miles.
     */
    public static final String SEGMENT_TYPE_LOCAL_NAME = "DistanceBO";
    /**
     * Name of the feature type used for stations.
     */
    public static final String STATION_TYPE_NAME = "FishstationBO";

    /**
     * Name of the feature type used for stratas.
     */
    public static final String STRATA_TYPE_NAME = "Strata";

    public static final String RECANGLE_TYPE_NAME = "Rectangle";
    private static final FilterFactory2 FF = (FilterFactory2) FactoryFinder.getFilterFactory(null);

    /**
     * Initiate acoustic features.
     *
     * @param distPSU
     * @param distFeatures
     */
    public static void initAcousticFeaturesByPSU(MatrixBO distPSU, List<FeatureBO> distFeatures) {
        Collection rowKeys = distPSU.getRowKeys();
        if (distFeatures == null) {
            return;
        }
        for (FeatureBO distFeat : distFeatures) {
            String distKey = getFKey(distFeat);
            Integer newValue = rowKeys.contains(distKey) ? SELECTEDANYWHERE : UNSELECTED;
            distFeat.setSelection(newValue);
        }
    }

    /**
     * Transforms selection from selectedinpsu to selectedanywhere, otherwise
     * keep selection as is.
     *
     * @param distFeatures
     */
    public static void unSelectAcousticFeatures(List<FeatureBO> distFeatures) {
        if (distFeatures == null) {
            return;
        }
        for (FeatureBO distFeat : distFeatures) {
            Integer s = distFeat.getSelection();
            distFeat.setSelection(s == SELECTEDINPSU ? SELECTEDANYWHERE : s);
        }
    }

    public static Object getFProp(Feature f, String prop) {
        Property p = f.getProperty(prop);
        return p != null ? p.getValue() : null;
    }

    public static void setFProp(Feature f, String prop, Object newValue) {
        Object value = getFProp(f, prop);
        if (newValue == null && value == null || newValue != null && newValue.equals(value)) {
            return;
        }
        f.getProperty(prop).setValue(newValue);
    }

    public static String getFKey(Feature f) {
        return (String) f.getProperty("name").getValue();
    }

    public static void updateFeaturesForAllDistances(MatrixBO distPSU, FeatureCollection<? extends Feature> distFeatures) {
        Collection rowKeys = distPSU.getRowKeys();
        for (Feature distFeat : distFeatures) {
            String distKey = getFKey(distFeat);
            Integer newValue = rowKeys.contains(distKey) ? SELECTEDANYWHERE : UNSELECTED;
            setFProp(distFeat, "selection", newValue);
        }
    }

    /*
     public static FeatureMapLayer searchLayerByLayerName(MapContext context, String name) {

     FeatureMapLayer candidate = null;
     for (MapLayer layer : context.layers()) {
     if (layer instanceof FeatureMapLayer && layer.getName().equals(name)) {
     return (FeatureMapLayer) layer;
     }
     }
     return candidate;
     }
     */

    /**
     * Find the features that is part of this PSU
     *
     * @param edsuPSU PSUs
     * @param psu the selected PSU
     * @param distances Distances that should be selected from
     * @param features
     */
    public static Set<FeatureBO> findFeaturesForPSU(MatrixBO edsuPSU, String psu, List<FeatureBO> distances) {
        Set<FeatureBO> features = new HashSet<>();
        Set<String> selectedDistances = new HashSet<>();
        for (String edsuKey : edsuPSU.getRowKeys()) {
            String ps = (String) edsuPSU.getRowValue(edsuKey);
            if (ps.equals(psu)) {
                selectedDistances.add(edsuKey);
            }
        }
        for (FeatureBO distance : distances) {
            String featureKey = distance.getName();
            if (featureKey != null && selectedDistances.contains(featureKey)) {
                features.add(distance);
            }
        }
        return features;
    }

    public static String getFKey(FeatureBO f) {
        return f.getName();
    }

    /**
     * Selects the rectangle layer in the map that has the provided PSU id
     *
     * @param selectedPSU string id of the rectangle to select
     */
    public static void selectRectangleLayer(StoXMapSetup setup, String selectedPSU) {
        if (setup.getRectangleLayer() != null) {
            for (FeatureBO feature : setup.getRectangleFeatures()) {
                if (feature.getFeature().getIdentifier().getID().equals(selectedPSU)) {
                    setup.getRectangleLayer().setSelectionFilter(FF.id(Collections.singleton(feature.getFeature().getIdentifier())));
                    break;
                }
            }
        }
    }

    public static Coordinate getPointWGS84(J2DCanvas canvas, double x, double y) {
        CoordinateReferenceSystem displayCRS = canvas.getDisplayCRS();
        CoordinateReferenceSystem geoCRS = (CoordinateReferenceSystem) DefaultGeographicCRS.WGS84;
        MathTransform tr;
        DirectPosition sourcePt;
        DirectPosition targetPt;
        try {
            tr = CRS.findMathTransform(displayCRS, geoCRS);
            sourcePt = new DirectPosition2D(x, y);
            targetPt = tr.transform(sourcePt, null);
            Coordinate ret = new Coordinate(targetPt.getCoordinate()[0], targetPt.getCoordinate()[1]);
            return ret;
        } catch (MismatchedDimensionException | TransformException | FactoryException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    public static FeatureBO searchDistance(List<FeatureBO> distances, int x, int y, Integer searchSize, J2DCanvas canvas) {
        if (distances == null) {
            return null;
        }
        Coordinate[] coords = new Coordinate[9];
        coords[0] = getPointWGS84(canvas, x, y + searchSize);
        coords[1] = getPointWGS84(canvas, x + searchSize * 0.8, y + searchSize * 0.8);
        coords[2] = getPointWGS84(canvas, x + searchSize, y);
        coords[3] = getPointWGS84(canvas, x + searchSize * 0.8, y - searchSize * 0.8);
        coords[4] = getPointWGS84(canvas, x, y - searchSize);
        coords[5] = getPointWGS84(canvas, x - searchSize * 0.8, y - searchSize * 0.8);
        coords[6] = getPointWGS84(canvas, x - searchSize, y);
        coords[7] = getPointWGS84(canvas, x - searchSize * 0.8, y + searchSize * 0.8);
        coords[8] = coords[0];
        double[] polyX = new double[9];
        double[] polyY = new double[9];
        for (int i = 0; i <= 8; i++) {
            polyX[i] = coords[i].x;
            polyY[i] = coords[i].y;
        }
        double[] multiple = new double[9];
        double[] constant = new double[9];
        precalc_values(multiple, constant, polyY, polyX);
        for (FeatureBO distance : distances) {
            //Point c = (Point) MapUtils.getFProp(distance, "point");
            LineString l = (LineString) distance.getGeometry();
            double xm = (l.getCoordinates()[0].x + l.getCoordinates()[1].x) * 0.5d;
            double ym = (l.getCoordinates()[0].y + l.getCoordinates()[1].y) * 0.5d;
            if (pointInPolygon(xm, ym, multiple, constant, polyY, polyX)) {
                return distance;
            }
        }
        return null;
    }

    private static void precalc_values(double[] multiple, double[] constant, double[] polyY, double[] polyX) {
        int polySides = polyX.length - 1;

        int i, j = polySides - 1;

        for (i = 0; i < polySides; i++) {
            if (polyY[j] == polyY[i]) {
                constant[i] = polyX[i];
                multiple[i] = 0;
            } else {
                constant[i] = polyX[i] - (polyY[i] * polyX[j]) / (polyY[j] - polyY[i]) + (polyY[i] * polyX[i]) / (polyY[j] - polyY[i]);
                multiple[i] = (polyX[j] - polyX[i]) / (polyY[j] - polyY[i]);
            }
            j = i;
        }
    }

    public static Boolean pointInPolygon(double x, double y, double[] multiple, double[] constant, double[] polyY, double[] polyX) {
        int polySides = polyX.length - 1;
        int j = polySides - 1;
        boolean oddNodes = false;

        for (int i = 0; i < polySides; i++) {
            if ((polyY[i] < y && polyY[j] >= y
                    || polyY[j] < y && polyY[i] >= y)) {
                oddNodes ^= (y * multiple[i] + constant[i] < x);
            }
            j = i;
        }

        return oddNodes;
    }

    public static void onStrataEdited() {
        onFunctionEditedUseProcessData(Functions.FN_DEFINESTRATA, Functions.PM_DEFINESTRATA_USEPROCESSDATA);
    }

    public static void onAssignmentEdited() {
        //onFunctionEditedUseProcessData(Functions.FN_RECTANGLEASSIGNMENT, Functions.PM_RECTANGLEASSIGNMENT_USEPROCESSDATA);

        IModel model = Lookup.getDefault().lookup(ProcessDataProvider.class).getModel();
        if (model == null) {
            return;
        }
        IProcess p = model.getProcessByFunctionName(Functions.FN_BIOSTATIONASSIGNMENT);
        if (p == null) {
            return;
        }
        p.setParameterValue(Functions.PM_BIOSTATIONASSIGNMENT_ASSIGNMENTMETHOD, Functions.ASSIGNMENTMETHOD_USEPROCESSDATA);
    }

    public static void onFunctionEditedUseProcessData(String functionName, String parameterName) {
        ProcessDataProvider pdp = Lookup.getDefault().lookup(ProcessDataProvider.class);
        if (pdp == null) {
            return;
        }
        IModel model = pdp.getModel();
        if (model == null) {
            return;
        }
        IProcess p = model.getProcessByFunctionName(functionName);
        if (p == null) {
            return;
        }
        Boolean useProcessData = (Boolean) p.getActualValue(parameterName);
        if (!useProcessData) {
            p.setParameterValue(parameterName, String.valueOf(true));
        }
    }
}
