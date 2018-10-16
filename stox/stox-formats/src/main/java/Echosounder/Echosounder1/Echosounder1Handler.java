/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Echosounder.Echosounder1;

import EchoSounderTypes.v1.EchosounderDatasetType;
import XMLHandling.NamespaceVersionHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

/**
 *
 * @author Edvin Fuglebakk edvin.fuglebakk@imr.no
 */
public class Echosounder1Handler extends NamespaceVersionHandler<EchosounderDatasetType> {

    public Echosounder1Handler() {
        this.latestNamespace = "http://www.imr.no/formats/nmdechosounder/v1";
        this.latestBioticClass = EchosounderDatasetType.class;
        this.compatibleNamespaces = new HashSet<>();
    }
    
    @Override
    public EchosounderDatasetType read(InputStream xml) throws JAXBException, XMLStreamException, ParserConfigurationException, SAXException, IOException{
        return super.read(xml);
    }
    
    
    @Override
    public void save(OutputStream xml, EchosounderDatasetType data) throws JAXBException{
        super.save(xml, data);
    }
}
