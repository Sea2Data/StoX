/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package temp;

import java.io.File;
import java.util.List;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.stox.functions.acoustic.ListUser20Writer;
import no.imr.stox.functions.acoustic.ReadAcousticLUF5;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
@Ignore
public class Luf5FolderToXML {

    @Test
    public void test2() {
        String fName = "C:/Temp/luf.txt";
        List<DistanceBO> res = ReadAcousticLUF5.perform(fName, null);
        ListUser20Writer.export("Test", null, null, fName + ".xml", res);
    }

    //@Test
    public void test() {
        String fold = "E:\\Data\\2009\\2009202\\luf";
        String cruise = "2009202";

        File folder = new File(fold);
        List<DistanceBO> res = null;
        for (final File fileEntry : folder.listFiles()) {
            String fName = fileEntry.getPath();
            if (!fName.endsWith(".txt")) {
                continue;
            }
            res = ReadAcousticLUF5.perform(fName, res);
        }
        ListUser20Writer.export(cruise, null, null, fold + "/" + cruise + ".xml", res);
    }
}
