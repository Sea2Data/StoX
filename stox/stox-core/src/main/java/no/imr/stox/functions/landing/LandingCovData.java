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
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.sea2data.imrbase.util.ExportUtil;
import no.imr.sea2data.imrbase.util.IMRdate;
import no.imr.stox.bo.LandingCovDataMatrix;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.bo.landing.FiskeLinje;
import no.imr.stox.bo.landing.SluttSeddel;
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
        List<SluttSeddel> landData = (List) input.get(Functions.PM_LANDINGCOVDATA_LANDINGDATA);
        MatrixBO tempM = AbndEstProcessDataUtil.getTemporal(pd);
        MatrixBO gearM = AbndEstProcessDataUtil.getGear(pd);
        MatrixBO spatialM = AbndEstProcessDataUtil.getSpatial(pd);
        for (SluttSeddel sl : landData) {
            // Filter station against covariates
            LocalDate d = IMRdate.getLocalDate(sl.getSisteFangstDato());
            String tempKey = CovariateUtils.getTemporalFullKey(Functions.SOURCETYPE_LANDING, d, tempM);
            if (tempKey == null) {
                tempKey = "";
            }
            String gear = sl.getRedskap();
            String gearKey = CovariateUtils.getCovKeyByDefElm(Functions.SOURCETYPE_LANDING, gear, gearM, false);
            if (gearKey == null) {
                gearKey = "";
            }
            String spatCovValue = CovariateUtils.getSpatialCovValue(sl/*, var1, var2*/);
            String spatialKey = CovariateUtils.getCovKeyByDefElm(Functions.SOURCETYPE_LANDING, spatCovValue, spatialM, false);
            if (spatialKey == null) {
                spatialKey = "";
            }
            for (FiskeLinje fl : sl.getFiskelinjer()) {
                String cov = ExportUtil.separated('/', tempKey, gearKey, spatialKey);
                List<FiskeLinje> l = (List<FiskeLinje>) landCovData.getData().getRowValue(cov);
                if (l == null) {
                    l = new ArrayList<>();
                    landCovData.getData().setRowValue(cov, l);
                }
                l.add(fl);
            }
        }
        return landCovData;
    }

}
