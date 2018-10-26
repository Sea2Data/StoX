package no.imr.sea2data.biotic.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import no.imr.sea2data.imrbase.math.Calc;
import no.imr.sea2data.imrbase.math.ImrMath;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.stox.functions.utils.StoXMath;

/**
 *
 * @author oddrune
 */
public class IndividualBO implements Serializable {

    private Integer specimenid;
    private String individualproducttype;
    private Double individualweight;
    private Double individualweightG;
    private Double individualvolume;
    private String lengthresolution;
    private Double length;
    private Double lengthCM;
    private String fat;
    private String sex;
    private String maturationstage;
    private String specialstage;
    private String eggstage; // Used for forberg at capelin male (not in the database)
    private String stomachfillfield;
    private String stomachfilllab;
    private String digestion;
    private String liver;
    private String liverparasite;
    private String gillworms;
    private String swollengills;
    private String fungusheart;
    private String fungusspores;
    private String fungusouter;
    private String blackspot;
    private Integer vertebraecount;
    private Double gonadweight;
    private Double liverweight;
    private Double stomachweight;
    private String individualcomment;
    private List<AgeDeterminationBO> ageDeterminationBOs = new ArrayList<>();

    private CatchSampleBO catchsample;

    public IndividualBO() {
    }

    public IndividualBO(CatchSampleBO sampleF) {
        this.catchsample = sampleF;
    }

    public IndividualBO(CatchSampleBO sampleF, IndividualBO bo) {
        this(sampleF);
        specimenid = bo.getSpecimenid();
        individualproducttype = bo.getIndividualproducttype();
        individualweight = bo.getIndividualweight();
        individualweightG = bo.getIndividualweightG();
        individualvolume = bo.getindividualvolume();
        lengthresolution = bo.getLengthresolution();
        length = bo.getLength();
        lengthCM = bo.getLengthCM();
        fat = bo.getFat();
        sex = bo.getSex();
        maturationstage = bo.getMaturationstage();
        specialstage = bo.getSpecialstage();
        eggstage = bo.getEggstage();
        stomachfillfield = bo.getStomachfillfield();
        stomachfilllab = bo.getStomachfilllab();
        digestion = bo.getDigestion();
        liver = bo.getLiver();
        liverparasite = bo.getLiverparasite();
        gillworms = bo.getGillworms();
        swollengills = bo.getSwollengills();
        fungusheart = bo.getFungusheart();
        fungusspores = bo.getFungusspores();
        fungusouter = bo.getFungusouter();
        blackspot = bo.getBlackspot();
        vertebraecount = bo.getvertebraecount();
        gonadweight = bo.getGonadweight();
        liverweight = bo.getLiverweight();
        stomachweight = bo.getStomachweight();
        individualcomment = bo.getIndividualcomment();
        for (AgeDeterminationBO aBO : bo.getAgeDeterminationBOs()) {
            AgeDeterminationBO agBO = new AgeDeterminationBO(this, aBO);
            ageDeterminationBOs.add(agBO);
        }
    }

    public String getDigestion() {
        return digestion;
    }

    public void setDigestion(String digestion) {
        this.digestion = digestion;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getLengthresolution() {
        return lengthresolution;
    }

    public void setLengthresolution(String lengthresolution) {
        this.lengthresolution = lengthresolution;
    }

    public String getLiver() {
        return liver;
    }

    public void setLiver(String liver) {
        this.liver = liver;
    }

    public String getIndividualproducttype() {
        return individualproducttype;
    }

    public void setIndividualproducttype(String individualproducttype) {
        this.individualproducttype = individualproducttype;
    }

    public String getLiverparasite() {
        return liverparasite;
    }

    public void setLiverparasite(String liverparasite) {
        this.liverparasite = liverparasite;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSpecialstage() {
        return specialstage;
    }

    public void setSpecialstage(String specialstage) {
        this.specialstage = specialstage;
    }

    public String getMaturationstage() {
        return maturationstage;
    }

    public void setMaturationstage(String maturationstage) {
        this.maturationstage = maturationstage;
    }

    public String getStomachfillfield() {
        return stomachfillfield;
    }

    public void setStomachfillfield(String stomachfillfield) {
        this.stomachfillfield = stomachfillfield;
    }

    public String getStomachfilllab() {
        return stomachfilllab;
    }

    public void setStomachfilllab(String stomachfilllab) {
        this.stomachfilllab = stomachfilllab;
    }

    public void setIndividualweight(Double individualweight) {
        this.individualweight = individualweight;
        this.individualweightG = Calc.roundTo(StoXMath.kgToGrams(individualweight), 8);
    }

    public void setIndividualvolume(Double individualvolume) {
        this.individualvolume = individualvolume;
    }

    public void setVertebraecount(Integer vertebraecount) {
        this.vertebraecount = vertebraecount;
    }

    public void setStomachweight(Double stomachweight) {
        this.stomachweight = stomachweight;
    }

    public void setLiverweight(Double liverweight) {
        this.liverweight = liverweight;
    }

    public void setLength(Double length) {
        this.length = length;
        this.lengthCM = Calc.roundTo(StoXMath.mToCM(length), 8);
    }
    
    public void setGonadweight(Double gonadweight) {
        this.gonadweight = gonadweight;
    }

    private Double getIndividualweight() {
        return individualweight;
    }

    public Double getIndividualweightG() {
        return individualweightG;
    }
    
    public Double getindividualvolume() {
        return individualvolume;
    }

    public Integer getvertebraecount() {
        return vertebraecount;
    }

    public Double getStomachweight() {
        return stomachweight;
    }

    public Double getLiverweight() {
        return liverweight;
    }

    private Double getLength() {
        return length;
    }

    public Double getLengthCM() {
        return lengthCM;
    }

    public Integer getSpecimenid() {
        return specimenid;
    }

    public Double getGonadweight() {
        return gonadweight;
    }

    public void setIndividualcomment(String comment) {
        this.individualcomment = comment;
    }

    public String getIndividualcomment() {
        return individualcomment;
    }

    public String getEggstage() {
        return eggstage;
    }

    public void setEggstage(String eggstage) {
        this.eggstage = eggstage;
    }

    public String getGillworms() {
        return gillworms;
    }

    public void setGillworms(String gillworms) {
        this.gillworms = gillworms;
    }

    public String getSwollengills() {
        return swollengills;
    }

    public void setSwollengills(String swollengills) {
        this.swollengills = swollengills;
    }

    public String getFungusheart() {
        return fungusheart;
    }

    public void setFungusheart(String fungusheart) {
        this.fungusheart = fungusheart;
    }

    public String getFungusspores() {
        return fungusspores;
    }

    public void setFungusspores(String fungusspores) {
        this.fungusspores = fungusspores;
    }

    public String getFungusouter() {
        return fungusouter;
    }

    public void setFungusouter(String fungusouter) {
        this.fungusouter = fungusouter;
    }

    public String getBlackspot() {
        return blackspot;
    }

    public void setBlackspot(String blackspot) {
        this.blackspot = blackspot;
    }

    public void setSpecimenid(Integer specimenid) {
        this.specimenid = specimenid;
    }

    public CatchSampleBO getCatchSample() {
        return this.catchsample;
    }

    public Integer getAge() {
        return acquireAgeDet().getAge();
    }

    public Object getSpawningage() {
        return acquireAgeDet().getSpawningage();
    }

    public Object getSpawningzones() {
        return acquireAgeDet().getSpawningzones();
    }

    public Object getReadability() {
        return acquireAgeDet().getReadability();
    }

    public Object getOtolithtype() {
        return acquireAgeDet().getOtolithtype();
    }

    public Object getOtolithedge() {
        return acquireAgeDet().getOtolithedge();
    }

    public Object getOtolithcentre() {
        return acquireAgeDet().getOtolithcentre();
    }

    public Object getCalibration() {
        return acquireAgeDet().getCalibration();
    }

    @Override
    public String toString() {
        return getKey();
    }

    public List<AgeDeterminationBO> getAgeDeterminationBOs() {
        return ageDeterminationBOs;
    }

    public AgeDeterminationBO addAgeDetermination() {
        AgeDeterminationBO agedet = new AgeDeterminationBO(this);
        ageDeterminationBOs.add(agedet);
        return agedet;
    }

    public AgeDeterminationBO acquireAgeDet() {
        if (ageDeterminationBOs.isEmpty()) {
            return addAgeDetermination();
        }
        return ageDeterminationBOs.get(0);
    }

    public String getKey() {
        return (catchsample != null ? catchsample.getKey() + "/" : "") + (specimenid != null ? specimenid : "");
    }

}
