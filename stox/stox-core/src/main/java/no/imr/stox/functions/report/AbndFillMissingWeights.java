/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.report;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import no.imr.stox.functions.utils.ReportUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.imr.sea2data.imrbase.math.LWRelationship;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.sea2data.imrbase.util.ExportUtil;
import no.imr.stox.bo.LengthWeightRelationshipMatrix;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.StoXMath;

/**
 *
 * @author aasmunds
 */
public class AbndFillMissingWeights {

    /**
     * Function to fill in missing weights
     *
     * @param abnByIndData
     * @param method
     */
    public static void fillMissingWeights(MatrixBO abnByIndData, String method, String a, String b, String fileNameLengthWeight) {
        MatrixBO weights = new MatrixBO();
        Double lenIntv = null;
        for (String row : abnByIndData.getRowKeys()) {
            Double abundance = abnByIndData.getRowColValueAsDouble(row, Functions.COL_ABNDBYIND_ABUNDANCE);
            if (abundance == null || abundance == 0) {
                // Give error
                continue;
            }
            Double weight = abnByIndData.getRowColValueAsDouble(row, Functions.COL_IND_WEIGHT);
            String specCatKey = (String) abnByIndData.getRowColValue(row, Functions.COL_ABNDBYIND_SPECCAT);
            String stratum = (String) abnByIndData.getRowColValue(row, Functions.COL_ABNDBYIND_STRATUM);
            String estLayer = (String) abnByIndData.getRowColValue(row, Functions.COL_ABNDBYIND_ESTLAYER);
            String lenGrp = (String) abnByIndData.getRowColValue(row, Functions.COL_ABNDBYIND_LENGRP);
            if (lenIntv == null) {
                // Length interval is repeated in the table, thus retrieve it once. used in regression.
                lenIntv = abnByIndData.getRowColValueAsDouble(row, Functions.COL_ABNDBYIND_LENINTV);
            }
            for (int i = 0b0; i <= 0b1; i++) {
                String stratumKey = ReportUtil.key(0b1, i, stratum);
                String headingKey = ExportUtil.separated('/', estLayer, stratumKey, specCatKey);
                Double WAbnd = weight != null ? abundance : 0d;
                if (weight != null) {
                    // the productsum gives the weighted weights.
                    weights.addGroupRowColValue(headingKey, lenGrp, "SumW", weight * WAbnd);
                }
                weights.addGroupRowColValue(headingKey, lenGrp, "SumWAbnd", WAbnd);
            }
        }
        // Estimate missing weights per strata and length group
        for (String heading : weights.getKeys()) {
            if (isHeadingTotStratum(heading)) {
                continue;
            }
            for (String lenGrp : weights.getGroupRowKeys(heading)) {
                Double meanWeight = weights.getGroupRowColValueAsDouble(heading, lenGrp, "MeanWeight");
                if (meanWeight == null) {
                    switch (method) {
                        case Functions.FILLWEIGHT_MEAN:
                            meanWeight = getMeanWeightFromWeights(weights, heading, lenGrp);
                            break;
                        case Functions.FILLWEIGHT_REGRESSION:
                            meanWeight = getMeanWeightFromRegression(abnByIndData, lenGrp, lenIntv);
                            break;
                    }
                }
                weights.setGroupRowColValue(heading, lenGrp, "MeanWeight", meanWeight);
            }
        }
        LengthWeightRelationshipMatrix lwM = null;
        if (method.equals(Functions.FILLWEIGHT_FROMFILE)) {
            lwM = getLengthWeightFromFile(fileNameLengthWeight);
        }
        // Fill in missing weights in super individual matrix:
        for (String row : abnByIndData.getRowKeys()) {
            String lenGrp = (String) abnByIndData.getRowColValue(row, Functions.COL_ABNDBYIND_LENGRP);
            Double weight = abnByIndData.getRowColValueAsDouble(row, Functions.COL_IND_WEIGHT);
            if (weight == null) {
                switch (method) {
                    case Functions.FILLWEIGHT_MANUALLY: {
                        String species = (String) abnByIndData.getRowColValue(row, Functions.COL_IND_SPECIES);
                        weight = getMeanWeightFromStandard(lenGrp, lenIntv, a, b, species);
                        break;
                    }
                    case Functions.FILLWEIGHT_FROMFILE: {
                        String stratum = (String) abnByIndData.getRowColValue(row, Functions.COL_ABNDBYIND_STRATUM);
                        String aphia = (String) abnByIndData.getRowColValue(row, Functions.COL_IND_APHIA);
                        if (lwM != null) {
                            Double aa = lwM.getData().getGroupRowColValueAsDouble(aphia, stratum, Functions.LENGTHWEIGHT_COEFF_A);
                            Double bb = lwM.getData().getGroupRowColValueAsDouble(aphia, stratum, Functions.LENGTHWEIGHT_COEFF_B);
                            if (aa == null || bb == null) {
                                aa = lwM.getData().getGroupRowColValueAsDouble(aphia, "TOTAL", Functions.LENGTHWEIGHT_COEFF_A);
                                bb = lwM.getData().getGroupRowColValueAsDouble(aphia, "TOTAL", Functions.LENGTHWEIGHT_COEFF_B);
                            }
                            weight = getMeanWeightFromStandard(lenGrp, lenIntv, aa, bb);
                        }
                        break;
                    }
                    default: {
                        String estLayer = (String) abnByIndData.getRowColValue(row, Functions.COL_ABNDBYIND_ESTLAYER);
                        String stratum = (String) abnByIndData.getRowColValue(row, Functions.COL_ABNDBYIND_STRATUM);
                        String specCatKey = (String) abnByIndData.getRowColValue(row, Functions.COL_ABNDBYIND_SPECCAT);
                        String heading = ExportUtil.separated('/', estLayer, stratum, specCatKey);
                        weight = weights.getGroupRowColValueAsDouble(heading, lenGrp, "MeanWeight");
                    }
                }
                if (weight != null) {
                    abnByIndData.setRowColValue(row, Functions.COL_IND_WEIGHT, weight);
                }
            }
        }
    }

    // Estimate weighted mean weight directly from strata or all stratas at length group
    // Weighted by abundance, since superindividual split abundance may be distributed different (weighted by catch)
    // When equal distribution, this weighting has no effect, generally all means are weighted by 1
    private static Double getMeanWeightFromWeights(MatrixBO weights, String heading, String lenGrp) {
        String headingSearch = heading;
        Double sumWAbnd = weights.getGroupRowColValueAsDouble(headingSearch, lenGrp, "SumWAbnd");
        if (sumWAbnd == null || sumWAbnd == 0d) {
            headingSearch = headingTotStratum(headingSearch);
            sumWAbnd = weights.getGroupRowColValueAsDouble(headingSearch, lenGrp, "SumWAbnd");
            if (sumWAbnd == null || sumWAbnd == 0d) {
                return null;
            }
        }
        Double sumW = weights.getGroupRowColValueAsDouble(headingSearch, lenGrp, "SumW");
        return sumW != null ? sumW / sumWAbnd : null;
    }

    /**
     * Fill in missing weights base on regression
     *
     * @param abnByIndData
     * @param lenGrp
     * @param lenIntv
     * @return
     */
    private static Double getMeanWeightFromRegression(MatrixBO abnByIndData, String lenGrp, Double lenIntv) {
        // For now use all individuals with given length group in all strata
        List<MatrixBO> indList = new ArrayList<>();
        for (String rowKey : abnByIndData.getRowKeys()) {
            MatrixBO row = abnByIndData.getRowValueAsMatrix(rowKey);
            Double weight = row.getValueAsDouble(Functions.COL_IND_WEIGHT);
            if (weight != null && weight > 0d) {
                indList.add(row);
            }
        }
        if (indList.size() > 2) {
            Double[] lenInCM = new Double[indList.size()];
            Double[] wInGrams = new Double[indList.size()];
            for (int i = 0; i < indList.size(); i++) {
                MatrixBO row = indList.get(i);
                lenInCM[i] = row.getValueAsDouble(Functions.COL_IND_LENGTH);
                wInGrams[i] = row.getValueAsDouble(Functions.COL_IND_WEIGHT);
            }
            LWRelationship lwr = LWRelationship.getLWRelationship(lenInCM, wInGrams);
            Double length = StoXMath.getLength(Conversion.safeStringtoDoubleNULL(lenGrp), lenIntv);
            return lwr.getWeight(length);
        }
        return null;
    }

    public static String headingTotStratum(String heading) {
        String[] s = heading.split("/");
        return ExportUtil.separated('/', s[0], "TOTAL", s[2]);
    }

    public static Boolean isHeadingTotStratum(String heading) {
        String[] s = heading.split("/");
        return s[1].equals("TOTAL");
    }

    private static Double extractConstant(String constant, String species) {
        if (constant == null) {
            return null;
        }
        Double c = Conversion.safeStringtoDoubleNULL(constant);
        if (c == null) {
            if (constant.contains(",")) {
                String[] elms = constant.split(",");
                Optional<String> str = Arrays.stream(elms).map(s -> s.split(":"))
                        .filter(st -> {
                            return st.length == 2 && st[0].equals(species);
                        }).map(s -> s[1]).findFirst();
                if (str.isPresent()) {
                    return Conversion.safeStringtoDoubleNULL(str.get());
                }
            }
        }
        return c;
    }

    private static Double getMeanWeightFromStandard(String lenGrp, Double lenIntv, String aa, String bb, String species) {
        Double a = extractConstant(aa, species);
        Double b = extractConstant(bb, species);
        return getMeanWeightFromStandard(lenGrp, lenIntv, a, b);
    }

    private static Double getMeanWeightFromStandard(String lenGrp, Double lenIntv, Double a, Double b) {
        Double length = StoXMath.getLength(Conversion.safeStringtoDoubleNULL(lenGrp), lenIntv);
        if (a != null && b != null && length != null) {
            return a * Math.pow(length, b);
        }
        return null;
    }

    private static LengthWeightRelationshipMatrix getLengthWeightFromFile(String fileNameLengthWeight) {
        try {
            LengthWeightRelationshipMatrix res = new LengthWeightRelationshipMatrix();
            if (!(new File(fileNameLengthWeight)).exists()) {
                return res;
            }
            List<String> lines = Files.readAllLines(Paths.get(fileNameLengthWeight));
            if (lines.size() <= 1) {
                return res;
            }
            lines.remove(0);
            lines.forEach(line -> {
                String[] elms = line.split("\\s++");
                if (elms.length == 5) {
                    res.getData().setGroupRowColValue(elms[0], elms[1], Functions.LENGTHWEIGHT_COEFF_A, Conversion.safeStringtoDoubleNULL(elms[2]));
                    res.getData().setGroupRowColValue(elms[0], elms[1], Functions.LENGTHWEIGHT_COEFF_B, Conversion.safeStringtoDoubleNULL(elms[3]));
                    res.getData().setGroupRowColValue(elms[0], elms[1], Functions.LENGTHWEIGHT_COEFF_R2, Conversion.safeStringtoDoubleNULL(elms[4]));
                }
            });
            return res;
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }
}
