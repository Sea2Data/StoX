/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.processdata;

import no.imr.stox.functions.processdata.DefineRectangle;
import com.vividsolutions.jts.geom.Coordinate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.imrbase.util.IMRdate;
import no.imr.sea2data.imrmap.utils.JTSUtils;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Åsmund
 */
public class DefineRectangleTest {

    @Test
    public void test() {
        Map<String, Object> input = getInput();
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_DEFINERECTANGLE_PROCESSDATA);
        (new DefineRectangle()).perform(input);
        System.out.println(pd.asTable());
        assertEquals(10, AbndEstProcessDataUtil.getPSUStrata(pd).getRowKeys().size());
        assertEquals("1", AbndEstProcessDataUtil.getPSUStratum(pd, "01000020010203"));
    }

    private Map<String, Object> getInput() {
        Map<String, Object> input = new HashMap<>();
        input.put(Functions.PM_DEFINERECTANGLE_HEIGHT, 1.0);
        input.put(Functions.PM_DEFINERECTANGLE_WIDTH, 2.0);
        input.put(Functions.PM_DEFINERECTANGLE_ACOUSTICDATA, getDistances());
        ProcessDataBO pd = new ProcessDataBO();
       AbndEstProcessDataUtil.setStratumPolygon(pd, "1", true, JTSUtils.createMultiPolygon(Arrays.asList(JTSUtils.createLineString(Arrays.asList(
                  new Coordinate(-2.5, 2),
                new Coordinate(0, 2),
                new Coordinate(0, 1),
                new Coordinate(2.7, 1),
                new Coordinate(2.6, -1),
                new Coordinate(-2.4, -1)
        )))));
        input.put(Functions.PM_DEFINERECTANGLE_PROCESSDATA, pd);
        return input;
    }

    List<DistanceBO> getDistances() {
        List<DistanceBO> distances = new ArrayList<>();
        String cruise = "2013101";
        DistanceBO d = new DistanceBO();
        d.setCruise(cruise);
        d.setLog_start(100d);
        d.setStart_time(IMRdate.encodeDate(2013, 1, 1, 12, 0, 0, true));
        d.setLat_start(0.25);
        d.setLon_start(-0.25);
        distances.add(d);
        d = new DistanceBO();
        d.setCruise(cruise);
        d.setLog_start(101d);
        d.setStart_time(IMRdate.encodeDate(2013, 1, 1, 12, 1, 0, true));
        d.setLat_start(-0.25);
        d.setLon_start(0.25);
        distances.add(d);
        return distances;
    }
}
