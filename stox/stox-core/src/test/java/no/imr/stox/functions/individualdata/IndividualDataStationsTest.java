/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.individualdata;

import no.imr.stox.functions.individualdata.IndividualDataStations;
import java.util.HashMap;
import java.util.Map;
import no.imr.stox.bo.AbundanceMatrix;
import no.imr.stox.bo.IndividualDataStationsMatrix;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
public class IndividualDataStationsTest {

    @Test
    public void test() {
        Map<String, Object> input = getInput();
        IndividualDataStationsMatrix result = (IndividualDataStationsMatrix) (new IndividualDataStations()).perform(input);
        assertTrue((Boolean) result.getData().getRowColCellValue("ST1", "E1", "2013/1001"));
    }

    private Map<String, Object> getInput() {
        Map<String, Object> input = new HashMap<>();
        // ModelParameters with est layers definition
        ProcessDataBO pd = new ProcessDataBO();
        MatrixBO trawlAsg = (MatrixBO) AbndEstProcessDataUtil.getBioticAssignments(pd);
        trawlAsg.setRowColValue("ASG1", "2013/1001", 1.0);
        MatrixBO suAsg = (MatrixBO) AbndEstProcessDataUtil.getSUAssignments(pd);
        suAsg.setRowColValue("2013101/100/2013-01-01/12:00:00", "E1", "ASG1");
        MatrixBO distPSU = (MatrixBO) AbndEstProcessDataUtil.getEDSUPSUs(pd);
        distPSU.setRowValue("2013101/100/2013-01-01/12:00:00", "T1");
        MatrixBO psuStrata = (MatrixBO) AbndEstProcessDataUtil.getPSUStrata(pd);
        psuStrata.setRowValue("T1", "ST1");
        MatrixBO resolution = (MatrixBO) AbndEstProcessDataUtil.getAssignmentResolutions(pd);
        resolution.setRowValue(Functions.RES_SAMPLEUNITTYPE, Functions.SAMPLEUNIT_EDSU);
        input.put(Functions.PM_INDIVIDUALDATASTATIONS_PROCESSDATA, pd);
        AbundanceMatrix abnd = new AbundanceMatrix();
        //abnd.getEstLayerDefMatrix().setRowValue("E1", "1-16");
        input.put(Functions.PM_INDIVIDUALDATASTATIONS_ABUNDANCE, abnd);
        return input;
    }
}
