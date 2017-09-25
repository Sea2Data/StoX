/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.density;

import no.imr.stox.functions.density.SumDensity;
import java.util.HashMap;
import java.util.Map;
import no.imr.stox.bo.DensityMatrix;
import no.imr.stox.functions.utils.AbndEstParamUtil;
import no.imr.stox.functions.utils.Functions;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
public class SumDensityTest {

    @Test
    public void test() {
        Map<String, Object> input = getInput();
        DensityMatrix result = (DensityMatrix) (new SumDensity()).perform(input);
        // Aggregate from channels to estimation layers
        assertEquals(result.getData().getGroupRowColCellValueAsDouble("havsil", "2013101/100/2013-01-01/12:00:00", "E1", "10"), 10d + 20d, 0d);
    }

    private Map<String, Object> getInput() {
        Map<String, Object> input = new HashMap<String, Object>();
        DensityMatrix density = new DensityMatrix();
        density.getData().setGroupRowColCellValue("havsil", "2013101/100/2013-01-01/12:00:00", "1", "10", 10d);
        density.getData().setGroupRowColCellValue("havsil", "2013101/100/2013-01-01/12:00:00", "2", "10", 20d);
        input.put(Functions.PM_SUMDENSITY_DENSITY, density);
        density.setEstLayerDefMatrix(AbndEstParamUtil.getEstLayerMatrixFromEstLayerDef("E1~1-16"));
        return input;
    }
}
