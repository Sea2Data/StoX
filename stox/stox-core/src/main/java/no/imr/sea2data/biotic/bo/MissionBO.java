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

    public FishstationBO addFishstation() {
        return addFishstation((FishstationType) null);
    }

    public FishstationBO addFishstation(FishstationType fst) {
        if (fst == null) {
            fst = new FishstationType();
        }
        return addFishstation(new FishstationBO(this, fst));
    }

    public FishstationBO addFishstation(FishstationBO bo) {
        getFishstationBOs().add(bo);
        return bo;
    }

    @Override
    public String getInternalKey() {
        String cruise = bo().getCruise();
        return cruise != null ? cruise : (bo().getStartyear() != null ? bo().getStartyear() + "" : "");
    }

}
