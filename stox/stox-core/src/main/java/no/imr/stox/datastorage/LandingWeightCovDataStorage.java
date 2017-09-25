/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.datastorage;

import java.io.Writer;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrbase.util.ExportUtil;
import no.imr.sea2data.imrbase.util.ImrIO;
import no.imr.stox.bo.LandingWeightCovDataMatrix;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.CovariateUtils;
import no.imr.stox.functions.utils.Functions;

/**
 *
 * @author aasmunds
 */
public class LandingWeightCovDataStorage extends FileDataStorage {

    @Override
    public <T> void asTable(T data, Integer level, Writer wr, Boolean withUnits) {
        Boolean seasonal = true;
        MatrixBO m = AbndEstProcessDataUtil.getTemporal(getProcess().getModel().getProject().getProcessData());
        for (String cov : m.getRowColKeys(Functions.SOURCETYPE_BIOTIC)) {
            // At this point we can extract the year from the covariate, since it is aggregated into a year.season covariate
            seasonal = CovariateUtils.isCovariateSeasonal(cov);
            break;
        }
        String temporalHdr = seasonal ? ExportUtil.tabbed("Year", "Season") : ExportUtil.tabbed("Temporal");
        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(temporalHdr, "GearFactor", "Spatial", "Weight")));
        LandingWeightCovDataMatrix flData = (LandingWeightCovDataMatrix) data;
        // GROUP: For each temporal 
        for (String cov : flData.getData().getSortedKeys()) {
            Integer year = Conversion.safeStringtoIntegerNULL(Conversion.safeSubstring(cov, 0, 4));
            Integer season = CovariateUtils.getSeasonByCovariate(cov);
            String temporalCov = seasonal ? ExportUtil.tabbed(year, season) : cov;
            MatrixBO covM = flData.getData().getValueAsMatrix(cov);
            // ROW: For each gear
            for (String covGearKey : covM.getSortedKeys()) {
                MatrixBO covGear = covM.getValueAsMatrix(covGearKey);
                // ROW: For each spatial
                for (String covSpatialKey : covGear.getSortedKeys()) {
                    Double w = covGear.getValueAsDouble(covSpatialKey);
                    ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(temporalCov, covGearKey, covSpatialKey, 
                            Conversion.formatDoubletoDecimalString(w, 3))));
                }
            }
        }
    }
}
