package no.imr.sea2data.biotic.bo;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author oddrune
 */
public class AgeDeterminationBO implements Serializable {

    private Integer original;
    private Date last_edited;
    protected transient Integer no; // age read number.
    private Integer age;
    private Integer spawningAge;
    private Integer spawningZones;
    protected String readability;
    protected String type;
    protected String otolithEdge;
    protected String otolithCentre;
    private Integer calibration;
    private Integer growthZone1;
    private Integer growthZone2;
    private Integer growthZone3;
    private Integer growthZone4;
    private Integer growthZone5;
    private Integer growthZone6;
    private Integer growthZone7;
    private Integer growthZone8;
    private Integer growthZone9;
    private Integer growthZonesTotal;
    private Integer coastalAnnuli;
    private Integer oceanicAnnuli;
    private Integer smoltAge;
    private Integer seaAge;
    private Double otolithLength;
    private Integer hyalineZones;
    private Double weight;
    private Double weightRightOtolith;
    private Double weightLeftOtolith;
    private Integer readDate;
    private Integer readerLevel;
    private Integer verifiedBy;
    private String image;
    // The following fields are not used.
    protected String medium;
    protected String ageMedium;
    protected String preparation;
    protected String genetics;
    protected String embedding;
    private String description;
    private IndividualBO individual;

    public AgeDeterminationBO() {
    }

    public AgeDeterminationBO(IndividualBO ind, AgeDeterminationBO bo) {
        // copy age and prey info at individual:
        no = bo.getNo();
        age = bo.getAge();
        spawningAge = bo.getSpawningAge();
        spawningZones = bo.getSpawningZones();
        readability = bo.getReadability();
        type = bo.getType();
        otolithEdge = bo.getOtolithEdge();
        otolithCentre = bo.getOtolithCentre();
        calibration = bo.getCalibration();
        growthZone1 = bo.getGrowthZone1();
        growthZone2 = bo.getGrowthZone2();
        growthZone3 = bo.getGrowthZone3();
        growthZone4 = bo.getGrowthZone4();
        growthZone5 = bo.getGrowthZone5();
        growthZone6 = bo.getGrowthZone6();
        growthZone7 = bo.getGrowthZone7();
        growthZone8 = bo.getGrowthZone8();
        growthZone9 = bo.getGrowthZone9();
        growthZonesTotal = bo.getGrowthZonesTotal();
        coastalAnnuli = bo.getCoastalAnnuli();
        oceanicAnnuli = bo.getOceanicAnnuli();
        smoltAge = bo.getSmoltAge();
        seaAge = bo.getSeaAge();
        otolithLength = bo.getOtolithLength();
        hyalineZones = bo.getHyalineZones();
        weight = bo.getWeight();
        weightRightOtolith = bo.getWeightRightOtolith();
        weightLeftOtolith = bo.getWeightLeftOtolith();
        readDate = bo.getReadDate();
        readerLevel = bo.getReaderLevel();
        verifiedBy = bo.getVerifiedBy();
        image = bo.getImage();
        individual = ind;
    }

    public void setWeightRightOtolith(Double weightRightOtolith) {
        this.weightRightOtolith = weightRightOtolith;
    }

    public void setWeightLeftOtolith(Double weightLeftOtolith) {
        this.weightLeftOtolith = weightLeftOtolith;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    public void setVerifiedBy(Integer verifiedBy) {
        this.verifiedBy = verifiedBy;
    }

    public void setSpawningZones(Integer spawningZones) {
        this.spawningZones = spawningZones;
    }

    public void setSpawningAge(Integer spawningAge) {
        this.spawningAge = spawningAge;
    }

    public void setSmoltAge(Integer smoltAge) {
        this.smoltAge = smoltAge;
    }

    public void setSeaAge(Integer seaAge) {
        this.seaAge = seaAge;
    }

    public void setReaderLevel(Integer readerLevel) {
        this.readerLevel = readerLevel;
    }

    public void setReadDate(Integer readDate) {
        this.readDate = readDate;
    }

    public void setOtolithLength(Double otolithLength) {
        this.otolithLength = otolithLength;
    }

    public void setOceanicAnnuli(Integer oceanicAnnuli) {
        this.oceanicAnnuli = oceanicAnnuli;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setHyalineZones(Integer hyalineZones) {
        this.hyalineZones = hyalineZones;
    }

    public void setGrowthZonesTotal(Integer growthZonesTotal) {
        this.growthZonesTotal = growthZonesTotal;
    }

    public void setGrowthZone9(Integer growthZone9) {
        this.growthZone9 = growthZone9;
    }

    public void setGrowthZone8(Integer growthZone8) {
        this.growthZone8 = growthZone8;
    }

    public void setGrowthZone7(Integer growthZone7) {
        this.growthZone7 = growthZone7;
    }

    public void setGrowthZone6(Integer growthZone6) {
        this.growthZone6 = growthZone6;
    }

    public void setGrowthZone5(Integer growthZone5) {
        this.growthZone5 = growthZone5;
    }

    public void setGrowthZone4(Integer growthZone4) {
        this.growthZone4 = growthZone4;
    }

    public void setGrowthZone3(Integer growthZone3) {
        this.growthZone3 = growthZone3;
    }

    public void setGrowthZone2(Integer growthZone2) {
        this.growthZone2 = growthZone2;
    }

    public void setGrowthZone1(Integer growthZone1) {
        this.growthZone1 = growthZone1;
    }

    public void setCoastalAnnuli(Integer coastalAnnuli) {
        this.coastalAnnuli = coastalAnnuli;
    }

    public void setCalibration(Integer calibration) {
        this.calibration = calibration;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Double getWeightRightOtolith() {
        return weightRightOtolith;
    }

    public Double getWeightLeftOtolith() {
        return weightLeftOtolith;
    }

    public Double getWeight() {
        return weight;
    }

    public Integer getVerifiedBy() {
        return verifiedBy;
    }

    public Integer getSpawningZones() {
        return spawningZones;
    }

    public Integer getSpawningAge() {
        return spawningAge;
    }

    public Integer getSmoltAge() {
        return smoltAge;
    }

    public Integer getSeaAge() {
        return seaAge;
    }

    public Integer getReaderLevel() {
        return readerLevel;
    }

    public Integer getReadDate() {
        return readDate;
    }

    public Double getOtolithLength() {
        return otolithLength;
    }

    public Integer getOceanicAnnuli() {
        return oceanicAnnuli;
    }

    public String getImage() {
        return image;
    }

    public Integer getHyalineZones() {
        return hyalineZones;
    }

    public Integer getGrowthZonesTotal() {
        return growthZonesTotal;
    }

    public Integer getGrowthZone9() {
        return growthZone9;
    }

    public Integer getGrowthZone8() {
        return growthZone8;
    }

    public Integer getGrowthZone7() {
        return growthZone7;
    }

    public Integer getGrowthZone6() {
        return growthZone6;
    }

    public Integer getGrowthZone5() {
        return growthZone5;
    }

    public Integer getGrowthZone4() {
        return growthZone4;
    }

    public Integer getGrowthZone3() {
        return growthZone3;
    }

    public Integer getGrowthZone2() {
        return growthZone2;
    }

    public Integer getGrowthZone1() {
        return growthZone1;
    }

    public Integer getCoastalAnnuli() {
        return coastalAnnuli;
    }

    public Integer getCalibration() {
        return calibration;
    }

    public Integer getAge() {
        return age;
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

    public String getAgeMedium() {
        return ageMedium;
    }

    public void setAgeMedium(String ageMedium) {
        this.ageMedium = ageMedium;
    }

    public String getEmbedding() {
        return embedding;
    }

    public void setEmbedding(String embedding) {
        this.embedding = embedding;
    }

    public String getGenetics() {
        return genetics;
    }

    public void setGenetics(String genetics) {
        this.genetics = genetics;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getOtolithCentre() {
        return otolithCentre;
    }

    public void setOtolithCentre(String otolithCentre) {
        this.otolithCentre = otolithCentre;
    }

    public String getOtolithEdge() {
        return otolithEdge;
    }

    public void setOtolithEdge(String otolithEdge) {
        this.otolithEdge = otolithEdge;
    }

    public String getPreparation() {
        return preparation;
    }

    public void setPreparation(String preparation) {
        this.preparation = preparation;
    }

    public String getReadability() {
        return readability;
    }

    public void setReadability(String readability) {
        this.readability = readability;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "AgeDeterminationBO{" + "otolithEdge=" + otolithEdge + ", type=" + type + ", otolithCentre=" + otolithCentre + ", medium=" + medium + ", ageMedium=" + ageMedium + ", preparation=" + preparation + ", genetics=" + genetics + ", embedding=" + embedding + ", readability=" + readability + '}';
    }

    public String description() {
        return this.description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public void setNo(Integer no) {
        this.no = no;
    }

    public Integer getNo() {
        return no;
    }

    public void setIndividual(IndividualBO individual) {
        this.individual = individual;
    }
}
