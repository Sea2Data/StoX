/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package XMLHandling;

import java.io.File;
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
public class InfoTest {
    
    public InfoTest() {
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
     * Test of getNamespace method, of class Info.
     */
    @Test
    public void testGetNamespace() throws Exception {
        System.out.println("getNamespace");
        File xml = new File(InfoTest.class.getClassLoader().getResource("test.xml").toURI());
        String expResult = "http://www.imr.no/formats/nmdbiotic/v1.4";
        String result = Info.getNamespace(xml);
        assertEquals(expResult, result);
    }

    /**
     * Test of getEncoding method, of class Info.
     */
    @Test
    public void testGetEncoding() throws Exception {
        System.out.println("getEncoding");
        File xml = new File(InfoTest.class.getClassLoader().getResource("test.xml").toURI());
        String expResult = "UTF-8";
        String result = Info.getEncoding(xml);
        assertEquals(expResult, result);
    }
    
}
