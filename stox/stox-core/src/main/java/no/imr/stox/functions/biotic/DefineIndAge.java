/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.biotic;

import java.util.Map;
import no.imr.stox.bo.BioticData;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.BioticUtils;
import no.imr.stox.functions.utils.Functions;

/**
 *
 * @author aasmunds
 */
public class DefineIndAge extends AbstractFunction {

    @Override
    public Object perform(Map<String, Object> input) {
        BioticData origMissions = (BioticData) input.get(Functions.PM_DEFINEINDMEASUREUNIT_BIOTICDATA);
        Boolean includeAge = (Boolean) input.get(Functions.PM_DEFINEINDAGE_AGE);
        BioticData missions = BioticUtils.copyBioticData(origMissions);
        missions.setAgeAdded(includeAge);
        return missions;
    }
}
