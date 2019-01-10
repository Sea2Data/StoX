/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Landings;

import LandingsTypes.v2.LandingsdataType;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Edvin Fuglebakk edvin.fuglebakk@imr.no
 */
public class LandingsHandlerTest {

    public LandingsHandlerTest() {
    }

//    @Test
    public void testRead() throws Exception {
        System.out.println("readLanding");
        if (false) {
            File f = new File("E:\\Edvin\\2015\\2015_torsk.xml");
            BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream("E:\\Edvin\\2015\\2015_torsk.xml"), "8859_1"));
            File targetFile = new File("E:\\Edvin\\2015\\2015_torsk-utf8-2.xml");
            OutputStream outStream = new FileOutputStream(targetFile);
            char[] buffer = new char[8 * 10240];
            int bytesRead;
            int i = 0;
            while ((bytesRead = in.read(buffer)) != -1) {
                String s = new String(buffer);
                s = s.replace("><", ">\r\n<");
                outStream.write(s.getBytes(StandardCharsets.UTF_8));
                i++;
                if (i == 1000) {
                    outStream.close();
                    break;
                }
            }
            /*while (in.readLine() != null) {
            System.out.println(in.readLine());
        }*/
            in.close();
        } else {
            LandingsHandler h = new LandingsHandler();
            //InputStream xml = LandingsHandlerTest.class.getClassLoader().getResourceAsStream("landinger_100_lines.xml");
            File f = new File("E:\\Edvin\\2015\\2015_torsk-utf8-2.xml");
            LandingsdataType landings = h.read(f);
            assertNotNull(landings.getSeddellinje().get(0).getArtKode());
            assertEquals(landings.getSeddellinje().get(0).getSisteFangstdato().getYear(), 2015);
            assertEquals(landings.getSeddellinje().get(0).getProduksjon().getLandingsdato().getYear(), 2015);
        }
    }

    @Test
    public void testSaveLanding() throws Exception {
        System.out.println("saveLanding");
        InputStream xml = LandingsHandlerTest.class.getClassLoader().getResourceAsStream("landinger_100_lines.xml");

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
