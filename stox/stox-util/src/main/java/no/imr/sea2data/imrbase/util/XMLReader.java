package no.imr.sea2data.imrbase.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Stack;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.XMLEvent;
import no.imr.sea2data.imrbase.exceptions.XMLReaderException;

/**
 *
 * @author aasmunds
 */
public class XMLReader {

    XMLEventReader eventReader;
    XMLEvent event;
    Stack objects = new Stack();
    Stack<String> elements = new Stack<String>();

    /**
     * Character data is compound of successive character events divided by
     * special escapements i.e: less than or greater sign.
     *
     * @return
     * @throws XMLReaderException
     */
    public String getCurrentElementValue() throws XMLReaderException {
        String res = "";
        try {
            nextEventReady = false;
            while (eventReader.hasNext()) {
                event = eventReader.nextEvent();
                if (event.isCharacters()) {
                    res += event.asCharacters().getData();
                } else {
                    nextEventReady = true;
                    break;
                }
            }
        } catch (XMLStreamException ex) {
            throw new XMLReaderException(ex);
        }
        return res;
    }

    public String getCurrentAttributeValue(String attname) {
        Attribute att = event.asStartElement().getAttributeByName(QName.valueOf(attname));
        return att != null ? att.getValue() : null;
    }
    boolean nextEventReady;

    public void readXML(InputStream in) throws XMLReaderException, IOException {

        // First create a new XMLInputFactory
        XMLInputFactory inputFactory = XMLInputFactory.newInstance();
        //Boolean hasElements = false;
        try {
            eventReader = inputFactory.createXMLEventReader(in);
            // Read the XML document
            nextEventReady = false;
            while (eventReader.hasNext()) {
                if (!nextEventReady) {
                    event = eventReader.nextEvent();
                }
                nextEventReady = false;
                Object current = objects.isEmpty() ? null : objects.peek();
                String elmName = elements.isEmpty() ? null : elements.peek();;
                if (event.isStartElement()) {
                    //hasElements = true;
                    elmName = event.asStartElement().getName().getLocalPart();
                    Object object = getObject(current, elmName);
                    if (object != null) {
                        objects.push(object);
                        elements.push(elmName);
                        Iterator<Attribute> itr = event.asStartElement().getAttributes();
                        while (itr.hasNext()) {
                            Attribute att = itr.next();
                            onObjectValue(object, att.getName().getLocalPart(), att.getValue());
                        }
                    } else {
                        if (!onObjectElement(current, elmName)) {
                            onObjectValue(current, elmName, getCurrentElementValue()); // This will consume the next event
                        }
                    }
                } else if (event.isEndElement()) {
                    String elm = event.asEndElement().getName().getLocalPart();
                    if (!elements.isEmpty() && elements.peek().equals(elm)) {
                        elements.pop();
                        objects.pop();
                    }
                } else if (elmName != null && event.isCharacters()) {
                    // This is a special case where the object tag itsself contains data, like the sa tag in luf20
                    String data = event.asCharacters().getData().trim();
                    if (!data.isEmpty()) {
                        onObjectValue(current, elmName, data);
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new XMLReaderException(ex);
        } 
    }

    /**
     * event called when a object value is detected (either key or not)
     *
     * @param current
     * @param elmName
     * @param value
     */
    protected void onObjectValue(Object current, String elmName, String value) {
        // Override at use
    }

    /*
     * called when a new element level in the structure is about to be detected
     */
    protected Object getObject(Object current, String elmName) {
        // Override at use
        return null;
    }

    /**
     * Override this to handle object element, return true if handled.
     *
     * @param current
     * @param elmName
     * @return
     */
    protected boolean onObjectElement(Object current, String elmName) {
        return false;
    }

}
