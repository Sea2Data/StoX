/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.density;

import java.util.Map;
import no.imr.stox.bo.DensityMatrix;
import no.imr.stox.functions.AbstractFunction;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.StoXMath;

/**
 * TODO: what does this class do?
 *
 * @author aasmunds
 */
public class MeanDensity extends AbstractFunction {

    /**
     *
     * @param input contains matrix DENSITY_MATRIX, PROCESSDATA logger
     * @return Matrix object of type DENSITY_MATRIX - see
     * DataTypeDescription.txt
     */
    @Override
    public Object perform(Map<String, Object> input) {

        //Parameter: Name=ProcessData, DataType=ProcessData
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_MEANDENSITY_PROCESSDATA);
        //Parameter: Name=Densities, DataType=Matrix[GROUP~Species / ROW~SampleUnit / COL~Layer / CELL~LengthGroup / VAR~Density]
        DensityMatrix densities = (DensityMatrix) input.get(Functions.PM_MEANDENSITY_DENSITY);
        String sampleUnitType = (String) input.get(Functions.PM_MEANDENSITY_SAMPLEUNITTYPE);
        Boolean sourceIsEDSU = densities.getResolutionMatrix().getRowValue(Functions.RES_SAMPLEUNITTYPE).equals(Functions.SAMPLEUNIT_EDSU);
        DensityMatrix result = new DensityMatrix();
        // Inherit the estimation layer matrix
        result.setEstLayerDefMatrix(densities.getEstLayerDefMatrix());
        // Define resolution:
        result.getResolutionMatrix().setRowValue(Functions.RES_LENGTHINTERVAL, densities.getResolutionMatrix().getRowValueAsDouble(Functions.RES_LENGTHINTERVAL));

        result.getResolutionMatrix().setRowValue(Functions.RES_SAMPLEUNITTYPE, sampleUnitType);
        result.getResolutionMatrix().setRowValue(Functions.RES_LAYERTYPE, densities.getResolutionMatrix().getRowValue(Functions.RES_LAYERTYPE));
        // Create a target matrix to keep track of the target SUs
        MatrixBO targetMatrix = new MatrixBO("Matrix[ROW~SourceSampleUnit / VAR~TargetSampleUnit]");
        Boolean targetIsPSU = sampleUnitType.equals(Functions.SAMPLEUNIT_PSU);
        // Build the basis for calculating mean weights
        for (String sourceSU : densities.getSampleSizeMatrix().getRowKeys()) {
            String targetSU = sourceIsEDSU ? AbndEstProcessDataUtil.getEDSUPSU(pd, sourceSU) : targetIsPSU ? sourceSU : AbndEstProcessDataUtil.getPSUStratum(pd, sourceSU);
            if (targetSU == null) {
                continue;

            }
            if (sourceIsEDSU && !targetIsPSU) {
                targetSU = AbndEstProcessDataUtil.getPSUStratum(pd, targetSU);
            }
            targetMatrix.setRowValue(sourceSU, targetSU);
            result.getSampleSizeMatrix().addRowValue(targetSU, densities.getSampleSizeMatrix().getRowValueAsInteger(sourceSU));
            result.getDistanceMatrix().addRowValue(targetSU, densities.getDistanceMatrix().getRowValueAsDouble(sourceSU));
        }
        // Iterate the source samples and apply the densities to the mean
        MatrixBO posSampleSize = new MatrixBO();
        for (String sourceSU : targetMatrix.getRowKeys()) {
            String targetSU = (String) targetMatrix.getRowValue(sourceSU);
            Double sourceWeight = densities.getDistanceMatrix().getRowValueAsDouble(sourceSU);
            Double targetWeight = result.getDistanceMatrix().getRowValueAsDouble(targetSU);
            Double sampleSize = densities.getSampleSizeMatrix().getRowValueAsDouble(sourceSU);
            if (sourceWeight == null && targetWeight == null) {
                // Using sample size as weight when distance not given.
                sourceWeight = sampleSize;
                targetWeight = result.getSampleSizeMatrix().getRowValueAsDouble(targetSU);
            }
            if (targetWeight == null || targetWeight == 0) {
                continue;
            }
            Double relWeight = StoXMath.relativeToTotal(sourceWeight, targetWeight);
            for (String specCat : densities.getData().getKeys()) {
                // For each layer
                /*MatrixBO layerM = densities.getData().getGroupRowValueAsMatrix(specCat, sourceSU);
                if (layerM == null) {
                    continue;
                }*/
                for (String layer : densities.getData().getColKeys()) {
                    // Handle source data
                    MatrixBO densCell = (MatrixBO) densities.getData().getGroupRowColValueAsMatrix(specCat, sourceSU, layer);
                    if (densCell == null) {
                        result.getData().addGroupRowColCellValue(specCat, targetSU, layer, null, 0d);
                        continue;
                    }
                    for (String lenGrp : densCell.getKeys()) {
                        Double density = densCell.getValueAsDouble(lenGrp);
                        Double densProportion = StoXMath.proportion(density, relWeight);
                        if (densProportion != null && densProportion > 0d) {
                            posSampleSize.setGroupRowColValue(specCat, sourceSU, layer, sampleSize);
                        }
                        result.getData().addGroupRowColCellValue(specCat, targetSU, layer, lenGrp, densProportion);
                    }
                }
            }
        }
        // Aggregate the poaitive sample sizes
        for (String specCat : posSampleSize.getKeys()) {
            for (String sourceSU : posSampleSize.getRowKeys()) {
                String targetSU = (String) targetMatrix.getRowValue(sourceSU);
                for (String layer : posSampleSize.getColKeys()) {
                    Double sampleSize = posSampleSize.getGroupRowColValueAsDouble(specCat, sourceSU, layer);
                    result.getPosSampleSizeMatrix().addGroupRowColValue(specCat, targetSU, layer, sampleSize);
                }
            }
        }
        return result;
    }
}
