/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HierarchicalData;

import XMLHandling.NamespaceFilter;
import BioticTypes.v1_4.MissionsType;
import java.util.HashSet;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.UnmarshallerHandler;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xml.sax.InputSource;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

/**
 *
 * @author Edvin Fuglebakk edvin.fuglebakk@imr.no
 */
public class NamespaceFilterTest {

    public NamespaceFilterTest() {
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
    public void testParse() throws Exception {
        System.out.println("parse actual namespace");

        Set<String> comp = new HashSet<>();
        comp.add("http://www.imr.no/formats/nmdbiotic/v1.1");
        XMLFilter filter = new NamespaceFilter("http://www.imr.no/formats/nmdbiotic/v1.4", comp);

        // Set the parent XMLReader on the XMLFilter
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        SAXParser sp = spf.newSAXParser();
        XMLReader xr = sp.getXMLReader();
        filter.setParent(xr);

        JAXBContext jc = JAXBContext.newInstance(MissionsType.class);

        // Set UnmarshallerHandler as ContentHandler on XMLFilter
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        UnmarshallerHandler unmarshallerHandler = unmarshaller
                .getUnmarshallerHandler();
        filter.setContentHandler(unmarshallerHandler);

        InputSource xml = new InputSource(NamespaceFilterTest.class.getClassLoader().getResourceAsStream("test.xml"));
        filter.parse(xml);
        MissionsType toplevel = (MissionsType) unmarshallerHandler.getResult();

        assertTrue(toplevel.getMission().size() > 0);
    }

    @Test
    public void testParseComp() throws Exception {
        System.out.println("parse compatible");

        Set<String> comp = new HashSet<>();
        comp.add("http://www.imr.no/formats/nmdbiotic/v1");
        XMLFilter filter = new NamespaceFilter("http://www.imr.no/formats/nmdbiotic/v1.4", comp);

        // Set the parent XMLReader on the XMLFilter
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        SAXParser sp = spf.newSAXParser();
        XMLReader xr = sp.getXMLReader();
        filter.setParent(xr);

        JAXBContext jc = JAXBContext.newInstance(MissionsType.class);

        // Set UnmarshallerHandler as ContentHandler on XMLFilter
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        UnmarshallerHandler unmarshallerHandler = unmarshaller
                .getUnmarshallerHandler();
        filter.setContentHandler(unmarshallerHandler);

        InputSource xml = new InputSource(NamespaceFilterTest.class.getClassLoader().getResourceAsStream("test_v1.xml"));
        filter.parse(xml);
        MissionsType toplevel = (MissionsType) unmarshallerHandler.getResult();

        assertTrue(toplevel.getMission().size() > 0);
    }

    @Test
    public void testParseGarbage() throws Exception {
        System.out.println("parse actually not compatible (and not labelled to be).");

        Set<String> comp = new HashSet<>();
        comp.add("http://www.imr.no/formats/nmdbiotic/v1.1");
        XMLFilter filter = new NamespaceFilter("http://www.imr.no/formats/nmdbiotic/v1.4", comp);

        // Set the parent XMLReader on the XMLFilter
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        SAXParser sp = spf.newSAXParser();
        XMLReader xr = sp.getXMLReader();
        filter.setParent(xr);

        JAXBContext jc = JAXBContext.newInstance(MissionsType.class);

        // Set UnmarshallerHandler as ContentHandler on XMLFilter
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        UnmarshallerHandler unmarshallerHandler = unmarshaller
                .getUnmarshallerHandler();
        filter.setContentHandler(unmarshallerHandler);

        InputSource xml = new InputSource(NamespaceFilterTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd"));
        try {
            filter.parse(xml);
            MissionsType toplevel = (MissionsType) unmarshallerHandler.getResult();
            fail("Exception expected");
        } catch (Exception e) {

        }

    }

    @Test
    public void testParseNotLabelledComp() throws Exception {
        System.out.println("parse not labelled compatible");

        Set<String> comp = new HashSet<>();
        comp.add("http://www.imr.no/formats/nmdbiotic/v1.1");
        XMLFilter filter = new NamespaceFilter("http://www.imr.no/formats/nmdbiotic/v1.4", comp);

        // Set the parent XMLReader on the XMLFilter
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setNamespaceAware(true);
        SAXParser sp = spf.newSAXParser();
        XMLReader xr = sp.getXMLReader();
        filter.setParent(xr);

        JAXBContext jc = JAXBContext.newInstance(MissionsType.class);

        // Set UnmarshallerHandler as ContentHandler on XMLFilter
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        UnmarshallerHandler unmarshallerHandler = unmarshaller
                .getUnmarshallerHandler();
        filter.setContentHandler(unmarshallerHandler);

        InputSource xml = new InputSource(NamespaceFilterTest.class.getClassLoader().getResourceAsStream("test_v1.xml"));
        try {
            filter.parse(xml);
            MissionsType toplevel = (MissionsType) unmarshallerHandler.getResult();
            fail("Exception expected");
        } catch (Exception e) {

        }

    }

    @Test
    public void testParseNotNsAware() throws Exception {
        System.out.println("parse not namespace aware");

        Set<String> comp = new HashSet<>();
        comp.add("http://www.imr.no/formats/nmdbiotic/v1.1");
        XMLFilter filter = new NamespaceFilter("http://www.imr.no/formats/nmdbiotic/v1.4", comp);

        // Set the parent XMLReader on the XMLFilter
        SAXParserFactory spf = SAXParserFactory.newInstance();
        SAXParser sp = spf.newSAXParser();
        XMLReader xr = sp.getXMLReader();
        filter.setParent(xr);

        JAXBContext jc = JAXBContext.newInstance(MissionsType.class);

        // Set UnmarshallerHandler as ContentHandler on XMLFilter
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        UnmarshallerHandler unmarshallerHandler = unmarshaller
                .getUnmarshallerHandler();
        filter.setContentHandler(unmarshallerHandler);

        InputSource xml = new InputSource(NamespaceFilterTest.class.getClassLoader().getResourceAsStream("test_v1.xml"));
        try {
            filter.parse(xml);
            MissionsType toplevel = (MissionsType) unmarshallerHandler.getResult();
            fail("Exception expected");
        } catch (Exception e) {

        }

    }

    @Test
    public void testCanFilter() throws Exception {
        System.out.println("canFilter");

        Set<String> comp = new HashSet<>();
        comp.add("http://www.imr.no/formats/nmdbiotic/v1.1");
        NamespaceFilter filter = new NamespaceFilter("http://www.imr.no/formats/nmdbiotic/v1.4", comp);

        assertTrue(filter.canFilter("http://www.imr.no/formats/nmdbiotic/v1.1"));
        assertTrue(filter.canFilter("http://www.imr.no/formats/nmdbiotic/v1.4"));
        assertFalse(filter.canFilter("http://www.imr.no/formats/nmdbiotic/v1"));
    }

}
