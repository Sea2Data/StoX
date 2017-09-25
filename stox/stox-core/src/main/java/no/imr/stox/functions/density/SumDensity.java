package no.imr.stox.functions.density;

import java.util.Map;
import no.imr.stox.bo.DensityMatrix;
import no.imr.stox.functions.AbstractFunction;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.functions.utils.AbndEstParamUtil;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.log.ILogger;

/**
 * TODO: what does this class do?
 *
 * @author aasmund
 */
public class SumDensity extends AbstractFunction {

    /**
     * @param input contains matrix DENSITY_MATRIX
     * @return Matrix object of type AGGREGATEVERTICAL_MATRIX - see
     * DataTypeDescription.txt
     * @throws no.imr.stox.exception.UserErrorException
     */
    @Override
    public Object perform(Map<String, Object> input) {
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        DensityMatrix density = (DensityMatrix) input.get(Functions.PM_SUMDENSITY_DENSITY);
        DensityMatrix result = new DensityMatrix();//Functions.MM_ESTLAYER_DENSITY_MATRIX);
        // Set how to translate the SampleUnit in the result, i.e labels in csv outpur

        // Inherite the estimation layer from density
        result.setEstLayerDefMatrix(density.getEstLayerDefMatrix());

        // Define resolution 
        // inherit sample unit
        result.getResolutionMatrix().setRowValue(Functions.RES_SAMPLEUNITTYPE, density.getResolutionMatrix().getRowValue(Functions.RES_SAMPLEUNITTYPE));
        // inherit length interval
        result.getResolutionMatrix().setRowValue(Functions.RES_LENGTHINTERVAL, density.getResolutionMatrix().getRowValue(Functions.RES_LENGTHINTERVAL));

        // Inherit the distance 
        result.setDistanceMatrix(density.getDistanceMatrix().copy());
        // Inherit the sample size
        result.setSampleSizeMatrix(density.getSampleSizeMatrix().copy());
        result.setPosSampleSizeMatrix(density.getPosSampleSizeMatrix().copy());
        for (String specCat : density.getData().getKeys()) {
            MatrixBO group = density.getData().getValueAsMatrix(specCat);
            for (String sampleUnit : group.getKeys()) {
                MatrixBO row = group.getValueAsMatrix(sampleUnit);
                for (String layer : row.getKeys()) {
                    MatrixBO cell = row.getValueAsMatrix(layer);
                    for (String lenGrp : cell.getKeys()) {
                        Double dens = cell.getValueAsDouble(lenGrp);
                        String estLayer = AbndEstParamUtil.getEstLayerFromLayer(result.getEstLayerDefMatrix(), layer);
                        if (estLayer == null) {
                            logger.error("Layer " + layer + " not included by estimation layer definition", null);
                            continue; // TODO give ero
                        }
                        result.getData().addGroupRowColCellValue(specCat, sampleUnit, estLayer, lenGrp, dens);
                    }
                }
            }
        }
        return result;
    }

}
