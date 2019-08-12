/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.density;

import no.imr.stox.functions.density.MeanDensity;
import com.vividsolutions.jts.geom.Coordinate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import no.imr.sea2data.imrmap.utils.JTSUtils;
import no.imr.stox.bo.DensityMatrix;
import no.imr.stox.util.matrix.MatrixBO;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
public class MeanDensityTest {

    @Test
    public void test() {
        Map<String, Object> input = getInput();
        // Aggregate from distance to PSU
        DensityMatrix result = perform(input, Functions.SAMPLEUNIT_PSU);
        assertEquals(result.getData().getGroupRowColCellValueAsDouble("havsil", "T1", "1", "10"), (10d * 1d + 20d * 2d) / (1d + 2d), 0.0001d);
        assertEquals(result.getData().getGroupRowColCellValueAsDouble("havsil", "T2", "1", "10"), (30d * 2d + 40d * 3d) / (2d + 3d), 0.0001d);
        // Aggregate from PSU to Strata
        input.put(Functions.PM_MEANDENSITY_DENSITY, result);
        result = perform(input, Functions.SAMPLEUNIT_STRATUM);
        assertEquals(result.getData().getGroupRowColCellValueAsDouble("havsil", "1", "1", "10"), (10d * 1d + 20d * 2d + 30d * 2d + 40d * 3d) / (1d + 2d + 2d + 3d), 0.0001d);
    }

    private DensityMatrix perform(Map<String, Object> input, String aggregation) {
        input.put(Functions.PM_MEANDENSITY_SAMPLEUNITTYPE, aggregation);
        return (DensityMatrix) (new MeanDensity()).perform(input);
    }

    private Map<String, Object> getInput() {
        Map<String, Object> input = new HashMap<>();
        ProcessDataBO pd = new ProcessDataBO();
        MatrixBO stratumPolygon = (MatrixBO) AbndEstProcessDataUtil.getStratumPolygons(pd);
        stratumPolygon.setRowValue("1", JTSUtils.createPolygon(JTSUtils.createLineString(Arrays.asList(new Coordinate(0, 0), new Coordinate(0, 1), new Coordinate(1, 2)))));
        MatrixBO psuStrata = (MatrixBO) AbndEstProcessDataUtil.getPSUStrata(pd);
        psuStrata.setRowValue("T1", "1");
        psuStrata.setRowValue("T2", "1");
        MatrixBO distPSU = (MatrixBO) AbndEstProcessDataUtil.getEDSUPSUs(pd);
        distPSU.setRowValue("2013101/100/2013-01-01/12:00:00", "T1");
        distPSU.setRowValue("2013101/101/2013-01-01/12:01:00", "T1");
        distPSU.setRowValue("2013101/102/2013-01-01/12:02:00", "T2");
        distPSU.setRowValue("2013101/103/2013-01-01/12:03:00", "T2");
        input.put(Functions.PM_MEANDENSITY_PROCESSDATA, pd);
        DensityMatrix density = new DensityMatrix();
        density.getResolutionMatrix().setRowValue(Functions.RES_LENGTHINTERVAL, 1.0);
        density.getResolutionMatrix().setRowValue(Functions.RES_SAMPLEUNITTYPE, Functions.SAMPLEUNIT_EDSU);
        density.getData().setGroupRowColCellValue("havsil", "2013101/100/2013-01-01/12:00:00", "1", "10", 10d);
        density.getData().setGroupRowColCellValue("havsil", "2013101/101/2013-01-01/12:01:00", "1", "10", 20d);
        density.getData().setGroupRowColCellValue("havsil", "2013101/102/2013-01-01/12:02:00", "1", "10", 30d);
        density.getData().setGroupRowColCellValue("havsil", "2013101/103/2013-01-01/12:03:00", "1", "10", 40d);
        density.getDistanceMatrix().setRowValue("2013101/100/2013-01-01/12:00:00", 1.0);
        density.getDistanceMatrix().setRowValue("2013101/101/2013-01-01/12:01:00", 2.0);
        density.getDistanceMatrix().setRowValue("2013101/102/2013-01-01/12:02:00", 2.0);
        density.getDistanceMatrix().setRowValue("2013101/103/2013-01-01/12:03:00", 3.0);
        density.getSampleSizeMatrix().setRowValue("2013101/100/2013-01-01/12:00:00", 1);
        density.getSampleSizeMatrix().setRowValue("2013101/101/2013-01-01/12:01:00", 1);
        density.getSampleSizeMatrix().setRowValue("2013101/102/2013-01-01/12:02:00", 1);
        density.getSampleSizeMatrix().setRowValue("2013101/103/2013-01-01/12:03:00", 1);
        input.put(Functions.PM_MEANDENSITY_DENSITY, density);
        return input;
    }

}
