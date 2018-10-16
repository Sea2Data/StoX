/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HierarchicalData;

import BioticTypes.v1_4.AgedeterminationType;
import BioticTypes.v1_4.FishstationType;
import BioticTypes.v1_4.IndividualType;
import BioticTypes.v1_4.MissionType;
import BioticTypes.v1_4.MissionsType;
import BioticTypes.v1_4.PreyType;
import java.io.IOException;
import java.util.List;
import javax.xml.bind.JAXBException;
import javax.xml.stream.XMLStreamException;
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
public class HierarchicalDataTest {
    
    public HierarchicalDataTest() {
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
     * Test of getParent method, of class HierarchicalData.
     */
    @Test
    public void testGetParent() throws JAXBException, IOException, XMLStreamException {
        System.out.println("getParent");
        MissionsType result = IO.parse(HierarchicalDataTest.class.getClassLoader().getResourceAsStream("test.xml"), MissionsType.class);
        MissionType m = result.getMission().get(0);
        assertTrue(m==m.getFishstation().get(0).getParent());
        assertTrue(result.getParent()==null);
    }
    
        /**
     * Test of getParent method, of class HierarchicalData.
     */
    @Test
    public void testgetChildren() throws JAXBException, IOException, XMLStreamException {
        System.out.println("getChildren root and mission");
        MissionsType result = IO.parse(HierarchicalDataTest.class.getClassLoader().getResourceAsStream("test.xml"), MissionsType.class);
        assert(result.getChildren() != null);
        
        MissionType m = result.getMission().get(0);
        
        List<HierarchicalData> children = m.getChildren();
        assertTrue(children.get(0) instanceof FishstationType);
        assertTrue(children.size() == m.getFishstation().size());
    }
    
            /**
     * Test of getParent method, of class HierarchicalData.
     */
    @Test
    public void testgetChildrenLeaf() throws JAXBException, IOException, XMLStreamException {
        System.out.println("getChildren leaf");
        MissionsType result = IO.parse(HierarchicalDataTest.class.getClassLoader().getResourceAsStream("test.xml"), MissionsType.class);
        assert(result.getChildren() != null);
        
        AgedeterminationType m = result.getMission().get(0).getFishstation().get(0).getCatchsample().get(0).getIndividual().get(0).getAgedetermination().get(0);
        assertTrue(m.getChildren()==null);
    }
    
                /**
     * Test of getParent method, of class HierarchicalData.
     */
    @Test
    public void testgetChildrenSplit() throws JAXBException, IOException, XMLStreamException {
        System.out.println("getChildren split");
        MissionsType result = IO.parse(HierarchicalDataTest.class.getClassLoader().getResourceAsStream("test.xml"), MissionsType.class);
        assert(result.getChildren() != null);
        
        List<IndividualType> i = result.getMission().get(0).getFishstation().get(0).getCatchsample().get(0).getIndividual();
        List<PreyType> p = result.getMission().get(0).getFishstation().get(0).getCatchsample().get(0).getPrey();
        List<HierarchicalData> c = result.getMission().get(0).getFishstation().get(0).getCatchsample().get(0).getChildren();
        assertTrue(c.size() == i.size() + p.size());
    }

    
}
