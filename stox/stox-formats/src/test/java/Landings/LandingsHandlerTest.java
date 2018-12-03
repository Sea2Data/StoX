/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Landings;

import BioticTypes.v1_4.MissionsType;
import LandingsTypes.v2.LandingsdataType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Edvin Fuglebakk edvin.fuglebakk@imr.no
 */
public class LandingsHandlerTest {
    
    public LandingsHandlerTest() {
    }

    @Test
    public void testRead() throws Exception{
        System.out.println("readLanding");
        LandingsHandler h = new LandingsHandler();
        InputStream xml = LandingsHandlerTest.class.getClassLoader().getResourceAsStream("landinger_100_lines.xml");
        LandingsdataType landings = h.read(xml);
        System.out.println(landings.getSeddellinje().get(0).getArtKode());
        assertEquals(landings.getSeddellinje().get(0).getSisteFangstdato().getYear(), 2015);
    }
    
        @Test
    public void testSaveBiotic() throws Exception {
        System.out.println("saveLanding");
        InputStream xml = LandingsHandlerTest.class.getClassLoader().getResourceAsStream("landinger_100_lines.xml");
        boolean acceptBiotic1 = true;
        LandingsHandler instance = new LandingsHandler();
        LandingsdataType result = instance.read(xml);
        assertTrue(result.getSeddellinje().size() > 0);
        xml.close();

        File temp = File.createTempFile("landing_example", ".tmp");
        temp.deleteOnExit();
        instance.save(new FileOutputStream(temp), result);

        InputStream re = new FileInputStream(temp);
        LandingsdataType result_re = instance.read(re);

        assertEquals(result.getSeddellinje().size(), result_re.getSeddellinje().size());
    }
}
