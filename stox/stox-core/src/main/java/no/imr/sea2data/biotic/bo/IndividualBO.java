package no.imr.sea2data.biotic.bo;

import BioticTypes.v3.AgedeterminationType;
import BioticTypes.v3.IndividualType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import no.imr.sea2data.imrbase.math.Calc;
import no.imr.stox.functions.utils.StoXMath;

/**
 *
 * @author oddrune
 */
public class IndividualBO implements Serializable {

    private IndividualType ii;

    private CatchSampleBO catchsample;
    private List<AgeDeterminationBO> ageDeterminationBOs = new ArrayList<>();
    private Double individualweightG;
    private Double lengthCM;

    
    private IndividualBO(CatchSampleBO sampleF) {
        this.catchsample = sampleF;
    }

    public IndividualBO(CatchSampleBO sampleF, IndividualType i) {
        this(sampleF);
        this.ii = i;
        recache();
    }

    public IndividualType getI() {
        return ii;
    }

    public IndividualBO(CatchSampleBO sampleF, IndividualBO bo) {
        this(sampleF, bo.getI());
        for (AgeDeterminationBO aBO : bo.getAgeDeterminationBOs()) {
            AgeDeterminationBO agBO = new AgeDeterminationBO(this, aBO);
            ageDeterminationBOs.add(agBO);
        }
    }
    
    private void recache() {
        this.lengthCM = Calc.roundTo(StoXMath.mToCM(ii.getLength()), 8);
        this.individualweightG = Calc.roundTo(StoXMath.kgToGrams(ii.getIndividualweight()), 8);
    }

    public void setLength(Double length) {
        ii.setLength(length);
        recache();
    }

    public void setIndividualweight(Double individualweight) {
        ii.setIndividualweight(individualweight);
        recache();
    }

    public Double getIndividualweightG() {
        return individualweightG;
    }

    public Double getLengthCM() {
        return lengthCM;
    }

    public CatchSampleBO getCatchSample() {
        return this.catchsample;
    }

    public Integer getAge() {
        return acquireAgeDet().getAgedet().getAge();
    }

    public Object getSpawningage() {
        return acquireAgeDet().getAgedet().getSpawningage();
    }

    public Object getSpawningzones() {
        return acquireAgeDet().getAgedet().getSpawningzones();
    }

    public Object getReadability() {
        return acquireAgeDet().getAgedet().getReadability();
    }

    public Object getOtolithtype() {
        return acquireAgeDet().getAgedet().getOtolithtype();
    }

    public Object getOtolithedge() {
        return acquireAgeDet().getAgedet().getOtolithedge();
    }

    public Object getOtolithcentre() {
        return acquireAgeDet().getAgedet().getOtolithcentre();
    }

    public Object getCalibration() {
        return acquireAgeDet().getAgedet().getCalibration();
    }

    @Override
    public String toString() {
        return getKey();
    }

    public List<AgeDeterminationBO> getAgeDeterminationBOs() {
        return ageDeterminationBOs;
    }

    public AgeDeterminationBO addAgeDetermination(AgedeterminationType aa) {
        if(aa == null) {
            aa = new AgedeterminationType();
//            aa.setParent(ii);
        }
        AgeDeterminationBO agedet = new AgeDeterminationBO(this, aa);
        ageDeterminationBOs.add(agedet);
        return agedet;
    }

    public AgeDeterminationBO acquireAgeDet() {
        if (ageDeterminationBOs.isEmpty()) {
            return addAgeDetermination(null);
        }
        return ageDeterminationBOs.get(0);
    }

    public String getKey() {
        return (catchsample != null ? catchsample.getKey() + "/" : "") + (ii.getSpecimenid() != null ? ii.getSpecimenid() : "");
    }

}
