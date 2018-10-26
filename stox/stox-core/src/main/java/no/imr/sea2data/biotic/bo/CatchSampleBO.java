package no.imr.sea2data.biotic.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author oddrune
 */
public class CatchSampleBO {

    private String catchcategory; // tsn number as natural key. Use external reference system for lookup by tsn key.
    private String commonname; // tsn number as natural key. Use external reference system for lookup by tsn key.
    private String aphia; // tsn number as natural key. Use external reference system for lookup by tsn key.

    private String agingstructure;
    private Integer catchpartnumber;
    protected String sampletype;
    protected String group;
    protected String conservation;
    protected String catchproducttype;
    private Double catchweight;
    private Double catchvolume;
    private Integer catchcount;
    protected String sampleproducttype;
    protected String lengthmeasurement;
    private Double lengthsampleweight;
    private Double lengthsamplevolume;
    private Integer lengthsamplecount;
    private Integer specimensamplecount;
    private Double raisingfactor;
    protected String parasite;
    protected String stomach;
    protected String tissuesample;
    protected String foreignobject;
    private String catchcomment;
    private FishstationBO fishstationBO;
    final private List<IndividualBO> individualBOs = new ArrayList<>();
    private String specCat;
    private String stock;

    public CatchSampleBO(FishstationBO fishstationBO) {
        this.fishstationBO = fishstationBO;
    }

    public CatchSampleBO(FishstationBO fsBO, CatchSampleBO bo) {
        this(fsBO);
        catchcategory = bo.getCatchcategory();
        commonname = bo.getCommonname();
        aphia = bo.getAphia();
        catchpartnumber = bo.getCatchpartnumber();
        sampletype = bo.getSampletype();
        group = bo.getGroup();
        conservation = bo.getConservation();
        catchproducttype = bo.getCatchproducttype();
        catchweight = bo.getCatchweight();
        catchvolume = bo.getCatchvolume();
        catchcount = bo.getCatchcount();
        sampleproducttype = bo.getSampleproducttype();
        lengthmeasurement = bo.getLengthmeasurement();
        lengthsampleweight = bo.getlengthsampleweight();
        lengthsamplevolume = bo.getLengthsamplevolume();
        lengthsamplecount = bo.getLengthsamplecount();
        specimensamplecount = bo.getSpecimensamplecount();
        raisingfactor = bo.getRaisingfactor();
        agingstructure = bo.getAgingstructure();
        parasite = bo.getParasite();
        stomach = bo.getStomach();
        tissuesample = bo.getGenetics();
        foreignobject = bo.getForeignobject();
        catchcomment = bo.getCatchcomment();
    }

    public String getCatchcategory() {
        return catchcategory;
    }

    public void setCatchcategory(String catchcategory) {
        this.catchcategory = catchcategory;
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

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getSampleproducttype() {
        return sampleproducttype;
    }

    public void setSampleproducttype(String sampleproducttype) {
        this.sampleproducttype = sampleproducttype;
    }

    public String getCatchproducttype() {
        return catchproducttype;
    }

    public void setCatchproducttype(String catchproducttype) {
        this.catchproducttype = catchproducttype;
    }

    public String getForeignobject() {
        return foreignobject;
    }

    public void setForeignobject(String foreignobject) {
        this.foreignobject = foreignobject;
    }

    public String getParasite() {
        return parasite;
    }

    public void setParasite(String parasite) {
        this.parasite = parasite;
    }

    public String getSampletype() {
        return sampletype;
    }

    public void setSampletype(String sampletype) {
        this.sampletype = sampletype;
    }

    public String getStomach() {
        return stomach;
    }

    public void setStomach(String stomach) {
        this.stomach = stomach;
    }

    public void setCatchweight(Double catchweight) {
        this.catchweight = catchweight;
    }

    public void setCatchvolume(Double catchvolume) {
        this.catchvolume = catchvolume;
    }

    public void setLengthsamplevolume(Double lengthsamplevolume) {
        this.lengthsamplevolume = lengthsamplevolume;
    }

    public void setCatchpartnumber(Integer catchpartnumber) {
        this.catchpartnumber = catchpartnumber;
    }

    public void setRaisingfactor(Double raisingfactor) {
        this.raisingfactor = raisingfactor;
    }

    public Double getCatchweight() {
        return catchweight;
    }

    public Double getCatchvolume() {
        return catchvolume;
    }

    public Integer getCatchcount() {
        return catchcount;
    }

    public Double getLengthsamplevolume() {
        return lengthsamplevolume;
    }

    public Integer getCatchpartnumber() {
        return catchpartnumber;
    }

    public Double getRaisingfactor() {
        return raisingfactor;
    }

    public Integer getSpecimensamplecount() {
        return specimensamplecount;
    }

    public void setCatchcomment(String catchcomment) {
        this.catchcomment = catchcomment;
    }

    public String getCatchcomment() {
        return catchcomment;
    }

    public String getGenetics() {
        return tissuesample;
    }

    public void setTissuesample(String genetics) {
        this.tissuesample = genetics;
    }

    public String getConservation() {
        return conservation;
    }

    public void setConservation(String conservation) {
        this.conservation = conservation;
    }

    public String getLengthmeasurement() {
        return lengthmeasurement;
    }

    public void setLengthmeasurement(String lengthmeasurement) {
        this.lengthmeasurement = lengthmeasurement;
    }

    public Double getlengthsampleweight() {
        return lengthsampleweight;
    }

    public void setLengthsampleweight(Double lengthsampleweight) {
        this.lengthsampleweight = lengthsampleweight;
    }

    public Integer getSpecimentsamplecount() {
        return specimensamplecount;
    }

    public void setSpecimensamplecount(Integer specimensamplecount) {
        this.specimensamplecount = specimensamplecount;
    }

    public void setCatchcount(Integer catchcount) {
        this.catchcount = catchcount;
    }

    public Integer getLengthsamplecount() {
        return lengthsamplecount;
    }

    public void setLengthsamplecount(Integer lengthsamplecount) {
        this.lengthsamplecount = lengthsamplecount;
    }

    public String getAgingstructure() {
        return agingstructure;
    }

    public void setAgingstructure(String agingstructure) {
        this.agingstructure = agingstructure;
    }

    public List<IndividualBO> getIndividualBOs() {
        return individualBOs;
    }

    public IndividualBO addIndividual() {
        IndividualBO bo = new IndividualBO(this);
        getIndividualBOs().add(bo);
        return bo;
    }

    public String getKey() {
        return fishstationBO.getKey() + "/" + getSpeciesKey() + "/" + catchpartnumber;
    }

    @Override
    public String toString() {
        return getKey();
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

    public FishstationBO getStationBO() {
        return this.fishstationBO;
    }

    public String getSpeciesKey() {
        return (commonname != null && !commonname.isEmpty() ? commonname : (catchcategory + (stock != null ? "." + stock : "")));
    }

}
