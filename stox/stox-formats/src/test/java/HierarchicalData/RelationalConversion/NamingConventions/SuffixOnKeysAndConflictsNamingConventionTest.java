/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HierarchicalData.RelationalConversion.NamingConventions;

import HierarchicalData.RelationalConversion.RelationalConvertionException;
import XMLHandling.SchemaReader;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
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
public class SuffixOnKeysAndConflictsNamingConventionTest {

    public SuffixOnKeysAndConflictsNamingConventionTest() {
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
     * Test of getTableName method, of class
     * SuffixOnKeysAndConflictsNamingConvention.
     */
    @Test
    public void testGetTableName() throws Exception {
        System.out.println("getTableName");
        String typeName = "FishstationType";
        String tableName = "Station";
        String tableNameColumns = "St";
        Map<String, String> typemap = new HashMap<>();
        typemap.put(typeName, tableName);
        Map<String, String> columnmap = new HashMap<>();
        columnmap.put(typeName, tableNameColumns);
        SuffixOnKeysAndConflictsNamingConvention instance = new SuffixOnKeysAndConflictsNamingConvention(new SchemaReader(SuffixOnKeysAndConflictsNamingConventionTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), columnmap, typemap);
        String result = instance.getTableName(typeName);
        assertEquals(tableName, result);
    }

    /**
     * Test of getTableName method, of class
     * SuffixOnKeysAndConflictsNamingConvention.
     */
    @Test
    public void testWrongTypes() throws Exception {
        System.out.println("constructor");
        String typeName = "FishStation";
        String tableName = "Station";
        String tableNameColumns = "St";
        Map<String, String> typemap = new HashMap<>();
        typemap.put(typeName, tableName);
        Map<String, String> columnmap = new HashMap<>();
        columnmap.put(typeName, tableNameColumns);
        try {
            SuffixOnKeysAndConflictsNamingConvention instance = new SuffixOnKeysAndConflictsNamingConvention(new SchemaReader(SuffixOnKeysAndConflictsNamingConventionTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), columnmap, typemap);
            fail("Exception expected");
        } catch (RelationalConvertionException e) {

        }

    }

    /**
     * Test of getTableName method, of class
     * SuffixOnKeysAndConflictsNamingConvention.
     */
    @Test
    public void testInconsistentTypes() throws Exception {
        System.out.println("constructor");
        String typeName = "FishstationType";
        String tableName = "Station";
        String tableNameColumns = "St";
        Map<String, String> typemap = new HashMap<>();
        typemap.put(typeName, tableName);
        Map<String, String> columnmap = new HashMap<>();
        columnmap.put("FishStation", tableNameColumns);
        try {
            SuffixOnKeysAndConflictsNamingConvention instance = new SuffixOnKeysAndConflictsNamingConvention(new SchemaReader(SuffixOnKeysAndConflictsNamingConventionTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), columnmap, typemap);
            fail("Exception expected");
        } catch (RelationalConvertionException e) {

        }

    }

    /**
     * Test of getTypeName method, of class
     * SuffixOnKeysAndConflictsNamingConvention.
     */
    @Test
    public void testGetTypeName() throws RelationalConvertionException, JAXBException, ParserConfigurationException, ITableMakerNamingConvention.NamingException {
        String typeName = "FishstationType";
        String tableName = "Station";
        String tableNameColumns = "St";
        Map<String, String> typemap = new HashMap<>();
        typemap.put(typeName, tableName);
        Map<String, String> columnmap = new HashMap<>();
        columnmap.put(typeName, tableNameColumns);
        SuffixOnKeysAndConflictsNamingConvention instance = new SuffixOnKeysAndConflictsNamingConvention(new SchemaReader(SuffixOnKeysAndConflictsNamingConventionTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), columnmap, typemap);
        String result = instance.getTypeName(tableName);
        assertEquals(typeName, result);
    }

    /**
     * Test of getColumnName method, of class
     * SuffixOnKeysAndConflictsNamingConvention.
     */
    @Test
    public void testGetColumnNameKeysAndNonKey() throws Exception {
        System.out.println("getTableName");
        String typeName = "FishstationType";
        String tableName = "Station";
        String tableNameColumns = "St";
        Map<String, String> typemap = new HashMap<>();
        typemap.put(typeName, tableName);
        Map<String, String> columnmap = new HashMap<>();
        columnmap.put(typeName, tableNameColumns);
        SuffixOnKeysAndConflictsNamingConvention instance = new SuffixOnKeysAndConflictsNamingConvention(new SchemaReader(SuffixOnKeysAndConflictsNamingConventionTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), columnmap, typemap);
        String result = instance.getColumnName(typeName, "serialno");
        assertEquals("serialno" + instance.getSeparator() + tableNameColumns, result);
        result = instance.getColumnName(typeName, "trawquality");
        assertEquals("trawquality", result);
    }

    /**
     * Test of getColumnName method, of class
     * SuffixOnKeysAndConflictsNamingConvention.
     */
    @Test
    public void testGetColumnNameConflictAndNonConflict() throws Exception {
        System.out.println("getTableName");
        String typeName = "FishstationType";
        String tableName = "Station";
        String tableNameColumns = "St";
        Map<String, String> typemap = new HashMap<>();
        typemap.put(typeName, tableName);
        Map<String, String> columnmap = new HashMap<>();
        columnmap.put(typeName, tableNameColumns);
        SuffixOnKeysAndConflictsNamingConvention instance = new SuffixOnKeysAndConflictsNamingConvention(new SchemaReader(SuffixOnKeysAndConflictsNamingConventionTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), columnmap, typemap);
        String result = instance.getColumnName(typeName, "platform");
        assertEquals("platform" + instance.getSeparator() + tableNameColumns, result);
        result = instance.getColumnName(typeName, "trawquality");
        assertEquals("trawquality", result);
    }
    
        /**
     * Test of getColumnName method, of class
     * SuffixOnKeysAndConflictsNamingConvention.
     */
    @Test
    public void testGetColumnNameConflictAndNonConflictDefaultConstr() throws Exception {
        System.out.println("getTableName");
        String typeName = "FishstationType";
        String tableName = "Fishstation";
        String tableNameColumns = "Fishstation";
        Map<String, String> typemap = new HashMap<>();
        typemap.put(typeName, tableName);
        Map<String, String> columnmap = new HashMap<>();
        columnmap.put(typeName, tableNameColumns);
        SuffixOnKeysAndConflictsNamingConvention instance = new SuffixOnKeysAndConflictsNamingConvention(new SchemaReader(SuffixOnKeysAndConflictsNamingConventionTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")));
        String result = instance.getColumnName(typeName, "platform");
        assertEquals("platform" + instance.getSeparator() + tableNameColumns, result);
        result = instance.getColumnName(typeName, "trawquality");
        assertEquals("trawquality", result);
    }

    /**
     * Test of getNodeName method, of class
     * SuffixOnKeysAndConflictsNamingConvention.
     */
    @Test
    public void testGetNodeName() throws Exception {
        System.out.println("getNodeName");
        String typeName = "FishstationType";
        String tableName = "Station";
        String tableNameColumns = "St";
        Map<String, String> typemap = new HashMap<>();
        typemap.put(typeName, tableName);
        Map<String, String> columnmap = new HashMap<>();
        columnmap.put(typeName, tableNameColumns);
        SuffixOnKeysAndConflictsNamingConvention instance = new SuffixOnKeysAndConflictsNamingConvention(new SchemaReader(SuffixOnKeysAndConflictsNamingConventionTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), columnmap, typemap);
        String result = instance.getNodeName(tableName, "platform" + instance.getSeparator() + tableNameColumns);
        assertEquals("platform", result);
        result = instance.getNodeName(tableName, "trawquality");
        assertEquals("trawquality", result);
    }

    /**
     * Test of getDescription method, of class
     * SuffixOnKeysAndConflictsNamingConvention.
     */
    @Test
    public void testGetDescription() throws RelationalConvertionException, JAXBException, ParserConfigurationException {
        System.out.println("getDescription");
        SuffixOnKeysAndConflictsNamingConvention instance = new SuffixOnKeysAndConflictsNamingConvention(new SchemaReader(SuffixOnKeysAndConflictsNamingConventionTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), new HashMap<>(), new HashMap<>());
        String result = instance.getDescription();
        assertTrue(result.length() > 0);
    }

}
