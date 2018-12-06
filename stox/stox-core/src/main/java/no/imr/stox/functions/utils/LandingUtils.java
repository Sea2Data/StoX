/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.utils;

import java.util.ArrayList;
import java.util.List;
import no.imr.stox.bo.LandingData;
import no.imr.stox.bo.landing.LandingsdataBO;
import no.imr.stox.bo.landing.SeddellinjeBO;

/**
 *
 * @author aasmunds
 */
public class LandingUtils {

    public static LandingData copyLandingData(LandingData ldList) {
        LandingData landingsdata = new LandingData();
        ldList.forEach((ld) -> {
            LandingsdataBO ld2 = new LandingsdataBO(ld);
            landingsdata.add(ld2);
            ld.getSeddellinjeBOs().forEach((sl) -> {
                ld2.addSeddellinje(new SeddellinjeBO(ld2, sl));
            });
        });
        return landingsdata;
    }
}
