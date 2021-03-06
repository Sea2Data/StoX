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
import no.imr.stox.util.matrix.MatrixBO;
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
        /*List<DistanceBO> distances = (List<DistanceBO>) input.get(Functions.PM_ACOUSTICDENSITY_ACOUSTICDATA);
        if (d != null && distances == null) {
            logger.error("AcousticDensity is blank when d is set", null);
            return null;
        }*/
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
        String layerTypePd = (String) AbndEstProcessDataUtil.getAssignmentResolutions(pd).getRowValue(Functions.RES_LAYERTYPE);
        String sampleUnitType = (String) nascMatrix.getResolutionMatrix().getRowValue(Functions.RES_SAMPLEUNITTYPE);//input.get(Functions.PM_DENSITY_SAMPLEUNITTYPE);
        String layerTypeNASC = (String) nascMatrix.getResolutionMatrix().getRowValue(Functions.RES_LAYERTYPE);
        // Set how to translate the SampleUnit in the result, i.e labels in csv outpur
        if (sampleUnitType == null) {
            logger.error("Missing NASC sampleunit", null);
            return null;
        }
        if (layerTypeNASC == null) {
            logger.error("Missing NASC layer type", null);
            return null;
        }
        if (layerTypePd == null) {
            logger.error("Missing Assignment layer type", null);
            return null;
        }
        if (!layerTypeNASC.equals(layerTypePd)) {
            logger.error("The layer type " + layerTypeNASC + " given in the NASC is different from the layer type " + layerTypePd
                    + " derived from the estimation layer definition BioStationAssignment EstLayers. ", null);
            return null;
        }
        if (d != null && d != 0d && !layerTypeNASC.equals(Functions.LAYERTYPE_PCHANNEL)) {
            logger.log("Depth dependent target strength cannot be calculated for the layer type " + layerTypeNASC
                    + ". Constant d=" + d + " will be ignored.");
        }
        if (!AbndEstProcessDataUtil.isResolutionCompatibleWithSUAssignment(pd, nascMatrix.getResolutionMatrix())) {
            logger.error("NASC resolution is not compatible with the process data resolution.", null);
            return null;
        }
        MatrixBO sampleUnitAssignment = AbndEstProcessDataUtil.getSUAssignments(pd);
        // Check input sources before calculation
        if (/*distances == null || */sampleUnitAssignment == null) {
            return null;
        }
        // The following iterates dimensions GROUP, COL, ROW and CELL according to the function diagram from Atle.
        DensityMatrix result = new DensityMatrix();
        // Define resolution:
        result.getResolutionMatrix().setRowValue(Functions.RES_LAYERTYPE, layerTypeNASC);
        Boolean asPChannel = layerTypeNASC.equals(Functions.LAYERTYPE_PCHANNEL);
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
            //DistanceBO depthRepDist = null;
            Double channelThickness = nascMatrix.getChannelThicknessMatrix().getRowValueAsDouble(sampleUnit);
            if (asPChannel && (channelThickness == null || channelThickness <= 0d)) {
                error += "\n" + "Depth correction on TS is not possible for " + AbndEstProcessDataUtil.getSampleUnitPath(pd, sampleUnit, sampleUnitType)
                        + ". Reason: Channel thickness must be equal inside sample unit "
                        + ". Solution: Choose a smaller sampleunit type or set d constant to blank";
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
                        error += "\n" + AbndEstProcessDataUtil.getSampleUnitPath(pd, sampleUnit, sampleUnitType) + " have no assignments for layer " + layer;
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
                Double depth = EchosounderUtils.getDepth(channelThickness, layer);

                // For each species in total length distributions group dimension
                for (String specCat : totLengthDist.getData().getKeys()) {
                    MatrixBO lDist = totLengthDist.getData().getGroupRowDefaultValueAsMatrix(specCat, asgID);
                    if (lDist == null && nasc > 0d) {
                        if (model.getWarningLevel().equals(Functions.WARNINGLEVEL_STRICT)) {
                            error += "\n" + AbndEstProcessDataUtil.getSampleUnitPath(pd, sampleUnit, sampleUnitType) + ": missing length distribution for mean nasc value " + nasc;
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

    /*
     Calcuate the  representative sdistance from sample unit.
     If sampleUnit is PSU or Stratum, use one of the EDSUs if thickness and upperdepth is the same.
     */
    DistanceBO getDepthRepresentativeDistance(ProcessDataBO pd, List<DistanceBO> distances, String sampleUnitType, String sampleUnit) {
        //Set<Double> upperIntpDep = new HashSet<>();
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
                return d;
            }
        }
        return null;
    }

}
