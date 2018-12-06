package no.imr.stox.functions.landing;

import Landings.LandingsHandler;
import LandingsTypes.v2.LandingsdataType;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.bo.LandingData;
import no.imr.stox.bo.landing.LandingsdataBO;
import no.imr.stox.bo.landing.SeddellinjeBO;
import no.imr.stox.log.ILogger;
import org.xml.sax.SAXException;

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
        List<LandingsdataBO> landings = new LandingData();
        for (int i = 1; i <= 20; i++) {
            String fileName = (String) input.get("FileName" + i);
            if (fileName == null) {
                continue;
            }
            readLandingsdata(input, landings, fileName);
        }
        return landings;
    }

    public void readLandingsdata(Map<String, Object> input, List<LandingsdataBO> landings, String fileName) {
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        if (!(new File(fileName)).exists()) {
            fileName = (String) input.get(Functions.PM_PROJECTFOLDER) + "/" + fileName;
        }
        LandingsHandler h = new LandingsHandler();
        try (InputStream stream = new FileInputStream(fileName)) {
            LandingsdataType ldt = (LandingsdataType) h.read(stream);
            LandingsdataBO ldb = new LandingsdataBO(ldt);
            landings.add(ldb);
            ldt.getSeddellinje().forEach(sl -> {
                ldb.addSeddellinje(new SeddellinjeBO(ldb, sl));
            });
        } catch (JAXBException | XMLStreamException | ParserConfigurationException | SAXException | IOException ex) {
            throw new RuntimeException(ex);
        }
    }
}
