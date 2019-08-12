/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.bo;

import no.imr.stox.util.matrix.MatricesBO;
import java.util.Arrays;
import java.util.List;
import no.imr.stox.functions.utils.Functions; 

/**
 * TODO: what does this class do?
 *
 * @author aasmunds
 */
public class ReportsBO extends MatricesBO {

    /**
     * Get output order.
     *
     * @return
     */
    @Override
    public List<String> getOutputOrder() {
        return Arrays.asList(Functions.REPORT_XCATBYLENGTH);
    }

}
