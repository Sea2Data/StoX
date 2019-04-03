/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.bo;

import java.util.ArrayList;
import java.util.List;
import no.imr.sea2data.biotic.bo.MissionBO;

/**
 *
 * @author aasmunds
 */
public class BioticData {

    List<MissionBO> missions;

    public BioticData() {
        missions = new ArrayList<>();
    }

    public BioticData(List<MissionBO> missions) {
        this.missions = missions;
    }

    public List<MissionBO> getMissions() {
        return missions;
    }

    boolean specCatAdded = false;
    boolean lengthCMAdded = false;
    boolean individualWeightGAdded = false;
    boolean ageMerged = false;

    public boolean isSpecCatAdded() {
        return specCatAdded;
    }

    public void setSpecCatAdded(boolean specCatAdded) {
        this.specCatAdded = specCatAdded;
    }

    public boolean isLengthCMAdded() {
        return lengthCMAdded;
    }

    public void setLengthCMAdded(boolean lengthCMAdded) {
        this.lengthCMAdded = lengthCMAdded;
    }

    public boolean isIndividualWeightGAdded() {
        return individualWeightGAdded;
    }

    public void setIndividualWeightGAdded(boolean individualWeightGAdded) {
        this.individualWeightGAdded = individualWeightGAdded;
    }

    public boolean isAgeMerged() {
        return ageMerged;
    }

    public void setAgeMerged(boolean ageAdded) {
        this.ageMerged = ageAdded;
    }
}
