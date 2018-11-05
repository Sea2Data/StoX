package no.imr.sea2data.biotic.bo;

import BioticTypes.v3.AgedeterminationType;
import BioticTypes.v3.IndividualType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import no.imr.sea2data.imrbase.math.Calc;
import no.imr.stox.functions.utils.StoXMath;


public class IndividualBO extends BaseBO implements Serializable {


    private List<AgeDeterminationBO> ageDeterminationBOs = new ArrayList<>();
    private Double individualweightG;
    private Double lengthCM;

    
    public IndividualBO(CatchSampleBO sampleF, IndividualType i) {
        super(sampleF, i);
        recache();
    }

    public IndividualType bo() {
        return (IndividualType)bo;
    }

    public IndividualBO(CatchSampleBO sampleF, IndividualBO bo) {
        this(sampleF, bo.bo());
        for (AgeDeterminationBO aBO : bo.getAgeDeterminationBOs()) {
            AgeDeterminationBO agBO = new AgeDeterminationBO(this, aBO);
            ageDeterminationBOs.add(agBO);
        }
    }
    
    private void recache() {
        this.lengthCM = Calc.roundTo(StoXMath.mToCM(bo().getLength()), 8);
        this.individualweightG = Calc.roundTo(StoXMath.kgToGrams(bo().getIndividualweight()), 8);
    }

    public void setLength(Double length) {
        bo().setLength(length);
        recache();
    }

    public void setIndividualweight(Double individualweight) {
        bo().setIndividualweight(individualweight);
        recache();
    }

    public Double getIndividualweightG() {
        return individualweightG;
    }

    public Double getLengthCM() {
        return lengthCM;
    }

    public CatchSampleBO getCatchSample() {
        return (CatchSampleBO)getParent();
    }

    public Integer getAge() {
        return acquireAgeDet().bo().getAge();
    }

    public Object getSpawningage() {
        return acquireAgeDet().bo().getSpawningage();
    }

    public Object getSpawningzones() {
        return acquireAgeDet().bo().getSpawningzones();
    }

    public Object getReadability() {
        return acquireAgeDet().bo().getReadability();
    }

    public Object getOtolithtype() {
        return acquireAgeDet().bo().getOtolithtype();
    }

    public Object getOtolithedge() {
        return acquireAgeDet().bo().getOtolithedge();
    }

    public Object getOtolithcentre() {
        return acquireAgeDet().bo().getOtolithcentre();
    }

    public Object getCalibration() {
        return acquireAgeDet().bo().getCalibration();
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
        return (getCatchSample() != null ? getCatchSample().getKey() + "/" : "") + (bo().getSpecimenid() != null ? bo().getSpecimenid() : "");
    }

}
