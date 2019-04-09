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

    public static final int VAR_LENGHTCM = 1;
    public static final int VAR_INDIVIDUALWEIGHTGRAM = 2;
    public static final int VAR_AGE = 3;

    List<Integer> indvars = new ArrayList<>();
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

    /*    boolean lengthCMAdded = false;
    boolean individualWeightGAdded = false;
    boolean ageMerged = false;*/

    public boolean isSpecCatAdded() {
        return specCatAdded;
    }

    public void setSpecCatAdded(boolean specCatAdded) {
        this.specCatAdded = specCatAdded;
    }

    public boolean isLengthCMAdded() {
        return indvars.stream().anyMatch(i -> i != null && i.equals(VAR_LENGHTCM));
    }

    public void addLengthCM() {
        indvars.add(VAR_LENGHTCM);
    }

    public boolean isIndividualWeightGAdded() {
        return indvars.stream().anyMatch(i -> i != null && i.equals(VAR_INDIVIDUALWEIGHTGRAM));
    }

    public void addIndividualWeightG() {
        indvars.add(VAR_INDIVIDUALWEIGHTGRAM);
    }

    public boolean isAgeMerged() {
        return indvars.stream().anyMatch(i -> i != null && i.equals(VAR_AGE));
    }

    public void addAge() {
        indvars.add(VAR_AGE);
    }

    public List<Integer> getIndvars() {
        return indvars;
    }

    public void setIndvars(List<Integer> indvars) {
        this.indvars = new ArrayList<>(indvars);
    }
}
