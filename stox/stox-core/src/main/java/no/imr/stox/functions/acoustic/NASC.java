package no.imr.stox.functions.acoustic;

import java.util.List;
import java.util.Map;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.AbstractFunction;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.echosounderbo.FrequencyBO; 
import no.imr.sea2data.echosounderbo.SABO;
import no.imr.sea2data.imrbase.math.ImrMath;
import no.imr.stox.bo.NASCMatrix;

/**
 * TODO: what does this class do?
 *
 * @author aasmunds, reworked to use matrix by aasmunds, reworked to use matrix
 * by aasmunds
 */
public class NASC extends AbstractFunction {

    @Override
    public Object perform(Map<String, Object> input) {
        if (input == null) {
            return null;
        }
        List<DistanceBO> distances = (List<DistanceBO>) input.get(Functions.PM_NASC_ACOUSTICDATA);
        String layerType = (String) input.get(Functions.PM_NASC_LAYERTYPE);
        Boolean asDepthLayer = layerType.equalsIgnoreCase(Functions.LAYERTYPE_DEPTHLAYER); // Support 3 types: PCHANNEL, LAYER, WATERCOLUMN
        Boolean asWaterColumn = layerType.equalsIgnoreCase(Functions.LAYERTYPE_WATERCOLUMN); // Support 3 types: PCHANNEL, LAYER, WATERCOLUMN
        MatrixBO raw = new MatrixBO("Matrix[GROUP~AcoCatDistance / ROW~ChType / COL~Channel / VAR~NASC]");
        // Raw data includes ChannelType P (P+B), and B, P (over bottom) is missing in the raw data.
        NASCMatrix result = new NASCMatrix();
        // Generate the raw layers in a intermediate grouped matrix
        for (DistanceBO d : distances) {
            String distance = d.getKey();
            Integer sampleSize = 1;
            result.getSampleSizeMatrix().addRowValue(distance, sampleSize);
            result.getDistanceMatrix().addRowValue(distance, d.getIntegrator_dist());
            boolean sampleHasValue = false;
            for (FrequencyBO f : d.getFrequencies()) {
                for (SABO s : f.getSa()) {
                    if (s == null || s.getCh() == null || s.getCh_type() == null) {
                        continue;
                    }
                    String channel = s.getCh().toString();
                    Double nasc = s.getSa();

                    // From the principle figure a channel/edsu matrix with NASC
                    String acoCat = s.getAcoustic_category();
                    sampleHasValue = sampleHasValue || nasc != null && nasc > 0d;
                    raw.setGroupRowColValue(acoCat + "/" + distance, s.getCh_type(), channel, nasc);
                }
            }
            result.getPosSampleSizeMatrix().addRowValue(distance, sampleHasValue ? sampleSize : 0);
            
        }

        result.getResolutionMatrix().setRowValue(Functions.RES_SAMPLEUNITTYPE, Functions.SAMPLEUNIT_EDSU);
        result.getResolutionMatrix().setRowValue(Functions.RES_LAYERTYPE, layerType);
        // Create acoustic matrix with P or B
        for (String acoCatDistance : raw.getKeys()) {
            String[] s = acoCatDistance.split("/", 2);
            String acoCat = s[0];
            String distance = s[1];
            // Sum up PB (Pelagic with bottom included)
            MatrixBO row = (MatrixBO) raw.getGroupRowValue(acoCatDistance, "P");
            if (row == null) {
                continue;
            }
            if (asDepthLayer || asWaterColumn) {
                Double pbSum = row.getSum(); // Sum of channels
                if (asWaterColumn) {
                    result.getData().setGroupRowColValue(acoCat, distance, Functions.WATERCOLUMN_PELBOT, pbSum);
                } else {
                    // Sum up B (Bottom)
                    Double bSum = null;
                    MatrixBO b = (MatrixBO) raw.getGroupRowValue(acoCatDistance, "B");
                    if (b != null) {
                        bSum = b.getSum();
                    }
                    // Calculate PEL = PB - B (Depth layer Pelagic over Bottom)
                    Double pSum = ImrMath.safeMinus(pbSum, bSum != null ? bSum : 0d);
                    // Set depth layers PEL and BOT into result
                    if (pSum != null && pSum >= 0d) {
                        result.getData().setGroupRowColValue(acoCat, distance, Functions.DEPTHLAYER_PEL, pSum);
                    }
                    if (bSum != null) {
                        result.getData().setGroupRowColValue(acoCat, distance, Functions.DEPTHLAYER_BOT, bSum);
                    }
                }
            } else {
                for (String channel : row.getKeys()) {
                    Double d = (Double) row.getValue(channel);
                    if (d != null) {
                        result.getData().setGroupRowColValue(acoCat, distance, channel, d);
                    }
                }
            }
        }
        return result;
    }
}
