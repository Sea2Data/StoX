/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.individualdata;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.biotic.bo.CatchSampleBO;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.sea2data.imrbase.util.ExportUtil;
import no.imr.stox.bo.BioticCovDataMatrix;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.CovariateUtils;
import no.imr.stox.functions.utils.Functions;

/**
 *
 * @author aasmunds
 */
public class BioticCovData extends AbstractFunction {

    /**
     * define covariate individual data
     *
     * @param input
     * @return
     */
    @Override
    public Object perform(Map<String, Object> input) {
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_BIOTICCOVDATA_PROCESSDATA);
        //String var1 = (String) pd.getMatrix(Functions.TABLE_SPATIALVAR).getRowValue(Functions.PM_VAR1);
        //String var2 = (String) pd.getMatrix(Functions.TABLE_SPATIALVAR).getRowValue(Functions.PM_VAR2);
        BioticCovDataMatrix indData = new BioticCovDataMatrix();
        // Default handling (Define by given time interval:
        List<FishstationBO> bioticData = (List) input.get(Functions.PM_BIOTICCOVDATA_BIOTICDATA);
        MatrixBO tempM = AbndEstProcessDataUtil.getTemporal(pd);
        MatrixBO gearM = AbndEstProcessDataUtil.getGear(pd);
        MatrixBO spatialM = AbndEstProcessDataUtil.getSpatial(pd);
        for (FishstationBO fs : bioticData) {
            // Filter station against covariates
            LocalDate d = fs.getStationstartdate();
            String tempKey = CovariateUtils.getTemporalFullKey(Functions.SOURCETYPE_BIOTIC, d, tempM);
            if (tempKey == null) {
                tempKey = "";
            }
            String gear = fs.getGear();
            String gearKey = CovariateUtils.getCovKeyByDefElm(Functions.SOURCETYPE_BIOTIC, gear, gearM, true);
            if (gearKey == null) {
                gearKey = "";
            }

            String spatCovValue = CovariateUtils.getSpatialCovValue(fs/*, var1, var2*/);
            String spatialKey = CovariateUtils.getCovKeyByDefElm(Functions.SOURCETYPE_BIOTIC, spatCovValue, spatialM, false);
            if (spatialKey == null) {
                spatialKey = "";
            }
            String platformKey = fs.getCatchplatform();
            if (platformKey == null) {
                platformKey = "";
            }
            for (CatchSampleBO s : fs.getCatchSampleBOs()) {
                for (IndividualBO i : s.getIndividualBOs()) {
                    String cov = ExportUtil.separated('/', tempKey, gearKey, spatialKey, platformKey);
                    List<IndividualBO> l = (List<IndividualBO>) indData.getData().getRowValue(cov);
                    if (l == null) {
                        l = new ArrayList<>();
                        indData.getData().setRowValue(cov, l);
                    }
                    l.add(i);
                }
            }
        }
        return indData;
    }

}
