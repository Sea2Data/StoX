package no.imr.sea2data.biotic.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author oddrune
 */
public class IndividualBO implements Serializable {

    private Integer original;
    private Date last_edited;

    private Integer individualNo;
    protected String measurementType;
    private Double weight;
    private Double volume;
    protected String lengthType;
    private Double length;
    protected String fat;
    protected String sex;
    protected String stage;
    protected String specialStage;
    private transient String eggStage; // Used for forberg at capelin male (not in the database)
    protected String stomachFill;
    protected String stomachFill2;
    protected String digestDeg;
    protected String liver;
    protected String liverParasite;
    protected String specialCode;
    // special code is not enough for combining illness parameters, thus introduce the following transients:
    private transient String gillWorms;
    private transient String swollenGills;
    private transient String fungusHeart;
    private transient String fungusSpores;
    private transient String fungusOuter;
    private transient String blackSpot;
    private Integer vertebrae;
    private List<no.imr.sea2data.biotic.bo.AgeDeterminationBO> ageDeterminationBOs = new ArrayList<>();
    private List<no.imr.sea2data.biotic.bo.PreyBO> preyBOs = new ArrayList<>();

    private Integer age; // this is redundant, since agedetermination keep allways one line with age data.

    private Double gonadWeight;
    private Double liverWeight;
    private Double stomachWeight;
    private Boolean fromLengthFrequency; // redundant?
    private String comment;

    private String description; // why is this a field?
    private SampleBO sample;
    private transient String developmentalStage;

    public IndividualBO() {
    }

    public IndividualBO(SampleBO sampleF, IndividualBO bo) {
        individualNo = bo.getIndividualNo();
        measurementType = bo.getMeasurementType();
        weight = bo.getWeight();
        volume = bo.getVolume();
        lengthType = bo.getLengthType();
        length = bo.getLength();
        fat = bo.getFat();
        sex = bo.getSex();
        stage = bo.getStage();
        developmentalStage = bo.getDevelopmentalStage();
        specialStage = bo.getSpecialStage();
        eggStage = bo.getEggStage();
        stomachFill = bo.getStomachFill();
        stomachFill2 = bo.getStomachFill2();
        digestDeg = bo.getDigestDeg();
        liver = bo.getLiver();
        liverParasite = bo.getLiverParasite();
        gillWorms = bo.getGillWorms();
        swollenGills = bo.getSwollenGills();
        fungusHeart = bo.getFungusHeart();
        fungusSpores = bo.getFungusSpores();
        fungusOuter = bo.getFungusOuter();
        blackSpot = bo.getBlackSpot();
        vertebrae = bo.getVertebrae();
        gonadWeight = bo.getGonadWeight();
        liverWeight = bo.getLiverWeight();
        stomachWeight = bo.getStomachWeight();
        comment = bo.getComment();
        sample = sampleF;
        for (AgeDeterminationBO aBO : bo.getAgeDeterminationBOCollection()) {
            AgeDeterminationBO agBO = new AgeDeterminationBO(this, aBO);
            ageDeterminationBOs.add(agBO);
        }
        for (PreyBO pBO : bo.getPreyBOCollection()) {
            PreyBO prBO = new PreyBO(sample, pBO);
            preyBOs.add(prBO);
        }
    }

    public String getDigestDeg() {
        return digestDeg;
    }

    public void setDigestDeg(String digestDeg) {
        this.digestDeg = digestDeg;
    }

    public String getFat() {
        return fat;
    }

    public void setFat(String fat) {
        this.fat = fat;
    }

    public String getLengthType() {
        return lengthType;
    }

    public void setLengthType(String lengthType) {
        this.lengthType = lengthType;
    }

    public String getLiver() {
        return liver;
    }

    public void setLiver(String liver) {
        this.liver = liver;
    }

    public String getMeasurementType() {
        return measurementType;
    }

    public void setMeasurementType(String measurementType) {
        this.measurementType = measurementType;
    }

    public void setProductType(String productType) {
        this.measurementType = productType;
    }
    
    public String getLiverParasite() {
        return liverParasite;
    }

    public void setLiverParasite(String liverParasite) {
        this.liverParasite = liverParasite;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getSpecialStage() {
        return specialStage;
    }

    public void setSpecialStage(String specialStage) {
        this.specialStage = specialStage;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getStomachFill() {
        return stomachFill;
    }

    public void setStomachFill(String stomachFill) {
        this.stomachFill = stomachFill;
    }

    public String getStomachFill2() {
        return stomachFill2;
    }

    public void setStomachFill2(String stomachFill2) {
        this.stomachFill2 = stomachFill2;
    }

    public void setOriginal(Integer original) {

        this.original = original;
    }

    public void setLast_edited(Date last_edited) {

        this.last_edited = last_edited;
    }

    public Integer getOriginal() {
        return original;
    }

    public Date getLast_edited() {
        return last_edited;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public void setVertebrae(Integer vertebrae) {
        this.vertebrae = vertebrae;
    }

    public void setStomachWeight(Double stomachWeight) {
        this.stomachWeight = stomachWeight;
    }

    public void setLiverWeight(Double liverWeight) {
        this.liverWeight = liverWeight;
    }

    public void setLength(Double length) {
        this.length = length;
    }

    public void setGonadWeight(Double gonadWeight) {
        this.gonadWeight = gonadWeight;
    }

    public void setFromLengthFrequency(Boolean fromLengthFrequency) {
        this.fromLengthFrequency = fromLengthFrequency;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getWeight() {
        return weight;
    }

    public Double getVolume() {
        return volume;
    }

    public Integer getVertebrae() {
        return vertebrae;
    }

    public Double getStomachWeight() {
        return stomachWeight;
    }

    public Double getLiverWeight() {
        return liverWeight;
    }

    public Double getLength() {
        return length;
    }

    public Integer getIndividualNo() {
        return individualNo;
    }

    public Double getGonadWeight() {
        return gonadWeight;
    }

    public Boolean getFromLengthFrequency() {
        return fromLengthFrequency;
    }

    public String description() {
        return description;
    }

    public List<PreyBO> getPreyBOCollection() {
        return preyBOs;
    }

    public void setPreyBOCollection(List<PreyBO> preyBOs) {
        this.preyBOs = preyBOs;
    }

    public String getSpecialCode() {
        return specialCode;
    }

    public void setSpecialCode(String specialCode) {
        this.specialCode = specialCode;
    }

    public List<AgeDeterminationBO> getAgeDeterminationBOCollection() {
        return ageDeterminationBOs;
    }

    public void setAgeDeterminationBOCollection(List<AgeDeterminationBO> ageDeterminationBOs) {
        this.ageDeterminationBOs = ageDeterminationBOs;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return getKey();
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public String getEggStage() {
        return eggStage;
    }

    public void setEggStage(String eggStage) {
        this.eggStage = eggStage;
    }

    public String getGillWorms() {
        return gillWorms;
    }

    public void setGillWorms(String gillWorms) {
        this.gillWorms = gillWorms;
    }

    public String getSwollenGills() {
        return swollenGills;
    }

    public void setSwollenGills(String swollenGills) {
        this.swollenGills = swollenGills;
    }

    public String getFungusHeart() {
        return fungusHeart;
    }

    public void setFungusHeart(String fungusHeart) {
        this.fungusHeart = fungusHeart;
    }

    public String getFungusSpores() {
        return fungusSpores;
    }

    public void setFungusSpores(String fungusSpores) {
        this.fungusSpores = fungusSpores;
    }

    public String getFungusOuter() {
        return fungusOuter;
    }

    public void setFungusOuter(String fungusOuter) {
        this.fungusOuter = fungusOuter;
    }

    public String getBlackSpot() {
        return blackSpot;
    }

    public void setBlackSpot(String blackSpot) {
        this.blackSpot = blackSpot;
    }

    public void setIndividualNo(Integer individualNo) {
        this.individualNo = individualNo;
    }

    public void setSample(SampleBO sample) {
        this.sample = sample;
    }

    public SampleBO getSample() {
        return this.sample;
    }
/*
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.individualNo != null ? this.individualNo.hashCode() : 0);
        hash = 41 * hash + (this.sample != null ? this.sample.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final IndividualBO other = (IndividualBO) obj;
        if (this.individualNo != other.individualNo && (this.individualNo == null || !this.individualNo.equals(other.individualNo))) {
            return false;
        }
        if (this.sample != other.sample && (this.sample == null || !this.sample.equals(other.sample))) {
            return false;
        }
        return true;
    }
*/
    public String getKey() {
        return (sample != null ? sample.getKey() + "/" : "") + (individualNo != null ? individualNo : "");
    }

    // Wrappers
    public Integer getNo() {
        return getIndividualNo();
    }

    public String getLengthUnit() {
        return getLengthType();
    }

    public String getWeightMethod() {
        return getMeasurementType();
    }

    public String getProductType() {
        return getMeasurementType();
    }
    
    public Object getStomachFillField() {
        return getStomachFill();
    }

    public AgeDeterminationBO acquireAgeDet() {
        if (ageDeterminationBOs.isEmpty()) {
            AgeDeterminationBO agedet = new AgeDeterminationBO();
            agedet.setIndividual(this);
            ageDeterminationBOs.add(agedet);
        }
        return ageDeterminationBOs.get(0);
    }

    public Integer getAge() {
        return acquireAgeDet().getAge();
    }

    public Object getSpawningAge() {
        return acquireAgeDet().getSpawningAge();
    }

    public Object getSpawningZones() {
        return acquireAgeDet().getSpawningZones();
    }

    public Object getReadability() {
        return acquireAgeDet().getReadability();
    }

    public Object getOtolithType() {
        return acquireAgeDet().getType();
    }

    public Object getOtolithEdge() {
        return acquireAgeDet().getOtolithEdge();
    }

    public Object getOtolithCentre() {
        return acquireAgeDet().getOtolithCentre();
    }

    public Object getCalibration() {
        return acquireAgeDet().getCalibration();
    }

    public String getDevelopmentalStage() {
        return developmentalStage;
    }

    public void setDevelopmentalStage(String developmentalStage) {
        this.developmentalStage = developmentalStage;
    }

}
