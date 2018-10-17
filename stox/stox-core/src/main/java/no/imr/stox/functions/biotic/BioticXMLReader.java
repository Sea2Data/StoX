package no.imr.stox.functions.biotic;

import java.util.List;
import java.util.UUID;
import no.imr.sea2data.biotic.bo.AgeDeterminationBO;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.biotic.bo.PreyBO;
import no.imr.sea2data.biotic.bo.SampleBO;
import no.imr.sea2data.imrbase.math.Calc;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrbase.util.IMRdate;
import no.imr.sea2data.imrbase.util.XMLReader;
import no.imr.stox.functions.utils.StoXMath;
import no.imr.stox.model.IProject;

/**
 * Reader that extends XMLReader for reading biotic xml files coming from S2D
 * Editor
 *
 * @author aasmunds
 * @author sjurl
 */
public class BioticXMLReader extends XMLReader {

    /**
     * Fishstations read.
     */
    private String currentMissionType;
    private String currentCruise;
    private String currentPlatform;
    private String currentPlatformName;
    private final List<FishstationBO> stations;
    IProject project;

    /**
     * Constructor that takes a list to populate with fishstations as input.
     *
     * @param stations List to populate.
     * @param project
     */
    public BioticXMLReader(final List<FishstationBO> stations, IProject project) {
        this.stations = stations;
        this.project = project;
    }

    @Override
    protected void onObjectValue(final Object object, final String key, final String value) {
        if (key.equals("cruise")) {
            currentCruise = value;
        } else if (object instanceof FishstationBO) {
            createFishStation(object, key, value);
        } else if (object instanceof SampleBO) {
            createSample(object, key, value);
        } else if (object instanceof IndividualBO) {
            createIndividual(object, key, value);
        } else if (object instanceof AgeDeterminationBO) {
            createAgeDetermination(object, key, value);
        } else if (object instanceof PreyBO) {
            createPrey(object, key, value);
        }
    }

    /*String getSpecies(String taxa) {
        switch (taxa) {
            case "171677":
                return "HAVSIL";
            case "162035":
                return "LODDE";
            case "161722":
                return "SILD";
            case "161722.G03":
                return "SILDG03";
            case "172414":
                return "MAKRELL";
            case "164774":
                return "KOLMULE";
            case "164712":
                return "TORSK";
            case "164744":
                return "HYSE";
            case "164727":
                return "SEI";
        }
        return null;
    }*/
    @Override
    protected Object getObject(final Object current, final String elmName) {
        Object result = null;
        if (current == null && elmName.equals("missions")) {
            result = null;
        }
        if (current == null && elmName.equals("mission")) {
            currentMissionType = getCurrentAttributeValue("missiontype");
            boolean useMissionTypeInCruiseTag = project.getResourceVersion() > 1.26;
            String mtPref = useMissionTypeInCruiseTag ? getCurrentAttributeValue("missiontype") + "-" : "";
            currentCruise = mtPref + getCurrentAttributeValue("year");
            currentPlatform = getCurrentAttributeValue("platform");
            currentPlatformName = getCurrentAttributeValue("platformname");
            result = stations;
        } else if (current instanceof List && elmName.equals("fishstation")) {
            FishstationBO station = new FishstationBO();
            station.setId(UUID.randomUUID().toString().replace("-", "").toString());
            station.setCruise(currentCruise);
            station.setMissionType(currentMissionType);
            station.setPlatform(currentPlatform);
            station.setPlatformName(currentPlatformName);
            stations.add(station);
            result = station;
        } else if (current instanceof FishstationBO && elmName.equals("catchsample")) {
            FishstationBO station = (FishstationBO) current;
            SampleBO sample = new SampleBO();
            String taxa = getCurrentAttributeValue("species");
            if (taxa == null) {
                taxa = "ukjent";
            }
            station.addSample(taxa, sample);
            String noName = getCurrentAttributeValue("noname");
            if (noName == null || noName.isEmpty()) {
                // Back compability when noname is not given in xml file.
                // noName = getSpecies(taxa);
            } else {
                // Rid of single quite since in stock SILD'G03 Jexl is using it as string delimiter
                noName = noName.replace("'", "")/*.toUpperCase()*/;
            }
            sample.getCatchBO().setNoname(noName);
            sample.getCatchBO().setAphia(getCurrentAttributeValue("aphia"));
            result = sample;
        } else if (current instanceof SampleBO && elmName.equals("individual")) {
            SampleBO sample = (SampleBO) current;
            IndividualBO indBO = new IndividualBO();
            sample.getIndividualBOCollection().add(indBO);
            indBO.setSample(sample);
            result = indBO;
        } else if (current instanceof SampleBO && elmName.equals("prey")) {
            SampleBO sample = (SampleBO) current;
            PreyBO prey = new PreyBO();
            sample.addPrey(Conversion.safeStringtoIntegerNULL(getCurrentAttributeValue("fishno")), prey);
            prey.setSample(sample);
            result = prey;
        } else if (current instanceof IndividualBO && elmName.equals("agedetermination")) {
            IndividualBO ind = (IndividualBO) current;
            AgeDeterminationBO ageBO = new AgeDeterminationBO();
            ind.getAgeDeterminationBOCollection().add(ageBO);
            ageBO.setIndividual(ind);
            result = ageBO;
        }
        return result;
    }

    /**
     * Sets an attribute on a fishstation object.
     *
     * @param object The fishstation object
     * @param key The attribute being set
     * @param value The value being set
     */
    private void createFishStation(final Object object, final String key, final String value) {
        FishstationBO bo = (FishstationBO) object;
        if (key.equals("nation")) {
            bo.setNation(value);
        } else if (key.equals("platform")) {
            bo.setPlatformcode(value);
        } else if (key.equals("startdate")) {
            bo.setStartDate(IMRdate.strToDate(value));
            bo.setYear(IMRdate.getYear(bo.getStartDate(), true));
        } else if (key.equals("stopdate")) {
            bo.setStopDate(IMRdate.strToDate(value));
        } else if (key.equals("station")) {
            bo.setStationNo(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("serialno") || key.equals("catchnumber")) {
            bo.setSerialNo(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("fishstationtype") || key.equals("stationtype")) {
            bo.setStationType(value);
        } else if (key.equals("latitudestart")) {
            bo.setLatitudeStart(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("longitudestart")) {
            bo.setLongitudeStart(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("latitudeend")) {
            bo.setLatitudeEnd(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("longitudeend")) {
            bo.setLongitudeEnd(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("system")) {
            bo.setSystem(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("area")) {
            bo.setArea(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("location")) {
            bo.setLocation(value);
        } else if (key.equals("bottomdepthstart")) {
            bo.setBottomDepthStart(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("bottomdepthstop")) {
            bo.setBottomDepthStop(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("equipmentnumber") || key.equals("gearno")) {
            bo.setGearNo(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("equipment") || key.equals("gear")) {
            bo.setGear(value);
        } else if (key.equals("equipmentcount") || key.equals("gearcount")) {
            bo.setGearCount(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("directiongps") || key.equals("direction")) {
            bo.setDirection(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("speedequipment") || key.equals("gearspeed")) {
            bo.setGearSpeed(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("starttime")) {
            bo.setStartTime(IMRdate.strToTime(value));
        } else if (key.equals("logstart") || key.equals("startlog")) {
            bo.setLogStart(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("stoptime")) {
            bo.setStopTime(IMRdate.strToTime(value));
        } else if (key.equals("distance")) {
            bo.setDistance(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("gearcondition")) {
            bo.setGearCondition(value);
        } else if (key.equals("trawlquality")) {
            bo.setTrawlQuality(value);
        } else if (key.equals("fishingdepthmax")) {
            bo.setFishingDepthMax(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("fishingdepthmin")) {
            bo.setFishingDepthMin(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("fishingdepthcount")) {
            bo.setFishingDepthCount(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("trawlopening")) {
            bo.setTrawlOpening(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("trawlstdopening") || key.equals("trawlopeningsd")) {
            bo.setTrawlStdOpening(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("trawldoorspread") || key.equals("doorspread")) {
            bo.setTrawlDoorSpread(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("doorspreadsd")) {
            bo.setTrawlStdDoorSpread(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("wirelength")) {
            bo.setWireLength(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("soaktime")) {
            bo.setSoaktime(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("tripno")) {
            bo.setTripNo(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("flowconst")) {
            bo.setFlowConst(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("flowcount")) {
            bo.setFlowCount(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("comment")) {
            bo.setComment(value);
        }
    }

    /**
     * Sets an attribute on a sample object.
     *
     * @param object The sample object
     * @param key The attribute being set
     * @param value The value being set
     */
    private void createSample(final Object object, final String key, final String value) {
        SampleBO bo = (SampleBO) object;
        if (key.equals("samplenumber")) {
            bo.setSampleNo(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("sampletype")) {
            bo.setSampletype(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("group")) {
            bo.setGroup(value);
        } else if (key.equals("conservation")) {
            bo.setConservationtype(value);
        } else if (key.equals("measurement") || key.equals("producttype")) {
            bo.setMeasurementTypeTotal(value);
        } else if (key.equals("weight")) {
            bo.setTotalWeight(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("volume")) {
            bo.setTotalVolume(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("count")) {
            bo.setTotalCount(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("samplemeasurement") || key.equals("sampleproducttype")) {
            bo.setMeasurementTypeSampled(value);
        } else if (key.equals("lengthmeasurement")) {
            bo.setLengthType(value);
        } else if (key.equals("lengthsampleweight")) {
            bo.setSampledWeight(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("lengthsamplevolume")) {
            bo.setSampledVolume(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("lengthsamplecount")) {
            bo.setSampledCount(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("individualsamplecount") || key.equals("specimensamplecount")) {
            bo.setInumber(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("raisingfactor")) {
            bo.setRaisingFactor(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("agesample") || key.equals("agingstructure")) {
            bo.setFrozenSample(value);
        } else if (key.equals("parasite")) {
            bo.setParasite(value);
        } else if (key.equals("stomach")) {
            bo.setStomach(value);
        } else if (key.equals("genetics")) {
            bo.setGenetics(value);
        } else if (key.equals("nonbiological") || key.equals("foreignobject")) {
            bo.setNonBiological(value);
        } else if (key.equals("comment")) {
            bo.setComment(value);
        }
    }

    /**
     * Sets an attribute on a individual object.
     *
     * @param object The individual object
     * @param key The attribute being set
     * @param value The value being set
     */
    private void createIndividual(final Object object, final String key, final String value) {
        IndividualBO bo = (IndividualBO) object;
        if (key.equals("no") || key.equals("specimenno")) {
            bo.setIndividualNo(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("weightmethod") || key.equals("producttype")) {
            bo.setProductType(value);
        } else if (key.equals("weight")) {
            bo.setWeight(Calc.roundTo(StoXMath.kgToGrams(Conversion.safeStringtoDoubleNULL(value)), 8));
        } else if (key.equals("volume")) {
            bo.setVolume(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("lengthunit")) {
            bo.setLengthType(value);
        } else if (key.equals("length")) {
            // In StoX read length as cm.
            bo.setLength(Calc.roundTo(StoXMath.mToCM(Conversion.safeStringtoDoubleNULL(value)), 8));
        } else if (key.equals("fat")) {
            bo.setFat(value);
        } else if (key.equals("sex")) {
            bo.setSex(value);
        } else if (key.equals("stage")) {
            bo.setStage(value);
        } else if (key.equals("specialstage")) {
            bo.setSpecialStage(value);
        } else if (key.equals("eggstage")) {
            bo.setEggStage(value);
        } else if (key.equals("stomachfillfield")) {
            bo.setStomachFill(value);
        } else if (key.equals("stomachfilllab")) {
            bo.setStomachFill2(value);
        } else if (key.equals("digestdeg") || key.equals("digestion")) {
            bo.setDigestDeg(value);
        } else if (key.equals("liver")) {
            bo.setLiver(value);
        } else if (key.equals("liverparasite")) {
            bo.setLiverParasite(value);
        } else if (key.equals("gillworms")) {
            bo.setGillWorms(value);
        } else if (key.equals("swollengills")) {
            bo.setSwollenGills(value);
        } else if (key.equals("fungusheart")) {
            bo.setFungusHeart(value);
        } else if (key.equals("fungusspores")) {
            bo.setFungusSpores(value);
        } else if (key.equals("fungusouter")) {
            bo.setFungusOuter(value);
        } else if (key.equals("blackspot")) {
            bo.setBlackSpot(value);
        } else if (key.equals("vertebrae")) {
            bo.setVertebrae(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("gonadweight")) {
            bo.setGonadWeight(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("liverweight")) {
            bo.setLiverWeight(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("stomachweight")) {
            bo.setStomachWeight(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("developmentalstage")) {
            bo.setDevelopmentalStage(value);
        } else if (key.equals("comment")) {
            bo.setComment(value);
        }
    }

    /**
     * Sets an attribute on a agedetermination object.
     *
     * @param object The agedetermination object
     * @param key The attribute being set
     * @param value The value being set
     */
    private void createAgeDetermination(final Object object, final String key, final String value) {
        AgeDeterminationBO bo = (AgeDeterminationBO) object;
        if (key.equals("no")) {
            bo.setNo(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("age")) {
            bo.setAge(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("spawningage")) {
            bo.setSpawningAge(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("spawningzones")) {
            bo.setSpawningZones(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("readability")) {
            bo.setReadability(value);
        } else if (key.equals("otolithtype")) {
            bo.setType(value);
        } else if (key.equals("otolithedge")) {
            bo.setOtolithEdge(value);
        } else if (key.equals("otolithcentre")) {
            bo.setOtolithCentre(value);
        } else if (key.equals("calibration")) {
            bo.setCalibration(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("growthzone1")) {
            bo.setGrowthZone1(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("growthzone2")) {
            bo.setGrowthZone2(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("growthzone3")) {
            bo.setGrowthZone3(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("growthzone4")) {
            bo.setGrowthZone4(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("growthzone5")) {
            bo.setGrowthZone5(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("growthzone6")) {
            bo.setGrowthZone6(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("growthzone7")) {
            bo.setGrowthZone7(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("growthzone8")) {
            bo.setGrowthZone8(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("growthzone9")) {
            bo.setGrowthZone9(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("growthzonestotal")) {
            bo.setGrowthZonesTotal(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("coastalannuli")) {
            bo.setCoastalAnnuli(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("oceanicannuli")) {
            bo.setOceanicAnnuli(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("smoltage")) {
            bo.setSmoltAge(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("seaage")) {
            bo.setSeaAge(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("otolithlength")) {
            bo.setOtolithLength(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("hyalinezones")) {
            bo.setHyalineZones(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("weight")) {
            bo.setWeight(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("weightrightotolith")) {
            bo.setWeightRightOtolith(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("weightleftotolith")) {
            bo.setWeightLeftOtolith(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("readdate")) {
            bo.setReadDate(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("readerlevel")) {
            bo.setReaderLevel(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("verifiedby")) {
            bo.setVerifiedBy(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("image")) {
            bo.setImage(value);
        }
    }

    /**
     * Sets an attribute on a prey object.
     *
     * @param object The prey object
     * @param key The attribute being set
     * @param value The value being set
     */
    private void createPrey(final Object object, final String key, final String value) {
        PreyBO bo = (PreyBO) object;
        if (key.equals("species")) {
            bo.setTaxa(value);
        } else if (key.equals("partno")) {
            bo.setPartNo(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("totalcount")) {
            bo.setTotalCount(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("totalweight")) {
            bo.setTotalWeight(Conversion.safeStringtoDoubleNULL(value));
        }
    }
}
