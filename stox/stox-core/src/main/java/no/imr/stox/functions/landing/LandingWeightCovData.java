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
        for (String tempKey : landCovData.getData().getKeys()) {
            for (String gearKey : landCovData.getData().getGroupRowKeys(tempKey)) {
                for (String spatialKey : landCovData.getData().getGroupRowColKeys(tempKey, gearKey)) {
                    List<FiskeLinje> fls = (List) landCovData.getData().getGroupRowColValue(tempKey, gearKey, spatialKey);
                    for (FiskeLinje fl : fls) {
                        Double rw = fl.getRundVekt();
                        if (rw == null) {
                            continue;
                        }
                        Double w = landWeight.getData().getGroupRowColValueAsDouble(tempKey, gearKey, spatialKey);
                        if (w == null) {
                            w = 0d;
                        }
                        w += rw;
                        landWeight.getData().setGroupRowColValue(tempKey, gearKey, spatialKey, w);
                    }
                }
            }
        }
        return landWeight;
    }
}
