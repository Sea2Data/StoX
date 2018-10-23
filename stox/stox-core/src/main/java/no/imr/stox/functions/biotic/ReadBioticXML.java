package no.imr.stox.functions.biotic;

import Biotic.Biotic3.Biotic3Handler;
import BioticTypes.v3.FishstationType;
import BioticTypes.v3.MissionType;
import BioticTypes.v3.MissionsType;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.AbstractFunction;
import no.imr.sea2data.imrbase.exceptions.XMLReaderException;
import no.imr.sea2data.imrbase.util.Conversion;
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
            translateBioticV3ToBO(result.getMission(), stations);
        } catch (Exception e) {

        }
        // Read by StaX into FishStationBO
        try (InputStream stream = new FileInputStream(fileName)) {
            BioticXMLReader reader = new BioticXMLReader(stations, model.getProject());
            reader.readXML(stream);
        } catch (XMLReaderException | IOException ex) {
            logger.error("Error reading Biotic XML", ex);
        }
    }


    private void translateBioticV3ToBO(List<MissionType> mission, List<FishstationBO> stations) {
        for (MissionType mt : mission) {
            for (FishstationType fs : mt.getFishstation()) {
                FishstationBO fbo = new FishstationBO();
                stations.add(fbo);
                fbo.setArea(Conversion.safeStringtoIntegerNULL(fs.getArea()));
                fbo.setBottomDepthStart(fs.getBottomdepthstart());
                fbo.setBottomDepthStop(fs.getBottomdepthstop());
                fbo.setCallSignal(mt.getCallsignal());
                fbo.setComment(fs.getStationcomment());
                fbo.setCruise(mt.getCruise());
                fbo.setDirection(fs.getDirection());
                fbo.setDistance(fs.getDistance());
                fbo.setFishingDepthCount(fs.getFishingdepthcount());
                fbo.setFishingDepthMax(fs.getFishingdepthmax());
                fbo.setFishingDepthMin(fs.getFishingdepthmin());
                fbo.setGear(fs.getGear());
                fbo.setGearCondition(fs.getGearcondition());
                fbo.setGearCount(fs.getGearcount());
                fbo.setGearNo(fs.getGearno());
                fbo.setLatitudeEnd(fs.getLatitudeend());
                fbo.setLatitudeStart(fs.getLatitudestart());
                fbo.setLocation(fs.getLocation());
                fbo.setLogStart(fs.getLogstart());
                fbo.setLogStop(fs.getLogstop());
                fbo.setLongitudeEnd(fs.getLongitudeend());
                fbo.setLongitudeStart(fs.getLongitudestart());
                fbo.setMissionType(mt.getMissiontype());
                fbo.setNation(fs.getNation());
                fbo.setCatchPlatform(fs.getCatchplatform());
                fbo.setPlatformName(mt.getPlatformname());
                fbo.setSerialNo(fs.getSerialnumber());
                fbo.setSoaktime(fs.getSoaktime());
                fbo.setStation(fs.getStation());
                fbo.setStationStartDate(fs.getStationstartdate() == null ? null : Date.from(fs.getStationstartdate().atStartOfDay().toInstant(ZoneOffset.UTC)));
                fbo.setStationStartTime(fs.getStationstarttime() == null ? null : Date.from(fs.getStationstarttime().atDate(LocalDate.MAX).toInstant(ZoneOffset.UTC)));
                fbo.setStationStopDate(fs.getStationstopdate() == null ? null : Date.from(fs.getStationstopdate().atStartOfDay().toInstant(ZoneOffset.UTC)));
                fbo.setStationStopTime(fs.getStationstoptime() == null ? null : Date.from(fs.getStationstoptime().atDate(LocalDate.MAX).toInstant(ZoneOffset.UTC)));
                fbo.setStationType(fs.getStationtype());
                fbo.setSystem(Conversion.safeStringtoIntegerNULL(fs.getSystem()));
                fbo.setTrawlDoorSpread(fs.getTrawldoorspread());
                fbo.setVerticalTrawlOpening(fs.getVerticaltrawlopening());
                fbo.setVesselSpeed(fs.getVesselspeed());
                fbo.setWireLength(fs.getWirelength() != null ? fs.getWirelength().intValue() : null);
                fbo.setGearCount(fs.getGearcount());
            }
        }
    }
}
