package no.imr.sea2data.geotk.util;

import java.util.HashMap;
import java.util.Map;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.geotoolkit.referencing.cs.DefaultCartesianCS;
import org.geotoolkit.referencing.operation.DefiningConversion;
import org.opengis.parameter.ParameterValueGroup;
import org.opengis.referencing.crs.CRSFactory;
import org.opengis.referencing.crs.ProjectedCRS;
import org.opengis.referencing.operation.MathTransformFactory;

/**
 *
 * @author trondwe
 */
public class ProjectionUtils {

    /**
     * Creates a Lambert Conformal Conic 1SP ProjectedCRS with the central
     * meridan set to central_meridan and latitude of origin set to
     * latitude_of_origin
     *
     * @param central_meridan central meridan used in the projection
     * @param latitude_of_origin latitude of origin used in the projection
     * @return
     */
    public static ProjectedCRS getLocalLambertCRS(double central_meridan, double latitude_of_origin) {
        try {
            MathTransformFactory mtFactory = FactoryFinder.getMathTransformFactory(null);
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
            return targetCRS;
        } catch (Exception ex) {
            return null;
        }
    }
}
