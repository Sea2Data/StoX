/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.sea2data.biotic.bo;

import BioticTypes.v3.FishstationType;
import BioticTypes.v3.MissionType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author aasmunds
 */
public class MissionBO extends BaseBO {

    private List<FishstationBO> fishstationBOs = new ArrayList<>();

    public MissionBO() {
        this(new MissionType());
    }

    public MissionBO(MissionType ms) {
        super(null, ms);
    }

    public MissionBO(MissionBO bo) {
        this(bo.bo());
    }

    public MissionType bo() {
        return (MissionType) bo;
    }

    public List<FishstationBO> getFishstationBOs() {
        return fishstationBOs;
    }

    public FishstationBO addFishstation(FishstationType fst) {
        if (fst == null) {
            fst = new FishstationType();
        }
        FishstationBO fs = new FishstationBO(this, fst);
        getFishstationBOs().add(fs);
        return fs;
    }
}
