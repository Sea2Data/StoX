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
        LengthDistMatrix mbo = (LengthDistMatrix) (new StationLengthDist()).perform(getInput(1.0, true, true, getFishStationsMissingDistance()));
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
        LengthDistMatrix mbo = (LengthDistMatrix) (new StationLengthDist()).perform(getInput(1.0, true, true, getFishStationsMissingSampledWeight()));
        assertEquals(mbo.getData().getGroupRowCellValueAsDouble("havsil", "2013/1000", "10"), 50d, 0);
        fail("This should have thrown a nullpointer exception by now");
    }*/
    LengthDistMatrix perform(Boolean inPercent, Boolean normToDist) {
        return (LengthDistMatrix) (new StationLengthDist()).perform(getInput(inPercent, normToDist, getFishStations()));
    }

    private Map<String, Object> getInput(Boolean inPercent, Boolean normToDist, List<MissionBO> missions) {
        Map<String, Object> input = new HashMap<>();
        input.put(Functions.PM_STATIONLENGTHDIST_BIOTICDATA, missions);
        //input.put(Functions.PM_STATIONLENGTHDIST_LENGTHINTERVAL, lenInterval);
        input.put(Functions.PM_STATIONLENGTHDIST_LENGTHDISTTYPE, inPercent ? Functions.LENGTHDISTTYPE_PERCENTLENGHTDIST
                : normToDist ? Functions.LENGTHDISTTYPE_NORMLENGHTDIST : Functions.LENGTHDISTTYPE_LENGHTDIST);
        return input;
    }

    List<MissionBO> getFishStations() {
        MissionBO m = new MissionBO();
        m.getMs().setCruise("2013");
        FishstationBO f = m.addFishstation(null);
        f.getFs().setDistance(1.5d);
        f.getFs().setSerialnumber(1000);
        CatchSampleBO s = f.addCatchSample(null);
        s.getCs().setCatchcategory("havsil");
        s.getCs().setCatchweight(100.0);
        s.getCs().setLengthsampleweight(50.0);
        s.getCs().setCatchpartnumber(Integer.SIZE);
        IndividualBO i = s.addIndividual(null);
        i.setLength(0.1);
        i = s.addIndividual(null);
        i.setLength(0.11);
        return Arrays.asList(m);
    }

    List<MissionBO> getFishStationsMissingSampledWeight() {
        MissionBO ms = new MissionBO();
        ms.getMs().setCruise("2013");
        FishstationBO f = ms.addFishstation(null);
        f.getFs().setDistance(1.5d);
        f.getFs().setSerialnumber(1000);
        CatchSampleBO s = f.addCatchSample(null);
        s.getCs().setCatchcategory("havsil");
        s.getCs().setCatchweight(100.0);
        s.getCs().setCatchpartnumber(Integer.SIZE);
        IndividualBO i = s.addIndividual(null);
        i.setLength(0.1);
        i = s.addIndividual(null);
        i.setLength(0.11);
        return Arrays.asList(ms);
    }

    List<MissionBO> getFishStationsMissingDistance() {
        MissionBO ms = new MissionBO();
        ms.getMs().setCruise("2013");
        
        FishstationBO f = ms.addFishstation(null);
        f.getFs().setSerialnumber(1000);
        f.getFs().setDistance(1.5d);
        CatchSampleBO s = f.addCatchSample(null);
        s.getCs().setCatchcategory("havsil");
        s.getCs().setCatchweight(100.0);
        s.getCs().setLengthsampleweight(50.0);
        s.getCs().setCatchpartnumber(Integer.SIZE);
        IndividualBO i = s.addIndividual(null);
        i.setLength(0.1);
        i = s.addIndividual(null);
        i.setLength(0.11);
        
        f = ms.addFishstation(null);
        f.getFs().setSerialnumber(1001);
        s = f.addCatchSample(null);
        s.getCs().setCatchcategory("havsil");
        s.getCs().setCatchweight(100.0);
        s.getCs().setLengthsampleweight(50.0);
        s.getCs().setCatchpartnumber(Integer.SIZE);
        i = s.addIndividual(null);
        i.setLength(0.1);
        i = s.addIndividual(null);
        i.setLength(0.11);
        return Arrays.asList(ms);
    }
}
