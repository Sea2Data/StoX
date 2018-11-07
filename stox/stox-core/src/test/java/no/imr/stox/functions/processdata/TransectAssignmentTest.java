/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.processdata;

import BioticTypes.v3.MissionType;
import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiPolygon;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.MissionBO;
import no.imr.sea2data.imrmap.utils.JTSUtils;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test transect assignment of trawl stations to psu transects using strata
 * polygon boundaries.
 *
 * @author Åsmund
 */
@Ignore
public class TransectAssignmentTest {

    @Test
    public void test() {
        Map<String, Object> input = getInput();
        (new BioStationAssignment()).perform(input);
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_BIOSTATIONASSIGNMENT_PROCESSDATA);
        assertEquals(AbndEstProcessDataUtil.getSUAssignment(pd, "2013101/100/2013-01-01/12:00:00", "3"), "1");
        assertEquals(AbndEstProcessDataUtil.getBioticAssignments(pd).getRowColValue("1", "2013/1000"), 1.0);
        //assertEquals(perform(1.0, true, false).getGroupRowCellValueAsDouble("havsil", "2013/1000", "10"), 50d, 0);
    }

    private Map<String, Object> getInput() {
        Map<String, Object> input = new HashMap<String, Object>();
        input.put(Functions.PM_BIOSTATIONASSIGNMENT_BIOTICDATA, getMissions());
        ProcessDataBO pd = new ProcessDataBO();
        AbndEstProcessDataUtil.setStratumPolygon(pd, "1",true,  getStrataPlg());
        MatrixBO psuStrata = (MatrixBO) AbndEstProcessDataUtil.getPSUStrata(pd);
        psuStrata.setRowValue("T1", "1");
        psuStrata.setRowValue("T2", "1");
        MatrixBO distPSU = (MatrixBO) AbndEstProcessDataUtil.getEDSUPSUs(pd);
        distPSU.setRowValue("2013101/100/2013-01-01/12:00:00", "T1");
        input.put(Functions.PM_BIOSTATIONASSIGNMENT_PROCESSDATA, pd);
        return input;
    }

    MultiPolygon getStrataPlg() {
        return JTSUtils.createMultiPolygon(Arrays.asList(JTSUtils.createLineString(Arrays.asList(
                new Coordinate(0d, 0d), new Coordinate(2d, 0d), new Coordinate(2d, 2d), new Coordinate(0d, 2d)))));
    }

    List<MissionBO> getMissions() {
        MissionBO mt = new MissionBO();
        FishstationBO f = mt.addFishstation();
        f.bo().setLongitudestart(1.1d);
        f.bo().setLatitudestart(0.9d);
        f.bo().setSerialnumber(1000);
        
        f = mt.addFishstation();
        f.bo().setLongitudestart(2.9d);
        f.bo().setLatitudestart(0.9d);
        f.bo().setSerialnumber(1000);
        return Arrays.asList(mt);
    }

}
