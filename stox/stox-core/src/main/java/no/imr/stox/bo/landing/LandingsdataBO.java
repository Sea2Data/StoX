/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.bo.landing;

import LandingsTypes.v2.LandingsdataType;
import LandingsTypes.v2.SeddellinjeType;
import java.util.ArrayList;
import java.util.List;
import no.imr.sea2data.biotic.bo.BaseBO;

/**
 *
 * @author aasmunds
 */
public class LandingsdataBO extends BaseBO {

    private List<SeddellinjeBO> seddellinjeBOs = new ArrayList<>();

    public LandingsdataBO() {
        this(new LandingsdataType());
    }

    public LandingsdataBO(LandingsdataType ms) {
        super(null, ms);
    }

    public LandingsdataBO(LandingsdataBO bo) {
        this(bo.bo());
    }

    public LandingsdataType bo() {
        return (LandingsdataType) bo;
    }

    public List<SeddellinjeBO> getSeddellinjeBOs() {
        return seddellinjeBOs;
    }

    public SeddellinjeBO addSeddellinje() {
        return addSeddellinje((SeddellinjeType) null);
    }

    public SeddellinjeBO addSeddellinje(SeddellinjeType fst) {
        if (fst == null) {
            fst = new SeddellinjeType();
        }
        return addSeddellinje(new SeddellinjeBO(this, fst));
    }

    public SeddellinjeBO addSeddellinje(SeddellinjeBO bo) {
        getSeddellinjeBOs().add(bo);
        return bo;
    }

}
