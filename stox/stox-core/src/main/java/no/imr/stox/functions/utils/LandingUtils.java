/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.utils;

import java.util.ArrayList;
import java.util.List;
import no.imr.sea2data.biotic.bo.AgeDeterminationBO;
import no.imr.sea2data.biotic.bo.CatchSampleBO;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.stox.bo.landing.FiskeLinje;
import no.imr.stox.bo.landing.SluttSeddel;

/**
 *
 * @author aasmunds
 */
public class LandingUtils {

    public static List<SluttSeddel> copyLandingData(List<SluttSeddel> sList) {
        List<SluttSeddel> fiskelinjer = new ArrayList<>();
        sList.forEach((sl) -> {
            SluttSeddel sl2 = new SluttSeddel(sl);
            fiskelinjer.add(sl2);
            sl.getFiskelinjer().forEach((fl) -> {
                sl2.addFiskeLinje(new FiskeLinje(sl2, fl));
            });
        });
        return fiskelinjer;
    }

}
