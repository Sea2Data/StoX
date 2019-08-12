/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.processdata;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.MissionBO;
import no.imr.stox.util.matrix.MatrixBO;
import no.imr.stox.bo.BioticData;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;

/**
 *
 * @author aasmunds
 */
public class DefinePlatform extends AbstractFunction {

    /**
     * define gear covariates
     *
     * @param input contains Polygon file name DataTypeDescription.txt
     * @return
     */
    @Override
    public Object perform(Map<String, Object> input) {
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_DEFINESPATIAL_PROCESSDATA);
        BioticData biotic = (BioticData) input.get(Functions.PM_DEFINESPATIAL_BIOTICDATA);
        String defMethod = (String) input.get(Functions.PM_DEFINESPATIAL_DEFINITIONMETHOD);
        // Apply transient dimension info to process data about spatial dimensions (used in cell translation/aggregation):
        // Default handling (by data)
        String sourceType = (String) input.get(Functions.PM_DEFINESPATIAL_SOURCETYPE);
        String covariateType = (String) input.get(Functions.PM_DEFINESPATIAL_COVARIATETYPE);
        
        // Cov param
        MatrixBO covParam = AbndEstProcessDataUtil.getCovParam(pd);
        MatrixBO m = covParam.getRowValueAsMatrix(AbndEstProcessDataUtil.TABLE_PLATFORM);
        if (m != null) {
            m.clear(); // Clear covariates for covariate type and source
        }
        if (sourceType.equals(Functions.SOURCETYPE_BIOTIC)) {
            covParam.setRowColValue(AbndEstProcessDataUtil.TABLE_PLATFORM, Functions.PM_DEFINEPLATFORM_COVARIATETYPE, covariateType);
        }
        
        // Covariate
        MatrixBO covP = AbndEstProcessDataUtil.getPlatform(pd);
        if (defMethod.equals(Functions.DEFINITIONMETHOD_USEPROCESSDATA)) {
            // Use existing, do not read from file.
            return pd;
        }
        m = covP.getRowValueAsMatrix(sourceType);
        if (m != null) {
            m.clear(); // Clear covariates for covariate type and source
        }
        if (defMethod.equals(Functions.DEFINITIONMETHOD_USEDATA)) {
            // Use var1 and var2 to generate covariates from landing data.

            Set<String> covs = new HashSet<>();
            if (sourceType.equals(Functions.SOURCETYPE_BIOTIC)) {
                if (biotic == null) {
                    return pd;
                }
                biotic.getMissions().stream().flatMap(ms -> ms.getFishstationBOs().stream()).map((fs) -> {
                    //fs.
                    return fs.bo().getCatchplatform();
                }).filter((def) -> (def != null)).forEach((def) -> {
                    covP.setRowColValue(sourceType, def, def);
                });
            }
        }
        return pd;
    }
}
