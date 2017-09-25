package no.imr.sea2data.imrmap.utils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiLineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.imr.sea2data.imrbase.map.LatLonUtil;

/**
 * JTS utilities - a class for light adaption to JTS functions.
 *
 * @author aasmunds
 */
public class JTSUtils {

    // A geometry factory fpr creating JTS objects
    private static final GeometryFactory GEOMETRY_FACTORY = new GeometryFactory();

    /**
     * create a coordinate based on lon, lat
     *
     * @param lon longitude
     * @param lat latitude
     * @return the JTS coordinate
     */
    public static Coordinate createCoordinate(Double lon, Double lat) {
        return new Coordinate(lon, lat);
    }

    /**
     * create a coordinate based on a line in a text file. if the line begins
     * with '>' null is returned.
     *
     * @param s the line
     * @param lonfirst if longitude is before latitude.
     * @return the JTS coordinate created.
     */
    public static Coordinate createCoordinateFromFileInput(String s, boolean lonfirst) {
        if (!s.isEmpty() && !s.startsWith(">")) {
            String[] sb = s.trim().split("\\s+");
            return createCoordinate(Double.valueOf(sb[lonfirst ? 0 : 1]), Double.valueOf(sb[lonfirst ? 1 : 0]));
        }
        return null;
    }

    /**
     * create a linestring from 2 lon, lat pairs.
     *
     * @param lon1 longitude point 1
     * @param lat1 latitude point 1
     * @param lon2 longitude point 2
     * @param lat2
     * @return
     */
    public static LineString createLineString(Double lon1, Double lat1, Double lon2, Double lat2) {
        return createLineString(Arrays.asList(createCoordinate(lon1, lat1), createCoordinate(lon2, lat2)));
    }

    public static LineString createLineString(List<Coordinate> coordinates) {
        return createLineString(coordinates.toArray(new Coordinate[coordinates.size()]));
    }

    public static LineString createLineString(Coordinate[] coordinates) {
        CoordinateSequence cs = new CoordinateArraySequence(coordinates);
        if (cs.size() >= 4 && coordinates[0].equals2D(coordinates[coordinates.length - 1])) {
            return new LinearRing(cs, GEOMETRY_FACTORY);
        } else if (cs.size() >= 2) {
            return new LineString(cs, GEOMETRY_FACTORY);
        } else {
            return null;
        }
    } // createLineString

    public static Point createPoint(Coordinate c) {
        return GEOMETRY_FACTORY.createPoint(c);
    }

    public static List<Coordinate> ensureClosed(List<Coordinate> crds) {
        if (crds.size() == 1 || crds.size() > 1 && !crds.get(0).equals2D(crds.get(crds.size() - 1))) {
            List<Coordinate> l = new ArrayList<Coordinate>(crds);
            l.add(crds.get(0));
            return l;
        }
        return crds;
    }

    public static LinearRing getRingFromPoints(List<Coordinate> coords) {
        List<Coordinate> cds = ensureClosed(coords);
        if (cds.size() >= 4) {
            LineString ls = createLineString(cds);
            return new LinearRing(ls.getCoordinateSequence(), GEOMETRY_FACTORY);
        }
        return null;
    }

    public static LinearRing getRingFromPoints(Coordinate[] coords) {
        return getRingFromPoints(Arrays.asList(coords));
    } // getRingFromLineString

    public static LinearRing getRingFromLineString(LineString ls) {
        if (ls == null) {
            return null;
        }
        return getRingFromPoints(ls.getCoordinates());
    }

    public static Polygon createPolygon(List<Coordinate> ls) {
        LinearRing r = getRingFromPoints(ls);
        if (r == null) {
            return null;
        }
        return GEOMETRY_FACTORY.createPolygon(r, null);
    }

    public static Polygon createPolygon(LineString ls) {
        return createPolygon(Arrays.asList(ls.getCoordinates()));
    }

    public static Boolean within(Coordinate c, MultiPolygon mp) {
        return createPoint(c).within(mp);
    }

    public static Boolean within(Coordinate c, LinearRing lr) {
        return createPoint(c).within(createPolygon(lr));
    }

    public static Boolean within(Coordinate c, LineString ls) {
        return createPoint(c).within(createPolygon(ls));
    }

    public static Boolean within(Coordinate c, Coordinate[] coords) {
        return within(c, getRingFromPoints(coords));
    }

    public static Boolean within(Coordinate c, List<Coordinate> coords) {
        return within(c, getRingFromPoints(coords));
    }

    public static Boolean within(java.awt.Point p, LinearRing lr) {
        return within(getCoordinateFromPoint(p), lr);
    }

    public static Boolean within(java.awt.Point p, List<Coordinate> coords) {
        return within(getCoordinateFromPoint(p), coords);
    }

    /**
     * great circle distance Source:
     * http://www.movable-type.co.uk/scripts/latlong.html
     *
     * @param c1
     * @param c2
     * @return
     */
    public static double gcircledist(Coordinate c1, Coordinate c2) {
        /*GeodeticCalculator geocalc = new GeodeticCalculator();
         geocalc.setStartingGeographicPoint(c1.x, c1.y);
         geocalc.setDestinationGeographicPoint(c2.x, c2.y);
         double ort = geocalc.getOrthodromicDistance();
         System.out.println(ort);*/
        double lon1 = c1.x;
        double lat1 = c1.y;
        double lon2 = c2.x;
        double lat2 = c2.y;
        double R = 6371; // km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d / 1.852; // nmi
    }

    /**
     * A NASA planar algorithm for computing the area of a polygon whose edges
     * are segments of great circles. This algorithm works directly on a sphere
     * thus avoiding projections. Source: "Some Algorithms for Polygons on a
     * Sphere" chapter "Spherical case -approximation" by Chamberlain &
     * Duquette,
     * http://trs-new.jpl.nasa.gov/dspace/bitstream/2014/40409/1/07-03.pdf for
     * the signed area under the edge that goes from point i to point i+1.
     * Summing the signed areas under all the edges. Note: Check against
     * geodesic planimetric fourier based algorithm
     * http://geographiclib.sourceforge.net/cgi-bin/Planimeter (1 square meter =
     * 2.9155335 × 10-7 square nautical miles) shows significant rhumb errors
     * for polygons with distances over 100 km. Solution: Reduce distances with
     * different types of splining maybe. Discuss. Warning: The great circle
     * distance also have an small ellipsoid error.
     *
     * @param coords the vertices of the polygon
     * @return polygon area
     */
    public static Double polygonArea(LinearRing lr) {
        //Convert the vertices to ring.
        if (lr == null) {
            return 0d;
        }
        Coordinate[] coords = lr.getCoordinates();
        double area = 0;
        int len = coords.length;
        for (int i = 0; i < len - 1; i++) {
            Coordinate p1 = coords[i];
            Coordinate p2 = coords[i + 1];
            area += Math.toRadians(p1.x - p2.x)
                    * (2 + Math.sin(Math.toRadians(p1.y))
                    + Math.sin(Math.toRadians(p2.y)));
        }
        double r = 6371000.0 * 0.000539956803; // avg.radius * n.mi/m 

        area = Math.abs(area * r * r / 2.0);
        return area;
    }

    public static Double polygonArea(List<Coordinate> cds) {
        if (cds.isEmpty()) {
            return 0d;
        }
        return polygonArea(cds.toArray(new Coordinate[cds.size()]));
    }

    public static Double polygonArea(Coordinate[] cds) {
        LinearRing lr = JTSUtils.getRingFromPoints(cds);
        if (lr == null) {
            return 0d;
        }
        return polygonArea(lr);

    }

    /**
     * Calculate area of polygons with multiple exterior/interior rings 
     * Used in stox as simple (non accurate area)
     * @param mp
     * @return 
     */
    public static Double polygonArea(MultiPolygon mp) {
        Double p = 0d;
        for (int nG = 0; nG < mp.getNumGeometries(); nG++) {
            Polygon pol = (Polygon) mp.getGeometryN(nG);
            double polv = JTSUtils.polygonArea(pol.getExteriorRing().getCoordinates());
            for (int nInt = 0; nInt < pol.getNumInteriorRing(); nInt++) {
                double intv = JTSUtils.polygonArea(pol.getInteriorRingN(nInt).getCoordinates());
                polv -= intv;
            }
            p += polv;
        }
        return p;
    }

    public static void writePolygonsToFile(String fileName, List<LineString> lst) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(fileName);
            for (LineString ls : lst) {
                pw.println(">" + (ls.getUserData() != null ? ls.getUserData().toString() : ""));
                for (Coordinate c : ls.getCoordinates()) {
                    pw.println(c.x + "\t" + c.y);
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JTSUtils.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            pw.close();
        }
    }

    public static String getCoordinateDescr(Coordinate c, Boolean withMinute) {
        String lats = LatLonUtil.getDegreeDecimalDescr(c.y, new String[]{"S", "N"}, 2, withMinute);
        String lons = LatLonUtil.getDegreeDecimalDescr(c.x, new String[]{"W", "E"}, 3, withMinute);
        String sep = withMinute ? " " : "-";
        if (!withMinute) {
            lats = lats.trim();
            lons = lons.trim();
        }
        return lats + sep + lons;
    }

    public static MultiPolygon createMultiPolygon(List<LineString> l) {
        Polygon[] polygons = new Polygon[l.size()];
        for (int i = 0; i < l.size(); i++) {
            LineString ls = l.get(i);

            polygons[i] = createPolygon(ls);
        }
        return new MultiPolygon(polygons, GEOMETRY_FACTORY);
    }

    public static MultiLineString createMultiLineString(List<LineString> l) {
        LineString[] lineStrings = new LineString[l.size()];
        return new MultiLineString(l.toArray(lineStrings), GEOMETRY_FACTORY);
    }

    public static Geometry getConvexHull(List<LineString> ls) {
        return createMultiPolygon(ls).convexHull();
    }

    public static MultiPolygon lineStringToAreaUnits(LineString ls, double dx, double dy) {
        Polygon[] polygons = new Polygon[ls.getNumPoints()];
        for (int i = 0; i < ls.getNumPoints(); i++) {
            Coordinate c = ls.getCoordinates()[i];
            double x1 = c.x - dx;
            double x2 = c.x + dx;
            double y1 = c.y - dy;
            double y2 = c.y + dy;
            Coordinate[] cs = new Coordinate[]{new Coordinate(x1, y1), new Coordinate(x2, y1),
                new Coordinate(x2, y2), new Coordinate(x1, y2), new Coordinate(x1, y1)};
            polygons[i] = createPolygon(createLineString(cs));
        }
        return new MultiPolygon(polygons, GEOMETRY_FACTORY);
    }

    /**
     * create a strata system from a convex hull and intervals
     *
     * @param bound the area of the strata system
     * @param xint x interval
     * @param yint y interval
     * @return
     */
    public static List<LineString> createStrataSystem(List<LineString> bound, BigDecimal xint, BigDecimal yint) {
        Polygon convexHull = (Polygon) JTSUtils.getConvexHull(bound);
        Coordinate[] e = ((Polygon) convexHull.getEnvelope()).getCoordinates();
        List<LineString> l = new ArrayList<LineString>();
        BigDecimal x = new BigDecimal(e[0].x);
        do {
            BigDecimal y = new BigDecimal(e[0].y);
            do {
                LineString ls = createLineString(Arrays.asList(
                        createCoordinate(x.doubleValue(), y.doubleValue()),
                        createCoordinate(x.add(xint).doubleValue(), y.doubleValue()),
                        createCoordinate(x.add(xint).doubleValue(), y.add(yint).doubleValue()),
                        createCoordinate(x.doubleValue(), y.add(yint).doubleValue()),
                        createCoordinate(x.doubleValue(), y.doubleValue())));
                if (ls.coveredBy(convexHull)) {
                    l.add(ls);
                }
                y = y.add(yint);
            } while (y.doubleValue() <= e[2].y);
            x = x.add(xint);
        } while (x.doubleValue() <= e[3].x);
        return l;
    }

    public static String getLineStringName(LineString ls) {
        if (ls.getUserData() != null) {
            return ls.getUserData().toString();
        }
        Coordinate br = ((Polygon) ls.getEnvelope()).getCoordinates()[0];
        return getCoordinateDescr(br, false);
    }

    public static java.awt.Point getPointFromCoordinate(Coordinate c) {
        return new java.awt.Point((int) c.x, (int) c.y);
    }

    public static Coordinate getCoordinateFromPoint(java.awt.Point p) {
        return new Coordinate(p.x, p.y);
    }

}
