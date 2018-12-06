/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.bo.landing;

import LandingsTypes.v2.SeddellinjeType;
import no.imr.sea2data.biotic.bo.BaseBO;

/**
 *
 * @author aasmunds
 */
public class SeddellinjeBO extends BaseBO {

    Double latitude;
    Double longitude;
    String stratum;

    public SeddellinjeBO(LandingsdataBO ld, SeddellinjeType fs) {
        super(ld, fs);
    }

    public SeddellinjeBO(LandingsdataBO ld, SeddellinjeBO bo) {
        this(ld, bo.bo());
        latitude = bo.getLatitude();
        longitude = bo.getLongitude();
    }

    public LandingsdataBO getLandingsdata() {
        return (LandingsdataBO) getParent();
    }

    public SeddellinjeType bo() {
        return (SeddellinjeType) bo;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getStratum() {
        return stratum;
    }

    public void setStratum(String stratum) {
        this.stratum = stratum;
    }
/*    public Integer getQuarter() {
        return ((SeddellinjeType)bo).getProduksjon().getLandingsdato()
    }*/
}
