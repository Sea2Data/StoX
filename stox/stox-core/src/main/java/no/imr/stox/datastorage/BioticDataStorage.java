/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.datastorage;

import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import no.imr.sea2data.biotic.bo.CatchBO;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.biotic.bo.SampleBO;
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
                for (FishstationBO fs : (List<FishstationBO>) (List) list) {
                    ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(fs.getMissiontype(), fs.getCruise(), fs.getSerialnumber(), fs.getCatchplatform(), IMRdate.formatDate(fs.getStationstartdate()),
                            fs.getStation(), fs.getStationtype(), Conversion.formatDoubletoDecimalString(fs.getLatitudestart(), 4),
                            Conversion.formatDoubletoDecimalString(fs.getLongitudestart(), 4), fs.getSystem(), fs.getArea(),
                            fs.getLocation(), fs.getStratum(), fs.getBottomdepthstart(), fs.getBottomdepthstop(), fs.getGear(), fs.getGearcount(), fs.getVesselspeed(),
                            IMRdate.formatTime(fs.getStationstarttime()), fs.getLogstart(), IMRdate.formatTime(fs.getStationstoptime()),
                            fs.getDistance(), fs.getGearcondition(), fs.getSamplequality(), fs.getFishingdepthmax(), fs.getFishingdepthmin(), fs.getFishingdepthcount(),
                            fs.getVerticaltrawlopening(), fs.getTrawldoorspread(), fs.getLatitudeend(), fs.getLongitudeend(), fs.getwirelength(),
                            IMRdate.formatDate(fs.getStationstopdate()), fs.getLogstop(), null, null, fs.getComment())));
                }
                break;

            case 2:
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed("cruise", "serialno", "platform", Functions.COL_IND_SPECCAT, "species", "noname", "aphia", "samplenumber", "sampletype", "group",
                        "conservation", "measurement", "weight", "count", "samplemeasurement", "lengthmeasurement", "lengthsampleweight",
                        "lengthsamplecount", "individualsamplecount", "parasite", "stomach", "genetics", "comment")));
                for (FishstationBO fs : (List<FishstationBO>) (List) list) {
                    for (CatchBO c : fs.getCatchBOs()) {
                        for (SampleBO s : c.getSampleBOs()) {
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                                    /*IMRdate.getYear(fs.getStationstartdate(), true)*/fs.getCruise(), fs.getSerialnumber(), fs.getCatchplatform(),
                                    s.getCatchBO().getSpeciesCatTableKey(), c.getCatchcategory(), c.getCommonname(), c.getAphia(), s.getCatchpartnumber(), s.getSampletype(), s.getGroup(), s.getConservation(), s.getCatchproducttype(),
                                    s.getCatchweight(), s.getCatchcount(), s.getSampleproducttype(), s.getLengthmeasurement(), s.getlengthsampleweight(),
                                    s.getLengthsamplecount(), s.getSpecimensamplecount(), s.getParasite(), s.getStomach(), s.getGenetics(), s.getCatchcomment())));
                        }
                    }
                }
                break;
            case 3:
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(ExportUtil.tabbed(Functions.INDIVIDUALS))));
                for (FishstationBO fs : (List<FishstationBO>) (List) list) {
                    for (CatchBO c : fs.getCatchBOs()) {
                        for (SampleBO s : c.getSampleBOs()) {
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
