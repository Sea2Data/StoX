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
import no.imr.stox.log.ILogger;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author aasmunds
 */
public class DefineStratumNeighbour extends AbstractFunction {

    /**
     * define temporal covariates
     *
     * @param input contains Polygon file name DataTypeDescription.txt
     * @return
     */
    @Override
    public Object perform(Map<String, Object> input) {
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_DEFINESTRATUMNEIGHBOUR_PROCESSDATA);
        MatrixBO stratumNeighbour = pd.getMatrix(AbndEstProcessDataUtil.TABLE_STRATUMNEIGHBOUR);
        String defMethod = (String) input.get(Functions.PM_DEFINESTRATUMNEIGHBOUR_DEFINITIONMETHOD);
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        if (defMethod == null || defMethod.equals(Functions.DEFINITIONMETHOD_USEPROCESSDATA)) {
            // Use existing, do not read from file.
            return pd;
        }
        stratumNeighbour.clear();
        String fileName = ProjectUtils.resolveParameterFileName((String) input.get(Functions.PM_DEFINESTRATUMNEIGHBOUR_FILENAME), (String) input.get(Functions.PM_PROJECTFOLDER));
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
                if (elements.length != 2) {
                    return null;
                }
                String var = elements[0];
                String value = elements[1];
                stratumNeighbour.setRowValue(var, value);
            }
        } catch (IOException ex) {
            Logger.getLogger(DefineGearFactor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return pd;
    }
}
