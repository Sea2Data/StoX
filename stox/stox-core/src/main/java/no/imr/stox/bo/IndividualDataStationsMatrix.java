/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.bo;

import java.util.Arrays;
import java.util.List;
import no.imr.stox.util.matrix.MatrixBO;
import static no.imr.stox.bo.SingleMatrix.TABLE_DATA;
import no.imr.stox.functions.utils.Functions; 

/**
 *
 * @author aasmunds
 */
public class IndividualDataStationsMatrix extends SingleMatrixWithResolution {

    protected static final String TABLE_ESTLAYERDEF = "ESTLAYERDEF";

    @Override
    public List<String> getOutputOrder() {
        return Arrays.asList(
                TABLE_DATA, TABLE_RESOLUTION, TABLE_ESTLAYERDEF);
    }

    public MatrixBO getEstLayerDefMatrix() {
        return getMatrix(TABLE_ESTLAYERDEF);
    }


    public void setEstLayerDefMatrix(MatrixBO m) {
        getMatrices().put(TABLE_ESTLAYERDEF, m);
    }

    @Override
    protected MatrixBO createMatrix(String table) {
        switch (table) {
            case TABLE_ESTLAYERDEF:
                return new MatrixBO(Functions.MM_DEFINEESTLAYER_MATRIX);
        }
        return super.createMatrix(table);
    }    @Override

    protected String getMetaMatrix() {
        return Functions.MM_INDIVIDUALDATASTATIONS_MATRIX;
    }

}
