/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.landing;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import no.imr.stox.util.matrix.MatrixBO;
import no.imr.stox.util.base.ExportUtil;
import no.imr.stox.util.base.IMRdate;
import no.imr.stox.bo.LandingCovDataMatrix;
import no.imr.stox.bo.LandingData;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.bo.landing.LandingsdataBO;
import no.imr.stox.bo.landing.SeddellinjeBO;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.CovariateUtils;
import no.imr.stox.functions.utils.Functions;

/**
 *
 * @author aasmunds
 */
public class LandingCovData extends AbstractFunction {

    /**
     * define covariate individual data
     *
     * @param input
     * @return
     */
    @Override
    public Object perform(Map<String, Object> input) {
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_LANDINGCOVDATA_PROCESSDATA);
        //String var1 = (String) pd.getMatrix(Functions.TABLE_SPATIALVAR).getRowValue(Functions.PM_VAR1);
        //String var2 = (String) pd.getMatrix(Functions.TABLE_SPATIALVAR).getRowValue(Functions.PM_VAR2);
        LandingCovDataMatrix landCovData = new LandingCovDataMatrix();
        // Default handling (Define by given time interval:
        LandingData landData = (LandingData) input.get(Functions.PM_LANDINGCOVDATA_LANDINGDATA);
        MatrixBO tempM = AbndEstProcessDataUtil.getTemporal(pd);
        MatrixBO gearM = AbndEstProcessDataUtil.getGear(pd);
        MatrixBO spatialM = AbndEstProcessDataUtil.getSpatial(pd);
        for (LandingsdataBO la : landData) {
            for (SeddellinjeBO sl : la.getSeddellinjeBOs()) {
                // Filter station against covariates
                LocalDate d = sl.bo().getSisteFangstdato();
                if (d == null) {
                    continue;
                }
                String tempKey = CovariateUtils.getTemporalFullKey(Functions.SOURCETYPE_LANDING, d, tempM);
                if (tempKey == null) {
                    tempKey = "";
                }
                String gear = sl.bo().getRedskapKode();
                String gearKey = CovariateUtils.getCovKeyByDefElm(Functions.SOURCETYPE_LANDING, gear, gearM, false);
                if (gearKey == null) {
                    gearKey = "";
                }
                String spatCovValue = CovariateUtils.getSpatialCovValue(sl/*, var1, var2*/);
                String spatialKey = CovariateUtils.getCovKeyByDefElm(Functions.SOURCETYPE_LANDING, spatCovValue, spatialM, false);
                if (spatialKey == null) {
                    spatialKey = "";
                }
                String cov = ExportUtil.separated('/', tempKey, gearKey, spatialKey);
                List<SeddellinjeBO> l = (List<SeddellinjeBO>) landCovData.getData().getRowValue(cov);
                if (l == null) {
                    l = new ArrayList<>();
                    landCovData.getData().setRowValue(cov, l);
                }
                l.add(sl);
            }
        }
        return landCovData;
    }
}
