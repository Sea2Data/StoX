/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package convert;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiPolygon;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import no.imr.sea2data.imrbase.util.Conversion;
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
public class ConvertStrata {

    @Test
    public void convert() throws IOException {
        System.out.println("Convert strata test");
        LineIterator it = FileUtils.lineIterator(new File("\\\\delphi\\felles\\alle\\åsmund\\stox\\strata_norskehavstoktet\\stratum1-6 2014.txt"));
        it.nextLine(); // skip header
        List<Coordinate> coords = new ArrayList<>();
        String strata = null;
        while (it.hasNext()) {
            String line = it.nextLine().trim();
            String elms[] = line.split("\t");
            String s = elms[2];
            if (strata != null && !s.equals(strata)) {
                MultiPolygon mp = JTSUtils.createMultiPolygon(Arrays.asList(JTSUtils.createLineString(coords)));
                System.out.println(strata + "\t" + mp);
                coords.clear();
            }
            coords.add(new Coordinate(Conversion.safeStringtoDoubleNULL(elms[1]), Conversion.safeStringtoDoubleNULL(elms[0])));
            strata = s;
        }
        MultiPolygon mp = JTSUtils.createMultiPolygon(Arrays.asList(JTSUtils.createLineString(coords)));
        System.out.println(strata + "\t" + mp);
    }
}
