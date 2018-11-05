package no.imr.sea2data.biotic.bo;

import BioticTypes.v3.AgedeterminationType;
import java.io.Serializable;


public class AgeDeterminationBO extends BaseBO implements Serializable {

    public AgeDeterminationBO(IndividualBO ind, AgedeterminationType agedet) {
        super(ind, agedet);
    }
    
    public AgeDeterminationBO(IndividualBO ind, AgeDeterminationBO bo) {
        this(ind, bo.bo());
    }

    public AgedeterminationType bo() {
        return (AgedeterminationType) bo;
    }
    public IndividualBO getIndividual() {
        return (IndividualBO)getParent();
    }

    @Override
    public String toString() {
        return bo().getAgedeterminationid() + "";
    }
}
