package no.imr.stoxmap.utils;

import com.vividsolutions.jts.geom.Coordinate;
import java.awt.Point;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import org.apache.sis.geometry.DirectPosition2D;
import org.apache.sis.geometry.GeneralDirectPosition;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.cs.DefaultCartesianCS;
import org.geotoolkit.referencing.operation.DefiningConversion;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.MismatchedDimensionException;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.MathTransformFactory;
import org.opengis.referencing.operation.TransformException;
import org.opengis.util.FactoryException;
import org.openide.util.Exceptions;

/**
 *
 * @author trondwe
 */
public class ProjectionUtils {

    private static CoordinateReferenceSystem wgs84CRS;

    static {
        try {
            wgs84CRS = CRS.decode("EPSG:4326"); // 4326=WGS 84, 3395= WGS 84 / World Mercator
        } catch (FactoryException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public static void setLambertProjection(JMap2D map, double cm, double lo) {
        try {
            if(Double.isNaN(cm) || Double.isNaN(lo)) {
                return;
            }
            ProjectedCRS prCRS = ProjectionUtils.createLocalLambertCRS(cm, lo);
            map.getCanvas().setObjectiveCRS(prCRS);
            map.getContainer().getContext().setCoordinateReferenceSystem(prCRS);
        } catch (TransformException ex) {
            Exceptions.printStackTrace(ex);

        }
    }

    public static DirectPosition transformCRSToWGS84(CoordinateReferenceSystem sourceCRS, Coordinate c) {
        return transformCRSToWGS84(sourceCRS, c.x, c.y);
    }

    public static DirectPosition transformCRSToWGS84(CoordinateReferenceSystem sourceCRS, Point2D pt) {
        return transformCRSToWGS84(sourceCRS, pt.getX(), pt.getY());
    }

    public static DirectPosition transformCRSToWGS84(CoordinateReferenceSystem sourceCRS, double lon, double lat) {
        try {
            MathTransform tr = CRS.findMathTransform(sourceCRS, wgs84CRS);
            return tr.transform(new GeneralDirectPosition(lon, lat), null);
        } catch (FactoryException | MismatchedDimensionException | TransformException ex) {
        }
        return null;
    }

    /**
     * Creates a Lambert Conformal Conic 1SP ProjectedCRS with the central
     * meridan set to central_meridan and latitude of origin set to
     * latitude_of_origin
     *
     * @param central_meridan central meridan used in the projection
     * @param latitude_of_origin latitude of origin used in the projection
     * @return
     */
    public static ProjectedCRS createLocalLambertCRS(double central_meridan, double latitude_of_origin) {
        try {
            MathTransformFactory mtFactory = FactoryFinder.getMathTransformFactory(null);
            ParameterValueGroup parameters = mtFactory.getDefaultParameters("Lambert_Azimuthal_Equal_Area");
            parameters.parameter("longitude_of_center").setValue(central_meridan);
            parameters.parameter("latitude_of_center").setValue(latitude_of_origin);
            //String scentralMeridian = ((Integer) ((int) (Math.floor(central_meridan)))).toString();
            //String slatitudeOfOrigin = ((Integer) ((int) (Math.floor(latitude_of_origin)))).toString();
            DefiningConversion conversion = new DefiningConversion("My conversion", parameters);
            CRSFactory crsFactory = FactoryFinder.getCRSFactory(null);
            final Map<String, Object> properties = new HashMap<>();
            properties.put(ProjectedCRS.NAME_KEY, "LAEA_" + "CM" + "_" + "LO");
            ProjectedCRS targetCRS = crsFactory.createProjectedCRS(properties, DefaultGeographicCRS.WGS84, conversion, DefaultCartesianCS.PROJECTED);

            /*          MathTransformFactory mtFactory = FactoryFinder.getMathTransformFactory(null);
             ParameterValueGroup parameters = mtFactory.getDefaultParameters("Lambert_Conformal_Conic_1SP");
             parameters.parameter("central_meridian").setValue(central_meridan);
             parameters.parameter("latitude_of_origin").setValue(latitude_of_origin);
             String scentralMeridian = ((Integer) ((int) (Math.floor(central_meridan)))).toString();
             String slatitudeOfOrigin = ((Integer) ((int) (Math.floor(latitude_of_origin)))).toString();
             DefiningConversion conversion = new DefiningConversion("My conversion", parameters);
             CRSFactory crsFactory = FactoryFinder.getCRSFactory(null);
             final Map<String, Object> properties = new HashMap<String, Object>();
             properties.put(ProjectedCRS.NAME_KEY, "LambertCC_" + slatitudeOfOrigin + "_" + scentralMeridian);
             ProjectedCRS targetCRS = crsFactory.createProjectedCRS(properties, DefaultGeographicCRS.WGS84, conversion, DefaultCartesianCS.PROJECTED);
             */
            return targetCRS;
        } catch (Exception ex) {
            return null;
        }
    }

    public static Coordinate getLatLonFromPoint(JMap2D map, Point p) {
        Point2D coord = new DirectPosition2D();
        DirectPosition targetPt;
        try {
            coord = map.getCanvas().getObjectiveToDisplay().inverseTransform(p, coord);
            targetPt = ProjectionUtils.transformCRSToWGS84(map.getCanvas().getObjectiveCRS(), coord);
            return new Coordinate(targetPt.getOrdinate(1), targetPt.getOrdinate(0));
        } catch (NoninvertibleTransformException ex) {
        }
        return null;
    }

}
