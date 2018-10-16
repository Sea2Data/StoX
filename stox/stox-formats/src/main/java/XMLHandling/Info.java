/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package XMLHandling;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

/**
 *
 * @author Edvin Fuglebakk edvin.fuglebakk@imr.no
 */
public class Info {

    protected static Document getDom(File xml) throws ParserConfigurationException, SAXException, IOException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(xml);
        return doc;
    }

    public static String getNamespace(File xml) throws ParserConfigurationException, SAXException, IOException {
        org.w3c.dom.Element root = getDom(xml).getDocumentElement();
        return ((Node) root).getNamespaceURI();
    }

    public static String getEncoding(File xml) throws ParserConfigurationException, SAXException, IOException {
        Document doc = getDom(xml);

        return doc.getXmlEncoding();

    }
}
