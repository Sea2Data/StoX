/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.bo;

import no.imr.sea2data.imrbase.matrix.MatrixBO;
import java.util.Arrays;
import java.util.List;
import static no.imr.stox.bo.SingleMatrix.TABLE_DATA; 
import static no.imr.stox.bo.VariableWithDistance.TABLE_SAMPLESIZE;
import no.imr.stox.functions.utils.Functions;

/**
 *
 * @author aasmunds
 */
public abstract class VariableWithEstimationLayer extends VariableWithDistance {


    @Override
    public List<String> getOutputOrder() {
        return Arrays.asList(
                TABLE_DATA, TABLE_SAMPLESIZE, TABLE_DISTANCE, TABLE_RESOLUTION, Functions.TABLE_ESTLAYERDEF);
    }

    public MatrixBO getEstLayerDefMatrix() {
        return getMatrices().get(Functions.TABLE_ESTLAYERDEF);
    }


    public void setEstLayerDefMatrix(MatrixBO m) {
        getMatrices().put(Functions.TABLE_ESTLAYERDEF, m);
    }

    @Override
    protected MatrixBO createMatrix(String table) {
        switch (table) {
            case Functions.TABLE_ESTLAYERDEF:
                return new MatrixBO(Functions.MM_DEFINEESTLAYER_MATRIX);
        }
        return super.createMatrix(table);
    }
    
}
