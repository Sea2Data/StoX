package no.imr.stox.functions.report;

import no.imr.stox.functions.utils.ReportUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import no.imr.stox.util.math.Calc;
import no.imr.stox.util.math.ImrMath;
import no.imr.stox.util.base.Conversion;
import no.imr.stox.util.base.ImrSort;
import no.imr.stox.bo.AbundanceIndividualsMatrix;
import no.imr.stox.util.matrix.MatrixBO;
import no.imr.stox.bo.ReportsBO;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.BioticUtils;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.StoXMath;
import no.imr.stox.log.ILogger;
import org.apache.commons.lang.StringUtils;

/**
 * This function converts an abundance estimate into an estimate by one or more
 * population parameters (example age, sex, stage..) over length. Inputs to this
 * function are a list of population parameters and a list of individuals in
 * estimation layers and strata.
 *
 * @author esmaelmh
 */
public class AbundanceByPopulationCategory extends AbstractFunction {

    @Override
    public Object perform(Map<String, Object> input) {
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        AbundanceIndividualsMatrix abnByInd = (AbundanceIndividualsMatrix) input.get(Functions.PM_ESTIMATEBYPOPCATEGORY_SUPERINDIVIDUALS);
        MatrixBO abnd = abnByInd.getData();
        Integer scale = (Integer) input.get(Functions.PM_ESTIMATEBYPOPCATEGORY_SCALE);
        Double lenInterval = (Double) input.get(Functions.PM_ESTIMATEBYPOPCATEGORY_LENGTHINTERVAL);
        // Ensure that parameter lenInterval >= inherited length interval.
        if (!abnd.isEmpty() && lenInterval != null) {
            Double inhInterval = abnd.getRowColValueAsDouble(abnd.getRowKeys().get(0), Functions.COL_ABNDBYIND_LENINTV);
            if (inhInterval != null && !StoXMath.isInteger(lenInterval / inhInterval)) {
                logger.error("Length interval " + lenInterval + " must be a multiple integer factor of " + inhInterval + ".", null);
            }
        }

        MatrixBO result = new MatrixBO(Functions.MM_XCATBYLENGTH_MATRIX);
        ReportsBO reports = new ReportsBO();
        String dim1 = (String) input.get(Functions.PM_ESTIMATEBYPOPCATEGORY_DIM1); // Get from parameter.
        String dim2 = (String) input.get(Functions.PM_ESTIMATEBYPOPCATEGORY_DIM2); // Get from parameter.
        String dim3 = (String) input.get(Functions.PM_ESTIMATEBYPOPCATEGORY_DIM3); // Get from parameter.
        String dim4 = (String) input.get(Functions.PM_ESTIMATEBYPOPCATEGORY_DIM4); // Get from parameter.
        String dim5 = (String) input.get(Functions.PM_ESTIMATEBYPOPCATEGORY_DIM5); // Get from parameter.
        if (scale == null) {
            scale = 1;
        }
        try {

            // Resolve the exclude strata set 
            Set<String> exclStrata = getExcludedStrata(abnd);
            // Aggregate accumulation variables from super individuals to strata (abundance, SumL, Biomass, SUmWAbnd) by reporttype, cruise, station, strata, length and xcat. (Totals handled)
            result = aggregateVariables(abnd, exclStrata, scale, logger, dim1, dim2, dim3, dim4, dim5, result, lenInterval);
            // Calculate derived variables (MeanWeight, MeanLength) at aggregated levels (Totals included):
            calculateDerivedVariables(result, logger);
            // fill missing abundance with zero 
            if (dim1.equals(Functions.COL_ABNDBYIND_LENGRP)) {
                fillMissingLengthKeysForDim1(result, lenInterval);
            }
            // Info and heading
            // apply excluded data to the result.
            setExcludedStrataInResult(exclStrata, result);
            // Set result
            reports.getMatrices().put(Functions.REPORT_XCATBYLENGTH, result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return reports;
    }

    private Set<String> getExcludedStrata(MatrixBO abnByIndData) {
        Set<String> exclStrata = new HashSet<>();
        for (String row : abnByIndData.getRowKeys()) {
            String stratum = (String) abnByIndData.getRowColValue(row, Functions.COL_ABNDBYIND_STRATUM);
            String b = (String) abnByIndData.getRowColValue(row, Functions.COL_ABNDBYIND_INCLUDEINTOTAL);
            Boolean exl = b != null ? !Boolean.valueOf(b) : false;
            if (exl) {
                exclStrata.add(stratum);
            }
        }
        return exclStrata;
    }

    private void setExcludedStrataInResult(Set<String> exclStrata, MatrixBO result) {
        if (exclStrata.isEmpty()) {
            return;
        }
        // Sort the excluded strata in a list
        List<String> exlStrataL = new ArrayList<>(exclStrata);
        Collections.sort(exlStrataL, new ImrSort.TranslativeComparator<>(true));
        // Sort the excluded strata in a list for total in all dimensions
        String headingKey = ReportUtil.heading("TOTAL", "TOTAL", "TOTAL", "TOTAL", "TOTAL");
        result.setGroupRowColCellValue(headingKey, "TOTAL", "TOTAL", "ExcludedStrata",
                StringUtils.join(exlStrataL, ','));
    }

    // Aggregate accumulation variables from superindividual to station level.
    // This will be the basic for unknown distribution of variables.
    //
    private MatrixBO aggregateVariables(MatrixBO abnd, Set<String> exclStrata, Integer scale, ILogger logger,
            String dim1, String dim2, String dim3, String dim4, String dim5, MatrixBO result, Double lenInterval) {
        abnd.getRowKeys().stream().forEach((row) -> {
            String stratum = (String) abnd.getRowColValue(row, Functions.COL_ABNDBYIND_STRATUM);
            String estLayer = (String) abnd.getRowColValue(row, Functions.COL_ABNDBYIND_ESTLAYER);
            String dim1Val = getDimVal(dim1, abnd, row, lenInterval);
            String dim2Val = getDimVal(dim2, abnd, row, lenInterval);
            String dim3Val = getDimVal(dim3, abnd, row, lenInterval);
            String dim4Val = getDimVal(dim4, abnd, row, lenInterval);
            String dim5Val = getDimVal(dim5, abnd, row, lenInterval);

            Boolean doExclStrataFromTotal = exclStrata.contains(stratum);
            Double abundance = abnd.getRowColValueAsDouble(row, Functions.COL_ABNDBYIND_ABUNDANCE);
            abundance = ImrMath.safeDivide(abundance, scale.doubleValue());
            if (abundance == null || abundance == 0) {
                logger.error("Super individual cannot contain zero abundance", null);
            }
            Double length = abnd.getRowColValueAsDouble(row, Functions.COL_IND_LENGTHCENTIMETER);
            Double sumL = length != null ? abundance * length : null;
            Double weight = abnd.getRowColValueAsDouble(row, Functions.COL_IND_INDIVIDUALWEIGHTGRAM);
            Double biomass = ImrMath.safeMult(abundance, StoXMath.gramsToKg(weight));
            //String reportKey = byStation ? ExportUtil.separated('~', reportType, cruise, station) : reportType;
            // Aggregate superindividual variables
            for (int i = 0b0000000; i <= 0b1111111; i++) {
                String stratumKey = ReportUtil.key(0b1000000, i, stratum);
                String estKey = ReportUtil.key(0b0100000, i, estLayer);
                String dim1Key = ReportUtil.key(0b0010000, i, dim1Val);
                String dim2Key = ReportUtil.key(0b0001000, i, dim2Val);
                String dim3Key = ReportUtil.key(0b0000100, i, dim3Val);
                String dim4Key = ReportUtil.key(0b0000010, i, dim4Val);
                String dim5Key = ReportUtil.key(0b0000001, i, dim5Val);

                String headingKey = ReportUtil.heading(estKey, stratumKey, dim3Key, dim4Key, dim5Key);
                if (stratumKey.equals("TOTAL")) {
                    if (doExclStrataFromTotal) {
                        continue;
                    }
                }
                result.addGroupRowColCellValue(headingKey, dim1Key, dim2Key, "Abundance", abundance);
                if (biomass != null) {
                    result.addGroupRowColCellValue(headingKey, dim1Key, dim2Key, "Biomass", biomass);
                    result.addGroupRowColCellValue(headingKey, dim1Key, dim2Key, "SumWAbnd", abundance);
                }
                if (sumL != null) {
                    result.addGroupRowColCellValue(headingKey, dim1Key, dim2Key, "SumL", sumL);
                }
            }
        });
        return result;
    }

    public static Boolean isHeadingDistributed(String heading) {
        String[] s = heading.split("/");
        return s[0].startsWith("D");
    }

    private void calculateDerivedVariables(MatrixBO result, ILogger logger) {
        Set<String> missingBiomass = new HashSet<>();
        for (String heading : result.getKeys()) {
            for (String dim1Key : result.getGroupRowKeys(heading)) {
                for (String dim2Key : result.getGroupRowColKeys(heading, dim1Key)) {
                    Double abundance = result.getGroupRowColCellValueAsDouble(heading, dim1Key, dim2Key, "Abundance");
                    Double biomass = result.getGroupRowColCellValueAsDouble(heading, dim1Key, dim2Key, "Biomass");
                    Double sumWAbnd = result.getGroupRowColCellValueAsDouble(heading, dim1Key, dim2Key, "SumWAbnd");
                    Double sumL = result.getGroupRowColCellValueAsDouble(heading, dim1Key, dim2Key, "SumL");
                    Double meanWeightInG = biomass != null && sumWAbnd != null && sumWAbnd > 0 ? biomass * 1000 / sumWAbnd : null;
                    Double meanLength = sumL != null && abundance != null ? sumL / abundance : null;
                    result.setGroupRowColCellValue(heading, dim1Key, dim2Key, "MeanWeight", meanWeightInG);
                    result.setGroupRowColCellValue(heading, dim1Key, dim2Key, "MeanLength", meanLength);
                }
                Double b = result.getGroupRowColCellValueAsDouble(heading, dim1Key, "TOTAL", "Biomass");
                if (b == null) {
                    missingBiomass.add(dim1Key);
                }
            }
        }
        if (!missingBiomass.isEmpty()) {
            logger.log("Warning: Biomass not calculated for length group(s): " + missingBiomass + ". Consider using regression to estimate meanweight at the report");
        }
    }

    // Zero out missing length groups
    private void fillMissingLengthKeysForDim1(MatrixBO result, Double lenInterval) {
        Double firstLenGrp = Double.MAX_VALUE;
        Double lastLenGrp = -Double.MAX_VALUE;
        for (String heading : result.getKeys()) {
            for (String lenGrp : result.getGroupRowKeys(heading)) {
                if (lenGrp.equals("TOTAL")) {
                    continue;
                }
                Double lengthGroupInCM = ImrMath.trunc(Conversion.safeStringtoDoubleNULL(lenGrp), lenInterval);
                if (lengthGroupInCM == null) {
                    continue;
                }
                firstLenGrp = Math.min(firstLenGrp, lengthGroupInCM);
                lastLenGrp = Math.max(lastLenGrp, lengthGroupInCM);
            }
        }
        if (firstLenGrp == Double.MAX_VALUE) {
            return;
        }
        Integer numLenGroups = (int) ((lastLenGrp - firstLenGrp) / lenInterval);
        for (String heading : result.getKeys()) {
            MatrixBO page = result.getValueAsMatrix(heading);
            for (int i = 0; i < numLenGroups - 1; i++) {
                Double lGrp = Calc.roundTo(firstLenGrp + i * lenInterval, 8);
                String lenGrp = BioticUtils.getLenGrp(lGrp, lenInterval);
                // Register the key with null value if not exisiting:
                // This will ensure that the report contains the length group
                if (page.getValue(lenGrp) == null) {
                    page.setValue(lenGrp, null);
                }
            }
        }
    }

    private String getDimVal(String dim, MatrixBO abnd, String row, Double lenInterval) {
        Object dimVal = abnd.getRowColValue(row, dim);
        if (dimVal != null && dim.equals(Functions.COL_ABNDBYIND_LENGRP)) {
            Double len = Conversion.safeObjectToDouble(dimVal);
            return BioticUtils.getLenGrp(len, lenInterval);
        }
        return dimVal != null ? dimVal.toString() : "-";
    }

}
