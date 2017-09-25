/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package temp;

import java.util.List;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.stox.functions.acoustic.ListUser20Writer;
import no.imr.stox.functions.acoustic.FilterAcoustic;
import no.imr.stox.functions.acoustic.ReadAcousticXML;
import org.junit.Ignore;

/**
 *
 * @author aasmunds
 */
@Ignore
public class ReadTobisXMLFilterAndExport {

    public static void main(String[] args) {
        String fName = "C:\\Data\\2014807\\ListUserFile20__L185.0-2774.9.txt";
        // Read the LUF20 file into distance list
        List<DistanceBO> dist = ReadAcousticXML.perform(fName);
        // Filter the distance list
        dist = FilterAcoustic.perform(dist, 38000, 2, 27, "P");
        // Export distance list to file
        ListUser20Writer.export("2014807", null, null, fName + ".out", dist);
    }
}
