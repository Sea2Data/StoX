package no.imr.sea2data.echosounderbo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Business object containing data for a Echosounder Dataset.
 *
 * @author aasmunds
 */
public class EchosounderDatasetBO implements Serializable {

    private Date report_time;
    private String lsss_version;
    private String nation;
    private String platform;
    private String cruise;
    private final List<DistanceBO> distances = new ArrayList<DistanceBO>();
    private final List<PurposeBO> purposes = new ArrayList<PurposeBO>();

    /**
     * Default constructor
     */
    public EchosounderDatasetBO() {
    }

    /**
     * @return the report_time
     */
    public Date getReport_time() {
        return report_time;
    }

    /**
     * @param report_time the report_time to set
     */
    public void setReport_time(Date report_time) {
        this.report_time = report_time;
    }

    /**
     * @return the lsss_version
     */
    public String getLsss_version() {
        return lsss_version;
    }

    /**
     * @param lsss_version the lsss_version to set
     */
    public void setLsss_version(String lsss_version) {
        this.lsss_version = lsss_version;
    }

    /**
     * @return the nation
     */
    public String getNation() {
        return nation;
    }

    /**
     * @param nation the nation to set
     */
    public void setNation(String nation) {
        this.nation = nation;
    }

    /**
     * @return the platform
     */
    public String getPlatform() {
        return platform;
    }

    /**
     * @param platform the platform to set
     */
    public void setPlatform(String platform) {
        this.platform = platform;
    }

    /**
     * @return the cruise
     */
    public String getCruise() {
        return cruise;
    }

    /**
     * @param cruise the cruise to set
     */
    public void setCruise(String cruise) {
        this.cruise = cruise;
    }

    /**
     * @return the distances
     */
    public List<DistanceBO> getDistances() {
        return distances;
    }

    /**
     * @return the purposes
     */
    public List<PurposeBO> getPurposes() {
        return purposes;
    }

    /*@Override
    public int hashCode() {
        int hash = 3;
        hash = 83 * hash + (this.report_time != null ? this.report_time.hashCode() : 0);
        hash = 83 * hash + (this.cruise != null ? this.cruise.hashCode() : 0);
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
        final EchosounderDatasetBO other = (EchosounderDatasetBO) obj;
        if (this.report_time != other.report_time && (this.report_time == null || !this.report_time.equals(other.report_time))) {
            return false;
        }
        if ((this.cruise == null) ? (other.cruise != null) : !this.cruise.equals(other.cruise)) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(final EchosounderDatasetBO other) {
        EchosounderDatasetBO otherE = (EchosounderDatasetBO) other;
        return new CompareToBuilder()
                .append(this.cruise, otherE.cruise)
                .toComparison();
    }*/
}
