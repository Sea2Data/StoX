package no.imr.sea2data.biotic.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author oddrune
 */
public class CatchBO {

    protected String taxa; // tsn number as natural key. Use external reference system for lookup by tsn key.
    protected String noname; // tsn number as natural key. Use external reference system for lookup by tsn key.
    protected String aphia; // tsn number as natural key. Use external reference system for lookup by tsn key.
    protected transient String stock; // stock code (G03, G05 etc.)
    String specCat;

    // The following fields are either misplaced or redundant and should not be taken into use before properly discussed:
    protected Integer measurementType; // misplaced, belongs to sample
    protected int taxaTypeCode;        // not important (old overpunch code)
    private String fishingArea;        // misplaced, belongs to fishstation
    private Boolean targetSpecies;     // not in use
    private String norwegianname;      // redundant reference info
    private String scientificname;     // redundant reference info
    private String englishname;        // redundant reference info

    private List<no.imr.sea2data.biotic.bo.SampleBO> sampleBOs = new ArrayList<>();
    private FishstationBO stationBO;

    public CatchBO() {
    }

    public CatchBO(FishstationBO fsF, CatchBO cb) {
        taxa = cb.getTaxa();
        noname = cb.getNoname();
        aphia = cb.getAphia();
        stock = cb.getStock();
        stationBO = fsF;
    }

    public String getStock() {
        return stock;
    }

    CatchBO(String species) {
        this();
        taxa = species;
        String[] s = species.split(".");
        if (s.length > 1) {
            taxa = s[0];
            stock = s[1];
        }
    }

    public List<SampleBO> getSampleBOCollection() {
        return sampleBOs;
    }

    public void setSampleBOCollection(List<SampleBO> sampleBOs) {
        this.sampleBOs = sampleBOs;
    }

    public String getTaxa() {
        return taxa;
    }

    public String getSpeciesTableKey() {
        return noname != null && !noname.isEmpty() ? noname : taxa;
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

    public void setTaxa(String taxa) {
        this.taxa = taxa;
    }

    public int getTaxaTypeCode() {
        return taxaTypeCode;
    }

    public void setTaxaTypeCode(int taxaTypeCode) {
        this.taxaTypeCode = taxaTypeCode;
    }

    public Integer getMeasurementType() {
        return measurementType;
    }

    public void setMeasurementType(Integer measurementType) {
        this.measurementType = measurementType;
    }

    public void setTargetSpecies(Boolean targetSpecies) {
        this.targetSpecies = targetSpecies;
    }

    public void setFishingArea(String fishingArea) {
        this.fishingArea = fishingArea;
    }

    public Boolean getTargetSpecies() {
        return targetSpecies;
    }

    public String getFishingArea() {
        return fishingArea;
    }

    @Override
    public String toString() {
        return getKey();
    }

    /**
     * @return the norwegianname
     */
    public String getNorwegianname() {
        return norwegianname;
    }

    /**
     * @param norwegianname the norwegianname to set
     */
    public void setNorwegianname(String norwegianname) {
        this.norwegianname = norwegianname;
    }

    /**
     * @return the scientificname
     */
    public String getScientificname() {
        return scientificname;
    }

    /**
     * @param scientificname the scientificname to set
     */
    public void setScientificname(String scientificname) {
        this.scientificname = scientificname;
    }

    /**
     * @return the englishname
     */
    public String getEnglishname() {
        return englishname;
    }

    /**
     * @param englishname the englishname to set
     */
    public void setEnglishname(String englishname) {
        this.englishname = englishname;
    }

    /**
     * Set this station.
     *
     * @param station
     */
    public void setStationBO(FishstationBO station) {
        this.stationBO = station;
    }

    public FishstationBO getStationBO() {
        return this.stationBO;
    }

/*    @Override
    public int hashCode() {
        int hash = 3;
        hash = 73 * hash + (this.taxa != null ? this.taxa.hashCode() : 0);
        hash = 73 * hash + (this.stationBO != null ? this.stationBO.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CatchBO other = (CatchBO) obj;
        if ((this.taxa == null) ? (other.taxa != null) : !this.taxa.equals(other.taxa)) {
            return false;
        }
        if (this.stationBO != other.stationBO && (this.stationBO == null || !this.stationBO.equals(other.stationBO))) {
            return false;
        }
        return true;
    }
*/
    public String getSpeciesKey() {
        return (noname != null && !noname.isEmpty() ? noname : (taxa + (stock != null ? "." + stock : "")));
    }

    public String getKey() {
        return stationBO.getKey() + "/" + getSpeciesKey();
    }

    // Wrappers:
    public String getSpecies() {
        return getTaxa();
    }

    public void setSpecies(String species) {
        setTaxa(species);
    }

    public String getNoname() {
        return noname;
    }

    public void setNoname(String noname) {
        this.noname = noname;
    }

    public String getAphia() {
        return aphia;
    }

    public void setAphia(String aphia) {
        this.aphia = aphia;
    }
}
