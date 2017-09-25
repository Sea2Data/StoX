/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.biotic;

import java.util.Map;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.bo.LengthDistMatrix;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.BioticUtils;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.log.ILogger;

/**
 *
 * @author aasmunds
 */
public class RelLengthDist extends AbstractFunction {

    /**
     * Relative Length distribution
     *
     * @param input
     * @return
     */
    @Override
    public Object perform(Map<String, Object> input) {
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        LengthDistMatrix lengthDistMatrix = (LengthDistMatrix) input.get(Functions.PM_RELLENGTHDIST_LENGTHDIST);
        MatrixBO lengthDist = lengthDistMatrix.getData();
        Double prevLenInterval = lengthDistMatrix.getResolutionMatrix().getRowValueAsDouble(Functions.RES_LENGTHINTERVAL);
        LengthDistMatrix result = new LengthDistMatrix();
        result.getResolutionMatrix().setRowValue(Functions.RES_OBSERVATIONTYPE, lengthDistMatrix.getResolutionMatrix().getRowValue(Functions.RES_OBSERVATIONTYPE));
        result.getResolutionMatrix().setRowValue(Functions.RES_LENGTHINTERVAL, prevLenInterval);
        result.getResolutionMatrix().setRowValue(Functions.RES_LENGTHDISTTYPE, Functions.LENGTHDISTTYPE_PERCENTLENGHTDIST);
        result.setData(lengthDist.copy());
        BioticUtils.toPercent(result);
        return result;
    }
}
