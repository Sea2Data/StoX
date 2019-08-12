/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.bo;

import no.imr.stox.util.matrix.MatrixBO;
import java.util.Arrays;
import java.util.List; 
import no.imr.stox.functions.utils.Functions;

/**
 *
 * @author aasmunds
 */
public class AbundanceMatrix extends VariableWithEstimationLayer {

    protected static final String TABLE_AREA = "AREA";

    @Override
    public List<String> getOutputOrder() {
        return Arrays.asList(
                TABLE_DATA, TABLE_RESOLUTION, Functions.TABLE_ESTLAYERDEF, TABLE_AREA);
    }

    public MatrixBO getAreaMatrix() {
        return getMatrix(TABLE_AREA);
    }

    public void setAreaMatrix(MatrixBO m) {
        getMatrices().put(TABLE_AREA, m);
    }

    @Override
    public MatrixBO getSampleSizeMatrix() {
        return null; // Not relevant for abundance
    }

    @Override
    public MatrixBO getPosSampleSizeMatrix() {
        return null; // Not relevant for abundance
    }

    @Override
    public MatrixBO getDistanceMatrix() {
        return null;// Not relevant for abundance
    }


    @Override
    protected MatrixBO createMatrix(String table) {
        switch (table) {
            case TABLE_AREA:
                return new MatrixBO(Functions.MM_POLYGONAREA_MATRIX);
        }
        return super.createMatrix(table);
    }

    @Override
    protected String getMetaMatrix() {
        return Functions.MM_ABUNDANCE_MATRIX;
    }
}
