package no.imr.sea2data.echosounderbo;

import java.io.Serializable;

/**
 * Business object containing SA values. Usually contained within a FrequencyBO
 *
 * @author aasmunds
 */
public class SABO implements Serializable {

    private String acoustic_category;
    private Integer ch;
    private String ch_type;
    private Double sa;
    private FrequencyBO frequencyBO;

    /**
     * Default constructor
     */
    public SABO() {
    }

    public SABO(FrequencyBO frF, SABO nasc) {
        acoustic_category = nasc.getAcoustic_category();
        ch = nasc.getCh();
        ch_type = nasc.getCh_type();
        sa = nasc.getSa();
        frequencyBO = frF;
    }

    /**
     * @return the acoustic_category
     */
    public String getAcoustic_category() {
        return acoustic_category;
    }

    /**
     * @param acoustic_category the acoustic_category to set
     */
    public void setAcoustic_category(String acoustic_category) {
        this.acoustic_category = acoustic_category;
    }

    /**
     * @return the ch
     */
    public Integer getCh() {
        return ch;
    }

    /**
     * @param ch the ch to set
     */
    public void setCh(Integer ch) {
        this.ch = ch;
    }

    /**
     * @return the ch_type
     */
    public String getCh_type() {
        return ch_type;
    }

    /**
     * @param ch_type the ch_type to set
     */
    public void setCh_type(String ch_type) {
        this.ch_type = ch_type;
    }

    /**
     * @return the sa
     */
    public Double getSa() {
        return sa;
    }

    /**
     * @param sa the sa to set
     */
    public void setSa(Double sa) {
        this.sa = sa;
    }

    public void setFrequency(FrequencyBO frequencyBO) {
        this.frequencyBO = frequencyBO;
    }

    public FrequencyBO getFrequencyBO() {
        return frequencyBO;
    }
/*
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 71 * hash + (this.acoustic_category != null ? this.acoustic_category.hashCode() : 0);
        hash = 71 * hash + (this.ch != null ? this.ch.hashCode() : 0);
        hash = 71 * hash + (this.ch_type != null ? this.ch_type.hashCode() : 0);
        hash = 71 * hash + (this.frequencyBO != null ? this.frequencyBO.hashCode() : 0);
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
        final SABO other = (SABO) obj;
        if ((this.acoustic_category == null) ? (other.acoustic_category != null) : !this.acoustic_category.equals(other.acoustic_category)) {
            return false;
        }
        if (this.ch != other.ch && (this.ch == null || !this.ch.equals(other.ch))) {
            return false;
        }
        if ((this.ch_type == null) ? (other.ch_type != null) : !this.ch_type.equals(other.ch_type)) {
            return false;
        }
        if (this.frequencyBO != other.frequencyBO && (this.frequencyBO == null || !this.frequencyBO.equals(other.frequencyBO))) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(final SABO other) {
        SABO otherSA = (SABO) other;
        return new CompareToBuilder()
                .append(this.frequencyBO, otherSA.frequencyBO)
                .append(this.ch, otherSA.ch)
                .append(this.ch_type, otherSA.ch_type)
                .toComparison();
    }
*/
    /**
     * Get 0 value sa.
     *
     * @param frequency
     * @param chType
     * @param ch
     * @param acousticCategory
     * @return
     */
    public static SABO getZeroValueSABO(FrequencyBO frequency, String chType, int ch, String acousticCategory) {
        SABO saBO = new SABO();
        saBO.setSa(0.0d);
        saBO.setAcoustic_category(acousticCategory);
        saBO.setCh(ch);
        saBO.setFrequency(frequency);
        saBO.setCh_type(chType);
        return saBO;
    }

}
