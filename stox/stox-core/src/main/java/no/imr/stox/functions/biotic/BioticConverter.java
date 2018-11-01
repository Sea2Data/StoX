/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.biotic;

import Biotic.Biotic1.Biotic1Handler;
import Biotic.Biotic3.Biotic3Handler;
import Biotic.BioticConversionException;
import BioticTypes.v3.MissionsType;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

/**
 *
 * @author aasmunds
 */
public class BioticConverter {

    public static void convertBioticFileToV3(String fileName, String outFileName) {
        try {
            File xml = new File(fileName);
            Biotic3Handler instance = new Biotic3Handler();
            MissionsType result = instance.read(xml);
            instance.save(new FileOutputStream(outFileName), result);
        } catch (JAXBException | XMLStreamException | ParserConfigurationException | SAXException | IOException ex) {
            ex.printStackTrace();
        }
    }

    public static void convertBioticFileToV1_4(String fileName, String outFileName) {
        try {
            File xml = new File(fileName);
            Biotic3Handler inst3 = new Biotic3Handler();
            MissionsType m3 = inst3.read(xml);
            Biotic1Handler inst1_4 = new Biotic1Handler();
            BioticTypes.v1_4.MissionsType mConverted = inst1_4.convertBiotic3(m3);
            inst1_4.save(new FileOutputStream(outFileName), mConverted);
        } catch (JAXBException | XMLStreamException | ParserConfigurationException | SAXException | IOException | BioticConversionException ex) {
            ex.printStackTrace();
        }
    }
}
