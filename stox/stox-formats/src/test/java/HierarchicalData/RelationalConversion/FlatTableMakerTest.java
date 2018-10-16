/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HierarchicalData.RelationalConversion;

import BioticTypes.v1_4.IndividualType;
import BioticTypes.v1_4.MissionsType;
import HierarchicalData.IO;
import HierarchicalData.RelationalConversion.NamingConventions.ITableMakerNamingConvention;
import XMLHandling.SchemaReader;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tests flat structure with Individuals assumed to have non-repeating age
 * readings and tags.
 *
 * @author Edvin Fuglebakk edvin.fuglebakk@imr.no
 */
public class FlatTableMakerTest {

    protected List<IndividualType> individuals;
    protected TableMaker flattablemaker;

    public FlatTableMakerTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws JAXBException, XMLStreamException, ParserConfigurationException {
        MissionsType missions = IO.parse(FlatTableMakerTest.class.getClassLoader().getResourceAsStream("test.xml"), MissionsType.class);
        this.individuals = missions.getMission().get(0).getFishstation().get(0).getCatchsample().get(0).getIndividual();
        assert this.individuals.size() > 3;
        this.flattablemaker = new FlatTableMaker(new SchemaReader(FlatTableMakerTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), new TableMakerTest.BioticHandler());
        this.flattablemaker.setNamingConvention(new TableMakerTest.DummyNamingConvention());
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getHeadersDownWards method, of class FlatTableMaker.
     */
    @Test
    public void testGetHeaders() throws Exception {
        System.out.println("getHeaders");
        List<String> headers = this.flattablemaker.getHeaders(this.individuals.get(0));
        Set<String> headersSet = new HashSet<>(headers);
        assertEquals(headers.size(), headersSet.size());
        assertTrue(headersSet.size() > 0);

        ITableMakerNamingConvention nc = this.flattablemaker.getNamingConvention();
        assertTrue(headers.contains(nc.getColumnName("FishstationType", "serialno")));
        assertTrue(headers.contains(nc.getColumnName("MissionType", "year")));
        assertTrue(headers.contains(nc.getColumnName("AgedeterminationType", "age")));
        assertFalse(headers.contains(nc.getColumnName("FishstationType", "platform")));

    }

    @Test
    public void testGetHeadersException() throws Exception {
        System.out.println("getHeadersException");
        MissionsType missions = IO.parse(FlatTableMakerTest.class.getClassLoader().getResourceAsStream("test.xml"), MissionsType.class);
        try {
            this.flattablemaker.getHeaders(missions.getMission().get(0).getFishstation().get(0));
            fail("Exception expected");
        } catch (RelationalConvertionException e) {

        }
    }

    /**
     * Test of getRowDownwards method, of class FlatTableMaker.
     */
    @Test
    public void testGetRow() throws Exception {
        System.out.println("getRow");
        List<String> headers = this.flattablemaker.getHeaders(this.individuals.get(0));
        List<String> row = this.flattablemaker.getRow(this.individuals.get(0));

        assertEquals(headers.size(), row.size());
        assertFalse(headers.equals(row));

        ITableMakerNamingConvention nc = this.flattablemaker.getNamingConvention();

        try {
            Integer.parseInt(row.get(headers.indexOf(nc.getColumnName("FishstationType", "serialno"))));
            Integer.parseInt(row.get(headers.indexOf(nc.getColumnName("AgedeterminationType", "age"))));
        } catch (Exception e) {
            e.printStackTrace();
            fail("Formatting problems, when looking up by header name");
        }
        try {
            Integer.parseInt(row.get(headers.indexOf(nc.getColumnName("IndividualType", "length"))));
            fail("Exception expected");
        } catch (Exception e) {

        }
    }

    @Test
    public void testGetRowException() throws Exception {
        System.out.println("getRowException");
        MissionsType missions = IO.parse(FlatTableMakerTest.class.getClassLoader().getResourceAsStream("test.xml"), MissionsType.class);
        try {
            this.flattablemaker.getRow(missions.getMission().get(0).getFishstation().get(0));
            fail("Exception expected");
        } catch (RelationalConvertionException e) {

        }
    }

    @Test
    public void testGetTableContents() throws Exception {
        System.out.println("getTableContents");
        List<String> headers = this.flattablemaker.getHeaders(this.individuals.get(0));
        List<List<String>> content = this.flattablemaker.getTableContent(this.individuals);

        assertEquals(content.size(), this.individuals.size());
        assertEquals(content.get(0).size(), headers.size());

    }
    
    @Test
    public void testAllTables() throws Exception {
        System.out.println("getTableContents");
        ITableMakerNamingConvention nc = this.flattablemaker.getNamingConvention();
        List<String> headers = this.flattablemaker.getHeaders(this.individuals.get(0));
        List<List<String>> content = this.flattablemaker.getTableContent(this.individuals);
        Map<String, List<List<String>>> allTables = this.flattablemaker.getAllTables(this.individuals.get(0).getParent());
        List<List<String>> tabContent = allTables.get(nc.getTableName(this.individuals.get(0).getParent().getClass().getSimpleName()));
        List<String> tabHeader = tabContent.remove(0);
        assertEquals(tabContent, content);
        assertEquals(tabHeader, headers);
        
        assertEquals(allTables.keySet().size(), 1);
    }

}
