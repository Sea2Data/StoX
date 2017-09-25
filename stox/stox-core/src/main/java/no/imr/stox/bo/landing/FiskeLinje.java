/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.bo.landing;

/**
 *
 * @author aasmunds
 */
public class FiskeLinje {

    String id;
    String fisk;
    String konservering;
    String tilstand;
    String kvalitet;
    String anvendelse;
    Double prodVekt;
    Double rundVekt;
    SluttSeddel sluttSeddel;

    public FiskeLinje(SluttSeddel sluttSeddel, String id) {
        this.id = id;
        this.sluttSeddel = sluttSeddel;
    }

    public FiskeLinje(SluttSeddel sluttSeddel, FiskeLinje fl) {
        this.sluttSeddel = sluttSeddel;
        this.id = fl.getId();
        this.fisk = fl.getFisk();
        this.konservering = fl.getKonservering();
        this.tilstand = fl.getTilstand();
        this.kvalitet = fl.getKvalitet();
        this.anvendelse = fl.getAnvendelse();
        this.prodVekt = fl.getProdVekt();
        this.rundVekt = fl.getRundVekt();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFisk() {
        return fisk;
    }

    public void setFisk(String fisk) {
        this.fisk = fisk;
    }

    public String getKonservering() {
        return konservering;
    }

    public void setKonservering(String konservering) {
        this.konservering = konservering;
    }

    public String getTilstand() {
        return tilstand;
    }

    public void setTilstand(String tilstand) {
        this.tilstand = tilstand;
    }

    public String getKvalitet() {
        return kvalitet;
    }

    public void setKvalitet(String kvalitet) {
        this.kvalitet = kvalitet;
    }

    public String getAnvendelse() {
        return anvendelse;
    }

    public void setAnvendelse(String anvendelse) {
        this.anvendelse = anvendelse;
    }

    public Double getProdVekt() {
        return prodVekt;
    }

    public void setProdVekt(Double prodVekt) {
        this.prodVekt = prodVekt;
    }

    public Double getRundVekt() {
        return rundVekt;
    }

    public void setRundVekt(Double rundVekt) {
        this.rundVekt = rundVekt;
    }

    @Override
    public String toString() {
        return id + "/" + fisk;
    }

    public SluttSeddel getSluttSeddel() {
        return sluttSeddel;
    }

}
