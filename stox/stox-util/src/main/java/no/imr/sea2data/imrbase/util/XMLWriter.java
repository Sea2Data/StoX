package no.imr.sea2data.imrbase.util;

import java.util.Iterator;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Utility class for writing xml documents
 *
 * @author aasmunds
 */
public final class XMLWriter {

    /**
     * Private constructor
     */
    private XMLWriter() {
    }

    public static class StAXWriterException extends Exception {

        public StAXWriterException(Throwable cause) {
            super(cause);
        }
    }

    private static String getIndentByLevel(Integer level) {
        StringBuilder buf = new StringBuilder("");
        for (int i = 0; i < level; i++) {
            buf.append("  ");
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
     * @throws no.imr.sea2data.core.util.StAXWriter.StAXWriterException
     */
    public static void writeXMLElement(Integer level, XMLStreamWriter xmlw, String elmName, String characters) throws StAXWriterException {
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
     * @throws no.imr.sea2data.core.util.StAXWriter.StAXWriterException
     */
    public static void writeXMLElement(Integer level, XMLStreamWriter xmlw, String elmName, String characters, Map attributes) throws StAXWriterException {
        try {
            writeXMLElementNameAndAttributes(level, xmlw, elmName, attributes);
            xmlw.writeCharacters(characters);
            xmlw.writeEndElement();
            xmlw.writeCharacters("\n");
        } catch (XMLStreamException ex) {
            throw new StAXWriterException(ex);
        }
    }

    private static void writeXMLElementNameAndAttributes(Integer level, XMLStreamWriter xmlw, String elmName, Map attributes) throws StAXWriterException {
        try {
            xmlw.writeCharacters(getIndentByLevel(level));
            xmlw.writeStartElement(elmName);
            if (attributes != null) {
                Iterator i = attributes.entrySet().iterator();
                while (i.hasNext()) {
                    Map.Entry me = (Map.Entry) i.next();
                    xmlw.writeAttribute((String) me.getKey(), (String) me.getValue());
                }
            }
        } catch (Exception ex) {
            throw new StAXWriterException(ex);
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
     * @throws no.imr.sea2data.core.util.StAXWriter.StAXWriterException
     */
    public static void writeXMLElementStart(Integer level, XMLStreamWriter xmlw, String elmName, Map attributes) throws StAXWriterException {
        try {
            writeXMLElementNameAndAttributes(level, xmlw, elmName, attributes);
            xmlw.writeCharacters("\n");
        } catch (Exception ex) {
            throw new StAXWriterException(ex);
        }
    }

    public static void writeXMLElementStartWithoutNewline(Integer level, XMLStreamWriter xmlw, String elmName, Map attributes) throws StAXWriterException {
        try {
            writeXMLElementNameAndAttributes(level, xmlw, elmName, attributes);
        } catch (Exception ex) {
            throw new StAXWriterException(ex);
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
     * @throws no.imr.sea2data.core.util.StAXWriter.StAXWriterException
     */
    public static void writeXMLElementStart(Integer level, XMLStreamWriter xmlw, String elmName) throws StAXWriterException {
        writeXMLElementStart(level, xmlw, elmName, null);
    }

    /**
     * Write the next end element that is on the stack
     *
     * @param level
     * @param xmlw
     * @throws no.imr.sea2data.core.util.StAXWriter.StAXWriterException
     */
    public static void writeXMLElementEnd(Integer level, XMLStreamWriter xmlw) throws StAXWriterException {
        try {
            xmlw.writeCharacters(getIndentByLevel(level));
            xmlw.writeEndElement();
            xmlw.writeCharacters("\n");
        } catch (Exception ex) {
            throw new StAXWriterException(ex);
        }
    }
}
