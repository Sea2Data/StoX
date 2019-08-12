/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.bo;

import no.imr.stox.util.matrix.MatrixBO;
import java.util.Arrays;
import java.util.List;
import static no.imr.stox.bo.SingleMatrix.TABLE_DATA; 
import static no.imr.stox.bo.SingleMatrixWithResolution.TABLE_RESOLUTION;
import no.imr.stox.functions.utils.Functions;

/**
 *
 * @author aasmunds
 */
public abstract class VariableWithDistance extends SingleMatrixWithResolution {

    protected static final String TABLE_SAMPLESIZE = "SAMPLESIZE";
    protected static final String TABLE_POSSAMPLESIZE = "POSSAMPLESIZE";
    protected static final String TABLE_DISTANCE = "DISTANCE";

    @Override
    public List<String> getOutputOrder() {
        return Arrays.asList(
                TABLE_DATA, TABLE_SAMPLESIZE, TABLE_DISTANCE, TABLE_RESOLUTION);
    }

    public MatrixBO getSampleSizeMatrix() {
        return getMatrix(TABLE_SAMPLESIZE);
    }

    public MatrixBO getPosSampleSizeMatrix() {
        return getMatrix(TABLE_POSSAMPLESIZE);
    }
    public MatrixBO getDistanceMatrix() {
        return getMatrix(TABLE_DISTANCE);
    }

    public void setSampleSizeMatrix(MatrixBO m) {
        getMatrices().put(TABLE_SAMPLESIZE, m);
    }

    public void setPosSampleSizeMatrix(MatrixBO m) {
        getMatrices().put(TABLE_POSSAMPLESIZE, m);
    }

    public void setDistanceMatrix(MatrixBO m) {
        getMatrices().put(TABLE_DISTANCE, m);
    }

    @Override
    protected MatrixBO createMatrix(String table) {
        switch (table) {
            case TABLE_SAMPLESIZE:
                return new MatrixBO(Functions.MM_SAMPLEUNIT_MATRIX);
            case TABLE_POSSAMPLESIZE:
                return new MatrixBO(Functions.MM_SPECCATSAMPLEUNITLAYER_MATRIX);
            case TABLE_DISTANCE:
                return new MatrixBO(Functions.MM_SAMPLEUNIT_MATRIX);
        }
        return super.createMatrix(table);
    }
    
    public String getSUHdr() {
        return "SampleUnit";//(String) getResolutionMatrix().getRowValue(Functions.RES_SAMPLEUNITTYPE);
    }


}
