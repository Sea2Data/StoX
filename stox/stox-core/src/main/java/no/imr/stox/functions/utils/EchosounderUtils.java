package no.imr.stox.functions.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.echosounderbo.FrequencyBO;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.bo.NASCMatrix;

/**
 * TODO: what does this class do? 
 *
 * @author Åsmund
 */
public final class EchosounderUtils {

    /**
     *
     * @param distances
     * @param e
     * @param key
     * @return distance by key
     */
    public static DistanceBO findDistance(List<DistanceBO> distances, String key) {
        if (key == null || key.isEmpty()) {
            System.out.println("Error: Searching for empty key");
        }
        for (DistanceBO d : distances) {
            if (d.getKey().equals(key)) {
                return d;
            }
        }
        return null;
    }

    public static Collection<DistanceBO> findDistances(List<DistanceBO> distances, Collection<String> keys) {
        if (keys == null) {
            return null;
        }
        List<DistanceBO> dist = new ArrayList<>();
        for (String key : keys) {
            DistanceBO d = findDistance(distances, key);
            if (d == null) {
                // Some distances may be defined in project.xml but undefined in raw data by replacing the input file.
                continue;
            }
            dist.add(d);
        }
        return dist;
    }

    /**
     *
     * @param distances
     * @return max pelagic channel
     */
    public static Integer findMaxPelagicChannel(List<DistanceBO> distances) {
        Integer res = 0;
        for (DistanceBO d : distances) {
            if (d == null) {
                continue;
            }
            for (FrequencyBO f : d.getFrequencies()) {
                if (f == null) {
                    continue;
                }
                res = Math.max(res, f.getNum_pel_ch());
            }
        }
        return res;
    }

    /**
     * Aggregate vertically
     *
     * @param values Matrix[ROW~Distance / COL~Layer / VAR~Value]
     * @param sourceSUs
     * @return sample units aggregated vertically.
     */
    public static NASCMatrix aggregateVerticallyMatrix(NASCMatrix values, String acoCat, Collection<String> sourceSUs) {
        NASCMatrix result = new NASCMatrix();
        result.setSampleSizeMatrix(values.getSampleSizeMatrix());
        result.setPosSampleSizeMatrix(values.getPosSampleSizeMatrix());
        result.setDistanceMatrix(values.getDistanceMatrix());
        // Aggregate first vertically
        for (String sourceSU : values.getData().getGroupRowKeys(acoCat)) {
            if (sourceSUs != null && !sourceSUs.contains(sourceSU)) {
                continue;
            }
            MatrixBO row = (MatrixBO) values.getData().getGroupRowValue(acoCat, sourceSU);
            for (String layer : row.getKeys()) {
                // Aggregate all channel layers into a total PB layer:
                result.getData().addGroupRowColValue(acoCat, sourceSU, Functions.WATERCOLUMN_PELBOT, values.getData().getGroupRowColValueAsDouble(acoCat, sourceSU, layer));
            }
        }
        return result;
    }

    public static Double aggregateHorizontallyDistanceMatrix(Collection<String> sourceSUs, NASCMatrix values, String acoCat, List<DistanceBO> distances, MatrixBO saProportion) {
        return aggregateHorizontallyMatrix(sourceSUs, values, acoCat, saProportion);
    }

    /**
     * Aggregate a value matrix horizontally
     *
     * @param sourceSUs - the sample units used in the aggregation
     * @param values - the 2d matrix values (could be density or NASC...)
     * @param distances - echo sounder dataset
     * @param pd - process data. set to null if sourceIsDistance=true
     * @param sourceIsDistance true if aggregate from distance, false if
     * aggregate from psu
     * @param saProportion - Matrix[ROW~SampleUnit / VAR~Proportion] the
     * proportions of NASC (used when calculating station assignment weights by
     * overlapping circles.)
     * @return the watercolumn value.
     */
    public static Double aggregateHorizontallyMatrix(Collection<String> sourceSUs, NASCMatrix values, String acoCat, MatrixBO saProportion) {
        NASCMatrix aggVertical = aggregateVerticallyMatrix(values, acoCat, sourceSUs);
        // Matrix[ROW~SampleUnit / VAR~Weight]
        MatrixBO sourceWeights = getWeights(sourceSUs, aggVertical.getDistanceMatrix(), false);
        return aggregateHorizontallyVector(sourceSUs, aggVertical.getData(), acoCat, sourceWeights, saProportion, Functions.WATERCOLUMN_PELBOT);
    }

    /**
     * Aggregate horizontally
     *
     * @param sourceSUs
     * @param values Matrix[ROW~SampleUnit / VAR~Value]
     * @param distances
     * @param pd
     *
     * @param sourceWeights
     * @param layer
     * @param saProportion Matrix[ROW~SampleUnit / VAR~Proportion]
     * @return
     */
    public static Double aggregateHorizontallyVector(Collection<String> sourceSUs, MatrixBO values, String acoCat,
            MatrixBO sourceWeights, MatrixBO saProportion, String layer) {
        // For each target sample unit
        // For each source sample unit
        Double sumWeights = sourceWeights.getSum();
        Double result = 0d;
        for (String sourceSU : sourceSUs) {
            Double v = values.getGroupRowColValueAsDouble(acoCat, sourceSU, layer);
            if (v == null || v == 0d) {
                continue;
            }
            Double sourceWeight = (Double) sourceWeights.getRowValue(sourceSU);
            Double relWeight = StoXMath.relativeToTotal(sourceWeight, sumWeights);
            if (relWeight == null) {
                continue;
            }
            // Default proportion is to keep the value as is
            Double saProp = 1.0;
            if (saProportion != null) {
                Double sap = saProportion.getRowValueAsDouble(sourceSU);
                if (sap != null) {
                    saProp = sap;
                }
            }
            relWeight = StoXMath.proportion(saProp, relWeight);
            Double weightedValue = StoXMath.proportion(v, relWeight);
            result += weightedValue;
        }
        return result;
    }

    public static Collection<String> getSourceSUs(ProcessDataBO pd, String targetSU, Boolean sourceIsEDSU) {
        // distancePSUs=Matrix[ROW~Distance / VAR~PSU]
        // PSUStrata=Matrix[ROW~PSU / VAR~Strata]
        if (sourceIsEDSU) {
            return AbndEstProcessDataUtil.getEDSUsByPSU(pd, targetSU);
        } else {
            return AbndEstProcessDataUtil.getPSUsByStratum(pd, targetSU);
        }
    }

    /**
     * Can be used with mean NASC function where NASCMatrix have its own
     * distance matrix. Note: To be used with Mean density as well?
     *
     * @param sourceSUs
     * @param dist
     * @param useEqualWeight
     * @return
     */
    public static MatrixBO getWeights(Collection<String> sourceSUs, MatrixBO dist,
            Boolean useEqualWeight) {
        MatrixBO result = new MatrixBO("Matrix[ROW~SampleUnit / VAR~Weight]");
        //System.out.println(dist.asTable());
        for (String su : sourceSUs) {
            Double d = dist.getRowValueAsDouble(su);
            result.addRowValue(su, d);
        }
        return result;
    }

    /**
     * Aggregate distance integrator lengths on source psu keys. Can be used
     * from a mean density function
     *
     * @param sourceSUs
     * @param distances
     * @param pd
     * @param sourceIsDistance true if aggregate distance, false if aggregate
     * PSU
     * @param useEqualWeight
     * @return
     */
    public static MatrixBO getWeights(Collection<String> sourceSUs, List<DistanceBO> distances, ProcessDataBO pd, Boolean sourceIsDistance,
            Boolean useEqualWeight) {
        MatrixBO result = new MatrixBO("Matrix[ROW~SampleUnit / VAR~Weight]");
        for (DistanceBO d : distances) {
            String su = sourceIsDistance ? d.getKey() : (String) AbndEstProcessDataUtil.getEDSUPSUs(pd).getRowValue(d.getKey());
            if (!sourceSUs.contains(su)) {
                continue;
            }
            Double weight = useEqualWeight ? 1 : d.getIntegrator_dist();
            result.addRowValue(su, weight);
        }
        return result;
    }

    public static Double getDepth(DistanceBO distance, String channelKey) {
        Integer channel = Conversion.safeStringtoIntegerNULL(channelKey);
        Double thickness = distance.getPel_ch_thickness();
        return StoXMath.depthFromChannel(thickness, channel);
    }

}
