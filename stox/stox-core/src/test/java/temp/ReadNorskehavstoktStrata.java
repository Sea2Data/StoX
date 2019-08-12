/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package temp;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiPolygon;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import no.imr.stox.util.base.Conversion;
import no.imr.sea2data.imrmap.utils.JTSUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.junit.Ignore;

/**
 * Convert Beam main area to polygon
 *
 * @author aasmunds
 */
@Ignore
public class ReadNorskehavstoktStrata {

    public static void main(String[] args) throws IOException {
        for (int i = 1; i <= 6; i++) {
            printStrata(i);
        }
    }

    public static void printStrata(Integer i) throws IOException {
        LineIterator it = FileUtils.lineIterator(new File("\\\\delphi\\felles\\alle\\åsmund\\stox\\strata_norskehavstoktet\\stratum" + i + ".txt"));
        it.nextLine(); // skip header
        List<Coordinate> coords = new ArrayList<>();
        while (it.hasNext()) {
            String line = it.nextLine().trim();
            String elms[] = line.split("\t");
            coords.add(new Coordinate(Conversion.safeStringtoDoubleNULL(elms[1]), Conversion.safeStringtoDoubleNULL(elms[0])));
        }
        MultiPolygon mp = JTSUtils.createMultiPolygon(Arrays.asList(JTSUtils.createLineString(coords)));
        System.out.println(i + "\t" + mp);
    }
}
