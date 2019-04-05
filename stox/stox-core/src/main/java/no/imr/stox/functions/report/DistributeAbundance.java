/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.functions.utils.Functions;

/**
 *
 * @author aasmunds
 */
public class DistributeAbundance {

    /**
     * Distribute unknown individual biological parameters from known values
     * ----
     *
     * This function fills in holes in individual fish samples (also called
     * imputation). In cases where individuals are not aged, the variables
     * "weight","age","sex", and "specialstage" are sampled from fish in the
     * same length group at the lowest imputation level possible. impLevel = 0:
     * no imputation, biological information exists impLevel = 1: imputation at
     * station level; biological information is selected at random from fish
     * within station impLevel = 2: imputation at strata level; no information
     * available at station level, random selection within stratum impLevel = 3:
     * imputation at survey level; no information available at lower levels,
     * random selection within suvey
     *
     * @param abnd Abundance matrix with individual data
     * @param seed
     */
    private static final int MAXLEVELS = 3;
    private static final String KNOWNCRITERIAVAR = Functions.COL_IND_AGE;

    public static void distributeAbundance(MatrixBO abnd, Integer seed, Boolean sortedAbundance) {
        Collection<String> abndRowKeys = sortedAbundance ? abnd.getSortedRowKeys() : abnd.getRowKeys();
        Collection<String> specList = abndRowKeys.stream().parallel().map(rowKey -> {
            MatrixBO row = abnd.getRowValueAsMatrix(rowKey);
            String species = (String) row.getValue(Functions.COL_IND_CATCHCATEGORY);
            return species;
        }).collect(Collectors.toSet());
        // Separated imputes must be done inside each species
        for (String spec : specList) {
            List<MatrixBO> abndDistrKnown = new ArrayList<>();
            List<MatrixBO> abndDistrUnknown = new ArrayList<>();
            // Set the random generator
            Random r = new Random();
            if (seed != null) {
                r.setSeed(Math.abs(seed)); // Reproducability
            }
            for (String rowKey : abndRowKeys) {
                MatrixBO row = abnd.getRowValueAsMatrix(rowKey);
                String species = (String) row.getValue(Functions.COL_IND_CATCHCATEGORY);
                if (!species.equals(spec)) {
                    continue;
                }
                Object age = row.getValue(KNOWNCRITERIAVAR);
                if (age == null) {
                    abndDistrUnknown.add(row);
                } else {
                    abndDistrKnown.add(row);
                }
            }
            /*System.out.println("Data have " + abndDistrKnown.size() + " aged individuals.");
        System.out.println("Data have " + abndDistrUnknown.size() + " length sampled individuals without known age.");

            int[] countImpLevels = new int[MAXLEVELS];
        int[] missingImputes = new int[1];
        missingImputes[0] = 0;
        for (int i = 0; i < countImpLevels.length; i++) {
            countImpLevels[i] = 0;
            }*/
            // Unknown loop (nonseed for parallelization, seed for reproducability)
            abndDistrUnknown.stream().forEach(row -> {
                List<MatrixBO> knownData = new ArrayList<>();
                String strata = (String) row.getValue(Functions.COL_ABNDBYIND_STRATUM);
                String lenGrp = (String) row.getValue(Functions.COL_ABNDBYIND_LENGRP);
                String cruise = (String) row.getValue(Functions.COL_IND_CRUISE);
                String serialno = (String) row.getValue(Functions.COL_IND_SERIALNUMBER);
                // Pick known data from different imputation levels
                for (int impLevel = 0; impLevel < MAXLEVELS; impLevel++) {
                    for (MatrixBO rowK : abndDistrKnown) {
                        String strataK = (String) rowK.getValue(Functions.COL_ABNDBYIND_STRATUM);
                        String lenGrpK = (String) rowK.getValue(Functions.COL_ABNDBYIND_LENGRP);
                        String cruiseK = (String) rowK.getValue(Functions.COL_IND_CRUISE);
                        String serialnoK = (String) rowK.getValue(Functions.COL_IND_SERIALNUMBER);
                        Boolean skipStationCheck = impLevel >= 1;
                        Boolean skipStrataCheck = impLevel >= 2;
                        boolean match = (skipStrataCheck || strataK.equals(strata))
                                && (skipStationCheck || cruiseK.equals(cruise) && serialnoK.equals(serialno))
                                && lenGrpK.equals(lenGrp);
                        if (match) {
                            knownData.add(rowK);
                        }
                    }
                    if (knownData.size() > 0) {
                        //countImpLevels[impLevel]++;
                        break;
                    }
                }
                if (knownData.isEmpty()) {
                    //missingImputes[0]++;
                } else {
                    // Pick random from known data
                    int idx = r.nextInt(knownData.size());//r.nextInt(abndRowKeys.size());
                    idx = idx % knownData.size();
                    MatrixBO rowK = knownData.get(idx);
                    // Transfer individual variables if missing
                    for (String code : Functions.INDIVIDUALS) {
                        Object o = row.getValue(code);
                        if (o == null || o.toString().equals("-")) {
                            row.setValue(code, rowK.getValue(code));
                        }
                    }
                }
            });
            /*System.out.println(countImpLevels[0] + " individual ages were imputed at station level.");
        System.out.println(countImpLevels[1] + " individual ages were imputed at strata level.");
        System.out.println(countImpLevels[2] + " individual ages were imputed at survey level.");
        System.out.println(missingImputes[0] + " individual ages were not possible to impute.");*/
        }
    }
}
