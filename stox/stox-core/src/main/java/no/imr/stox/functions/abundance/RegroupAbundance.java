package no.imr.stox.functions.abundance;

import java.util.Map;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.stox.bo.AbundanceMatrix;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.BioticUtils;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.StoXMath;
import no.imr.stox.log.ILogger;

/**
 * TODO: what does this class do?
 *
 * @author Åsmund
 */
public class RegroupAbundance extends AbstractFunction {

    /**
     * @param input contains matrix DENSITY_MATRIX
     * @return Matrix object of type AGGREGATEVERTICAL_MATRIX - see
     * DataTypeDescription.txt
     */
    @Override
    public Object perform(Map<String, Object> input) {

        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        AbundanceMatrix abundance = (AbundanceMatrix) input.get(Functions.PM_REGROUPABUNDANCE_ABUNDANCE);
        Double lenInterval = (Double) input.get(Functions.PM_REGROUPABUNDANCE_LENGTHINTERVAL);
        Double prevLenIntervals = abundance.getResolutionMatrix().getRowValueAsDouble(Functions.RES_LENGTHINTERVAL);
        if (prevLenIntervals != null && lenInterval != null && !StoXMath.isInteger(lenInterval / prevLenIntervals)) {
            logger.error("Length interval " + lenInterval + " must be a multiple integer factor of " + prevLenIntervals + ".", null);
        }
        AbundanceMatrix result = new AbundanceMatrix();
        // Define estimation layer from densities
        result.setEstLayerDefMatrix(abundance.getEstLayerDefMatrix());

        // Define resolution:
        result.getResolutionMatrix().setRowValue(Functions.RES_SAMPLEUNITTYPE, abundance.getResolutionMatrix().getRowValue(Functions.RES_SAMPLEUNITTYPE));
        result.getResolutionMatrix().setRowValue(Functions.RES_LAYERTYPE, abundance.getResolutionMatrix().getRowValue(Functions.RES_LAYERTYPE));
        result.getResolutionMatrix().setRowValue(Functions.RES_LENGTHINTERVAL, lenInterval);
        // Define area
        result.setAreaMatrix(abundance.getAreaMatrix().copy());

        for (String species : abundance.getData().getKeys()) {
            MatrixBO group = (MatrixBO) abundance.getData().getValue(species);
            for (String su : group.getKeys()) {
                MatrixBO row = (MatrixBO) group.getValue(su);
                for (String estLayer : row.getKeys()) {
                    MatrixBO cell = (MatrixBO) row.getValue(estLayer);
                    for (String lenGrp : cell.getKeys()) {
                        Double oldLenGrp = Conversion.safeStringtoDoubleNULL(lenGrp);
                        String newLenGroupKey = BioticUtils.getLenGrp(oldLenGrp, lenInterval);
                        Double abnd = cell.getValueAsDouble(lenGrp);
                        result.getData().addGroupRowColCellValue(species, su, estLayer, newLenGroupKey, abnd);
                    }
                }
            }
        }
        return result;
    }

}
