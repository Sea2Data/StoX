package no.imr.stox.functions.acoustic;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.echosounderbo.FrequencyBO;
import no.imr.sea2data.echosounderbo.SABO;
import no.imr.sea2data.imrbase.math.Calc;
import no.imr.sea2data.imrbase.math.ImrMath;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrbase.util.IMRdate;
import no.imr.sea2data.imrbase.util.ImrSort;

/**
 * A List20Writer to export BO structure to list user 20. This function is
 * written to let one go directly from a luf5 file to luf20 file without using
 * any db
 *
 * @author aasmunds
 */
public final class ListUser20Writer {

    private static final int MAX_SA_VALUE_LENGTH = 10;

    /**
     * Hidden constructor
     */
    private ListUser20Writer() {
    }

    public static void export(String cruise, String nation, String platform, String fileName, List<DistanceBO> distances) {
        export(cruise, nation, platform, fileName, distances, true);
    }
    /**
     * Writes the echosounder dataset into the outputstream as a ListUser20 file
     *
     * @param cruise
     * @param fileName
     * @param distances
     */
    public static void export(String cruise, String nation, String platform, String fileName, List<DistanceBO> distances, Boolean compact) {

        try (OutputStream os = new FileOutputStream(fileName)) {
            XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
            XMLStreamWriter xmlw = xmlof.createXMLStreamWriter(os, "UTF-8");
            int level = 0;
            Map<String, String> attr = new LinkedHashMap<>();
            xmlw.writeStartDocument("UTF-8", "1.0");
            xmlw.writeCharacters("\n");

            level = writeHeader(cruise, nation, platform, level, xmlw, compact);

            for (DistanceBO distance : distances) {
                level = writeDistanceAttributes(attr, distance, level, xmlw, compact);

                for (FrequencyBO frequency : distance.getFrequencies()) {
                    level = writeFrequencyAttributes(frequency, level, xmlw, compact);

                    Set<String> channelTypes = new HashSet<>();
                    for (SABO sabo : frequency.getSa()) {
                        channelTypes.add(sabo.getCh_type());
                    }
                    List<String> sortedchannelTypes = new ArrayList<>(channelTypes);
                    // P, B
                    Collections.sort(sortedchannelTypes, new ImrSort.TranslativeComparator(false));
                    for (String channelType : sortedchannelTypes) {
                        attr.put("type", channelType);
                        writeXMLElementStartWithoutNewline(level++, xmlw, "ch_type", attr);
                        Set<String> acocatids = new HashSet<>();
                        for (SABO sabo : frequency.getSa()) {
                            acocatids.add(sabo.getAcoustic_category());
                        }
                        List<String> sortedacocatids = new ArrayList<>(acocatids);
                        Collections.sort(sortedacocatids, new ImrSort.TranslativeComparator(true));
                        boolean first = true;
                        for (String acocatid : sortedacocatids) {
                            if (first) {
                                xmlw.writeCharacters("\n");
                                first = false;
                            }
                            level = writeAcousticCategorySAValues(acocatid, attr, level, xmlw, frequency, channelType);
                        }
                        // ch_type
                        writeXMLElementEnd(--level, xmlw);
                    }
                    // frequency
                    writeXMLElementEnd(--level, xmlw);
                    xmlw.flush();
                }
                // distance
                writeXMLElementEnd(--level, xmlw);

            }
            // distance_list
            writeXMLElementEnd(--level, xmlw);
            // echosounder_dataset
            writeXMLElementEnd(--level, xmlw);
            xmlw.writeEndDocument();
            xmlw.close();
        } catch (IOException | XMLStreamException ex) {
            throw new RuntimeException(ex);
        }
    }
    private static int writeHeader(String cruise, String nation, String platform, int level, XMLStreamWriter xmlw, boolean compact) {
        //DateFormat timeformat = IMRdate.getDefaultDateTimeFormat();
        writeXMLElementStart(level++, xmlw, "echosounder_dataset");
        writeXMLElement(level, xmlw, "report_time", null, null, compact);
        writeXMLElement(level, xmlw, "lsss_version", null, null, compact);
        //writeXMLElement(level, xmlw, "lsss_version", echo.getLsss_version());
        writeXMLElement(level, xmlw, "nation", nation);
        writeXMLElement(level, xmlw, "platform", platform);
        writeXMLElement(level, xmlw, "cruise", cruise);
        writeXMLElementStart(level++, xmlw, "distance_list");
        return level;
    }

    private static int writeFrequencyAttributes(FrequencyBO freqvo, int level, XMLStreamWriter xmlw, boolean compact) {
        Map<String, String> attr = new LinkedHashMap();
        attr.put("freq", freqvo.getFreq().toString());
        attr.put("transceiver", freqvo.getTranceiver().toString());
        writeXMLElementStart(level++, xmlw, "frequency", attr);
        writeXMLElement(level, xmlw, "threshold", freqvo.getThreshold(), null, compact);
        writeXMLElement(level, xmlw, "num_pel_ch", freqvo.getNum_pel_ch(), null, compact);
        writeXMLElement(level, xmlw, "num_bot_ch", freqvo.getNum_bot_ch(), null, compact);
        writeXMLElement(level, xmlw, "min_bot_depth", freqvo.getMin_bot_depth(), null, compact);
        writeXMLElement(level, xmlw, "max_bot_depth", freqvo.getMax_bot_depth(), null, compact);
        writeXMLElement(level, xmlw, "upper_interpret_depth", freqvo.getUpper_interpret_depth(), null, compact);
        writeXMLElement(level, xmlw, "lower_interpret_depth", freqvo.getLower_interpret_depth(), null, compact);
        writeXMLElement(level, xmlw, "upper_integrator_depth", freqvo.getUpper_integrator_depth(), null, compact);
        writeXMLElement(level, xmlw, "lower_integrator_depth", freqvo.getLower_integrator_depth(), null, compact);
        writeXMLElement(level, xmlw, "quality", freqvo.getQuality(), null, compact);
        writeXMLElement(level, xmlw, "bubble_corr", freqvo.getBubble_corr() != null ? freqvo.getBubble_corr() : 1d, null, compact);
        return level;
    }

    private static int writeDistanceAttributes(Map<String, String> attr, DistanceBO distVO, int level, XMLStreamWriter xmlw, Boolean compact) {
        DateFormat timeformat = IMRdate.getDefaultDateTimeFormat();
        attr.put("log_start", distVO.getLog_start().toString());
        attr.put("start_time", timeformat.format(distVO.getStart_time()));
        writeXMLElementStart(level++, xmlw, "distance", attr);
        writeXMLElement(level, xmlw, "stop_time", distVO.getStop_time() != null ? timeformat.format(distVO.getStop_time()) : null, null, compact);
        Double d = Calc.roundTo(distVO.getIntegrator_dist(), 6);
        writeXMLElement(level, xmlw, "integrator_dist", d, null, compact);
        writeXMLElement(level, xmlw, "pel_ch_thickness", distVO.getPel_ch_thickness(), null, compact);
        writeXMLElement(level, xmlw, "bot_ch_thickness", distVO.getBot_ch_thickness(), null, compact);
        writeXMLElement(level, xmlw, "include_estimate", distVO.getInclude_estimate(), null, compact);
        writeXMLElement(level, xmlw, "lat_start", distVO.getLat_start(), null, compact);
        writeXMLElement(level, xmlw, "lat_stop", distVO.getLat_stop(), null, compact);
        writeXMLElement(level, xmlw, "lon_start", distVO.getLon_start(), null, compact);
        writeXMLElement(level, xmlw, "lon_stop", distVO.getLon_stop(), null, compact);
        return level;
    }

    private static int writeAcousticCategorySAValues(String acocatid, Map<String, String> attr, int level, XMLStreamWriter xmlw, FrequencyBO freqbo, String chtype) {
        attr.put("acocat", acocatid);
        writeXMLElementStart(level++, xmlw, "sa_by_acocat", attr);
        for (SABO sabo : freqbo.getSa()) {
            if (!sabo.getAcoustic_category().equalsIgnoreCase(acocatid)) {
                continue;
            }
            if (!sabo.getCh_type().equals(chtype)) {
                continue;
            }
            String ch = Conversion.safeIntegertoString(sabo.getCh());
            attr.put("ch", ch);

            Double value = sabo.getSa();
            if (value == 0d || value < ImrMath.RND_ERR) {
                continue;
            }
            value = Calc.roundTo(value, 13);
            /*if (value.length() > MAX_SA_VALUE_LENGTH) {
                value = String.format("%e", sabo.getSa());
            }*/
            writeXMLElement(level, xmlw, "sa", value, attr);
            attr.remove("ch");
        }
        writeXMLElementEnd(--level, xmlw);
        return level;
    }

    private static String getIndentByLevel(Integer level) {
        StringBuilder buf = new StringBuilder("");
        for (int i = 0; i < level; i++) {
            buf.append("   ");
        }
        return buf.toString();
    }

    /**
     * Writes an xml tag (elmname) to the xmlstreamwriter with the internal
     * value of "characters" on level "level"
     *
     * @param level
     * @param xmlw
     * @param elmName
     * @param characters
     */
    public static void writeXMLElement(Integer level, XMLStreamWriter xmlw, String elmName, Object characters) {
        writeXMLElement(level, xmlw, elmName, characters, null);
    }

    /**
     * Write the element with the attributes contained in the input map and the
     * internal element in characters at level level
     *
     * @param level
     * @param xmlw
     * @param elmName
     * @param characters
     * @param attributes
     */
    public static void writeXMLElement(Integer level, XMLStreamWriter xmlw, String elmName, Object characters, Map attributes) {
        writeXMLElement(level, xmlw, elmName, characters, attributes, false);
    }

    public static void writeXMLElement(Integer level, XMLStreamWriter xmlw, String elmName, Object characters, Map attributes, boolean force) {
        try {
            if (characters == null && !force) {
                return;
            }
            writeXMLElementNameAndAttributes(level, xmlw, elmName, attributes);
            if (characters != null) {
                xmlw.writeCharacters(characters.toString());
            }
            xmlw.writeEndElement();
            xmlw.writeCharacters("\n");
        } catch (XMLStreamException ex) {
            Logger.getLogger(ListUser20Writer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void writeXMLElementNameAndAttributes(Integer level, XMLStreamWriter xmlw, String elmName, Map attributes) {
        try {
            xmlw.writeCharacters(getIndentByLevel(level));
            xmlw.writeStartElement(elmName);
            if (attributes != null) {
                Iterator i = attributes.entrySet().iterator();
                while (i.hasNext()) {
                    Map.Entry me = (Map.Entry) i.next();
                    xmlw.writeAttribute((String) me.getKey(), (String) me.getValue());
                }
                attributes.clear();
            }
        } catch (XMLStreamException ex) {
            Logger.getLogger(ListUser20Writer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Write the element start (that will contain other elements) having the
     * attributes contained in the input map.
     *
     * To end the element use writeXMLElementEnd
     *
     * @param level
     * @param xmlw
     * @param elmName
     * @param attributes
     */
    public static void writeXMLElementStart(Integer level, XMLStreamWriter xmlw, String elmName, Map attributes) {
        try {
            writeXMLElementNameAndAttributes(level, xmlw, elmName, attributes);
            xmlw.writeCharacters("\n");
        } catch (XMLStreamException ex) {
            Logger.getLogger(ListUser20Writer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void writeXMLElementStartWithoutNewline(Integer level, XMLStreamWriter xmlw, String elmName, Map attributes) {
        try {
            writeXMLElementNameAndAttributes(level, xmlw, elmName, attributes);
        } catch (Exception ex) {
            Logger.getLogger(ListUser20Writer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Write an xml element to xmlw as a start element
     *
     * To end the element use writeXMLElementEnd
     *
     * @param level
     * @param xmlw
     * @param elmName
     */
    public static void writeXMLElementStart(Integer level, XMLStreamWriter xmlw, String elmName) {
        writeXMLElementStart(level, xmlw, elmName, null);
    }

    /**
     * Write the next end element that is on the stack
     *
     * @param level
     * @param xmlw
     */
    public static void writeXMLElementEnd(Integer level, XMLStreamWriter xmlw) {
        try {
            xmlw.writeCharacters(getIndentByLevel(level));
            xmlw.writeEndElement();
            xmlw.writeCharacters("\n");
        } catch (XMLStreamException ex) {
            Logger.getLogger(ListUser20Writer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
