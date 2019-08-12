/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.abundance;

import java.util.Map;
import no.imr.stox.bo.AbundanceMatrix;
import no.imr.stox.bo.DensityMatrix;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.util.matrix.MatrixBO;
import no.imr.stox.bo.PolygonAreaMatrix;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.StoXMath;

/**
 * TODO: what does this class do?
 *
 * @author Åsmund
 */
public class Abundance extends AbstractFunction {

    /**
     *
     * @param input contains matrix DENSITY_MATRIX
     * @return Matrix object of type ABUNDANCE_MATRIX - see
     * DataTypeDescription.txt
     */
    @Override
    public Object perform(Map<String, Object> input) {
        PolygonAreaMatrix sampleArea = (PolygonAreaMatrix) input.get(Functions.PM_ABUNDANCE_POLYGONAREA);//  Matrix[ROW~PolygonKey / VAR~Area]
        DensityMatrix densities = (DensityMatrix) input.get(Functions.PM_ABUNDANCE_DENSITY);
        // Set how to translate the SampleUnit in the result, i.e labels in csv outpur
        AbundanceMatrix result = new AbundanceMatrix();

        // Define estimation layer from densities
        result.setEstLayerDefMatrix(densities.getEstLayerDefMatrix());

        // Define resolution:
        result.getResolutionMatrix().setRowValue(Functions.RES_LAYERTYPE, densities.getResolutionMatrix().getRowValue(Functions.RES_LAYERTYPE));
        result.getResolutionMatrix().setRowValue(Functions.RES_SAMPLEUNITTYPE, densities.getResolutionMatrix().getRowValue(Functions.RES_SAMPLEUNITTYPE));
        result.getResolutionMatrix().setRowValue(Functions.RES_LENGTHINTERVAL, densities.getResolutionMatrix().getRowValue(Functions.RES_LENGTHINTERVAL));

        // Calculate abundance
        for (String species : densities.getData().getKeys()) {
            MatrixBO group = densities.getData().getValueAsMatrix(species);
            for (String sourceSU : group.getKeys()) {
                Double area = sampleArea.getData().getRowValueAsDouble(sourceSU);
                if (area == null) {
                    continue;
                }
                // Copy to area matrix (area for used sourcesSUs:
                result.getAreaMatrix().setRowValue(sourceSU, area);
                MatrixBO row = group.getValueAsMatrix(sourceSU);
                for (String estLayer : row.getKeys()) {
                    MatrixBO cell = row.getValueAsMatrix(estLayer);
                    for (String lenGrp : cell.getKeys()) {
                        Double density = cell.getValueAsDouble(lenGrp);
                        Double abundance = StoXMath.abundance(density, area);
                        result.getData().setGroupRowColCellValue(species, sourceSU, estLayer, lenGrp, abundance);
                    }
                }
            }
        }
        return result;
    }
}
