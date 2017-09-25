/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stratum;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.imr.sea2data.imrmap.utils.JTSUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
@Ignore       
public class square {

    @Test
    public void testpol() {
        try {
            WKTReader wktr = new WKTReader();
            MultiPolygon mp = (MultiPolygon)wktr.read("MULTIPOLYGON(((40 40, 20 45, 45 30, 40 40)),((20 35, 10 30, 10 10, 30 5, 45 20, 20 35),(30 20, 20 15, 20 25, 30 20)))"); // ((40 40, 20 45, 45 30, 40 40)),((20 35, 10 30, 10 10, 30 5, 45 20, 20 35),(30 20, 20 15, 20 25, 30 20))
            Double p = 0d;
            for(int nG = 0; nG < mp.getNumGeometries(); nG++) {
                Polygon pol = (Polygon) mp.getGeometryN(nG);
                double polv = JTSUtils.polygonArea(pol.getExteriorRing().getCoordinates());
                for(int nInt = 0; nInt < pol.getNumInteriorRing(); nInt++) {
                    double intv = JTSUtils.polygonArea(pol.getInteriorRingN(nInt).getCoordinates());
                    polv -= intv;
                }
                p += polv;
            }
            System.out.println(p);
        } catch (ParseException ex) {
            Logger.getLogger(square.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    //@Test
    public void test() {

        for (int lon = 0; lon <= 90; lon += 2) {
            for (int lat = 66; lat <= 81; lat++) {
                System.out.println((lon + "_" + lat).replace("-", "_") + "\tMULTIPOLYGON((("
                        + lon + " " + lat + ","
                        + (lon + 2) + " " + lat + ","
                        + (lon + 2) + " " + (lat + 1) + ","
                        + lon + " " + (lat + 1) + ","
                        + lon + " " + lat + ")))");
            }
        }
    }

}
