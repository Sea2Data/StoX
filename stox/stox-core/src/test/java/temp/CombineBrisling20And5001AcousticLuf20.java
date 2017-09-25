/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package temp;

import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.echosounderbo.FrequencyBO;
import no.imr.sea2data.echosounderbo.SABO;
import no.imr.stox.exception.UserErrorException;
import no.imr.stox.functions.acoustic.ListUser20Writer;
import no.imr.stox.functions.acoustic.ReadAcousticXML;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
//@Ignore
public class CombineBrisling20And5001AcousticLuf20 {

    @Test
    public void test() {
        try {
            String fName = "C:\\Users\\aasmunds\\workspace\\stox\\project\\Brislingtokt2015\\input\\acoustic\\ListUserFile20__L107.0-1689.9.txt";
            // Read the LUF20 file into distance list
            List<DistanceBO> dist = ReadAcousticXML.perform(fName);
            for (DistanceBO d : dist) {
                for (FrequencyBO f : d.getFrequencies()) {
                    Map<Integer, SABO> sa5001 = new HashMap<>();
                    Map<Integer, SABO> sa20 = new HashMap<>();
                    for (SABO s : f.getSa()) {
                        if (s.getCh_type().equals("B")) {
                            continue;
                        }
                        switch (s.getAcoustic_category()) {
                            case "5001":
                                sa5001.put(s.getCh(), s);
                                break;
                            case "20":
                                sa20.put(s.getCh(), s);
                                break;
                        }
                    }
                    // Append 5001 to 20 list
                    for (SABO s : sa5001.values()) {
                        SABO s1 = sa20.get(s.getCh());
                        if (s1 == null) {
                            s1 = new SABO(f, s);
                            s1.setCh(s.getCh());
                            s1.setAcoustic_category("20");
                            sa20.put(s.getCh(), s1);
                        } else {
                            s1.setSa(s1.getSa() + s.getSa());
                        }
                    }
                    // Add the sa values
                    f.getSa().clear();
                    List<SABO> newlist = sa20.entrySet().stream()
                            .sorted(Comparator.comparing(Map.Entry::getKey))
                            .map(e -> e.getValue())
                            .collect(Collectors.toList());
                    f.getSa().addAll(newlist);
                }
            }
            // Export distance list to file
            ListUser20Writer.export("2015625", "578", "1002", fName + ".out", dist);
        } catch (UserErrorException ex) {
            Logger.getLogger(CombineBrisling20And5001AcousticLuf20.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
