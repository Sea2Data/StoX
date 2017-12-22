/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package temp;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.echosounderbo.FrequencyBO;
import no.imr.sea2data.echosounderbo.SABO;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.stox.functions.acoustic.AcousticConverter;
import no.imr.stox.functions.acoustic.ListUser20Writer;
import no.imr.stox.functions.acoustic.ReadAcousticLUF3;
import no.imr.stox.functions.utils.EchosounderUtils;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
//@Ignore
public class Luf3ToLuf20 {

    @Test
    public void test() {
        //String path = "\\\\ces.imr.no\\cruise_data\\2004\\S2004109_PGOSARS_4174\\ACOUSTIC_DATA\\LSSS\\REPORTS\\ListUserFile03__F038000_T1_L1210.0-4806.0.txt";
        String path = "\\\\ces.imr.no\\cruise_data\\2017\\S2017113_PGOSARS_4174\\ACOUSTIC_DATA\\LSSS\\Reports\\ListComScatter_F038000_T2_L2040.0-2124.0.txt";
        AcousticConverter.convertAcousticCSVFileToLuf20(path, "E:/Data/test.xml");
    }

//  @Test
    public void convertComScatterForNansis() {
        for (String comScatterFolder : Arrays.asList(/*"ComScatter-2011-2014", */"ComScatter-1994-2010")) {
            String comScatterRoot = "E:\\Bjørn Erik\\" + comScatterFolder + "\\ComScatter\\";
            File[] fsCr = new File(comScatterRoot).listFiles();
            for (File cruiseFolder : fsCr) {
                String cruise = cruiseFolder.getName();
                for (String subDir : Arrays.asList("", "Minisurvey", "other_freq")) {
                    String root = comScatterRoot + cruise + subDir;
                    File r = new File(root);
                    if (!(r.exists() && r.isDirectory())) {
                        continue;
                    }
                    File[] fs = new File(root).listFiles((File f, String name)
                            -> !(name.contains("ListAll") || name.startsWith("LAS")) && (name.endsWith(".txt") || name.endsWith(".0")));
                    Arrays.stream(fs).forEach(f -> {
                        String fullname = root + "\\" + f.getName();
                        if (fullname.contains("wellformed")) {
                            return;
                        }
                        System.out.println("converting " + fullname);
                        List<DistanceBO> d = ReadAcousticLUF3.perform(fullname);
                        ListUser20Writer.export(cruise, "58", "1172", fullname + ".xml", d);
                    });
                }
            }
        }
    }
    //@Test

    public void convert() {
        convertMareksLUFData();
//        Map<String, Object> input = new HashMap<>();
//        input.put(Functions.PM_READACOUSTICLUF5_FILENAME, "C://Data//stox//ListUserFile05__F038000_T2_L105.0-2764.9_SIL.txt");
//        List<DistanceBO> distances = (List<DistanceBO>) (new ReadAcousticLUF5()).perform(input);
//        ListUser20Writer.export("tobistokt", "58", "1", "C://Data//stox//ListUserFile05__F038000_T2_L105.0-2764.9_SIL.xml", distances);

        /*Map<String, Object> input = new HashMap<>();
        input.put(Functions.PM_READACOUSTICLUF5_FILENAME, "C://Data//Øyvind//2006AA//LUF5");
        List<DistanceBO> distances = (List<DistanceBO>) (new ReadAcousticLUF5()).perform(input);
        ListUser20Writer.export("luf5", "58", "1", "C://Data//Øyvind//2006AA//LUF5.xml", distances);*/
 /*String f = "F://Georg//LUF3-Vilnyus.prn";
        List<DistanceBO> distances = ReadAcousticLUF3.perform(f, false);
        ListUser20Writer.export("vilnius", "RU", "vilnius", f + ".xml", distances);
        f = "F://Georg//lcsnftot_NF_2002";
        distances = ReadAcousticLUF3.perform(f, true);
        ListUser20Writer.export("vilnius", "RU", "vilnius", f + ".xml", distances);*/
        //convertVintertokt("0123_2016_UANA_NANSE", "5004", "2016_Nansen.luf3");
        //convertVintertokt("0120_2015_UANA_NANSE", "5004", "2015_Nansen.luf3");
        /*String d = "E://SigbjørnMehl//2007203//";
        String f = d + "ListComScatter_JH-del1-vin-2007-5840-9200.txt";//"2014_Nansen.luf3";
        List<DistanceBO> distances = ReadAcousticLUF3.perform(f, true, "yyyy.MM.dd");
        ListUser20Writer.export("2007203", "58", "1019", f + ".xml", distances);
         */

 /* convertVintertokt("0114_2014_UANA_NANSE", "5004", "2014_Nansen.luf3");
        convertVintertokt("0113_2013_UFJN_VILNY", "5481", "2013_Vilnyus.luf3");
        convertVintertokt("0111_2012_UANA_NANSE", "5004", "2012_Nansen.luf3");
        convertVintertokt("0108_2011_UANA_NANSE", "5004", "2011_Nansen.luf3");
        convertVintertokt("0122_2010_UANA_NANSE", "5004", "2010_Nansen.luf3");
        convertVintertokt("0104_2009_UANA_NANSE", "5004", "2009_Nansen.luf3");
        convertVintertokt("0121_2009_UFJN_VILNY", "5481", "2009_Vilnyus.luf3");
        convertVintertokt("0114_2008_UANA_NANSE", "5004", "2008_Nansen.luf3");
        convertVintertokt("0091_2005_UFJJ_SMOLE", "5905", "2005_Smolensk.luf3");
        convertVintertokt("0090_2004_UANA_SMOLE", "5905", "2004_Smolensk.luf3");
        convertVintertokt("0085_2003_UASI_PERS3", "5432", "2003_Persey3.luf3");
        convertVintertokt("0083_2002_UASI_PERS3", "5432", "2002_Persey3.luf3");
        convertVintertokt("0079_2001_UGNQ_PERS4", "5489", "2001_Persey4.luf3");
//        convertVintertokt("0114_2014_UANA_NANSE", "5004", "2014_Nansen.luf3");*/
    }

    private static void convertVintertokt(String cruise, String platform, String file) {
        String d = "E://SigbjørnMehl//Vintertokt 2000-2016//";
        String f = d + file;//"2014_Nansen.luf3";
        List<DistanceBO> distances = ReadAcousticLUF3.perform(f);
        ListUser20Writer.export(cruise, "RU", "5004", f + ".xml", distances);
    }

    private static void convertMareksLUFData() {
        String lok = "E://Marek//LUF";
        File fdir = new File(lok);
        File[] fl = fdir.listFiles();
        for (File f : fl) {
            try {
                System.out.println(f);
                String[] fn = f.getName().split("_");
                String cruise = fn[0];
                List<DistanceBO> distances = performMarekLufDataConversion(f.getPath());
                ListUser20Writer.export(cruise, "58", "1172", lok + "XML/" + f.getName().substring(0, f.getName().length() - 4) + ".xml", distances);
            } catch (Exception ex) {
                Logger.getLogger(Luf3ToLuf20.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static List<DistanceBO> performMarekLufDataConversion(String fileName) throws Exception {
        // Try as absolute file first, then as relative to workpath
        List<DistanceBO> distances = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd H:mm");
        /*DateTimeFormatter formatter
                = new DateTimeFormatterBuilder().appendPattern("yy-MM-dd HH:mm")
                        .appendValueReduced(
                                ChronoField.YEAR, 2, 2, 1900
                        ).toFormatter();*/
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;

            String[] hdr = null;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                String[] elms = line.split("/t");
                if (elms.length < 9) {
                    continue;
                }
                if (line.contains("JDAY")) {
                    hdr = line.trim().split("//s+");
                    continue;
                }
                LocalDateTime dateTime = LocalDateTime.parse(elms[3], formatter);
                if (dateTime.getYear() > 2090) {
                    dateTime = dateTime.minusYears(100);
                }
                DistanceBO dist = new DistanceBO();
                distances.add(dist);
                dist.setStart_time(Date.from(dateTime.atZone(ZoneId.systemDefault()).toInstant()));
                dist.setLat_start(Conversion.safeStringtoDouble(elms[2]));
                dist.setLon_start(Conversion.safeStringtoDouble(elms[1]));
                Double startLog = Conversion.safeStringtoDouble(elms[4]);
                Double stopLog = Conversion.safeStringtoDouble(elms[5]);
                dist.setLog_start(startLog);
                dist.setIntegrator_dist(stopLog - startLog);
                //Double dep = Conversion.safeStringtoDouble(elms[7]);
                /*if (dep != null && !dep.equals(999d)) {
                    dist.setPel_ch_thickness(dep);
                }*/
                FrequencyBO f = new FrequencyBO();
                f.setDistance(dist);
                dist.getFrequencies().add(f);
                f.setFreq(38000);
                f.setTranceiver(1);
                f.setNum_pel_ch(1);
                for (int i = 8; i < elms.length; i++) {
                    Double saval = Conversion.safeStringtoDoubleNULL(elms[i]);
                    if (saval == null || saval == 0d) {
                        continue;
                    }
                    String acoStr = hdr[i];
                    Integer acoCat = EchosounderUtils.acoCatFromAcoStr(acoStr);
                    if (acoCat == null) {
                        throw new Exception("Error missing " + acoStr);
                        //continue;
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
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Luf3ToLuf20.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Luf3ToLuf20.class.getName()).log(Level.SEVERE, null, ex);
        }
        return distances;
    }
}
