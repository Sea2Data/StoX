/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.biotic;

import no.imr.stox.functions.biotic.RegroupLengthDist;
import java.util.HashMap;
import java.util.Map;
import no.imr.stox.bo.LengthDistMatrix;
import no.imr.stox.functions.utils.Functions;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Test regrouping of length interval
 *
 * @author aasmunds
 */
public class RegroupLengthDistTest {

    /**
     * Test of regrouping length intervals L10=100, L11=100. Int=2 -> Lgrp10=100
     * + 100 = 200
     *
     */
    @Test
    public void test() {
        LengthDistMatrix result = (LengthDistMatrix) (new RegroupLengthDist()).perform(getInput());
        assertEquals(result.getData().getGroupRowCellValueAsDouble("havsil", "2013/1000", "10"), 200d, 0);
    }

    private Map<String, Object> getInput() {
        Map<String, Object> input = new HashMap<String, Object>();
        LengthDistMatrix lengthDist = new LengthDistMatrix();
        lengthDist.getData().setGroupRowCellValue("havsil", "2013/1000", "10", 100d);
        lengthDist.getData().setGroupRowCellValue("havsil", "2013/1000", "11", 100d);
        lengthDist.getResolutionMatrix().setRowValue(Functions.RES_LENGTHINTERVAL, 1.0);
        input.put(Functions.PM_REGROUPLENGTHDIST_LENGTHDIST, lengthDist);
        input.put(Functions.PM_REGROUPLENGTHDIST_LENGTHINTERVAL, 2d);
        return input;
    }

}
