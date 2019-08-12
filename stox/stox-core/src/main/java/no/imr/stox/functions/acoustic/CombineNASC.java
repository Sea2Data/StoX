/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.acoustic;

import java.util.Map;
import no.imr.stox.util.matrix.MatrixBO;
import no.imr.stox.bo.NASCMatrix;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.log.ILogger;

/**
 *
 * @author aasmunds
 */
public class CombineNASC extends AbstractFunction {

    /**
     * @param input
     * @return NASC Matrix
     */
    @Override
    public Object perform(Map<String, Object> input) {
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        String targetCat = (String) input.get(Functions.PM_COMBINENASC_TARGETACOCAT);
        NASCMatrix nascMatrix = new NASCMatrix();
        String layerType = null;
        String sampleUnitType = null;
        MatrixBO sampleSizeM = null;
        MatrixBO distM = null;
        MatrixBO posSampleSizeM = null;
        // Define resolution:
        for (int i = 1; i < 50; i++) {
            NASCMatrix nm = (NASCMatrix) input.get("NASC" + i);
            if (nm == null) {
                continue;
            }
            String layerTypeS = (String) nm.getResolutionMatrix().getRowValue(Functions.RES_LAYERTYPE);
            String sampleUnitTypeS = (String) nm.getResolutionMatrix().getRowValue(Functions.RES_SAMPLEUNITTYPE);
            if (layerType != null && !layerType.equals(layerTypeS) || sampleUnitType != null && !sampleUnitType.equals(sampleUnitTypeS)) {
                logger.error("Sample unit and Layer type differs for the NASC matrices ", null);
            }
            layerType = layerTypeS;
            sampleUnitType = sampleUnitTypeS;
            sampleSizeM = nm.getSampleSizeMatrix();
            posSampleSizeM = nm.getPosSampleSizeMatrix();
            distM = nm.getSampleSizeMatrix();
        }
        nascMatrix.getResolutionMatrix().setRowValue(Functions.RES_LAYERTYPE, layerType);
        nascMatrix.getResolutionMatrix().setRowValue(Functions.RES_SAMPLEUNITTYPE, sampleUnitType);
        // Define samplesize and distance
        nascMatrix.setSampleSizeMatrix(sampleSizeM.copy());
        nascMatrix.setPosSampleSizeMatrix(posSampleSizeM.copy());
        nascMatrix.setDistanceMatrix(distM.copy());

        for (int i = 1; i < 50; i++) {
            NASCMatrix nm = (NASCMatrix) input.get("NASC" + i);
            if (nm == null) {
                continue;
            }
            for (String acoCat : nm.getData().getKeys()) {
                if (targetCat != null && !targetCat.equals(acoCat)) {
                    // filter
                    continue;
                }
                for (String su : nm.getData().getGroupRowKeys(acoCat)) {
                    for (String layer : nm.getData().getGroupRowColKeys(acoCat, su)) {
                        Double val = nm.getData().getGroupRowColValueAsDouble(acoCat, su, layer);
                        nascMatrix.getData().addGroupRowColValue(acoCat, su, layer, val);
                    }
                }
            }
        }
        return nascMatrix;
    }
}
