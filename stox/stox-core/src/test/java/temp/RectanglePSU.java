/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package temp;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryCollection;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import no.imr.sea2data.imrmap.utils.JTSUtils;
import no.imr.stox.functions.utils.RectangleUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.junit.Ignore;

/**
 * Convert Beam main area to polygon
 *
 * @author aasmunds
 */
@Ignore
public class RectanglePSU {

    public static void main(String[] args) throws IOException {
        System.out.println("test");
        LineIterator it = FileUtils.lineIterator(new File("\\\\delphi\\felles\\alle\\åsmund\\stox\\Beam5.2\\gis\\gisdat\\TMP\\nabmain.txt"));
        // Loop through the lines
        List<LineString> ls = new ArrayList<>();
        //List<Geometry> gl = new ArrayList<>();
        Map<String, List<Coordinate[]>> mpumap = new HashMap<>();
        it.nextLine();
        while (it.hasNext()) {
            String line = it.nextLine().trim();
            String elms[] = line.split("\t");
            String rect = elms[0];
            String mainarea = elms[1].trim();
            if (mainarea.isEmpty() || mainarea.equals("99")) {
                continue;
            }
            Coordinate[] coords = RectangleUtil.getCoordsByRectangleKey(rect);
            //Geometry mp = JTSUtils.createPolygon(JTSUtils.createLineString(coords));
            List<Coordinate[]> mpu = mpumap.get(mainarea);
            if (mpu == null) {
                mpu = new ArrayList<>();
                mpumap.put(mainarea, mpu);
            }
            mpu.add(coords);
        }
        GeometryFactory gf = new GeometryFactory();
        for (Entry<String, List<Coordinate[]>> e : mpumap.entrySet()) {
            List<Coordinate[]> mpu = e.getValue();
            Geometry[] gl = new Geometry[mpu.size()];
            for (int i = 0; i < mpu.size(); i++) {
                gl[i] = JTSUtils.createPolygon(JTSUtils.createLineString(mpu.get(i)));
            }
            GeometryCollection polygonCollection = gf.createGeometryCollection(gl);
            Geometry union = polygonCollection.buffer(0);
            if(union instanceof Polygon) {
                union = new MultiPolygon(new Polygon[]{(Polygon)union}, gf);
            }
            System.out.println(e.getKey() + "\t" + union);
            /*for (int i = 0; i < union.getNumGeometries(); i++) {
             Geometry m = union.getGeometryN(i);
             System.out.println(e.getKey() + " " + m);
             }*/
        }
    }
}
