package no.imr.stox.datastorage;

import java.io.Writer;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrbase.util.IMRdate;
import no.imr.sea2data.imrbase.util.ImrSort;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.bo.ReportsBO;
import no.imr.stox.functions.utils.ReportUtil;
import no.imr.sea2data.imrbase.util.ExportUtil;
import no.imr.sea2data.imrbase.util.ImrIO;
import no.imr.stox.functions.utils.BioticUtils;
import no.imr.stox.functions.utils.Functions;
import org.apache.commons.lang.WordUtils;

/**
 * TODO: what does this class do?
 *
 * @author aasmunds
 */
public class AbundanceByPopCategoryDataStorage extends FileDataStorage {

    @Override
    public <T> void asTable(T data, Integer level, Writer wr, Boolean withUnits) {
        ReportsBO reports = (ReportsBO) data;
        if(reports == null) {
            return;
        }
        String var = null;
        switch (level) {
            case 1:
                var = ABUNDANCE_LITERAL;
                break;
            case 2:
                var = BIOMASS_LITERAL;
                break;
            case 3:
                var = MEAN_WEIGHT_LITERAL;
                break;
            default:
                return;
        }
        MatrixBO mReport = reports.getMatrix(Functions.REPORT_XCATBYLENGTH);
        int widthCell = getMaxWidthCell(mReport);
        ImrIO.write(wr, ExportUtil.tabbedCRLF("Time", IMRdate.formatDate(new Date(), "yyyy-MM-dd HH:mm:ss", false)));
        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(""));
        String dim1 = (String) getProcess().getParameterValue(Functions.PM_ESTIMATEBYPOPCATEGORY_DIM1);
        String dim2 = (String) getProcess().getParameterValue(Functions.PM_ESTIMATEBYPOPCATEGORY_DIM2);
        String dim3 = (String) getProcess().getParameterValue(Functions.PM_ESTIMATEBYPOPCATEGORY_DIM3);
        String dim4 = (String) getProcess().getParameterValue(Functions.PM_ESTIMATEBYPOPCATEGORY_DIM4);
        String dim5 = (String) getProcess().getParameterValue(Functions.PM_ESTIMATEBYPOPCATEGORY_DIM5);
        asTable(mReport, var, wr, dim1, dim2, dim3, dim4, dim5, widthCell);
    }

    int getMaxWidthCell(MatrixBO mReport) {
        int s = 7;
        for (String hdr : mReport.getKeys()) {
            for (String lenGrp : mReport.getGroupRowKeys(hdr)) {
                for (String xCat : mReport.getGroupRowColKeys(hdr, lenGrp)) {
                    s = Math.max(s, xCat.length());
                    for (String var : mReport.getGroupRowColCellKeys(hdr, lenGrp, xCat)) {
                        Object val = mReport.getGroupRowColCellValueAsDouble(hdr, lenGrp, xCat, var);
                        if (val == null || !(val instanceof Double)) {
                            continue;
                        }
                        if (!(var.equals("Abundance") || var.equals("Biomass"))) {
                            continue;
                        }
                        int numDec = var.equals("Abundance") ? 0 : 1;
                        String str = String.format("%25." + numDec + "f", (Double) val).trim();
                        s = Math.max(s, str.length());
                    }
                }
            }
        }
        return s + 1; // extra space
    }

    // Report variables (scalars from super individuals)
    private static final String BIOMASS_LITERAL = "Biomass";
    //private static final String SUML_LITERAL = "SumL"; // Sum of length used
    private static final String ABUNDANCE_LITERAL = "Abundance";

    // Report variables (derived in this report)
    private static final String MEAN_WEIGHT_LITERAL = "MeanWeight";
    private static final String MEAN_LENGTH_LITERAL = "MeanLength";

    private static final String RULER = "______________________________________________________________________________________________________";

    @Override
    public Integer getNumDataStorageFiles() {
        return 3;
    }

    @Override
    public String getStorageFileNamePostFix(Integer idxFile) {
        switch (idxFile) {
            case 1:
                return ABUNDANCE_LITERAL;
            case 2:
                return BIOMASS_LITERAL;
            case 3:
                return MEAN_WEIGHT_LITERAL;
        }
        return "";
    }

    public void asTable(MatrixBO mReport, String var, Writer wr, String dim1, String dim2, String dim3, String dim4, String dim5, int widthCell) {
        int numDec = var.equals(ABUNDANCE_LITERAL) ? 0 : 1;
        Double lenInt = (Double) getProcess().getActualValue(Functions.PM_ESTIMATEBYPOPCATEGORY_LENGTHINTERVAL);
        Integer scale = (Integer) getProcess().getActualValue(Functions.PM_ESTIMATEBYPOPCATEGORY_SCALE);
        if (scale == null) {
            scale = 1;
        }
        String scaleStr = ReportUtil.getScaleString(scale);
        List<String> groupKeys = mReport.getKeys();
        // Sort TOTALs at the end
        Collections.sort(groupKeys, new ImrSort.TranslativeComparatorWithToken(true, "/", groupKeys) {

            @Override
            protected int compareInternal(Object o1, Object o2) {

                if (!o1.toString().equals("TOTAL") && o2.toString().equals("TOTAL")) {
                    return -1;
                }
                if (o1.toString().equals("TOTAL") && !o2.toString().equals("TOTAL")) {
                    return 1;
                }
                return super.compareInternal(o1, o2); //To change body of generated methods, choose Tools | Templates.
            }
        });
        Set<String> dim3Keys = new HashSet<>();
        Set<String> dim4Keys = new HashSet<>();
        Set<String> dim5Keys = new HashSet<>();
        Set<String> strata = new HashSet<>();
        Set<String> estLayers = new HashSet<>();
        for (String groupKey : groupKeys) {
            String[] keys = groupKey.split("/");
            String estLayer = keys[0];
            String stratum = keys[1];
            String dim3Key = keys[2];
            String dim4Key = keys[3];
            String dim5Key = keys[4];
            if (!estLayer.equals("TOTAL")) {
                estLayers.add(estLayer);
            }
            if (!stratum.equals("TOTAL")) {
                strata.add(stratum);
            }
            if (!dim3Key.equals("TOTAL")) {
                dim3Keys.add(dim3Key);
            }
            if (!dim4Key.equals("TOTAL")) {
                dim4Keys.add(dim4Key);
            }
            if (!dim5Key.equals("TOTAL")) {
                dim5Keys.add(dim5Key);
            }
        }
        // Special variable (list of excluded strata) transferred from aggregation
        /*String estLayerTotal = estLayers.size() == 1 ? estLayers.iterator().next() : "TOTAL";
        String stratumTotal = strata.size() == 1 ? strata.iterator().next() : "TOTAL";
        String dim3Total = dim3Keys.size() == 1 ? dim3Keys.iterator().next() : "TOTAL";
        String dim4Total = dim3Keys.size() == 1 ? dim4Keys.iterator().next() : "TOTAL";
        String dim5Total = dim3Keys.size() == 1 ? dim5Keys.iterator().next() : "TOTAL";*/
        
        String exclStrata = (String) mReport.getGroupRowColCellValue(
                ReportUtil.heading("TOTAL", "TOTAL", "TOTAL", "TOTAL", "TOTAL"), "TOTAL", "TOTAL", "ExcludedStrata");
        if(exclStrata != null) {
            exclStrata = WordUtils.wrap(exclStrata.replaceAll(",", ", "), 110, "\n      ", false);
        }
        for (String heading : groupKeys) {
            MatrixBO group = mReport.getValueAsMatrix(heading);
            String[] keys = heading.split("/");
            String estLayer = keys[0];
            String stratum = keys[1];
            String dim3Key = keys[2];
            String dim4Key = keys[3];
            String dim5Key = keys[4];
            if (estLayers.size() == 1 && !estLayers.contains(estLayer)) { // Filter out TOTAL when only one
                continue;
            }
            if (strata.size() == 1 && !strata.contains(stratum)) { // Filter out TOTAL when only one
                continue;
            }
            if (dim3Keys.size() == 1 && !dim3Keys.contains(dim3Key)) { // Filter out TOTAL when only one
                continue;
            }
            if (dim4Keys.size() == 1 && !dim4Keys.contains(dim4Key)) { // Filter out TOTAL when only one
                continue;
            }
            if (dim5Keys.size() == 1 && !dim5Keys.contains(dim5Key)) { // Filter out TOTAL when only one
                continue;
            }
            Boolean inTotalStratumAndEstLayer = (strata.size() == 1 || stratum.equals("TOTAL"))
                    && (estLayers.size() == 1 || estLayer.equals("TOTAL"));
            if (inTotalStratumAndEstLayer) {
                if (exclStrata != null && !exclStrata.isEmpty()) {
                    stratum += "\n   Excluded:\n      " + exclStrata;
                }
            }
            ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(""));
            ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(""));
            ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(RULER));
            ImrIO.write(wr, ExportUtil.carrageReturnLineFeed("Variable: " + var));
            ImrIO.write(wr, ExportUtil.carrageReturnLineFeed("EstLayer: " + estLayer));
            ImrIO.write(wr, ExportUtil.carrageReturnLineFeed("Stratum: " + stratum));
            if (!dim3.equalsIgnoreCase("none")) {
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(dim3 + ": " + getDimVal(Functions.PM_ESTIMATEBYPOPCATEGORY_DIM3, dim3Key)));
            }
            if (!dim4.equalsIgnoreCase("none")) {
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(dim4 + ": " + getDimVal(Functions.PM_ESTIMATEBYPOPCATEGORY_DIM4, dim4Key)));
            }
            if (!dim5.equalsIgnoreCase("none")) {
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(dim5 + ": " + getDimVal(Functions.PM_ESTIMATEBYPOPCATEGORY_DIM5, dim5Key)));
            }
            List<String> dim1Keys = group.getSortedKeys();
            if (lenInt == null) {
                lenInt = 1d;
            }
            List<String> dim2Keys = mReport.getSortedGroupColKeys(heading);
            String line = String.format("%-" + (18 + 2) + "s", dim1);
            String sForm = "%" + widthCell + "s";
            ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(RULER));
            ImrIO.write(wr, ExportUtil.carrageReturnLineFeed("                   " + dim2 + "                                          "));
            for (String dim2Key : dim2Keys) {
                if (dim2Key.equals(ReportUtil.TOTAL)) {
                    continue;
                }
                line = line + String.format(sForm, getDimVal(Functions.PM_ESTIMATEBYPOPCATEGORY_DIM2, dim2Key));
            }
            line = line + String.format(sForm + sForm + sForm, "Number", BIOMASS_LITERAL, "Mean W");
            line = ExportUtil.carrageReturnLineFeed(line);
            ImrIO.write(wr, line);
            Integer numSpace = 18 + 2 + widthCell * (dim2Keys.size() - 1);
            line = String.format("%" + numSpace + "s", "");
            line += String.format(sForm + sForm + sForm, "(" + scaleStr + ")", "(" + scaleStr + "kg)", "(g)");
            ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(line));
            ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(RULER));
            for (String dim1Key : dim1Keys) {
                if (dim1Key.equals(ReportUtil.TOTAL)) {
                    continue;
                }
                /*Double len1 = Conversion.safeStringtoDouble(dim1);
                 Double len2 = len1 + lenInt;
                 String s = String.format("%5.1f - %5.1f", len1, len2);*/
                ImrIO.write(wr, getReportLine(dim1Key, getDimVal(Functions.PM_ESTIMATEBYPOPCATEGORY_DIM1, dim1Key), var, numDec, group, dim2Keys, widthCell));
            }
            ImrIO.write(wr, ExportUtil.carrageReturnLineFeed("______________________________________________________________________________________________________"));
            ImrIO.write(wr, getReportLine(ReportUtil.TOTAL, "TSN(" + scale + ")", ABUNDANCE_LITERAL, 0, group, dim2Keys, widthCell));
            ImrIO.write(wr, getReportLine(ReportUtil.TOTAL, "TSB(" + scale + " kg)", BIOMASS_LITERAL, 1, group, dim2Keys, widthCell));
            ImrIO.write(wr, getReportLine(ReportUtil.TOTAL, "Mean length (cm)", MEAN_LENGTH_LITERAL, 2, group, dim2Keys, widthCell));
            ImrIO.write(wr, getReportLine(ReportUtil.TOTAL, "Mean weight (g)", MEAN_WEIGHT_LITERAL, 2, group, dim2Keys, widthCell));
            ImrIO.write(wr, ExportUtil.carrageReturnLineFeed("______________________________________________________________________________________________________"));
        }
    }

    String getReportLine(String dim1Key, String s, String var, Integer numDec, MatrixBO group, List<String> dim2Keys, int widthCell) {
        String line = String.format("%-18s", s);
        line = line + "| ";
        MatrixBO row = group.getValueAsMatrix(dim1Key);
        for (String xKey : dim2Keys) {
            if (xKey.equals(ReportUtil.TOTAL)) {
                continue;
            }
            line = line + formatNumber(row == null ? null : row.getValueAsMatrix(xKey), var, numDec, widthCell);
        }
        MatrixBO totalCell = row == null ? null : row.getValueAsMatrix(ReportUtil.TOTAL);

        line = line + formatNumber(!dim1Key.equals(ReportUtil.TOTAL) || var.equals(ABUNDANCE_LITERAL) ? totalCell : null, ABUNDANCE_LITERAL, 0, widthCell);
        line = line + formatNumber(!dim1Key.equals(ReportUtil.TOTAL) || var.equals(BIOMASS_LITERAL) ? totalCell : null, BIOMASS_LITERAL, 1, widthCell);
        line = line + formatNumber(!dim1Key.equals(ReportUtil.TOTAL) || var.equals(MEAN_WEIGHT_LITERAL) ? totalCell : null, MEAN_WEIGHT_LITERAL, 2, widthCell);
        line = ExportUtil.carrageReturnLineFeed(line);
        return line;
    }

    String formatNumber(MatrixBO cell, String var, int numDec, int width) {
        Double val = null;
        if (cell != null) {
            val = cell.getValueAsDouble(var);
        }
        return ReportUtil.formatNumber(val, numDec, width);
    }

    private String getDimVal(String dimParam, String dimVal) {
        if (dimVal != null && dimVal.equals("-")) {
            return "Unknown";
        }
        String dimParamVal = (String) getProcess().getParameterValue(dimParam);
        if (dimParamVal != null && dimParamVal.equals(Functions.COL_ABNDBYIND_LENGRP)) {
            Double dimInterval = Conversion.safeStringtoDoubleNULL(getProcess().getParameterValue(Functions.PM_ESTIMATEBYPOPCATEGORY_LENGTHINTERVAL) + "");
            Double v = Conversion.safeStringtoDouble(dimVal);
            String s = BioticUtils.getLenGrp(v, dimInterval) + "-" + BioticUtils.getLenGrp(v + dimInterval, dimInterval);
            return s;
        }
        return dimVal;

    }
}
