package no.imr.stox.functions.biotic;

import java.util.List;
import no.imr.sea2data.biotic.bo.AgeDeterminationBO;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
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
            currentMissionType = getCurrentAttributeValue("missiontype");
            boolean useMissionTypeInCruiseTag = project.getResourceVersion() > 1.26;
            String mtPref = useMissionTypeInCruiseTag ? getCurrentAttributeValue("missiontype") + "-" : "";
            currentCruise = mtPref + getCurrentAttributeValue("year");
            currentPlatform = getCurrentAttributeValue("platform");
            currentPlatformName = getCurrentAttributeValue("platformname");
            result = stations;
        } else if (current instanceof List && elmName.equals("fishstation")) {
            FishstationBO station = new FishstationBO();
            station.setCruise(currentCruise);
            station.setMissiontype(currentMissionType);
            station.setCatchplatform(currentPlatform);
            station.setPlatformname(currentPlatformName);
            stations.add(station);
            result = station;
        } else if (current instanceof FishstationBO && elmName.equals("catchsample")) {
            FishstationBO station = (FishstationBO) current;
            String taxa = getCurrentAttributeValue("species");
            if (taxa == null) {
                taxa = "ukjent";
            }
            SampleBO sample = station.addSample(taxa);
            String noName = getCurrentAttributeValue("noname");
            if (noName == null || noName.isEmpty()) {
                // Back compability when noname is not given in xml file.
                // noName = getSpecies(taxa);
            } else {
                // Rid of single quite since in stock SILD'G03 Jexl is using it as string delimiter
                noName = noName.replace("'", "")/*.toUpperCase()*/;
            }
            sample.getCatchBO().setCommonname(noName);
            sample.getCatchBO().setAphia(getCurrentAttributeValue("aphia"));
            result = sample;
        } else if (current instanceof SampleBO && elmName.equals("individual")) {
            SampleBO sample = (SampleBO) current;
            IndividualBO indBO = sample.addIndividual();
            result = indBO;
        } /*else if (current instanceof SampleBO && elmName.equals("prey")) {
            SampleBO sample = (SampleBO) current;
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
            bo.setNation(value);
        } else if (key.equals("platform")) {
            bo.setCatchplatform(value);
        } else if (key.equals("startdate")) {
            bo.setStationstartdate(IMRdate.strToDate(value));
        } else if (key.equals("stopdate")) {
            bo.setStationstopdate(IMRdate.strToDate(value));
        } else if (key.equals("station")) {
            bo.setStation(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("serialno") || key.equals("catchnumber")) {
            bo.setSerialnumber(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("fishstationtype") || key.equals("stationtype")) {
            bo.setStationtype(value);
        } else if (key.equals("latitudestart")) {
            bo.setLatitudestart(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("longitudestart")) {
            bo.setLongitudestart(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("latitudeend")) {
            bo.setLatitudeend(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("longitudeend")) {
            bo.setLongitudeend(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("system")) {
            bo.setSystem(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("area")) {
            bo.setArea(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("location")) {
            bo.setLocation(value);
        } else if (key.equals("bottomdepthstart")) {
            bo.setBottomdepthstart(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("bottomdepthstop")) {
            bo.setBottomdepthstop(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("equipmentnumber") || key.equals("gearno")) {
            bo.setGearno(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("equipment") || key.equals("gear")) {
            bo.setGear(value);
        } else if (key.equals("equipmentcount") || key.equals("gearcount")) {
            bo.setGearcount(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("directiongps") || key.equals("direction")) {
            bo.setDirection(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("speedequipment") || key.equals("gearspeed")) {
            bo.setVesselspeed(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("starttime")) {
            bo.setStationstarttime(IMRdate.strToTime(value));
        } else if (key.equals("logstart") || key.equals("startlog")) {
            bo.setLogstart(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("stoptime")) {
            bo.setStationstoptime(IMRdate.strToTime(value));
        } else if (key.equals("distance")) {
            bo.setDistance(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("gearcondition")) {
            bo.setGearcondition(value);
        } else if (key.equals("trawlquality")) {
            bo.setSamplequality(value);
        } else if (key.equals("fishingdepthmax")) {
            bo.setFishingdepthmax(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("fishingdepthmin")) {
            bo.setFishingdepthmin(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("fishingdepthcount")) {
            bo.setFishingdepthcount(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("trawlopening")) {
            bo.setVerticalTrawlOpening(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("trawlstdopening") || key.equals("trawlopeningsd")) {
            bo.setVerticaltrawlopeningsd(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("trawldoorspread") || key.equals("doorspread")) {
            bo.setTrawldoorspread(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("doorspreadsd")) {
            bo.setTrawldoorspreadsd(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("wirelength")) {
            bo.setWireLength(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("soaktime")) {
            bo.setSoaktime(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("tripno")) {
            bo.setTripno(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("comment")) {
            bo.setStationcomment(value);
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
            bo.setCatchpartnumber(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("sampletype")) {
            bo.setSampletype(Conversion.safeStringtoIntegerNULL(value));
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
            bo.setIndividualweight(Calc.roundTo(StoXMath.kgToGrams(Conversion.safeStringtoDoubleNULL(value)), 8));
        } else if (key.equals("volume")) {
            bo.setIndividualvolume(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("lengthunit")) {
            bo.setLengthresolution(value);
        } else if (key.equals("length")) {
            // In StoX read length as cm.
            bo.setLength(Calc.roundTo(StoXMath.mToCM(Conversion.safeStringtoDoubleNULL(value)), 8));
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
