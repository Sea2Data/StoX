/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.bo;

import no.imr.stox.functions.utils.Functions; 

/**
 *
 * @author aasmunds
 */
public class AbundanceIndividualsMatrix extends SingleMatrix {

    @Override
    protected String getMetaMatrix() {
        return Functions.MM_SUPERINDABUNDANCE_MATRIX;
    }

}
