/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.biotic;

import java.util.Map;
import no.imr.sea2data.biotic.bo.CatchSampleBO;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.biotic.bo.MissionBO;
import no.imr.sea2data.imrbase.math.Calc;
import no.imr.stox.bo.BioticData;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.BioticUtils;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.StoXMath;

/**
 *
 * @author aasmunds
 */
public class DefineLengthCentimeter extends AbstractFunction {

    @Override
    public Object perform(Map<String, Object> input) {
        BioticData origMissions = (BioticData) input.get(Functions.PM_DEFINELENGTHCENTIMETER_BIOTICDATA);
        BioticData missions = BioticUtils.copyBioticData(origMissions, BioticUtils.BIOTICDATA_COPY_FLAGS_USEEXISTINGDATA);
        missions.setLengthCMAdded(true);
        for (MissionBO ms : missions.getMissions()) {
            for (FishstationBO fs : ms.getFishstationBOs()) {
                for (CatchSampleBO cb : fs.getCatchSampleBOs()) {
                    for (IndividualBO i : cb.getIndividualBOs()) {
                        i.setLengthCentimeter(Calc.roundTo(StoXMath.mToCM(i.bo().getLength()), 8));
                    }
                }
            }
        }
        return missions;
    }
}
