package no.imr.stox.functions.acoustic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.echosounderbo.FrequencyBO;
import no.imr.sea2data.echosounderbo.SABO;
import no.imr.sea2data.imrbase.math.Calc;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrbase.util.IMRdate;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.EchosounderUtils;
import no.imr.stox.functions.utils.Functions;

/**
 * Method to read the LUF5 into Acoustic data
 *
 * @author Åsmund
 */
public class ReadAcousticLUF5 {

    /*@Override
    public Object perform(Map<String, Object> input) {
        String fileName = (String) input.get(Functions.PM_READACOUSTICLUF5_FILENAME);
        List<DistanceBO> distances = (List<DistanceBO>) input.get(Functions.PM_READACOUSTICLUF5_ACOUSTICDATA);
        // Try as absolute file first, then as relative to workpath
        if (!(new File(fileName)).exists()) {
            fileName = (String) input.get(Functions.PM_PROJECTFOLDER) + "/" + fileName;
        }
        return perform(fileName, distances);
    }*/

    public static List<DistanceBO> perform(String fileName, List<DistanceBO> distances) {
        if (distances == null) {
            distances = new ArrayList<>();
        }
        int lineno = 0;
        BufferedReader reader = null;
        try {
            File aFile = new File(fileName);
            reader = new BufferedReader(new FileReader(aFile));
            int linetype;
            int maxtimechars = 0;
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] elements = line.split("\\s+");
                linetype = lineno % 3;
                switch (linetype) {
                    case 0: {
                        String timestr = elements[5];
                        maxtimechars = Math.max(maxtimechars, timestr.length());
                    }
                }
                lineno = lineno + 1;
            }
            String timeformatstr = (maxtimechars <= 6 ? "HHmmss" : "HHmmssSS");
            String dateformatstr = "yyyyMMdd";
            String timestampformatstr = dateformatstr + timeformatstr;
            lineno = 0;
            reader = new BufferedReader(new FileReader(aFile));
            String[] firstLine = null;
            String[] secondLine = null;
            String[] thirdLine = null;
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) {
                    continue;
                }
                String[] elements = line.trim().split("\\s+");
                linetype = lineno % 3;
                lineno = lineno + 1;
                switch (linetype) {
                    case 0:
                        firstLine = elements;
                        continue;
                    case 1:
                        secondLine = elements;
                        continue;
                    case 2:
                        thirdLine = elements;
                        break;
                }
                if (createDistanceAndAddValues(firstLine, timeformatstr, timestampformatstr, dateformatstr, distances, secondLine, thirdLine)) {
                    break;
                }
            }
        } catch (IOException e) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ex) {
                }
            }
        }
        return distances;
    }


    private static boolean createDistanceAndAddValues(String[] firstLine, String timeformatstr, String timestampformatstr, String dateformatstr,
            List<DistanceBO> distances, String[] secondLine, String[] thirdLine) {
        Date startTime = null;
        startTime = createStartTime(firstLine, timeformatstr, timestampformatstr, dateformatstr);
        Double logStart = Calc.roundTo(Conversion.safeStringtoDouble(firstLine[5]), 3);
        Double logStop = Calc.roundTo(Conversion.safeStringtoDouble(firstLine[6]), 3);

        DistanceBO dist = new DistanceBO();
        dist.setId(UUID.randomUUID().toString().replace("-", ""));
        dist.setCruise(firstLine[0]);
        dist.setNation(firstLine[1]);
        dist.setPlatform(firstLine[2]);
        Double val = logStart;
        dist.setLog_start(val);
        dist.setStart_time(startTime);

        DistanceBO d = EchosounderUtils.findDistance(distances, dist.getKey());
        if (d == null) {
            distances.add(dist);
        } else {
            dist = d;
        }
        dist.setIntegrator_dist(logStop - logStart);
        Double latStart = Conversion.safeStringtoDouble(firstLine[7]);
        dist.setLat_start(latStart);
        Double lonStart = Conversion.safeStringtoDouble(firstLine[8]);
        dist.setLon_start(lonStart);
        dist.setPel_ch_thickness(Conversion.safeStringtoDouble(secondLine[0]));
        dist.setBot_ch_thickness(Conversion.safeStringtoDouble(thirdLine[0]));
        dist.setInclude_estimate(1); // default
        // Search for distance
        createAndInsertFrequency(dist, firstLine, secondLine, thirdLine);
        return false;
    }

    private static Date createStartTime(String[] firstLine, String timeformatstr, String timestampformatstr, String dateformatstr) {
        String datestr = firstLine[3];
        String timestr = firstLine[4];
        DateFormat timestampFormat = IMRdate.getDateFormat(timestampformatstr);
        String datetimestr = String.format("%0" + dateformatstr.length() + "d", Integer.valueOf(datestr))
                + String.format("%0" + timeformatstr.length() + "d", Integer.valueOf(timestr));
        try {
            return timestampFormat.parse(datetimestr);
        } catch (ParseException ex) {
        }
        return null;
    }

    private static void createAndInsertFrequency(DistanceBO dist, String[] headerElements, String[] pelagicElements, String[] bottomElements) {
        String acocatno;
        // Construct frequency by logical keys
        FrequencyBO freq = new FrequencyBO();
        freq.setDistance(dist);
        freq.setDistance(dist);
        freq.setFreq(Integer.valueOf(headerElements[11]));
        freq.setTranceiver(Integer.valueOf(headerElements[12]));
        // Search for frequency
        FrequencyBO ff = null;
        for (FrequencyBO f : dist.getFrequencies()) {
            if (Objects.equals(f.getFreq(), freq.getFreq()) && Objects.equals(f.getTranceiver(), freq.getTranceiver())) {
                ff = f;
            }
        }
        if (ff == null) {
            dist.getFrequencies().add(freq);
        } else {
            freq = ff;
        }
        // Set the information fields
        freq.setMin_bot_depth(Conversion.safeStringtoDouble(headerElements[9]));
        freq.setUpper_integrator_depth(0.0);
        freq.setUpper_interpret_depth(0.0);
        freq.setMax_bot_depth(Conversion.safeStringtoDouble(headerElements[10]));
        freq.setLower_integrator_depth(freq.getMax_bot_depth());
        freq.setLower_interpret_depth(freq.getMax_bot_depth());
        freq.setThreshold(Conversion.safeStringtoDouble(headerElements[13]));
        // TODO: why do we run this method??? isn't always the number in elements[1] the number of pel/bot channels?
        freq.setNum_pel_ch(setNumChannels(pelagicElements));
        freq.setNum_bot_ch(setNumChannels(bottomElements));
        freq.setQuality(1); // default
        // Get guid from acocat no.
        acocatno = headerElements[14];

        if (freq.getNum_pel_ch() > 0) {
            createSAValues(freq.getNum_pel_ch(), pelagicElements, "P", acocatno, freq);
        }
        if (freq.getNum_bot_ch() > 0) {
            createSAValues(freq.getNum_bot_ch(), bottomElements, "B", acocatno, freq);
        }
    }

    private static int setNumChannels(String[] elements) {
        return elements.length >= 4 ? Integer.valueOf(elements[1]) : 0;
    }

    private static void createSAValues(Integer numChanels, String[] elements, String p, String acocat, FrequencyBO freq) {
        for (int elm = 0; elm < numChanels; elm++) {
            Double saval = 0.0;
            if (elm + 3 < elements.length) {
                saval = Conversion.safeStringtoDouble(elements[3 + elm]);
            }
            if (saval == 0) {
                continue;
            }
            SABO sa = new SABO();
            sa.setFrequency(freq);
            sa.setSa(saval);
            sa.setAcoustic_category(acocat);
            sa.setCh_type(p);
            sa.setCh(elm + 1);
            freq.getSa().add(sa);
        }
    }
}
