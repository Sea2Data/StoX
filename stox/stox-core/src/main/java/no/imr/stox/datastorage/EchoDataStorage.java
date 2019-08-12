/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.datastorage;

import java.io.Writer;
import java.util.List;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.echosounderbo.FrequencyBO;
import no.imr.sea2data.echosounderbo.SABO;
import no.imr.stox.util.math.Calc;
import no.imr.stox.util.base.IMRdate;
import no.imr.stox.util.base.ExportUtil;
import no.imr.stox.util.base.ImrIO;

/**
 * TODO: waht does this class do?
 *
 * @author aasmunds
 */
public class EchoDataStorage extends FileDataStorage {

    @Override
    public <T> void asTable(T data, Integer level, Writer wr, Boolean withUnits)  {
        asTable((List<DistanceBO>) data, level, wr);
    }

    @Override
    public Integer getNumDataStorageFiles() {
        return 2;
    }

    @Override
    public String getStorageFileNamePostFix(Integer idxFile) {
        switch (idxFile) {
            case 1:
                return "DistanceFrequency";
            case 2:
                return "NASC";
        }
        return "";
    }

    public static void asTable(List<DistanceBO> list, Integer level, Writer wr) {
        switch (level) {
            case 1:
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed("cruise", "log_start", "start_time", "lon_start", "lat_start", "lon_stop", "lat_stop", "integrator_dist",
                        "pel_ch_thickness", "bot_ch_thickness", "freq", "transceiver", "upper_interpret_depth", "lower_interpret_depth",
                        "upper_integrator_depth", "lower_integrator_depth", "num_pel_ch", "num_bot_ch", "min_bot_depth", "max_bot_depth", "quality",
                        "bubble_corr", "threshold")));
                for (DistanceBO d : list) {
                    for (FrequencyBO f : d.getFrequencies()) {
                        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(d.getCruise(), d.getLog_start(), IMRdate.formatDate(d.getStart_time(),
                                "yyyy-MM-dd HH:mm:ss"), d.getLon_start(), d.getLat_start(),  d.getLon_stop(), d.getLat_stop(), Calc.roundTo(d.getIntegrator_dist(), 8),
                                d.getPel_ch_thickness(), d.getBot_ch_thickness(), f.getFreq(), f.getTranceiver(), f.getUpper_interpret_depth(),
                                f.getLower_interpret_depth(), f.getUpper_integrator_depth(), f.getLower_integrator_depth(), f.getNum_pel_ch(),
                                f.getNum_bot_ch(), f.getMin_bot_depth(), f.getMax_bot_depth(), f.getQuality(), f.getBubble_corr(), f.getThreshold())));
                    }
                }
                break;

            case 2:
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed("cruise", "log_start", "start_time", "freq", "transceiver", "acocat", "ch_type", "ch", "sa")));
                for (DistanceBO d : list) {
                    for (FrequencyBO f : d.getFrequencies()) {
                        for (SABO nasc : f.getSa()) {
                            ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(d.getCruise(), d.getLog_start(), IMRdate.formatDate(d.getStart_time(),
                                    "yyyy-MM-dd HH:mm:ss"), f.getFreq(), f.getTranceiver(), nasc.getAcoustic_category(), nasc.getCh_type(), nasc.getCh(), nasc.getSa())));
                        }
                    }
                }
        }
    }

}
