/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.datastorage;

import java.io.Writer;
import no.imr.stox.util.matrix.MatricesBO;
import no.imr.stox.util.matrix.MatrixBO;

/**
 *
 * @author aasmunds
 */
public class MatricesStorage extends FileDataStorage {

    @Override
    public <T> void asTable(T data, Integer level, Writer wr, Boolean withUnits) {
        MatricesBO ms = (MatricesBO) data;
        ms.asTable(wr);
    }

    @Override
    public <T> void prepareData(T data) {
        MatricesBO ms = (MatricesBO) data;
        for (MatrixBO m : ms.getMatrices().values()) {
            super.prepareData(m);
        }
    }

}
