package no.imr.stox.functions.biotic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.AbstractFunction;
import no.imr.sea2data.imrbase.exceptions.XMLReaderException;
import no.imr.stox.log.ILogger;
import no.imr.stox.model.IModel;

/**
 *
 * This function reads a biotic file and stores it in the datastorage. This
 * function requires that the data input is in XML format.
 *
 * @author kjetilf
 */
public class ReadBioticXML extends AbstractFunction {

    /**
     * @param input Contains Biotic XML filename, Working directory and logger
     * @return Matrix object of type FISHSTATIONS - see DataTypeDescription.txt
     */
    @Override
    public Object perform(Map<String, Object> input) {
        List<FishstationBO> stations = new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            String fileName = (String) input.get("FileName" + i);
            if (fileName == null) {
                continue;
            }
            readStations(input, stations, fileName);
        }
        return stations;
    }

    public void readStations(Map<String, Object> input, List<FishstationBO> stations, String fileName) {
        IModel model = (IModel) input.get(Functions.PM_MODEL);
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        if (!(new File(fileName)).exists()) {
            fileName = (String) input.get(Functions.PM_PROJECTFOLDER) + "/" + fileName;
        }
        // Read by StaX into FishStationBO
        try (InputStream stream = new FileInputStream(fileName)) {
            BioticXMLReader reader = new BioticXMLReader(stations, model.getProject());
            reader.readXML(stream);
        } catch (XMLReaderException | IOException ex) {
            logger.error("Error reading Biotic XML", ex);
        }
    }
}
