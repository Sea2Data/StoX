/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HierarchicalData.RelationalConversion.NamingConventions;

import HierarchicalData.RelationalConversion.NamingConventions.DoNothingNamingConvention;
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
public class DoNothingNamingConventionTest {
    
    public DoNothingNamingConventionTest() {
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
     * Test of getTableName method, of class DoNothingNamingConvention.
     */
    @Test
    public void testGetTableName() throws Exception {
        System.out.println("getTableName");
        String typeName = "Table";
        DoNothingNamingConvention instance = new DoNothingNamingConvention();
        String expResult = "Table";
        String result = instance.getTableName(typeName);
        assertEquals(expResult, result);
    }

    /**
     * Test of getTypeName method, of class DoNothingNamingConvention.
     */
    @Test
    public void testGetTypeName() {
        System.out.println("getTypeName");
        String tableName = "Type";
        DoNothingNamingConvention instance = new DoNothingNamingConvention();
        String expResult = "Type";
        String result = instance.getTypeName(tableName);
        assertEquals(expResult, result);
    }

    /**
     * Test of getColumnName method, of class DoNothingNamingConvention.
     */
    @Test
    public void testGetColumnName() throws Exception {
        System.out.println("getColumnName");
        String typename = "Type";
        String nodeName = "Column";
        DoNothingNamingConvention instance = new DoNothingNamingConvention();
        String expResult = "Column";
        String result = instance.getColumnName(typename, nodeName);
        assertEquals(expResult, result);
    }

    /**
     * Test of getNodeName method, of class DoNothingNamingConvention.
     */
    @Test
    public void testGetNodeName() throws Exception {
        System.out.println("getNodeName");
        String tablename = "Table";
        String fieldname = "Node";
        DoNothingNamingConvention instance = new DoNothingNamingConvention();
        String expResult = "Node";
        String result = instance.getNodeName(tablename, fieldname);
        assertEquals(expResult, result);
    }

    /**
     * Test of getDescription method, of class DoNothingNamingConvention.
     */
    @Test
    public void testGetDescription() {
        System.out.println("getDescription");
        DoNothingNamingConvention instance = new DoNothingNamingConvention();
        String result = instance.getDescription();
        assertTrue(result.length()>0);
    }
    
}
