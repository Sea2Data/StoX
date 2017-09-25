/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package temp;

import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Point;
import no.imr.sea2data.imrbase.math.Calc;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.sea2data.imrbase.util.ImrSort;
import no.imr.stox.functions.utils.StratumUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
@Ignore
public class TestStratum {

    @Test
    public void test() {
        MatrixBO pol = StratumUtils.getStratumPolygonByWKTFile("C:\\Users\\aasmunds\\workspace\\stox\\reference\\stratum\\MainAreaLocation.txt");
        //MatrixBO posMap = StratumUtils.getAreaLocationPositionByFile("C:\\Users\\aasmunds\\workspace\\stox\\reference\\stratumpos\\area_loc_pos.txt");
        System.out.println("omr	lok	lat	lon");
        pol.getRowKeys().stream().sorted(new ImrSort.TranslativeComparatorWithToken<>(true, "_", 2)).forEach(name -> {
            MultiPolygon mp = (MultiPolygon) pol.getRowValue(name);
            String[] s = name.split("_");
            Point p = mp.getInteriorPoint();
            System.out.println(s[0] + "\t" + s[1] + "\t" + Calc.roundTo(p.getCoordinate().y, 4) + "\t" + Calc.roundTo(p.getCoordinate().x, 4));
            /*Point2D.Double pt = (Point2D.Double) posMap.getRowValue(name);
            if (pt != null) {
                Coordinate c = new Coordinate(pt.x, pt.y);
                if (JTSUtils.within(c, mp.getCoordinates())) {
                    //System.out.println(name + " is inside");
                    mp.getInteriorPoint()
                } else {
                    System.out.println(name + c.toString() + " is outside" + mp.toString());
                    System.out.println("Centroid:" + mp.getCentroid().toString());
                    System.out.println("Interior:" + mp.getInteriorPoint().toString());
                }
            } else {
                System.out.println(name + " is missing");
            }*/
        });

    }
}
