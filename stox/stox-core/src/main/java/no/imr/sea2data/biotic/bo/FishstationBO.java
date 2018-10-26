package no.imr.sea2data.biotic.bo;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import no.imr.sea2data.imrbase.map.ILatLonEvent;
import no.imr.sea2data.imrbase.util.IMRdate;

/**
 *
 * @author oddrune
 */
public class FishstationBO implements ILatLonEvent {

    private String missiontype;
    private String cruise;
    private String callsignal;
    private String platformname;
    protected String catchplatform;
    protected String nation;
    private LocalDate stationstartdate;
    private Integer station;
    private Integer serialnumber;
    protected String stationtype;
    private Double latitudestart;
    private Double longitudestart;
    private String system;
    private String area;
    private String location;
    private Double bottomdepthstart;
    private Double bottomdepthstop;
    private Integer gearno;
    protected String gear;
    private Integer gearcount;
    private Double direction;
    private Double vesselspeed;
    protected LocalTime stationstarttime;
    private Double logstart;
    protected LocalTime stationStopTime;
    private Double distance;
    protected String gearcondition;
    protected String samplequality;
    private Double fishingdepthmax;
    private Double fishingdepthmin;
    private Integer fishingdepthcount = 1;
    private Double verticaltrawlopening;
    private Double verticaltrawlopeningsd;
    private Double trawldoorspread;
    private Double trawldoorspreadsd;
    private Double wirelength;
    private Double soaktime;
    private Integer tripno;
    private String stationcomment;
    private LocalDate stationstopdate;
    private Double logstop;
    private List<CatchSampleBO> catchSampleBOs = new ArrayList<>();
    private Integer year;
    private Double wingspread;
    private Double wingspreadsd;
    private Double longitudeend;
    private Double latitudeend;
    private String key = null;
    private String stratum;

    public FishstationBO() {
    }

    public FishstationBO(FishstationBO bo) {
        cruise = bo.getCruise();
        missiontype = bo.getMissiontype();
        callsignal = bo.getCallsignal();
        platformname = bo.getPlatformname();
        nation = bo.getNation();
        catchplatform = bo.getCatchplatform();
        stationstartdate = bo.getStationstartdate();
        stationstarttime = bo.getStationstarttime();
        stationstopdate = bo.getStationstopdate();
        stationStopTime = bo.getStationstoptime();
        year = bo.getYear();
        station = bo.getStation();
        serialnumber = bo.getSerialnumber();
        stationtype = bo.getStationtype();
        latitudestart = bo.getLatitudestart();
        longitudestart = bo.getLongitudestart();
        system = bo.getSystem();
        area = bo.getArea();
        location = bo.getLocation();
        bottomdepthstart = bo.getBottomdepthstart();
        bottomdepthstop = bo.getBottomdepthstop();
        gearno = bo.getGearno();
        gear = bo.getGear();
        gearcount = bo.getGearcount();
        direction = bo.getDirection();
        vesselspeed = bo.getVesselspeed();
        logstart = bo.getLogstart();
        distance = bo.getDistance();
        gearcondition = bo.getGearcondition();
        samplequality = bo.getSamplequality();
        fishingdepthmax = bo.getFishingdepthmax();
        fishingdepthmin = bo.getFishingdepthmin();
        fishingdepthcount = bo.getFishingdepthcount();
        verticaltrawlopening = bo.getVerticaltrawlopening();
        verticaltrawlopeningsd = bo.getVerticaltrawlopeningsd();
        trawldoorspread = bo.getTrawldoorspread();
        trawldoorspreadsd = bo.getTrawldoorspreadsd();
        wirelength = bo.getwirelength();
        soaktime = bo.getSoaktime();
        tripno = bo.gettripno();
        stationcomment = bo.getComment();
        stratum = bo.getStratum();
    }

    public String getCruise() {
        return cruise;
    }

    public void setCruise(String cruise) {
        this.cruise = cruise;
    }

    public void setGearcondition(String gearCondition) {
        this.gearcondition = gearCondition;
    }

    public void setSamplequality(String samplequality) {
        this.samplequality = samplequality;
    }

    public String getGearcondition() {
        return gearcondition;
    }

    public String getCatchplatform() {
        return catchplatform;
    }

    public void setCatchplatform(String catchplatform) {
        this.catchplatform = catchplatform;
    }

    public String getSamplequality() {
        return samplequality;
    }

    public void setWireLength(Double wirelength) {
        this.wirelength = wirelength;
    }

    public void setTripno(Integer tripno) {
        this.tripno = tripno;
    }

    public void setWingspread(Double wingspread) {
        this.wingspread = wingspread;
    }

    public void setWingspreadsd(Double wingspreadsd) {
        this.wingspreadsd = wingspreadsd;
    }

    public void setVerticaltrawlopeningsd(Double verticaltrawlopeningsd) {
        this.verticaltrawlopeningsd = verticaltrawlopeningsd;
    }

    public void setTrawldoorspreadsd(Double trawldoorspreadsd) {
        this.trawldoorspreadsd = trawldoorspreadsd;
    }

    public void setVerticalTrawlOpening(Double verticaltrawlopening) {
        this.verticaltrawlopening = verticaltrawlopening;
    }

    public void setTrawldoorspread(Double trawldoorspread) {
        this.trawldoorspread = trawldoorspread;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public void setStationstopdate(LocalDate stationstopdate) {
        this.stationstopdate = stationstopdate;
    }

    public void setStationstartdate(LocalDate stationstartdate) {
        this.stationstartdate = stationstartdate;
    }

    public void setVesselspeed(Double vesselspeed) {
        this.vesselspeed = vesselspeed;
    }

    public void setSerialnumber(Integer serialnumber) {
        this.serialnumber = serialnumber;
    }

    public void setLongitudestart(Double longitudestart) {
        this.longitudestart = longitudestart;
    }

    public void setLongitudeend(Double longitudeend) {
        this.longitudeend = longitudeend;
    }

    public void setLogstop(Double logstop) {
        this.logstop = logstop;
    }

    public void setLogstart(Double logstart) {
        this.logstart = logstart;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setLatitudestart(Double latitudestart) {
        this.latitudestart = latitudestart;
    }

    public void setLatitudeend(Double latitudeend) {
        this.latitudeend = latitudeend;
    }

    public void setFishingdepthmin(Double fishingdepthmin) {
        this.fishingdepthmin = fishingdepthmin;
    }

    public void setFishingdepthcount(Integer fishingdepthcount) {
        this.fishingdepthcount = fishingdepthcount;
    }

    public void setFishingdepthmax(Double fishingdepthmax) {
        this.fishingdepthmax = fishingdepthmax;
    }

    public void setGearno(Integer gearno) {
        this.gearno = gearno;
    }

    public void setGearcount(Integer gearcount) {
        this.gearcount = gearcount;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public void setDirection(Double direction) {
        this.direction = direction;
    }

    public void setStationcomment(String stationcomment) {
        this.stationcomment = stationcomment;
    }

    public void setBottomdepthstop(Double bottomdepthstop) {
        this.bottomdepthstop = bottomdepthstop;
    }

    public void setBottomdepthstart(Double bottomdepthstart) {
        this.bottomdepthstart = bottomdepthstart;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public Double getSoaktime() {
        return soaktime;
    }

    public void setSoaktime(Double soaktime) {

        this.soaktime = soaktime;
    }

    public Double getwirelength() {
        return wirelength;
    }

    public Integer gettripno() {
        return tripno;
    }

    public Double getwingspread() {
        return wingspread;
    }

    public Double getwingspreadsd() {
        return wingspreadsd;
    }

    public Double getVerticaltrawlopeningsd() {
        return verticaltrawlopeningsd;
    }

    public Double getTrawldoorspreadsd() {
        return trawldoorspreadsd;
    }

    public Double getVerticaltrawlopening() {
        return verticaltrawlopening;
    }

    public Double getTrawldoorspread() {
        return trawldoorspread;
    }

    public String getSystem() {
        return system;
    }

    public LocalDate getStationstopdate() {
        return stationstopdate;
    }

    public Integer getStation() {
        return station;
    }

    public void setStation(Integer station) {
        this.station = station;
    }

    public LocalDate getStationstartdate() {
        return stationstartdate;
    }

    public Double getVesselspeed() {
        return vesselspeed;
    }

    public Integer getSerialnumber() {
        return serialnumber;
    }

    public Double getLongitudestart() {
        return longitudestart;
    }

    public Double getLongitudeend() {
        return longitudeend;
    }

    public Double getLogstop() {
        return logstop;
    }

    public Double getLogstart() {
        return logstart;
    }

    public String getLocation() {
        return location;
    }

    public Double getLatitudestart() {
        return latitudestart;
    }

    public Double getLatitudeend() {
        return latitudeend;
    }

    public Double getFishingdepthmin() {
        return fishingdepthmin;
    }

    public Integer getFishingdepthcount() {
        return fishingdepthcount;
    }

    public Double getFishingdepthmax() {
        return fishingdepthmax;
    }

    public Integer getGearno() {
        return gearno;
    }

    public Integer getGearcount() {
        return gearcount;
    }

    public Double getDistance() {
        return distance;
    }

    public Double getDirection() {
        return direction;
    }

    public String getComment() {
        return stationcomment;
    }

    public Double getBottomdepthstop() {
        return bottomdepthstop;
    }

    public Double getBottomdepthstart() {
        return bottomdepthstart;
    }

    public String getArea() {
        return area;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public LocalTime getStationstarttime() {
        return stationstarttime;
    }

    public void setStationstarttime(LocalTime starttime) {
        this.stationstarttime = starttime;
    }

    public LocalTime getStationstoptime() {
        return stationStopTime;
    }

    public void setStationstoptime(LocalTime stopTime) {
        this.stationStopTime = stopTime;
    }

    public String getStationtype() {
        return stationtype;
    }

    public void setStationtype(String stationtype) {
        this.stationtype = stationtype;
    }

    public String getGear() {
        return gear;
    }

    public void setGear(String gear) {
        this.gear = gear;
    }

    public String getCallsignal() {
        return callsignal;
    }

    public void setCallsignal(String callsignal) {
        this.callsignal = callsignal;
    }

    public String getPlatformname() {
        return platformname;
    }

    public void setPlatformname(String platformname) {
        this.platformname = platformname;
    }

    public String getMissiontype() {
        return missiontype;
    }

    public void setMissiontype(String missiontype) {
        this.missiontype = missiontype;
    }

    @Override
    public Double getStartLat() {
        return getLatitudestart();
    }

    @Override
    public Double getStartLon() {
        return getLongitudestart();
    }

    public boolean hasCatch(String noName) {
        for (CatchSampleBO c : getCatchSampleBOs()) {
            if (c.getCommonname().equals(noName)) {
                return true;
            }
        }
        return false;
    }

    public int getCountBy(String species, Function<CatchSampleBO, String> spcodeFunc) {
        int n = 0;
        if (getCatchSampleBOs() == null) {
            return 0;
        }
        for (CatchSampleBO c : getCatchSampleBOs()) {
            String spcode = spcodeFunc.apply(c);
            if (spcode == null) {
                continue;
            }
            if (!spcode.equals(species)) {
                continue;
            }
            if (c.getCatchcount() == null) {
                continue;
            }
            n += c.getCatchcount();
        }
        return n;
    }

    public int getCount(String code) {
        return getCountBy(code, c -> c.getCommonname());
    }

    public int getCountBySpecies(String code) {
        return getCountBy(code, c -> c.getCatchcategory());
    }

    public int getLengthSampleCount(String spec) {
        int n = 0;
        if (getCatchSampleBOs() == null) {
            return 0;
        }
        for (CatchSampleBO c : getCatchSampleBOs()) {
            if (c.getCommonname() == null && c.getCatchcategory() == null) {
                continue;
            }
            if (c.getCommonname() != null) {
                if (!c.getCommonname().equalsIgnoreCase(spec)) { // SILDG03
                    continue;
                }
            } else if (c.getCatchcategory() != null) {
                if (!c.getCatchcategory().equalsIgnoreCase(spec)) { // 161722.G03
                    continue;
                }
            }
            if (c.getLengthsamplecount() == null) {
                continue;
            }
            n += c.getLengthsamplecount();
        }
        return n;
    }

    @Override
    public Double getStopLat() {
        return getLatitudeend();
    }

    @Override
    public Double getStopLon() {
        return getLongitudeend();
    }

    // Helpers
    public String getStratum() {
        return stratum;
    }

    public void setStratum(String stratum) {
        this.stratum = stratum;
    }

    public Integer getYear() {
        return getStationstartdate() != null ? getStationstartdate().getYear() : null;
    }

    @Override
    public String getKey() {
        if (key != null) {
            return key;
        }
        key = (cruise != null ? cruise : (year != null ? year : "")) + "/" + (serialnumber != null ? serialnumber : "");
        return key;
    }

    @Override
    public String toString() {
        return getKey();
    }

    public List<CatchSampleBO> getCatchSampleBOs() {
        return catchSampleBOs;
    }

    public CatchSampleBO addCatchSample() {
        CatchSampleBO cbo = new CatchSampleBO(this);
        getCatchSampleBOs().add(cbo);
        return cbo;
    }

}
