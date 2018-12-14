/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.acoustic;

import java.util.List;
import java.util.stream.Stream;
import no.imr.sea2data.echosounderbo.DistanceBO;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
public class PgNapesTest {

    @Test
    public void test() {
        String path = "E:\\SigbjørnMehl\\2004212\\luf5";
        String fileName = "E:\\SigbjørnMehl\\2004212\\luf5\\2004212.xml";
        List<DistanceBO> dist = ReadAcousticXML.perform(fileName);
        
//        PgNapesEchoWriter.export("2004212", "58", "12", path, fileNameOut, dist, "HER", 1d, 50d);

        Stream.of("HER"/*, "HAD"*/).forEach(species -> {
            //PgNapesEchoWriter.export1("2004212", "58", "12", path, species, dist, species, 1d, 10d);
            PgNapesEchoWriter.export2("2004212", "58", "12", path, "Table", dist, species, 1d, 10d, null, null, false);
        });
    }
}
