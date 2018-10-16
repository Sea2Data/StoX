/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HierarchicalData;

import XMLHandling.SchemaReader;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import DocumentationTypes.ImrDocType;
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
public class SchemaReaderTest {
    
    public SchemaReaderTest() {
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
     * Test of getComplextypes method, of class SchemaReader.
     */
    @Test
    public void testGetComplextypes() throws JAXBException, ParserConfigurationException {
        System.out.println("getComplextypes");
        SchemaReader instance = new SchemaReader(SchemaReaderTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd"));
        List<String> result = instance.getComplextypes();
        assertTrue(result.contains("MissionType"));
        assertTrue(result.contains("FishstationType"));
        assertTrue(result.contains("AgedeterminationType"));
    }

    /**
     * Test of getNodes method, of class SchemaReader.
     */
    @Test
    public void testGetNodes() throws JAXBException, ParserConfigurationException {
        System.out.println("getNodes");
        SchemaReader instance = new SchemaReader(SchemaReaderTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd"));
        List<SchemaReader.SchemaNode> resultFs = instance.getNodes("FishstationType");
        assertTrue(resultFs.contains(new SchemaReader.SchemaNode("platform", "StringDescriptionType")));
        assertTrue(resultFs.contains(new SchemaReader.SchemaNode("catchsample", "catchsampleType")));
        assertTrue(resultFs.contains(new SchemaReader.SchemaNode("serialno", "integer")));
        assertTrue(resultFs.contains(new SchemaReader.SchemaNode("fishingdepthmin", "decimal")));
        
        List<SchemaReader.SchemaNode> resultA = instance.getNodes("TagType");
        assertTrue(resultA.contains(new SchemaReader.SchemaNode("tagtype", "StringDescriptionType")));
        assertTrue(resultA.contains(new SchemaReader.SchemaNode("tagno", "integer")));
    }

    /**
     * Test of getKeys method, of class SchemaReader.
     */
    @Test
    public void testGetKeys() throws JAXBException, ParserConfigurationException {
        System.out.println("getKeys");
        String complexType = "FishstationType";
        SchemaReader instance = new SchemaReader(SchemaReaderTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd"));
        Set<String> expResult = new HashSet<>();
        expResult.add("serialno");
        Set<String> result = instance.getKeys(complexType);
        assertEquals(expResult, result);
    }

    /**
     * Test of getDocumentation method, of class SchemaReader.
     */
    @Test
    public void testGetDocumentation() throws JAXBException, ParserConfigurationException {
        System.out.println("getDocumentation");
        SchemaReader instance = new SchemaReader(SchemaReaderTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd"));
        Map<String, ImrDocType> result = instance.getDocumentation("FishstationType");
        assertTrue(result.get("serialno").getDescription().size()>0);
        assertTrue(result.get("platform").getDescription().size()>0);
        assertEquals(result.get("platform").getReferenceTable().getHref(), "http://tomcat7.imr.no:8080/apis/nmdapi/reference/v1/platform");
    }
    
        @Test
    public void testTargetNameSpace() throws JAXBException, ParserConfigurationException {
        System.out.println("getTargetNameSpace");
        SchemaReader instance = new SchemaReader(SchemaReaderTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd"));
        String result = instance.getTargetNameSpace();
        assertEquals(result, "http://www.imr.no/formats/nmdbiotic/v1.4");
    }
    
    
}
