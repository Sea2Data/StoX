/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.report;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.stox.bo.AbundanceIndividualsMatrix;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.ProjectUtils;
import no.imr.stox.log.ILogger;

/**
 *
 * @author aasmunds
 */
public class FillMissingData extends AbstractFunction {

    @Override
    public Object perform(Map<String, Object> input) {
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        String fileNameSuperInd = (String) input.get(Functions.PM_FILLMISSINGDATA_SUPERINDIVIDUALS);
        if (!new File(fileNameSuperInd).exists()) {
            logger.error("Missing input file for parameter " + Functions.PM_ESTIMATEBYPOPCATEGORY_SUPERINDIVIDUALS, null);
        }
        AbundanceIndividualsMatrix abnByInd = readAbundance(fileNameSuperInd);
        MatrixBO abnd = abnByInd.getData();

        String fillVariables = (String) input.get(Functions.PM_FILLMISSINGDATA_FILLVARIABLES);
        String fillWeight = (String) input.get(Functions.PM_FILLMISSINGDATA_FILLWEIGHT);
        String a = (String) input.get(Functions.PM_FILLMISSINGDATA_A);
        String b = (String) input.get(Functions.PM_FILLMISSINGDATA_B);
        String fileNameLengthWeight = ProjectUtils.resolveParameterFileName((String) input.get(Functions.PM_FILLMISSINGDATA_FILENAME),
                (String) input.get(Functions.PM_PROJECTFOLDER));
        
        Integer seed = (Integer) input.get(Functions.PM_FILLMISSINGDATA_SEED);
        Boolean imputeByAge = fillVariables != null && fillVariables.equals(Functions.FILLVARIABLES_IMPUTEBYAGE);
        // Fill in missing weights
        if (imputeByAge) {
            if (seed == null) {
                seed = 1;
            }
            DistributeAbundance.distributeAbundance(abnd, seed, isSortedAbundance());
        }
        if (fillWeight != null) {
            AbndFillMissingWeights.fillMissingWeights(abnd, fillWeight, a, b, fileNameLengthWeight);
        }

        return abnByInd;
    }

    /**
     * Read abundance matrix by file.
     *
     * @param fileName
     * @return
     */
    private AbundanceIndividualsMatrix readAbundance(String fileName) {
        AbundanceIndividualsMatrix res = new AbundanceIndividualsMatrix();
        try (BufferedReader br = new BufferedReader((new InputStreamReader(new FileInputStream(fileName), StandardCharsets.UTF_8)))) {
            String line;
            String[] hdr = null;
            while ((line = br.readLine()) != null) {
                String[] s = line.split("\t");
                if (hdr == null) {
                    hdr = s;
                } else {
                    for (int j = 1; j < s.length; j++) {
                        String var = hdr[j];
                        switch (var) {
                            case "station":
                                var = Functions.COL_IND_SERIALNO;
                        }
                        Object val = s[j];
                        switch (var) {
                            case Functions.COL_IND_GONADWEIGHT:
                            case Functions.COL_IND_LIVERWEIGHT:
                            case Functions.COL_IND_WEIGHT:
                            case Functions.COL_IND_LENGTH:
                            case Functions.COL_IND_STOMACHWEIGHT:
                                val = Conversion.safeStringtoDoubleNULL(s[j]);
                                break;
                            case Functions.COL_IND_AGE:
                            case Functions.COL_IND_VERTEBRAE:
                                val = Conversion.safeStringtoIntegerNULL(s[j]);
                        }
                        res.getData().setRowColValue(s[0], var, val);
                    }
                }
            }
        } catch (IOException e) {
        }
        return res;
    }

    protected Boolean isSortedAbundance() {
        return true;
    }
}
