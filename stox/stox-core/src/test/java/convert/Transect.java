/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package convert;

/**
 *
 * @author aasmunds
 */
public class Transect {
    
    Integer mission;
    Integer stratumNo;
    String stratum;
    Integer transect;
    String ship;
    Integer from;
    Integer to;
    
    public Transect(Integer mission, Integer stratumNo, String stratum, Integer transect, String ship, Integer from, Integer to) {
        this.mission = mission;
        this.stratumNo = stratumNo;
        this.stratum = stratum;
        this.transect = transect;
        this.ship = ship;
        this.from = from;
        this.to = to;
    }
    
    public Integer getMission() {
        return mission;
    }
    
    public Integer getStratumNo() {
        return stratumNo;
    }
    
    public String getStratum() {
        return stratum;
    }
    
    public String getMissionStratumKey() {
        return Util.getMissionStratumKey(mission, stratum);
    }
    
    public String getTransectKey() {
        return getMissionStratumKey() + getTransect();
    }
    public Integer getTransect() {
        return transect;
    }
    
    public String getShip() {
        return ship;
    }
    
    public Integer getFrom() {
        return from;
    }
    
    public Integer getTo() {
        return to;
    }

    public void setFrom(Integer from) {
        this.from = from;
    }

    public void setTo(Integer to) {
        this.to = to;
    }

    public void setMission(Integer mission) {
        this.mission = mission;
    }

    public void setStratumNo(Integer stratumNo) {
        this.stratumNo = stratumNo;
    }

    public void setStratum(String stratum) {
        this.stratum = stratum;
    }
    
    
    
}
