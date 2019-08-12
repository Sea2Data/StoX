/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.bo;

import no.imr.stox.util.matrix.MatrixBO; 

/**
 *
 * @author aasmunds
 */
public class MatrixUtil {

    /**
     * Manipulate a matrix with row values, one col value and values. Can be
     * used i.e to set scaled mean NASC values in one call from a bootstrap in r.
     *
     * @param m
     * @param groupValues
     * @param rowValues the row Values
     * @param colValues
     * @param colValue the col value
     * @param values the values corresponding to rhe rowValues.
     */
    public static void setGroupRowColValues(MatrixBO m, String[] groupValues, String[] rowValues, String[] colValues, double[] values) {
        for (int i = 0; i < rowValues.length; i++) {
            m.setGroupRowColValue(groupValues[i], rowValues[i], colValues[i], values[i]);
        }
    }

}
