package no.imr.stox.functions.biotic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.AbstractFunction;
import no.imr.sea2data.imrbase.exceptions.XMLReaderException;
import no.imr.stox.bo.BioticXMLData;
import no.imr.stox.functions.utils.JDOMUtils;
import no.imr.stox.log.ILogger;
import no.imr.stox.model.IModel;
import org.jdom2.Element;

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
        Object stations = Functions.XMLDATA ? new BioticXMLData() : new ArrayList<>();
        for (int i = 1; i <= 20; i++) {
            String fileName = (String) input.get("FileName" + i);
            if (fileName == null) {
                continue;
            }
            readStations(input, stations, fileName);
        }
        return stations;
    }

    public void readStations(Map<String, Object> input, Object stations, String fileName) {
        IModel model = (IModel) input.get(Functions.PM_MODEL);
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        if (!(new File(fileName)).exists()) {
            fileName = (String) input.get(Functions.PM_PROJECTFOLDER) + "/" + fileName;
        }
        if (Functions.XMLDATA) {
            // Read by JDOM into Element
            List<Element> fsList = JDOMUtils.readXML(fileName, "fishstation");
            ((BioticXMLData)stations).addAll(fsList);
            List<Element> transparencyFields = new ArrayList<>();
            Consumer<Element> elmConsumer = elm -> {
                switch (elm.getName()) {
                    case "fishstation":
                        // Copy cruise element to BioticData fishstation level.
                        Element parentCruiseElm = JDOMUtils.getChild(elm.getParentElement(), "cruise");
                        if (parentCruiseElm != null) {
                            JDOMUtils.setAttribute(elm, true, parentCruiseElm.getName(), parentCruiseElm.getText());
                        } else {
                            JDOMUtils.setAttribute(elm, true, "cruise", JDOMUtils.getAttributeValue(elm.getParentElement(), "missiontype") + "-"
                                    + JDOMUtils.getAttributeValue(elm.getParentElement(), "year"));
                        }
                        // Copy mission type to an element.
                        JDOMUtils.insertElement(elm, 0, "missiontype", elm.getParentElement().getAttributeValue("missiontype"));
                        break;
                    case "agedetermination":
                        elm.removeAttribute("no"); // This is a generated key and should not be included, until multiple agereadings is used.
                    case "tag":
                        // agedetermination and tag attributes and elements will be presented as individual elements.
                        //if (elm.getChildren().size() == 1) {
                            transparencyFields.add(elm);
                        //}

                }
            };
            Predicate<Element> branchPredicate = e -> !e.getName().equals("prey");
            JDOMUtils.consumeTree(fsList.stream(), null, elmConsumer, null, branchPredicate);
            transparencyFields.stream().forEach(elm -> {
                // Transfer elements and attributes up to individual as transparancy fields
                elm.getAttributes().forEach(a -> {
                    JDOMUtils.insertElement(elm.getParentElement(), -1, a.getName(), a.getValue());
                });
                elm.getChildren().forEach(ce -> {
                    JDOMUtils.insertElement(elm.getParentElement(), -1, ce.getName(), ce.getValue());
                });
                elm.detach();
            });
        } else {
            // Read by StaX into FishStationBO
            try (InputStream stream = new FileInputStream(fileName)) {
                BioticXMLReader reader = new BioticXMLReader((List)stations, model.getProject());
                reader.readXML(stream);
            } catch (XMLReaderException | IOException ex) {
                logger.error("Error reading Biotic XML", ex);
            }
        }
    }
}
