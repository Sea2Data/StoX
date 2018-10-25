package no.imr.sea2data.biotic.bo;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author oddrune
 */
public class CatchBO {

    private String catchcategory; // tsn number as natural key. Use external reference system for lookup by tsn key.
    private String commonname; // tsn number as natural key. Use external reference system for lookup by tsn key.
    private String aphia; // tsn number as natural key. Use external reference system for lookup by tsn key.
    private String specCat;
    private String stock;

    // The following fields are either misplaced or redundant and should not be taken into use before properly discussed:
    final private List<SampleBO> sampleBOs = new ArrayList<>();
    private FishstationBO fishStation;

    public CatchBO(FishstationBO fishStation) {
        this.fishStation = fishStation;
    }

    CatchBO(FishstationBO fsF, String catchcategory) {
        this(fsF);
        this.catchcategory = catchcategory;
        String[] s = catchcategory.split(".");
        if (s.length > 1) {
            this.catchcategory = s[0];
            stock = s[1];
        }
    }

    public CatchBO(FishstationBO fsF, CatchBO cb) {
        this(fsF, cb.getCatchcategory());
        commonname = cb.getCommonname();
        aphia = cb.getAphia();
    }

    public List<SampleBO> getSampleBOs() {
        return sampleBOs;
    }

    public String getCatchcategory() {
        return catchcategory;
    }

    public String getSpeciesTableKey() {
        return commonname != null && !commonname.isEmpty() ? commonname : catchcategory;
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

    public void setCatchcategory(String catchcategory) {
        this.catchcategory = catchcategory;
    }

    @Override
    public String toString() {
        return getKey();
    }

    public FishstationBO getStationBO() {
        return this.fishStation;
    }

    public String getSpeciesKey() {
        return (commonname != null && !commonname.isEmpty() ? commonname : (catchcategory + (stock != null ? "." + stock : "")));
    }

    public String getKey() {
        return fishStation.getKey() + "/" + getSpeciesKey();
    }

    public String getCommonname() {
        return commonname;
    }

    public void setCommonname(String noname) {
        this.commonname = noname;
    }

    public String getAphia() {
        return aphia;
    }

    public void setAphia(String aphia) {
        this.aphia = aphia;
    }

    public SampleBO addSample() {
        SampleBO bo = new SampleBO(this);
        getSampleBOs().add(bo);
        return bo;
    }
}
