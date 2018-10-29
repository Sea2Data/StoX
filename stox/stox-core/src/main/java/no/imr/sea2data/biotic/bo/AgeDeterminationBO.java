package no.imr.sea2data.biotic.bo;

import BioticTypes.v3.AgedeterminationType;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author oddrune
 */
public class AgeDeterminationBO implements Serializable {

    AgedeterminationType agedet;

    private IndividualBO individual;

    private AgeDeterminationBO(IndividualBO ind) {
        this.individual = ind;
    }

    public AgeDeterminationBO(IndividualBO ind, AgedeterminationType agedet) {
        this.agedet = agedet;
    }
    
    public AgeDeterminationBO(IndividualBO ind, AgeDeterminationBO bo) {
        this(ind);
        agedet = bo.getAgedet();
    }

    public AgedeterminationType getAgedet() {
        return agedet;
    }
    public IndividualBO getIndividual() {
        return individual;
    }

    @Override
    public String toString() {
        return agedet.getAgedeterminationid() + "";
    }
}
