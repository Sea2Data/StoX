package no.imr.sea2data.biotic.bo;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author oddrune
 */
public class PreyBO implements Serializable {

    private Integer original;
    private Date last_edited;
    protected String taxa;
    private Integer partNo;
    private Integer totalCount;
    private Double totalWeight;
    protected int taxaTypeCode; // redundant
    private String description;
    private SampleBO sample;

    public PreyBO() {
    }

    PreyBO(SampleBO sample, PreyBO bo) {
        taxa = bo.getTaxa();
        partNo = bo.getPartNo();
        totalCount = bo.getTotalCount();
        totalWeight = bo.getTotalWeight();
        this.sample = sample;
    }

    public String getTaxa() {
        return taxa;
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

    public String description() {
        return this.description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the partNo
     */
    public Integer getPartNo() {
        return partNo;
    }

    /**
     * @param partNo the partNo to set
     */
    public void setPartNo(Integer partNo) {
        this.partNo = partNo;
    }

    /**
     * @return the totalCount
     */
    public Integer getTotalCount() {
        return totalCount;
    }

    /**
     * @param totalCount the totalCount to set
     */
    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    /**
     * @return the totalWeight
     */
    public Double getTotalWeight() {
        return totalWeight;
    }

    /**
     * @param totalWeight the totalWeight to set
     */
    public void setTotalWeight(Double totalWeight) {
        this.totalWeight = totalWeight;
    }

    /**
     * @return the original
     */
    public Integer getOriginal() {
        return original;
    }

    /**
     * @param original the original to set
     */
    public void setOriginal(Integer original) {
        this.original = original;
    }

    /**
     * @return the last_edited
     */
    public Date getLast_edited() {
        return last_edited;
    }

    /**
     * @param last_edited the last_edited to set
     */
    public void setLast_edited(Date last_edited) {
        this.last_edited = last_edited;
    }

    /**
     * Set this sample.
     *
     * @param sample
     */
    public void setSample(SampleBO sample) {
        this.sample = sample;
    }
}
