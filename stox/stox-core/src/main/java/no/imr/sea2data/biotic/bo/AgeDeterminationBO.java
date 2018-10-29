package no.imr.sea2data.biotic.bo;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author oddrune
 */
public class AgeDeterminationBO implements Serializable {

    private Integer agedeterminationid; // age read number.
    private Integer age;
    private Integer spawningage;
    private Integer spawningzones;
    private String readability;
    private String otolithtype;
    private String otolithedge;
    private String otolitcentre;
    private Integer calibration;
    private Integer growthzone1;
    private Integer growthzone2;
    private Integer growthzone3;
    private Integer growthzone4;
    private Integer growthzone5;
    private Integer growthzone6;
    private Integer growthzone7;
    private Integer growthzone8;
    private Integer growthzone9;
    private Integer growthzonestotal;
    private Integer coastalannuli;
    private Integer oceanicannuli;

    private IndividualBO individual;

    public AgeDeterminationBO(IndividualBO ind) {
        this.individual = ind;
    }

    public AgeDeterminationBO(IndividualBO ind, AgeDeterminationBO bo) {
        this(ind);
        // copy age and prey info at individual:
        agedeterminationid = bo.getagedeterminationid();
        age = bo.getAge();
        spawningage = bo.getSpawningage();
        spawningzones = bo.getSpawningzones();
        readability = bo.getReadability();
        otolithtype = bo.getOtolithtype();
        otolithedge = bo.getOtolithedge();
        otolitcentre = bo.getOtolithcentre();
        calibration = bo.getCalibration();
        growthzone1 = bo.getGrowthzone1();
        growthzone2 = bo.getGrowthzone2();
        growthzone3 = bo.getGrowthzone3();
        growthzone4 = bo.getGrowthzone4();
        growthzone5 = bo.getGrowthzone5();
        growthzone6 = bo.getGrowthzone6();
        growthzone7 = bo.getGrowthzone7();
        growthzone8 = bo.getGrowthzone8();
        growthzone9 = bo.getGrowthzone9();
        growthzonestotal = bo.getGrowthzonestotal();
        coastalannuli = bo.getCoastalannuli();
        oceanicannuli = bo.getOceanicannuli();
    }

    public void setSpawningzones(Integer spawningzones) {
        this.spawningzones = spawningzones;
    }

    public void setSpawningage(Integer spawningage) {
        this.spawningage = spawningage;
    }

    public void setOceanicannuli(Integer oceanicannuli) {
        this.oceanicannuli = oceanicannuli;
    }

    public void setGrowthzonestotal(Integer growthzonestotal) {
        this.growthzonestotal = growthzonestotal;
    }

    public void setGrowthzone9(Integer growthzone9) {
        this.growthzone9 = growthzone9;
    }

    public void setGrowthzone8(Integer growthzone8) {
        this.growthzone8 = growthzone8;
    }

    public void setGrowthzone7(Integer growthzone7) {
        this.growthzone7 = growthzone7;
    }

    public void setGrowthzone6(Integer growthzone6) {
        this.growthzone6 = growthzone6;
    }

    public void setGrowthzone5(Integer growthzone5) {
        this.growthzone5 = growthzone5;
    }

    public void setGrowthzone4(Integer growthzone4) {
        this.growthzone4 = growthzone4;
    }

    public void setGrowthzone3(Integer growthzone3) {
        this.growthzone3 = growthzone3;
    }

    public void setGrowthzone2(Integer growthzone2) {
        this.growthzone2 = growthzone2;
    }

    public void setGrowthzone1(Integer growthzone1) {
        this.growthzone1 = growthzone1;
    }

    public void setCoastalannuli(Integer coastalannuli) {
        this.coastalannuli = coastalannuli;
    }

    public void setCalibration(Integer calibration) {
        this.calibration = calibration;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getSpawningzones() {
        return spawningzones;
    }

    public Integer getSpawningage() {
        return spawningage;
    }

    public Integer getOceanicannuli() {
        return oceanicannuli;
    }

    public Integer getGrowthzonestotal() {
        return growthzonestotal;
    }

    public Integer getGrowthzone9() {
        return growthzone9;
    }

    public Integer getGrowthzone8() {
        return growthzone8;
    }

    public Integer getGrowthzone7() {
        return growthzone7;
    }

    public Integer getGrowthzone6() {
        return growthzone6;
    }

    public Integer getGrowthzone5() {
        return growthzone5;
    }

    public Integer getGrowthzone4() {
        return growthzone4;
    }

    public Integer getGrowthzone3() {
        return growthzone3;
    }

    public Integer getGrowthzone2() {
        return growthzone2;
    }

    public Integer getGrowthzone1() {
        return growthzone1;
    }

    public Integer getCoastalannuli() {
        return coastalannuli;
    }

    public Integer getCalibration() {
        return calibration;
    }

    public Integer getAge() {
        return age;
    }

    public String getOtolithcentre() {
        return otolitcentre;
    }

    public void setOtolithcentre(String otolithcentre) {
        this.otolitcentre = otolithcentre;
    }

    public String getOtolithedge() {
        return otolithedge;
    }

    public void setOtolithedge(String otolithedge) {
        this.otolithedge = otolithedge;
    }

    public String getReadability() {
        return readability;
    }

    public void setReadability(String readability) {
        this.readability = readability;
    }

    public String getOtolithtype() {
        return otolithtype;
    }

    public void setOtolithtype(String otolithtype) {
        this.otolithtype = otolithtype;
    }


    public void setAgedeterminationid(Integer agedeterminationid) {
        this.agedeterminationid = agedeterminationid;
    }

    public Integer getagedeterminationid() {
        return agedeterminationid;
    }

    public IndividualBO getIndividual() {
        return individual;
    }

    @Override
    public String toString() {
        return getagedeterminationid() + "";
    }
}
