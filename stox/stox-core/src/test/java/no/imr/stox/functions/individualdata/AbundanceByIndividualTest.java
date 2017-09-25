package no.imr.stox.functions.individualdata;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiPolygon;
import no.imr.stox.functions.individualdata.SuperIndAbundance;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.biotic.bo.SampleBO;
import no.imr.sea2data.imrmap.utils.JTSUtils;
import no.imr.stox.bo.AbundanceIndividualsMatrix;
import no.imr.stox.bo.AbundanceMatrix;
import no.imr.stox.bo.IndividualDataMatrix;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
public class AbundanceByIndividualTest {

    @Test
    public void test() {
        Map<String, Object> input = getInput();
        AbundanceIndividualsMatrix result = (AbundanceIndividualsMatrix) (new SuperIndAbundance()).perform(input);
        MatrixBO resData = result.getData();
        assertEquals("havsil", resData.getRowColValue("1", Functions.COL_ABNDBYIND_SPECCAT));
        assertEquals("ST1", resData.getRowColValue("1", Functions.COL_ABNDBYIND_STRATUM));
        assertEquals("E1", resData.getRowColValue("1", Functions.COL_ABNDBYIND_ESTLAYER));
        assertEquals("10", resData.getRowColValue("1", Functions.COL_ABNDBYIND_LENGRP));
        assertEquals(500d, resData.getRowColValue("1", Functions.COL_ABNDBYIND_ABUNDANCE));
    }

    @Test
    public void testNoInput() {
        Map<String, Object> input = new HashMap<String, Object>();
        AbundanceIndividualsMatrix result = (AbundanceIndividualsMatrix) (new SuperIndAbundance()).perform(input);
        Assert.assertNull(result);
    }

    private Map<String, Object> getInput() {
        Map<String, Object> input = new HashMap<>();

        IndividualDataMatrix indData = new IndividualDataMatrix();
        // indData = DataType=Matrix[GROUP~Species / ROW~EstLayer / COL~Stratum / CELL~LengthGroup / VAR~Individuals]
        indData.getData().setGroupRowColCellValue("havsil", "ST1", "E1", "10", getIndividuals());
        input.put(Functions.PM_SUPERINDABUNDANCE_INDIVIDUALDATA, indData);

        AbundanceMatrix abundance = new AbundanceMatrix();
        abundance.getResolutionMatrix().setRowValue(Functions.RES_SAMPLEUNITTYPE, Functions.SAMPLEUNIT_STRATUM);
        // abundance = DataType=Matrix[GROUP~Species / ROW~EstLayer / COL~SampleUnit / CELL~LengthGroup / VAR~Abundance]
        abundance.getData().setGroupRowColCellValue("havsil", "ST1", "E1", "10", 500d);
        //abundance.getData().setGroupRowColCellValue("havsil", "ST2", "E1", "10", 600d);
        input.put(Functions.PM_SUPERINDABUNDANCE_ABUNDANCE, abundance);
        ProcessDataBO pd = new ProcessDataBO();
        AbndEstProcessDataUtil.setStratumPolygon(pd, "1", true, getStrataPlg());
        input.put(Functions.PM_SUPERINDABUNDANCE_PROCESSDATA, pd);
        return input;
    }

    MultiPolygon getStrataPlg() {
        return JTSUtils.createMultiPolygon(Arrays.asList(JTSUtils.createLineString(Arrays.asList(
                new Coordinate(0d, 0d), new Coordinate(2d, 0d), new Coordinate(2d, 2d), new Coordinate(0d, 2d)))));
    }

    List<IndividualBO> getIndividuals() {
        List<IndividualBO> inds = new ArrayList<>();
        FishstationBO f = new FishstationBO();
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
        i.setWeight(5.0);
        s.getIndividualBOCollection().add(i);
        inds.add(i);
        return inds;
    }

}
