/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.processdata;

import no.imr.stox.functions.processdata.TotalLengthDist;
import java.util.HashMap;
import java.util.Map;
import no.imr.stox.bo.LengthDistMatrix;
import no.imr.stox.util.matrix.MatrixBO;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 * Test total length distribution. 4 length groups are merged by 2 stations 1000
 * and 1001 with different assignment weights 1.0 and 3.0, The station dimension
 * is eliminated in the total dist. matrix.
 *
 * @author aasmunds
 */
public class TotalLengthDistTest {

    @Test
    public void test() {
        LengthDistMatrix result = (LengthDistMatrix) (new TotalLengthDist()).perform(getInput());
        assertEquals(result.getData().getGroupRowCellValueAsDouble("havsil", "1", "10"), 25d, 0);
        assertEquals(result.getData().getGroupRowCellValueAsDouble("havsil", "1", "12"), 75d, 0);
    }

    private Map<String, Object> getInput() {
        Map<String, Object> input = new HashMap<String, Object>();
        LengthDistMatrix lengthDist = new LengthDistMatrix();
        lengthDist.getResolutionMatrix().setRowValue(Functions.RES_LENGTHDISTTYPE, Functions.LENGTHDISTTYPE_PERCENTLENGHTDIST);
        lengthDist.getData().setGroupRowCellValue("havsil", "2013/1000", "10", 100d);
        lengthDist.getData().setGroupRowCellValue("havsil", "2013/1000", "11", 100d);
        lengthDist.getData().setGroupRowCellValue("havsil", "2013/1001", "12", 100d);
        lengthDist.getData().setGroupRowCellValue("havsil", "2013/1001", "13", 100d);
        input.put(Functions.PM_TOTALLENGTHDIST_LENGTHDIST, lengthDist);
        ProcessDataBO pd = new ProcessDataBO();
        MatrixBO trawlAsg = (MatrixBO) AbndEstProcessDataUtil.getBioticAssignments(pd);
        trawlAsg.setRowColValue("1", "2013/1000", 1.0);
        trawlAsg.setRowColValue("1", "2013/1001", 3.0);
        input.put(Functions.PM_TOTALLENGTHDIST_PROCESSDATA, pd);
        return input;
    }
}
