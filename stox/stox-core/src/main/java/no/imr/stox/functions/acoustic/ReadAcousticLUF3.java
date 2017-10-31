package no.imr.stox.functions.acoustic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.echosounderbo.FrequencyBO;
import no.imr.sea2data.echosounderbo.SABO;
import no.imr.sea2data.imrbase.map.LatLonUtil;
import no.imr.sea2data.imrbase.math.Calc;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrbase.util.IMRdate;
import no.imr.stox.functions.utils.EchosounderUtils;

/**
 * Method to read the LUF5 into Acoustic data
 *
 * @author Åsmund
 */
public class ReadAcousticLUF3 {

    public static List<DistanceBO> perform(String fileName, boolean lcs) {
        return perform(fileName, lcs, lcs ? "dd.MM.yyyy" : "yyyy.MM.dd");
    }

    public static List<DistanceBO> performLCS(String fileName) {
        return perform(fileName, true);
    }

    public static List<DistanceBO> performLUF3(String fileName) {
        return perform(fileName, false);
    }

    /**
     *
     * @param fileName
     * @param lcs if list com scatter format is used
     * @param dateFormat
     * @param timeFormat
     * @return
     */
    public static List<DistanceBO> perform(String fileName, boolean lcs, String dateFormat) {
        // Try as absolute file first, then as relative to workpath
        List<DistanceBO> distances = new ArrayList<>();
        DateFormat df = IMRdate.getDateFormat(dateFormat, true);
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;

            String[] hdr = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.contains("SHIP:") || line.contains("FREQUENCY:")) {
                    continue;
                }
                line = line.replace(" -10000.0 ", " - 10000.0 ");
                line = line.replace(" E ", " E0");
                line = line.replace(" S ", " S0");
                line = line.replace(" N ", " N0");
                line = line.replace(" W ", " W0");
                line = line.replace(" V ", " V0");
                String[] elms = line.split("\\s+");
                if (elms.length < 9) {
                    continue;
                }
                if (line.contains("DATE")) {
                    hdr = fixHdr(line).trim().split("\\s+");
                    continue;
                }
                Double startLog = Conversion.safeStringtoDoubleNULL(elms[lcs ? 2 : 4]);
                Double stopLog = Conversion.safeStringtoDoubleNULL(elms[lcs ? 4 : 6]);
                if (lcs && (startLog == null || stopLog == null)) {
                    continue;
                }
                Date da = df.parse(elms[0]);
                Date ti = IMRdate.strToTime(elms[1]);
                DistanceBO dist = new DistanceBO();
                distances.add(dist);
                dist.setStart_time(IMRdate.encodeDate(da, ti));
                dist.setLat_start(lcs ? getLCSPos(elms[5]) : Conversion.safeStringtoDouble(elms[2]));
                dist.setLon_start(lcs ? getLCSPos(elms[6]) : Conversion.safeStringtoDouble(elms[3]));
                dist.setLog_start(new BigDecimal(startLog));
                dist.setIntegrator_dist(stopLog - startLog);
                Double dep = Conversion.safeStringtoDouble(elms[7]);
                if (dep != null && !dep.equals(999d)) {
                    dist.setPel_ch_thickness(dep);
                }
                FrequencyBO f = new FrequencyBO();
                f.setDistance(dist);
                dist.getFrequencies().add(f);
                f.setFreq(38000);
                f.setTranceiver(1);
                f.setNum_pel_ch(1);
                for (int i = 8; i < elms.length - (lcs ? 0 : 2); i++) {
                    Double saval = Conversion.safeStringtoDoubleNULL(elms[i]);
                    if (saval == null || saval == 0d) {
                        continue;
                    }
                    String acoStr = hdr[i - (lcs ? 3 : 2)];
                    if (acoStr.equals("SC")) {
                        continue;
                    }
                    Integer acoCat = EchosounderUtils.acoCatFromAcoStr(acoStr);
                    if (acoCat == null) {
                        throw new RuntimeException(acoStr + " not found");
                    }
                    SABO sa = new SABO();
                    sa.setFrequency(f);
                    f.getSa().add(sa);
                    sa.setAcoustic_category(acoCat.toString());
                    sa.setCh(1);
                    sa.setCh_type("P");
                    sa.setSa(saval);
                }
            }
        } catch (ParseException | IOException ex) {
            Logger.getLogger(ReadAcousticLUF3.class.getName()).log(Level.SEVERE, null, ex);
        }
        return distances;
    }


    private static String fixHdr(String hdr) {
        hdr = hdr.replaceAll("Blue wh", "Blue-wh");
        hdr = hdr.replaceAll("Polar cod", "Polarcod");
        hdr = hdr.replaceAll("pol cod", "Polarcod");
        return hdr;
    }

    private static Double getLCSPos(String elm) {
        String c = elm.substring(1 - 1, 1);
        String[] s = elm.substring(2 - 1).split(":");
        if (s.length < 2) {
            System.out.println("error");
        }
        Integer deg = Conversion.safeStringtoIntegerNULL(s[0]);
        Double m = Conversion.safeStringtoDoubleNULL(s[1]);
        return Calc.roundTo(LatLonUtil.getLatOrLon("SW".contains(c), deg, m), 3);
    }
}
