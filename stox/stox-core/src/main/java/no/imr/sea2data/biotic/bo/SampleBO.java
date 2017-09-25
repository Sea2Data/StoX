package no.imr.sea2data.biotic.bo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 *
 * @author oddrune
 */
public class SampleBO implements Serializable {

    private Integer original;
    private Date last_edited;
    protected String stock; // misplaced field?, Belongs to taxa, transient now at catchBO
    private Integer sampleNo;
    protected Integer sampletype;
    protected String group;
    protected String conservationtype;
    protected String measurementTypeTotal;
    private Double totalWeight;
    private Double totalVolume;
    private Integer totalCount;
    protected String measurementTypeSampled;
    protected String lengthType;
    private Double sampledWeight;
    private Double sampledVolume;
    private Integer sampledCount;
    private Integer inumber;
    private Double raisingFactor;
    protected String frozenSample;
    protected String parasite;
    protected String stomach;
    protected String genetics;
    protected String nonBiological;
    private List<IndividualBO> individualBOs = new ArrayList<>();
    protected String person; // not in use
    private String comment;
    private String description;
    private CatchBO catchBO;

    public SampleBO() {
    }

    public SampleBO(CatchBO cbF, SampleBO bo) {
        sampleNo = bo.getSampleNo();
        sampletype = bo.getSampletype();
        group = bo.getGroup();
        conservationtype = bo.getConservationtype();
        measurementTypeTotal = bo.getMeasurementTypeTotal();
        totalWeight = bo.getTotalWeight();
        totalVolume = bo.getTotalVolume();
        totalCount = bo.getTotalCount();
        measurementTypeSampled = bo.getMeasurementTypeSampled();
        lengthType = bo.getLengthType();
        sampledWeight = bo.getSampledWeight();
        sampledVolume = bo.getSampledVolume();
        sampledCount = bo.getSampledCount();
        inumber = bo.getInumber();
        raisingFactor = bo.getRaisingFactor();
        frozenSample = bo.getFrozenSample();
        parasite = bo.getParasite();
        stomach = bo.getStomach();
        genetics = bo.getGenetics();
        nonBiological = bo.getNonBiological();
        comment = bo.getComment();
        catchBO = cbF;
    }

    public final void addPrey(Integer fishno, PreyBO prey) {
        for (IndividualBO indBO : individualBOs) {
            if (indBO.getIndividualNo().equals(fishno)) {
                indBO.getPreyBOCollection().add(prey);
                break;
            }
        }

    }

    public String getConservationtype() {
        return conservationtype;
    }

    public void setConservationtype(String conservationtype) {
        this.conservationtype = conservationtype;
    }

    public String getFrozenSample() {
        return frozenSample;
    }

    public void setFrozenSample(String frozenSample) {
        this.frozenSample = frozenSample;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getLengthType() {
        return lengthType;
    }

    public void setLengthType(String lengthType) {
        this.lengthType = lengthType;
    }

    public String getMeasurementTypeSampled() {
        return measurementTypeSampled;
    }

    public void setMeasurementTypeSampled(String measurementTypeSampled) {
        this.measurementTypeSampled = measurementTypeSampled;
    }

    public String getMeasurementTypeTotal() {
        return measurementTypeTotal;
    }

    public void setMeasurementTypeTotal(String measurementTypeTotal) {
        this.measurementTypeTotal = measurementTypeTotal;
    }

    public String getNonBiological() {
        return nonBiological;
    }

    public void setNonBiological(String nonBiological) {
        this.nonBiological = nonBiological;
    }

    public String getParasite() {
        return parasite;
    }

    public void setParasite(String parasite) {
        this.parasite = parasite;
    }

    public String getPerson() {
        return person;
    }

    public void setPerson(String person) {
        this.person = person;
    }

    public Integer getSampletype() {
        return sampletype;
    }

    public void setSampletype(Integer sampletype) {
        this.sampletype = sampletype;
    }

    public String getStock() {
        return stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }

    public String getStomach() {
        return stomach;
    }

    public void setStomach(String stomach) {
        this.stomach = stomach;
    }

    public List<IndividualBO> getIndividualBOCollection() {
        return individualBOs;
    }

    public void setIndividualBOCollection(List<IndividualBO> individualBOs) {
        this.individualBOs = individualBOs;
    }

    public void setOriginal(Integer original) {
        this.original = original;
    }

    public void setLast_edited(Date last_edited) {
        this.last_edited = last_edited;
    }

    public Integer getOriginal() {
        return original;
    }

    public Date getLast_edited() {
        return last_edited;
    }

    public void setTotalWeight(Double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public void setTotalVolume(Double totalVolume) {
        this.totalVolume = totalVolume;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public void setSampledWeight(Double sampledWeight) {
        this.sampledWeight = sampledWeight;
    }

    public void setSampledVolume(Double sampledVolume) {
        this.sampledVolume = sampledVolume;
    }

    public void setSampledCount(Integer sampledCount) {
        this.sampledCount = sampledCount;
    }

    public void setSampleNo(Integer sampleNo) {
        this.sampleNo = sampleNo;
    }

    public void setRaisingFactor(Double raisingFactor) {
        this.raisingFactor = raisingFactor;
    }

    public void setInumber(Integer inumber) {
        this.inumber = inumber;
    }

    public Double getTotalWeight() {
        return totalWeight;
    }

    public Double getTotalVolume() {
        return totalVolume;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public Double getSampledWeight() {
        return sampledWeight;
    }

    public Double getSampledVolume() {
        return sampledVolume;
    }

    public Integer getSampledCount() {
        return sampledCount;
    }

    public Integer getSampleNo() {
        return sampleNo;
    }

    public Double getRaisingFactor() {
        return raisingFactor;
    }

    public Integer getInumber() {
        return inumber;
    }

    public String description() {
        return this.description;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public String toString() {
        return getKey();
    }

    /**
     * @return the genetics
     */
    public String getGenetics() {
        return genetics;
    }

    /**
     * @param genetics the genetics to set
     */
    public void setGenetics(String genetics) {
        this.genetics = genetics;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Set catchBO.
     *
     * @param cb
     */
    public void setCatchBO(CatchBO cb) {
        this.catchBO = cb;
    }

    /**
     * Return catch.
     *
     * @return
     */
    public CatchBO getCatchBO() {
        return this.catchBO;
    }

  /*  @Override
    public int hashCode() {
        int hash = 5;
        hash = 37 * hash + (this.stock != null ? this.stock.hashCode() : 0);
        hash = 37 * hash + (this.sampleNo != null ? this.sampleNo.hashCode() : 0);
        hash = 37 * hash + (this.catchBO != null ? this.catchBO.hashCode() : 0);
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
        final SampleBO other = (SampleBO) obj;
        if ((this.stock == null) ? (other.stock != null) : !this.stock.equals(other.stock)) {
            return false;
        }
        if (this.sampleNo != other.sampleNo && (this.sampleNo == null || !this.sampleNo.equals(other.sampleNo))) {
            return false;
        }
        if (this.catchBO != other.catchBO && (this.catchBO == null || !this.catchBO.equals(other.catchBO))) {
            return false;
        }
        return true;
    }
*/
    public String getKey() {
        return catchBO.getKey() + "/" + sampleNo;
    }

    // Wrappers
    public Integer getSampleNumber() {
        return getSampleNo();
    }

    public String getConservation() {
        return getConservationtype();
    }

    public void setConservation(String conservation) {
        setConservationtype(conservation);
    }

    public String getMeasurement() {
        return getMeasurementTypeTotal();
    }

    public void setMeasurement(String measurement) {
        setMeasurementTypeTotal(measurement);
    }

    public Double getWeight() {
        return getTotalWeight();
    }

    public void setWeight(Double weight) {
        setTotalWeight(weight);
    }

    public String getSampleMeasurement() {
        return getMeasurementTypeSampled();
    }

    public void setSampleMeasurement(String sampleMeasurement) {
        setMeasurementTypeSampled(sampleMeasurement);
    }

    public String getLengthMeasurement() {
        return getLengthType();
    }

    public void setLengthMeasurement(String lengthMeasurement) {
        setLengthType(lengthMeasurement);
    }

    public Double getLengthSampleWeight() {
        return getSampledWeight();
    }

    public void setLengthSampleWeight(Double lengthSampleWeight) {
        setSampledWeight(lengthSampleWeight);
    }

    public Integer getIndividualSampleCount() {
        return getInumber();
    }

    public void setIndividualSampleCount(Integer individualSampleCount) {
        setInumber(individualSampleCount);
    }

    public Integer getCount() {
        return getTotalCount();
    }

    public void setCount(Integer count) {
        setTotalCount(count);
    }

    public Integer getLengthSampleCount() {
        return getSampledCount();
    }

    public void setLengthSampleCount(Integer lengthSampleCount) {
        setSampledCount(lengthSampleCount);
    }

    public String getAgeSample() {
        return getFrozenSample();
    }

    public void setAgeSample(String ageSample) {
        setFrozenSample(ageSample);
    }
}
