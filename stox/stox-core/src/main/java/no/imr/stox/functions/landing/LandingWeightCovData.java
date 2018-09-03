/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.landing;

import java.util.List;
import java.util.Map;
import no.imr.stox.bo.LandingCovDataMatrix;
import no.imr.stox.bo.LandingWeightCovDataMatrix;
import no.imr.stox.bo.landing.FiskeLinje;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.Functions;

/**
 *
 * @author aasmunds
 */
public class LandingWeightCovData extends AbstractFunction {

    /**
     * define covariate individual data
     *
     * @param input
     * @return
     */
    @Override
    public Object perform(Map<String, Object> input) {
        LandingCovDataMatrix landCovData = (LandingCovDataMatrix) input.get(Functions.PM_LANDINGWEIGHTCOVDATA_LANDINGCOVDATA);
        LandingWeightCovDataMatrix landWeight = new LandingWeightCovDataMatrix();
        // Default handling (Define by given time interval:
        for (String covKey : landCovData.getData().getRowKeys()) {
            List<FiskeLinje> fls = (List) landCovData.getData().getRowValue(covKey);
            for (FiskeLinje fl : fls) {
                Double rw = fl.getRundVekt();
                if (rw == null) {
                    continue;
                }
                Double w = landWeight.getData().getRowValueAsDouble(covKey);
                if (w == null) {
                    w = 0d;
                }
                w += rw;
                landWeight.getData().setRowValue(covKey, w);
            }
        }
        return landWeight;
    }
}
