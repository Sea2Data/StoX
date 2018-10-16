/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Biotic.Biotic1;

import Biotic.Biotic3.Biotic3Handler;
import Biotic.BioticConversionException;
import BioticTypes.v1_4.MissionsType;
import BioticTypes.v1_4.ObjectFactory;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import javax.xml.datatype.XMLGregorianCalendar;
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
public class Biotic1HandlerTest {

    public Biotic1HandlerTest() {
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

    @Test
    public void testRead() throws Exception {
        Biotic1Handler r = new Biotic1Handler();
        MissionsType m = r.read(Biotic1HandlerTest.class.getClassLoader().getResourceAsStream("test.xml"));
        assertTrue(m.getMission().get(0).getFishstation().size() > 0);
    }

    @Test
    public void testReadComp() throws Exception {
        Biotic1Handler r = new Biotic1Handler();
        MissionsType m = r.read(Biotic1HandlerTest.class.getClassLoader().getResourceAsStream("test_v1.xml"));
        assertTrue(m.getMission().get(0).getFishstation().size() > 0);
    }

    @Test
    public void testReadGarbage() {
        try {
            Biotic1Handler r = new Biotic1Handler();
            MissionsType m = r.read(Biotic1HandlerTest.class.getClassLoader().getResourceAsStream("biotic1_4.xsd.xml"));
            fail("Exception expected!");
        } catch (Exception e) {

        }
    }

    @Test
    public void testReadSave() throws Exception {
        ObjectFactory f = new ObjectFactory();
        Biotic1Handler r = new Biotic1Handler();

        File temp = File.createTempFile("biotic_example", ".tmp");
        temp.deleteOnExit();
        r.save(new FileOutputStream(temp), f.createMissionsType());

    }

    protected void testConvertBiotic3BackAndForth(String testfile) throws Exception {
        System.out.println("test convert back and forth");
        // read biotic as 1
        Biotic1Handler r = new Biotic1Handler();
        MissionsType m = r.read(Biotic1HandlerTest.class.getClassLoader().getResourceAsStream(testfile));

        // save to temp
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        r.save(baos, m);
        String m1 = baos.toString();

        //read as 3
        Biotic3Handler r3 = new Biotic3Handler();
        BioticTypes.v3.MissionsType b3 = r3.readOldBiotic(Biotic1HandlerTest.class.getClassLoader().getResourceAsStream(testfile));

        // convert back
        MissionsType mConverted = r.convertBiotic3(b3);
        ByteArrayOutputStream baos3 = new ByteArrayOutputStream();
        r.save(baos3, mConverted);
        String m3 = baos3.toString();

        //check equality
        assertEquals(m1, m3);
    }
    
        @Test
    public void testConvertBiotic3() throws Exception {
        testConvertBiotic3BackAndForth("test.xml");//test w station, catch, individuals and age
        testConvertBiotic3BackAndForth("minimaltest.xml"); //test nulls
    }
    
    @Test
    public void testConvertBioticTwoYears() throws Exception {
        System.out.println("test convert mission spanning two years");
        // read biotic as 1
        Biotic1Handler r = new Biotic1Handler();
        MissionsType m = r.read(Biotic1HandlerTest.class.getClassLoader().getResourceAsStream("test.xml"));
        
        //read as 3
        Biotic3Handler r3 = new Biotic3Handler();
        BioticTypes.v3.MissionsType b3 = r3.readOldBiotic(Biotic1HandlerTest.class.getClassLoader().getResourceAsStream("test.xml"));
        int year = b3.getMission().get(0).getMissionstopdate().getYear();
        XMLGregorianCalendar stopdate = b3.getMission().get(0).getMissionstopdate();
        stopdate.setYear(year+1);
        b3.getMission().get(0).setMissionstopdate(stopdate);
        
        try{
            MissionsType mConverted = r.convertBiotic3(b3);
            fail("Exception expected");
        } catch(BioticConversionException bce){
            
        }

    }

    @Test
    public void testKeyCheckNull() throws Exception {
        Biotic1Handler instance = new Biotic1Handler();
        BioticTypes.v1_4.MissionsType m = instance.read(Biotic1HandlerTest.class.getClassLoader().getResourceAsStream("test.xml"));
        
        m.getMission().get(0).setYear(null);
        try {
            instance.checkKeys(m);
            fail("Exception expected");
        } catch (BioticConversionException bce) {

        }
    }

    @Test
    public void testKeyCheckDupl() throws Exception {
        Biotic1Handler instance = new Biotic1Handler();
        BioticTypes.v1_4.MissionsType m = instance.read(Biotic1HandlerTest.class.getClassLoader().getResourceAsStream("test.xml"));
        m.getMission().get(0).getFishstation().get(1).setSerialno(m.getMission().get(0).getFishstation().get(0).getSerialno());
        try {
            instance.checkKeys(m);
            fail("Exception expected");
        } catch (BioticConversionException bce) {

        }
    }

}
