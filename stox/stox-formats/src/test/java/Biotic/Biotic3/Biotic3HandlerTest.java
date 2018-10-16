/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Biotic.Biotic3;

import Biotic.BioticConversionException;
import Biotic.Biotic1.Biotic1Handler;
import BioticTypes.v3.AgedeterminationType;
import BioticTypes.v3.CatchsampleType;
import BioticTypes.v3.CopepodedevstageType;
import BioticTypes.v3.FishstationType;
import BioticTypes.v3.IndividualType;
import BioticTypes.v3.MissionType;
import BioticTypes.v3.MissionsType;
import BioticTypes.v3.PreyType;
import BioticTypes.v3.PreylengthType;
import BioticTypes.v3.TagType;
import HierarchicalData.HierarchicalData;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Edvin Fuglebakk edvin.fuglebakk@imr.no
 */
public class Biotic3HandlerTest {

    public Biotic3HandlerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of read method, of class Biotic3Handler.
     */
    //@Test 
    public void testReadBiotic_InputStream() throws Exception {
        System.out.println("readBiotic");
        InputStream xml = null;
        Biotic3Handler instance = new Biotic3Handler();
        MissionsType expResult = null;
        MissionsType result = instance.read(xml);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of read method, of class Biotic3Handler.
     */
    @Test
    public void testReadBiotic_File_OldBiotic() throws Exception {
        System.out.println("readBiotic file old format");
        File xml = new File(Biotic3HandlerTest.class.getClassLoader().getResource("test.xml").toURI());
        Biotic3Handler instance = new Biotic3Handler();
        MissionsType result = instance.read(xml);
        assertTrue(result.getMission().get(0).getFishstation().size() > 0);
    }

    /**
     * Test of read method, of class Biotic3Handler.
     */
    @Test
    public void testReadOldBiotic() throws Exception {
        System.out.println("readBiotic");
        InputStream xml = Biotic3HandlerTest.class.getClassLoader().getResourceAsStream("test.xml");
        boolean acceptBiotic1 = true;
        Biotic3Handler instance = new Biotic3Handler();
        MissionsType result = instance.readOldBiotic(xml);
        assertTrue(result.getMission().get(0).getFishstation().size() > 0);
        xml.close();
    }

    /**
     * Test of save method, of class Biotic3Handler.
     */
    @Test
    public void testSaveBiotic() throws Exception {
        System.out.println("saveBiotic");
        InputStream xml = Biotic3HandlerTest.class.getClassLoader().getResourceAsStream("test.xml");
        boolean acceptBiotic1 = true;
        Biotic3Handler instance = new Biotic3Handler();
        MissionsType result = instance.readOldBiotic(xml);
        assertTrue(result.getMission().get(0).getFishstation().size() > 0);
        xml.close();

        File temp = File.createTempFile("biotic_example", ".tmp");
        temp.deleteOnExit();
        instance.save(new FileOutputStream(temp), result);

        InputStream re = new FileInputStream(temp);
        MissionsType result_re = instance.read(re);

        assertEquals(result.getMission().get(0).getFishstation().size(), result_re.getMission().get(0).getFishstation().size());
    }

    /**
     * checks that all getters in newObject return the same value as the getter
     * in oldObject with the same name, unless its name is in the set of
     * exceptions.
     *
     * Handles instances of DescriptionType
     *
     * All getters that return a list are paired and recursively checked in the
     * same manner.
     *
     * @param oldObject
     * @param newObject
     * @param exceptions
     */
    private void compareSameName(HierarchicalData oldObject, HierarchicalData newObject, Set<String> exceptions) throws Exception {
        Class cl = newObject.getClass();
        Method[] newMethods = cl.getMethods();
        Map<String, Method> newGetters = new HashMap<>();

        for (Method method : newMethods) {
            if (method.getName().startsWith("get")) {
                newGetters.put(method.getName(), method);
            }
        }

        Class ol = oldObject.getClass();
        Method[] oldMethods = ol.getMethods();
        Map<String, Method> oldGetters = new HashMap<>();
        for (Method method : oldMethods) {
            if (method.getName().startsWith("get")) {
                oldGetters.put(method.getName(), method);
            }
        }

        int checked = 0;
        for (String getter : oldGetters.keySet()) {
            if (newGetters.containsKey(getter) && !exceptions.contains(getter)) {
                checked++;
                Object newResult = newGetters.get(getter).invoke(newObject, null);
                Object oldResult = oldGetters.get(getter).invoke(oldObject, null);
                if (newResult instanceof List) {
                    List<HierarchicalData> newList = (List<HierarchicalData>) newResult;
                    List<HierarchicalData> oldList = (List<HierarchicalData>) oldResult;
                    assertEquals(newList.size(), oldList.size());
                    for (int i = 0; i < newList.size(); i++) {
                        this.compareSameName(oldList.get(i), newList.get(i), exceptions);
                    }
                } else if (oldResult instanceof BioticTypes.v1_4.StringDescriptionType) {
                    String newSdt = (String) newResult;
                    BioticTypes.v1_4.StringDescriptionType oldSdt = (BioticTypes.v1_4.StringDescriptionType) oldResult;
                    System.out.println("Checking: " + getter);
                    assertEquals(newSdt, oldSdt.getValue());
                } else {
                    System.out.println(getter);
                    assertEquals(newResult, oldResult);
                }
            } else if (!exceptions.contains(getter)) {
                fail("Getter " + getter + " not in new format, and not listed in the exceptions.");
            }
        }

        Set oldNames = oldGetters.keySet();
        Set newNames = newGetters.keySet();
        oldNames.retainAll(newNames);
        oldNames.removeAll(exceptions);

        assertEquals(checked, oldNames.size());
    }

    /**
     * Test of convertBiotic1 method, of class Biotic3Handler.
     */
    @Test
    public void testConvertBiotic1ConservedFields() throws Exception {
        System.out.println("convertBiotic1 complete");
        Biotic1Handler b1handler = new Biotic1Handler();
        BioticTypes.v1_4.MissionsType missionsBiotic1 = b1handler.read(Biotic3HandlerTest.class.getClassLoader().getResourceAsStream("ecosurvey.xml"));
        Biotic3Handler instance = new Biotic3Handler();
        MissionsType result = instance.convertBiotic1(missionsBiotic1);

        Set<String> exceptions = new HashSet<>();
        Method[] objectmethods = (new Object()).getClass().getMethods();
        for (Method method : objectmethods) {
            exceptions.add(method.getName());
        }

        // manually check that these cases are handled in other tests. Compare number in comments with matching number in testConvertBiotic1ChangedFields()
        // note that some getters occur on several levels. Also mentioned in comments below.
        exceptions.add("getChildren"); // not part of format
        exceptions.add("getParent"); // not part of format

        exceptions.add("getFlowconst"); //field removed
        exceptions.add("getFlowcount"); //field removed
        exceptions.add("getFishno"); //field removed
        exceptions.add("getDevelopmentalstage"); //field removed
        exceptions.add("getPrey"); //moved. Tested in: testPreyConversionAllThere()
        exceptions.add("getStartdate"); //1 mission and fishstation
        exceptions.add("getStarttime"); //2
        exceptions.add("getStoptime"); //3
        exceptions.add("getStopdate"); //4 mission and fishstation
        exceptions.add("getYear"); //5
        exceptions.add("getTrawlquality"); //6
        exceptions.add("getPlatform"); //7 mission and fishstation
        exceptions.add("getTrawlopening"); //8
        exceptions.add("getTrawlopeningsd"); //9
        exceptions.add("getNoname"); //10
        exceptions.add("getGenetics"); //11
        exceptions.add("getGeneticsnumber"); //12
        exceptions.add("getLengthunit"); //13
        exceptions.add("getStage"); //14
        exceptions.add("getWirelength"); //15
        exceptions.add("getGearspeed"); //16
        exceptions.add("getSpecies"); //17 catch and prey
        exceptions.add("getProducttype"); //18 catch and individual
        exceptions.add("getWeight"); //19 catch and individual
        exceptions.add("getVolume"); //20 catch and individual
        exceptions.add("getCount"); // 21 catch, preylength and copepepodedevstage
        exceptions.add("getComment"); //22 station, catch and individual
        exceptions.add("getCopepodedevstage"); //21
        exceptions.add("getPreylength"); //21
        exceptions.add("getDigestion"); //23 prey
        exceptions.add("getLengthmeasurement"); //24 prey
        exceptions.add("getLength");//21
        exceptions.add("getNo"); //25
        exceptions.add("getTagno"); //26

        exceptions.add("getSystem"); //27
        exceptions.add("getArea"); //28
        exceptions.add("getLocation"); //29
        
        exceptions.add("getStoplog"); //30
        exceptions.add("getStartlog"); //31
        
        exceptions.add("getDoorspread"); //32
        exceptions.add("getDoorspreadsd"); //33
        
        exceptions.add("getCountofvessels"); //34
        
        exceptions.add("getVertebrae"); //35
        
        exceptions.add("getSpecimenno"); //36
        
        exceptions.add("getSamplenumber"); //37
        exceptions.add("getSerialno"); //38

        this.compareSameName(missionsBiotic1, result, exceptions);
    }

    @Test
    /**
     * Tests fields that have changed name of format. Note that level is not
     * checked so all getters that apply to any level have to be checked if in
     * the exceptions to testConvertBiotic1ConservedFields
     */
    public void testConvertBiotic1ChangedFields() throws Exception {
        //ref changes_3.txt

        Biotic1Handler b1handler = new Biotic1Handler();
        BioticTypes.v1_4.MissionsType missionsBiotic1 = b1handler.read(Biotic3HandlerTest.class.getClassLoader().getResourceAsStream("test.xml"));
        Biotic3Handler instance = new Biotic3Handler();
        MissionsType result = instance.convertBiotic1(missionsBiotic1);

        boolean checkedAge = false;
        List<MissionType> newMissions = result.getMission();
        List<BioticTypes.v1_4.MissionType> oldMissions = missionsBiotic1.getMission();
        for (int i = 0; i < newMissions.size(); i++) {
            MissionType newMission = newMissions.get(i);
            BioticTypes.v1_4.MissionType oldMission = oldMissions.get(i);

            //5
            assertEquals(newMission.getStartyear(), oldMission.getYear());

            //1
            String[] date = oldMission.getStartdate().split("/");
            assertEquals(newMission.getMissionstartdate().toString(), date[2] + "-" + date[1] + "-" + date[0] + "Z");

            //3
            date = oldMission.getStopdate().split("/");
            assertEquals(newMission.getMissionstopdate().toString(), date[2] + "-" + date[1] + "-" + date[0] + "Z");

            //7
            assertEquals(newMission.getPlatform(), oldMission.getPlatform());

            List<FishstationType> newStations = newMission.getFishstation();
            List<BioticTypes.v1_4.FishstationType> oldStations = oldMission.getFishstation();
            for (int j = 0; j < newStations.size(); j++) {
                FishstationType newStation = newStations.get(j);
                BioticTypes.v1_4.FishstationType oldStation = oldStations.get(j);

                //38
                assertEquals(newStation.getSerialnumber(), oldStation.getSerialno());
                
                //1
                if (oldStation.getStartdate() == null) {
                    assertNull(newStation.getStationstartdate());
                } else {
                    date = oldStation.getStartdate().split("/");
                    assertEquals(newStation.getStationstartdate().toString(), date[2] + "-" + date[1] + "-" + date[0] + "Z");
                }

                //3
                if (oldStation.getStopdate() == null) {
                    assertNull(newStation.getStationstopdate());
                } else {
                    date = oldStation.getStopdate().split("/");
                    assertEquals(newStation.getStationstopdate().toString(), date[2] + "-" + date[1] + "-" + date[0] + "Z");
                }

                //2
                if (newStation.getStationstarttime() == null) {
                    assertNull(oldStation.getStarttime());
                } else {
                    assertEquals(newStation.getStationstarttime(), oldStation.getStarttime() + "Z");
                }

                //4
                if (newStation.getStationstoptime() == null) {
                    assertNull(oldStation.getStoptime());
                } else {
                    assertEquals(newStation.getStationstoptime(), oldStation.getStoptime() + "Z");
                }

                //7
                assertEquals(newStation.getCatchplatform(), oldStation.getPlatform().getValue());

                //6
                assertEquals(newStation.getSamplequality(), oldStation.getTrawlquality().getValue());

                //8
                assertEquals(newStation.getVerticaltrawlopening(), oldStation.getTrawlopening());
                //9
                assertEquals(newStation.getVerticaltrawlopeningsd(), oldStation.getTrawlopeningsd());

                //15
                if (newStation.getWirelength() == null) {
                    assertNull(oldStation.getWirelength());
                } else {
                    assertTrue(Math.abs(newStation.getWirelength().doubleValue() - oldStation.getWirelength().doubleValue()) < 10e-10);
                }

                //16
                if (newStation.getGearflow() == null) {
                    assertNull(oldStation.getGearspeed());
                } else {
                    assertEquals(newStation.getGearflow(), oldStation.getGearspeed());
                }

                //22
                assertEquals(newStation.getStationcomment(), oldStation.getComment());

                //27
                assertEquals(newStation.getSystem(), oldStation.getSystem().toString());
                //28
                assertEquals(newStation.getArea(), oldStation.getArea().toString());
                //29
                assertEquals(newStation.getLocation(), oldStation.getLocation());
                
                //30
                assertEquals(newStation.getLogstop(), oldStation.getStoplog());
                
                //31
                assertEquals(newStation.getLogstart(), oldStation.getStartlog());

                //32
                assertEquals(newStation.getTrawldoorspread(), oldStation.getDoorspread());
                
                //33
                assertEquals(newStation.getTrawldoorspreadsd(), oldStation.getDoorspreadsd());
                
                //34
                assertEquals(newStation.getVesselcount(), oldStation.getCountofvessels());
                
                List<CatchsampleType> newcatches = newStation.getCatchsample();
                List<BioticTypes.v1_4.CatchsampleType> oldcatches = oldStation.getCatchsample();
                for (int k = 0; k < newcatches.size(); k++) {
                    CatchsampleType newCatch = newcatches.get(k);
                    BioticTypes.v1_4.CatchsampleType oldCatch = oldcatches.get(k);

                    //37
                    assertEquals(newCatch.getCatchpartnumber(), oldCatch.getSamplenumber());
                    
                    //17
                    assertEquals(newCatch.getCatchcategory(), oldCatch.getSpecies());

                    //18
                    assertEquals(newCatch.getCatchproducttype(), oldCatch.getProducttype().getValue());

                    //19
                    assertEquals(newCatch.getCatchweight(), oldCatch.getWeight());

                    //20
                    assertEquals(newCatch.getCatchvolume(), oldCatch.getVolume());

                    //21
                    assertEquals(newCatch.getCatchcount(), oldCatch.getCount());

                    //22
                    assertEquals(newCatch.getCatchcomment(), oldCatch.getComment());

                    //10
                    assertEquals(newCatch.getCommonname(), oldCatch.getNoname());
                    //11
                    if (oldCatch.getGenetics() == null) {
                        assertEquals(null, newCatch.getTissuesample());
                    } else {
                        assertEquals(newCatch.getTissuesample(), oldCatch.getGenetics().getValue());
                    }

                    List<IndividualType> newIndividuals = newCatch.getIndividual();
                    List<BioticTypes.v1_4.IndividualType> oldIndividuals = oldCatch.getIndividual();

                    for (int l = 0; l < newIndividuals.size(); l++) {
                        IndividualType newInd = newIndividuals.get(l);
                        BioticTypes.v1_4.IndividualType oldInd = oldIndividuals.get(l);
                        
                        //36
                        assertEquals(newInd.getSpecimenid(), oldInd.getSpecimenno());

                        //13
                        assertEquals(newInd.getLengthresolution(), oldInd.getLengthunit().getValue());

                        //18
                        if (newInd.getIndividualproducttype() == null) {
                            assertEquals(oldInd.getProducttype(), null);
                        } else {
                            assertEquals(newInd.getIndividualproducttype(), oldInd.getProducttype().getValue());
                        }

                        //19
                        assertEquals(newInd.getIndividualweight(), oldInd.getWeight());

                        //20
                        assertEquals(newInd.getIndividualvolume(), oldInd.getVolume());

                        //22
                        assertEquals(newInd.getIndividualcomment(), oldInd.getComment());

                        //14
                        if (oldInd.getStage() == null) {
                            assertEquals(newInd.getMaturationstage(), null);
                        } else {
                            assertEquals(newInd.getMaturationstage(), oldInd.getStage().getValue());
                        }

                        //11
                        assertEquals(newInd.getTissuesamplenumber(), oldInd.getGeneticsnumber());
                        
                        //35
                        assertEquals(newInd.getVertebraecount(), oldInd.getVertebrae());

                        if (!newInd.getAgedetermination().isEmpty()) {
                            AgedeterminationType newAge = newInd.getAgedetermination().get(0);

                            checkedAge = true;
                        }

                        for (int a = 0; a < newInd.getAgedetermination().size(); a++) {
                            AgedeterminationType newAge = newInd.getAgedetermination().get(a);
                            BioticTypes.v1_4.AgedeterminationType oldAge = oldInd.getAgedetermination().get(a);
                            //25
                            assertEquals(oldAge.getNo(), newAge.getAgedeterminationid());
                        }

                        for (int a = 0; a < newInd.getTag().size(); a++) {
                            TagType newAge = newInd.getTag().get(a);
                            BioticTypes.v1_4.TagType oldAge = oldInd.getTag().get(a);
                            //25
                            assertEquals(oldAge.getTagno(), newAge.getTagid());
                        }

                    }
                }
            }
        }
        assertTrue(checkedAge);
    }

    @Test
    /**
     * Test that there are as many prey samples in new format as it was in the
     * old.
     */
    public void testPreyConversionAllThere() throws Exception {
        Biotic1Handler b1handler = new Biotic1Handler();
        BioticTypes.v1_4.MissionsType missionsBiotic1 = b1handler.read(Biotic3HandlerTest.class.getClassLoader().getResourceAsStream("ecosurvey.xml"));
        Biotic3Handler instance = new Biotic3Handler();
        MissionsType result = instance.convertBiotic1(missionsBiotic1);

        int preyCountOld = 0;
        int preyCountNew = 0;

        for (MissionType m : result.getMission()) {
            for (FishstationType f : m.getFishstation()) {
                for (CatchsampleType c : f.getCatchsample()) {
                    for (IndividualType i : c.getIndividual()) {
                        preyCountNew += i.getPrey().size();
                    }
                }
            }
        }

        for (BioticTypes.v1_4.MissionType m : missionsBiotic1.getMission()) {
            for (BioticTypes.v1_4.FishstationType f : m.getFishstation()) {
                for (BioticTypes.v1_4.CatchsampleType c : f.getCatchsample()) {
                    preyCountOld += c.getPrey().size();
                }
            }
        }

        assertTrue(preyCountNew > 0);
        assertEquals(preyCountNew, preyCountOld);
    }

    @Test
    public void testPreyConversionCorrectlyMapped() throws Exception {
        Biotic1Handler b1handler = new Biotic1Handler();
        BioticTypes.v1_4.MissionsType missionsBiotic1 = b1handler.read(Biotic3HandlerTest.class.getClassLoader().getResourceAsStream("ecosurvey.xml"));
        Biotic3Handler instance = new Biotic3Handler();
        MissionsType result = instance.convertBiotic1(missionsBiotic1);

        boolean checkedPrey = false;
        List<MissionType> newMissions = result.getMission();
        List<BioticTypes.v1_4.MissionType> oldMissions = missionsBiotic1.getMission();
        for (int i = 0; i < newMissions.size(); i++) {
            MissionType newMission = newMissions.get(i);
            BioticTypes.v1_4.MissionType oldMission = oldMissions.get(i);

            List<FishstationType> newStations = newMission.getFishstation();
            List<BioticTypes.v1_4.FishstationType> oldStations = oldMission.getFishstation();
            for (int j = 0; j < newStations.size(); j++) {
                FishstationType newStation = newStations.get(j);
                BioticTypes.v1_4.FishstationType oldStation = oldStations.get(j);

                List<CatchsampleType> newcatches = newStation.getCatchsample();
                List<BioticTypes.v1_4.CatchsampleType> oldcatches = oldStation.getCatchsample();
                for (int k = 0; k < newcatches.size(); k++) {
                    CatchsampleType newCatch = newcatches.get(k);
                    BioticTypes.v1_4.CatchsampleType oldCatch = oldcatches.get(k);

                    for (IndividualType newInd : newCatch.getIndividual()) {
                        for (PreyType newPrey : newInd.getPrey()) {
                            checkedPrey = true;
                            //find matching prey in old:
                            boolean found = false;
                            for (BioticTypes.v1_4.PreyType oldPrey : oldCatch.getPrey()) { //17
                                if (oldPrey.getSpecies().equals(newPrey.getPreycategory()) && oldPrey.getFishno().equals(newInd.getSpecimenid()) && oldPrey.getPartno().equals(newPrey.getPreypartnumber())) {
                                    found = true;
                                }
                            }
                            assertTrue(found);
                        }
                    }
                }
            }
        }
        assertTrue(checkedPrey);
    }

    public void testPreyLength() throws Exception {
        Biotic1Handler b1handler = new Biotic1Handler();
        BioticTypes.v1_4.MissionsType missionsBiotic1 = b1handler.read(Biotic3HandlerTest.class.getClassLoader().getResourceAsStream("ecosurvey.xml"));
        Biotic3Handler instance = new Biotic3Handler();
        MissionsType result = instance.convertBiotic1(missionsBiotic1);

        boolean checkedPreyLength = false;
        List<MissionType> newMissions = result.getMission();
        List<BioticTypes.v1_4.MissionType> oldMissions = missionsBiotic1.getMission();
        for (int i = 0; i < newMissions.size(); i++) {
            MissionType newMission = newMissions.get(i);
            BioticTypes.v1_4.MissionType oldMission = oldMissions.get(i);

            List<FishstationType> newStations = newMission.getFishstation();
            List<BioticTypes.v1_4.FishstationType> oldStations = oldMission.getFishstation();
            for (int j = 0; j < newStations.size(); j++) {
                FishstationType newStation = newStations.get(j);
                BioticTypes.v1_4.FishstationType oldStation = oldStations.get(j);

                List<CatchsampleType> newcatches = newStation.getCatchsample();
                List<BioticTypes.v1_4.CatchsampleType> oldcatches = oldStation.getCatchsample();
                for (int k = 0; k < newcatches.size(); k++) {
                    CatchsampleType newCatch = newcatches.get(k);
                    BioticTypes.v1_4.CatchsampleType oldCatch = oldcatches.get(k);

                    for (IndividualType newInd : newCatch.getIndividual()) {
                        for (PreyType newPrey : newInd.getPrey()) {
                            BioticTypes.v1_4.PreyType oldPrey = null;
                            for (BioticTypes.v1_4.PreyType oldPreyC : oldCatch.getPrey()) {
                                if (oldPreyC.getFishno().equals(newInd.getSpecimenid()) && oldPreyC.getPartno().equals(newPrey.getPreypartnumber())) {
                                    oldPrey = oldPreyC;
                                }
                            }
                            assertFalse(oldPrey == null);
                            assertEquals(oldPrey.getPreylength().size(), newPrey.getPreylengthfrequencytable().size());

                            List<BigInteger> numbers = new ArrayList<>();
                            for (PreylengthType preyLength : newPrey.getPreylengthfrequencytable()) {
                                checkedPreyLength = true;
                                assertFalse(numbers.contains(preyLength.getPreylengthid()));
                                numbers.add(preyLength.getPreylengthid());
                                assertTrue(numbers.contains(preyLength.getPreylengthid()));
                            }

                            //21
                            for (int preyindex = 0; i < newPrey.getPreylengthfrequencytable().size(); i++) {
                                assertEquals(newPrey.getPreylengthfrequencytable().get(preyindex).getLengthintervalcount(), oldPrey.getPreylength().get(preyindex).getCount());
                                assertEquals(newPrey.getPreylengthfrequencytable().get(preyindex).getLengthintervalstart(), oldPrey.getPreylength().get(preyindex).getLength());
                            }

                            //21
                            for (int preyindex = 0; i < newPrey.getCopepodedevstagefrequencytable().size(); i++) {
                                assertEquals(newPrey.getCopepodedevstagefrequencytable().get(preyindex).getDevstagecount(), oldPrey.getCopepodedevstage().get(preyindex).getCount());
                                assertEquals(newPrey.getCopepodedevstagefrequencytable().get(preyindex).getCopepodedevstage(), oldPrey.getCopepodedevstage().get(preyindex).getCopepodedevstage());
                            }

                            //23
                            assertEquals(newPrey.getPreydigestion(), oldPrey.getDigestion().getValue());
                            //24
                            assertEquals(newPrey.getPreylengthmeasurement(), oldPrey.getLengthmeasurement().getValue());
                        }
                    }
                }
            }
        }
        assertTrue(checkedPreyLength);
    }

    @Test
    public void testKeys() throws Exception {
        Biotic1Handler b1handler = new Biotic1Handler();
        BioticTypes.v1_4.MissionsType missionsBiotic1 = b1handler.read(Biotic3HandlerTest.class.getClassLoader().getResourceAsStream("ecosurvey.xml"));
        Biotic3Handler instance = new Biotic3Handler();
        MissionsType result = instance.convertBiotic1(missionsBiotic1);

        Set<String> missionKeys = new HashSet<>();

        for (MissionType m : result.getMission()) {
            assertNotNull(m.getMissiontype());
            assertNotNull(m.getStartyear());
            assertNotNull(m.getPlatform());
            assertNotNull(m.getMissionnumber());

            String missionkeystring = m.getMissiontype() + "/" + m.getStartyear() + "/" + m.getPlatform() + "/" + m.getMissionnumber();
            assertFalse(missionKeys.contains(missionkeystring));
            missionKeys.add(missionkeystring);

            Set<String> stationKeys = new HashSet<>();
            for (FishstationType f : m.getFishstation()) {
                assertNotNull(f.getSerialnumber());

                String stationkeystring = "" + f.getSerialnumber();
                assertFalse(stationKeys.contains(stationkeystring));
                stationKeys.add(stationkeystring);

                Set<String> catchKeys = new HashSet<>();
                for (CatchsampleType c : f.getCatchsample()) {
                    assertNotNull(c.getCatchsampleid());

                    String catchkeystring = c.getCatchcategory() + "/" + c.getCatchpartnumber();
                    assertFalse(catchKeys.contains(catchkeystring));
                    catchKeys.add(catchkeystring);

                    Set<String> individualKeys = new HashSet<>();
                    for (IndividualType i : c.getIndividual()) {
                        assertNotNull(i.getSpecimenid());

                        String individualkeystring = "" + i.getSpecimenid();
                        assertFalse(individualKeys.contains(individualkeystring));
                        individualKeys.add(individualkeystring);

                        Set<String> ageKeys = new HashSet<>();
                        for (AgedeterminationType a : i.getAgedetermination()) {
                            assertNotNull(a.getAgedeterminationid());

                            String agekeystring = "" + a.getAgedeterminationid();
                            assertFalse(ageKeys.contains(agekeystring));
                            ageKeys.add(agekeystring);
                        }

                        Set<String> tagKeys = new HashSet<>();
                        for (TagType t : i.getTag()) {
                            assertNotNull(t.getTagid());

                            String tagKeyString = "" + t.getTagid();
                            assertFalse(tagKeys.contains(tagKeyString));
                            tagKeys.add(tagKeyString);
                        }

                        Set<String> preyKeys = new HashSet<>();
                        for (PreyType p : i.getPrey()) {
                            assertNotNull(p.getPreysampleid());

                            String preyKeyString = p.getPreycategory() + "/" + p.getPreypartnumber();
                            assertFalse(preyKeys.contains(preyKeyString));
                            preyKeys.add(preyKeyString);

                            Set<String> preylengthKeys = new HashSet<>();
                            for (PreylengthType pl : p.getPreylengthfrequencytable()) {
                                assertNotNull(pl.getPreylengthid());

                                String plkeystring = "" + pl.getPreylengthid();
                                assertFalse(preylengthKeys.contains(plkeystring));
                                preylengthKeys.add(plkeystring);
                            }

                            Set<String> ccpKeys = new HashSet<>();
                            for (CopepodedevstageType cp : p.getCopepodedevstagefrequencytable()) {
                                assertNotNull(cp.getCopepodedevstage());

                                String cpkeystring = "" + cp.getCopepodedevstage();
                                assertFalse(ccpKeys.contains(cpkeystring));
                                ccpKeys.add(cpkeystring);
                            }
                        }
                    }
                }
            }
        }

    }

    @Test
    public void testPreyConversionMissingIndividuals() throws Exception {
        Biotic1Handler b1handler = new Biotic1Handler();
        BioticTypes.v1_4.MissionsType missionsBiotic1 = b1handler.read(Biotic3HandlerTest.class.getClassLoader().getResourceAsStream("dummy_bad_ind_key.xml"));
        Biotic3Handler instance = new Biotic3Handler();
        try {
            instance.convertBiotic1(missionsBiotic1);
            fail("Exception expected");
        } catch (BioticConversionException e) {

        }
    }

    @Test
    public void testKeyCheckNull() throws Exception {
        Biotic1Handler b1handler = new Biotic1Handler();
        BioticTypes.v1_4.MissionsType missionsBiotic1 = b1handler.read(Biotic3HandlerTest.class.getClassLoader().getResourceAsStream("test.xml"));
        Biotic3Handler instance = new Biotic3Handler();
        MissionsType m = instance.convertBiotic1(missionsBiotic1);
        m.getMission().get(0).setStartyear(null);
        try {
            instance.checkKeys(m);
            fail("Exception expected");
        } catch (BioticConversionException bce) {

        }
    }

    @Test
    public void testKeyCheckDupl() throws Exception {
        Biotic1Handler b1handler = new Biotic1Handler();
        BioticTypes.v1_4.MissionsType missionsBiotic1 = b1handler.read(Biotic3HandlerTest.class.getClassLoader().getResourceAsStream("test.xml"));
        Biotic3Handler instance = new Biotic3Handler();
        MissionsType m = instance.convertBiotic1(missionsBiotic1);
        m.getMission().get(0).getFishstation().get(1).setSerialnumber(m.getMission().get(0).getFishstation().get(0).getSerialnumber());
        try {
            instance.checkKeys(m);
            fail("Exception expected");
        } catch (BioticConversionException bce) {

        }
    }
}
