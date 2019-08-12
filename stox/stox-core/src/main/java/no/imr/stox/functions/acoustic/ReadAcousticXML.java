/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.acoustic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.stox.util.exceptions.XMLReaderException;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.log.ILogger;

/**
 * This function reads an echosounder file and stores it in the datastorage.
 * This function requires that the data input is in XML format.
 *
 * @author kjetilf
 */
public class ReadAcousticXML extends AbstractFunction {

    /**
     * @param input Contains Acoustic XML filename, Working directory and logger
     * @return Matrix object of type ACOUSTICDATA - see DataTypeDescription.txt
     */
    @Override
    public Object perform(Map<String, Object> input) {
        List<DistanceBO> distances = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            String fileName = (String) input.get("FileName" + i);
            if (fileName == null) {
                continue;
            }
            readDistances(input, distances, fileName);
        }
        return distances;
    }

    public void readDistances(Map<String, Object> input, List<DistanceBO> distances, String fileName) {
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        if (!(new File(fileName)).exists()) {
            fileName = (String) input.get(Functions.PM_PROJECTFOLDER) + "/" + fileName;
        }
        try (InputStream stream = new FileInputStream(fileName)) {
            EchoXMLReader reader = new EchoXMLReader(distances);
            reader.readXML(stream);
        } catch (XMLReaderException | IOException ex) {
            ex.printStackTrace();
            logger.error("XML not properly read", ex);
        }
    }

    // Helper function
    public static List<DistanceBO> perform(String fileName) {
        Map<String, Object> input = new HashMap<>();
        input.put("FileName1", fileName);
        return (List<DistanceBO>) (new ReadAcousticXML()).perform(input);
    }
}
