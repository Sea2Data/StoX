/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HierarchicalData.RelationalConversion;

import BioticTypes.v1_4.FishstationType;
import BioticTypes.v1_4.MissionsType;
import HierarchicalData.IO;
import XMLHandling.SchemaReader;
import java.util.List;
import java.util.Map;
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
public class MergedTableMakerTest {

    public MergedTableMakerTest() {
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
     * Test of getParentColumnNames method, of class MergedTableMaker.
     */
    @Test
    public void testGetParentColumnNames() throws Exception {
        System.out.println("getParentColumnNames");
        MissionsType missions = IO.parse(MergedTableMakerTest.class.getClassLoader().getResourceAsStream("test.xml"), MissionsType.class);
        FishstationType st = missions.getMission().get(0).getFishstation().get(0);

        ILeafNodeHandler bioticHandler = new TableMakerTest.BioticHandler();
        MergedTableMaker flatBioticTableMaker = new MergedTableMaker(new SchemaReader(MergedTableMakerTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), bioticHandler);
        flatBioticTableMaker.setNamingConvention(new TableMakerTest.DummyNamingConvention());

        List<String> result = flatBioticTableMaker.getParentColumnNames(st.getParent());
        List<String> headers = flatBioticTableMaker.getHeaders(missions.getMission().get(0));
        assertEquals(headers, result);
    }

    /**
     * Test of getParentColumnValues method, of class MergedTableMaker.
     */
    @Test
    public void testGetParentColumnValues() throws Exception {
        System.out.println("getParentColumnValues");
        MissionsType missions = IO.parse(MergedTableMakerTest.class.getClassLoader().getResourceAsStream("test.xml"), MissionsType.class);
        FishstationType st = missions.getMission().get(0).getFishstation().get(0);

        ILeafNodeHandler bioticHandler = new TableMakerTest.BioticHandler();
        MergedTableMaker flatBioticTableMaker = new MergedTableMaker(new SchemaReader(MergedTableMakerTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), bioticHandler);
        flatBioticTableMaker.setNamingConvention(new TableMakerTest.DummyNamingConvention());

        List<String> result = flatBioticTableMaker.getParentColumnValues(st.getParent());
        List<String> headers = flatBioticTableMaker.getRow(missions.getMission().get(0));
        assertEquals(headers, result);
    }

    /**
     * Test of getHeaders method, of class MergedTableMaker.
     */
    @Test
    public void testGetHeaders() throws Exception {
        System.out.println("getHeaders");
        MissionsType missions = IO.parse(MergedTableMakerTest.class.getClassLoader().getResourceAsStream("test.xml"), MissionsType.class);
        FishstationType st = missions.getMission().get(0).getFishstation().get(0);

        ILeafNodeHandler bioticHandler = new TableMakerTest.BioticHandler();
        TableMaker flatBioticTableMaker = new MergedTableMaker(new SchemaReader(MergedTableMakerTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), bioticHandler);
        flatBioticTableMaker.setNamingConvention(new TableMakerTest.DummyNamingConvention());

        List<String> result = flatBioticTableMaker.getRow(st);
        List<String> headers = flatBioticTableMaker.getHeaders(st);
        assertTrue(headers.size() == result.size());
    }

    /**
     * Test of getRow method, of class MergedTableMaker.
     */
    @Test
    public void testGetRow() throws Exception {
        System.out.println("getRow");
        MissionsType missions = IO.parse(MergedTableMakerTest.class.getClassLoader().getResourceAsStream("test.xml"), MissionsType.class);
        FishstationType st = missions.getMission().get(0).getFishstation().get(0);

        ILeafNodeHandler bioticHandler = new TableMakerTest.BioticHandler();
        TableMaker flatBioticTableMaker = new MergedTableMaker(new SchemaReader(MergedTableMakerTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), bioticHandler);
        flatBioticTableMaker.setNamingConvention(new TableMakerTest.DummyNamingConvention());

        TableMaker bioticTableMaker = new TableMaker(new SchemaReader(MergedTableMakerTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), bioticHandler);
        bioticTableMaker.setNamingConvention(new TableMakerTest.DummyNamingConvention());

        List<String> result = flatBioticTableMaker.getRow(st);
        List<String> headers = flatBioticTableMaker.getHeaders(st);
        assertTrue(headers.size() == result.size());

        List<String> result_nonflat_st = bioticTableMaker.getRow(st);
        List<String> result_nonflat_m = bioticTableMaker.getRow(missions.getMission().get(0));
        assertTrue(result.size() > result_nonflat_st.size());
        assertTrue(result.size() < result_nonflat_st.size() + result_nonflat_m.size());
        assertTrue(result.size() == result_nonflat_st.size() + result_nonflat_m.size() - bioticTableMaker.keys.get("MissionType").size());
    }

    /**
     * Test of getAllTables method, of class MergedTableMaker.
     */
    @Test
    public void testGetAllTables() throws Exception {
        System.out.println("getAllTables");

        MissionsType root = IO.parse(MergedTableMakerTest.class.getClassLoader().getResourceAsStream("test.xml"), MissionsType.class);

        ILeafNodeHandler bioticHandler = new TableMakerTest.BioticHandler();
        MergedTableMaker flatBioticTableMaker = new MergedTableMaker(new SchemaReader(MergedTableMakerTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), bioticHandler);
        flatBioticTableMaker.setNamingConvention(new TableMakerTest.DummyNamingConvention());

        TableMaker bioticTableMaker = new TableMaker(new SchemaReader(MergedTableMakerTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), bioticHandler);
        bioticTableMaker.setNamingConvention(new TableMakerTest.DummyNamingConvention());

        Map<String, List<List<String>>> resultFlat = flatBioticTableMaker.getAllTables(root);
        Map<String, List<List<String>>> resultNonFlat = bioticTableMaker.getAllTables(root);
        for (String ct: resultNonFlat.keySet()){
            System.out.print(ct);
            assertTrue(resultFlat.get(ct).size() == resultNonFlat.get(ct).size());
            assertTrue(ct.equals("TableFromTypeMissionsType") || ct.equals("TableFromTypeMissionType") || resultFlat.get(ct).get(0).size() > resultNonFlat.get(ct).get(0).size());
        }
    }

}
