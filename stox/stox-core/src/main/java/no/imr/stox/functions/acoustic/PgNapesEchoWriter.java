/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.acoustic;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.echosounderbo.FrequencyBO;
import no.imr.sea2data.echosounderbo.SABO;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrbase.util.IMRdate;

/**
 *
 * @author aasmunds
 */
public class PgNapesEchoWriter {

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
