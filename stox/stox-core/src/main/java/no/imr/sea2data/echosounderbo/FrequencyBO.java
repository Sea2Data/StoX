package no.imr.sea2data.echosounderbo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Buisness object for Acoustic Frequencies. Usually contained within a list in
 * DistanceBO
 *
 * @author aasmunds
 */
public class FrequencyBO implements Serializable {

    private Integer freq;
    private Integer tranceiver;
    private Double threshold;
    private Integer num_pel_ch;
    private Integer num_bot_ch;
    private Double min_bot_depth;
    private Double max_bot_depth;
    private Double upper_interpret_depth;
    private Double lower_interpret_depth;
    private Double upper_integrator_depth;
    private Double lower_integrator_depth;
    private Integer quality;
    private Double bubble_corr;
    private List<SABO> sa = new ArrayList<SABO>();
    private DistanceBO distanceBO;

    /**
     * Default constructor
     */
    public FrequencyBO() {
    }

    public FrequencyBO(DistanceBO dsF, FrequencyBO fr) {
        freq = fr.getFreq();
        tranceiver = fr.getTranceiver();
        threshold = fr.getThreshold();
        num_pel_ch = fr.getNum_pel_ch();
        num_bot_ch = fr.getNum_bot_ch();
        min_bot_depth = fr.getMin_bot_depth();
        max_bot_depth = fr.getMax_bot_depth();
        upper_interpret_depth = fr.getUpper_interpret_depth();
        lower_interpret_depth = fr.getLower_interpret_depth();
        upper_integrator_depth = fr.getUpper_integrator_depth();
        lower_integrator_depth = fr.getLower_integrator_depth();
        quality = fr.getQuality();
        bubble_corr = fr.getBubble_corr();
        distanceBO = dsF;
    }

    /**
     * @return the freq
     */
    public Integer getFreq() {
        return freq;
    }

    /**
     * @param freq the freq to set
     */
    public void setFreq(Integer freq) {
        this.freq = freq;
    }

    /**
     * @return the tranceiver
     */
    public Integer getTranceiver() {
        return tranceiver;
    }

    /**
     * @param tranceiver the tranceiver to set
     */
    public void setTranceiver(Integer tranceiver) {
        this.tranceiver = tranceiver;
    }

    /**
     * @return the threshold
     */
    public Double getThreshold() {
        return threshold;
    }

    /**
     * @param threshold the threshold to set
     */
    public void setThreshold(Double threshold) {
        this.threshold = threshold;
    }

    /**
     * @return the num_pel_ch
     */
    public Integer getNum_pel_ch() {
        return num_pel_ch;
    }

    /**
     * @param num_pel_ch the num_pel_ch to set
     */
    public void setNum_pel_ch(Integer num_pel_ch) {
        this.num_pel_ch = num_pel_ch;
    }

    /**
     * @return the num_bot_ch
     */
    public Integer getNum_bot_ch() {
        return num_bot_ch;
    }

    /**
     * @param num_bot_ch the num_bot_ch to set
     */
    public void setNum_bot_ch(Integer num_bot_ch) {
        this.num_bot_ch = num_bot_ch;
    }

    /**
     * @return the min_bot_depth
     */
    public Double getMin_bot_depth() {
        return min_bot_depth;
    }

    /**
     * @param min_bot_depth the min_bot_depth to set
     */
    public void setMin_bot_depth(Double min_bot_depth) {
        this.min_bot_depth = min_bot_depth;
    }

    /**
     * @return the max_bot_depth
     */
    public Double getMax_bot_depth() {
        return max_bot_depth;
    }

    /**
     * @param max_bot_depth the max_bot_depth to set
     */
    public void setMax_bot_depth(Double max_bot_depth) {
        this.max_bot_depth = max_bot_depth;
    }

    /**
     * @return the upper_interpret_depth
     */
    public Double getUpper_interpret_depth() {
        return upper_interpret_depth;
    }

    /**
     * @param upper_interpret_depth the upper_interpret_depth to set
     */
    public void setUpper_interpret_depth(Double upper_interpret_depth) {
        this.upper_interpret_depth = upper_interpret_depth;
    }

    /**
     * @return the lower_interpret_depth
     */
    public Double getLower_interpret_depth() {
        return lower_interpret_depth;
    }

    /**
     * @param lower_interpret_depth the lower_interpret_depth to set
     */
    public void setLower_interpret_depth(Double lower_interpret_depth) {
        this.lower_interpret_depth = lower_interpret_depth;
    }

    /**
     * @return the upper_integrator_depth
     */
    public Double getUpper_integrator_depth() {
        return upper_integrator_depth;
    }

    /**
     * @param upper_integrator_depth the upper_integrator_depth to set
     */
    public void setUpper_integrator_depth(Double upper_integrator_depth) {
        this.upper_integrator_depth = upper_integrator_depth;
    }

    /**
     * @return the lower_integrator_depth
     */
    public Double getLower_integrator_depth() {
        return lower_integrator_depth;
    }

    /**
     * @param lower_integrator_depth the lower_integrator_depth to set
     */
    public void setLower_integrator_depth(Double lower_integrator_depth) {
        this.lower_integrator_depth = lower_integrator_depth;
    }

    /**
     * @return the quality
     */
    public Integer getQuality() {
        return quality;
    }

    /**
     * @param quality the quality to set
     */
    public void setQuality(Integer quality) {
        this.quality = quality;
    }

    /**
     * @return the bubble_corr
     */
    public Double getBubble_corr() {
        return bubble_corr;
    }

    /**
     * @param bubble_corr the bubble_corr to set
     */
    public void setBubble_corr(Double bubble_corr) {
        this.bubble_corr = bubble_corr;
    }

    /**
     * @return the sa
     */
    public List<SABO> getSa() {
        return sa;
    }

    public void setSa(List<SABO> sa) {
        this.sa = sa;
    }

    public void setDistance(DistanceBO distanceBO) {
        this.distanceBO = distanceBO;
    }

    public DistanceBO getDistanceBO() {
        return distanceBO;
    }
/*
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + (this.freq != null ? this.freq.hashCode() : 0);
        hash = 59 * hash + (this.tranceiver != null ? this.tranceiver.hashCode() : 0);
        hash = 59 * hash + (this.distanceBO != null ? this.distanceBO.hashCode() : 0);
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
        final FrequencyBO other = (FrequencyBO) obj;
        if (this.freq != other.freq && (this.freq == null || !this.freq.equals(other.freq))) {
            return false;
        }
        if (this.tranceiver != other.tranceiver && (this.tranceiver == null || !this.tranceiver.equals(other.tranceiver))) {
            return false;
        }
        if (this.distanceBO != other.distanceBO && (this.distanceBO == null || !this.distanceBO.equals(other.distanceBO))) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(final FrequencyBO other) {
        FrequencyBO otherFr = (FrequencyBO) other;
        return new CompareToBuilder()
                .append(this.distanceBO, otherFr.distanceBO)
                .append(this.freq, otherFr.freq)
                .toComparison();
    }*/

}
