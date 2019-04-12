/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.processdata;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.MissionBO;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.sea2data.imrbase.util.ImrSort;
import no.imr.stox.bo.LandingData;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.CovariateUtils;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.log.ILogger;

/**
 *
 * @author aasmunds
 */
public class DefineSpatial extends AbstractFunction {

    /**
     * define spatial covariates
     *
     * @param input contains Polygon file name DataTypeDescription.txt
     * @return
     */
    @Override
    public Object perform(Map<String, Object> input) {
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_DEFINESPATIAL_PROCESSDATA);
        String defMethod = (String) input.get(Functions.PM_DEFINESPATIAL_DEFINITIONMETHOD);
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        // Apply transient dimension info to process data about spatial dimensions (used in cell translation/aggregation):
        //String var1 = (String) input.get(Functions.PM_DEFINESPATIAL_VAR1);
        //String var2 = (String) input.get(Functions.PM_DEFINESPATIAL_VAR2);
        // Default handling (by data)
        String sourceType = (String) input.get(Functions.PM_DEFINESPATIAL_SOURCETYPE);
        String covariateType = (String) input.get(Functions.PM_DEFINESPATIAL_COVARIATETYPE);
        Boolean conditionalAutoRegression = (Boolean) input.get(Functions.PM_DEFINESPATIAL_USESTRATUMNEIGHBOUR);
        if (conditionalAutoRegression == null) {
            conditionalAutoRegression = false;
        }
        LandingData landingData = (LandingData) input.get(Functions.PM_DEFINESPATIAL_LANDINGDATA);
        List<MissionBO> biotic = (List) input.get(Functions.PM_DEFINESPATIAL_BIOTICDATA);

        // cov param
        MatrixBO covParam = AbndEstProcessDataUtil.getCovParam(pd);
        MatrixBO m = covParam.getRowValueAsMatrix(AbndEstProcessDataUtil.TABLE_SPATIAL);
        if (m != null) {
            m.clear(); // Clear cov param
        }
        if (sourceType.equals(Functions.SOURCETYPE_BIOTIC)) {
            covParam.setRowColValue(AbndEstProcessDataUtil.TABLE_SPATIAL, Functions.PM_DEFINESPATIAL_COVARIATETYPE, covariateType);
        }
        if (covariateType != null && covariateType.equalsIgnoreCase(Functions.COVARIATETYPE_RANDOM)) {
            covParam.setRowColValue(AbndEstProcessDataUtil.TABLE_SPATIAL, Functions.PM_DEFINESPATIAL_USESTRATUMNEIGHBOUR, conditionalAutoRegression.toString());
        }

        // covariate matrix
        MatrixBO covM = AbndEstProcessDataUtil.getSpatial(pd);
        if (defMethod.equals(Functions.DEFINITIONMETHOD_USEPROCESSDATA)) {
            // Use existing, do not read from file.
            return pd;
        }
        m = covM.getRowValueAsMatrix(sourceType);
        if (m != null) {
            m.clear(); // Clear covariates 
        }
        if (defMethod.equals(Functions.DEFINITIONMETHOD_COPYFROMLANDING)) {
            if (sourceType.equals(Functions.SOURCETYPE_LANDING)) {
                logger.error("Cannot inherit from landing when sourcetype=landing.\n", null);
            }
            // Inherit covarids and definitions
            String toType = sourceType;
            String fromType = Functions.SOURCETYPE_LANDING;
            // Copying cov key from .. to ..
            covM.getRowColKeys(fromType).stream().forEach((covKey) -> {
                covM.setRowColValue(toType, covKey, covM.getRowColValue(fromType, covKey));
            });
            return pd;
        } else if (defMethod.equals(Functions.DEFINITIONMETHOD_USEDATA)) {
            // Use var1 and var2 to generate covariates from landing data.
            Set<String> covs = new HashSet<>();
            if (sourceType.equals(Functions.SOURCETYPE_LANDING)) {
                if (landingData == null) {
                    return pd;
                }
                landingData.stream()
                        .flatMap(l->l.getSeddellinjeBOs().stream())
                        .map((sl) -> CovariateUtils.getSpatialCovValue(sl/*, var1, var2*/))
                        .filter((def) -> (def != null))
                        .forEach((def) -> {
                            covs.add(def);
                        });
            } else {
                if (biotic == null) {
                    return pd;
                }
                biotic.stream().flatMap(ms -> ms.getFishstationBOs().stream()).map((fs) -> CovariateUtils.getSpatialCovValue(fs/*, var1, var2*/)).filter((def) -> (def != null)).forEach((def) -> {
                    covs.add(def);
                });
            }
            List<String> covsList = new ArrayList<>(covs);
            Collections.sort(covsList, (String o1, String o2) -> {
                String[] elms1 = o1.split("_");
                String[] elms2 = o2.split("_");
                int i = ImrSort.compareTranslative(elms1[0], elms2[0]);
                if (i == 0 && elms1.length == 2 && elms2.length == 2) {
                    i = ImrSort.compareTranslative(elms1[1], elms2[1]);
                }
                return i;
            });
            // Generate covariates:
            for (int i = 0; i < covsList.size(); i++) {
                String cov = covsList.get(i);//(i + 1) + "";
                String def = covsList.get(i);
                covM.setRowColValue(sourceType, cov, def);
            }
        }
        return pd;
    }
}
