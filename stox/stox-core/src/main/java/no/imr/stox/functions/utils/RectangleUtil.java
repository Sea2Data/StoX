package no.imr.stox.functions.utils;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import no.imr.sea2data.imrbase.map.LatLonUtil;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrmap.utils.JTSUtils;

/**
 * Util class to work with rectangles
 *
 * @author Åsmund
 */
public final class RectangleUtil {

    /**
     * Hidden constructor
     */
    private RectangleUtil() {
    }

    /**
     * return the coordinates from psu code 8100680010200 =
     * MULTIPOLYGON(((68,82, 70,82, 70,81, 68,81))) with =2.0, height =1.0,
     * quadrant=0
     *
     *
     * @param rKey
     * @return
     */
    public static Coordinate[] getCoordsByRectangleKey(String rKey) {
        return RectangleUtil.getCoordsByRectangleKey(rKey, 14);
    }

    public static Coordinate[] getCoordsByRectangleKey(String rKey, int len) {
        if (rKey == null || rKey.length() != len) {
            return null;
        }
        int x = len - 13;
        Integer qdr = Conversion.safeStringtoIntegerNULL(rKey.substring(12 + x, 13 + x));
        Boolean neglat = qdr == 2 || qdr == 3;
        Boolean neglon = qdr == 1 || qdr == 2;
        Double lat = LatLonUtil.getLatOrLon(neglat, Conversion.safeStringtoInteger(rKey.substring(0, 2)),
                Conversion.safeStringtoDouble(rKey.substring(2, 4)));
        Double lon = LatLonUtil.getLatOrLon(neglon, Conversion.safeStringtoInteger(rKey.substring(4, 6 + x)),
                Conversion.safeStringtoDouble(rKey.substring(6 + x, 8 + x)));
        Double h = Conversion.safeStringtoDouble(rKey.substring(8 + x, 10 + x)) * 0.1;
        Double w = Conversion.safeStringtoDouble(rKey.substring(10 + x, 12 + x)) * 0.1;
        return new Coordinate[]{new Coordinate(lon, lat + h), new Coordinate(lon + w, lat + h), new Coordinate(lon + w, lat), new Coordinate(lon, lat)};
    }

    /**
     * return the PSU code Lon_Lat_Width_Height from position and width and
     * height 8100680010200
     *
     * @param lon
     * @param lat
     * @param w
     * @param h
     * @return
     */
    public static String getPSUByPosition(Double lon, Double lat, Double w, Double h) {

        lon = floorInt(lon, w);
        lat = floorInt(lat, h);
        int qdr = lon >= 0 && lat >= 0 ? 0 : lon < 0 && lat >= 0 ? 1 : lon < 0 && lat < 0 ? 2 : 3;
        int ilat = (int) Math.abs(lat);
        int ilon = (int) Math.abs(lon);
        int mlat = (int) (60 * (Math.abs(lat) - ilat));
        int mlon = (int) (60 * (Math.abs(lon) - ilon));
        return String.format("%02d%02d%03d%02d%02d%02d%1d", ilat, mlat, ilon, mlon, (int) (h * 10), (int) (w * 10), qdr);
    }
    private static GeometryFactory gf = new GeometryFactory();

    /**
     *
     * @param psu
     * @param strataPol
     * @return the psu rectangle polygon clipped by the strata polygon.
     */
    public static MultiPolygon getPSUPolygon(String psu, MultiPolygon strataPol) {
        Coordinate[] coords = RectangleUtil.getCoordsByRectangleKey(psu);
        Geometry gm = JTSUtils.createPolygon(JTSUtils.createLineString(coords));
        if (!gm.intersects(strataPol)) {
            return null;
        }
        Geometry gmi = gm.intersection(strataPol);
        if (gmi instanceof Polygon) {
            return new MultiPolygon(new Polygon[]{(Polygon) gmi}, gf);
        }
        if (gmi instanceof MultiPolygon) {
            return (MultiPolygon) gmi;
        }
        return null;
    }

    public static Double floorInt(Double d, double intv) {
        if (d < 0) {
            d -= intv * 0.9999999;
        }
        d = d / intv;
        d = d < 0 ? Math.ceil(d - 0.0000000000001) : Math.floor(d + 0.0000000000001);
        return d * intv;
    }

    public static Double ceilInt(Double d, double intv) {
        return floorInt(d, intv) + intv;
    }
}
