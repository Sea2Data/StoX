/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.biotic;

import java.util.List;
import java.util.Map;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.MissionBO;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.Functions;

/**
 * Take in the the biotic data and return it to be used by the data storage for
 * reporting.
 *
 * @author aasmunds
 */
public class DATRASConvert extends AbstractFunction {

    @Override
    public Object perform(Map<String, Object> input) {
        return (List<MissionBO>) input.get(Functions.PM_DATRAS_BIOTICDATA);
    }

}
