/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package convert;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.echosounderbo.FrequencyBO;
import no.imr.sea2data.echosounderbo.SABO;
import no.imr.stox.util.base.Conversion;
import no.imr.stox.util.base.IMRdate;
import no.imr.stox.functions.acoustic.ListUser20Writer;
import no.imr.stox.functions.acoustic.ReadAcousticLUF3;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
@Ignore
public class ConvertRuAcousticFiles {

    @Test
    // Converts russion acoustic format to luf20
    public void test() {
        // 31 = Torsk
        // 41 = Hyse
        List<DistanceBO> ac = new ArrayList<>();
        readRUFileWithChannels(ac, false, "F:\\SigbjørnMehl\\akustikk\\Cod Bottom-11880902.txt", 38000, "31");
        readRUFileWithChannels(ac, true, "F:\\SigbjørnMehl\\akustikk\\Cod Pelagic-11880902.txt", 38000, "31");
        readRUFileWithChannels(ac, false, "F:\\SigbjørnMehl\\akustikk\\Had Bottom-11881301.txt", 38000, "41");
        readRUFileWithChannels(ac, true, "F:\\SigbjørnMehl\\akustikk\\Had Pelagic-11881301.txt", 38000, "41");
        ListUser20Writer.export("2015-RU", "RU", "UANA", "F:\\SigbjørnMehl\\akustikk\\2015-RU.xml", ac);
        
        ac = ReadAcousticLUF3.perform("F:\\SigbjørnMehl\\akustikk\\Rus2016.luf3.txt");
        ListUser20Writer.export("2016-RU", "RU", "UANA", "F:\\SigbjørnMehl\\akustikk\\2016-RU.xml", ac);
    }

    void readRUFileWithChannels(List<DistanceBO> ac, Boolean pelagic, String fileName, Integer freq, String acoCat) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(fileName), Charset.defaultCharset());
            lines.remove(0);
            String[] s = lines.get(0).split("\t");
            String cruise = s[1];
            lines.remove(0);
            s = lines.get(0).split("\t");
            Double intDist = Conversion.safeStringtoDoubleNULL(s[1]);
            Double chThickness = Conversion.safeStringtoDoubleNULL(s[3]);
            lines.remove(0);
            lines.remove(0);
            for (int iL = 0; iL < lines.size(); iL++) {
                String l = lines.get(iL);
                s = l.split("\t");
                DistanceBO d;
                if (iL > ac.size() - 1) {
                    d = new DistanceBO();
                    ac.add(d);
                    d.setLog_start(Conversion.safeStringtoDoubleNULL(s[0]));
                    d.setStart_time(IMRdate.encodeDate(IMRdate.strToDate(s[1]), IMRdate.strToTime(s[2])));
                    d.setLat_start(Conversion.safeStringtoDoubleNULL(s[3]));
                    d.setLon_start(Conversion.safeStringtoDoubleNULL(s[4]));
                    d.setCruise(cruise);
                    d.setIntegrator_dist(intDist);
                } else {
                    d = ac.get(iL);
                }
                FrequencyBO f;
                if (d.getFrequencies().size() > 0) {
                    f = d.getFrequencies().get(0);
                } else {
                    f = new FrequencyBO();
                    f.setDistance(d);
                    d.getFrequencies().add(f);
                    f.setFreq(freq);
                    f.setTranceiver(Conversion.safeStringtoIntegerNULL(s[5]));
                }
                if (pelagic) {
                    d.setPel_ch_thickness(chThickness);
                    f.setNum_pel_ch(s.length - 9);
                } else {
                    d.setBot_ch_thickness(chThickness);
                    f.setNum_bot_ch(s.length - 9);
                }
                int ch = 1;
                for (int i = 9; i < s.length; i++) {
                    Double saVal = Conversion.safeStringtoDoubleNULL(s[i]);
                    if (saVal != null && saVal > 0d) {
                        SABO sabo = new SABO();
                        sabo.setFrequency(f);
                        f.getSa().add(sabo);
                        sabo.setAcoustic_category(acoCat);
                        sabo.setCh(ch);
                        sabo.setCh_type(pelagic ? "P" : "B");
                        sabo.setSa(saVal);
                    }
                    ch++;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ConvertRuAcousticFiles.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
