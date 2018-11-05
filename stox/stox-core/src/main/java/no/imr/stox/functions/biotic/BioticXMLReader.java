package no.imr.stox.functions.biotic;

import BioticTypes.v3.MissionType;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import no.imr.sea2data.biotic.bo.AgeDeterminationBO;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.biotic.bo.CatchSampleBO;
import no.imr.sea2data.biotic.bo.MissionBO;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrbase.util.IMRdate;
import no.imr.sea2data.imrbase.util.XMLReader;
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
    private final MissionBO stations;
    IProject project;

    /**
     * Constructor that takes a list to populate with fishstations as input.
     *
     * @param stations List to populate.
     * @param project
     */
    public BioticXMLReader(final MissionBO stations, IProject project) {
        this.stations = stations;
        this.project = project;
    }

    @Override
    protected void onObjectValue(final Object object, final String key, final String value) {
        if (key.equals("cruise")) {
            stations.bo().setCruise(value);
        } else if (object instanceof FishstationBO) {
            createFishStation(object, key, value);
        } else if (object instanceof CatchSampleBO) {
            createSample(object, key, value);
        } else if (object instanceof IndividualBO) {
            createIndividual(object, key, value);
        } else if (object instanceof AgeDeterminationBO) {
            createAgeDetermination(object, key, value);
        } 
    }

    @Override
    protected Object getObject(final Object current, final String elmName) {
        Object result = null;
        if (current == null && elmName.equals("missions")) {
            result = null;
        }
        if (current == null && elmName.equals("mission")) {
            MissionBO mission = new MissionBO();
            mission.bo().setMissiontype(getCurrentAttributeValue("missiontype"));
            boolean useMissionTypeInCruiseTag = project.getResourceVersion() > 1.26;
            String mtPref = useMissionTypeInCruiseTag ? getCurrentAttributeValue("missiontype") + "-" : "";
            String currentCruise = mtPref + getCurrentAttributeValue("year");
            mission.bo().setCruise(currentCruise); // default cruise=missiontype-year in earlier stox
            mission.bo().setPlatform(getCurrentAttributeValue("platform"));
            mission.bo().setPlatformname(getCurrentAttributeValue("platformname"));
            result = stations;
        } else if (current instanceof MissionBO && elmName.equals("fishstation")) {
            FishstationBO station = stations.addFishstation(null);
            station.bo().setCatchplatform(station.getMission().bo().getPlatform());
            result = station;
        } else if (current instanceof FishstationBO && elmName.equals("catchsample")) {
            FishstationBO station = (FishstationBO) current;
            String taxa = getCurrentAttributeValue("species");
            if (taxa == null) {
                taxa = "ukjent";
            }
            CatchSampleBO sample = station.addCatchSample(null);
            sample.bo().setCatchcategory(taxa);
            String noName = getCurrentAttributeValue("noname");
            if (noName == null || noName.isEmpty()) {
                // Back compability when noname is not given in xml file.
                // noName = getSpecies(taxa);
            } else {
                // Rid of single quite since in stock SILD'G03 Jexl is using it as string delimiter
                noName = noName.replace("'", "")/*.toUpperCase()*/;
            }
            sample.bo().setCommonname(noName);
            sample.bo().setAphia(getCurrentAttributeValue("aphia"));
            result = sample;
        } else if (current instanceof CatchSampleBO && elmName.equals("individual")) {
            CatchSampleBO sample = (CatchSampleBO) current;
            IndividualBO indBO = sample.addIndividual(null);
            result = indBO;
        } /*else if (current instanceof CatchSampleBO && elmName.equals("prey")) {
            CatchSampleBO sample = (CatchSampleBO) current;
            PreyBO prey = new PreyBO();
            sample.addPrey(Conversion.safeStringtoIntegerNULL(getCurrentAttributeValue("fishno")), prey);
            prey.setSample(sample);
            result = prey;
        }*/ else if (current instanceof IndividualBO && elmName.equals("agedetermination")) {
            IndividualBO ind = (IndividualBO) current;
            AgeDeterminationBO ageBO = ind.addAgeDetermination(null);
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
            bo.bo().setNation(value);
        } else if (key.equals("platform")) {
            bo.bo().setCatchplatform(value);
        } else if (key.equals("startdate")) {
            bo.bo().setStationstartdate(LocalDate.parse(value, DateTimeFormatter.ofPattern(IMRdate.DATE_FORMAT_DMY)));
        } else if (key.equals("stopdate")) {
            bo.bo().setStationstopdate(LocalDate.parse(value, DateTimeFormatter.ofPattern(IMRdate.DATE_FORMAT_DMY)));
        } else if (key.equals("station")) {
            bo.bo().setStation(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("serialno") || key.equals("catchnumber")) {
            bo.bo().setSerialnumber(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("fishstationtype") || key.equals("stationtype")) {
            bo.bo().setStationtype(value);
        } else if (key.equals("latitudestart")) {
            bo.bo().setLatitudestart(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("longitudestart")) {
            bo.bo().setLongitudestart(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("latitudeend")) {
            bo.bo().setLatitudeend(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("longitudeend")) {
            bo.bo().setLongitudeend(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("system")) {
            bo.bo().setSystem(value);
        } else if (key.equals("area")) {
            bo.bo().setArea(value);
        } else if (key.equals("location")) {
            bo.bo().setLocation(value);
        } else if (key.equals("bottomdepthstart")) {
            bo.bo().setBottomdepthstart(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("bottomdepthstop")) {
            bo.bo().setBottomdepthstop(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("equipmentnumber") || key.equals("gearno")) {
            bo.bo().setGearno(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("equipment") || key.equals("gear")) {
            bo.bo().setGear(value);
        } else if (key.equals("equipmentcount") || key.equals("gearcount")) {
            bo.bo().setGearcount(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("directiongps") || key.equals("direction")) {
            bo.bo().setDirection(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("speedequipment") || key.equals("gearspeed")) {
            bo.bo().setVesselspeed(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("starttime")) {
            bo.bo().setStationstarttime(LocalTime.parse(value, DateTimeFormatter.ofPattern(IMRdate.TIME_FORMAT_HMS)));
        } else if (key.equals("logstart") || key.equals("startlog")) {
            bo.bo().setLogstart(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("stoptime")) {
            bo.bo().setStationstoptime(LocalTime.parse(value, DateTimeFormatter.ofPattern(IMRdate.TIME_FORMAT_HMS)));
        } else if (key.equals("distance")) {
            bo.bo().setDistance(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("gearcondition")) {
            bo.bo().setGearcondition(value);
        } else if (key.equals("trawlquality")) {
            bo.bo().setSamplequality(value);
        } else if (key.equals("fishingdepthmax")) {
            bo.bo().setFishingdepthmax(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("fishingdepthmin")) {
            bo.bo().setFishingdepthmin(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("fishingdepthcount")) {
            bo.bo().setFishingdepthcount(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("trawlopening")) {
            bo.bo().setVerticaltrawlopening(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("trawlstdopening") || key.equals("trawlopeningsd")) {
            bo.bo().setVerticaltrawlopeningsd(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("trawldoorspread") || key.equals("doorspread")) {
            bo.bo().setTrawldoorspread(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("doorspreadsd")) {
            bo.bo().setTrawldoorspreadsd(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("wirelength")) {
            bo.bo().setWirelength(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("soaktime")) {
            bo.bo().setSoaktime(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("tripno")) {
            bo.bo().setTripno(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("comment")) {
            bo.bo().setStationcomment(value);
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
            bo.bo().setCatchpartnumber(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("sampletype")) {
            bo.bo().setSampletype(value);
        } else if (key.equals("group")) {
            bo.bo().setGroup(value);
        } else if (key.equals("conservation")) {
            bo.bo().setConservation(value);
        } else if (key.equals("measurement") || key.equals("producttype")) {
            bo.bo().setCatchproducttype(value);
        } else if (key.equals("weight")) {
            bo.bo().setCatchweight(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("volume")) {
            bo.bo().setCatchvolume(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("count")) {
            bo.bo().setCatchcount(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("samplemeasurement") || key.equals("sampleproducttype")) {
            bo.bo().setSampleproducttype(value);
        } else if (key.equals("lengthmeasurement")) {
            bo.bo().setLengthmeasurement(value);
        } else if (key.equals("lengthsampleweight")) {
            bo.bo().setLengthsampleweight(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("lengthsamplevolume")) {
            bo.bo().setLengthsamplevolume(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("lengthsamplecount")) {
            bo.bo().setLengthsamplecount(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("individualsamplecount") || key.equals("specimensamplecount")) {
            bo.bo().setSpecimensamplecount(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("raisingfactor")) {
            bo.bo().setRaisingfactor(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("agesample") || key.equals("agingstructure")) {
            bo.bo().setAgingstructure(value);
        } else if (key.equals("parasite")) {
            bo.bo().setParasite(value);
        } else if (key.equals("stomach")) {
            bo.bo().setStomach(value);
        } else if (key.equals("genetics")) {
            bo.bo().setTissuesample(value);
        } else if (key.equals("nonbiological") || key.equals("foreignobject")) {
            bo.bo().setForeignobject(value);
        } else if (key.equals("comment")) {
            bo.bo().setCatchcomment(value);
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
            bo.bo().setSpecimenid(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("weightmethod") || key.equals("producttype")) {
            bo.bo().setIndividualproducttype(value);
        } else if (key.equals("weight")) {
            bo.setIndividualweight(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("volume")) {
            bo.bo().setIndividualvolume(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("lengthunit")) {
            bo.bo().setLengthresolution(value);
        } else if (key.equals("length")) {
            // In StoX read length as cm.
            bo.setLength(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("fat")) {
            bo.bo().setFat(value);
        } else if (key.equals("sex")) {
            bo.bo().setSex(value);
        } else if (key.equals("stage")) {
            bo.bo().setMaturationstage(value);
        } else if (key.equals("specialstage")) {
            bo.bo().setSpecialstage(value);
        } else if (key.equals("eggstage")) {
            bo.bo().setEggstage(value);
        } else if (key.equals("stomachfillfield")) {
            bo.bo().setStomachfillfield(value);
        } else if (key.equals("stomachfilllab")) {
            bo.bo().setStomachfilllab(value);
        } else if (key.equals("digestdeg") || key.equals("digestion")) {
            bo.bo().setDigestion(value);
        } else if (key.equals("liver")) {
            bo.bo().setLiver(value);
        } else if (key.equals("liverparasite")) {
            bo.bo().setLiverparasite(value);
        } else if (key.equals("gillworms")) {
            bo.bo().setGillworms(value);
        } else if (key.equals("swollengills")) {
            bo.bo().setSwollengills(value);
        } else if (key.equals("fungusheart")) {
            bo.bo().setFungusheart(value);
        } else if (key.equals("fungusspores")) {
            bo.bo().setFungusspores(value);
        } else if (key.equals("fungusouter")) {
            bo.bo().setFungusouter(value);
        } else if (key.equals("blackspot")) {
            bo.bo().setBlackspot(value);
        } else if (key.equals("vertebrae")) {
            bo.bo().setVertebraecount(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("gonadweight")) {
            bo.bo().setGonadweight(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("liverweight")) {
            bo.bo().setLiverweight(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("stomachweight")) {
            bo.bo().setStomachweight(Conversion.safeStringtoDoubleNULL(value));
        } else if (key.equals("developmentalstage")) {
            //bo.setDevelopmentalStage(value);
        } else if (key.equals("comment")) {
            bo.bo().setIndividualcomment(value);
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
            bo.bo().setAgedeterminationid(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("age")) {
            bo.bo().setAge(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("spawningage")) {
            bo.bo().setSpawningage(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("spawningzones")) {
            bo.bo().setSpawningzones(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("readability")) {
            bo.bo().setReadability(value);
        } else if (key.equals("otolithtype")) {
            bo.bo().setOtolithtype(value);
        } else if (key.equals("otolithedge")) {
            bo.bo().setOtolithedge(value);
        } else if (key.equals("otolithcentre")) {
            bo.bo().setOtolithcentre(value);
        } else if (key.equals("calibration")) {
            bo.bo().setCalibration(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("growthzone1")) {
            bo.bo().setGrowthzone1(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("growthzone2")) {
            bo.bo().setGrowthzone2(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("growthzone3")) {
            bo.bo().setGrowthzone3(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("growthzone4")) {
            bo.bo().setGrowthzone4(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("growthzone5")) {
            bo.bo().setGrowthzone5(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("growthzone6")) {
            bo.bo().setGrowthzone6(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("growthzone7")) {
            bo.bo().setGrowthzone7(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("growthzone8")) {
            bo.bo().setGrowthzone8(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("growthzone9")) {
            bo.bo().setGrowthzone9(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("growthzonestotal")) {
            bo.bo().setGrowthzonestotal(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("coastalannuli")) {
            bo.bo().setCoastalannuli(Conversion.safeStringtoIntegerNULL(value));
        } else if (key.equals("oceanicannuli")) {
            bo.bo().setOceanicannuli(Conversion.safeStringtoIntegerNULL(value));
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
