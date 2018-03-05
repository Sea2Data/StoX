/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.report;

/**
 *
 * @author aasmunds
 */
public class FillMissingData2_4 extends FillMissingData {

    /**
     * Override this to use unsorted abundance dataset.
     * @return 
     */
    @Override
    protected Boolean isSortedAbundance() {
        return false;
    }

}
