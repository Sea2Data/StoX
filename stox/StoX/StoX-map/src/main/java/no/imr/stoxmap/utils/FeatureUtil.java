package no.imr.stoxmap.utils;

import no.imr.sea2data.imrbase.map.ILatLonEvent;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import no.imr.sea2data.imrmap.utils.JTSUtils;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.functions.utils.RectangleUtil;
import org.apache.sis.storage.DataStoreException;
import org.geotoolkit.data.FeatureCollection;
import org.geotoolkit.data.FeatureIterator;
import org.geotoolkit.data.memory.MemoryFeatureStore;
import org.geotoolkit.data.query.QueryBuilder;
import org.geotoolkit.data.session.Session;
import org.geotoolkit.factory.FactoryFinder;
import org.geotoolkit.feature.AttributeDescriptorBuilder;
import org.geotoolkit.feature.AttributeTypeBuilder;
import org.geotoolkit.feature.FeatureTypeBuilder;
import org.geotoolkit.geometry.jts.JTS;
import org.geotoolkit.gui.swing.render2d.JMap2D;
import org.geotoolkit.map.FeatureMapLayer;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.opengis.feature.Feature;
import org.opengis.feature.GeometryAttribute;
import org.opengis.feature.type.AttributeType;
import org.opengis.feature.type.FeatureType;
import org.opengis.filter.Filter;
import org.opengis.filter.FilterFactory2;
import org.opengis.referencing.crs.GeographicCRS;
import org.openide.util.Exceptions;

/**
 * Creates a FeatureCollection out of any class that uses the FeaturePojo
 * annotations.
 *
 * @author sjurl
 */
public class FeatureUtil {

    private static final String GET_METHOD = "get";

    /**
     * Method that creates a FeatureCollection from objects that has the
     * FeaturePojo annotation set
     *
     * @param collection collection of pojo objects that should be made into
     * features contained in the annotations will be overridden
     * @return a feature collection made up of all the input pojos
     */
    public static FeatureCollection createFeatureCollection(String collectionName, final Collection<FeatureBO> collection) {
        try {
            final FeatureType type = createFeatureType(FeatureBO.class);
            final MemoryFeatureStore store = new MemoryFeatureStore(type, true);
            final Session session = store.createSession(true);
            final FeatureCollection features = session.getFeatureCollection(QueryBuilder.all(type.getName()));
            for (final FeatureBO vo : collection) {
                final Feature f = vo.getFeature();
                GeometryAttribute geomatt = f.getDefaultGeometryProperty();
                if (geomatt.getValue() != null) {
                    features.add(f);
                }
            }
            session.commit();
            return features;
        } catch (DataStoreException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    /**
     * Creates the FeatureType that corresponds to the input class
     *
     * @param clazz class that should be used as building stone for the feature
     * type
     * @return
     */
    public static FeatureType createFeatureType(Class clazz) {
        FeatureTypeBuilder ftb = getFTB(clazz);
        final FeatureType res = ftb.buildFeatureType();
        return res;
    }

    /**
     * Fills a FeatureTypeBuilder with the features contained in the input Pojo
     * class
     *
     * @param clazz class that the feature should be created from
     * @param ignoreMethods methods that will be ignored when creating the
     * feature
     * @param geometryField field that contains the geometry
     * @return
     * @throws SecurityException
     */
    private static FeatureTypeBuilder getFTB(Class clazz) {
        final FeatureTypeBuilder ftb = new FeatureTypeBuilder();
        final AttributeDescriptorBuilder adb = new AttributeDescriptorBuilder();
        final AttributeTypeBuilder atb = new AttributeTypeBuilder();
        for (final Method method : clazz.getMethods()) {
            //we check method and not field to ensure we also take field which may be private            
            String methodname = method.getName();
            if (!(methodname.equals("getName") || methodname.equals("getGeometry") || methodname.equals("getSelection")
                    || methodname.equals("getPresencelevel"))) {
                continue;
            }
            String baseName;
            String attributeName;
            baseName = methodname.substring(3);
            attributeName = baseName.substring(0, 1).toLowerCase() + baseName.substring(1);

            atb.reset();
            atb.setName(attributeName);
            atb.setBinding(method.getReturnType());
            AttributeType type;
            if (attributeName.equals("geometry")) {
                //geometry type, we expect the geometry to have the correct srid set.
                atb.setCRS((GeographicCRS) DefaultGeographicCRS.WGS84);
                type = atb.buildGeometryType();
            } else {
                //basic attribute type
                type = atb.buildType();
            }
            adb.reset();
            adb.setName(attributeName);
            adb.setMinOccurs(1);
            adb.setMaxOccurs(1);
            adb.setNillable(true);
            adb.setType(type);
            ftb.add(adb.buildDescriptor());
        }
        ftb.setDefaultGeometry("geometry");
        ftb.setName(clazz.getSimpleName());
        return ftb;
    }

    public static List<FeatureBO> getFeatureBOWithGeometryFromWKTStream(InputStream inp) {
        WKTReader wktr = new WKTReader();
        String line;
        String[] elements;
        List<FeatureBO> fbs = new ArrayList<>();
        try (BufferedReader in = new BufferedReader(new InputStreamReader(inp))) {
            while ((line = in.readLine()) != null) {
                if (line.startsWith("#")) {
                    // Skip comments
                    continue;
                }
                elements = line.split("\t");
                if (elements.length < 2) {
                    continue;
                }
                String name = elements[0].trim();
                Geometry gm;
                try {
                    gm = (Geometry) wktr.read(elements[elements.length - 1]); // Read geometry from last column
                    gm.setSRID(4326); // SRID 4326 = WGS 84 (lat/lon)
                    FeatureBO fb = new FeatureBO(name, gm);
                    fbs.add(fb);
                } catch (ParseException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        } catch (IOException e) {

        }
        return fbs;
    }

    public static List<FeatureBO> getEarthGrid() {
        List<LineString> lss = new ArrayList<>();
        // create vert. lines
        for (int i = -180; i < 180; i += 5) {
            List<Coordinate> l = new ArrayList<>();
            for (int j = -90; j <= 90; j += 5) {
                l.add(new Coordinate(i, j));
            }
            lss.add(JTSUtils.createLineString(l));
        }
        // create hor. lines
        for (int j = -90; j <= 90; j += 5) {
            List<Coordinate> l = new ArrayList<>();
            for (int i = -180; i < 180; i += 5) {
                l.add(new Coordinate(i, j));
            }
            lss.add(JTSUtils.createLineString(l));
        }
        MultiLineString mls = JTSUtils.createMultiLineString(lss);
        return Arrays.asList(new FeatureBO("grid", mls));
    }

    public static List<FeatureBO> createFeatureBOWithPointFromPosList(List<ILatLonEvent> pos) {
        List<FeatureBO> l = new ArrayList<>();
        for (int i = 0; i < pos.size(); i++) {
            ILatLonEvent p = pos.get(i);
            if (p.getStartLon() == null || p.getStartLat() == null) {
                continue;
            }
            Point point = JTSUtils.createPoint(new Coordinate(p.getStartLon(), p.getStartLat()));
            point.setSRID(4326);
            FeatureBO f = new FeatureBO(p.getKey(), point);
            f.setUserData(p);
            l.add(f);
        }
        return l;
    }

    /**
     * Create feature bo lines from position list. The lines are creatd from the
     * position in its given order. The last line is extrapolated
     *
     * @param pos
     * @return
     */
    public static List<FeatureBO> createFeatureBOWithLineFromPosList(List<ILatLonEvent> pos, double maxDist) {
        List<FeatureBO> l = new ArrayList<>();
        if (pos.isEmpty()) {
            return l;
        }
        for (int i = 0; i < pos.size(); i++) {
            ILatLonEvent p = pos.get(i);
            String key = p.getKey();
            Double lon1 = p.getStartLon();
            Double lat1 = p.getStartLat();
            if (lon1 == null | lat1 == null) {
                continue;
            }
            Double lon2 = p.getStopLon();
            Double lat2 = p.getStopLat();
            if (lon2 == null || lat2 == null || (lon1.equals(lon2) && lat1.equals(lat2))) {
                if (i < pos.size() - 1) {
                    // Use start of next
                    ILatLonEvent p1 = pos.get(i + 1);
                    lon2 = p1.getStartLon();
                    lat2 = p1.getStartLat() + 0.000000001;
                } else {
                    // Last point use the point only
                    lon2 = lon1 + 0.000000001;
                    lat2 = lat1 + 0.000000001;
                }
            }
            /*if (lon2 == null ) {
                continue;
            }*/
            /*// Check distance
             Double dNM = JTSUtils.gcircledist(new Coordinate(lon1, lat1), new Coordinate(lon2, lat2));
             if (dNM > maxDist) {
             continue;
             }*/
            Geometry g = JTSUtils.createLineString(lon1, lat1, lon2, lat2);
            g.setSRID(4326);
            FeatureBO f = new FeatureBO(key, g);
            f.setUserData(p);
            l.add(f);
        }
        return l;
    }

    /**
     * createFeatureBOFromGeometryMatrix
     *
     * @param gmMatrx
     * @return FeatureBO's
     */
    public static List<FeatureBO> createFeatureBOFromGeometryMatrix(MatrixBO gmMatrx, String variable) {
        if (gmMatrx == null) {
            return null;
        }
        List<FeatureBO> fs = new ArrayList<>();
        for (String key : gmMatrx.getRowKeys()) {
            Geometry gm = (Geometry) gmMatrx.getRowColValue(key, variable);
            if (gm == null) {
                continue;
            }
            FeatureBO f = new FeatureBO(key, gm);
            fs.add(f);
        }
        return fs;
    }

    public static List<FeatureBO> createFeatureBOFromRectangleKeys(List<String> rKeys) {
        List<FeatureBO> fs = new ArrayList<>();
        for (String key : rKeys) {
            List<Coordinate> coordinates = new ArrayList<>(Arrays.asList(RectangleUtil.getCoordsByRectangleKey(key)));
            if (coordinates.isEmpty()) {
                continue;
            }
            Coordinate last = coordinates.get(0);
            coordinates.add(last);
            Geometry gm = JTSUtils.createPolygon(coordinates);
            if (gm == null) {
                continue;
            }
            gm.setSRID(4326);
            FeatureBO f = new FeatureBO(key, gm);
            fs.add(f);
        }
        return fs;
    }
    private static final GeometryFactory GF = new GeometryFactory();
    private static final FilterFactory2 FF = (FilterFactory2) FactoryFinder.getFilterFactory(null);

    /**
     * Search for a feature at given mouse position in given collection.
     *
     * @param dataColl Collection of features
     * @param me MouseEvent that contains information about where the user
     * clicked
     * @param searchSize Size of the search area
     * @param map the map component where the features are shown
     * @return The clicked feature or null if none was close enough to where the
     * user clicked.
     */
    public static Feature searchFeature(FeatureCollection dataColl, MouseEvent me, Integer searchSize, JMap2D map) {
        if (dataColl == null) {
            return null;
        }

        //define the search area
        Geometry searchArea = GF.createPoint(new Coordinate(me.getX(), me.getY()));
        searchArea = searchArea.buffer(searchSize);
        JTS.setCRS(searchArea, map.getCanvas().getDisplayCRS());

        final Filter filter = FF.intersects(FF.literal(searchArea), FF.property(dataColl.getFeatureType().getGeometryDescriptor().getLocalName()));
        try {
            final FeatureCollection<Feature> intersectColl = dataColl.subCollection(
                    QueryBuilder.filtered(dataColl.getFeatureType().getName(), filter));
            final FeatureIterator ite = intersectColl.iterator();
            try {
                if (ite.hasNext()) {
                    return ite.next();
                }
            } finally {
                ite.close();
            }
        } catch (DataStoreException ex) {
            Exceptions.printStackTrace(ex);
        }

        return null;
    }

    public static FeatureBO getFeatureByName(Collection<FeatureBO> fCol, String name) {
        for (FeatureBO f : fCol) {
            if (f.getName().equals(name)) {
                return f;
            }
        }
        return null;
    }

    public static void clearLayerSelection(Collection<FeatureBO> fbs, FeatureMapLayer fl) {
        for (FeatureBO bo : fbs) {
            bo.setSelection(0);
        }
        if (fl != null) {
            fl.setSelectionFilter(FF.id((Set) Collections.emptySet()));
        }
    }
}
