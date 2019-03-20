/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package convert;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.echosounderbo.FrequencyBO;
import no.imr.sea2data.echosounderbo.SABO;
import no.imr.stox.functions.acoustic.ListUser20Writer;
import no.imr.stox.functions.acoustic.ReadAcousticXML;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
//@Ignore
public class ConvertLUF {

    class LogPos {

        Double log;
        Double lat;
        Double lon;

        public LogPos(Double log, Double lat, Double lon) {
            this.log = log;
            this.lat = lat;
            this.lon = lon;
        }

        public Double getLog() {
            return log;
        }

        public Double getLat() {
            return lat;
        }

        public Double getLon() {
            return lon;
        }

    }

    List<LogPos> getCorrection() {
        try {
            List<String> l = Files.readAllLines(Paths.get("E:\\SigbjørnMehl\\2008202\\rutepos.txt"));
            l.remove(0); // hdr
            return l.stream()
                    .map(s -> {
                        String str[] = s.split("\t");
                        return new LogPos(Double.valueOf(str[0]), Double.valueOf(str[1]), Double.valueOf(str[2]));
                    })
                    .collect(Collectors.toList());
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    DistanceBO getDistance(List<DistanceBO> dist, Double log) {
        return dist.stream()
                .filter(d -> Double.valueOf(d.getLog_start().doubleValue()).equals(log))
                .findFirst()
                .orElse(null);
    }

    //@Test
    public void convert() {
        String fileName = "E:\\SigbjørnMehl\\2008202\\ListUserFile20__L1660.0-5539.0.txt";
        List<DistanceBO> dist = ReadAcousticXML.perform(fileName);
        getCorrection().stream().forEach(lp -> {
            DistanceBO d = getDistance(dist, lp.getLog());
            d.setLat_start(lp.getLat());
            d.setLat_stop(lp.getLat());
            d.setLon_start(lp.getLon());
            d.setLon_stop(lp.getLon());
        });
        ListUser20Writer.export("2008202", "58", "1019", "E:\\SigbjørnMehl\\2008202\\echosounder_cruiseNumber_2008202_Johan+Hjort-2.xml", dist);
    }

    @Test
    public void convertBubble() {
        convertBubble("E:\\Are\\acoustic\\echosounder_cruiseNumber_2019840_Kings+Bay", "2019840", "58", "3223");
        convertBubble("E:\\Are\\acoustic\\echosounder_cruiseNumber_2019841_Eros", "2019841", "58", "3317");
        convertBubble("E:\\Are\\acoustic\\echosounder_cruiseNumber_2019842_Vendla", "2019842", "58", "3206");
    }

    public void convertBubble(String fileName, String cruise, String nation, String platform) {
        List<DistanceBO> dist = ReadAcousticXML.perform(fileName + ".xml");
        boolean found = false;
        for (DistanceBO d : dist) {
            for (FrequencyBO f : d.getFrequencies()) {
                if (f.getBubble_corr() != null && f.getBubble_corr() != 1.0d) {
                    found = true;
                    System.out.println("distance " + d.toString());
                }
                for (SABO s : f.getSa()) {
                    if (s.getSa() != null && f.getBubble_corr() != null && f.getBubble_corr() != 1.0d) {
                        s.setSa(s.getSa() / f.getBubble_corr());
                    }
                }
            }
        }
        /*getCorrection().stream().forEach(lp -> {
            DistanceBO d = getDistance(dist, lp.getLog());
            d.setLat_start(lp.getLat());
            d.setLat_stop(lp.getLat());
            d.setLon_start(lp.getLon());
            d.setLon_stop(lp.getLon());
        });*/
        if (found) {
            ListUser20Writer.export(cruise, nation, platform, fileName + "-corr" + ".xml", dist);
        }
    }
}
