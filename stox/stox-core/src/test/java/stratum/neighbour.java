/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stratum;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
//@Ignore
public class neighbour {

    @Test
    public void test() {
        try {
            List<String> lines = FileUtils.readLines(new File("F:/Gjert/neihbour1.txt"));
            boolean hdr = false;
            String stratum = null;
            for (String line : lines) {
                if (hdr = !hdr) {
                    stratum = line.split(" ")[2];
                } else {
                    System.out.println(stratum + "\t" + line.replace(' ', ','));
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(neighbour.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
