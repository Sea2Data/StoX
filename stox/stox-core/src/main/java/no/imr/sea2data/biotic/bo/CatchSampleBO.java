package no.imr.sea2data.biotic.bo;

import BioticTypes.v3.CatchsampleType;
import BioticTypes.v3.IndividualType;
import java.util.ArrayList;
import java.util.List;


public class CatchSampleBO extends BaseBO {

    final private List<IndividualBO> individualBOs = new ArrayList<>();
    private String specCat;

    public CatchSampleBO(FishstationBO fsBO, CatchsampleType cs) {
        super(fsBO, cs);
    }

    public CatchSampleBO(FishstationBO fsBO, CatchSampleBO bo) {
        this(fsBO, bo.bo());
        specCat = bo.getSpecCat();
    }

    public CatchsampleType bo() {
        return (CatchsampleType) bo;
    }

    public List<IndividualBO> getIndividualBOs() {
        return individualBOs;
    }

    public IndividualBO addIndividual(IndividualType i) {
        if(i == null) {
            i = new IndividualType();
            //i.setParent(bo());
        }
        IndividualBO bo = new IndividualBO(this, i);
        getIndividualBOs().add(bo);
        return bo;
    }

    public String getKey() {
        return getFishstation().getKey() + "/" + getSpeciesKey() + "/" + bo().getCatchpartnumber();
    }

    @Override
    public String toString() {
        return getKey();
    }

    public String getSpeciesTableKey() {
        return bo().getCommonname() != null && !bo().getCommonname().isEmpty() ? bo().getCommonname() : bo().getCatchcategory();
    }

    /**
     * return species category, used as key in matrix stock estimations. default
     * = species, but can be explicitely defined if missing. I.e. SILD =
     * SILDG03+SILDG07 or SARDINELLA=SARDA+SARDM
     *
     * @return
     */
    public String getSpeciesCatTableKey() {
        return specCat != null ? specCat : getSpeciesTableKey();
    }

    public String getSpecCat() {
        return specCat;
    }

    public void setSpecCat(String specCat) {
        this.specCat = specCat;
    }

    public FishstationBO getFishstation() {
        return (FishstationBO)getParent();
    }

    public String getSpeciesKey() {
        return bo().getCommonname() != null && !bo().getCommonname().isEmpty() ? bo().getCommonname() : bo().getCatchcategory();
    }

}
