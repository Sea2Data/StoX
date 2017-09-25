package no.imr.stox.functions.landing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.AbstractFunction;
import no.imr.sea2data.imrbase.exceptions.XMLReaderException;
import no.imr.stox.bo.LandingData;
import no.imr.stox.bo.landing.SluttSeddel;
import no.imr.stox.log.ILogger;

/**
 *
 * This function reads a biotic file and stores it in the datastorage. This
 * function requires that the data input is in XML format.
 *
 * @author kjetilf
 */
public class ReadLandingXML extends AbstractFunction {

    /**
     * @param input Contains Biotic XML filename, Working directory and logger
     * @return Matrix object of type FISHSTATIONS - see DataTypeDescription.txt
     */
    @Override
    public Object perform(Map<String, Object> input) {
        List<SluttSeddel> landings = new LandingData();
        for (int i = 1; i <= 20; i++) {
            String fileName = (String) input.get("FileName" + i);
            if (fileName == null) {
                continue;
            }
            readSluttsedler(input, landings, fileName);
        }
        return landings;
    }

    public void readSluttsedler(Map<String, Object> input, List<SluttSeddel> landings, String fileName) {
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        if (!(new File(fileName)).exists()) {
            fileName = (String) input.get(Functions.PM_PROJECTFOLDER) + "/" + fileName;
        }
        try (InputStream stream = new FileInputStream(fileName)) {
            LandingXMLReader reader = new LandingXMLReader(landings);
            reader.readXML(stream);
        } catch (XMLReaderException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
