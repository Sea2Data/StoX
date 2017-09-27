package no.imr.stox.functions.density;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import no.imr.stox.functions.AbstractFunction;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.stox.bo.DensityMatrix;
import no.imr.stox.bo.LengthDistMatrix;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.bo.NASCMatrix;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.EchosounderUtils;
import no.imr.stox.functions.utils.AbndEstParamUtil;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.StoXMath;
import no.imr.stox.log.ILogger;
import no.imr.stox.model.IModel;

/**
 * This function calculates densities by length.
 *
 * @author aasmunds
 */
public class AcousticDensity extends AbstractFunction {

    /**
     * @param input contains a, b, c flags, group interval, layertype,
     * ACOUSTICDATA, PROCESSDATA, matrices STATIONLENGTHDISTand NASC, logger
     * @return Matrix object of type DENSITY_MATRIX - see
     * DataTypeDescription.txt
     */
    @Override
    public Object perform(Map<String, Object> input) {
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        IModel model = (IModel) input.get(Functions.PM_MODEL);
        // a, b, c constants used in TS formula
        Double m = (Double) input.get(Functions.PM_ACOUSTICDENSITY_M);
        Double a = (Double) input.get(Functions.PM_ACOUSTICDENSITY_A);
        Double d = (Double) input.get(Functions.PM_ACOUSTICDENSITY_D);
        // Acoustic data used in channel to depth calculation (formula with upper int.dep. and pel.thickness)
        List<DistanceBO> distances = (List<DistanceBO>) input.get(Functions.PM_ACOUSTICDENSITY_ACOUSTICDATA);
        // totLengthDist = Matrix[GROUP~Species / ROW~Assignment / CELL~LengthGroup / VAR~WeightedCount]
        LengthDistMatrix totLengthDist = (LengthDistMatrix) input.get(Functions.PM_ACOUSTICDENSITY_LENGTHDIST);
        // NASCMatrix = Matrix[ROW~Distance / COL~Layer / VAR~NASC]
        NASCMatrix nascMatrix = (NASCMatrix) input.get(Functions.PM_ACOUSTICDENSITY_NASC);
        if (totLengthDist == null || nascMatrix == null) {
            return null;
        }
        String lenDistType = (String) totLengthDist.getResolutionMatrix().getRowValue(Functions.RES_LENGTHDISTTYPE);
        if (lenDistType == null || !lenDistType.equals(Functions.LENGTHDISTTYPE_PERCENTLENGHTDIST)) {
            logger.error("Length distribution type must be PercentLengthDist", null);
        }
        MatrixBO nascValues = nascMatrix.getData();
        // Check nasc acocat dimension
        List<String> keys = nascValues.getKeys();
        String acoCat = keys.size() == 1 ? keys.get(0) : null;
        if (acoCat == null) {
            logger.error("NASC should contain one and only one acoustic category.", null);
        }
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_ACOUSTICDENSITY_PROCESSDATA);
        String sampleUnitType = (String) nascMatrix.getResolutionMatrix().getRowValue(Functions.RES_SAMPLEUNITTYPE);//input.get(Functions.PM_DENSITY_SAMPLEUNITTYPE);
        String layerType = (String) nascMatrix.getResolutionMatrix().getRowValue(Functions.RES_LAYERTYPE);
        // Set how to translate the SampleUnit in the result, i.e labels in csv outpur
        if (sampleUnitType == null || layerType == null) {
            return null;
        }
        if (d != null && d != 0d && !layerType.equals(Functions.LAYERTYPE_PCHANNEL)) {
            logger.log("Depth dependent target strength cannot be calculated for the layer type " + layerType
                    + ". Constant d=" + d + " will be ignored.");
        }
        if (!AbndEstProcessDataUtil.isResolutionCompatibleWithSUAssignment(pd, nascMatrix.getResolutionMatrix())) {
            logger.error("NASC resolution is not compatible with the process data resolution.", null);
        }
        MatrixBO sampleUnitAssignment = AbndEstProcessDataUtil.getSUAssignments(pd);
        // Check input sources before calculation
        if (distances == null || sampleUnitAssignment == null) {
            return null;
        }
        // The following iterates dimensions GROUP, COL, ROW and CELL according to the function diagram from Atle.
        DensityMatrix result = new DensityMatrix();
        // Define resolution:
        result.getResolutionMatrix().setRowValue(Functions.RES_LAYERTYPE, layerType);
        Boolean asPChannel = layerType.equals(Functions.LAYERTYPE_PCHANNEL);
        result.getResolutionMatrix().setRowValue(Functions.RES_SAMPLEUNITTYPE, sampleUnitType);
        Double lengthInterval = totLengthDist.getResolutionMatrix().getRowValueAsDouble(Functions.RES_LENGTHINTERVAL);
        result.getResolutionMatrix().setRowValue(Functions.RES_LENGTHINTERVAL, lengthInterval);
        // Define samplesize and distance
        result.setSampleSizeMatrix(nascMatrix.getSampleSizeMatrix().copy());
        result.setPosSampleSizeMatrix(nascMatrix.getPosSampleSizeMatrix().copy());
        result.setDistanceMatrix(nascMatrix.getDistanceMatrix().copy());

        result.setEstLayerDefMatrix(pd.getMatrices().get(Functions.TABLE_ESTLAYERDEF));
        // For each sample unit in NASC.
        String error = "";
        for (String sampleUnit : nascValues.getGroupRowKeys(acoCat)) {
            // When using pchannels on each distance, depth correction can be performed; Get depth from distance and channel
            DistanceBO depthRepDist = null;
            if (asPChannel && d != null && d != 0d) {
                depthRepDist = getDepthRepresentativeDistance(pd, distances, sampleUnitType, sampleUnit);
                if (depthRepDist == null) {
                    error += "\n" + "Depth correction on TS is not possible for " + getSampleUnitPath(pd, sampleUnit, sampleUnitType)
                            + ". Reason: Channel thickness must be equal inside sample unit "
                            + ". Solution: Choose a smaller sampleunit type or set d constant to blank";
                }
            }
            // For each layer in NASC matrix
            for (String layer : nascValues.getGroupRowColKeys(acoCat, sampleUnit)) {
                // Lookup assignment from sampleUnit and layer
                String estLayer = AbndEstParamUtil.getEstLayerFromLayer(result.getEstLayerDefMatrix(), layer);
                if (estLayer == null) {
                    continue;
                }
                String asgID = AbndEstProcessDataUtil.getSUAssignmentIDBySampleUnitAndEstimationLayer(pd, sampleUnit, estLayer, sampleUnitType);

                if (asgID == null) {
                    if (sampleUnitType.equals(Functions.SAMPLEUNIT_PSU)) {
                        error += "\n" + getSampleUnitPath(pd, sampleUnit, sampleUnitType) + " have no assignments";
                    }
                    continue;
                }
                /*                MatrixBO basgm = bsAsg.getRowValueAsMatrix(asgID);
                if (basgm == null) {
                    logger.error(sampleUnitType + ": " + sampleUnit + ", Assignment id: " + asgID + ", missing biotic station assignments", null);
                    continue;
                }*/
                // Lookup lenght distribution cell from species and assignment
                // Lookup NASC value 
                Double nasc = (Double) nascValues.getGroupRowColValue(acoCat, sampleUnit, layer);
                if (nasc == null) {
                    //nasc = 0d; // The translation of null->0 should be moved from meannasc to this function.
                    // No nasc to calculate density from
                    continue;
                }
                Double depth = depthRepDist != null ? EchosounderUtils.getDepth(depthRepDist, layer) : null;

                // For each species in total length distributions group dimension
                for (String specCat : totLengthDist.getData().getKeys()) {
                    MatrixBO lDist = totLengthDist.getData().getGroupRowDefaultValueAsMatrix(specCat, asgID);
                    if (lDist == null && nasc > 0d) {
                        if (model.getWarningLevel().equals(Functions.WARNINGLEVEL_STRICT)) {
                            error += "\n" + getSampleUnitPath(pd, sampleUnit, sampleUnitType) + ": missing length distribution for mean nasc value " + nasc;
                        }
                        continue;
                    }
                    MatrixBO densM = StoXMath.getDensityDistribution(nasc, lDist, lengthInterval, depth, m, a, d);
                    for (String lenGrp : densM.getRowKeys()) {
                        result.getData().setGroupRowColCellValue(specCat, sampleUnit, layer, lenGrp, densM.getRowValue(lenGrp));
                    }
                }
            }
        }
        if (!error.isEmpty()) {
            logger.error(error, null);
        }
        return result;
    }

    String getSampleUnitPath(ProcessDataBO pd, String sampleUnit, String sampleUnitType) {
        String s1 = sampleUnitType + " '" + sampleUnit + "'";
        String s2 = "";
        switch (sampleUnitType) {
            case Functions.SAMPLEUNIT_EDSU: {
                String psu = AbndEstProcessDataUtil.getPSUBySampleUnit(pd, sampleUnit);
                String stratum = AbndEstProcessDataUtil.getPSUStratum(pd, psu);
                s2 = " in PSU '" + psu + "' in Stratum '" + stratum + "'";
                break;
            }
            case Functions.SAMPLEUNIT_PSU: {
                String stratum = AbndEstProcessDataUtil.getPSUStratum(pd, sampleUnit);
                s2 = " in Stratum '" + stratum + "'";
                break;
            }
        }
        return s1 + s2;
    }

    /*
     Calcuate the  representative sdistance from sample unit.
     If sampleUnit is PSU or Stratum, use one of the EDSUs if thickness and upperdepth is the same.
     */
    DistanceBO getDepthRepresentativeDistance(ProcessDataBO pd, List<DistanceBO> distances, String sampleUnitType, String sampleUnit) {
        //Set<Double> upperIntpDep = new HashSet<>();
        Set<Double> pelChThickness = new HashSet<>();
        DistanceBO result = null;
        List<String> EDSUs = new ArrayList<>();
        switch (sampleUnitType) {
            case Functions.SAMPLEUNIT_EDSU:
                EDSUs.add(sampleUnit);
                break;
            case Functions.SAMPLEUNIT_PSU:
                EDSUs.addAll(AbndEstProcessDataUtil.getEDSUsByPSU(pd, sampleUnit));
                break;
            case Functions.SAMPLEUNIT_STRATUM:
                EDSUs.addAll(AbndEstProcessDataUtil.getEDSUsByStratum(pd, sampleUnit));
        }
        for (DistanceBO d : distances) {
            if (EDSUs.contains(d.getKey())) {
                result = d;
                pelChThickness.add(d.getPel_ch_thickness());
                /*for (FrequencyBO f : d.getFrequencies()) {
                 upperIntpDep.add(f.getUpper_interpret_depth());
                 break;
                 }*/
                if (EDSUs.size() == 1) {
                    break;
                }
            }
        }
        if (/*upperIntpDep.size() != 1 || */pelChThickness.size() != 1) {
            // Sample unit have no uniquely defined upper interpret dep  / pelchthickness
            return null;
        }
        return result;
    }

}
