package no.imr.sea2data.biotic.bo;

import BioticTypes.v3.AgedeterminationType;
import BioticTypes.v3.IndividualType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import no.imr.stox.util.math.Calc;
import no.imr.stox.functions.utils.StoXMath;

public class IndividualBO extends BaseBO implements Serializable {

    private List<AgeDeterminationBO> ageDeterminationBOs = new ArrayList<>();

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

    public Double getLengthCentimeter() {
        return Calc.roundTo(StoXMath.mToCM(bo().getLength()), 8);
    }

    public void setLengthCentimeter(Double lengthCentimeter) {
        bo().setLength(lengthCentimeter != null ? lengthCentimeter * 0.01 : null);;
    }

    public void setIndividualWeightGram(Double individualWeightGram) {
        bo().setIndividualweight(individualWeightGram != null ? individualWeightGram * 0.001 : null);
    }

    public Double getIndividualWeightGram() {
        return Calc.roundTo(StoXMath.kgToGrams(bo().getIndividualweight()), 8);
    }

    public Integer getAgeDeterminationId() {
        return getAgeDet() != null ? getAgeDet().bo().getAgedeterminationid() : null;
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
        if (ageDeterminationBOs.isEmpty()) {
            return null;
        }
        if (ageDeterminationBOs.size() == 1) {
            return ageDeterminationBOs.get(0);
        } else {
            if (bo() != null) {
                Integer pref = bo().getPreferredagereading();
                if (pref == null) {
                    pref = ageDeterminationBOs.stream().mapToInt(a -> a.bo().getAgedeterminationid()).min().orElse(1);
                }
                Integer preff = pref;
                return ageDeterminationBOs.stream().filter(a -> a.bo().getAgedeterminationid() != null
                        && a.bo().getAgedeterminationid().equals(preff)).findFirst().orElse(null);
            }
        }
        return null;
    }

    @Override
    public String getInternalKey() {
        return bo().getSpecimenid() != null ? bo().getSpecimenid() + "" : "";
    }

}
