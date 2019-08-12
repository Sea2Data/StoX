package no.imr.stox.functions.biotic;

import java.util.Map;
import no.imr.stox.util.matrix.MatrixBO;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.util.base.Conversion;
import no.imr.stox.bo.LengthDistMatrix;
import no.imr.stox.functions.utils.BioticUtils;
import no.imr.stox.functions.utils.StoXMath;
import no.imr.stox.log.ILogger;

/**
 * Regroup the length distribution 
 */
public class RegroupLengthDist extends AbstractFunction {

    /**
     * @param input contains matrix STATIONLENGTHDIST, lengthinterval and logger
     * @return Matrix object of type STATIONLENGTHDIST_MATRIX - see
     * DataTypeDescription.txt
     */
    @Override
    public Object perform(Map<String, Object> input) {
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        LengthDistMatrix lengthDistMatrix = (LengthDistMatrix) input.get(Functions.PM_REGROUPLENGTHDIST_LENGTHDIST);
        MatrixBO lengthDist = lengthDistMatrix.getData();
        Double lenInterval = (Double) input.get(Functions.PM_REGROUPLENGTHDIST_LENGTHINTERVAL);
        // Check length interval compability:
        Double prevLenInterval = lengthDistMatrix.getResolutionMatrix().getRowValueAsDouble(Functions.RES_LENGTHINTERVAL);
        if (prevLenInterval != null && lenInterval != null && !StoXMath.isInteger(lenInterval / prevLenInterval)) {
            logger.error("Length interval " + lenInterval + " must be a multiple integer factor of " + prevLenInterval + ".", null);
        }

        LengthDistMatrix result = new LengthDistMatrix();
        result.getResolutionMatrix().setRowValue(Functions.RES_OBSERVATIONTYPE, lengthDistMatrix.getResolutionMatrix().getRowValue(Functions.RES_OBSERVATIONTYPE));
        result.getResolutionMatrix().setRowValue(Functions.RES_LENGTHINTERVAL, lenInterval);
        result.getResolutionMatrix().setRowValue(Functions.RES_LENGTHDISTTYPE, lengthDistMatrix.getResolutionMatrix().getRowValue(Functions.RES_LENGTHDISTTYPE));

        for (String specCat : lengthDist.getKeys()) {
            for (String obsKey : lengthDist.getGroupRowKeys(specCat)) {
                MatrixBO obs = (MatrixBO) lengthDist.getGroupRowValue(specCat, obsKey);
                MatrixBO lfq = obs.getDefaultValueAsMatrix();

                Double tot = 0d;
                for (String lenGrp : lfq.getKeys()) {
                    Double value = lfq.getValueAsDouble(lenGrp);
                    if(value == null) {
                        continue;
                    }
                    tot = StoXMath.append(value, tot);
                }
                if (tot == 0) {
                    continue;
                }
                for (String lenGrp : lfq.getKeys()) {
                    Double value = lfq.getValueAsDouble(lenGrp);
                    Double oldLenGrp = Conversion.safeStringtoDoubleNULL(lenGrp);
                    String newLenGroupKey = BioticUtils.getLenGrp(oldLenGrp, lenInterval);
                    if (newLenGroupKey.isEmpty()) {
                        continue;
                    }
                    result.getData().addGroupRowCellValue(specCat, obsKey, newLenGroupKey, value);
                }
            }
        }
        return result;
    }
}
