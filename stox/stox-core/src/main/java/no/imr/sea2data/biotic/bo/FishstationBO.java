package no.imr.sea2data.biotic.bo;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Function;
import no.imr.sea2data.imrbase.map.ILatLonEvent;
import no.imr.sea2data.jts.FeaturePojo;

/**
 *
 * @author oddrune
 */
@FeaturePojo(geometryField = "point", idField = "id", ignoreMethods = {"catchBOCollection"})
public class FishstationBO implements Serializable, ILatLonEvent {

    private String id;
    private Integer original;
    private String missionType;
    private String cruise;
    private String callSignal;
    private String platformName;
    private Date last_edited;
    protected String platformcode;
    protected transient String nation;
    private Date startDate;
    private Integer stationNo;
    private Integer catchNumber;
    private Integer serialNo; // redundant
    protected String stationType;
    private Double latitudeStart;
    private Double longitudeStart;
    private Integer system;
    private Integer area;
    private String location;
    private Double bottomDepthStart;
    private Double bottomDepthStop;
    private Integer equipmentNo;
    protected String fishingGearCode;
    private Integer equipmentCount;
    private Double directionGps;
    private Double speedEquipment;
    protected transient Date startTime;
    private Double logStart;
    protected transient Date stopTime;
    private Double distance;
    protected String gearCondition;
    protected String trawlQuality;
    private Double fishingDepthMax;
    private Double fishingDepthMin;
    private Integer fishingDepthCount = 1;
    private Double trawlOpening;
    private Double trawlStdOpening;
    private Double trawlDoorSpread;
    private Double trawlStdDoorSpread;
    private Integer wireLength;
    private Double soaktime;
    private Integer tripNo;
    private String comment;
    private Date stopDate;
    private Double logStop;
    protected Integer platformcodesys;
    private List<no.imr.sea2data.biotic.bo.CatchBO> catchBOs = new ArrayList<>();
    protected String countryCode;
    protected String dataQuality;
    protected String qualityCode;
    protected String fishPlant;
    protected String datasource;
    protected String missionCode;
    protected String strataName;
    protected Double strataArea;
    private Date catchStartDate;
    private Date catchStopDate;
    private Boolean hourUnknownStartDate;
    private Boolean hourUnknownStopDate;
    private Integer year;
    private Double depthGear;
    private Integer sunUp;
    private Integer moonPhase;
    private String stratificationSystem;
    private Double directionGyro;
    private Double speedVessel;
    private Double trawlWingSpread;
    private Double trawlStdWingSpread;
    private Double currentSpeedSurface;
    private Double currentSpeedBottom;
    private Double currentSpeedEquipment;
    private Double currentDirectionSurface;
    private Double currentDirectionBottom;
    private Double currentDirectionEquipment;
    private Integer originalFormat;
    private Double salinityEquipmentDepth;
    private Double temperatureEquipmentDepth;
    private Double longitudeEnd;
    private Double latitudeEnd;
    private String description;
    private Point point;
    private String key = null;
    private Integer flowCount;
    private Double flowConst;
    private String stratum;

    public FishstationBO() {
    }

    public FishstationBO(FishstationBO bo) {
        id = bo.getId();
        cruise = bo.getCruise();
        missionType = bo.getMissionType();
        callSignal = bo.getCallSignal();
        platformName = bo.getPlatformName();
        nation = bo.getNation();
        platformcode = bo.getPlatformcode();
        startDate = bo.getStartDate();
        year = bo.getYear();
        stopDate = bo.getStopDate();
        stationNo = bo.getStationNo();
        serialNo = bo.getSerialNo();
        stationType = bo.getStationType();
        latitudeStart = bo.getLatitudeStart();
        longitudeStart = bo.getLongitudeStart();
        system = bo.getSystem();
        area = bo.getArea();
        location = bo.getLocation();
        bottomDepthStart = bo.getBottomDepthStart();
        bottomDepthStop = bo.getBottomDepthStop();
        equipmentNo = bo.getEquipmentNo();
        fishingGearCode = bo.getFishingGearCode();
        equipmentCount = bo.getEquipmentCount();
        directionGps = bo.getDirectionGps();
        speedEquipment = bo.getSpeedEquipment();
        startTime = bo.getStartTime();
        logStart = bo.getLogStart();
        stopTime = bo.getStopTime();
        distance = bo.getDistance();
        gearCondition = bo.getGearCondition();
        trawlQuality = bo.getTrawlQuality();
        fishingDepthMax = bo.getFishingDepthMax();
        fishingDepthMin = bo.getFishingDepthMin();
        fishingDepthCount = bo.getFishingDepthCount();
        trawlOpening = bo.getTrawlOpening();
        trawlStdOpening = bo.getTrawlStdOpening();
        trawlDoorSpread = bo.getTrawlDoorSpread();
        trawlStdDoorSpread = bo.getTrawlStdDoorSpread();
        wireLength = bo.getWireLength();
        soaktime = bo.getSoaktime();
        tripNo = bo.getTripNo();
        comment = bo.getComment();
        flowCount = bo.getFlowCount();
        flowConst = bo.getFlowConst();
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

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public void setPlatformcodesys(int i) {
        this.platformcodesys = i;
    }

    public void setPlatformcode(String ship) {
        this.platformcode = ship;
    }

    public void setStationType(String stationType) {
        this.stationType = stationType;
    }

    public void setGearCondition(String gearCondition) {
        this.gearCondition = gearCondition;
    }

    public void setTrawlQuality(String trawlQuality) {
        this.trawlQuality = trawlQuality;
    }

    public Integer getPlatformcodesys() {
        return platformcodesys;
    }

    public void setPlatformcodesys(Integer platformcodesys) {
        this.platformcodesys = platformcodesys;
    }

    public List<CatchBO> getCatchBOCollection() {
        return catchBOs;
    }

    public void setCatchBOCollection(List<CatchBO> catchBOs) {
        this.catchBOs = catchBOs;
    }

    public void setFishingGearCode(String fishingGearCode) {
        this.fishingGearCode = fishingGearCode;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getFishingGearCode() {
        return fishingGearCode;
    }

    public String getGearCondition() {
        return gearCondition;
    }

    public String getPlatformcode() {
        return platformcode;
    }

    public String getStationType() {
        return stationType;
    }

    public String getTrawlQuality() {
        return trawlQuality;
    }

    public void setOriginal(Integer original) {
        this.original = original;
    }

    public void setLast_edited(Date last_edited) {
        this.last_edited = last_edited;
    }

    public Integer getOriginal() {
        return original;
    }

    public Date getLast_edited() {
        return last_edited;
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

    public void setTrawlOpening(Double trawlOpening) {
        this.trawlOpening = trawlOpening;
    }

    public void setTrawlDoorSpread(Double trawlDoorSpread) {
        this.trawlDoorSpread = trawlDoorSpread;
    }

    public void setTemperatureEquipmentDepth(Double temperatureEquipmentDepth) {
        this.temperatureEquipmentDepth = temperatureEquipmentDepth;
    }

    public void setSystem(Integer system) {
        this.system = system;
    }

    public void setSunUp(Integer sunUp) {
        this.sunUp = sunUp;
    }

    public void setStratificationSystem(String stratificationSystem) {
        this.stratificationSystem = stratificationSystem;
    }

    public void setStopDate(Date stopDate) {
        this.stopDate = stopDate;
    }

    public void setStationNo(Integer stationNo) {
        this.stationNo = stationNo;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setSpeedVessel(Double speedVessel) {
        this.speedVessel = speedVessel;
    }

    public void setSpeedEquipment(Double speedEquipment) {
        this.speedEquipment = speedEquipment;
    }

    public void setGearSpeed(Double gearSpeed) {
        setSpeedEquipment(gearSpeed);
    }

    public void setSerialNo(Integer serialNo) {
        this.serialNo = serialNo;
    }

    public void setSalinityEquipmentDepth(Double salinityEquipmentDepth) {
        this.salinityEquipmentDepth = salinityEquipmentDepth;
    }

    public void setOriginalFormat(Integer originalFormat) {
        this.originalFormat = originalFormat;
    }

    public void setMoonPhase(Integer moonPhase) {
        this.moonPhase = moonPhase;
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

    public void setEquipmentNo(Integer equipmentNo) {
        this.equipmentNo = equipmentNo;
    }

    public void setGearNo(Integer gearNo) {
        setEquipmentNo(gearNo);
    }

    public void setEquipmentCount(Integer equipmentCount) {
        this.equipmentCount = equipmentCount;
    }

    public void setGearCount(Integer gearCount) {
        setEquipmentCount(gearCount);
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public void setDirectionGyro(Double directionGyro) {
        this.directionGyro = directionGyro;
    }

    public void setDirectionGps(Double directionGps) {
        this.directionGps = directionGps;
    }

    public void setDirection(Double direction) {
        setDirectionGps(direction);
    }

    public void setDepthGear(Double depthGear) {
        this.depthGear = depthGear;
    }

    public void setCurrentSpeedSurface(Double currentSpeedSurface) {
        this.currentSpeedSurface = currentSpeedSurface;
    }

    public void setCurrentSpeedEquipment(Double currentSpeedEquipment) {
        this.currentSpeedEquipment = currentSpeedEquipment;
    }

    public void setCurrentSpeedBottom(Double currentSpeedBottom) {
        this.currentSpeedBottom = currentSpeedBottom;
    }

    public void setCurrentDirectionSurface(Double currentDirectionSurface) {
        this.currentDirectionSurface = currentDirectionSurface;
    }

    public void setCurrentDirectionEquipment(Double currentDirectionEquipment) {
        this.currentDirectionEquipment = currentDirectionEquipment;
    }

    public void setCurrentDirectionBottom(Double currentDirectionBottom) {
        this.currentDirectionBottom = currentDirectionBottom;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCatchStopDate(Date catchStopDate) {
        this.catchStopDate = catchStopDate;
    }

    public void setCatchStartDate(Date catchStartDate) {
        this.catchStartDate = catchStartDate;
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

    public Double getTrawlOpening() {
        return trawlOpening;
    }

    public Double getTrawlDoorSpread() {
        return trawlDoorSpread;
    }

    public Double getTemperatureEquipmentDepth() {
        return temperatureEquipmentDepth;
    }

    public Integer getSystem() {
        return system;
    }

    public Integer getSunUp() {
        return sunUp;
    }

    public String getStratificationSystem() {
        return stratificationSystem;
    }

    public Date getStopDate() {
        return stopDate;
    }

    public Integer getStationNo() {
        return stationNo;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Double getSpeedVessel() {
        return speedVessel;
    }

    public Double getSpeedEquipment() {
        return speedEquipment;
    }

    public Double getGearSpeed() {
        return getSpeedEquipment();
    }

    public Integer getSerialNo() {
        return serialNo;
    }

    public Double getSalinityEquipmentDepth() {
        return salinityEquipmentDepth;
    }

    public Integer getOriginalFormat() {
        return originalFormat;
    }

    public Integer getMoonPhase() {
        return moonPhase;
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
        return equipmentNo;
    }

    public Integer getGearNo() {
        return getEquipmentNo();
    }

    public Integer getEquipmentCount() {
        return equipmentCount;
    }

    public Integer getGearCount() {
        return getEquipmentCount();
    }

    public Double getDistance() {
        /*if (distance == null) {
            if (getStartDate() == null || getStopDate() == null || getStopTime() == null || getStartTime() == null
                    || getSpeedEquipment() == null) {
                return null;
            }
            Date start = IMRdate.encodeDate(getStartDate(), getStartTime());
            Date end = IMRdate.encodeDate(getStopDate(), getStopTime());
            double hours = (end.getTime() - start.getTime()) / 3600000.0;
            if (hours < 0) {
                return null;
            }
            double speed = getSpeedEquipment(); // knot = nm/h
            distance = speed * hours;
        }*/
        return distance;
    }

    public Double getDirectionGyro() {
        return directionGyro;
    }

    public Double getDirectionGps() {
        return directionGps;
    }

    public Double getDirection() {
        return getDirectionGps();
    }

    public Double getDepthGear() {
        return depthGear;
    }

    public Double getCurrentSpeedSurface() {
        return currentSpeedSurface;
    }

    public Double getCurrentSpeedEquipment() {
        return currentSpeedEquipment;
    }

    public Double getCurrentSpeedBottom() {
        return currentSpeedBottom;
    }

    public Double getCurrentDirectionSurface() {
        return currentDirectionSurface;
    }

    public Double getCurrentDirectionEquipment() {
        return currentDirectionEquipment;
    }

    public Double getCurrentDirectionBottom() {
        return currentDirectionBottom;
    }

    public String getComment() {
        return comment;
    }

    public Date getCatchStopDate() {
        return catchStopDate;
    }

    public Date getCatchStartDate() {
        return catchStartDate;
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

    public Integer getCatchNumber() {
        return catchNumber;
    }

    public void setCatchNumber(Integer catchNumber) {

        this.catchNumber = catchNumber;
    }

    public Boolean getHourUnknownStartDate() {
        return hourUnknownStartDate;
    }

    public void setHourUnknownStartDate(Boolean hourUnknownStartDate) {

        this.hourUnknownStartDate = hourUnknownStartDate;
    }

    public Boolean getHourUnknownStopDate() {
        return hourUnknownStopDate;
    }

    public void setHourUnknownStopDate(Boolean hourUnknownStopDate) {

        this.hourUnknownStopDate = hourUnknownStopDate;
    }

    public String getQualityCode() {
        return qualityCode;
    }

    public String getFishPlant() {
        return fishPlant;
    }

    public void setFishPlant(String fishPlant) {
        this.fishPlant = fishPlant;
    }

    public void setQualityCode(String qualityCode) {
        this.qualityCode = qualityCode;
    }

    public String getDataQuality() {
        return dataQuality;
    }

    public void setDataQuality(String dataQuality) {
        this.dataQuality = dataQuality;
    }

    public String getDatasource() {
        return datasource;
    }

    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    public String getMissionCode() {
        return missionCode;
    }

    public void setMissionCode(String missionCode) {
        this.missionCode = missionCode;
    }

    public Double getStrataArea() {
        return strataArea;
    }

    public void setStrataArea(Double strataArea) {
        this.strataArea = strataArea;
    }

    public String getStrataName() {
        return strataName;
    }

    public void setStrataName(String strataName) {
        this.strataName = strataName;
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

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStopTime() {
        return stopTime;
    }

    public void setStopTime(Date stopTime) {
        this.stopTime = stopTime;
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

   /* @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (this.serialNo != null ? this.serialNo.hashCode() : 0);
        hash = 53 * hash + (this.year != null ? this.year.hashCode() : 0);
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
        final FishstationBO other = (FishstationBO) obj;
        if (this.serialNo != other.serialNo && (this.serialNo == null || !this.serialNo.equals(other.serialNo))) {
            return false;
        }
        if (this.year != other.year && (this.year == null || !this.year.equals(other.year))) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(final FishstationBO other) {
        int cmpVal = ObjectUtils.compare(this.year, other.year);
        if (cmpVal == 0) {
            cmpVal = ObjectUtils.compare(this.serialNo, other.serialNo);
        }
        return cmpVal;
    }*/

    /**
     *
     * @return the point
     */
    public Point getPoint() {

        if (longitudeStart != null && latitudeStart != null) {
            final GeometryFactory geomFac = new GeometryFactory();
            point = geomFac.createPoint(new Coordinate(longitudeStart, latitudeStart));
            return point;
        }
        return null;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    // Wrappers for biotic xml naming:
    public String getPlatform() {
        return getPlatformcode();
    }

    public void setPlatform(String platform) {
        setPlatformcode(platform);
    }

    public Integer getStation() {
        return getStationNo();
    }

    public void setStation(Integer station) {
        setStationNo(station);
    }

    public String getFishStationType() {
        return getStationType();
    }

    public void setFishStationType(String fishStationType) {
        setStationType(fishStationType);
    }

    public Integer getEquipmentNumber() {
        return getEquipmentNo();
    }

    public void setEquipmentNumber(Integer equipmentNumber) {
        setEquipmentNo(equipmentNumber);
    }

    public String getGear() {
        return getFishingGearCode();
    }

    public void setGear(String equipment) {
        setFishingGearCode(equipment);
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

    public Integer getFlowCount() {
        return flowCount;
    }

    public void setFlowCount(Integer flowCount) {
        this.flowCount = flowCount;
    }

    public Double getFlowConst() {
        return flowConst;
    }

    public void setFlowConst(Double flowConst) {
        this.flowConst = flowConst;
    }

    public String getStratum() {
        return stratum;
    }

    public void setStratum(String stratum) {
        this.stratum = stratum;
    }

    public String getMissionType() {
        return missionType;
    }

    public void setMissionType(String missionType) {
        this.missionType = missionType;
    }

}
