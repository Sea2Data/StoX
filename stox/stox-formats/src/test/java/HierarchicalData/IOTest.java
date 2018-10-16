/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HierarchicalData;

import XMLHandling.NamespaceFilter;
import BioticTypes.v1_4.MissionsType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashSet;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xml.sax.XMLFilter;

/**
 *
 * @author Edvin Fuglebakk edvin.fuglebakk@imr.no
 */
public class IOTest {

    public IOTest() {
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
     * Test of parse method, of class IO.
     */
    @Test
    public void testParse() throws Exception {
        System.out.println("parse");
        MissionsType result = IO.parse(IOTest.class.getClassLoader().getResourceAsStream("test.xml"), MissionsType.class);
        assertTrue(result.getMission().get(0).getFishstation().size() > 0);
    }

    /**
     * Test of parse method, of class IO.
     */
    @Test
    public void testFilter() throws Exception {
        System.out.println("parse with filter");

        Set<String> comp = new HashSet<>();
        comp.add("http://www.imr.no/formats/nmdbiotic/v1");
        XMLFilter filter = new NamespaceFilter("http://www.imr.no/formats/nmdbiotic/v1.4", comp);

        MissionsType result = IO.parse(IOTest.class.getClassLoader().getResourceAsStream("test_v1.xml"), MissionsType.class, filter);
        TestDependencies.BioticTypes.v1.MissionsType resultOld = IO.parse(IOTest.class.getClassLoader().getResourceAsStream("test_v1.xml"), TestDependencies.BioticTypes.v1.MissionsType.class);
        assertEquals(result.getMission().get(0).getFishstation().size(), resultOld.getMission().get(0).getFishstation().size());
    }

    @Test
    public void testFilterFail() throws Exception {
        System.out.println("parse with filter");

        Set<String> comp = new HashSet<>();
        comp.add("http://www.imr.no/formats/nmdbiotic/v1.1");
        XMLFilter filter = new NamespaceFilter("http://www.imr.no/formats/nmdbiotic/v1.4", comp);

        try {
            MissionsType result = IO.parse(IOTest.class.getClassLoader().getResourceAsStream("test_v1.xml"), MissionsType.class, filter);
            fail("Exception expected");
        } catch (Exception e) {

        }
    }

    /**
     * Test of save method, of class IO.
     */
    @Test
    public void testSave() throws Exception {
        System.out.println("save");
        MissionsType result = IO.parse(IOTest.class.getClassLoader().getResourceAsStream("test.xml"), MissionsType.class);
        String cs = "Not a real callsignal";
        result.getMission().get(0).setCallsignal(cs);

        File temp = File.createTempFile("biotic_example", ".tmp");
        temp.deleteOnExit();
        IO.save(new FileOutputStream(temp), result);

        MissionsType reRead = IO.parse(new FileInputStream(temp), MissionsType.class);
        String csRead = reRead.getMission().get(0).getCallsignal();

        assertEquals(cs, csRead);
    }

}
