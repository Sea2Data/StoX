/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.datastorage;

import java.io.Writer;
import java.util.List;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.imrbase.util.ExportUtil;
import no.imr.sea2data.imrbase.util.ImrIO;
import no.imr.stox.bo.BioticCovDataMatrix;
import no.imr.stox.functions.utils.Functions;

/**
 *
 * @author aasmunds
 */
public class BioticCovDataStorage extends FileDataStorage {

    @Override
    public <T> void asTable(T data, Integer level, Writer wr, Boolean withUnits) {
        BioticCovDataMatrix indData = (BioticCovDataMatrix) data;
        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed("Temporal", "GearFactor", "Spatial", "PlatformFactor", ExportUtil.tabbed(Functions.INDIVIDUALS_FULL))));
        // GROUP: For each temporal 
        for (String cov : indData.getData().getSortedRowKeys()) {
            String[] keys = cov.split("/");
            String context = ExportUtil.tabbed(keys[0], keys[1], keys[2], keys[3]);
            List<IndividualBO> indList = (List<IndividualBO>) indData.getData().getRowValue(cov);
            if (indList == null) {
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(context));
            } else {
                BioticDataStorage.asTable(Functions.INDIVIDUALS_FULL, context, indList, wr);
            }
        }
    }
}
