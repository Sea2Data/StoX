/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.individualdata;

import BioticTypes.v3.MissionType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.biotic.bo.CatchSampleBO;
import no.imr.sea2data.biotic.bo.MissionBO;
import no.imr.stox.bo.BioticData;
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
        input.put(Functions.PM_INDIVIDUALDATA_BIOTICDATA, getMissions());
        IndividualDataStationsMatrix indDataSel = new IndividualDataStationsMatrix();
        indDataSel.getData().setRowColCellValue("ST1", "E1", "2013/1000", true);
        input.put(Functions.PM_INDIVIDUALDATA_INDIVIDUALDATASTATIONS, indDataSel);
        indDataSel.getResolutionMatrix().setRowValue(Functions.RES_LENGTHINTERVAL, 1.0);
        return input;
    }

    BioticData getMissions() {
        MissionBO mt = new MissionBO();
        mt.bo().setCruise("2013");
        FishstationBO f = mt.addFishstation();
        f.bo().setDistance(1.5d);
        f.bo().setSerialnumber(1000);
        CatchSampleBO s = f.addCatchSample();
        s.bo().setCatchcategory("havsil");
        s.setSpecCat(s.bo().getCatchcategory());
        s.bo().setCatchweight(100.0);
        s.bo().setLengthsampleweight(50.0);
        s.bo().setCatchpartnumber(Integer.SIZE);
        IndividualBO i = s.addIndividual();
        i.bo().setSpecimenid(1);
        i.setLengthCM(10d);
        i.setIndividualWeightG(0.5d);
        BioticData missions = new BioticData();
        missions.getMissions().add(mt);
        return missions;
    }

}
