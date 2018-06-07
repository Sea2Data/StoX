/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package temp;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.stox.functions.acoustic.AcousticConverter;
import no.imr.stox.functions.acoustic.ListUser20Writer;
import no.imr.stox.functions.acoustic.ReadAcousticLUF5;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
//@Ignore
public class Luf5FolderToXML {

    //@Test
    public void test2() {
        String fName = "C:/Temp/luf.txt";
        List<DistanceBO> res = ReadAcousticLUF5.perform(fName, null);
        ListUser20Writer.export("Test", null, null, fName + ".xml", res);
    }

    @Test
    public void test() {
        AcousticConverter.convertLUF5DirToLuf20("E:\\SigbjørnMehl\\2010704\\luf");
//        AcousticConverter.convertLUF5DirToLuf20("E:\\Data\\luf5\\luf5files");
        //AcousticConverter.convertLUF5FileToLuf20("E:\\Data\\luf5\\luf5files\\ListUserFile05.F017986_T1_R10_L7245.0-7999.9", null);
        //AcousticConverter.convertLUF5FileToLuf20("E:\\Data\\luf5\\luf5files\\ListUserFile05.F017986_T1_R10_L7245.0-7999.9", "E:\\Data\\luf5\\luf5files\\ListUserFile05.F017986_T1_R10_L7245.0-7999.xml");
        //AcousticConverter.convertLUF20FileToLuf5Files("E:\\Data\\luf5\\luf5files\\2000208.xml");
    }
}
