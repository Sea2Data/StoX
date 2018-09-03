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
        String temporalHdr = /*seasonal ? ExportUtil.tabbed("Year", "Season") : */ ExportUtil.tabbed("Temporal");
        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(temporalHdr, "GearFactor", "Spatial", "Weight")));
        LandingWeightCovDataMatrix flData = (LandingWeightCovDataMatrix) data;
        // GROUP: For each temporal 
        for (String cov : flData.getData().getSortedRowKeys()) {
            String[] keys = cov.split("/");
            String context = ExportUtil.tabbed(keys[0], keys[1], keys[2]/*, keys[3]*/);
            Double w = flData.getData().getRowValueAsDouble(cov);
            ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(context,
                    Conversion.formatDoubletoDecimalString(w, 3))));
        }
    }
}
