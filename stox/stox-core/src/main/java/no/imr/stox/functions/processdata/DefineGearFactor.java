/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.processdata;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.ProjectUtils;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author aasmunds
 */
public class DefineGearFactor extends AbstractFunction {

    /**
     * define gear covariates
     *
     * @param input contains Polygon file name DataTypeDescription.txt
     * @return
     */
    @Override
    public Object perform(Map<String, Object> input) {
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_DEFINEGEARFACTOR_PROCESSDATA);
        String defMethod = (String) input.get(Functions.PM_DEFINEGEARFACTOR_DEFINITIONMETHOD);
        String covariateType = (String) input.get(Functions.PM_DEFINETEMPORAL_COVARIATETYPE);
        String sourceType = (String) input.get(Functions.PM_DEFINEGEARFACTOR_SOURCETYPE);
        // cov param
        MatrixBO covParam = AbndEstProcessDataUtil.getCovParam(pd);
        MatrixBO m = covParam.getRowValueAsMatrix(AbndEstProcessDataUtil.TABLE_GEARFACTOR);
        if (m != null) {
            m.clear(); // Clear cov param
        }
        if (sourceType.equals(Functions.SOURCETYPE_BIOTIC)) {
            covParam.setRowColValue(AbndEstProcessDataUtil.TABLE_GEARFACTOR, Functions.PM_DEFINEGEARFACTOR_COVARIATETYPE, covariateType);
        }

        // covariate matrix
        MatrixBO covM = AbndEstProcessDataUtil.getGear(pd);
        if (defMethod.equals(Functions.DEFINITIONMETHOD_USEPROCESSDATA)) {
            // Use existing, do not read from file.
            return pd;
        }
        m = covM.getRowValueAsMatrix(sourceType);
        if (m != null) {
            m.clear(); // Clear covariates for covariate type and source
        }
        if (defMethod.equals(Functions.DEFINITIONMETHOD_RESOURCEFILE)) {
            // TODO: Read from file into process data.
            String fileName = ProjectUtils.resolveParameterFileName((String) input.get(Functions.PM_DEFINEGEARFACTOR_FILENAME), (String) input.get(Functions.PM_PROJECTFOLDER));
            if (fileName == null) {
                return pd;
            }
            List<String> lines;
            try {
                lines = FileUtils.readLines(new File(fileName));
                // Loop through the lines
                if (lines.isEmpty()) {
                    return pd;
                }
                lines.remove(0); // Remove header
                for (String line : lines) {
                    if (line.startsWith("#")) {
                        continue;
                    }
                    String[] elements = line.split("\t");
                    if (elements.length == 3) {
                        String id = elements[0].trim();
                        String source = elements[1].trim();
                        if (!source.equalsIgnoreCase(sourceType)) {
                            continue;
                        }
                        String def = elements[2].replace(" ", "");
                        covM.setRowColValue(source, id, def);
                    }
                }
            } catch (IOException ex) {
                Logger.getLogger(DefineGearFactor.class.getName()).log(Level.SEVERE, null, ex);
            }

            return pd;
        }
        return pd;
    }
}
