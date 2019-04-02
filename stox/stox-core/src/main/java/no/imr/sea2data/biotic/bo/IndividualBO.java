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
        return getAgeDet() != null ? getAgeDet().bo().getAge() : null;
    }

    public Object getSpawningage() {
        return getAgeDet() != null ? getAgeDet().bo().getSpawningage() : null;
    }

    public Object getSpawningzones() {
        return getAgeDet() != null ? getAgeDet().bo().getSpawningzones() : null;
    }

    public Object getReadability() {
        return getAgeDet() != null ? getAgeDet().bo().getReadability() : null;
    }

    public Object getOtolithtype() {
        return getAgeDet() != null ? getAgeDet().bo().getOtolithtype() : null;
    }

    public Object getOtolithedge() {
        return getAgeDet() != null ? getAgeDet().bo().getOtolithedge() : null;
    }

    public Object getOtolithcentre() {
        return getAgeDet() != null ? getAgeDet().bo().getOtolithcentre() : null;
    }

    public Object getCalibration() {
        return getAgeDet() != null ? getAgeDet().bo().getCalibration() : null;
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

    public AgeDeterminationBO getAgeDet() {
        return ageDeterminationBOs.size() == 1 ? ageDeterminationBOs.get(0) : null;
    }

    @Override
    public String getInternalKey() {
        return bo().getSpecimenid() != null ? bo().getSpecimenid() + "" : "";
    }

}
