package no.imr.sea2data.biotic.bo;

import BioticTypes.v3.CatchsampleType;
import BioticTypes.v3.IndividualType;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author oddrune
 */
public class CatchSampleBO {

    CatchsampleType cs;

    private FishstationBO fishstationBO;
    final private List<IndividualBO> individualBOs = new ArrayList<>();
    private String specCat;

    private CatchSampleBO(FishstationBO fishstationBO) {
        this.fishstationBO = fishstationBO;
    }

    public CatchSampleBO(FishstationBO fsBO, CatchsampleType cs) {
        this(fsBO);
        this.cs = cs;
    }

    public CatchSampleBO(FishstationBO fsBO, CatchSampleBO bo) {
        this(fsBO, bo.getCs());
        specCat = bo.getSpecCat();
    }

    public CatchsampleType getCs() {
        return cs;
    }

    public List<IndividualBO> getIndividualBOs() {
        return individualBOs;
    }

    public IndividualBO addIndividual(IndividualType i) {
        if(i == null) {
            i = new IndividualType();
            //i.setParent(getCs());
        }
        IndividualBO bo = new IndividualBO(this, i);
        getIndividualBOs().add(bo);
        return bo;
    }

    public String getKey() {
        return fishstationBO.getKey() + "/" + getSpeciesKey() + "/" + cs.getCatchpartnumber();
    }

    @Override
    public String toString() {
        return getKey();
    }

    public String getSpeciesTableKey() {
        return cs.getCommonname() != null && !cs.getCommonname().isEmpty() ? cs.getCommonname() : cs.getCatchcategory();
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

    public FishstationBO getStationBO() {
        return fishstationBO;
    }

    public String getSpeciesKey() {
        return (cs.getCommonname() != null && !cs.getCommonname().isEmpty() ? cs.getCommonname() : cs.getCatchcategory());
    }

}
