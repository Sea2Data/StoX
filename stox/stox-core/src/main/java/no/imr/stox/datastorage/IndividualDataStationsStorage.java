package no.imr.stox.datastorage;

import java.io.Writer;
import no.imr.sea2data.imrbase.matrix.IMetaMatrix;
import no.imr.sea2data.imrbase.util.ExportUtil;
import no.imr.sea2data.imrbase.util.ImrIO;
import no.imr.stox.bo.IndividualDataStationsMatrix;
import no.imr.stox.functions.utils.Functions;

/**
 * TODO: what does this class do?
 *
 * @author aasmunds
 */
public class IndividualDataStationsStorage extends FileDataStorage {

    @Override
    public <T> void asTable(T data, Integer level, Writer wr, Boolean withUnits) {
        //Matrix[GROUP~Species / ROW~SampleUnit / COL~Layer / CELL~LengthGroup / VAR~Density]
        IndividualDataStationsMatrix mtrx = (IndividualDataStationsMatrix) data;
        String rowHdr = mtrx.getData().getMetaMatrix().getHeader(IMetaMatrix.ROW);
        String colHdr = mtrx.getData().getMetaMatrix().getHeader(IMetaMatrix.COL);
        String cellHdr = mtrx.getData().getMetaMatrix().getHeader(IMetaMatrix.CELL);
        String var = mtrx.getData().getMetaMatrix().getVariable();
        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(Functions.RES_SAMPLEUNITTYPE, rowHdr,
                colHdr, "EstLayerDef",
                Functions.RES_OBSERVATIONTYPE, cellHdr,
                Functions.RES_LENGTHINTERVAL, var)));
        String sampleUnitType = (String) mtrx.getResolutionMatrix().getRowValue(Functions.RES_SAMPLEUNITTYPE);
        String obsType = (String) mtrx.getResolutionMatrix().getRowValue(Functions.RES_OBSERVATIONTYPE);
        Double lengthInterval = mtrx.getResolutionMatrix().getRowValueAsDouble(Functions.RES_LENGTHINTERVAL);
        for (String su : mtrx.getData().getSortedRowKeys()) {
            for (String layer : mtrx.getData().getSortedRowColKeys(su)) {
                String estLayerDef = (String) mtrx.getEstLayerDefMatrix().getRowValue(layer);
                for (String obs : mtrx.getData().getSortedRowColCellKeys(su, layer)) {
                    Object value = mtrx.getData().getRowColCellValue(su, layer, obs);
                    String s = ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                            sampleUnitType, su,
                            layer, estLayerDef,
                            obsType, obs, lengthInterval,
                            value));
                    ImrIO.write(wr, s);
                }
            }
        }
    }
}
