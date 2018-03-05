/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.processdata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.stox.bo.LengthDistMatrix;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.bo.NASCMatrix;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.bo.SpeciesTSMix;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.AbndEstParamUtil;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.EchosounderUtils;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.StoXMath;
import no.imr.stox.log.ILogger;

/**
 *
 * @author aasmunds
 */
public class SplitNASC extends AbstractFunction {

    /**
     * @param input contains a, b, c flags, group interval, layertype,
     * ACOUSTICDATA, PROCESSDATA, matrices STATIONLENGTHDISTand NASC, logger
     * @return Matrix object of type DENSITY_MATRIX - see
     * DataTypeDescription.txt
     */
    @Override
    public Object perform(Map<String, Object> input) {
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        // a, b, c constants used in TS formula
        //String mixAco = (String) input.get(Functions.PM_SPLITNASC_MIXACOCAT);
        String speciesTS = (String) input.get(Functions.PM_SPLITNASC_SPECIESTS);
        // Acoustic data used in channel to depth calculation (formula with upper int.dep. and pel.thickness)
        List<DistanceBO> distances = (List<DistanceBO>) input.get(Functions.PM_SPLITNASC_ACOUSTICDATA);
        // totLengthDist = Matrix[GROUP~Species / ROW~Assignment / CELL~LengthGroup / VAR~WeightedCount]
        LengthDistMatrix totLengthDist = (LengthDistMatrix) input.get(Functions.PM_SPLITNASC_LENGTHDIST);
        // NASCMatrix = Matrix[ROW~Distance / COL~Layer / VAR~NASC]
        NASCMatrix nascMatrix = (NASCMatrix) input.get(Functions.PM_SPLITNASC_NASC);
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_SPLITNASC_PROCESSDATA);
        if (totLengthDist == null || nascMatrix == null) {
            return null;
        }
        Map<String, MatrixBO> acospecTS = getSpecTS(speciesTS, totLengthDist.getData().getKeys());
        // Accorinding to STOX-80, SplitNasc must work with all lfq types.
        /*String lenDistType = (String) totLengthDist.getResolutionMatrix().getRowValue(Functions.RES_LENGTHDISTTYPE);
        if (lenDistType == null || !lenDistType.equals(Functions.LENGTHDISTTYPE_NORMLENGHTDIST)) {
            logger.error("Length distribution type must be NormLengthDist", null);
        }*/

        MatrixBO nascValues = nascMatrix.getData();
        // Check nasc acocat dimension
        String sampleUnitType = (String) nascMatrix.getResolutionMatrix().getRowValue(Functions.RES_SAMPLEUNITTYPE);//input.get(Functions.PM_DENSITY_SAMPLEUNITTYPE);
        String layerType = (String) nascMatrix.getResolutionMatrix().getRowValue(Functions.RES_LAYERTYPE);
        // Set how to translate the SampleUnit in the result, i.e labels in csv outpur
        if (sampleUnitType == null || layerType == null) {
            return null;
        }
        if (!sampleUnitType.equals(Functions.SAMPLEUNIT_EDSU) || !layerType.equals(Functions.LAYERTYPE_PCHANNEL)) {
            logger.log("NASC matrix resolution must be EDSU and PChannel");
        }
        if (!AbndEstProcessDataUtil.isResolutionCompatibleWithSUAssignment(pd, nascMatrix.getResolutionMatrix())) {
            logger.error("NASC resolution is not compatible with the process data resolution.", null);
        }
        // Check input sources before calculation
        if (distances == null) {
            return null;
        }
        // Create a copy NASC matrix
        NASCMatrix result = new NASCMatrix();
        result.setData(nascMatrix.getData().copy());
        /*
        // Remove the acocats not in split category:
        for (String acoCat : result.getData().getKeys()) {
            if (!acoCat.equals(mixAcoCat)) {
                result.getData().removeValue(acoCat);
            }
        }*/

        // Define resolution:
        result.getResolutionMatrix().setRowValue(Functions.RES_LAYERTYPE, layerType);
        result.getResolutionMatrix().setRowValue(Functions.RES_SAMPLEUNITTYPE, sampleUnitType);
        Double lengthInterval = totLengthDist.getResolutionMatrix().getRowValueAsDouble(Functions.RES_LENGTHINTERVAL);
        result.getResolutionMatrix().setRowValue(Functions.RES_LENGTHINTERVAL, lengthInterval);
        // Define samplesize and distance
        result.setSampleSizeMatrix(nascMatrix.getSampleSizeMatrix().copy());
        result.setPosSampleSizeMatrix(nascMatrix.getPosSampleSizeMatrix().copy());
        result.setDistanceMatrix(nascMatrix.getDistanceMatrix().copy());
        MatrixBO estLayerMatrix = pd.getMatrices().get(Functions.TABLE_ESTLAYERDEF);
        // For each sample unit in NASC.
        for (String mixAcoCat : acospecTS.keySet()) {
            MatrixBO specTS = acospecTS.get(mixAcoCat);
            for (String edsu : nascValues.getGroupRowKeys(mixAcoCat)) {
                // When using pchannels on each distance, depth correction can be performed; Get depth from distance and channel
                Double channelThickness = nascMatrix.getChannelThicknessMatrix().getRowValueAsDouble(edsu);
                // For each layer in NASC matrix
                for (String channel : nascValues.getGroupRowColKeys(mixAcoCat, edsu)) {
                    // Lookup assignment from sampleUnit and layer
                    String estLayer = AbndEstParamUtil.getEstLayerFromLayer(estLayerMatrix, channel);
                    String asgID = AbndEstProcessDataUtil.getSUAssignmentIDBySampleUnitAndEstimationLayer(pd, edsu, estLayer, sampleUnitType);
                    /*String assignment = (String) sampleUnitAssignment.getRowColValue(sampleUnit, layer);
                 if (assignment == null && (layerType.equals(Functions.LAYERTYPE_PCHANNEL) || layerType.equals(Functions.LAYERTYPE_DEPTHLAYER))) {
                 // Assignments was not found for spesific layer, try PELBOT:
                 assignment = (String) sampleUnitAssignment.getRowColValue(sampleUnit, Functions.WATERCOLUMN_PELBOT);
                 }*/

                    if (asgID == null) {
                        continue;
                    }
                    Double depth = EchosounderUtils.getDepth(channelThickness, channel);
                    Double nasc = (Double) nascValues.getGroupRowColValue(mixAcoCat, edsu, channel);
                    if (nasc == null || nasc == 0d) {
                        continue;
                    }
                    // Build lDist parameter
                    MatrixBO lDist = new MatrixBO();
                    for (String acoCat : specTS.getRowKeys()) {
                        String specCat = (String) specTS.getRowColValue(acoCat, "SpecCat");
                        MatrixBO lD = totLengthDist.getData().getGroupRowDefaultValueAsMatrix(specCat, asgID);
                        if (lD == null) {
                            continue;
                        }
                        for (String lenGrp : lD.getKeys()) {
                            lDist.setRowColValue(acoCat, lenGrp, lD.getValueAsDouble(lenGrp));
                        }
                    }
                    if (lDist.getKeys().isEmpty()) {
                        logger.error("Length distribution not found for edsu " + edsu + ". Increase the radius/ellipsoidal distance to assign at least one station which includes at least one of the splitted species categories.", null);
                    }
                    // Calculate NASC proportions
                    MatrixBO nascProp = StoXMath.getNASCProportions(nasc, lDist, lengthInterval, depth, specTS);

                    Double mixval = result.getData().getGroupRowColValueAsDouble(mixAcoCat, edsu, channel);
                    if (mixval == null) {
                        logger.error("Mix NASC is missing.", null);
                    }
                    // Move the proportion from mix to single categories
                    for (String acoCat : nascProp.getRowKeys()) {
                        MatrixBO m = nascProp.getRowValueAsMatrix(acoCat);
                        if (m == null) {
                            continue;
                        }
                        Double s = m.getSum();
                        if (s == null) {
                            continue;
                        }
                        // Move the nasc proportion out of split into the single category.
                        result.getData().addGroupRowColValue(acoCat, edsu, channel, s);
                        mixval -= s;
                        result.getData().setGroupRowColValue(mixAcoCat, edsu, channel, mixval);
                    }
                    if (Math.abs(mixval) > 0.000000001 && mixval < 0) {
                        logger.error("Error in splitting mix - val < 0", null);
                    }
                    // It is not possible to retain a mix value at this moment, because the lfq is required so that at least one species will get the nasc value.
                    result.getData().getGroupRowValueAsMatrix(mixAcoCat, edsu).removeValue(channel);
                }
            }
        }

        return result;
    }

    private Map<String, MatrixBO> getSpecTS(String speciesTS, List<String> specKeys) {
        Map<String, MatrixBO> res2 = new HashMap<>();
        // Correct case from specKeys in the res:
        // And filter out those not found in specKeys
        List<SpeciesTSMix> lines = SpeciesTSMix.fromString(speciesTS);
        List<SpeciesTSMix> linesCorr = lines.stream()
                .map(st -> {
                    if (st.getMixAcoCat() == null || st.getAcoCat() == null || st.getSpecCat() == null) {
                        return null;
                    }
                    Optional<String> opt = specKeys.stream().filter(st.getSpecCat()::equalsIgnoreCase).findFirst();
                    if (opt.isPresent()) {
                        st.setSpecCat(opt.get());
                        return st;
                    }
                    return null;
                })
                .filter(p -> p != null)
                .collect(Collectors.toList());

        linesCorr.forEach(line -> {
            MatrixBO res = res2.get(line.getMixAcoCat());
            if (res == null) {
                res = new MatrixBO();
                res2.put(line.getMixAcoCat(), res);
            }
            String acoCat = line.getAcoCat();
            // Wrap the spec cat to same case used by total length dist
            res.setRowColValue(acoCat, "SpecCat", line.getSpecCat());
            res.setRowColValue(acoCat, "m", line.getM());
            res.setRowColValue(acoCat, "a", line.getA());
            res.setRowColValue(acoCat, "d", line.getD());
        });
        return res2;
    }

}
