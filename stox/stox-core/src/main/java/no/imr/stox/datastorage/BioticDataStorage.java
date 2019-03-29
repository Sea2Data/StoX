/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.datastorage;

import BioticTypes.v3.AgedeterminationType;
import BioticTypes.v3.CatchsampleType;
import BioticTypes.v3.CopepodedevstageType;
import BioticTypes.v3.FishstationType;
import BioticTypes.v3.IndividualType;
import BioticTypes.v3.MissionType;
import BioticTypes.v3.PreyType;
import BioticTypes.v3.PreylengthType;
import BioticTypes.v3.TagType;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import no.imr.sea2data.biotic.bo.AgeDeterminationBO;
import no.imr.sea2data.biotic.bo.CatchSampleBO;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.biotic.bo.MissionBO;
import no.imr.stox.functions.utils.BioticUtils;
import no.imr.sea2data.imrbase.util.ExportUtil;
import no.imr.sea2data.imrbase.util.ImrIO;
import no.imr.stox.bo.BioticData;
import no.imr.stox.functions.utils.Functions;

/**
 * TODO: what does this class do?
 *
 * @author aasmunds
 */
public class BioticDataStorage extends FileDataStorage {

    public BioticDataStorage() {
    }

    @Override
    public <T> void asTable(T data, Integer level, Writer wr, Boolean withUnits) {
        asTable((List<Object>) data, level, wr);
    }

    @Override
    public Integer getNumDataStorageFiles() {
        return 9;
    }

    @Override
    public String getStorageFileNamePostFix(Integer idxFile) {
        Class cls = getClass(idxFile);
        String str = cls.getSimpleName();
        return str.substring(0, str.length() - 4); // remove Type
    }

    private static Class getClass(Integer idx) {
        switch (idx) {
            case 1:
                return MissionType.class;
            case 2:
                return FishstationType.class;
            case 3:
                return CatchsampleType.class;
            case 4:
                return IndividualType.class;
            case 5:
                return AgedeterminationType.class;
            case 6:
                return TagType.class;
            case 7:
                return PreyType.class;
            case 8:
                return PreylengthType.class;
            case 9:
                return CopepodedevstageType.class;
        }
        return null;
    }

    public static void asTable(List<Object> list, Integer level, Writer wr) {
        // Old code
        BioticData ml = ((BioticData) (List) list);
        switch (level) {
            case 1:
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed("missiontype", "startyear", "platform", "missionnumber",
                        "missiontypename", "callsignal", "platformname", "cruise", "missionstartdate", "missionstopdate", "purpose")));
                for (MissionBO ms : ml) {
                    MissionType m = ms.bo();
                    ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                            m.getMissiontype(), m.getStartyear(), m.getPlatform(), m.getMissionnumber(), m.getMissiontypename(),
                            m.getCallsignal(), m.getPlatformname(), m.getCruise(), m.getMissionstartdate(), m.getMissionstopdate(),
                            m.getPurpose())));
                }
                break;
            case 2:
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                        "missiontype", "startyear", "platform", "missionnumber",
                        "serialnumber",
                        "nation", "catchplatform", "station", "fixedstation", "stationstartdate",
                        "stationstarttime", "stationstopdate", "stationstoptime", "stationtype", "latitudestart", "longitudestart",
                        "latitudeend", "longitudeend", "system", "area", "location", "bottomdepthstart", "bottomdepthstop", "bottomdepthmean",
                        "fishingdepthstart", "fishingdepthstop", "fishingdepthcount", "fishingdepthmax", "fishingdepthmin", "fishingdepthmean",
                        "fishingdepthtemperature", "gearno", "sweeplength", "gear", "gearcount", "direction", "gearflow", "vesselspeed", "logstart",
                        "logstop", "distance", "gearcondition", "samplequality", "verticaltrawlopening", "verticaltrawlopeningsd", "trawldoortype",
                        "trawldoorarea", "trawldoorweight", "trawldoorspread", "trawldoorspreadsd", "wingspread", "wingspreadsd", "wirelength",
                        "wirediameter", "wiredensity", "soaktime", "tripno", "fishabundance", "fishdistribution", "landingsite", "fishingground",
                        "vesselcount", "dataquality", "haulvalidity", "flora", "vegetationcover", "visibility", "waterlevel", "winddirection",
                        "windspeed", "clouds", "sea", "weather", "stationcomment")));
                for (MissionBO ms : ml) {
                    MissionType m = ms.bo();
                    for (FishstationBO fs : ms.getFishstationBOs()) {
                        FishstationType f = fs.bo();
                        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                                m.getMissiontype(), m.getStartyear(), m.getPlatform(), m.getMissionnumber(),
                                f.getSerialnumber(),
                                f.getNation(), f.getCatchplatform(), f.getStation(), f.getFixedstation(), f.getStationstartdate(),
                                f.getStationstarttime(), f.getStationstopdate(), f.getStationstoptime(), f.getStationtype(), f.getLatitudestart(), f.getLongitudestart(),
                                f.getLatitudeend(), f.getLongitudeend(), f.getSystem(), f.getArea(), f.getLocation(), f.getBottomdepthstart(), f.getBottomdepthstop(), f.getBottomdepthmean(),
                                f.getFishingdepthstart(), f.getFishingdepthstop(), f.getFishingdepthcount(), f.getFishingdepthmax(), f.getFishingdepthmin(), f.getFishingdepthmean(),
                                f.getFishingdepthtemperature(), f.getGearno(), f.getSweeplength(), f.getGear(), f.getGearcount(), f.getDirection(), f.getGearflow(), f.getVesselspeed(), f.getLogstart(),
                                f.getLogstop(), f.getDistance(), f.getGearcondition(), f.getSamplequality(), f.getVerticaltrawlopening(), f.getVerticaltrawlopeningsd(), f.getTrawldoortype(),
                                f.getTrawldoorarea(), f.getTrawldoorweight(), f.getTrawldoorspread(), f.getTrawldoorspreadsd(), f.getWingspread(), f.getWingspreadsd(), f.getWirelength(),
                                f.getWirediameter(), f.getWiredensity(), f.getSoaktime(), f.getTripno(), f.getFishabundance(), f.getFishdistribution(), f.getLandingsite(), f.getFishingground(),
                                f.getVesselcount(), f.getDataquality(), f.getHaulvalidity(), f.getFlora(), f.getVegetationcover(), f.getVisibility(), f.getWaterlevel(), f.getWinddirection(),
                                f.getWindspeed(), f.getClouds(), f.getSea(), f.getWeather(), f.getStationcomment()
                        )));
                    }
                }
                break;
            case 3:
                // todo add spec cat
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                        "missiontype", "startyear", "platform", "missionnumber",
                        "serialnumber",
                        "catchsampleid",
                        ExportUtil.getObj("SpecCat", ml.isSpecCatAdded()),
                        "commonname", "catchcategory", "catchpartnumber", "aphia", "scientificname", "identification", "foreignobject",
                        "sampletype", "group", "conservation", "catchproducttype", "raisingfactor", "catchweight", "catchvolume", "catchcount",
                        "abundancecategory", "sampleproducttype", "lengthmeasurement", "lengthsampleweight", "lengthsamplevolume",
                        "lengthsamplecount", "specimensamplecount", "agesamplecount", "agingstructure", "parasite", "stomach", "intestine",
                        "tissuesample", "samplerecipient", "catchcomment"
                )));
                for (MissionBO ms : ml) {
                    MissionType m = ms.bo();
                    for (FishstationBO fs : ms.getFishstationBOs()) {
                        FishstationType f = fs.bo();
                        for (CatchSampleBO cs : fs.getCatchSampleBOs()) {
                            CatchsampleType c = cs.bo();
                            ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                                    m.getMissiontype(), m.getStartyear(), m.getPlatform(), m.getMissionnumber(),
                                    f.getSerialnumber(),
                                    c.getCatchsampleid(),
                                    ExportUtil.getObj(cs.getSpecCat(), ml.isSpecCatAdded()),
                                    c.getCommonname(), c.getCatchcategory(), c.getCatchpartnumber(), c.getAphia(), c.getScientificname(), c.getIdentification(), c.getForeignobject(),
                                    c.getSampletype(), c.getGroup(), c.getConservation(), c.getCatchproducttype(), c.getRaisingfactor(), c.getCatchweight(), c.getCatchvolume(), c.getCatchcount(),
                                    c.getAbundancecategory(), c.getSampleproducttype(), c.getLengthmeasurement(), c.getLengthsampleweight(), c.getLengthsamplevolume(),
                                    c.getLengthsamplecount(), c.getSpecimensamplecount(), c.getAgesamplecount(), c.getAgingstructure(), c.getParasite(), c.getStomach(), c.getIntestine(),
                                    c.getTissuesample(), c.getSamplerecipient(), c.getCatchcomment()
                            )));
                        }
                    }
                }
                break;
            case 4:
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                        "missiontype", "startyear", "platform", "missionnumber",
                        "serialnumber",
                        "catchsampleid",
                        "specimenid",
                        ExportUtil.getObj("LengthCM", ml.isLengthCMAdded()),
                        ExportUtil.getObj("IndividualWeightG", ml.isIndividualWeightGAdded()),
                        ExportUtil.getObj("age", ml.isAgeAdded()),
                        "individualproducttype", "individualweight", "individualvolume", "lengthresolution", "length", "fat",
                        "fatpercent", "sex", "maturationstage", "specialstage", "eggstage", "moultingstage", "spawningfrequency",
                        "stomachfillfield", "stomachfilllab", "digestion", "liver", "liverparasite", "gillworms", "swollengills",
                        "fungusheart", "fungusspores", "fungusouter", "blackspot", "vertebraecount", "gonadweight", "liverweight",
                        "stomachweight", "diameter", "mantlelength", "carapacelength", "headlength", "snouttoendoftail", "snouttoendsqueezed",
                        "snouttoanalfin", "snouttodorsalfin", "forklength", "snouttoboneknob", "lengthwithouthead", "carapacewidth", "rightclawwidth",
                        "rightclawlength", "meroswidth", "meroslength", "japanesecut", "abdomenwidth", "tissuesamplenumber", "individualcomment",
                        "preferredagereading"
                )));
                for (MissionBO ms : ml) {
                    MissionType m = ms.bo();
                    for (FishstationBO fs : ms.getFishstationBOs()) {
                        FishstationType f = fs.bo();
                        for (CatchSampleBO cs : fs.getCatchSampleBOs()) {
                            CatchsampleType c = cs.bo();
                            for (IndividualBO ii : cs.getIndividualBOs()) {
                                IndividualType i = ii.bo();
                                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                                        m.getMissiontype(), m.getStartyear(), m.getPlatform(), m.getMissionnumber(),
                                        f.getSerialnumber(),
                                        c.getCatchsampleid(),
                                        i.getSpecimenid(),
                                        ExportUtil.getObj(ii.getLengthCM(), ml.isLengthCMAdded()),
                                        ExportUtil.getObj(ii.getIndividualWeightG(), ml.isIndividualWeightGAdded()),
                                        ExportUtil.getObj(ii.getAge(), ml.isAgeAdded()),
                                        i.getIndividualproducttype(), i.getIndividualweight(), i.getIndividualvolume(), i.getLengthresolution(), i.getLength(), i.getFat(),
                                        i.getFatpercent(), i.getSex(), i.getMaturationstage(), i.getSpecialstage(), i.getEggstage(), i.getMoultingstage(), i.getSpawningfrequency(),
                                        i.getStomachfillfield(), i.getStomachfilllab(), i.getDigestion(), i.getLiver(), i.getLiverparasite(), i.getGillworms(), i.getSwollengills(),
                                        i.getFungusheart(), i.getFungusspores(), i.getFungusouter(), i.getBlackspot(), i.getVertebraecount(), i.getGonadweight(), i.getLiverweight(),
                                        i.getStomachweight(), i.getDiameter(), i.getMantlelength(), i.getCarapacelength(), i.getHeadlength(), i.getSnouttoendoftail(), i.getSnouttoendsqueezed(),
                                        i.getSnouttoanalfin(), i.getSnouttodorsalfin(), i.getForklength(), i.getSnouttoboneknob(), i.getLengthwithouthead(), i.getCarapacewidth(), i.getRightclawwidth(),
                                        i.getRightclawlength(), i.getMeroswidth(), i.getMeroslength(), i.getJapanesecut(), i.getAbdomenwidth(), i.getTissuesamplenumber(), i.getIndividualcomment(),
                                        i.getPreferredagereading()
                                )));
                            }
                        }
                    }
                }
                break;
            case 5:
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                        "missiontype", "startyear", "platform", "missionnumber",
                        "serialnumber",
                        "catchsampleid",
                        "specimenid",
                        "agedeterminationid",
                        "agingstructureread", "agingstructureweight", "agingstructurelength", "nowearpoint", "externalspinebase", "countedannualage",
                        "age", "spawningage", "smoltage", "marineage", "spawningzones", "readability", "otolithtype", "otolithedge", "otolithcentre",
                        "calibration", "growthzone1", "growthzone2", "growthzone3", "growthzone4", "growthzone5", "growthzone6", "growthzone7", "growthzone8",
                        "growthzone9", "growthzonestotal", "coastalannuli", "oceanicannuli", "blindreading", "readingdate", "agereader"
                )));
                for (MissionBO ms : ml) {
                    MissionType m = ms.bo();
                    for (FishstationBO fs : ms.getFishstationBOs()) {
                        FishstationType f = fs.bo();
                        for (CatchSampleBO cs : fs.getCatchSampleBOs()) {
                            CatchsampleType c = cs.bo();
                            for (IndividualBO ii : cs.getIndividualBOs()) {
                                IndividualType i = ii.bo();
                                for (AgeDeterminationBO aa : ii.getAgeDeterminationBOs()) {
                                    AgedeterminationType a = aa.bo();
                                    ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                                            m.getMissiontype(), m.getStartyear(), m.getPlatform(), m.getMissionnumber(),
                                            f.getSerialnumber(),
                                            c.getCatchsampleid(),
                                            i.getSpecimenid(),
                                            a.getAgedeterminationid(),
                                            a.getAgingstructureread(), a.getAgingstructureweight(), a.getAgingstructurelength(), a.getNowearpoint(), a.getExternalspinebase(), a.getCountedannualage(),
                                            a.getAge(), a.getSpawningage(), a.getSmoltage(), a.getMarineage(), a.getSpawningzones(), a.getReadability(), a.getOtolithtype(), a.getOtolithedge(), a.getOtolithcentre(),
                                            a.getCalibration(), a.getGrowthzone1(), a.getGrowthzone2(), a.getGrowthzone3(), a.getGrowthzone4(), a.getGrowthzone5(), a.getGrowthzone6(), a.getGrowthzone7(), a.getGrowthzone8(),
                                            a.getGrowthzone9(), a.getGrowthzonestotal(), a.getCoastalannuli(), a.getOceanicannuli(), a.getBlindreading(), a.getReadingdate(), a.getAgereader()
                                    )));
                                }
                            }
                        }
                    }
                }
                break;
            case 6:
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                        "missiontype", "startyear", "platform", "missionnumber",
                        "serialnumber",
                        "catchsampleid",
                        "specimenid",
                        "tagid",
                        "tagtype"
                )));
                for (MissionBO ms : ml) {
                    MissionType m = ms.bo();
                    for (FishstationBO fs : ms.getFishstationBOs()) {
                        FishstationType f = fs.bo();
                        for (CatchSampleBO cs : fs.getCatchSampleBOs()) {
                            CatchsampleType c = cs.bo();
                            for (IndividualBO ii : cs.getIndividualBOs()) {
                                IndividualType i = ii.bo();
                                for (TagType t : ii.bo().getTag()) { // If tag should support filter, create a TagBO
                                    ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                                            m.getMissiontype(), m.getStartyear(), m.getPlatform(), m.getMissionnumber(),
                                            f.getSerialnumber(),
                                            c.getCatchsampleid(),
                                            i.getSpecimenid(),
                                            t.getTagid(),
                                            t.getTagtype()
                                    )));
                                }
                            }
                        }
                    }
                }
                break;
            case 7:
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                        "missiontype", "startyear", "platform", "missionnumber",
                        "serialnumber",
                        "catchsampleid",
                        "specimenid",
                        "preysampleid",
                        "preypartnumber", "preycategory", "source", "preydigestion", "totalcount", "weightresolution", "totalweight",
                        "interval", "devstage", "preylengthmeasurement"
                )));
                for (MissionBO ms : ml) {
                    MissionType m = ms.bo();
                    for (FishstationBO fs : ms.getFishstationBOs()) {
                        FishstationType f = fs.bo();
                        for (CatchSampleBO cs : fs.getCatchSampleBOs()) {
                            CatchsampleType c = cs.bo();
                            for (IndividualBO ii : cs.getIndividualBOs()) {
                                IndividualType i = ii.bo();
                                for (PreyType p : ii.bo().getPrey()) {
                                    ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                                            m.getMissiontype(), m.getStartyear(), m.getPlatform(), m.getMissionnumber(),
                                            f.getSerialnumber(),
                                            c.getCatchsampleid(),
                                            i.getSpecimenid(),
                                            p.getPreysampleid(),
                                            p.getPreypartnumber(), p.getPreycategory(), p.getSource(), p.getPreydigestion(), p.getTotalcount(), p.getWeightresolution(), p.getTotalweight(),
                                            p.getInterval(), p.getDevstage(), p.getPreylengthmeasurement()
                                    )));
                                }
                            }
                        }
                    }
                }
                break;
            case 8:
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                        "missiontype", "startyear", "platform", "missionnumber",
                        "serialnumber",
                        "catchsampleid",
                        "specimenid",
                        "preysampleid",
                        "preylengthid",
                        "lengthintervalstart", "lengthintervalcount"
                )));
                for (MissionBO ms : ml) {
                    MissionType m = ms.bo();
                    for (FishstationBO fs : ms.getFishstationBOs()) {
                        FishstationType f = fs.bo();
                        for (CatchSampleBO cs : fs.getCatchSampleBOs()) {
                            CatchsampleType c = cs.bo();
                            for (IndividualBO ii : cs.getIndividualBOs()) {
                                IndividualType i = ii.bo();
                                for (PreyType p : ii.bo().getPrey()) {
                                    for (PreylengthType pl : p.getPreylengthfrequencytable()) {
                                        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                                                m.getMissiontype(), m.getStartyear(), m.getPlatform(), m.getMissionnumber(),
                                                f.getSerialnumber(),
                                                c.getCatchsampleid(),
                                                i.getSpecimenid(),
                                                p.getPreysampleid(),
                                                pl.getPreylengthid(), pl.getLengthintervalstart(), pl.getLengthintervalcount()
                                        )));
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case 9:
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                        "missiontype", "startyear", "platform", "missionnumber",
                        "serialnumber",
                        "catchsampleid",
                        "specimenid",
                        "preysampleid",
                        "preylengthid",
                        "copepodedevstage", "devstagecount"
                )));
                for (MissionBO ms : ml) {
                    MissionType m = ms.bo();
                    for (FishstationBO fs : ms.getFishstationBOs()) {
                        FishstationType f = fs.bo();
                        for (CatchSampleBO cs : fs.getCatchSampleBOs()) {
                            CatchsampleType c = cs.bo();
                            for (IndividualBO ii : cs.getIndividualBOs()) {
                                IndividualType i = ii.bo();
                                for (PreyType p : ii.bo().getPrey()) {
                                    for (CopepodedevstageType cd : p.getCopepodedevstagefrequencytable()) {
                                        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                                                m.getMissiontype(), m.getStartyear(), m.getPlatform(), m.getMissionnumber(),
                                                f.getSerialnumber(),
                                                c.getCatchsampleid(),
                                                i.getSpecimenid(),
                                                p.getPreysampleid(),
                                                cd.getCopepodedevstage(), cd.getDevstagecount()
                                        )));
                                    }
                                }
                            }
                        }
                    }
                }
                break;
        }
    }

    /**
     *
     * @param context
     * @param inds
     * @param wr
     */
    public static void asTable(String context, List<IndividualBO> inds, Writer wr) {
        List<String> fields = new ArrayList<>(Functions.INDIVIDUALS);
//        fields.add("comment");
        asTable(fields, context, inds, wr);
    }

    public static void asTable(List<String> fields, String context, List<IndividualBO> inds, Writer wr) {
        for (IndividualBO i : inds) {
            String line = context;
            for (String code : fields) {
                Object c = BioticUtils.getIndVar(i, code);
                line = line != null ? ExportUtil.tabbed(line, c) : c == null ? "-" : c.toString();
            }
            // Add comment
            //line = line != null ? ExportUtil.tabbed(line, i.getCatchcomment()) : line;

            line = ExportUtil.carrageReturnLineFeed(line);
            ImrIO.write(wr, line);
        }
    }
}
