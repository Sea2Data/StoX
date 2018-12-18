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
        //String fileName = path + "2004212.xml";
        //PgNapesIO.convertLuf20ToPgNapes(fileName);
        PgNapesIO.convertPgNapesToLuf20(path, "Acoustic", "-Luf20");
        /*String fileName = path + "/" + "2004212-Luf20.xml";
        PgNapesIO.convertLuf20ToPgNapes(fileName, "Acoustic2");
        PgNapesIO.convertPgNapesToLuf20(path, "Acoustic2", "-Luf20(2)");*/
    }
}
