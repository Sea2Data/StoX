/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.acoustic;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.stox.util.base.Conversion;
import no.imr.stox.functions.utils.EchosounderUtils;

/**
 *
 * @author aasmunds
 */
public class AcousticConverter {

    /**
     * Converts directory with LUF5 files into Luf20 files
     *
     * @param dir
     */
    public static void convertLUF5DirToLuf20(String dir) {

        File folder = new File(dir);
        List<DistanceBO> res = null;
        for (final File fileEntry : folder.listFiles()) {
            String fName = fileEntry.getPath();
            if (fName.endsWith(".xml")) {
                continue;
            }
            System.out.println("Reading " + fName);
            try {
                res = ReadAcousticLUF5.perform(fName, res);
            } catch (Exception e) {
                System.out.println("Error reading " + fName);
            }
        }
        if (res != null) {
            Map<String, List<DistanceBO>> cruises = res.stream()
                    .collect(Collectors.groupingBy(DistanceBO::getCruise, Collectors.toList()));
            cruises.entrySet().parallelStream().forEach(es -> {
                String cruise = es.getKey();
                List<DistanceBO> distances = es.getValue();
                String nation = distances.stream().map(DistanceBO::getNation).findFirst().orElse(null);
                String platform = distances.stream().map(DistanceBO::getPlatform).findFirst().orElse(null);
                ListUser20Writer.export(cruise, nation, platform, dir + "/" + cruise + ".xml", distances);
            });
        }

    }

    public static void convertLUF5FileToLuf20(String fName, String outFileName) {
        List<DistanceBO> distances = ReadAcousticLUF5.perform(fName, null);
        if (distances != null) {
            String cruise = distances.stream().map(DistanceBO::getCruise).findFirst().orElse(null);
            String nation = distances.stream().map(DistanceBO::getNation).findFirst().orElse(null);
            String platform = distances.stream().map(DistanceBO::getPlatform).findFirst().orElse(null);
            ListUser20Writer.export(cruise, nation, platform, outFileName != null ? outFileName : (fName + ".xml"), distances);
        }
    }

    public static void convertLUF20FileToLuf5Files(String fName) {
        List<DistanceBO> res = ReadAcousticXML.perform(fName);
        if (res == null || res.isEmpty()) {
            System.out.println("Error reading " + fName);
            return;
        }
        Map<String, List<DistanceBO>> cruises = res.stream()
                .collect(Collectors.groupingBy(DistanceBO::getCruise, Collectors.toList()));
        cruises.entrySet().parallelStream().forEach(es -> {
            List<DistanceBO> distances = es.getValue();
            String cruise = es.getKey();
            String nation = distances.stream().map(DistanceBO::getNation).filter(n -> n != null).findAny().orElse(null);
            String platform = distances.stream().map(DistanceBO::getPlatform).filter(n -> n != null).findAny().orElse(null);
            Set<Integer> freqs
                    = distances.stream()
                            .flatMap(d -> d.getFrequencies().stream())
                            .map(s -> s.getFreq())
                            .collect(Collectors.toSet());
            Set<Integer> transceivers
                    = distances.stream()
                            .flatMap(d -> d.getFrequencies().stream())
                            .map(s -> s.getTranceiver())
                            .collect(Collectors.toSet());
            Set<Integer> acCats
                    = distances.stream()
                            .flatMap(d -> d.getFrequencies().stream())
                            .flatMap(f -> f.getSa().stream())
                            .map(s -> Conversion.safeStringtoIntegerNULL(s.getAcoustic_category()))
                            .filter(a -> a != null)
                            .collect(Collectors.toSet());
            freqs.forEach(freq -> {
                transceivers.forEach(transceiver -> {
                    List<DistanceBO> distancesfreqtr = distances.stream()
                            .filter(d -> d.getFrequencies().stream()
                            .anyMatch(f -> f.getFreq().equals(freq) && f.getTranceiver().equals(transceiver)))
                            .collect(Collectors.toList());
                    if (distancesfreqtr.isEmpty()) {
                        return;
                    }
                    acCats.forEach(acoCat -> {
                        Double startLog = distancesfreqtr.get(0).getLog_start();
                        Double stopLog = distancesfreqtr.get(distancesfreqtr.size() - 1).getLog_start();
                        String fout = (new File(fName)).getParent() + "/ListUserFile05__F" + freq + "_T" + transceiver + "_L" + Conversion.safeStr(startLog.toString())
                                + "-" + Conversion.safeStr(stopLog.toString()) + "_" + EchosounderUtils.acoCatToAcoStr(acoCat).toUpperCase() + ".txt";
                        ListUser05Writer.export(cruise, nation, platform, fout, distancesfreqtr, freq, transceiver, acoCat);
                    });
                });
            });
        });

    }

    public static void convertAcousticCSVFileToLuf20(String fName, String outFileName) {
        convertAcousticCSVFileToLuf20(fName, outFileName, null, null, null);
    }

    public static void convertAcousticCSVFileToLuf20(String fName, String outFileName, String cruise, String nation, String platform) {
        List<DistanceBO> distances = ReadAcousticLUF3.perform(fName);
        if (outFileName == null) {
            outFileName = fName + ".xml";
        }
        if (distances.size() > 0) {
            if (cruise == null) {
                cruise = distances.get(0).getCruise();
            }
            if (platform == null) {
                platform = distances.get(0).getPlatform();
            }
            if (nation == null) {
                nation = distances.get(0).getNation();
            }
        }
        ListUser20Writer.export(cruise, nation, platform, outFileName, distances);
    }
}
