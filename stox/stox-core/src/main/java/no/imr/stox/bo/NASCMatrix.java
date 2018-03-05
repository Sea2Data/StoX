/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.bo;

import java.util.Arrays;
import java.util.List;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import static no.imr.stox.bo.SingleMatrix.TABLE_DATA;
import static no.imr.stox.bo.SingleMatrixWithResolution.TABLE_RESOLUTION;
import no.imr.stox.functions.utils.Functions;

/**
 *
 * @author aasmunds
 */
public class NASCMatrix extends VariableWithDistance {

    @Override
    protected String getMetaMatrix() {
        return Functions.MM_NASC_MATRIX;
    }
    protected static final String TABLE_CHANNELTHICKNESS = "CHANNELTHICKNESS";

    @Override
    public List<String> getOutputOrder() {
        return Arrays.asList(
                TABLE_DATA, TABLE_SAMPLESIZE, TABLE_DISTANCE, TABLE_CHANNELTHICKNESS, TABLE_RESOLUTION);
    }

    public MatrixBO getChannelThicknessMatrix() {
        return getMatrix(TABLE_CHANNELTHICKNESS);
    }

    public void setChannelThicknessMatrix(MatrixBO m) {
        getMatrices().put(TABLE_CHANNELTHICKNESS, m);
    }

    @Override
    protected MatrixBO createMatrix(String table) {
        switch (table) {
            case TABLE_CHANNELTHICKNESS:
                return new MatrixBO(Functions.MM_SAMPLEUNIT_MATRIX);
        }
        return super.createMatrix(table);
    }
}
