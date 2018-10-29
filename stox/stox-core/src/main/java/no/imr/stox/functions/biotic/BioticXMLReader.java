package no.imr.stox.functions.biotic;

import BioticTypes.v3.FishstationType;
import BioticTypes.v3.MissionType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import no.imr.sea2data.biotic.bo.AgeDeterminationBO;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.biotic.bo.CatchSampleBO;
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
    private MissionType currentMission;
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
            currentMission.setCruise(value);
        } else if (object instanceof FishstationBO) {
            createFishStation(object, key, value);
        } else if (object instanceof CatchSampleBO) {
            createSample(object, key, value);
        } else if (object instanceof IndividualBO) {
            createIndividual(object, key, value);
        } else if (object instanceof AgeDeterminationBO) {
            createAgeDetermination(object, key, value);
        } /*else if (object instanceof PreyBO) {
            createPrey(object, key, value);
        }*/
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
            currentMission = new MissionType();
            currentMission.setMissiontype(getCurrentAttributeValue("missiontype"));
            boolean useMissionTypeInCruiseTag = project.getResourceVersion() > 1.26;
            String mtPref = useMissionTypeInCruiseTag ? getCurrentAttributeValue("missiontype") + "-" : "";
            String currentCruise = mtPref + getCurrentAttributeValue("year");
            currentMission.setCruise(currentCruise); // default cruise=missiontype-year in earlier stox
            currentMission.setPlatform(getCurrentAttributeValue("platform"));
            currentMission.setPlatformname(getCurrentAttributeValue("platformname"));
            result = stations;
        } else if (current instanceof List && elmName.equals("fishstation")) {
            FishstationBO station = new FishstationBO(currentMission);
            station.getFs().setCatchplatform(station.getMission().getPlatform());
            stations.add(station);
            result = station;
        } else if (current instanceof FishstationBO && elmName.equals("catchsample")) {
            FishstationBO station = (FishstationBO) current;
            String taxa = getCurrentAttributeValue("species");
            if (taxa == null) {
                taxa = "ukjent";
            }
            CatchSampleBO sample = station.addCatchSample();
            sample.setCatchcategory(taxa);
            String noName = getCurrentAttributeValue("noname");
            if (noName == null || noName.isEmpty()) {
                // Back compability when noname is not given in xml file.
                // noName = getSpecies(taxa);
            } else {
                // Rid of single quite since in stock SILD'G03 Jexl is using it as string delimiter
                noName = noName.replace("'", "")/*.toUpperCase()*/;
            }
            sample.setCommonname(noName);
            sample.setAphia(getCurrentAttributeValue("aphia"));
            result = sample;
        } else if (current instanceof CatchSampleBO && elmName.equals("individual")) {
            CatchSampleBO sample = (CatchSampleBO) current;
            IndividualBO indBO = sample.addIndividual();
            result = indBO;
        } /*else if (current instanceof CatchSampleBO && elmName.equals("prey")) {
            CatchSampleBO sample = (CatchSampleBO) current;
            PreyBO prey = new PreyBO();
            sample.addPrey(Conversion.safeStringtoIntegerNULL(getCurrentAttributeValue("fishno")), prey);
            prey.setSample(sample);
            result = prey;
        }*/ else if (current instanceof IndividualBO && elmName.equals("agedetermination")) {
            IndividualBO ind = (IndividualBO) current;
            AgeDeterminationBO ageBO = ind.addAgeDetermination();
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
            bo.getFs().setNation(value);
        } else if (key.equals("platform")) {
            bo.getFs().setCatchplatform(value);
        } else if (key.equals("startdate")) {
            bo.getFs().setStationstartdate(LocalDate.parse(value, DateTimeFormatter.ofPattern(IMRdate.DATE_FORMAT_DMY)));
        } else if (key.equals("stopdate")) {
            bo.getFs().setStationstopdate(LocalDate.parse(value, DateTimeFormatter.ofPattern(IMRdate.DATE_FORMAT_DMY)));
        } else if (key.equals("station")) {
            bo.getFs().setStation(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("serialno") || key.equals("catchnumber")) {
            bo.getFs().setSerialnumber(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("fishstationtype") || key.equals("stationtype")) {
            bo.getFs().setStationtype(value);
        } else if (key.equals("latitudestart")) {
            bo.getFs().setLatitudestart(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("longitudestart")) {
            bo.getFs().setLongitudestart(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("latitudeend")) {
            bo.getFs().setLatitudeend(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("longitudeend")) {
            bo.getFs().setLongitudeend(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("system")) {
            bo.getFs().setSystem(value);
        } else if (key.equals("area")) {
            bo.getFs().setArea(value);
        } else if (key.equals("location")) {
            bo.getFs().setLocation(value);
        } else if (key.equals("bottomdepthstart")) {
            bo.getFs().setBottomdepthstart(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("bottomdepthstop")) {
            bo.getFs().setBottomdepthstop(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("equipmentnumber") || key.equals("gearno")) {
            bo.getFs().setGearno(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("equipment") || key.equals("gear")) {
            bo.getFs().setGear(value);
        } else if (key.equals("equipmentcount") || key.equals("gearcount")) {
            bo.getFs().setGearcount(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("directiongps") || key.equals("direction")) {
            bo.getFs().setDirection(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("speedequipment") || key.equals("gearspeed")) {
            bo.getFs().setVesselspeed(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("starttime")) {
            bo.getFs().setStationstarttime(LocalTime.parse(value, DateTimeFormatter.ofPattern(IMRdate.TIME_FORMAT_HMS)));
        } else if (key.equals("logstart") || key.equals("startlog")) {
            bo.getFs().setLogstart(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("stoptime")) {
            bo.getFs().setStationstoptime(LocalTime.parse(value, DateTimeFormatter.ofPattern(IMRdate.TIME_FORMAT_HMS)));
        } else if (key.equals("distance")) {
            bo.getFs().setDistance(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("gearcondition")) {
            bo.getFs().setGearcondition(value);
        } else if (key.equals("trawlquality")) {
            bo.getFs().setSamplequality(value);
        } else if (key.equals("fishingdepthmax")) {
            bo.getFs().setFishingdepthmax(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("fishingdepthmin")) {
            bo.getFs().setFishingdepthmin(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("fishingdepthcount")) {
            bo.getFs().setFishingdepthcount(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("trawlopening")) {
            bo.getFs().setVerticaltrawlopening(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("trawlstdopening") || key.equals("trawlopeningsd")) {
            bo.getFs().setVerticaltrawlopeningsd(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("trawldoorspread") || key.equals("doorspread")) {
            bo.getFs().setTrawldoorspread(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("doorspreadsd")) {
            bo.getFs().setTrawldoorspreadsd(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("wirelength")) {
            bo.getFs().setWirelength(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("soaktime")) {
            bo.getFs().setSoaktime(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("tripno")) {
            bo.getFs().setTripno(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("comment")) {
            bo.getFs().setStationcomment(value);
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
        CatchSampleBO bo = (CatchSampleBO) object;
        if (key.equals("samplenumber")) {
            bo.setCatchpartnumber(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("sampletype")) {
            bo.setSampletype(value);
        } else if (key.equals("group")) {
            bo.setGroup(value);
        } else if (key.equals("conservation")) {
            bo.setConservation(value);
        } else if (key.equals("measurement") || key.equals("producttype")) {
            bo.setCatchproducttype(value);
        } else if (key.equals("weight")) {
            bo.setCatchweight(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("volume")) {
            bo.setCatchvolume(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("count")) {
            bo.setCatchcount(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("samplemeasurement") || key.equals("sampleproducttype")) {
            bo.setSampleproducttype(value);
        } else if (key.equals("lengthmeasurement")) {
            bo.setLengthmeasurement(value);
        } else if (key.equals("lengthsampleweight")) {
            bo.setLengthsampleweight(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("lengthsamplevolume")) {
            bo.setLengthsamplevolume(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("lengthsamplecount")) {
            bo.setLengthsamplecount(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("individualsamplecount") || key.equals("specimensamplecount")) {
            bo.setSpecimensamplecount(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("raisingfactor")) {
            bo.setRaisingfactor(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("agesample") || key.equals("agingstructure")) {
            bo.setAgingstructure(value);
        } else if (key.equals("parasite")) {
            bo.setParasite(value);
        } else if (key.equals("stomach")) {
            bo.setStomach(value);
        } else if (key.equals("genetics")) {
            bo.setTissuesample(value);
        } else if (key.equals("nonbiological") || key.equals("foreignobject")) {
            bo.setForeignobject(value);
        } else if (key.equals("comment")) {
            bo.setCatchcomment(value);
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
            bo.setSpecimenid(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("weightmethod") || key.equals("producttype")) {
            bo.setIndividualproducttype(value);
        } else if (key.equals("weight")) {
            bo.setIndividualweight(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("volume")) {
            bo.setIndividualvolume(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("lengthunit")) {
            bo.setLengthresolution(value);
        } else if (key.equals("length")) {
            // In StoX read length as cm.
            bo.setLength(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("fat")) {
            bo.setFat(value);
        } else if (key.equals("sex")) {
            bo.setSex(value);
        } else if (key.equals("stage")) {
            bo.setMaturationstage(value);
        } else if (key.equals("specialstage")) {
            bo.setSpecialstage(value);
        } else if (key.equals("eggstage")) {
            bo.setEggstage(value);
        } else if (key.equals("stomachfillfield")) {
            bo.setStomachfillfield(value);
        } else if (key.equals("stomachfilllab")) {
            bo.setStomachfilllab(value);
        } else if (key.equals("digestdeg") || key.equals("digestion")) {
            bo.setDigestion(value);
        } else if (key.equals("liver")) {
            bo.setLiver(value);
        } else if (key.equals("liverparasite")) {
            bo.setLiverparasite(value);
        } else if (key.equals("gillworms")) {
            bo.setGillworms(value);
        } else if (key.equals("swollengills")) {
            bo.setSwollengills(value);
        } else if (key.equals("fungusheart")) {
            bo.setFungusheart(value);
        } else if (key.equals("fungusspores")) {
            bo.setFungusspores(value);
        } else if (key.equals("fungusouter")) {
            bo.setFungusouter(value);
        } else if (key.equals("blackspot")) {
            bo.setBlackspot(value);
        } else if (key.equals("vertebrae")) {
            bo.setVertebraecount(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("gonadweight")) {
            bo.setGonadweight(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("liverweight")) {
            bo.setLiverweight(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("stomachweight")) {
            bo.setStomachweight(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("developmentalstage")) {
            //bo.setDevelopmentalStage(value);
        } else if (key.equals("comment")) {
            bo.setIndividualcomment(value);
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
            bo.setAgedeterminationid(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("age")) {
            bo.setAge(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("spawningage")) {
            bo.setSpawningage(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("spawningzones")) {
            bo.setSpawningzones(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("readability")) {
            bo.setReadability(value);
        } else if (key.equals("otolithtype")) {
            bo.setOtolithtype(value);
        } else if (key.equals("otolithedge")) {
            bo.setOtolithedge(value);
        } else if (key.equals("otolithcentre")) {
            bo.setOtolithcentre(value);
        } else if (key.equals("calibration")) {
            bo.setCalibration(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("growthzone1")) {
            bo.setGrowthzone1(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("growthzone2")) {
            bo.setGrowthzone2(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("growthzone3")) {
            bo.setGrowthzone3(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("growthzone4")) {
            bo.setGrowthzone4(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("growthzone5")) {
            bo.setGrowthzone5(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("growthzone6")) {
            bo.setGrowthzone6(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("growthzone7")) {
            bo.setGrowthzone7(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("growthzone8")) {
            bo.setGrowthzone8(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("growthzone9")) {
            bo.setGrowthzone9(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("growthzonestotal")) {
            bo.setGrowthzonestotal(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("coastalannuli")) {
            bo.setCoastalannuli(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("oceanicannuli")) {
            bo.setOceanicannuli(Conversion.safeStringtoIntegerNULL(value));
        } 
    }

    /**
     * Sets an attribute on a prey object.
     *
     * @param object The prey object
     * @param key The attribute being set
     * @param value The value being set
     */
    /*private void createPrey(final Object object, final String key, final String value) {
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
    }*/
}
