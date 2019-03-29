package no.imr.sea2data.biotic.bo;

import BioticTypes.v3.AgedeterminationType;
import BioticTypes.v3.IndividualType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class IndividualBO extends BaseBO implements Serializable {

    private List<AgeDeterminationBO> ageDeterminationBOs = new ArrayList<>();
    Double lengthCM;
    Double individualWeightG;
    
    public IndividualBO(CatchSampleBO sampleF, IndividualType i) {
        super(sampleF, i);
    }

    public IndividualType bo() {
        return (IndividualType) bo;
    }

    public IndividualBO(CatchSampleBO sampleF, IndividualBO bo) {
        this(sampleF, bo.bo());
    }

    public CatchSampleBO getCatchSample() {
        return (CatchSampleBO) getParent();
    }

    public Double getLengthCM() {
        return lengthCM;
    }

    public void setLengthCM(Double lengthCM) {
        this.lengthCM = lengthCM;
    }

    public Double getIndividualWeightG() {
        return individualWeightG;
    }

    public void setIndividualWeightG(Double individualWeightG) {
        this.individualWeightG = individualWeightG;
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

    public List<AgeDeterminationBO> getAgeDeterminationBOs() {
        return ageDeterminationBOs;
    }

    public AgeDeterminationBO addAgeDetermination() {
        return addAgeDetermination((AgedeterminationType) null);
    }

    public AgeDeterminationBO addAgeDetermination(AgedeterminationType aa) {
        if (aa == null) {
            aa = new AgedeterminationType();
        }
        return addAgeDetermination(new AgeDeterminationBO(this, aa));
    }

    final public AgeDeterminationBO addAgeDetermination(AgeDeterminationBO agedet) {
        ageDeterminationBOs.add(agedet);
        return agedet;
    }

    public AgeDeterminationBO acquireAgeDet() {
        if (ageDeterminationBOs.isEmpty()) {
            return addAgeDetermination();
        }
        return ageDeterminationBOs.get(0);
    }

    @Override
    public String getInternalKey() {
        return bo().getSpecimenid() != null ? bo().getSpecimenid() + "" : "";
    }

}
