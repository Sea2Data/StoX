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
public class MergeAgeDeterminationToIndividual extends AbstractFunction {

    @Override
    public Object perform(Map<String, Object> input) {
        BioticData origMissions = (BioticData) input.get(Functions.PM_MERGEAGEDETERMINATIONTOINDIVIDUAL_BIOTICDATA);
        BioticData missions = BioticUtils.copyBioticData(origMissions, BioticUtils.BIOTICDATA_COPY_FLAGS_USEEXISTINGDATA);
        missions.setAgeMerged(true);
        return missions;
    }
}
