package no.imr.stox.datastorage;

import java.io.Writer;
import java.util.List;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.stox.bo.IndividualDataMatrix;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.sea2data.imrbase.util.ExportUtil;
import no.imr.sea2data.imrbase.util.ImrIO;
import no.imr.stox.functions.utils.Functions;

/**
 * TODO: what does this class do?
 *
 * @author aasmunds
 */
public class IndividualDataStorage extends FileDataStorage {

    @Override
    public <T> void asTable(T data, Integer level, Writer wr, Boolean withUnits) {
        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed("Stratum", "EstLayer", "LenGrp", ExportUtil.tabbed(Functions.INDIVIDUALS))));
        IndividualDataMatrix indData = (IndividualDataMatrix) data;
        for (String specCatKey : indData.getData().getSortedKeys()) {
            MatrixBO specCat = indData.getData().getValueAsMatrix(specCatKey);
            // COL: For each distance in assignment matrix (or NASC matrix).
            for (String stratumKey : specCat.getSortedKeys()) {
                // ROW: For each channel in NASC matrix
                MatrixBO stratum = specCat.getValueAsMatrix(stratumKey);
                for (String estLayerKey : stratum.getSortedKeys()) {
                    MatrixBO estLayer = stratum.getValueAsMatrix(estLayerKey);
                    for (String lenGrpKey : estLayer.getSortedKeys()) {
                        String context = ExportUtil.tabbed(stratumKey, estLayerKey, lenGrpKey);
                        List<IndividualBO> indList = (List<IndividualBO>) estLayer.getValue(lenGrpKey);
                        if (indList == null) {
                            ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(context));
                        } else {
                            BioticDataStorage.asTable(context, indList, wr);
                        }
                    }
                }
            }
        }
    }
}
