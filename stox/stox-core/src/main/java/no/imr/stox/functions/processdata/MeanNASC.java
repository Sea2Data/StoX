/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.processdata;

import java.util.List;
import java.util.Map;
import no.imr.stox.util.matrix.MatrixBO;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.bo.NASCMatrix;
import no.imr.stox.functions.density.MeanDensity;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.StoXMath;
import no.imr.stox.log.ILogger;

/**
 * TODO: what does this class do?
 *
 * @author aasmunds
 */
public class MeanNASC extends MeanDensity {

    /**
     *
     * @param input
     * @return
     */
    @Override
    public Object perform(Map<String, Object> input) {
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        //Parameter: Name=ProcessData, DataType=ProcessData
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_MEANNASC_PROCESSDATA);
        //Parameter: Name=Densities, DataType=Matrix[ROW~SampleUnit / COL~Layer / VAR~NASC]
        NASCMatrix nasc = (NASCMatrix) input.get(Functions.PM_MEANNASC_NASC);
        String sampleUnitType = (String) input.get(Functions.PM_MEANNASC_SAMPLEUNITTYPE);
        Boolean sourceIsEDSU = nasc.getResolutionMatrix().getRowValue(Functions.RES_SAMPLEUNITTYPE).equals(Functions.SAMPLEUNIT_EDSU);
        NASCMatrix result = new NASCMatrix();
        // Define resolution:
        result.getResolutionMatrix().setRowValue(Functions.RES_SAMPLEUNITTYPE, sampleUnitType);
        result.getResolutionMatrix().setRowValue(Functions.RES_LAYERTYPE, nasc.getResolutionMatrix().getRowValue(Functions.RES_LAYERTYPE));
        // Create a target matrix to keep track of the target SUs
        MatrixBO targetMatrix = new MatrixBO("Matrix[ROW~SourceSampleUnit / VAR~TargetSampleUnit]");
        Boolean targetIsPSU = sampleUnitType.equals(Functions.SAMPLEUNIT_PSU);
        // Build the basis for calculating mean weights
        for (String sourceSU : nasc.getSampleSizeMatrix().getRowKeys()) {
            String targetSU = sourceIsEDSU ? AbndEstProcessDataUtil.getEDSUPSU(pd, sourceSU) : targetIsPSU ? sourceSU : AbndEstProcessDataUtil.getPSUStratum(pd, sourceSU);
            if (targetSU == null) {
                continue;
            }
            if (sourceIsEDSU && !targetIsPSU) {
                targetSU = AbndEstProcessDataUtil.getPSUStratum(pd, targetSU);
            }
            targetMatrix.setRowValue(sourceSU, targetSU);
            Double sourceChannelThickness = nasc.getChannelThicknessMatrix().getRowValueAsDouble(sourceSU);
            Double targetChannelThickness = result.getChannelThicknessMatrix().getRowValueAsDouble(targetSU);
            if (targetChannelThickness != null && !targetChannelThickness.equals(sourceChannelThickness)) {
                String s = (sourceIsEDSU ? "EDSU" : "PSU");
                String msg = "Channel thickness=" + sourceChannelThickness + " for " + s + " " + sourceSU
                        + " and is " + targetChannelThickness + " for the other " + s + "s at " + 
                        AbndEstProcessDataUtil.getSampleUnitPath(pd, targetSU, sampleUnitType);
                logger.error(msg, null);
            }
            result.getSampleSizeMatrix().addRowValue(targetSU, nasc.getSampleSizeMatrix().getRowValueAsInteger(sourceSU));
            result.getPosSampleSizeMatrix().addRowValue(targetSU, nasc.getPosSampleSizeMatrix().getRowValueAsInteger(sourceSU));
            result.getDistanceMatrix().addRowValue(targetSU, nasc.getDistanceMatrix().getRowValueAsDouble(sourceSU));
            result.getChannelThicknessMatrix().setRowValue(targetSU, sourceChannelThickness);
        }
        // Iterate the source samples and apply the densities to the mean
        List<String> rowKeys = targetMatrix.getRowKeys();
        if (rowKeys.isEmpty()) {
            String msg = "No " + (sourceIsEDSU ? "EDSU" : "PSU") + "s are assigned to " + (sourceIsEDSU ? "PSU" : "Stratum");
            logger.error(msg, null);
        }

        List<String> acoCats = nasc.getData().getKeys();
        if (acoCats.isEmpty()) {
            acoCats.add("-"); // only physical data (distances) is available. use a unknown symbol to this.
        }
        for (String acoCat : acoCats) {
            List<String> layers = nasc.getData().getGroupColKeys(acoCat);
            if (layers.isEmpty()) {
                layers.add("-"); // only physical data (distances) is available. use a unknown symbol to this.
            }
            for (String sourceSU : targetMatrix.getRowKeys()) {
                String targetSU = (String) targetMatrix.getRowValue(sourceSU);
                Double sourceWeight = nasc.getDistanceMatrix().getRowValueAsDouble(sourceSU);
                Double targetWeight = result.getDistanceMatrix().getRowValueAsDouble(targetSU);
                if (targetWeight == null || targetWeight == 0) {
                    continue;
                }
                Double relWeight = StoXMath.relativeToTotal(sourceWeight, targetWeight);
                // For each layer
                for (String layer : layers) {
                    // Handle source data
                    Double value = nasc.getData().getGroupRowColValueAsDouble(acoCat, sourceSU, layer);
                    Double valueProportion = StoXMath.proportion(value, relWeight);
                    if (valueProportion == null) {
                        valueProportion = 0d; // This is due to a rule for aggregation into mean. zero observations are included.
                    }
                    result.getData().addGroupRowColValue(acoCat, targetSU, layer, valueProportion);
                }
            }
        }

        return result;
    }
}
