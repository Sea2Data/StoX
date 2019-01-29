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
                        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(fs.getMission().bo().getMissiontype(), fs.getMission().bo().getCruise(), fs.bo().getSerialnumber(), fs.bo().getCatchplatform(), IMRdate.formatDate(fs.bo().getStationstartdate()),
                                fs.bo().getStation(), fs.bo().getStationtype(), Conversion.formatDoubletoDecimalString(fs.bo().getLatitudestart(), 4),
                                Conversion.formatDoubletoDecimalString(fs.bo().getLongitudestart(), 4), fs.bo().getSystem(), fs.bo().getArea(),
                                fs.bo().getLocation(), fs.getStratum(), fs.bo().getBottomdepthstart(), fs.bo().getBottomdepthstop(), fs.bo().getGear(), fs.bo().getGearcount(), fs.bo().getGearflow(),
                                IMRdate.formatTime(fs.bo().getStationstarttime()), fs.bo().getLogstart(), IMRdate.formatTime(fs.bo().getStationstoptime()),
                                fs.bo().getDistance(), fs.bo().getGearcondition(), fs.bo().getSamplequality(), fs.bo().getFishingdepthmax(), fs.bo().getFishingdepthmin(), fs.bo().getFishingdepthcount(),
                                fs.bo().getVerticaltrawlopening(), fs.bo().getTrawldoorspread(), fs.bo().getLatitudeend(), fs.bo().getLongitudeend(), fs.bo().getWirelength(),
                                IMRdate.formatDate(fs.bo().getStationstopdate()), fs.bo().getLogstop(), null, null, fs.bo().getStationcomment())));
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
                                    /*IMRdate.getYear(fs.bo().getStationstartdate(), true)*/fs.getMission().bo().getCruise(), fs.bo().getSerialnumber(), fs.bo().getCatchplatform(),
                                    s.getSpecCat(), s.bo().getCatchcategory(), s.bo().getCommonname(), s.bo().getAphia(), s.bo().getCatchpartnumber(), s.bo().getSampletype(), s.bo().getGroup(), s.bo().getConservation(), s.bo().getCatchproducttype(),
                                    s.bo().getCatchweight(), s.bo().getCatchcount(), s.bo().getSampleproducttype(), s.bo().getLengthmeasurement(), s.bo().getLengthsampleweight(),
                                    s.bo().getLengthsamplecount(), s.bo().getSpecimensamplecount(), s.bo().getParasite(), s.bo().getStomach(), s.bo().getTissuesample(), s.bo().getCatchcomment())));
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
