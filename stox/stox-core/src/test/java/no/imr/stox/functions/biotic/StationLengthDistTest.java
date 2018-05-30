/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.biotic;

import no.imr.stox.functions.biotic.StationLengthDist;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.biotic.bo.SampleBO;
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

    private Map<String, Object> getInput(Boolean inPercent, Boolean normToDist, List<FishstationBO> fishStations) {
        Map<String, Object> input = new HashMap<>();
        input.put(Functions.PM_STATIONLENGTHDIST_BIOTICDATA, fishStations);
        //input.put(Functions.PM_STATIONLENGTHDIST_LENGTHINTERVAL, lenInterval);
        input.put(Functions.PM_STATIONLENGTHDIST_LENGTHDISTTYPE, inPercent ? Functions.LENGTHDISTTYPE_PERCENTLENGHTDIST
                : normToDist ? Functions.LENGTHDISTTYPE_NORMLENGHTDIST : Functions.LENGTHDISTTYPE_LENGHTDIST);
        return input;
    }

    List<FishstationBO> getFishStations() {
        List<FishstationBO> fs = new ArrayList<>();
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
        i.setLength(10.0);
        s.getIndividualBOCollection().add(i);
        i = new IndividualBO();
        i.setLength(11.0);
        s.getIndividualBOCollection().add(i);
        return fs;
    }

    List<FishstationBO> getFishStationsMissingSampledWeight() {
        List<FishstationBO> fs = new ArrayList<FishstationBO>();
        FishstationBO f = new FishstationBO();
        fs.add(f);
        f.setDistance(1.5d);
        f.setYear(2013);
        f.setSerialNo(1000);
        SampleBO s = new SampleBO();
        s.setTotalWeight(100.0);
        f.addSample("havsil", s);
        s.setSampleNo(Integer.SIZE);
        IndividualBO i = new IndividualBO();
        i.setLength(10.0);
        s.getIndividualBOCollection().add(i);
        i = new IndividualBO();
        i.setLength(11.0);
        s.getIndividualBOCollection().add(i);
        return fs;
    }

    List<FishstationBO> getFishStationsMissingDistance() {
        List<FishstationBO> fs = new ArrayList<FishstationBO>();
        FishstationBO f = new FishstationBO();
        fs.add(f);
        f.setYear(2013);
        f.setSerialNo(1000);
        f.setDistance(1.5d);
        SampleBO s = new SampleBO();
        s.setTotalWeight(100.0);
        s.setSampledWeight(50.0);
        f.addSample("havsil", s);
        s.setSampleNo(Integer.SIZE);
        IndividualBO i = new IndividualBO();
        i.setLength(10.0);
        s.getIndividualBOCollection().add(i);
        i = new IndividualBO();
        i.setLength(11.0);
        s.getIndividualBOCollection().add(i);

        f = new FishstationBO();
        fs.add(f);
        f.setYear(2013);
        f.setSerialNo(1001);

        s = new SampleBO();
        s.setTotalWeight(100.0);
        s.setSampledWeight(50.0);
        f.addSample("havsil", s);
        s.setSampleNo(Integer.SIZE);
        i = new IndividualBO();
        i.setLength(10.0);
        s.getIndividualBOCollection().add(i);
        i = new IndividualBO();
        i.setLength(11.0);
        s.getIndividualBOCollection().add(i);

        return fs;
    }
}