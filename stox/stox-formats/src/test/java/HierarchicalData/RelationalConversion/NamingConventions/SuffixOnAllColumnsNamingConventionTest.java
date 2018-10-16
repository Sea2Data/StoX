/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HierarchicalData.RelationalConversion.NamingConventions;

import HierarchicalData.RelationalConversion.NamingConventions.SuffixOnAllColumnsNamingConvention;
import HierarchicalData.RelationalConversion.NamingConventions.ITableMakerNamingConvention.NamingException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

/**
 *
 * @author Edvin Fuglebakk edvin.fuglebakk@imr.no
 */
public class SuffixOnAllColumnsNamingConventionTest {

    public SuffixOnAllColumnsNamingConventionTest() {
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
     * Test of getTableName method, of class SuffixOnAllColumnsNamingConvention.
     */
    @org.junit.Test
    public void testGetTableName() throws Exception {
        System.out.println("getTableName");
        String typeName = "TableType";
        SuffixOnAllColumnsNamingConvention instance = new SuffixOnAllColumnsNamingConvention();
        String expResult = "Table";
        String result = instance.getTableName(typeName);
        assertEquals(expResult, result);
    }

    /**
     * Test of getTableName method, of class SuffixOnAllColumnsNamingConvention.
     */
    @org.junit.Test
    public void testGetTableNameError() throws Exception {
        System.out.println("getTableName");
        String typeName = "TableTyp";
        SuffixOnAllColumnsNamingConvention instance = new SuffixOnAllColumnsNamingConvention();
        String expResult = "Table";
        try {
            instance.getTableName(typeName);
            fail("Exception expected");
        } catch (NamingException ne) {

        }
    }

    /**
     * Test of getTypeName method, of class SuffixOnAllColumnsNamingConvention.
     */
    @org.junit.Test
    public void testGetTypeName() {
        System.out.println("getTypeName");
        String tableName = "Table";
        SuffixOnAllColumnsNamingConvention instance = new SuffixOnAllColumnsNamingConvention();
        String expResult = "TableType";
        String result = instance.getTypeName(tableName);
        assertEquals(expResult, result);
    }

    /**
     * Test of getColumnName method, of class SuffixOnAllColumnsNamingConvention.
     */
    @org.junit.Test
    public void testGetColumnName() throws Exception {
        System.out.println("getColumnName");
        String typename = "TableType";
        String nodeName = "node";
        SuffixOnAllColumnsNamingConvention instance = new SuffixOnAllColumnsNamingConvention();
        String expResult = "node.Table";
        String result = instance.getColumnName(typename, nodeName);
        assertEquals(expResult, result);
    }

    /**
     * Test of getColumnName method, of class SuffixOnAllColumnsNamingConvention.
     */
    @org.junit.Test
    public void testGetColumnNameError() throws Exception {
        System.out.println("getColumnName");
        String typename = "Table";
        String nodeName = "node";
        SuffixOnAllColumnsNamingConvention instance = new SuffixOnAllColumnsNamingConvention();
        String expResult = "node.Table";
        try {
            instance.getColumnName(typename, nodeName);
            fail("Exception expected");
        } catch (NamingException ne) {

        }

    }

    /**
     * Test of getNodeName method, of class SuffixOnAllColumnsNamingConvention.
     */
    @org.junit.Test
    public void testGetNodeName() throws Exception {
        System.out.println("getNodeName");
        String tablename = "Table";
        String columnname = "node.Table";
        SuffixOnAllColumnsNamingConvention instance = new SuffixOnAllColumnsNamingConvention();
        String expResult = "node";
        String result = instance.getNodeName(tablename, columnname);
        assertEquals(expResult, result);
    }

    /**
     * Test of getNodeName method, of class SuffixOnAllColumnsNamingConvention.
     */
    @org.junit.Test
    public void testGetNodeNameError() throws Exception {
        System.out.println("getNodeName");
        String tablename = "Table";
        String columnname = "node";
        SuffixOnAllColumnsNamingConvention instance = new SuffixOnAllColumnsNamingConvention();
        String expResult = "node";
        try {
            instance.getNodeName(tablename, columnname);
            fail("Exception expected");
        } catch (NamingException ne) {

        }
    }

    /**
     * Test of getDescription method, of class SuffixOnAllColumnsNamingConvention.
     */
    @org.junit.Test
    public void testGetDescription() {
        System.out.println("getDescription");
        SuffixOnAllColumnsNamingConvention instance = new SuffixOnAllColumnsNamingConvention();
        String expResult = "";
        String result = instance.getDescription();
        assertTrue(result.length()>0);
    }

}
