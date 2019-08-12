/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.acoustic;

import no.imr.stox.util.base.Conversion;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
public class PgNapesTest {

    @Test
    public void test() {
        String path = "E:\\SigbjørnMehl\\2008623";
        String fileName = path + "/" + "echo-2008623-append.xml";
        //PgNapesIO.convertLuf20ToPgNapes(fileName, "Acoustic");
        PgNapesIO.convertPgNapesToLuf20(path, "Acoustic", "-Luf20");
        /*fileName = path + "/" + "2007203-Luf20.xml";
        PgNapesIO.convertLuf20ToPgNapes(fileName, "Acoustic2");
        PgNapesIO.convertPgNapesToLuf20(path, "Acoustic2", "-Luf20(2)");
*/
    }
}
