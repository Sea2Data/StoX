/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.utils;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import no.imr.stox.util.matrix.MatrixBO;
import no.imr.stox.util.base.Conversion;
import no.imr.sea2data.imrmap.utils.JTSUtils;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author aasmunds
 */
public class StratumUtils {

    public static String getStratumName(String areaCoding, String area, String loc) {
        if (areaCoding.equals(Functions.AREACODING_MAINAREA) && (area == null || area.isEmpty())
                || areaCoding.equals(Functions.AREACODING_MAINAREAANDLOCATION) && ((area == null || area.isEmpty()) || (loc == null || loc.isEmpty()))) {
            return null;
        }
        Integer areaN = Conversion.safeStringtoIntegerNULL(area);
        area = areaN == null ? area : areaN + "";
        Integer locN = Conversion.safeStringtoIntegerNULL(loc);
        loc = locN == null ? loc : locN + "";
        if (areaCoding.equals(Functions.AREACODING_MAINAREAANDLOCATION)) {
            return area + "_" + loc;
        } else if (areaCoding.equals(Functions.AREACODING_MAINAREA)) {
            return area;
        }
        return null;
    }

    public static MatrixBO getAreaLocationPositionByFile(String fileName, String areaCoding) {
        MatrixBO posMap = new MatrixBO();
        List<String> lines;
        try {
            lines = FileUtils.readLines(new File(fileName));
            // Loop through the lines
            if (lines.isEmpty()) {
                return null;
            }
            lines.remove(0); // Remove header
            for (String line : lines) {
                if (line.startsWith("#")) {
                    continue;
                }
                String[] elements = line.split("\t");
                if (elements.length == 4) {
                    String area = elements[0].trim();
                    String loc = elements[1].trim();
                    String lats = elements[2].trim();
                    String lons = elements[3].trim();
                    Double lon = Conversion.safeStringtoDoubleNULL(lons);
                    Double lat = Conversion.safeStringtoDoubleNULL(lats);
                    if (lon == null || lat == null) {
                        continue;
                    }
                    Point2D.Double pt = new Point2D.Double(lon, lat);
                    String stratum = getStratumName(areaCoding, area, loc);
                    if (stratum != null) {
                        posMap.setRowValue(stratum, pt);
                    }
                }
            }
        } catch (IOException ex) {
            return null;
        }
        return posMap;
    }

    public static MatrixBO getStratumPolygonByWKTFile(String fileName) {
        MatrixBO res = new MatrixBO();
        WKTReader wktr = new WKTReader();
        try {
            List<String> lines = FileUtils.readLines(new File(fileName));
            // Loop through the lines
            for (String line : lines) {
                if (line.startsWith("#")) {
                    continue;
                }
                String[] elements = line.split("\t");
                if (elements.length > 1) {
                    String name = elements[0].trim();
                    String wkt = elements[elements.length - 1];
                    MultiPolygon mp;
                    if (wkt.startsWith("POLYGON")) {
                        Polygon p = (Polygon) wktr.read(wkt);
                        mp = JTSUtils.createMultiPolygon(Arrays.asList(JTSUtils.createLineString(p.getCoordinates())));
                    } else {
                        mp = (MultiPolygon) wktr.read(wkt);
                    }
                    mp.setSRID(4326);
                    name = name.replace("/", "_");
                    res.setRowValue(name, mp);
                }
            }
        } catch (IOException | ParseException ex) {
        }
        return res;
    }
}
