/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.processdata;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.ProjectUtils;
import no.imr.stox.log.ILogger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author aasmunds
 */
public class DefineAgeErrorMatrix extends AbstractFunction {

    /**
     * define temporal covariates
     *
     * @param input contains Polygon file name DataTypeDescription.txt
     * @return
     */
    @Override
    public Object perform(Map<String, Object> input) {
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_DEFINEAGEERRORMATRIX_PROCESSDATA);
        MatrixBO ageError = pd.getMatrix(AbndEstProcessDataUtil.TABLE_AGEERROR);
        String defMethod = (String) input.get(Functions.PM_DEFINEAGEERRORMATRIX_DEFINITIONMETHOD);
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        if (defMethod == null || defMethod.equals(Functions.DEFINITIONMETHOD_USEPROCESSDATA)) {
            // Use existing, do not read from file.
            return pd;
        }
        String fileName = ProjectUtils.resolveParameterFileName((String) input.get(Functions.PM_DEFINEAGEERRORMATRIX_FILENAME), (String) input.get(Functions.PM_PROJECTFOLDER));
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
            String[] realAges = lines.get(0).split("\t");
            if (lines.size() != realAges.length) {
                return null; // equal dimension size on real and read ages
            }
            lines.remove(0); // Remove header
            for (String line : lines) {
                if (line.startsWith("#")) {
                    continue;
                }
                String[] elements = line.split("\t");
                if (elements.length != realAges.length) {
                    return null;
                }
                String readAge = elements[0];
                for (int iRealAge = 1; iRealAge < elements.length; iRealAge++) {
                    String realAge = realAges[iRealAge];
                    Double p = Conversion.safeStringtoDoubleNULL(elements[iRealAge]);
                    if (p == null || p.equals(0d)) {
                        continue;
                    }
                    ageError.setRowColValue(realAge, readAge, p);
                }
            }
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
        return pd;
    }
}
