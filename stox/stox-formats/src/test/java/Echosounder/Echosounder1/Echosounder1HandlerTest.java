/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Echosounder.Echosounder1;

import EchoSounderTypes.v1.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
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
public class Echosounder1HandlerTest {
    
    public Echosounder1HandlerTest() {
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
     * Test of read method, of class Echosounder1Handler.
     */
    @Test
    public void testRead() throws Exception {
        System.out.println("read");
        InputStream xml = Echosounder1HandlerTest.class.getClassLoader().getResourceAsStream("echotest.xml");
        Echosounder1Handler instance = new Echosounder1Handler();
        EchosounderDatasetType result = instance.read(xml);
        assertTrue(result.getLsssVersion()!=null);
    }

    @Test
    public void testReadGarbage() {
        try {
            Echosounder1Handler r = new Echosounder1Handler();
            EchosounderDatasetType m = r.read(Echosounder1HandlerTest.class.getClassLoader().getResourceAsStream("biotic1_4.xsd.xml"));
            fail("Exceptione expected!");
        } catch (Exception e) {

        }
    }

    @Test
    public void testReadSave() throws Exception {
        ObjectFactory f = new ObjectFactory();
        Echosounder1Handler r = new Echosounder1Handler();

        File temp = File.createTempFile("echosounder_example", ".tmp");
        temp.deleteOnExit();
        r.save(new FileOutputStream(temp), f.createEchosounderDatasetType());
    }
    
}
