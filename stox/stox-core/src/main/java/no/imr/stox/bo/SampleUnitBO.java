/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.bo;

import java.util.Arrays;
import java.util.List;
import no.imr.sea2data.imrbase.matrix.MatricesBO;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;

/**
 *
 * @author aasmunds
 */
public class SampleUnitBO extends MatricesBO {

    public SampleUnitBO() {
        // SampleUnit datatype
        getMatrix(AbndEstProcessDataUtil.TABLE_EDSUPSU).setMetaMatrix(Functions.MM_EDSUPSU_MATRIX);
        getMatrix(AbndEstProcessDataUtil.TABLE_PSUSTRATUM).setMetaMatrix(Functions.MM_PSUSTRATUM_MATRIX);
    }

    @Override
    public List<String> getOutputOrder() {
        return Arrays.asList(
                AbndEstProcessDataUtil.TABLE_EDSUPSU,
                AbndEstProcessDataUtil.TABLE_PSUSTRATUM
        );
    }

}