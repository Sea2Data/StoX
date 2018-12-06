package no.imr.sea2data.biotic.bo;

import BioticTypes.v3.CatchsampleType;
import BioticTypes.v3.IndividualType;
import java.util.ArrayList;
import java.util.List;

public class CatchSampleBO extends BaseBO {

    final private List<IndividualBO> individualBOs = new ArrayList<>();
    private String specCat = null;

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

    public IndividualBO addIndividual() {
        return addIndividual((IndividualType) null);
    }

    public IndividualBO addIndividual(IndividualType i) {
        if (i == null) {
            i = new IndividualType();
        }
        return addIndividual(new IndividualBO(this, i));
    }

    public IndividualBO addIndividual(IndividualBO bo) {
        getIndividualBOs().add(bo);
        return bo;
    }

    @Override
    public String getInternalKey() {
        return getSpeciesKey() + "/" + bo().getCatchpartnumber();
    }

    /**
     * return species category, used as key in matrix stock estimations. default
     * = species, but can be explicitely defined if missing. I.e. SILD =
     * SILDG03+SILDG07 or SARDINELLA=SARDA+SARDM
     *
     * @return
     */
    public String getSpecCat() {
        return specCat;
    }

    public String getSpeciesKey() {
        return bo().getCommonname() != null && !bo().getCommonname().isEmpty() ? bo().getCommonname() : bo().getCatchcategory();
    }

    public void setSpecCat(String specCat) {
        this.specCat = specCat;
    }

    public FishstationBO getFishstation() {
        return (FishstationBO) getParent();
    }

}
