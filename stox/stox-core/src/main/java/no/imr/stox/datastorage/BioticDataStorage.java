/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.datastorage;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.biotic.bo.CatchSampleBO;
import no.imr.sea2data.biotic.bo.MissionBO;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrbase.util.IMRdate;
import no.imr.stox.functions.utils.BioticUtils;
import no.imr.sea2data.imrbase.util.ExportUtil;
import no.imr.sea2data.imrbase.util.ImrIO;
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
        return 3;
    }

    @Override
    public String getStorageFileNamePostFix(Integer idxFile) {
        switch (idxFile) {
            case 1:
                return "FishStation";
            case 2:
                return "CatchSample";
            case 3:
                return "Individual";
        }
        return "";
    }

    public static String getElmLevel(Integer idxFile) {
        switch (idxFile) {
            case 1:
                return "fishstation";
            case 2:
                return "catchsample";
            case 3:
                return "individual";
        }
        return "";
    }

    public static void asTable(List<Object> list, Integer level, Writer wr) {
        // Old code
        switch (level) {
            case 1:
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed("missiontype", "cruise", "serialno", "platform", "startdate", "station", "fishstationtype",
                        "latitudestart", "longitudestart", "system", "area", "location", "stratum", "bottomdepthstart", "bottomdepthstop", "gear", "gearcount",
                        "gearspeed", "starttime", "logstart", "stoptime", "distance", "gearcondition", "trawlquality", "fishingdepthmax",
                        "fishingdepthmin", "fishingdepthcount", "trawlopening", "trawldoorspread", "latitudeend", "longitudeend", "wirelength", "stopdate", "logstop", "flowcount",
                        "flowconst", "comment")));
                for (MissionBO ms : (List<MissionBO>) (List) list) {
                    for (FishstationBO fs : ms.getFishstationBOs()) {
                        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(fs.getMission().getMs().getMissiontype(), fs.getMission().getMs().getCruise(), fs.getFs().getSerialnumber(), fs.getFs().getCatchplatform(), IMRdate.formatDate(fs.getFs().getStationstartdate()),
                                fs.getFs().getStation(), fs.getFs().getStationtype(), Conversion.formatDoubletoDecimalString(fs.getFs().getLatitudestart(), 4),
                                Conversion.formatDoubletoDecimalString(fs.getFs().getLongitudestart(), 4), fs.getFs().getSystem(), fs.getFs().getArea(),
                                fs.getFs().getLocation(), fs.getStratum(), fs.getFs().getBottomdepthstart(), fs.getFs().getBottomdepthstop(), fs.getFs().getGear(), fs.getFs().getGearcount(), fs.getFs().getVesselspeed(),
                                IMRdate.formatTime(fs.getFs().getStationstarttime()), fs.getFs().getLogstart(), IMRdate.formatTime(fs.getFs().getStationstoptime()),
                                fs.getFs().getDistance(), fs.getFs().getGearcondition(), fs.getFs().getSamplequality(), fs.getFs().getFishingdepthmax(), fs.getFs().getFishingdepthmin(), fs.getFs().getFishingdepthcount(),
                                fs.getFs().getVerticaltrawlopening(), fs.getFs().getTrawldoorspread(), fs.getFs().getLatitudeend(), fs.getFs().getLongitudeend(), fs.getFs().getWirelength(),
                                IMRdate.formatDate(fs.getFs().getStationstopdate()), fs.getFs().getLogstop(), null, null, fs.getFs().getStationcomment())));
                    }
                }
                break;

            case 2:
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed("cruise", "serialno", "platform", Functions.COL_IND_SPECCAT, "species", "noname", "aphia", "samplenumber", "sampletype", "group",
                        "conservation", "measurement", "weight", "count", "samplemeasurement", "lengthmeasurement", "lengthsampleweight",
                        "lengthsamplecount", "individualsamplecount", "parasite", "stomach", "genetics", "comment")));
                for (MissionBO ms : (List<MissionBO>) (List) list) {
                    for (FishstationBO fs : ms.getFishstationBOs()) {
                        for (CatchSampleBO s : fs.getCatchSampleBOs()) {
                            ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                                    /*IMRdate.getYear(fs.getFs().getStationstartdate(), true)*/fs.getMission().getMs().getCruise(), fs.getFs().getSerialnumber(), fs.getFs().getCatchplatform(),
                                    s.getSpeciesCatTableKey(), s.getCs().getCatchcategory(), s.getCs().getCommonname(), s.getCs().getAphia(), s.getCs().getCatchpartnumber(), s.getCs().getSampletype(), s.getCs().getGroup(), s.getCs().getConservation(), s.getCs().getCatchproducttype(),
                                    s.getCs().getCatchweight(), s.getCs().getCatchcount(), s.getCs().getSampleproducttype(), s.getCs().getLengthmeasurement(), s.getCs().getLengthsampleweight(),
                                    s.getCs().getLengthsamplecount(), s.getCs().getSpecimensamplecount(), s.getCs().getParasite(), s.getCs().getStomach(), s.getCs().getTissuesample(), s.getCs().getCatchcomment())));
                        }
                    }
                }
                break;
            case 3:
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(ExportUtil.tabbed(Functions.INDIVIDUALS))));
                for (MissionBO ms : (List<MissionBO>) (List) list) {
                    for (FishstationBO fs : ms.getFishstationBOs()) {
                        for (CatchSampleBO s : fs.getCatchSampleBOs()) {
                            asTable(null, s.getIndividualBOs(), wr);
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
