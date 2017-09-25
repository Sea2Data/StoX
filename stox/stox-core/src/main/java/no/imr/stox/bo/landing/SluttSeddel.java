/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.bo.landing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author aasmunds
 */
public class SluttSeddel {

    Integer fangstAar;
    String id;
    Integer dokType;
    String sltsNr;
    Date formularDato;
    String salgslag;
    String salgslagOrgnr;
    String kjopOrgnr;
    String kjopKundenr;
    String kjopLand;
    String fiskerKomm;
    String fiskerLand;
    Integer fiskerManntall;
    String fartRegm;
    String fartLand;
    String fartType;
    Integer antMann;
    String kvoteType;
    Date sisteFangstDato;
    String fangstRegion;
    String fangstKystHav;
    Integer fangstHomr;
    String fangstLok;
    Double latitude;
    Double longitude;
    String stratum;
    String fangstSone;
    String redskap;
    String kvoteLand;
    Integer fiskedager;
    Date landingsDato;
    String landingsMottak;
    String landingsKomm;
    String landingsLand;
    List<FiskeLinje> fiskelinjer = new ArrayList<>();

    public SluttSeddel(Integer fangstAar, String id) {
        this.fangstAar = fangstAar;
        this.id = id;
    }

    public SluttSeddel(SluttSeddel sl) {
        this.fangstAar = sl.getFangstAar();
        this.id = sl.getId();
        this.dokType = sl.getDokType();
        this.sltsNr = sl.getSltsNr();
        this.formularDato = sl.getFormularDato();
        this.salgslag = sl.getSalgslag();
        this.salgslagOrgnr = sl.getSalgslagOrgnr();
        this.kjopOrgnr = sl.getKjopOrgnr();
        this.kjopKundenr = sl.getKjopKundenr();
        this.kjopLand = sl.getKjopLand();
        this.fiskerKomm = sl.getFiskerKomm();
        this.fiskerLand = sl.getFiskerLand();
        this.fiskerManntall = sl.getFiskerManntall();
        this.fartRegm = sl.getFartRegm();
        this.fartLand = sl.getFartLand();
        this.fartType = sl.getFartType();
        this.antMann = sl.getAntMann();
        this.kvoteType = sl.getKvoteType();
        this.sisteFangstDato = sl.getSisteFangstDato();
        this.fangstRegion = sl.getFangstRegion();
        this.fangstKystHav = sl.getFangstKystHav();
        this.fangstHomr = sl.getFangstHomr();
        this.fangstLok = sl.getFangstLok();
        this.latitude = sl.getLatitude();
        this.longitude = sl.getLongitude();
        this.stratum = sl.getStratum();
        this.fangstSone = sl.getFangstSone();
        this.redskap = sl.getRedskap();
        this.kvoteLand = sl.getKvoteLand();
        this.fiskedager = sl.getFiskedager();
        this.landingsDato = sl.getLandingsDato();
        this.landingsMottak = sl.getLandingsMottak();
        this.landingsKomm = sl.getLandingsKomm();
        this.landingsLand = sl.getLandingsLand();
    }

    public List<FiskeLinje> getFiskelinjer() {
        return fiskelinjer;
    }

    public Integer getFangstAar() {
        return fangstAar;
    }

    public void setFangstAar(Integer fangstAar) {
        this.fangstAar = fangstAar;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getDokType() {
        return dokType;
    }

    public void setDokType(Integer dokType) {
        this.dokType = dokType;
    }

    public String getSltsNr() {
        return sltsNr;
    }

    public void setSltsNr(String sltsNr) {
        this.sltsNr = sltsNr;
    }

    public Date getFormularDato() {
        return formularDato;
    }

    public void setFormularDato(Date formularDato) {
        this.formularDato = formularDato;
    }

    public String getSalgslag() {
        return salgslag;
    }

    public void setSalgslag(String salgslag) {
        this.salgslag = salgslag;
    }

    public String getSalgslagOrgnr() {
        return salgslagOrgnr;
    }

    public void setSalgslagOrgnr(String salgslagOrgnr) {
        this.salgslagOrgnr = salgslagOrgnr;
    }

    public String getKjopOrgnr() {
        return kjopOrgnr;
    }

    public void setKjopOrgnr(String kjopOrgnr) {
        this.kjopOrgnr = kjopOrgnr;
    }

    public String getKjopKundenr() {
        return kjopKundenr;
    }

    public void setKjopKundenr(String kjopKundenr) {
        this.kjopKundenr = kjopKundenr;
    }

    public String getKjopLand() {
        return kjopLand;
    }

    public void setKjopLand(String kjopLand) {
        this.kjopLand = kjopLand;
    }

    public String getFiskerKomm() {
        return fiskerKomm;
    }

    public void setFiskerKomm(String fiskerKomm) {
        this.fiskerKomm = fiskerKomm;
    }

    public String getFiskerLand() {
        return fiskerLand;
    }

    public void setFiskerLand(String fiskerLand) {
        this.fiskerLand = fiskerLand;
    }

    public Integer getFiskerManntall() {
        return fiskerManntall;
    }

    public void setFiskerManntall(Integer fiskerManntall) {
        this.fiskerManntall = fiskerManntall;
    }

    public String getFartRegm() {
        return fartRegm;
    }

    public void setFartRegm(String fartRegm) {
        this.fartRegm = fartRegm;
    }

    public String getFartLand() {
        return fartLand;
    }

    public void setFartLand(String fartLand) {
        this.fartLand = fartLand;
    }

    public String getFartType() {
        return fartType;
    }

    public void setFartType(String fartType) {
        this.fartType = fartType;
    }

    public Integer getAntMann() {
        return antMann;
    }

    public void setAntMann(Integer antMann) {
        this.antMann = antMann;
    }

    public String getKvoteType() {
        return kvoteType;
    }

    public void setKvoteType(String kvoteType) {
        this.kvoteType = kvoteType;
    }

    public Date getSisteFangstDato() {
        return sisteFangstDato;
    }

    public void setSisteFangstDato(Date sisteFangstDato) {
        this.sisteFangstDato = sisteFangstDato;
    }

    public String getFangstRegion() {
        return fangstRegion;
    }

    public void setFangstRegion(String fangstRegion) {
        this.fangstRegion = fangstRegion;
    }

    public String getFangstKystHav() {
        return fangstKystHav;
    }

    public void setFangstKystHav(String fangstKystHav) {
        this.fangstKystHav = fangstKystHav;
    }

    public Integer getFangstHomr() {
        return fangstHomr;
    }

    public void setFangstHomr(Integer fangstHomr) {
        this.fangstHomr = fangstHomr;
    }

    public String getFangstLok() {
        return fangstLok;
    }

    public void setFangstLok(String fangstLok) {
        this.fangstLok = fangstLok;
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

    public String getFangstSone() {
        return fangstSone;
    }

    public void setFangstSone(String fangstSone) {
        this.fangstSone = fangstSone;
    }

    public String getRedskap() {
        return redskap;
    }

    public void setRedskap(String redskap) {
        this.redskap = redskap;
    }

    public String getKvoteLand() {
        return kvoteLand;
    }

    public void setKvoteLand(String kvoteLand) {
        this.kvoteLand = kvoteLand;
    }

    public Integer getFiskedager() {
        return fiskedager;
    }

    public void setFiskedager(Integer fiskedager) {
        this.fiskedager = fiskedager;
    }

    public Date getLandingsDato() {
        return landingsDato;
    }

    public void setLandingsDato(Date landingsDato) {
        this.landingsDato = landingsDato;
    }

    public String getLandingsMottak() {
        return landingsMottak;
    }

    public void setLandingsMottak(String landingsMottak) {
        this.landingsMottak = landingsMottak;
    }

    public String getLandingsKomm() {
        return landingsKomm;
    }

    public void setLandingsKomm(String landingsKomm) {
        this.landingsKomm = landingsKomm;
    }

    public String getLandingsLand() {
        return landingsLand;
    }

    public void setLandingsLand(String landingsLand) {
        this.landingsLand = landingsLand;
    }

    @Override
    public String toString() {
        return fangstAar + "/" + id;
    }

}
