/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.sea2data.biotic.utils;

import java.util.List;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.MissionBO;

/**
 * Utilities functions for Biotic BO classes
 *
 * @author Åsmund
 */
public class BioticBOUtils {

    /**
     * Return a fishstation by year and serial number.
     *
     * @param fsl
     * @param year
     * @param serialNo
     * @return
     */
    public static FishstationBO getFishstationByYearAndSerialNo(List<MissionBO> msl, Integer year, Integer serialNo) {
        for (MissionBO ms : msl) {
            for (FishstationBO fs : ms.getFishstationBOs()) {
                if (year != null && serialNo != null && year.equals(fs.getYear()) && serialNo.equals(fs.getFs().getSerialnumber())) {
                    return fs;
                }
            }
        }
        return null;
    }
}
