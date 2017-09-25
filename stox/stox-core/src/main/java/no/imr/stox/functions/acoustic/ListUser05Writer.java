package no.imr.stox.functions.acoustic;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.text.DateFormat;
import java.util.*;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.echosounderbo.FrequencyBO;
import no.imr.sea2data.echosounderbo.SABO;
import no.imr.sea2data.imrbase.util.IMRdate;
import no.imr.sea2data.imrbase.util.ImrIO;

/**
 * Writes to the ListUserFile05 format
 *
 * @author sjurl
 */
public class ListUser05Writer {

    /**
     * export_luf5 Developed by Atle Enhanced 28.01.2008 - asmund: introduced
     * filter parameter
     *
     * @param cruise
     * @param nation
     * @param platform
     * @param fileName
     * @param distances
     * @param acocat
     * @param freq
     * @throws java.io.UncheckedIOException
     */
    public static void export(String cruise, String nation, String platform, String fileName, List<DistanceBO> distances, Integer freq, Integer acocat) {

        try (OutputStream os = new FileOutputStream(fileName);
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(os))) {
            // Get a list of GUID's from Frequency table for a dataset name by a specified frequency and transceiver
            Integer nobs = distances.size();
            //Use the GUID's frequency list to extract the wanted variables from the Frequency table in the DB
            for (int line = 0; line < nobs; line++) {
                DistanceBO dist = distances.get(line);
                FrequencyBO frequency = null;
                for (FrequencyBO f : dist.getFrequencies()) {
                    if (f.getFreq().equals(freq)) {
                        frequency = f;
                        break;
                    }
                } // Have to look up by frequency here in future.
                if (frequency == null) {
                    continue;
                }
                writeHeaderLine(frequency, dist, cruise, nation, platform, acocat, bufferedWriter);
                writePelagicLine(frequency, dist, acocat, bufferedWriter);
                writeBottomLine(frequency, dist, acocat, bufferedWriter);
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }

    }

    private static void writeBottomLine(FrequencyBO frequency, DistanceBO dist, Integer acocat, BufferedWriter bufferedWriter) {
        List<SABO> bottomSAlist = getSAValues("B", acocat, frequency);
        Integer numBottomChanels = 0;
        for (SABO savo : bottomSAlist) {
            if (savo.getCh() > numBottomChanels) {
                numBottomChanels = savo.getCh();
            }
        }
        if (frequency.getNum_bot_ch() != null && (numBottomChanels == 0 || numBottomChanels < frequency.getNum_bot_ch())) {
            numBottomChanels = frequency.getNum_bot_ch();
        }

        Double bottomTotal = 0.0;
        Double[] bottonSaValues = null;
        if (numBottomChanels > 0) {
            bottonSaValues = new Double[numBottomChanels];
            for (Integer ch = 0; ch < numBottomChanels; ch++) {
                Double saval = 0.0;
                for (Integer i = 0; i < bottomSAlist.size(); i++) {
                    SABO elm = bottomSAlist.get(i);
                    if (elm.getCh().equals(ch + 1)) {
                        saval = elm.getSa();
                        bottomTotal = bottomTotal + saval;
                        break;
                    }
                }
                bottonSaValues[ch] = saval;
            }
        }

        //Write the data to a formatted (LSSS) to a ListUser5 - file (3 lines pr obs)
        Double botChanThickness = dist.getBot_ch_thickness();
        if (botChanThickness == null) {
            botChanThickness = 0d;
        }
        String line3 = String.format(Locale.UK, "%10.3f%11d%17.5f", botChanThickness, numBottomChanels, bottomTotal);
        ImrIO.write(bufferedWriter, line3);
        writeSAvalues(numBottomChanels, bottonSaValues, bufferedWriter);
        ImrIO.newLine(bufferedWriter);
    }

    private static void writePelagicLine(FrequencyBO frequency, DistanceBO dist, Integer acocat, BufferedWriter bufferedWriter) {
        //Extract wanted variables from NASC table of the DB

        List<SABO> pelagicSAList = getSAValues("P", acocat, frequency);
        Integer numPelagicChanels = 0;

        for (SABO savo : pelagicSAList) {
            if (savo.getCh() > numPelagicChanels) {
                numPelagicChanels = savo.getCh();
            }
        }

        if (frequency.getNum_pel_ch() != null && (numPelagicChanels == 0 || numPelagicChanels < frequency.getNum_pel_ch())) {
            numPelagicChanels = frequency.getNum_pel_ch();
        }
        Double pelagicTotal = 0.0;
        Double[] pelagicSa = null;
        if (numPelagicChanels > 0) {
            pelagicSa = new Double[numPelagicChanels];
            for (Integer ch = 0; ch < numPelagicChanels; ch++) {
                Double saval = 0.0;
                for (Integer i = 0; i < pelagicSAList.size(); i++) {
                    SABO elm = pelagicSAList.get(i);
                    if (elm.getCh().equals(ch + 1)) {
                        saval = elm.getSa();
                        pelagicTotal = pelagicTotal + saval;
                        break;
                    } // if
                } // for
                pelagicSa[ch] = saval;
            }
        }
        Double pelChanThickness = dist.getPel_ch_thickness();
        if (pelChanThickness == null) {
            pelChanThickness = 0d;
        }
        String line2 = String.format(Locale.UK, "%10.3f%11d%17.5f", pelChanThickness, numPelagicChanels, pelagicTotal);
        ImrIO.write(bufferedWriter, line2);
        writeSAvalues(numPelagicChanels, pelagicSa, bufferedWriter);
        ImrIO.newLine(bufferedWriter);
    }

    private static void writeHeaderLine(FrequencyBO frequency, DistanceBO dist, String survey, String nation, String platcode, Integer acocat, BufferedWriter bufferedWriter) {
        Double mindepth = frequency.getMin_bot_depth();
        if (mindepth == null) {
            mindepth = 0d;
        }
        Double maxdepth = frequency.getMax_bot_depth();
        if (maxdepth == null) {
            maxdepth = 0d;
        }
        Integer frekvens = frequency.getFreq();
        Integer transceiver = frequency.getTranceiver();
        Double threshold = frequency.getThreshold();
        if (threshold == null) {
            threshold = 0d;
        }
        Date date = dist.getStart_time();
        DateFormat format = IMRdate.getDateFormat("yyyyMMdd", true);
        String dateyyyyMMdd = format.format(date);
        String timekkmmssSS = createTimeValue(date);
        Double logStart = dist.getLog_start().doubleValue();
        Double latitude = dist.getLat_start();
        Double longitude = dist.getLon_start();
        // Extract wanted variable "integrator_distance" from Echosounder_dataset table of the DB
        Double log2 = (dist.getIntegrator_dist() + dist.getLog_start().doubleValue());
        //TMP, these variables has to be input differently later from other DB's:
        String line1 = String.format(Locale.UK, "%10s%11s%11s%11s%11s%11.3f%11.3f%11.3f%11.3f%11.3f%11.3f%11d%11d%11.3f%11d%n",
                survey, nation, platcode, dateyyyyMMdd, timekkmmssSS, logStart, log2, latitude, longitude, mindepth, maxdepth, frekvens, transceiver, threshold, acocat);
        ImrIO.write(bufferedWriter, line1);
    }

    private static void writeSAvalues(Integer numChannels, Double[] nasc, BufferedWriter bufferedWriter) {
        if (numChannels > 0) {
            for (Integer element = 0; element < nasc.length; element++) {
                String saValue = String.format(Locale.UK, "%17.5f", nasc[element]);
                ImrIO.write(bufferedWriter, saValue);
            }
        }
    }

    private static String createTimeValue(Date date) {
        //Output LSSS format with milliseconds
        DateFormat sdf = IMRdate.getDateFormat("HHmmss", true);
        String timekkmmssSS = sdf.format(date);
        return timekkmmssSS.replaceFirst("^0+(?!$)", " ");
    }

    private static List<SABO> getSAValues(String channelType, Integer acocat, FrequencyBO frequency) {
        List<SABO> res = new ArrayList<>();
        for (SABO sabo : frequency.getSa()) {
            if (sabo.getAcoustic_category().equals(acocat + "") && sabo.getCh_type().equals(channelType)) {
                res.add(sabo);
            }
        }
        return res;
    }
}
