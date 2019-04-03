/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.biotic;

import BioticTypes.v3.MissionType;
import no.imr.stox.functions.biotic.StationLengthDist;
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
import no.imr.stox.bo.LengthDistMatrix;
import no.imr.stox.functions.utils.Functions;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author Åsmund
 */
public class StationLengthDistTest {

    @Test
    public void test() {
        assertEquals(perform(true, false).getData().getGroupRowCellValueAsDouble("havsil", "2013/1000", "10"), 50d, 0);
        assertEquals(perform(false, false).getData().getGroupRowCellValueAsDouble("havsil", "2013/1000", "10"), 2d, 0);
        assertEquals(perform(false, true).getData().getGroupRowCellValueAsDouble("havsil", "2013/1000", "10"), 1.333333d, 0.01);
        assertEquals(perform(true, true).getData().getGroupRowCellValueAsDouble("havsil", "2013/1000", "10"), 50d, 0);
    }

    /*@Test(expected = NullPointerException.class)
    public void testDataGoesMissingBecauseOfMissingDistance() {
        LengthDistMatrix mbo = (LengthDistMatrix) (new StationLengthDist()).perform(getInput(1.0, true, true, getMissionsMissingDistance()));
        assertEquals(mbo.getData().getGroupRowCellValueAsDouble("havsil", "2013/1000", "10"), 50d, 0);
        MatrixBO test = (MatrixBO) mbo.getData().getGroupValue("havsil");
        for (String key : test.getKeys()) {
            System.out.println(key);
        }
        assertEquals(mbo.getData().getGroupRowCellValueAsDouble("havsil", "2013/1001", "10"), 50d, 0);
        fail("This should have thrown a nullpointer exception by now");
    }

    @Test(expected = NullPointerException.class)
    public void testDataGoesMissingBecauseOfMissingSampleWeight() {
        LengthDistMatrix mbo = (LengthDistMatrix) (new StationLengthDist()).perform(getInput(1.0, true, true, getMissionsMissingSampledWeight()));
        assertEquals(mbo.getData().getGroupRowCellValueAsDouble("havsil", "2013/1000", "10"), 50d, 0);
        fail("This should have thrown a nullpointer exception by now");
    }*/
    LengthDistMatrix perform(Boolean inPercent, Boolean normToDist) {
        return (LengthDistMatrix) (new StationLengthDist()).perform(getInput(inPercent, normToDist, getMissions()));
    }

    private Map<String, Object> getInput(Boolean inPercent, Boolean normToDist, BioticData missions) {
        Map<String, Object> input = new HashMap<>();
        input.put(Functions.PM_STATIONLENGTHDIST_BIOTICDATA, missions);
        //input.put(Functions.PM_STATIONLENGTHDIST_LENGTHINTERVAL, lenInterval);
        input.put(Functions.PM_STATIONLENGTHDIST_LENGTHDISTTYPE, inPercent ? Functions.LENGTHDISTTYPE_PERCENTLENGHTDIST
                : normToDist ? Functions.LENGTHDISTTYPE_NORMLENGHTDIST : Functions.LENGTHDISTTYPE_LENGHTDIST);
        return input;
    }

    BioticData getMissions() {
        MissionBO m = new MissionBO();
        m.bo().setCruise("2013");
        FishstationBO f = m.addFishstation();
        f.bo().setDistance(1.5d);
        f.bo().setSerialnumber(1000);
        CatchSampleBO s = f.addCatchSample();
        s.bo().setCatchcategory("havsil");
        s.setSpecCat(s.bo().getCatchcategory());
        s.bo().setCatchweight(100.0);
        s.bo().setLengthsampleweight(50.0);
        s.bo().setCatchpartnumber(1);
        IndividualBO i = s.addIndividual();
        i.setLengthCM(10d);
        i = s.addIndividual();
        i.setLengthCM(11d);
        BioticData missions = new BioticData();
        missions.getMissions().add(m);
        return missions;
    }

    List<MissionBO> getMissionsMissingSampledWeight() {
        MissionBO ms = new MissionBO();
        ms.bo().setCruise("2013");
        FishstationBO f = ms.addFishstation();
        f.bo().setDistance(1.5d);
        f.bo().setSerialnumber(1000);
        CatchSampleBO s = f.addCatchSample();
        s.bo().setCatchcategory("havsil");
        s.bo().setCatchweight(100.0);
        s.bo().setCatchpartnumber(1);
        IndividualBO i = s.addIndividual();
        i.setLengthCM(10d);
        i = s.addIndividual();
        i.setLengthCM(11d);
        return Arrays.asList(ms);
    }

    List<MissionBO> getMissionsMissingDistance() {
        MissionBO ms = new MissionBO();
        ms.bo().setCruise("2013");
        
        FishstationBO f = ms.addFishstation();
        f.bo().setSerialnumber(1000);
        f.bo().setDistance(1.5d);
        CatchSampleBO s = f.addCatchSample();
        s.bo().setCatchcategory("havsil");
        s.setSpecCat(s.bo().getCatchcategory());
        s.bo().setCatchweight(100.0);
        s.bo().setLengthsampleweight(50.0);
        s.bo().setCatchpartnumber(Integer.SIZE);
        IndividualBO i = s.addIndividual();
        i.setLengthCM(10d);
        i = s.addIndividual();
        i.setLengthCM(11d);
        
        f = ms.addFishstation();
        f.bo().setSerialnumber(1001);
        s = f.addCatchSample();
        s.bo().setCatchcategory("havsil");
        s.setSpecCat(s.bo().getCatchcategory());
        s.bo().setCatchweight(100.0);
        s.bo().setLengthsampleweight(50.0);
        s.bo().setCatchpartnumber(Integer.SIZE);
        i = s.addIndividual();
        i.setLengthCM(10d);
        i = s.addIndividual();
        i.setLengthCM(11d);
        return Arrays.asList(ms);
    }
}
