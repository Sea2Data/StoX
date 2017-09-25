/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.datastorage;

import java.io.Writer;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.sea2data.imrbase.util.ExportUtil;
import no.imr.sea2data.imrbase.util.IMRdate;
import no.imr.sea2data.imrbase.util.ImrIO;
import no.imr.stox.bo.ReportsBO;
import no.imr.stox.functions.utils.ReportUtil;
import no.imr.stox.functions.utils.Functions;

/**
 *
 * @author aasmunds
 */
public class TotalAbundanceStorage extends FileDataStorage {

    Integer getMaxString(List l) {
        Optional<String> op = l.stream()
                .map(e -> e.toString())
                .max(Comparator.comparingInt(String::length));

        return Math.max(9, (op.isPresent() ? op.get().length() : 18) + 1);
    }

    @Override
    public <T> void asTable(T data, Integer level, Writer wr, Boolean withUnits) {
        ReportsBO reports = (ReportsBO) data;
        if (reports == null) {
            return;
        }
        MatrixBO m = reports.getMatrix(Functions.REPORT_TOTALABUNDANCE);
        if (m == null) {
            return;
        }
        ImrIO.write(wr, ExportUtil.tabbedCRLF("Time", IMRdate.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss", false)));
        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(""));
        Integer specMax = getMaxString(m.getKeys());
        Integer suMax = getMaxString(m.getRowKeys());
        Integer estMax = getMaxString(m.getColKeys());
        Integer scale = (Integer) getProcess().getActualValue(Functions.PM_ESTIMATEBYPOPCATEGORY_SCALE);
        if (scale == 0) {
            scale = 1;
        }
        String scaleStr = ReportUtil.getScaleString(scale);
        Integer numCountDec = 0;
        Integer numWeightDec = 1;
        Integer numWidth = 18;
        String format = Stream.of("-" + specMax, suMax, estMax, numWidth, numWidth)
                .map(o -> "%" + o + "s")
                .collect(Collectors.joining());
        String wAdd = scale > 1 ? " (" + scaleStr + " kg)" : "kg";
        String cAdd = scale > 1 ? " (" + scaleStr + ")" : "";
        ImrIO.writeln(wr, String.format(format, Functions.DIM_SPECCAT, Functions.DIM_STRATUM, Functions.DIM_ESTLAYER,
                "Count" + cAdd, "Weight" + wAdd));
        for (String spec : m.getSortedKeys()) {
            Double totW = null;
            Double totC = null;
            for (String su : m.getSortedGroupRowKeys(spec)) {
                for (String layer : m.getSortedGroupRowColKeys(spec, su)) {
                    Double w = m.getGroupRowColCellValueAsDouble(spec, su, layer, Functions.CATCHVARIABLE_WEIGHT);
                    Double c = m.getGroupRowColCellValueAsDouble(spec, su, layer, Functions.CATCHVARIABLE_COUNT);
                    if (c != null) {
                        totC = totC == null ? c : totC + c;
                    }
                    if (w != null) {
                        totW = totW == null ? w : totW + w;
                    }
                    ImrIO.writeln(wr, String.format(format, spec, su, layer, ReportUtil.formatNumber(c, numCountDec, numWidth),
                            ReportUtil.formatNumber(w, numWeightDec, numWidth)));
                }
            }
            ImrIO.writeln(wr, String.format(format, spec, ReportUtil.TOTAL, ReportUtil.TOTAL,
                    ReportUtil.formatNumber(totC, numCountDec, numWidth), ReportUtil.formatNumber(totW, numWeightDec, numWidth)) + "\n");
        }
    }

    @Override
    public Integer getNumDataStorageFiles() {
        return 1;
    }

    @Override
    public String getStorageFileNamePostFix(Integer idxFile) {
        switch (idxFile) {
            case 1:
                return "Abundance";
        }
        return "";
    }

}
