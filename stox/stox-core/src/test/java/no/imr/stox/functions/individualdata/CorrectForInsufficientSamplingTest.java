/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.individualdata;

import no.imr.stox.functions.individualdata.CorrectForInnsufficientSampling;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.biotic.bo.SampleBO;
import no.imr.stox.bo.AbundanceMatrix;
import no.imr.stox.bo.IndividualDataMatrix;
import no.imr.stox.functions.utils.Functions;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
public class CorrectForInsufficientSamplingTest {

    @Test
    public void test() {
        Map<String, Object> input = getInput();
        IndividualDataMatrix result = (IndividualDataMatrix) (new CorrectForInnsufficientSampling()).perform(input);
        assertEquals(((List) result.getData().getGroupRowColCellValue("havsil", "ST2", "E1", "10")).size(), 1);
    }

    private Map<String, Object> getInput() {
        Map<String, Object> input = new HashMap<>();
        IndividualDataMatrix indData = new IndividualDataMatrix();
        // indData = DataType=Matrix[GROUP~Species / ROW~EstLayer / COL~Stratum / CELL~LengthGroup / VAR~Individuals]
        indData.getData().setGroupRowColCellValue("havsil", "ST1", "E1", "10", getIndividuals());
        input.put(Functions.PM_CORRECTFORINNSUFFICIENTSAMPLING_INDIVIDUALDATA, indData);

        AbundanceMatrix abundance = new AbundanceMatrix();
        // abundance = DataType=Matrix[GROUP~Species / ROW~EstLayer / COL~SampleUnit / CELL~LengthGroup / VAR~Abundance]
        abundance.getData().setGroupRowColCellValue("havsil", "ST1", "E1", "10", 500d);
        abundance.getData().setGroupRowColCellValue("havsil", "ST2", "E1", "10", 600d);
        input.put(Functions.PM_CORRECTFORINNSUFFICIENTSAMPLING_ABUNDANCE, abundance);
        abundance.getResolutionMatrix().setRowValue(Functions.RES_LENGTHINTERVAL, 1.0);
        return input;
    }

    List<IndividualBO> getIndividuals() {
        List<IndividualBO> inds = new ArrayList<>();
        FishstationBO f = new FishstationBO();
        f.setDistance(1.5d);
        f.setSerialnumber(1000);
        SampleBO s = f.addSample("havsil");
        s.setCatchweight(100.0);
        s.setLengthsampleweight(50.0);
        s.setCatchpartnumber(Integer.SIZE);
        IndividualBO i = s.addIndividual();
        i.setSpecimenid(1);
        i.setLength(10.0);
        i.setIndividualweight(0.005);
        inds.add(i);
        return inds;
    }
}
