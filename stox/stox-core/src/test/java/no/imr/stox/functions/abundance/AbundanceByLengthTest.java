/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.abundance;

import java.util.HashMap;
import java.util.Map;
import no.imr.stox.bo.AbundanceMatrix;
import no.imr.stox.bo.DensityMatrix;
import no.imr.stox.bo.PolygonAreaMatrix;
import no.imr.stox.functions.utils.Functions;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
public class AbundanceByLengthTest { 

    @Test
    public void test() {
        AbundanceMatrix result = (AbundanceMatrix) (new Abundance()).perform(getInput());
        assertEquals(result.getData().getGroupRowColCellValueAsDouble("havsil", "STRATUM1", "ESTL1", "10"), 1000.0, 1d);
    }

    private Map<String, Object> getInput() {
        Map<String, Object> input = new HashMap<String, Object>();
        DensityMatrix density = new DensityMatrix();
        density.getData().setGroupRowColCellValue("havsil", "STRATUM1", "ESTL1", "10", 10d);
        input.put(Functions.PM_ABUNDANCE_DENSITY, density);
        input.put(Functions.PM_ABUNDANCE_POLYGONAREA, getStrataPlg());
        return input;
    }

    PolygonAreaMatrix getStrataPlg() {
        PolygonAreaMatrix p = new PolygonAreaMatrix();
        p.getData().setRowValue("STRATUM1", 100d);
        return p;
    }
}
