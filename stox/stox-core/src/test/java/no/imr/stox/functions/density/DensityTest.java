package no.imr.stox.functions.density;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.echosounderbo.FrequencyBO;
import no.imr.sea2data.imrbase.util.IMRdate;
import no.imr.stox.bo.DensityMatrix;
import no.imr.stox.bo.LengthDistMatrix;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.bo.NASCMatrix;
import no.imr.stox.functions.utils.AbndEstParamUtil;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
public class DensityTest {

    @Test
    public void test() {
//        assertEquals(7521.92, perform(20d, -70d, 4d, 1.5d, 2d, 5d, 8d, 23, 30), 0.05);
//        assertEquals(1 / (4 * Math.PI), perform(1d, 0d, 0d, 1d, 0d, 0d, 0d, 1, 0), 0);
//        assertEquals(1 / (4 * Math.PI), perform(0d, 0d, 0d, 1d, 0d, 0d, 0d, 1, 0), 0);
//        assertEquals(0d, perform(0d, 0d, 0d, 0d, 0d, 0d, 0d, 1, 0), 0);
//        assertEquals((Double)null, perform(0d, 0d, 0d, 0d, 0d, 0d, 0d, 0, 0), 0);
//        assertEquals(1 / (4 * Math.PI), perform(20d, -68d, 20d - 68d, 1.0d, 4d, 5d, 10d, 8, 9), 0);
//        assertEquals(100 / (4 * Math.PI), perform(null, -68d, 20d - 68d, 1.0d, 4d, 5d, 10d, 8, 9), 0);
    }

    private Double perform(Double m, Double a, Double d, Double nasc, Double intv, Integer len, Integer ch) {
        DensityMatrix result = (DensityMatrix) (new AcousticDensity()).perform(getInput(m, a, d, nasc, intv, len, ch));
        return result.getData().getGroupRowColCellValueAsDouble("havsil", "2013101/100/2013-01-01/12:00:00", ch.toString(), len.toString());
    }

    private Map<String, Object> getInput(Double m, Double a, Double d, Double sa, Double intv, Integer len, Integer ch) {
        Map<String, Object> input = new HashMap<>();
        input.put(Functions.PM_ACOUSTICDENSITY_M, m);
        input.put(Functions.PM_ACOUSTICDENSITY_A, a);
        input.put(Functions.PM_ACOUSTICDENSITY_D, d);
        LengthDistMatrix lengthDist = new LengthDistMatrix();
        lengthDist.getData().setGroupRowCellValue("havsil", "1", len.toString(), 100d);
        lengthDist.getResolutionMatrix().setRowValue(Functions.RES_LENGTHINTERVAL, intv);
        lengthDist.getResolutionMatrix().setRowValue(Functions.RES_LENGTHDISTTYPE, Functions.LENGTHDISTTYPE_PERCENTLENGHTDIST);
        input.put(Functions.PM_ACOUSTICDENSITY_LENGTHDIST, lengthDist);
        NASCMatrix saMatrix = new NASCMatrix();
        saMatrix.getData().setRowColValue("2013101/100/2013-01-01/12:00:00", ch.toString(), sa);
        saMatrix.getDistanceMatrix().setRowValue("2013101/100/2013-01-01/12:00:00", 1.0);
        saMatrix.getSampleSizeMatrix().setRowValue("2013101/100/2013-01-01/12:00:00", 1);
        saMatrix.getResolutionMatrix().setRowValue(Functions.RES_SAMPLEUNITTYPE, Functions.SAMPLEUNIT_EDSU);
        saMatrix.getResolutionMatrix().setRowValue(Functions.RES_LAYERTYPE, Functions.LAYERTYPE_PCHANNEL);
        input.put(Functions.PM_ACOUSTICDENSITY_NASC, saMatrix);
        ProcessDataBO pd = new ProcessDataBO();
        MatrixBO suAsg = (MatrixBO) AbndEstProcessDataUtil.getSUAssignments(pd);
        String estlayer = "1";
        suAsg.setRowColValue("2013101/100/2013-01-01/12:00:00", estlayer, "1");
        AbndEstProcessDataUtil.setAssignmentResolution(pd, Functions.SAMPLEUNIT_EDSU);
        input.put(Functions.PM_ACOUSTICDENSITY_PROCESSDATA, pd);
        MatrixBO estLayerM = AbndEstParamUtil.getEstLayerMatrixFromEstLayerDef(estlayer + "~" + Functions.WATERCOLUMN_PELBOT);
        pd.getMatrices().put(Functions.TABLE_ESTLAYERDEF, estLayerM);
        
        return input;
    }

    List<DistanceBO> getDistances(Double upper, Double pelThick) {
        List<DistanceBO> distances = new ArrayList<>();
        DistanceBO d = new DistanceBO();
        d.setCruise("2013101");
        d.setLog_start(100d);
        d.setStart_time(IMRdate.encodeDate(2013, 1, 1, 12, 0, 0, 0, true));
        distances.add(d);
        FrequencyBO f = new FrequencyBO();
        d.getFrequencies().add(f);
        d.setPel_ch_thickness(pelThick);
        f.setUpper_interpret_depth(upper);
        return distances;
    }
}
