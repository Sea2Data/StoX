package no.imr.sea2data.echosounderbo;

import java.io.Serializable;

/**
 * Business object containing information about the purpouse of each acoustic
 * cateory
 *
 * Usually contained within a EchosounderDatasetBO object
 *
 * @author aasmunds
 */
public class PurposeBO implements Serializable {

    private String acoustic_category;
    private Integer purpose;
    private EchosounderDatasetBO echosounderDatasetBO;

    public PurposeBO() {
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
     * @return the purpose
     */
    public Integer getPurpose() {
        return purpose;
    }

    /**
     * @param purpose the purpose to set
     */
    public void setPurpose(Integer purpose) {
        this.purpose = purpose;
    }


    public void setEchosounderDataSet(EchosounderDatasetBO echosounderDatasetBO) {
        this.echosounderDatasetBO = echosounderDatasetBO;
    }
/*
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + (this.acoustic_category != null ? this.acoustic_category.hashCode() : 0);
        hash = 43 * hash + (this.echosounderDatasetBO != null ? this.echosounderDatasetBO.hashCode() : 0);
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
        final PurposeBO other = (PurposeBO) obj;
        if ((this.acoustic_category == null) ? (other.acoustic_category != null) : !this.acoustic_category.equals(other.acoustic_category)) {
            return false;
        }
        if (this.echosounderDatasetBO != other.echosounderDatasetBO && (this.echosounderDatasetBO == null || !this.echosounderDatasetBO.equals(other.echosounderDatasetBO))) {
            return false;
        }
        return true;
    }*/
}
