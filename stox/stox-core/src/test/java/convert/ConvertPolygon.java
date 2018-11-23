/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package convert;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Envelope;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;
import com.vividsolutions.jts.io.WKTWriter;
import com.vividsolutions.jts.operation.linemerge.LineMerger;
import com.vividsolutions.jts.operation.union.UnaryUnionOp;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import no.imr.sea2data.imrmap.utils.JTSUtils;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
public class ConvertPolygon {

    @Test
    public void combine() {
        try {
            List<String> wkts = Files.readAllLines(Paths.get("C:\\Users\\aasmunds\\workspace\\stox\\reference\\stratum/Kysttokt_strata.txt"));
            Map<String, String> wkt = wkts.stream()
                    .map(s -> {
                        String[] str = s.split("\\t");
                        String strata = str[0].replaceFirst("Oppdrag[\\d]*_", "");
                        return new AbstractMap.SimpleEntry<>(strata, str[1]);
                    })
                    .collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue()));

            List<String> lines = Files.readAllLines(Paths.get("E:/arved/nyekysttorsk.txt"));
            String strata = null;
            List<String> strataRefs = new ArrayList<>();
            for (String line : lines) {
                if (line.startsWith("#")) {
                    continue;
                }
                if (line.trim().isEmpty()) {
                    if (strata != null) {
                        System.out.println(strata + "\t" + combine(strata, strataRefs));
                    }
                    strata = null;
                    strataRefs.clear();
                } else if (strata != null) {
                    String wktstr = wkt.get(line);
                    if (wktstr == null) {
                        System.out.println("error " + line);
                    }
                    strataRefs.add(wktstr);
                } else {
                    strata = line;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ConvertPolygon.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String combine(String strataName, List<String> geometriesWkt) {
        GeometryFactory geoFac = new GeometryFactory();
        List<MultiPolygon> geometries = geometriesWkt.stream().map(s -> {
            WKTReader wkt = new WKTReader();
            try {
                return (MultiPolygon) wkt.read(s);
            } catch (Throwable ex) {
                return null;
            }
        }).collect(Collectors.toList());
        // Create a total list of holes
        List<Polygon> pols = new ArrayList<>();
        try {
            for (MultiPolygon g : geometries) {
                for (int ig = 0; ig < g.getNumGeometries(); ig++) {
                    Polygon pg = (Polygon) g.getGeometryN(ig);
                    pols.add(pg);
                }
            }
        } catch (Throwable e) {
            return null;
        }
        MultiPolygon mp = geoFac.createMultiPolygon(pols.toArray(new Polygon[pols.size()]));
        WKTWriter wktw = new WKTWriter();
        String un = wktw.write(mp);
        return un;
    }
}
