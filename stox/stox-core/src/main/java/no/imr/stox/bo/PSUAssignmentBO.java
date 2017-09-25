package no.imr.stox.bo;

/**
 * Transect assignment
 *
 * @author trondwe
 */
public class PSUAssignmentBO { 

// tag pointing to transect or rectangle
    private String psu;
    private String station;
    private Double weight;

    public PSUAssignmentBO(String psu, String station, Double weight) {
        this.psu = psu;
        this.station = station;
        this.weight = weight;
    }

    public String getPSU() {
        return psu;
    }

    public void setTransect(String transect) {
        this.psu = transect;
    }

    public String getStation() {
        return station;
    }

    public void setStation(String station) {
        this.station = station;
    }

    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }

    @Override
    public String toString() {
        return psu + " " + station + " " + weight;
    }

}
