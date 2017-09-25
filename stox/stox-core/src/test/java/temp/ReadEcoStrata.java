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
import java.util.stream.Collectors;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrbase.util.ImrSort;
import no.imr.sea2data.imrmap.utils.JTSUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.LineIterator;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
@Ignore
public class ReadEcoStrata {

    @Test
    public void printStrata() throws IOException {
        LineIterator it = FileUtils.lineIterator(new File("F:\\MetteMauritsen\\ECOSTRATA.csv"));
        it.nextLine(); // skip header
        Map<String, List<Coordinate>> coords = new HashMap<>();
        it.nextLine();
        while (it.hasNext()) {
            String line = it.nextLine().trim().replace("\"", "");
            String elms[] = line.split(";");
            List<Coordinate> c = coords.get(elms[0]);
            if (c == null) {
                c = new ArrayList<>();
                coords.put(elms[0], c);
            }
            c.add(new Coordinate(Conversion.safeStringtoDoubleNULL(elms[2]), Conversion.safeStringtoDoubleNULL(elms[1])));
        }
        for (String strata : coords.keySet().stream().sorted((String s1, String s2) -> {
            return ImrSort.compareTranslative(s1, s2);
        }).collect(Collectors.toList())) {
            MultiPolygon mp = JTSUtils.createMultiPolygon(Arrays.asList(JTSUtils.createLineString(coords.get(strata))));
            System.out.println(strata + "\t" + mp);
        }
    }
}
