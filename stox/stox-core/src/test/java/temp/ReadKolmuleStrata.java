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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrmap.utils.JTSUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.junit.Ignore;

/**
 *
 * @author aasmunds
 */
@Ignore
public class ReadKolmuleStrata {

    public static void main(String[] args) throws IOException {
        printStrata();
    }

    public static void printStrata() throws IOException {
        LineIterator it = FileUtils.lineIterator(new File("\\\\delphi\\felles\\alle\\åsmund\\stox\\strata_kolmule\\strata.txt"));
        it.nextLine(); // skip header
        Map<String, List<Coordinate>> coords = new HashMap<>();
        while (it.hasNext()) {
            String line = it.nextLine().trim();
            String elms[] = line.split("\t");
            List<Coordinate> c = coords.get(elms[0]);
            if (c == null) {
                c = new ArrayList<>();
                coords.put(elms[0], c);
            }
            c.add(new Coordinate(Conversion.safeStringtoDoubleNULL(elms[2]), Conversion.safeStringtoDoubleNULL(elms[1])));
        }
        for (String strata : coords.keySet()) {
            MultiPolygon mp = JTSUtils.createMultiPolygon(Arrays.asList(JTSUtils.createLineString(coords.get(strata))));
            System.out.println(strata + "\t" + mp);
        }
    }
}
