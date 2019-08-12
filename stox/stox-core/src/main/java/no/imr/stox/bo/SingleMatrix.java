/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.bo;

import no.imr.stox.util.matrix.MatricesBO;
import no.imr.stox.util.matrix.MatrixBO; 

/**
 *
 * @author aasmunds
 */
public abstract class SingleMatrix extends MatricesBO {

    protected static final String TABLE_DATA = "DATA";

    public MatrixBO getData() {
        return getMatrix(TABLE_DATA);
    }

    public void setData(MatrixBO m) {
        getMatrices().put(TABLE_DATA, m);
    }

    @Override
    protected MatrixBO createMatrix(String table) {
        switch (table) {
            case TABLE_DATA:
                return new MatrixBO(getMetaMatrix());
        }
        return super.createMatrix(table);
    }

    abstract protected String getMetaMatrix();
}
