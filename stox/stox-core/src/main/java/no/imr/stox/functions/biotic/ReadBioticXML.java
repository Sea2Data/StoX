package no.imr.stox.functions.biotic;

import Biotic.Biotic3.Biotic3Handler;
import BioticTypes.v3.AgedeterminationType;
import BioticTypes.v3.CatchsampleType;
import BioticTypes.v3.FishstationType;
import BioticTypes.v3.IndividualType;
import BioticTypes.v3.MissionType;
import BioticTypes.v3.MissionsType;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import no.imr.sea2data.biotic.bo.AgeDeterminationBO;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.biotic.bo.CatchSampleBO;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.AbstractFunction;
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
        try {
            File xml = new File(fileName);
            Biotic3Handler instance = new Biotic3Handler();
            MissionsType result = instance.read(xml);
            connectBioticV3ToBO(result.getMission(), stations);
        } catch (Exception e) {

        }
        // Read by StaX into FishStationBO
        /*try (InputStream stream = new FileInputStream(fileName)) {
            BioticXMLReader reader = new BioticXMLReader(stations, model.getProject());
            reader.readXML(stream);
        } catch (XMLReaderException | IOException ex) {
            logger.error("Error reading Biotic XML", ex);
        }*/
    }

    private void connectBioticV3ToBO(List<MissionType> mission, List<FishstationBO> stations) {
        for (MissionType mt : mission) {
            for (FishstationType fs : mt.getFishstation()) {
                FishstationBO fbo = new FishstationBO(fs);
                stations.add(fbo);
                for (CatchsampleType cs : fs.getCatchsample()) {
                    CatchSampleBO sbo = fbo.addCatchSample(cs);
                    for (IndividualType i : cs.getIndividual()) {
                        IndividualBO ibo = sbo.addIndividual(i);
                        for (AgedeterminationType a : i.getAgedetermination()) {
                            ibo.addAgeDetermination(a);
                        }
                    }
                }
            }
        }
    }
}
