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
public abstract class SingleMatrixWithResolution extends SingleMatrix {

    protected static final String TABLE_RESOLUTION = "RESOLUTION"; 

    @Override
    public List<String> getOutputOrder() {
        return Arrays.asList(
                TABLE_DATA, TABLE_RESOLUTION);
    }

    public MatrixBO getResolutionMatrix() {
        return getMatrix(TABLE_RESOLUTION);
    }

    public void setResolutionMatrix(MatrixBO m) {
        getMatrices().put(TABLE_RESOLUTION, m);
    }

    @Override
    protected MatrixBO createMatrix(String table) {
        switch (table) {
            case TABLE_RESOLUTION:
                return new MatrixBO(Functions.MM_VARIABLE_MATRIX);
        }
        return super.createMatrix(table);
    }

}
