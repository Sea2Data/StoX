/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Biotic.Biotic3;

import Biotic.BioticConversionException;
import Biotic.Biotic1.Biotic1Handler;
import XMLHandling.NamespaceVersionHandler;
import BioticTypes.v3.PreyType;
import BioticTypes.v3.AgedeterminationType;
import BioticTypes.v3.CatchsampleType;
import BioticTypes.v3.CopepodedevstageType;
import BioticTypes.v3.FishstationType;
import BioticTypes.v3.IndividualType;
import BioticTypes.v3.MissionType;
import BioticTypes.v3.MissionsType;
import BioticTypes.v3.ObjectFactory;
import BioticTypes.v3.PreylengthType;
import BioticTypes.v3.TagType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

/**
 *
 * @author Edvin Fuglebakk edvin.fuglebakk@imr.no
 */
public class Biotic3Handler extends NamespaceVersionHandler<MissionsType> {

    private ObjectFactory biotic3factory;
    private boolean one2oneKeys = false;

    public Biotic3Handler() {
        this.latestNamespace = "http://www.imr.no/formats/nmdbiotic/v3_beta";
        this.latestBioticClass = MissionsType.class;
        this.compatibleNamespaces = null;
    }

    /**
     * @param one2onekeys If true, converters from older formats will attempt to generate new keys as a function of older keys. This might incur additional assumptions, or stricter format compliance, so might throw errors in cases where one 2 one correspondance is not ensured (one2one==false).
     */
    public Biotic3Handler(boolean one2onekeys) {
        this.latestNamespace = "http://www.imr.no/formats/nmdbiotic/v3_beta";
        this.latestBioticClass = MissionsType.class;
        this.compatibleNamespaces = null;
        this.one2oneKeys = one2onekeys;
    }

    
    @Override
    public MissionsType read(InputStream xml) throws JAXBException, XMLStreamException, ParserConfigurationException, SAXException, IOException {
        return super.read(xml);
    }

    /**
     * As read, but converts from biotic 1, if acceptBiotic1 is true
     *
     * @param xml
     * @return
     * @throws javax.xml.bind.JAXBException If biotic 1 is correctly formatted,
     * but conversion could not be done due to data consistency errors.
     */
    public MissionsType readOldBiotic(InputStream xml) throws JAXBException, XMLStreamException, ParserConfigurationException, SAXException, IOException, BioticConversionException {

        BioticTypes.v1_4.MissionsType missions = (new Biotic1Handler()).read(xml);
        return this.convertBiotic1(missions);

    }

    @Override
    /**
     * Reads biotic from file. Converts data if precious version of biotic is
     * detected.
     */
    public MissionsType read(File xml) throws JAXBException, XMLStreamException, FileNotFoundException, ParserConfigurationException, SAXException, IOException {
        MissionsType result;
        try (InputStream is = new FileInputStream(xml)) {
            result = this.read(is);
        } catch (javax.xml.bind.UnmarshalException e) {
            Biotic1Handler h = new Biotic1Handler();
            try {
                result = this.convertBiotic1(h.read(xml));
            } catch (BioticConversionException ex) {
                throw new IOException("Error in conversion from biotic 1" + ex.getMessage());
            }
        }
        return result;
    }

    @Override
    public void save(OutputStream xml, MissionsType data) throws JAXBException {
        super.save(xml, data);
    }

    /**
     * @param missionsBiotic1 data to be converted
     * @return converted data
     * @throws Biotic.BioticConversionException
     */
    protected MissionsType convertBiotic1(BioticTypes.v1_4.MissionsType missionsBiotic1) throws BioticConversionException {
        this.biotic3factory = new ObjectFactory();
        MissionsType missions = this.biotic3factory.createMissionsType();
        for (BioticTypes.v1_4.MissionType m : missionsBiotic1.getMission()) {
            MissionType mission = createMissionFromBiotic1(m);
            missions.getMission().add(mission);
        }
        checkKeys(missions);
        return missions;
    }

    private void checkKeyNotNull(Object o) throws BioticConversionException {
        if (o == null) {
            throw new BioticConversionException("Key fields can not be null");
        }
    }

    private void checkKeyNotDuplicated(String key, Set<String> otherKeys) throws BioticConversionException {
        if (otherKeys.contains(key)) {
            throw new BioticConversionException("Keys can not be duplicated");
        }
    }

    /**
     * Flags a conversion exception if key structure is not OK.
     *
     * @param missions
     * @throws BioticConversionException
     */
    protected void checkKeys(MissionsType missions) throws BioticConversionException {
        Set<String> missionKeys = new HashSet<>();

        for (MissionType m : missions.getMission()) {
            checkKeyNotNull(m.getMissiontype());
            checkKeyNotNull(m.getStartyear());
            checkKeyNotNull(m.getPlatform());
            checkKeyNotNull(m.getMissionnumber());

            String missionkeystring = m.getMissiontype() + "/" + m.getStartyear() + "/" + m.getPlatform() + "/" + m.getMissionnumber();
            checkKeyNotDuplicated(missionkeystring, missionKeys);
            missionKeys.add(missionkeystring);

            Set<String> stationKeys = new HashSet<>();
            for (FishstationType f : m.getFishstation()) {
                checkKeyNotNull(f.getSerialnumber());

                String stationkeystring = "" + f.getSerialnumber();
                checkKeyNotDuplicated(stationkeystring, stationKeys);
                stationKeys.add(stationkeystring);

                Set<String> catchKeys = new HashSet<>();
                for (CatchsampleType c : f.getCatchsample()) {
                    checkKeyNotNull(c.getCatchsampleid());

                    String catchkeystring = c.getCatchsampleid() + "";
                    checkKeyNotDuplicated(catchkeystring, catchKeys);
                    catchKeys.add(catchkeystring);

                    Set<String> individualKeys = new HashSet<>();
                    for (IndividualType i : c.getIndividual()) {
                        checkKeyNotNull(i.getSpecimenid());

                        String individualkeystring = "" + i.getSpecimenid();
                        checkKeyNotDuplicated(individualkeystring, individualKeys);
                        individualKeys.add(individualkeystring);

                        Set<String> ageKeys = new HashSet<>();
                        for (AgedeterminationType a : i.getAgedetermination()) {
                            checkKeyNotNull(a.getAgedeterminationid());

                            String agekeystring = "" + a.getAgedeterminationid();
                            checkKeyNotDuplicated(agekeystring, ageKeys);
                            ageKeys.add(agekeystring);
                        }

                        Set<String> tagKeys = new HashSet<>();
                        for (TagType t : i.getTag()) {
                            checkKeyNotNull(t.getTagid());

                            String tagKeyString = "" + t.getTagid();
                            checkKeyNotDuplicated(tagKeyString, tagKeys);
                            tagKeys.add(tagKeyString);
                        }

                        Set<String> preyKeys = new HashSet<>();
                        for (PreyType p : i.getPrey()) {
                            checkKeyNotNull(p.getPreysampleid());

                            String preyKeyString = p.getPreysampleid() + "";
                            checkKeyNotNull(preyKeys.contains(preyKeyString));
                            preyKeys.add(preyKeyString);

                            Set<String> preylengthKeys = new HashSet<>();
                            for (PreylengthType pl : p.getPreylengthfrequencytable()) {
                                checkKeyNotNull(pl.getPreylengthid());

                                String plkeystring = "" + pl.getPreylengthid();
                                checkKeyNotDuplicated(plkeystring, preylengthKeys);
                                preylengthKeys.add(plkeystring);
                            }

                            Set<String> ccpKeys = new HashSet<>();
                            for (CopepodedevstageType cp : p.getCopepodedevstagefrequencytable()) {
                                checkKeyNotNull(cp.getCopepodedevstage());

                                String cpkeystring = "" + cp.getCopepodedevstage();
                                checkKeyNotDuplicated(cpkeystring, ccpKeys);
                                ccpKeys.add(cpkeystring);
                            }
                        }
                    }
                }
            }
        }
    }

    private MissionType createMissionFromBiotic1(BioticTypes.v1_4.MissionType m) throws BioticConversionException {
        MissionType mission = this.biotic3factory.createMissionType();
        mission.setCallsignal(m.getCallsignal());
        mission.setCruise(m.getCruise());
        mission.setMissionnumber(m.getMissionnumber());
        mission.setMissiontype(m.getMissiontype());
        mission.setMissiontypename(m.getMissiontypename());
        mission.setPlatformname(m.getPlatformname());
        mission.setPurpose(m.getPurpose());
        mission.setPlatform(m.getPlatform());
        mission.setMissionstartdate(this.convertDateFromBiotic1(m.getStartdate()));
        mission.setMissionstopdate(this.convertDateFromBiotic1(m.getStopdate()));
        mission.setStartyear(m.getYear());

        for (BioticTypes.v1_4.FishstationType f : m.getFishstation()) {
            mission.getFishstation().add(createFishstationFromBiotic1(f));
        }

        return mission;
    }

    private FishstationType createFishstationFromBiotic1(BioticTypes.v1_4.FishstationType f) throws BioticConversionException {
        FishstationType fishstation = this.biotic3factory.createFishstationType();

        if (f.getArea() == null){
            fishstation.setArea(null);
        }
        else{
            fishstation.setArea(f.getArea().toString());
        }

        fishstation.setBottomdepthstart(f.getBottomdepthstart());
        fishstation.setBottomdepthstop(f.getBottomdepthstop());
        fishstation.setCatchplatform(createStringFromBiotic1(f.getPlatform()));
        fishstation.setClouds(createStringFromBiotic1(f.getClouds()));
        fishstation.setStationcomment(f.getComment());
        fishstation.setVesselcount(f.getCountofvessels());
        fishstation.setDataquality(createStringFromBiotic1(f.getDataquality()));
        fishstation.setDirection(f.getDirection());
        fishstation.setDistance(f.getDistance());
        fishstation.setTrawldoorspread(f.getDoorspread());
        fishstation.setTrawldoorspreadsd(f.getDoorspreadsd());
        fishstation.setFishabundance(createStringFromBiotic1(f.getFishabundance()));
        fishstation.setFishdistribution(createStringFromBiotic1(f.getFishdistribution()));

        fishstation.setFishingdepthmax(f.getFishingdepthmax());
        fishstation.setFishingdepthmean(f.getFishingdepthmean());
        fishstation.setFishingdepthmin(f.getFishingdepthmin());
        fishstation.setFishingground(createStringFromBiotic1(f.getFishingground()));
        fishstation.setFixedstation(createStringFromBiotic1(f.getFixedstation()));
        fishstation.setFlora(createStringFromBiotic1(f.getFlora()));
        fishstation.setGear(createStringFromBiotic1(f.getGear()));
        fishstation.setGearcondition(createStringFromBiotic1(f.getGearcondition()));
        fishstation.setGearcount(f.getGearcount());
        fishstation.setGearno(f.getGearno());
        fishstation.setGearflow(f.getGearspeed());
        fishstation.setHaulvalidity(createStringFromBiotic1(f.getHaulvalidity()));
        fishstation.setLandingsite(createStringFromBiotic1(f.getLandingsite()));
        fishstation.setLatitudeend(f.getLatitudeend());
        fishstation.setLatitudestart(f.getLatitudestart());
        fishstation.setLocation(f.getLocation());
        fishstation.setLongitudeend(f.getLongitudeend());
        fishstation.setLongitudestart(f.getLongitudestart());
        fishstation.setNation(createStringFromBiotic1(f.getNation()));
        fishstation.setSamplequality(createStringFromBiotic1(f.getTrawlquality()));
        fishstation.setSea(createStringFromBiotic1(f.getSea()));
        fishstation.setSerialnumber(f.getSerialno());
        fishstation.setSoaktime(f.getSoaktime());
        fishstation.setStationstartdate(this.convertDateFromBiotic1(f.getStartdate()));
        fishstation.setLogstart(f.getStartlog());
        fishstation.setStationstarttime(this.convertTimeBiotic1(f.getStarttime()));
        fishstation.setStation(f.getStation());
        fishstation.setStationtype(createStringFromBiotic1(f.getStationtype()));
        fishstation.setStationstopdate(this.convertDateFromBiotic1(f.getStopdate()));
        fishstation.setLogstop(f.getStoplog());
        fishstation.setStationstoptime(this.convertTimeBiotic1(f.getStoptime()));
        if (f.getSystem()==null){
            fishstation.setSystem(null);
        }
        else{
            fishstation.setSystem(f.getSystem().toString());
        }
        fishstation.setVerticaltrawlopening(f.getTrawlopening());
        fishstation.setVerticaltrawlopeningsd(f.getTrawlopeningsd());
        fishstation.setTripno(f.getTripno());
        fishstation.setVegetationcover(createStringFromBiotic1(f.getVegetationcover()));
        fishstation.setVisibility(createStringFromBiotic1(f.getVisibility()));
        fishstation.setWaterlevel(f.getWaterlevel());
        fishstation.setWeather(createStringFromBiotic1(f.getWeather()));
        fishstation.setWinddirection(f.getWinddirection());
        fishstation.setWindspeed(f.getWindspeed());
        fishstation.setWirelength(this.convertIntegerToDecimal(f.getWirelength()));

        for (BioticTypes.v1_4.CatchsampleType c : f.getCatchsample()) {
            fishstation.getCatchsample().add(this.createCatchsampleFromBiotic1(c));
        }

        return fishstation;
    }

    private CatchsampleType createCatchsampleFromBiotic1(BioticTypes.v1_4.CatchsampleType c) throws BioticConversionException {
        checkIndividualKeyingBiotic1(c);

        CatchsampleType catchsample = this.biotic3factory.createCatchsampleType();
        catchsample.setCatchsampleid(BigInteger.valueOf(this.createCatchsampleId(c)));
        catchsample.setAbundancecategory(this.createStringFromBiotic1(c.getAbundancecategory()));
        catchsample.setAphia(c.getAphia());
        catchsample.setCatchcomment(c.getComment());
        catchsample.setConservation(this.createStringFromBiotic1(c.getConservation()));
        catchsample.setCatchcount(c.getCount());
        catchsample.setForeignobject(this.createStringFromBiotic1(c.getForeignobject()));
        catchsample.setTissuesample(this.createStringFromBiotic1(c.getGenetics()));
        catchsample.setGroup(this.createStringFromBiotic1(c.getGroup()));
        catchsample.setAgingstructure(this.createStringFromBiotic1(c.getAgingstructure()));
        catchsample.setLengthmeasurement(this.createStringFromBiotic1(c.getLengthmeasurement()));
        catchsample.setLengthsamplecount(c.getLengthsamplecount());
        catchsample.setLengthsamplevolume(c.getLengthsamplevolume());
        catchsample.setLengthsampleweight(c.getLengthsampleweight());
        catchsample.setCommonname(c.getNoname());
        catchsample.setParasite(this.createStringFromBiotic1(c.getParasite()));
        catchsample.setCatchproducttype(this.createStringFromBiotic1((c.getProducttype())));
        catchsample.setRaisingfactor(c.getRaisingfactor());
        catchsample.setSampleproducttype(this.createStringFromBiotic1((c.getSampleproducttype())));
        catchsample.setCatchpartnumber(c.getSamplenumber());
        catchsample.setSampletype(this.createStringFromBiotic1(c.getSampletype()));

        catchsample.setCatchcategory(c.getSpecies());
        catchsample.setSpecimensamplecount(c.getSpecimensamplecount());
        catchsample.setStomach(this.createStringFromBiotic1(c.getStomach()));
        catchsample.setCatchvolume(c.getVolume());
        catchsample.setCatchweight(c.getWeight());

        for (BioticTypes.v1_4.IndividualType i : c.getIndividual()) {
            catchsample.getIndividual().add(this.createIndividualFromBiotic1(i));
        }

        return catchsample;
    }

    private IndividualType createIndividualFromBiotic1(BioticTypes.v1_4.IndividualType i) throws BioticConversionException {
        IndividualType individual = this.biotic3factory.createIndividualType();
        individual.setAbdomenwidth(i.getAbdomenwidth());
        individual.setBlackspot(this.createStringFromBiotic1(i.getBlackspot()));
        individual.setCarapacelength(i.getCarapacelength());
        individual.setCarapacewidth(i.getCarapacewidth());
        individual.setIndividualcomment(i.getComment());
        individual.setDiameter(i.getDiameter());
        individual.setDigestion(this.createStringFromBiotic1(i.getDigestion()));
        individual.setEggstage(this.createStringFromBiotic1(i.getEggstage()));
        individual.setFat(this.createStringFromBiotic1(i.getFat()));
        individual.setFatpercent(i.getFatpercent());
        individual.setForklength(i.getForklength());
        individual.setFungusheart(this.createStringFromBiotic1(i.getFungusheart()));
        individual.setFungusouter(this.createStringFromBiotic1(i.getFungusouter()));
        individual.setFungusspores(this.createStringFromBiotic1(i.getFungusspores()));
        individual.setTissuesamplenumber(i.getGeneticsnumber());
        individual.setGillworms(this.createStringFromBiotic1(i.getGillworms()));
        individual.setGonadweight(i.getGonadweight());
        individual.setHeadlength(i.getHeadlength());
        individual.setJapanesecut(i.getJapanesecut());
        individual.setLength(i.getLength());
        individual.setLengthresolution(this.createStringFromBiotic1(i.getLengthunit()));
        individual.setLengthwithouthead(i.getLengthwithouthead());
        individual.setLiver(this.createStringFromBiotic1(i.getLiver()));
        individual.setLiverparasite(this.createStringFromBiotic1(i.getLiverparasite()));
        individual.setLiverweight(i.getLiverweight());
        individual.setMantlelength(i.getMantlelength());
        individual.setMeroslength(i.getMeroslength());
        individual.setMeroswidth(i.getMeroswidth());
        individual.setMoultingstage(this.createStringFromBiotic1(i.getMoultingstage()));
        individual.setIndividualproducttype(this.createStringFromBiotic1(i.getProducttype()));
        individual.setRightclawlength(i.getRightclawlength());
        individual.setRightclawwidth(i.getRightclawwidth());
        individual.setSex(this.createStringFromBiotic1(i.getSex()));
        individual.setSnouttoanalfin(i.getSnouttoanalfin());
        individual.setSnouttoboneknob(i.getSnouttoboneknob());
        individual.setSnouttodorsalfin(i.getSnouttodorsalfin());
        individual.setSnouttoendoftail(i.getSnouttoendoftail());
        individual.setSnouttoendsqueezed(i.getSnouttoendsqueezed());

        individual.setSpecialstage(this.createStringFromBiotic1(i.getSpecialstage()));
        individual.setSpecimenid(i.getSpecimenno());
        individual.setMaturationstage(this.createStringFromBiotic1(i.getStage()));
        individual.setStomachfillfield(this.createStringFromBiotic1(i.getStomachfillfield()));
        individual.setStomachfilllab(this.createStringFromBiotic1(i.getStomachfilllab()));
        individual.setStomachweight(i.getStomachweight());
        individual.setSwollengills(this.createStringFromBiotic1(i.getSwollengills()));
        individual.setVertebraecount(i.getVertebrae());
        individual.setIndividualvolume(i.getVolume());
        individual.setIndividualweight(i.getWeight());

        for (BioticTypes.v1_4.AgedeterminationType a : i.getAgedetermination()) {
            individual.getAgedetermination().add(this.createAgedeterminationFromBiotic1(a));
        }

        //handles moving of prey from catchsample
        for (BioticTypes.v1_4.PreyType p : this.getPreyForIndividualBiotic1(i)) {
            individual.getPrey().add(createPreyFromBiotic1(p));
        }

        for (BioticTypes.v1_4.TagType t : i.getTag()) {
            individual.getTag().add(createTagFromBiotic1(t));
        }

        return individual;
    }

    private AgedeterminationType createAgedeterminationFromBiotic1(BioticTypes.v1_4.AgedeterminationType a) {
        AgedeterminationType age = this.biotic3factory.createAgedeterminationType();
        age.setAge(a.getAge());
        age.setCalibration(a.getCalibration());
        age.setCoastalannuli(a.getCoastalannuli());
        age.setGrowthzone1(a.getGrowthzone1());
        age.setGrowthzone2(a.getGrowthzone2());
        age.setGrowthzone3(a.getGrowthzone3());
        age.setGrowthzone4(a.getGrowthzone4());
        age.setGrowthzone5(a.getGrowthzone5());
        age.setGrowthzone6(a.getGrowthzone6());
        age.setGrowthzone7(a.getGrowthzone7());
        age.setGrowthzone8(a.getGrowthzone8());
        age.setGrowthzone9(a.getGrowthzone9());
        age.setGrowthzonestotal(a.getGrowthzonestotal());
        age.setAgedeterminationid(a.getNo());
        age.setOceanicannuli(a.getCoastalannuli());
        age.setOtolithcentre(this.createStringFromBiotic1(a.getOtolithcentre()));
        age.setOtolithedge(this.createStringFromBiotic1(a.getOtolithedge()));
        age.setOtolithtype(this.createStringFromBiotic1(a.getOtolithtype()));
        age.setReadability(this.createStringFromBiotic1(a.getReadability()));
        age.setSpawningage(a.getSpawningage());
        age.setSpawningzones(a.getSpawningzones());

        return age;
    }

    private PreyType createPreyFromBiotic1(BioticTypes.v1_4.PreyType p) throws BioticConversionException {

        PreyType prey = this.biotic3factory.createPreyType();
        prey.setPreysampleid(BigInteger.valueOf(createPreytypeid(p)));
        prey.setDevstage(this.createStringFromBiotic1(p.getDevstage()));
        prey.setPreydigestion(this.createStringFromBiotic1(p.getDigestion()));
        prey.setInterval(this.createStringFromBiotic1(p.getInterval()));
        prey.setPreylengthmeasurement(this.createStringFromBiotic1(p.getLengthmeasurement()));
        prey.setPreypartnumber(p.getPartno());
        prey.setPreycategory(p.getSpecies());
        prey.setTotalcount(p.getTotalcount());
        prey.setTotalweight(p.getTotalweight());
        prey.setWeightresolution(this.createStringFromBiotic1(p.getWeightresolution()));

        for (BioticTypes.v1_4.PreylengthType pl : p.getPreylength()) {
            prey.getPreylengthfrequencytable().add(this.convertPreyLengthFromBiotic1(pl));
        }

        for (BioticTypes.v1_4.CopepodedevstageType co : p.getCopepodedevstage()) {
            prey.getCopepodedevstagefrequencytable().add(this.convertCopepodedevstageFromBiotic1(co));
        }

        return prey;
    }

    private TagType createTagFromBiotic1(BioticTypes.v1_4.TagType t) {
        TagType tag = this.biotic3factory.createTagType();
        tag.setTagid(t.getTagno());
        tag.setTagtype(this.createStringFromBiotic1(t.getTagtype()));

        return tag;
    }

    private PreylengthType convertPreyLengthFromBiotic1(BioticTypes.v1_4.PreylengthType pl) throws BioticConversionException {

        PreylengthType preylength = this.biotic3factory.createPreylengthType();
        preylength.setPreylengthid(BigInteger.valueOf(this.creatPreyLengthId(pl)));
        preylength.setLengthintervalcount(pl.getCount());
        preylength.setLengthintervalstart(pl.getLength());

        return preylength;
    }

    private CopepodedevstageType convertCopepodedevstageFromBiotic1(BioticTypes.v1_4.CopepodedevstageType co) {
        CopepodedevstageType cop = this.biotic3factory.createCopepodedevstageType();
        cop.setCopepodedevstage(co.getCopepodedevstage());
        cop.setDevstagecount(co.getCount());

        return cop;
    }

    private String createStringFromBiotic1(BioticTypes.v1_4.StringDescriptionType s) {
        if (s == null) {
            return null;
        }
        return s.getValue();
    }

    // Checks if individuals are correctly keyed in biotic 1 data
    private void checkIndividualKeyingBiotic1(BioticTypes.v1_4.CatchsampleType c) throws BioticConversionException {
        Set<BigInteger> keys = new HashSet<>();
        for (BioticTypes.v1_4.IndividualType i : c.getIndividual()) {
            if (keys.contains(i.getSpecimenno())) {
                throw new BioticConversionException("Individuals not correctly keyed (specimenno " + i.getSpecimenno() + " repeated)");
            } else {
                keys.add(i.getSpecimenno());
            }
        }

        for (BioticTypes.v1_4.PreyType p : c.getPrey()) {
            if (!keys.contains(p.getFishno())) {
                throw new BioticConversionException("Individuals not correctly keyed (fishno " + p.getFishno() + " does not match any specimenno)");
            }
        }
    }

    // Finds all individuals from catchsample that are assosiated with this individual.
    private Iterable<BioticTypes.v1_4.PreyType> getPreyForIndividualBiotic1(BioticTypes.v1_4.IndividualType i) {
        List<BioticTypes.v1_4.PreyType> preys = new LinkedList<>();

        BioticTypes.v1_4.CatchsampleType catchsample = (BioticTypes.v1_4.CatchsampleType) i.getParent();
        for (BioticTypes.v1_4.PreyType p : catchsample.getPrey()) {
            if (p.getFishno().equals(i.getSpecimenno())) {
                preys.add(p);
            }
        }

        return preys;
    }

    private XMLGregorianCalendar convertDateFromBiotic1(String startdate) throws BioticConversionException {
        if (startdate == null) {
            return null;
        }
        try {
            String[] date = startdate.split("/");
            if (date.length != 3) {
                throw new BioticConversionException("Malformed date: " + startdate);
            }
            XMLGregorianCalendar newDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(date[2] + "-" + date[1] + "-" + date[0] + "Z");
            newDate.setTime(DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED, DatatypeConstants.FIELD_UNDEFINED);
            return newDate;
        } catch (DatatypeConfigurationException ex) {
            throw new BioticConversionException("Malformed date: " + startdate);
        }
    }

    private XMLGregorianCalendar convertTimeBiotic1(String starttime) throws BioticConversionException {
        if (starttime == null) {
            return null;
        }
        try {
            XMLGregorianCalendar newTime = DatatypeFactory.newInstance().newXMLGregorianCalendar(starttime + "Z");
            newTime.setDay(DatatypeConstants.FIELD_UNDEFINED);
            newTime.setMonth(DatatypeConstants.FIELD_UNDEFINED);
            newTime.setYear(DatatypeConstants.FIELD_UNDEFINED);
            return newTime;
        } catch (DatatypeConfigurationException ex) {
            throw new BioticConversionException("Malformed date: " + starttime);
        }
    }

    private BigDecimal convertIntegerToDecimal(BigInteger integer) {
        if (integer == null) {
            return null;
        } else {
            return new BigDecimal(integer);
        }
    }

    private int createCatchsampleId(BioticTypes.v1_4.CatchsampleType c) throws BioticConversionException {
        if (this.one2oneKeys) {
            if (c.getSpecies().trim().length() > 6) {
                throw new BioticConversionException("Species code " + c.getSpecies() + " in biotic 1.4 has mor than 6 digits, and key conversion routine does not work.");
            }
            String species = c.getSpecies().trim();

            return Integer.parseInt(String.format("1%06d%d", Integer.parseInt(species), c.getSamplenumber()));

        } else {
            return ((BioticTypes.v1_4.FishstationType) c.getParent()).getCatchsample().indexOf(c) + 1;
        }
    }

    private int createPreytypeid(BioticTypes.v1_4.PreyType p) throws BioticConversionException {

        if (this.one2oneKeys) {
            String catchn;
            //biotic 1.4 allows empty species for preyType.
            if (p.getSpecies().trim().length() == 0) {
                catchn = String.format("1%06d%d", 0, p.getPartno());
            } else if (p.getSpecies().trim().length() > 6) {
                throw new BioticConversionException("Species code " + p.getSpecies() + " in biotic 1.4 has mor than 6 digits, and key conversion routine does not work.");
            } else {
                catchn = String.format("1%06d%d", Integer.parseInt(p.getSpecies().trim()), p.getPartno());
            }
            return Integer.parseInt(catchn);
        } else {
            return ((BioticTypes.v1_4.CatchsampleType) p.getParent()).getPrey().indexOf(p) +1;
        }
    }

    private int creatPreyLengthId(BioticTypes.v1_4.PreylengthType pl) throws BioticConversionException {

        if (this.one2oneKeys) {
            String intervalcode = ((BioticTypes.v1_4.PreyType) pl.getParent()).getInterval().getValue();
            double resolutionM;
            switch (intervalcode) {
                case "1":
                    resolutionM = 1e-3;
                    break;
                case "2":
                    resolutionM = 5e-3;
                    break;
                case "3":
                    resolutionM = 1e-2;
                    break;
                case "4":
                    resolutionM = 3e-2;
                    break;
                case "5":
                    resolutionM = 5e-2;
                    break;
                case "6 ":
                    resolutionM = 5e-4;
                    break;
                case "7 ":
                    resolutionM = 1e-4;
                    break;
                default:
                    throw new BioticConversionException("Interval " + intervalcode + "not recognized.");
            }
            int id = (int) (pl.getLength().doubleValue() / resolutionM) + 1;
            return (id);
        } else {
            return ((BioticTypes.v1_4.PreyType) pl.getParent()).getPreylength().indexOf(pl) + 1;
        }
    }

}
