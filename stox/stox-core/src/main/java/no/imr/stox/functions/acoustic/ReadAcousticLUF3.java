package no.imr.stox.functions.acoustic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.echosounderbo.FrequencyBO;
import no.imr.sea2data.echosounderbo.SABO;
import no.imr.stox.util.map.LatLonUtil;
import no.imr.stox.util.math.Calc;
import no.imr.stox.util.base.Conversion;
import no.imr.stox.util.base.IMRdate;

/**
 * Method to read the LUF5 into Acoustic data
 *
 * @author Åsmund
 */
public class ReadAcousticLUF3 {

    public static List<DistanceBO> perform(String fileName, List<DistanceBO> distances) {
        return perform(fileName, null, null, "dd.MM.yyyy", distances);
    }
    public static List<DistanceBO> perform(String fileName) {
        return perform(fileName, null, null, "dd.MM.yyyy", new ArrayList<>());
    }

    public static List<DistanceBO> perform(String fileName, Integer frequency_def, Integer transceiver_def) {
        return perform(fileName, frequency_def, transceiver_def, "dd.MM.yyyy", new ArrayList<>());
    }

    /**
     *
     * @param fileName
     * @param frequency_def
     * @param transceiver_def
     * @return
     */
    public static List<DistanceBO> perform(String fileName, Integer frequency_def, Integer transceiver_def, String dateFormat) {
        return perform(fileName, frequency_def, transceiver_def, dateFormat, new ArrayList<>());
    }

    public static List<DistanceBO> perform(String fileName, Integer frequency_def, Integer transceiver_def, String dateFormat, List<DistanceBO> distances) {
        // Try as absolute file first, then as relative to workpath
        DateFormat df = null;
        String ship_def = null;
        String nation_def = null;
        String survey_def = null;
        Integer depthIdx = -1;
        List<String> hdrList = null;
        Pattern lcsMetaPattern = Pattern.compile("(ship|nation|survey|frequency|transceiver)\\s*:\\s*([\\w\\.]+)");
        List<AcoEntry> entries = AcoEntry.get();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;

            String[] hdr = null;
            int lineNo = 0;
            while ((line = reader.readLine()) != null) {
                lineNo++;
                line = line.trim().toLowerCase();
                if (line.isEmpty()) {
                    continue;
                }
                if (line.contains("ship:") || line.contains("frequency:")) {
                    // List com scatter metalines
                    Matcher m = lcsMetaPattern.matcher(line);
                    while (m.find()) {
                        String key = m.group(1);
                        String value = m.group(2);
                        switch (key) {
                            case "ship":
                                ship_def = value;
                                break;
                            case "nation":
                                nation_def = value;
                                break;
                            case "survey":
                                survey_def = value;
                                break;
                            case "frequency":
                                frequency_def = Conversion.safeStringtoIntegerNULL(value);
                                break;
                            case "transceiver":
                                transceiver_def = Conversion.safeStringtoIntegerNULL(value);
                                break;
                        }
                    }
                    continue;
                }

                boolean isHdr = line.contains("date");
                if (isHdr) {
                    line = fixHdr(line);
                } else {
                    line = fixLine(line);
                }
                if (line.contains("kursretning")) {
                    continue;
                }
                String[] elms = line.split("[\\t,]|[\\s]+");
                String dateStr;
                if (isHdr) {
                    hdr = elms;
                    hdrList = Stream.of(hdr).map(s -> s.trim()).collect(Collectors.toList());
                    depthIdx = indexOfStr(hdrList, "depth");
                    continue;
                } else {
                    dateStr = getElm(elms, hdrList, "date");
                    dateStr = dateStr.replaceFirst("[/-]", ".");
                    if (df == null && dateStr != null && dateStr.indexOf(".") == 4) {
                        dateFormat = "yyyy.MM.dd";
                    }
                }
                if (elms.length < 8) {
                    System.out.println("Too few elements in line " + lineNo + ": " + line);
                    continue;
                }
                Double startLog = Conversion.safeStringtoDoubleNULL(getElm(elms, hdrList, "logstart"));
                Double stopLog = Conversion.safeStringtoDoubleNULL(getElm(elms, hdrList, "logstop"));
                if (startLog == null || stopLog == null) {
                    System.out.println("Missing log at line no " + lineNo + ": " + line);
                    continue;
                }
                if (df == null) {
                    df = IMRdate.getDateFormat(dateFormat, true);
                }
                Date da = null;
                if (df != null) {
                    try {
                        da = df.parse(dateStr);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Date ti = IMRdate.strToTime(getElm(elms, hdrList, "time"));
                String ship = getElm(elms, hdrList, "ship");
                if (ship == null) {
                    ship = ship_def;
                }
                String nation = getElm(elms, hdrList, "nation");
                if (nation == null) {
                    nation = nation_def;
                }
                String survey = getElm(elms, hdrList, "survey");
                if (survey == null) {
                    survey = survey_def;
                }
                Integer frequency = Conversion.safeStringtoIntegerNULL(getElm(elms, hdrList, "frequency"));
                if (frequency == null) {
                    frequency = frequency_def;
                }
                Integer transceiver = Conversion.safeStringtoIntegerNULL(getElm(elms, hdrList, "transceiver"));
                if (transceiver == null) {
                    transceiver = Conversion.safeStringtoIntegerNULL(getElm(elms, hdrList, "tr"));
                    if (transceiver == null) {
                        transceiver = transceiver_def;
                    }
                }
                DistanceBO dist = new DistanceBO();
                dist.setCruise(survey);
                dist.setNation(nation);
                dist.setPlatform(ship);
                distances.add(dist);
                dist.setStart_time(IMRdate.encodeDate(da, ti));
                dist.setLat_start(getPos(getElm(elms, hdrList, "latitude")));
                dist.setLon_start(getPos(getElm(elms, hdrList, "longitude")));
                dist.setLog_start(startLog);
                try {
                    dist.setIntegrator_dist(Calc.roundTo(stopLog - startLog, 7));
                } catch (Exception ex) {
                    System.out.println("");
                }
                Double dep = Conversion.safeStringtoDouble(getElm(elms, hdrList, "depth"));
                if (dep != null && !dep.equals(999d)) {
                    dist.setPel_ch_thickness(dep);
                }
                FrequencyBO f = new FrequencyBO();
                f.setDistance(dist);
                dist.getFrequencies().add(f);
                f.setFreq(frequency);
                f.setTranceiver(transceiver);
                f.setNum_pel_ch(1);
                for (int i = depthIdx + 1; i < elms.length; i++) {
                    String acoStr = "";
                    try {
                        acoStr = hdrList.get(i);
                    } catch (Exception e) {
                        System.out.println("");
                    }
                    if (acoStr.equalsIgnoreCase("sc") || acoStr.equalsIgnoreCase("total")) {
                        continue;
                    }
                    Double saval = Conversion.safeStringtoDoubleNULL(elms[i]);
                    if (saval == null || saval == 0d) {
                        continue;
                    }
                    AcoEntry acoCat = AcoEntry.byName(entries, acoStr);
                    if (acoCat == null) {
                        throw new RuntimeException("Acoustic category " + acoStr + " not found");
                    }
                    SABO sa = new SABO();
                    sa.setFrequency(f);
                    f.getSa().add(sa);
                    sa.setAcoustic_category(acoCat.getAco().toString());
                    sa.setCh(1);
                    sa.setCh_type("P");
                    sa.setSa(saval);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ReadAcousticLUF3.class.getName()).log(Level.SEVERE, null, ex);
        }
        return distances;
    }

    private static Integer indexOfStr(List<String> list, String elm) {
        String colName2 = list.stream().filter(s -> s.equalsIgnoreCase(elm)).findAny().orElse(null);
        return list.indexOf(colName2);
    }

    private static String getElm(String[] elms, List<String> hdrList, String colName) {
        if (hdrList == null) {
            return null;
        }
        Integer idx = indexOfStr(hdrList, colName);
        if (idx < 0 || idx > elms.length - 1) {
            return null;
        }
        return elms[idx].trim();
    }

    private static Entry<String, String> ofPair(String key, String val) {
        return new AbstractMap.SimpleEntry(key, val);
    }

    private static String fixHdr(String hdr) {
        List<Entry<String, String>> l = Stream.of(
                ofPair("log", "logstart\tlogstop"),
                ofPair("position", "latitude\tlongitude"),
                ofPair("lat", "latitude"),
                ofPair("lon", "longitude"),
                ofPair("lat.", "latitude"),
                ofPair("lon.", "longitude"),
                ofPair("blue wh", "blue-wh"),
                ofPair("polar cod", "polarcod"),
                ofPair("pol cod", "polarcod")
        ).collect(Collectors.toList());
        for (Entry<String, String> e : l) {
            hdr = hdr.replaceFirst("(?i)(" + e.getKey() + "[\\s,]+)", e.getValue() + "\t");
        }
        return hdr;
    }

    private static String fixLine(String line) {
        line = line.replaceFirst("\\s+-\\s+", "\t");
        // other variants:
        line = line.replace(" -10000.0 ", " - 10000.0 ");
        line = line.replace(" e ", " e0");
        line = line.replace(" s ", " s0");
        line = line.replace(" n ", " n0");
        line = line.replace(" w ", " w0");
        line = line.replace(" v ", " v0");
        return line;
    }

    private static Double getPos(String elm) {
        if (!elm.contains(":")) {
            return Conversion.safeStringtoDouble(elm);
        }
        String c = elm.substring(1 - 1, 1);
        String[] s = elm.substring(2 - 1).split(":");
        if (s.length < 2) {
            System.out.println("error when converting position " + elm);
        }
        Integer deg = Conversion.safeStringtoIntegerNULL(s[0]);
        Double m = Conversion.safeStringtoDoubleNULL(s[1]);
        return Calc.roundTo(LatLonUtil.getLatOrLon("SW".contains(c), deg, m), 3);
    }

    public static class AcoEntry {

        Integer aco;
        List<String> names;

        public AcoEntry(Integer aco, List<String> names) {
            this.aco = aco;
            this.names = names;
        }

        public Integer getAco() {
            return aco;
        }

        public List<String> getNames() {
            return names;
        }

        public static List<AcoEntry> get() {
            String resName = "reference/acocat.txt";
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(resName);
            InputStreamReader isr = new InputStreamReader(in);
            // Try-with-resources autocloses br
            // Note: br.close closes underlying streams (isr/in)
            List<String> lines = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(isr)) {
                String line;
                while ((line = br.readLine()) != null) {
                    lines.add(line);
                }
            } catch (IOException ex) {
                Logger.getLogger(ReadAcousticLUF3.class.getName()).log(Level.SEVERE, null, ex);
            }
            return lines.stream()
                    .map(line -> {
                        String[] s = line.split("\t");
                        if (s.length != 2) {
                            return null;
                        }
                        return new AcoEntry(Conversion.safeStringtoIntegerNULL(s[0]), Stream.of(s[1].split(",")).collect(Collectors.toList()));
                    }).filter(e -> e != null)
                    .collect(Collectors.toList());
        }

        public static AcoEntry byName(List<AcoEntry> entries, String name) {
            return entries.stream()
                    .filter(e -> {
                        return e.getNames().stream().anyMatch(s -> name.equalsIgnoreCase(s));
                    }).findAny().orElse(null);
        }

        public static AcoEntry byAco(List<AcoEntry> entries, Integer aco) {
            return entries.stream()
                    .filter(e -> {
                        return e.getAco().equals(aco);
                    }).findAny().orElse(null);
        }
    }
}
