/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.datastorage;

import java.io.Writer;
import java.util.List;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrbase.util.ExportUtil;
import no.imr.sea2data.imrbase.util.ImrIO;
import no.imr.stox.bo.BioticCovDataMatrix;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.CovariateUtils;
import no.imr.stox.functions.utils.Functions;

/**
 *
 * @author aasmunds
 */
public class BioticCovDataStorage extends FileDataStorage {

    @Override
    public <T> void asTable(T data, Integer level, Writer wr, Boolean withUnits) {
        BioticCovDataMatrix indData = (BioticCovDataMatrix) data;
        Boolean seasonal = true;
        MatrixBO m = AbndEstProcessDataUtil.getTemporal(getProcess().getModel().getProject().getProcessData());
        for (String cov : m.getRowColKeys(Functions.SOURCETYPE_BIOTIC)) {
            // At this point we can extract the year from the covariate, since it is aggregated into a year.season covariate
            seasonal = CovariateUtils.isCovariateSeasonal(cov);
            break;
        }
        // fix stox-153
        String temporalHdr =  ExportUtil.tabbed("Temporal");//seasonal ? ExportUtil.tabbed("Temporal") : ExportUtil.tabbed("Temporal");
        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(temporalHdr, "GearFactor", "Spatial", ExportUtil.tabbed(Functions.INDIVIDUALS_FULL))));
        // GROUP: For each temporal 
        for (String cov : indData.getData().getSortedKeys()) {
            // At this point we can extract the year from the covariate, since it is aggregated into a year.season covariate
            //Integer year = Conversion.safeStringtoIntegerNULL(Conversion.safeSubstring(cov, 0, 4));
            //Integer season = CovariateUtils.getSeasonByCovariate(cov);
            String temporalCov = cov;//seasonal ? ExportUtil.tabbed(year, season) : cov;
            MatrixBO covM = indData.getData().getValueAsMatrix(cov);
            // ROW: For each gear
            for (String covGearKey : covM.getSortedKeys()) {
                MatrixBO covGear = covM.getValueAsMatrix(covGearKey);
                // ROW: For each spatial
                for (String covSpatialKey : covGear.getSortedKeys()) {
                    String context = ExportUtil.tabbed(temporalCov, covGearKey, covSpatialKey);
                    List<IndividualBO> indList = (List<IndividualBO>) covGear.getValue(covSpatialKey);
                    if (indList == null) {
                        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(context));
                    } else {
                        BioticDataStorage.asTable(Functions.INDIVIDUALS_FULL, context, indList, wr);
                    }
                }
            }
        }
    }
}
