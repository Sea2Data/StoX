/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Landings;

import LandingsTypes.v2.LandingsdataType;
import LandingsTypes.v2.SeddellinjeType;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Edvin Fuglebakk edvin.fuglebakk@imr.no
 */
public class FilterLandingsTest {
    
    public FilterLandingsTest() {
    }

    /**
     * Test of readLandingsSpecies method, of class FilterLandings.
     */
    @Test
    public void testReadLandingsSpecies() throws Exception {
        
        Set<String> retainspecies = new HashSet<>();
        retainspecies.add("102202");
        retainspecies.add("102701");

        Set<String> retainspecies2 = new HashSet<>();
        retainspecies.add("102202");
        
        Set<String> retainspecies3 = new HashSet<>();
        retainspecies.add("102701");
        
        System.out.println("readLandingsSpecies");
        InputStream xml = FilterLandingsTest.class.getClassLoader().getResourceAsStream("landinger_100_lines.xml");
        FilterLandings instance = new FilterLandings();
        
        LandingsdataType result2 = instance.readLandingsSpecies(retainspecies2, xml);
        for (SeddellinjeType s : result2.getSeddellinje()) {
            assertEquals("102202", s.getArtKode());
        }
        xml.close();
        xml = FilterLandingsTest.class.getClassLoader().getResourceAsStream("landinger_100_lines.xml");
        LandingsdataType result3 = instance.readLandingsSpecies(retainspecies3, xml);
        for (SeddellinjeType s : result3.getSeddellinje()) {
            assertEquals("102701", s.getArtKode());
        }
        xml.close();
        xml = FilterLandingsTest.class.getClassLoader().getResourceAsStream("landinger_100_lines.xml");
        LandingsdataType result = instance.readLandingsSpecies(retainspecies, xml);
        for (SeddellinjeType s : result.getSeddellinje()) {
            assertTrue(s.getArtKode().equals("102202") || s.getArtKode().equals("102701"));
        }
        xml.close();
        xml = FilterLandingsTest.class.getClassLoader().getResourceAsStream("landinger_100_lines.xml");
        assertTrue(result3.getSeddellinje().size()<result.getSeddellinje().size());
        assertTrue(result2.getSeddellinje().size()<result.getSeddellinje().size());
        
    }

    /**
     * Test of readBasic method, of class FilterLandings.
     */
    @Test
    public void testReadBasic() throws Exception {
        System.out.println("readBasic");
        InputStream xml = FilterLandingsTest.class.getClassLoader().getResourceAsStream("landinger_100_lines.xml");
        FilterLandings instance = new FilterLandings();
        LandingsdataType result = instance.readBasic(xml);
        assertEquals(result.getSeddellinje().size(), 99);
        assertNull(result.getSeddellinje().get(0).getDellanding());
        assertNotNull(result.getSeddellinje().get(0).getProdukt());
    }

    
}
