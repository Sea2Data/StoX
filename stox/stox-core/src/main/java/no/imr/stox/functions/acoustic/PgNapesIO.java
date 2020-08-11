/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.acoustic;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.echosounderbo.FrequencyBO;
import no.imr.sea2data.echosounderbo.SABO;
import no.imr.stox.util.math.ImrMath;
import no.imr.stox.util.base.Conversion;
import no.imr.stox.util.base.IMRdate;
import no.imr.stox.functions.utils.StoXMath;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author aasmunds
 */
public class PgNapesIO {

    public static void convertLuf20ToPgNapes(String luf20FileName, String outFilePrefix, Integer frequency) {
        File f = new File(luf20FileName);
        List<DistanceBO> dList = ReadAcousticXML.perform(luf20FileName);
        if (dList.isEmpty()) {
            return;
        }
        DistanceBO d = dList.get(0);
        String cruise = d.getCruise();
        String nation = d.getNation();
        String pl = d.getPlatform();
        PgNapesIO.export2(cruise, nation, pl, f.getParent(), outFilePrefix, dList, 10d, frequency, null, false);

    }

    public static void export2(String cruise, String country, String callSignal, String path, String fileName,
            List<DistanceBO> distances, Double groupThickness, Integer freqFilter, String specFilter, boolean withZeros) {
        Set<Integer> freqs = distances.stream()
                .flatMap(dist -> dist.getFrequencies().stream())
                .map(FrequencyBO::getFreq)
                .collect(Collectors.toSet());
        if (freqFilter == null && freqs.size() == 1) {
            freqFilter = freqs.iterator().next();
        }

        if (freqFilter == null) {
            System.out.println("Multiple frequencies, specify frequency filter as parameter");
            return;
        }
        Integer freqFilterF = freqFilter; // ef.final
        List<String> acList = distances.parallelStream()
                .flatMap(dist -> dist.getFrequencies().stream())
                .filter(fr -> freqFilterF.equals(fr.getFreq()))
                .map(f -> {
                    DistanceBO d = f.getDistanceBO();
                    LocalDateTime sdt = LocalDateTime.ofInstant(d.getStart_time().toInstant(), ZoneOffset.UTC);
                    Double intDist = d.getIntegrator_dist();
                    String month = StringUtils.leftPad(sdt.getMonthValue() + "", 2, "0");
                    String day = StringUtils.leftPad(sdt.getDayOfMonth() + "", 2, "0");
                    String hour = StringUtils.leftPad(sdt.getHour() + "", 2, "0");
                    String minute = StringUtils.leftPad(sdt.getMinute() + "", 2, "0");
                    String log = Conversion.formatDoubletoDecimalString(d.getLog_start(), "0.0");
                    String acLat = Conversion.formatDoubletoDecimalString(d.getLat_start(), "0.000");
                    String acLon = Conversion.formatDoubletoDecimalString(d.getLon_start(), "0.000");
                    return Stream.of(d.getNation(), d.getPlatform(), d.getCruise(), log, sdt.getYear(), month, day, hour, minute, acLat, acLon,
                            intDist, f.getFreq(), f.getThreshold())
                            .map(o -> o == null ? "" : o.toString())
                            .collect(Collectors.joining("\t")) + "\t";
                }).collect(Collectors.toList());
        String fil1 = path + "/" + fileName + ".txt";
        acList.add(0, Stream.of("Country", "Vessel", "Cruise", "Log", "Year", "Month", "Day", "Hour",
                "Min", "AcLat", "AcLon", "Logint", "Frequency", "Sv_threshold").collect(Collectors.joining("\t")));
        try {
            Files.write(Paths.get(fil1), acList, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            Logger.getLogger(PgNapesIO.class.getName()).log(Level.SEVERE, null, ex);
        }
        acList.clear();
        // Acoustic values
        distances.stream()
                .filter(d->d.getPel_ch_thickness() != null)
                .flatMap(dist -> dist.getFrequencies().stream())
                .filter(fr -> freqFilterF.equals(fr.getFreq()))
                .forEachOrdered(f -> {
                    try {
                        Double groupThicknessF = Math.max(f.getDistanceBO().getPel_ch_thickness(), groupThickness);
                        Map<String, Map<Integer, Double>> pivot = f.getSa().stream()
                                .filter(s -> s.getCh_type().equals("P"))
                                .map(s -> new SAGroup(s, groupThicknessF))
                                .filter(s -> s.getSpecies() != null && (specFilter == null || specFilter.equals(s.getSpecies())))
                                // create pivot table: species (dim1) -> depth interval index (dim2) -> sum sa (group aggregator)
                                .collect(Collectors.groupingBy(SAGroup::getSpecies,
                                        Collectors.groupingBy(SAGroup::getDepthGroupIdx, Collectors.summingDouble(SAGroup::sa)))
                                );
                        if (pivot.isEmpty() && specFilter != null && withZeros) {
                            pivot.put(specFilter, new HashMap<>());
                        }
                        Integer maxGroupIdx = pivot.entrySet().stream().flatMap(e->e.getValue().keySet().stream()).max(Integer::compare).orElse(null);
                        if(maxGroupIdx == null) {
                            return;
                        }
                        acList.addAll(pivot.entrySet().stream()
                                .sorted(Comparator.comparing(Map.Entry::getKey)).flatMap(e -> {
                            return IntStream.range(0, maxGroupIdx + 1).boxed().map(groupIdx -> {
                                Double chUpDepth = groupIdx * groupThicknessF;
                                Double chLowDepth = (groupIdx + 1) * groupThicknessF;
                                Double sa = e.getValue().get(groupIdx);
                                if (sa == null) {
                                    sa = 0d;
                                }
                                String res = null;
                                if (withZeros || sa > 0d) {
                                    DistanceBO d = f.getDistanceBO();
                                    String log = Conversion.formatDoubletoDecimalString(d.getLog_start(), "0.0");
                                    LocalDateTime sdt = LocalDateTime.ofInstant(d.getStart_time().toInstant(), ZoneOffset.UTC);
                                    String month = StringUtils.leftPad(sdt.getMonthValue() + "", 2, "0");
                                    String day = StringUtils.leftPad(sdt.getDayOfMonth() + "", 2, "0");
                                    //String sas = String.format(Locale.UK, "%11.5f", sa);
                                    res = Stream.of(d.getNation(), d.getPlatform(), d.getCruise(), log, sdt.getYear(), month, day, e.getKey(), chUpDepth, chLowDepth, sa)
                                            .map(o -> o == null ? "" : o.toString())
                                            .collect(Collectors.joining("\t"));
                                }
                                return res;
                            }).filter(s -> s != null);
                        }).collect(Collectors.toList()));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });

        String fil2 = path + "/" + fileName + "Values.txt";
        acList.add(0, Stream.of("Country", "Vessel", "Cruise", "Log", "Year", "Month", "Day", "Species", "ChUppDepth", "ChLowDepth", "SA").collect(Collectors.joining("\t")));
        try {
            Files.write(Paths.get(fil2), acList, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException ex) {
            Logger.getLogger(PgNapesIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    static class SAGroup {

        SABO s;
        Double groupThickness;

        public SAGroup(SABO s, Double groupThickness) {
            this.s = s;
            this.groupThickness = groupThickness;
        }

        public String getSpecies() {
            return PgNapesEchoConvert.getPgNapesSpeciesFromAcoCat(Conversion.safeStringtoIntegerNULL(s.getAcoustic_category()));
        }

        public Double getDepth() {
            return getDepth(s.getFrequencyBO().getDistanceBO().getPel_ch_thickness(), s.getCh());
        }

        public Integer getDepthGroupIdx() {
            return getDepthGroupIdx(getDepth(), groupThickness);
        }

        public static Double getDepth(Double pelThickness, Integer ch) {
            return StoXMath.depthFromChannel(pelThickness, ch);
        }

        public static Double getDepthGroupInterval(Double pelThickness, Integer ch, Double groupThickness) {
            return ImrMath.trunc(getDepth(pelThickness, ch), groupThickness);
        }

        public static Integer getDepthGroupIdx(Double pelThickness, Integer ch, Double groupThickness) {
            return getDepthGroupIdx(getDepth(pelThickness, ch), groupThickness);
        }

        public static Integer getDepthGroupIdx(Double depth, Double groupThickness) {
            return ImrMath.safeRound(ImrMath.safeTrunc(ImrMath.safeDivide(depth, groupThickness)));
        }

        public static Integer getNumDepthGroupIntervals(Double pelThickness, Integer numChannels, Double groupThickness) {
            return getDepthGroupIdx(getDepth(pelThickness, numChannels), groupThickness) + 1;
        }

        public Double sa() {
            return s.getSa();
        }
    }

    public static void convertPgNapesToLuf20(String path, String fileName, String outFileName) {
        try {
            List<String> acList = Files.readAllLines(Paths.get(path + "/" + fileName + ".txt"));
            List<String> acVList = Files.readAllLines(Paths.get(path + "/" + fileName + "Values.txt"));
            if (acList.isEmpty() || acVList.isEmpty()) {
                return;
            }
            acList.remove(0);
            acVList.remove(0);
            List<DistanceBO> dList = acList.stream().map(s -> {
                DistanceBO d = new DistanceBO();
                String[] str = s.split("\t", 14);
                d.setNation(str[0]);
                d.setPlatform(str[1]);
                d.setCruise(str[2]);
                d.setLog_start(Conversion.safeStringtoDoubleNULL(str[3]));
                d.setStart_time(Date.from(LocalDateTime.of(Conversion.safeStringtoIntegerNULL(str[4]),
                        Conversion.safeStringtoIntegerNULL(str[5]), Conversion.safeStringtoIntegerNULL(str[6]), Conversion.safeStringtoIntegerNULL(str[7]),
                        Conversion.safeStringtoIntegerNULL(str[8]), 0).toInstant(ZoneOffset.UTC)));
                d.setLat_start(Conversion.safeStringtoDoubleNULL(str[9]));
                d.setLon_start(Conversion.safeStringtoDoubleNULL(str[10]));
                d.setIntegrator_dist(Conversion.safeStringtoDoubleNULL(str[11]));
                FrequencyBO freq = new FrequencyBO();
                d.getFrequencies().add(freq);
                freq.setTranceiver(1); // implicit in pgnapes
                freq.setUpper_interpret_depth(0d);
                freq.setUpper_integrator_depth(0d);
                freq.setDistance(d);
                freq.setFreq(Conversion.safeStringtoIntegerNULL(str[12]));
                freq.setThreshold(Conversion.safeStringtoDoubleNULL(str[13]));
                return d;
            }).collect(Collectors.toList());
            // Fill in sa values
            acVList.forEach(s -> {
                String[] str = s.split("\t", 11);
                String cruise = str[2];
                Double log = Conversion.safeStringtoDoubleNULL(str[3]);
                Integer year = Conversion.safeStringtoIntegerNULL(str[4]);
                Integer month = Conversion.safeStringtoIntegerNULL(str[5]);
                Integer day = Conversion.safeStringtoIntegerNULL(str[6]);
                if (log == null || year == null || month == null || day == null) {
                    return;
                }
                DistanceBO d = dList.parallelStream().filter(di -> {
                    if (di.getCruise() == null || di.getLog_start() == null || di.getStart_time() == null) {
                        return false;
                    }
                    LocalDate ld = di.getStart_time().toInstant().atZone(ZoneOffset.UTC).toLocalDate();
                    return cruise.equals(di.getCruise()) && log.equals(di.getLog_start()) && year.equals(ld.getYear())
                            && month.equals(ld.getMonthValue()) && day.equals(ld.getDayOfMonth());
                }).findFirst().orElse(null);
                if (d == null) {
                    return;
                }
                FrequencyBO freq = d.getFrequencies().get(0);

                String species = str[7];
                Integer acocat = PgNapesEchoConvert.getAcoCatFromPgNapesSpecies(species);
                Double chUppDepth = Conversion.safeStringtoDoubleNULL(str[8]);
                Double chLowDepth = Conversion.safeStringtoDoubleNULL(str[9]);
                Double sa = Conversion.safeStringtoDoubleNULL(str[10]);
                if (acocat == null || sa == null || sa == 0d || chLowDepth == null || chUppDepth == null) {
                    return;
                }
                if (d.getPel_ch_thickness() == null) {
                    d.setPel_ch_thickness(chLowDepth - chUppDepth);
                }
                Integer ch = (int) (chLowDepth / d.getPel_ch_thickness() + 0.5);
                SABO sabo = new SABO();
                sabo.setFrequency(freq);
                freq.getSa().add(sabo);
                sabo.setAcoustic_category(acocat + "");
                sabo.setCh_type("P");
                sabo.setCh(ch);
                sabo.setSa(sa);
            });
            // Calculate number of pelagic channels

            /*dList.stream().forEach(d -> {
                FrequencyBO f = d.getFrequencies().get(0);
                Integer minCh = f.getSa().stream().map(SABO::getCh).min(Integer::compare).orElse(null);
                Integer maxCh = f.getSa().stream().map(SABO::getCh).max(Integer::compare).orElse(null);
                if (maxCh != null && minCh != null) {
                    f.setNum_pel_ch(maxCh - minCh + 1);
                }
            });*/

            if (dList.isEmpty()) {
                return;
            }
            DistanceBO d = dList.get(0);
            String cruise = d.getCruise();
            String nation = d.getNation();
            String pl = d.getPlatform();
            ListUser20Writer.export(cruise, nation, pl, path + "/" + cruise + outFileName + ".xml", dList);

        } catch (IOException ex) {
            Logger.getLogger(PgNapesIO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void export(String cruise, String country, String callSignal, String path, String fileName,
            List<DistanceBO> distances, String species, Double intDist, Double pchThick) {
        // Atle: implement!
        Integer acoCat = PgNapesEchoConvert.getAcoCatFromPgNapesSpecies(species);
        BufferedWriter bWriter1 = null;
        BufferedWriter bWriter2 = null;
        try {
            String fil1 = path + "/" + fileName + "_Acoustic.txt";
            String fil2 = path + "/" + fileName + "_AcousticValues.txt";

            bWriter1 = new BufferedWriter(new FileWriter(fil1));
            bWriter2 = new BufferedWriter(new FileWriter(fil2));

            bWriter1.write("Country" + "" + "\t" + "Vessel" + "" + "\t" + "Cruise" + "" + "\t" + "Log" + "" + "\t");
            bWriter1.write("Year" + "" + "\t" + "Month" + "" + "\t" + "Day" + "" + "\t" + "Hour" + "" + "\t");
            bWriter1.write("Min" + "" + "\t" + "AcLat" + "" + "\t" + "AcLon" + "" + "\t" + "Logint" + "" + "\t");
            bWriter1.write("Frequency" + "" + "\t" + "Sv_threshold");
            bWriter1.newLine();

            bWriter2.write("Country" + "" + "\t" + "Vessel" + "" + "\t" + "Cruise" + "" + "\t" + "Log" + "" + "\t");
            bWriter2.write("Year" + "" + "\t" + "Month" + "" + "\t" + "Day" + "" + "\t" + "Species" + "" + "\t");
            bWriter2.write("ChUppDepth" + "" + "\t" + "ChLowDepth" + "" + "\t" + "SA");
            bWriter2.newLine();

            Boolean isEnd = true;

            //Vertical Resolution/new channel thickness from filter
            Double totintdist = 0.0;
            Integer countdist = 0;
            Double sa50ch[] = new Double[1001]; //sa pr intdist
            Double saout[] = new Double[1001];  //sa accumulated over 5 nm. Output to files

            for (int g = 0; g < sa50ch.length; g++) {
                sa50ch[g] = 0.0;
                saout[g] = 0.0;
            }

            String hout1[] = new String[14];   // Sheet1: header info from first intdist in 5 nm dist
            for (int h = 0; h < hout1.length; h++) {
                hout1[h] = null;
            }

            String hout2[] = new String[8];   // Sheet2: header info from first intdist in 5 nm dist
            for (int h = 0; h < hout2.length; h++) {
                hout2[h] = null;
            }

            Resolution rr = new Resolution();

            //GO THROUGH ALL OBSERVATIONS IN SELECTED DATASETT ONE OBS AT THE TIME
            Integer nobs = distances.size();

            for (int line = 0; line < nobs; line++) {
                DistanceBO dist = distances.get(line);
                for (FrequencyBO f : dist.getFrequencies()) {
                    FrequencyBO frequency = f;
                    //PS! What if threshold change within a 5 nm ?? Use first value OK???
                    String threshold = Double.toString(frequency.getThreshold());
                    //String helpNumPel = Integer.toString(freq.getNum_pel_ch());

                    Date d = dist.getStart_time();
                    String helpymd = IMRdate.formatDate(d, "yyyyMMdd");
                    String year = helpymd.substring(0, 4);
                    String month = helpymd.substring(4, 6);
                    String day = helpymd.substring(6, 8);

                    String helphms = IMRdate.formatDate(d, "HHmmss");
                    String hour = helphms.substring(0, 2);
                    String min = helphms.substring(2, 4);

                    // String Log = df.format(dist.getLog_start()/*LogFloor*/);
                    String log = Conversion.formatDoubletoDecimalString(dist.getLog_start().doubleValue(), "0.0");

                    //Check if this is the end of the integrator distance (using the filter value
                    rr.setIntDist(Math.max(intDist, dist.getIntegrator_dist()));
                    rr.setLog(Conversion.safeStringtoDouble(log));
                    isEnd = rr.getIsEnd();

                    String acLat = Conversion.formatDoubletoDecimalString(dist.getLat_start(), "0.000");
                    String acLon = Conversion.formatDoubletoDecimalString(dist.getLon_start(), "0.000");
                    Double helppct = dist.getPel_ch_thickness();
                    Integer helppctR = (int) Math.round(helppct);

                    //Number of ch per 50 meter ch
                    Double vertRes = null;
                    if (pchThick != null && pchThick > dist.getPel_ch_thickness()) {
                        vertRes = pchThick;
                    } else {
                        vertRes = dist.getPel_ch_thickness();
                    }
                    Double ww = vertRes / helppct;
                    Integer helpnchch = ww.intValue();
                    //Number of 50 meter channels
                    Integer q = frequency.getNum_pel_ch();
                    Integer ww1 = frequency.getNum_pel_ch() % helpnchch;
                    Integer ww2 = (frequency.getNum_pel_ch() / helpnchch);
                    Integer helpnch = ww2.intValue();
                    if (ww1 > 0) {
                        helpnch = helpnch + 1;
                    }
                    if (helpnch > sa50ch[0]) {
                        sa50ch[0] = helpnch.doubleValue();
                    }

                    // SUM UP P SA VALUES FOR THIS OBSERVATION IN 50 meter CHANNELS
                    List<SABO> salist = frequency.getSa();

                    for (Integer ch = 1; ch <= frequency.getNum_pel_ch(); ch++) {
                        if (ch <= frequency.getNum_pel_ch()) {
                            //Double saval = 0.0;
                            for (Integer i = 0; i < salist.size(); i++) {
                                //storage.getSaByFrequencyChannelTypeAcousticCategory(freq, "P", f.getId_acoustic_category());
                                SABO elm = salist.get(i);
                                if (elm.getAcoustic_category() == null || elm.getCh() == null || elm.getCh_type() == null
                                        || !elm.getCh().equals(ch)
                                        || !elm.getCh_type().equals("P")
                                        || !elm.getAcoustic_category().equalsIgnoreCase(acoCat.toString())) {
                                    continue;
                                }
                                Double ch50a = (ch * helppctR.doubleValue()) % vertRes;
                                Double ch50b = (ch * helppctR.doubleValue()) / vertRes;
                                Integer ch50c = ch50b.intValue();

                                if (ch50a > 0) {
                                    ch50c = ch50c + 1;
                                }
                                sa50ch[ch50c] = elm.getSa() + sa50ch[ch50c];
                                sa50ch[1000] = elm.getSa() + sa50ch[1000];
                                break;
                            }
                        }
                    }

                    //IF START OF A NEW 5 NM DISTANCE KEEP HEADING VALUES
                    if (isEnd.equals(true)) {
                        hout1[0] = country;
                        hout1[1] = callSignal;
                        hout1[2] = cruise;
                        hout1[3] = log;
                        hout1[4] = year;
                        hout1[5] = month;
                        hout1[6] = day;
                        hout1[7] = hour;
                        hout1[8] = min;
                        hout1[9] = acLat;
                        hout1[10] = acLon;
                        //hout1[11] = Logint;
                        hout1[12] = frequency.getFreq().toString();
                        hout1[13] = threshold;

                        hout2[0] = country;
                        hout2[1] = callSignal;
                        hout2[2] = cruise;
                        hout2[3] = log;
                        hout2[4] = year;
                        hout2[5] = month;
                        hout2[6] = day;
                        hout2[7] = species;
                    }

                    //KEEP SA-VALUES FROM THIS INTEGRATOR DISTANCE
                    if (sa50ch[0] > saout[0]) {
                        saout[0] = sa50ch[0];
                    }
                    for (int k = 1; k < saout.length; k++) {
                        saout[k] = saout[k] + sa50ch[k];
                    }

                    //Count number of intdist + totaldist
                    countdist = countdist + 1;
                    totintdist = totintdist + dist.getIntegrator_dist();

                    boolean okIntDist = false;

                    //Check if distance from previous output distance is not to  large due to holes in data:
                    boolean okPrevDist = false;
                    if ((rr.getEndLog() != null) && (rr.getIntDist() != null)) {
                        if ((rr.getLog() - rr.getEndLog()) - 0.05 <= rr.getIntDist()) {
                            okPrevDist = true;
                        } else {
                            okPrevDist = false;
                        }
                    } else {
                        okPrevDist = true;
                    }

                    //Check if sum of distances is correct + check if stoplog minus startlog is equal to sum of distances
                    if (rr.getIntDist() == null) {
                        okIntDist = true;
                    } else {
                        if ((((rr.getIntDist() - 0.05) < totintdist) && (totintdist < (rr.getIntDist() + 0.05))) && okPrevDist) {
                            okIntDist = true;
                        }
                    }

                    //PRINT TO FILES
                    if (okIntDist && isEnd) {
                        for (int k = 1; k < saout.length; k++) {
                            saout[k] = saout[k] / countdist;
                        }

                        //Sheet file 1:
                        hout1[11] = Conversion.formatDoubletoDecimalString(totintdist, "0.0");

                        for (Integer elm = 0; elm < 14; elm++) {
                            if (hout1[elm] != null) {
                                bWriter1.write(hout1[elm] + "" + "\t");
                            } else {
                                bWriter1.write(" " + "" + "\t");
                            }
                        }
                        bWriter1.newLine();

                        //Sheet file 2:
                        for (Integer elm = 0; elm < saout[0].intValue(); elm++) {
                            for (Integer e = 0; e < 8; e++) {
                                bWriter2.write(hout2[e] + "" + "\t");
                            }
                            bWriter2.write((elm * vertRes) + "" + "\t");
                            bWriter2.write(((elm * vertRes) + vertRes) + "" + "\t");
                            String sa = String.format(Locale.UK, "%11.5f", (saout[elm + 1]));
                            bWriter2.write(sa + "" + "\t");
                            bWriter2.newLine();
                        }
                    } else {
                        for (int k = 1; k < sa50ch.length; k++) {
                            sa50ch[k] = 0.0;
                        }
                    }
                    if (isEnd.equals(true)) {
                        for (int g = 0; g < sa50ch.length; g++) {
                            sa50ch[g] = 0.0;
                            saout[g] = 0.0;
                        }
                        //Total sa all channels, this variable is not used, test only
                        sa50ch[1000] = 0.0;
                        //Max number of 50 meter channels in 5 nm distance
                        sa50ch[0] = 0.0;

                        countdist = 0;
                        totintdist = 0.0;
                    }
                } // Have to look up by frequency here in future.

            }
            bWriter1.close();
            bWriter2.close();
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }

    }

    private static class Resolution {

        private static final double FIVE_MILE = 5.0;
        private static final double ONE_MILE = 1.0;
        private Double log;
        private Double intDist;
        private Boolean isEnd;
        private Double endLog;

        public Double getEndLog() {
            return endLog;
        }

        public void setEndLog(Double endIntDist) {
            this.endLog = endIntDist;
        }

        public Double getIntDist() {
            return intDist;
        }

        public void setIntDist(Double intDist) {
            this.intDist = intDist;
        }

        public Boolean getIsEnd() {
            if (this.intDist != null) {
                if ((this.intDist == FIVE_MILE) || (this.intDist == ONE_MILE)) {
                    this.isEnd = !((log % this.intDist) > 0.0);
                    if (this.isEnd) {
                        this.setEndLog(this.log);
                    }
                }
            } else {
                isEnd = true; //0.1 always OK if selectable for user, else no filter and OK output of original vertical resolution
                this.setEndLog(this.log);
            }
            return isEnd;
        }

        public void setIsStart(Boolean isEnd) {
            this.isEnd = isEnd;
        }

        public Double getLog() {
            return log;
        }

        public void setLog(Double log) {
            this.log = log;
        }

    }
}
