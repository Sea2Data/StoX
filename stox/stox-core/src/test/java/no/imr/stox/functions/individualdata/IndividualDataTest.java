/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.individualdata;

import no.imr.stox.functions.individualdata.IndividualData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.biotic.bo.SampleBO;
import no.imr.stox.bo.IndividualDataMatrix;
import no.imr.stox.bo.IndividualDataStationsMatrix;
import no.imr.stox.functions.utils.Functions;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
public class IndividualDataTest {

    @Test
    public void test() {
        Map<String, Object> input = getInput();
        IndividualDataMatrix result = (IndividualDataMatrix) (new IndividualData()).perform(input);
        assertEquals(((List) result.getData().getGroupRowColCellValue("havsil", "ST1", "E1", "10")).size(), 1);
    }

    private Map<String, Object> getInput() {
        Map<String, Object> input = new HashMap<>();
        input.put(Functions.PM_INDIVIDUALDATA_BIOTICDATA, getFishStations());
        IndividualDataStationsMatrix indDataSel = new IndividualDataStationsMatrix();
        indDataSel.getData().setRowColCellValue("ST1", "E1", "2013/1000", true);
        input.put(Functions.PM_INDIVIDUALDATA_INDIVIDUALDATASTATIONS, indDataSel);
        indDataSel.getResolutionMatrix().setRowValue(Functions.RES_LENGTHINTERVAL, 1.0);
        return input;
    }

    List<FishstationBO> getFishStations() {
        List<FishstationBO> fs = new ArrayList<FishstationBO>();
        FishstationBO f = new FishstationBO();
        fs.add(f);
        f.setDistance(1.5d);
        f.setYear(2013);
        f.setSerialNo(1000);
        SampleBO s = new SampleBO();
        s.setTotalWeight(100.0);
        s.setSampledWeight(50.0);
        f.addSample("havsil", s);
        s.setSampleNo(Integer.SIZE);
        IndividualBO i = new IndividualBO();
        i.setIndividualNo(1);
        i.setSample(s);
        i.setLength(10.0);
        i.setWeight(0.005);
        s.getIndividualBOCollection().add(i);
        return fs;
    }

}