package no.imr.sea2data.biotic.bo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import no.imr.sea2data.imrbase.map.ILatLonEvent;

/**
 *
 * @author oddrune
 */
public class FishstationBO implements ILatLonEvent {

    private String missionType;
    private String cruise;
    private String callSignal;
    private String platformName;
    protected String catchplatform;
    protected String nation;
    private Date stationStartDate;
    private Integer station;
    private Integer serialNo; // redundant
    protected String stationType;
    private Double latitudeStart;
    private Double longitudeStart;
    private Integer system;
    private Integer area;
    private String location;
    private Double bottomDepthStart;
    private Double bottomDepthStop;
    private Integer gearNo;
    protected String gear;
    private Integer gearCount;
    private Double direction;
    private Double vesselSpeed;
    protected Date stationStartTime;
    private Double logStart;
    protected Date stationStopTime;
    private Double distance;
    protected String gearCondition;
    protected String trawlQuality;
    private Double fishingDepthMax;
    private Double fishingDepthMin;
    private Integer fishingDepthCount = 1;
    private Double verticalTrawlOpening;
    private Double trawlStdOpening;
    private Double trawlDoorSpread;
    private Double trawlStdDoorSpread;
    private Integer wireLength;
    private Double soaktime;
    private Integer tripNo;
    private String comment;
    private Date stationStopDate;
    private Double logStop;
    protected Integer platformcodesys;
    private List<no.imr.sea2data.biotic.bo.CatchBO> catchBOs = new ArrayList<>();
    private Integer year;
    private Double trawlWingSpread;
    private Double trawlStdWingSpread;
    private Double longitudeEnd;
    private Double latitudeEnd;
    private String key = null;
    private String stratum;

    public FishstationBO() {
    }

    public FishstationBO(FishstationBO bo) {
        cruise = bo.getCruise();
        missionType = bo.getMissionType();
        callSignal = bo.getCallSignal();
        platformName = bo.getPlatformName();
        nation = bo.getNation();
        catchplatform = bo.getCatchPlatform();
        stationStartDate = bo.getStationStartDate();
        stationStartTime = bo.getStationStartTime();
        stationStopDate = bo.getStationStopDate();
        stationStopTime = bo.getStationStopTime();
        year = bo.getYear();
        station = bo.getStation();
        serialNo = bo.getSerialNo();
        stationType = bo.getStationType();
        latitudeStart = bo.getLatitudeStart();
        longitudeStart = bo.getLongitudeStart();
        system = bo.getSystem();
        area = bo.getArea();
        location = bo.getLocation();
        bottomDepthStart = bo.getBottomDepthStart();
        bottomDepthStop = bo.getBottomDepthStop();
        gearNo = bo.getGearNo();
        gear = bo.getGear();
        gearCount = bo.getGearCount();
        direction = bo.getDirection();
        vesselSpeed = bo.getVesselSpeed();
        logStart = bo.getLogStart();
        distance = bo.getDistance();
        gearCondition = bo.getGearCondition();
        trawlQuality = bo.getTrawlQuality();
        fishingDepthMax = bo.getFishingDepthMax();
        fishingDepthMin = bo.getFishingDepthMin();
        fishingDepthCount = bo.getFishingDepthCount();
        verticalTrawlOpening = bo.getVerticalTrawlOpening();
        trawlStdOpening = bo.getTrawlStdOpening();
        trawlDoorSpread = bo.getTrawlDoorSpread();
        trawlStdDoorSpread = bo.getTrawlStdDoorSpread();
        wireLength = bo.getWireLength();
        soaktime = bo.getSoaktime();
        tripNo = bo.getTripNo();
        comment = bo.getComment();
        stratum = bo.getStratum();
    }

    public final void addSample(String species, SampleBO sampleBO) {
        CatchBO cb = null;
        for (CatchBO cab : catchBOs) {
            if (cab.getTaxa().equals(species)) {
                cb = cab;
                break;
            }
        }
        if (cb == null) {
            cb = new CatchBO(species);
            catchBOs.add(cb);
        }
        cb.getSampleBOCollection().add(sampleBO);
        sampleBO.setCatchBO(cb);
        cb.setStationBO(this);

    }

    public String getCruise() {
        return cruise;
    }

    public void setCruise(String cruise) {
        this.cruise = cruise;
    }

    public void setGearCondition(String gearCondition) {
        this.gearCondition = gearCondition;
    }

    public void setTrawlQuality(String trawlQuality) {
        this.trawlQuality = trawlQuality;
    }

    public List<CatchBO> getCatchBOCollection() {
        return catchBOs;
    }

    public void setCatchBOCollection(List<CatchBO> catchBOs) {
        this.catchBOs = catchBOs;
    }

    public String getGearCondition() {
        return gearCondition;
    }

    public String getCatchPlatform() {
        return catchplatform;
    }

    public void setCatchPlatform(String catchPlatform) {
        this.catchplatform = catchPlatform;
    }
    public String getTrawlQuality() {
        return trawlQuality;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public void setWireLength(Integer wireLength) {
        this.wireLength = wireLength;
    }

    public void setTripNo(Integer tripNo) {
        this.tripNo = tripNo;
    }

    public void setTrawlWingSpread(Double trawlWingSpread) {
        this.trawlWingSpread = trawlWingSpread;
    }

    public void setTrawlStdOpening(Double trawlStdOpening) {
        this.trawlStdOpening = trawlStdOpening;
    }

    public void setTrawlStdDoorSpread(Double trawlStdDoorSpread) {
        this.trawlStdDoorSpread = trawlStdDoorSpread;
    }

    public void setVerticalTrawlOpening(Double trawlOpening) {
        this.verticalTrawlOpening = trawlOpening;
    }

    public void setTrawlDoorSpread(Double trawlDoorSpread) {
        this.trawlDoorSpread = trawlDoorSpread;
    }

    public void setSystem(Integer system) {
        this.system = system;
    }

    public void setStationStopDate(Date stopDate) {
        this.stationStopDate = stopDate;
    }


    public void setStationStartDate(Date startDate) {
        this.stationStartDate = startDate;
    }

    public void setVesselSpeed(Double vesselSpeed) {
        this.vesselSpeed = vesselSpeed;
    }

    public void setSerialNo(Integer serialNo) {
        this.serialNo = serialNo;
    }

    public void setLongitudeStart(Double longitudeStart) {
        this.longitudeStart = longitudeStart;
    }

    public void setLongitudeEnd(Double longitudeEnd) {
        this.longitudeEnd = longitudeEnd;
    }

    public void setLogStop(Double logStop) {
        this.logStop = logStop;
    }

    public void setLogStart(Double logStart) {
        this.logStart = logStart;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setLatitudeStart(Double latitudeStart) {
        this.latitudeStart = latitudeStart;
    }

    public void setLatitudeEnd(Double latitudeEnd) {
        this.latitudeEnd = latitudeEnd;
    }

    public void setFishingDepthMin(Double fishingDepthMin) {
        this.fishingDepthMin = fishingDepthMin;
    }

    public void setFishingDepthCount(Integer fishingDepthCount) {
        this.fishingDepthCount = fishingDepthCount;
    }

    public void setFishingDepthMax(Double fishingDepthMax) {
        this.fishingDepthMax = fishingDepthMax;
    }

    public void setGearNo(Integer gearNo) {
        this.gearNo = gearNo;
    }

    public void setGearCount(Integer gearCount) {
        this.gearCount = gearCount;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public void setDirection(Double direction) {
        this.direction = direction;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setBottomDepthStop(Double bottomDepthStop) {
        this.bottomDepthStop = bottomDepthStop;
    }

    public void setBottomDepthStart(Double bottomDepthStart) {
        this.bottomDepthStart = bottomDepthStart;
    }

    public void setArea(Integer area) {
        this.area = area;
    }

    /**
     * @return soaktime the time either longline or fishnet is fishing in
     * decimal hours
     */
    public Double getSoaktime() {
        return soaktime;
    }

    /**
     * @param soaktime the time either longline or fishnet is fishing in decimal
     * hours
     */
    public void setSoaktime(Double soaktime) {

        this.soaktime = soaktime;
    }

    public Integer getYear() {
        return year;
    }

    public Integer getWireLength() {
        return wireLength;
    }

    public Integer getTripNo() {
        return tripNo;
    }

    public Double getTrawlWingSpread() {
        return trawlWingSpread;
    }

    public Double getTrawlStdWingSpread() {
        return trawlStdWingSpread;
    }

    public Double getTrawlStdOpening() {
        return trawlStdOpening;
    }

    public Double getTrawlStdDoorSpread() {
        return trawlStdDoorSpread;
    }

    public Double getVerticalTrawlOpening() {
        return verticalTrawlOpening;
    }

    public Double getTrawlDoorSpread() {
        return trawlDoorSpread;
    }

    public Integer getSystem() {
        return system;
    }

    public Date getStationStopDate() {
        return stationStopDate;
    }

    public Integer getStation() {
        return station;
    }

    public void setStation(Integer station) {
        this.station = station;
    }

    public Date getStationStartDate() {
        return stationStartDate;
    }

    public Double getVesselSpeed() {
        return vesselSpeed;
    }

    public Integer getSerialNo() {
        return serialNo;
    }

    public Double getLongitudeStart() {
        return longitudeStart;
    }

    public Double getLongitudeEnd() {
        return longitudeEnd;
    }

    public Double getLogStop() {
        return logStop;
    }

    public Double getLogStart() {
        return logStart;
    }

    public String getLocation() {
        return location;
    }

    public Double getLatitudeStart() {
        return latitudeStart;
    }

    public Double getLatitudeEnd() {
        return latitudeEnd;
    }

    public Double getFishingDepthMin() {
        return fishingDepthMin;
    }

    public Integer getFishingDepthCount() {
        return fishingDepthCount;
    }

    public Double getFishingDepthMax() {
        return fishingDepthMax;
    }

    public Integer getEquipmentNo() {
        return gearNo;
    }

    public Integer getGearNo() {
        return getEquipmentNo();
    }

    public Integer getGearCount() {
        return gearCount;
    }

    public Double getDistance() {
        /*if (distance == null) {
            if (getStationStartDate() == null || getStationStopDate() == null || getStationStopTime() == null || getStationStartTime() == null
                    || getSpeedEquipment() == null) {
                return null;
            }
            Date start = IMRdate.encodeDate(getStationStartDate(), getStationStartTime());
            Date end = IMRdate.encodeDate(getStationStopDate(), getStationStopTime());
            double hours = (end.getTime() - start.getTime()) / 3600000.0;
            if (hours < 0) {
                return null;
            }
            double speed = getSpeedEquipment(); // knot = nm/h
            distance = speed * hours;
        }*/
        return distance;
    }

    public Double getDirection() {
        return direction;
    }

    public String getComment() {
        return comment;
    }

    public Double getBottomDepthStop() {
        return bottomDepthStop;
    }

    public Double getBottomDepthStart() {
        return bottomDepthStart;
    }

    public Integer getArea() {
        return area;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public Date getStationStartTime() {
        return stationStartTime;
    }

    public void setStationStartTime(Date startTime) {
        this.stationStartTime = startTime;
    }

    public Date getStationStopTime() {
        return stationStopTime;
    }

    public void setStationStopTime(Date stopTime) {
        this.stationStopTime = stopTime;
    }

    public CatchBO findCatch(String taxa) {
        for (CatchBO c : getCatchBOCollection()) {
            if (c.getTaxa().equals(taxa)) {
                return c;
            }
        }
        return null;
    }

    public SampleBO findSample(String taxa) {
        CatchBO c = findCatch(taxa);
        if (c != null) {
            return c.getSampleBOCollection() != null && c.getSampleBOCollection().size() == 1
                    ? c.getSampleBOCollection().get(0) : null;
        }
        return null;
    }

    @Override
    public String getKey() {
        if (key != null) {
            return key;
        }
        key = (cruise != null ? cruise : (year != null ? year : "")) + "/" + (serialNo != null ? serialNo : "");
        return key;
    }

    @Override
    public String toString() {
        return getKey();
    }


    public String getStationType() {
        return stationType;
    }

    public void setStationType(String stationType) {
        this.stationType = stationType;
    }

    public String getGear() {
        return gear;
    }

    public void setGear(String gear) {
        this.gear = gear;
    }

    public Integer getEquipmentNumber() {
        return getEquipmentNo();
    }

    @Override
    public Double getStartLat() {
        return getLatitudeStart();
    }

    @Override
    public Double getStartLon() {
        return getLongitudeStart();
    }

    public boolean hasCatch(String noName) {
        for (CatchBO c : getCatchBOCollection()) {
            if (c.getNoname().equals(noName)) {
                return true;
            }
        }
        return false;
    }

    public int getCountBy(String species, Function<CatchBO, String> spcodeFunc) {
        int n = 0;
        if (getCatchBOCollection() == null) {
            return 0;
        }
        for (CatchBO c : getCatchBOCollection()) {
            String spcode = spcodeFunc.apply(c);
            if (spcode == null) {
                continue;
            }
            if (!spcode.equals(species)) {
                continue;
            }
            if (c.getSampleBOCollection() == null) {
                continue;
            }
            for (SampleBO s : c.getSampleBOCollection()) {
                if (s.getCount() == null) {
                    continue;
                }
                n += s.getCount();
            }
        }
        return n;
    }

    public int getCount(String code) {
        return getCountBy(code, c -> c.getNoname());
    }

    public int getCountBySpecies(String code) {
        return getCountBy(code, c -> c.getSpecies());
    }

    public int getLengthSampleCount(String spec) {
        int n = 0;
        if (getCatchBOCollection() == null) {
            return 0;
        }
        for (CatchBO c : getCatchBOCollection()) {
            if (c.getNoname() == null && c.getSpecies() == null) {
                continue;
            }
            if (c.getNoname() != null) {
                if (!c.getNoname().equalsIgnoreCase(spec)) { // SILDG03
                    continue;
                }
            } else if (c.getSpecies() != null) {
                if (!c.getSpecies().equalsIgnoreCase(spec)) { // 161722.G03
                    continue;
                }
            }
            if (c.getSampleBOCollection() == null) {
                continue;
            }
            for (SampleBO s : c.getSampleBOCollection()) {
                if (s.getLengthSampleCount() == null) {
                    continue;
                }
                n += s.getLengthSampleCount();
            }
        }
        return n;
    }

    @Override
    public Double getStopLat() {
        return getLatitudeEnd();
    }

    @Override
    public Double getStopLon() {
        return getLongitudeEnd();
    }

    public String getCallSignal() {
        return callSignal;
    }

    public void setCallSignal(String callSignal) {
        this.callSignal = callSignal;
    }

    public String getPlatformName() {
        return platformName;
    }

    public void setPlatformName(String platformName) {
        this.platformName = platformName;
    }

    public String getMissionType() {
        return missionType;
    }

    public void setMissionType(String missionType) {
        this.missionType = missionType;
    }
    public String getStratum() {
        return stratum;
    }

    public void setStratum(String stratum) {
        this.stratum = stratum;
    }
}
