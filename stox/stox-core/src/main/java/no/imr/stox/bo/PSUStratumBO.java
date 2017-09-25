package no.imr.stox.bo;

/**
 * TODO: what does this class do?
 *
 * @author aasmunds
 */
public class PSUStratumBO { 

    private String psu;
    private String stratum;

    public PSUStratumBO(String psu, String stratum) {
        this.psu = psu;
        this.stratum = stratum;
    }

    public String getPsu() {
        return psu;
    }

    public void setPsu(String psu) {
        this.psu = psu;
    }

    public String getStratum() {
        return stratum;
    }

    public void setStratum(String stratum) {
        this.stratum = stratum;
    }

}
