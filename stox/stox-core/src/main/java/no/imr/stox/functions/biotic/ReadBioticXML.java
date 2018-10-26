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
            translateBioticV3ToBO(result.getMission(), stations);
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

    private void translateBioticV3ToBO(List<MissionType> mission, List<FishstationBO> stations) {
        for (MissionType mt : mission) {
            for (FishstationType fs : mt.getFishstation()) {
                FishstationBO fbo = new FishstationBO();
                stations.add(fbo);

                fbo.setArea(fs.getArea());
                fbo.setBottomdepthstart(fs.getBottomdepthstart());
                fbo.setBottomdepthstop(fs.getBottomdepthstop());
                fbo.setCallsignal(mt.getCallsignal());
                fbo.setCatchplatform(fs.getCatchplatform());
                fbo.setCruise(mt.getCruise());
                fbo.setDirection(fs.getDirection());
                fbo.setDistance(fs.getDistance());
                fbo.setFishingdepthcount(fs.getFishingdepthcount());
                fbo.setFishingdepthmax(fs.getFishingdepthmax());
                fbo.setFishingdepthmin(fs.getFishingdepthmin());
                fbo.setGear(fs.getGear());
                fbo.setGearcondition(fs.getGearcondition());
                fbo.setGearcount(fs.getGearcount());
                fbo.setGearno(fs.getGearno());
                fbo.setLatitudeend(fs.getLatitudeend());
                fbo.setLatitudestart(fs.getLatitudestart());
                fbo.setLocation(fs.getLocation());
                fbo.setLogstart(fs.getLogstart());
                fbo.setLogstop(fs.getLogstop());
                fbo.setLongitudestart(fs.getLongitudestart());
                fbo.setLongitudeend(fs.getLongitudeend());
                fbo.setMissiontype(mt.getMissiontype());
                fbo.setNation(fs.getNation());
                fbo.setPlatformname(mt.getPlatformname());
                fbo.setSamplequality(fs.getSamplequality());
                fbo.setSerialnumber(fs.getSerialnumber());
                fbo.setSoaktime(fs.getSoaktime());
                fbo.setStation(fs.getStation());
                fbo.setStationcomment(fs.getStationcomment());
                fbo.setStationstartdate(fs.getStationstartdate());
                fbo.setStationstarttime(fs.getStationstarttime());
                fbo.setStationstopdate(fs.getStationstopdate());
                fbo.setStationstoptime(fs.getStationstoptime());
                fbo.setStationtype(fs.getStationtype());
                fbo.setSystem(fs.getSystem());
                fbo.setTrawldoorspread(fs.getTrawldoorspread());
                fbo.setTrawldoorspreadsd(fs.getTrawldoorspreadsd());
                fbo.setTripno(fs.getTripno());
                fbo.setVerticalTrawlOpening(fs.getVerticaltrawlopening());
                fbo.setVerticaltrawlopeningsd(fs.getVerticaltrawlopeningsd());
                fbo.setVesselspeed(fs.getVesselspeed());
                fbo.setWingspread(fs.getWingspread());
                fbo.setWingspreadsd(fs.getWingspreadsd());
                fbo.setWireLength(fs.getWirelength());
                for (CatchsampleType cs : fs.getCatchsample()) {
                    CatchSampleBO sbo = fbo.addCatchSample();
                    sbo.setCatchcategory(cs.getCatchcategory());
                    sbo.setAphia(cs.getAphia());
                    sbo.setCommonname(cs.getCommonname());
                    sbo.setAgingstructure(cs.getAgingstructure());
                    sbo.setCatchcomment(cs.getCatchcomment());
                    sbo.setCatchcount(cs.getCatchcount());
                    sbo.setCatchpartnumber(cs.getCatchpartnumber());
                    sbo.setCatchproducttype(cs.getCatchproducttype());
                    sbo.setCatchvolume(cs.getCatchvolume());
                    sbo.setCatchweight(cs.getCatchweight());
                    sbo.setConservation(cs.getConservation());
                    sbo.setForeignobject(cs.getForeignobject());
                    sbo.setGroup(cs.getGroup());
                    sbo.setLengthmeasurement(cs.getLengthmeasurement());
                    sbo.setLengthsamplecount(cs.getLengthsamplecount());
                    sbo.setLengthsamplevolume(cs.getLengthsamplevolume());
                    sbo.setLengthsampleweight(cs.getLengthsampleweight());
                    sbo.setParasite(cs.getParasite());
                    sbo.setRaisingfactor(cs.getRaisingfactor());
                    sbo.setSampletype(cs.getSampletype());
                    sbo.setSampleproducttype(cs.getSampleproducttype());
                    sbo.setSpecimensamplecount(cs.getSpecimensamplecount());
                    sbo.setStomach(cs.getStomach());
                    sbo.setTissuesample(cs.getTissuesample());
                    for (IndividualType i : cs.getIndividual()) {
                        IndividualBO ibo = sbo.addIndividual();
                        ibo.setBlackspot(i.getBlackspot());
                        ibo.setDigestion(i.getDigestion());
                        ibo.setEggstage(i.getEggstage());
                        ibo.setFat(i.getFat());
                        ibo.setFungusheart(i.getFungusheart());
                        ibo.setFungusouter(i.getFungusouter());
                        ibo.setFungusspores(i.getFungusspores());
                        ibo.setGillworms(i.getGillworms());
                        ibo.setGonadweight(i.getGonadweight());
                        ibo.setIndividualcomment(i.getIndividualcomment());
                        ibo.setIndividualproducttype(i.getIndividualproducttype());
                        ibo.setIndividualvolume(i.getIndividualvolume());
                        ibo.setIndividualweight(i.getIndividualweight());
                        ibo.setLength(i.getLength());
                        ibo.setLengthresolution(i.getLengthresolution());
                        ibo.setLiver(i.getLiver());
                        ibo.setLiverparasite(i.getLiverparasite());
                        ibo.setLiverweight(i.getLiverweight());
                        ibo.setMaturationstage(i.getMaturationstage());
                        ibo.setSex(i.getSex());
                        ibo.setSpecialstage(i.getSpecialstage());
                        ibo.setSpecimenid(i.getSpecimenid());
                        ibo.setStomachfillfield(i.getStomachfillfield());
                        ibo.setStomachfilllab(i.getStomachfilllab());
                        ibo.setSwollengills(i.getSwollengills());
                        ibo.setVertebraecount(i.getVertebraecount());
                        for (AgedeterminationType a : i.getAgedetermination()) {
                            AgeDeterminationBO abo = ibo.addAgeDetermination();
                            abo.setAge(a.getAge());
                            abo.setAgedeterminationid(a.getAgedeterminationid());
                            abo.setCalibration(a.getCalibration());
                            abo.setCoastalannuli(a.getCoastalannuli());
                            abo.setGrowthzonestotal(a.getGrowthzonestotal());
                            abo.setGrowthzone1(a.getGrowthzone1());
                            abo.setGrowthzone2(a.getGrowthzone2());
                            abo.setGrowthzone3(a.getGrowthzone3());
                            abo.setGrowthzone4(a.getGrowthzone4());
                            abo.setGrowthzone5(a.getGrowthzone5());
                            abo.setGrowthzone6(a.getGrowthzone6());
                            abo.setGrowthzone7(a.getGrowthzone7());
                            abo.setGrowthzone8(a.getGrowthzone8());
                            abo.setGrowthzone9(a.getGrowthzone9());
                            abo.setOceanicannuli(a.getOceanicannuli());
                            abo.setOtolithedge(a.getOtolithedge());
                            abo.setOtolithcentre(a.getOtolithcentre());
                            abo.setOtolithtype(a.getOtolithtype());
                            abo.setReadability(a.getReadability());
                            abo.setSpawningage(a.getSpawningage());
                            abo.setSpawningzones(a.getSpawningzones());
                        }
                    }
                }
            }
        }
    }
}
