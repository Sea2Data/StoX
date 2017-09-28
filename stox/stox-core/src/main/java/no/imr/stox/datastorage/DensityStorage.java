/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.datastorage;

import java.io.Writer;
import java.math.BigDecimal;
import no.imr.sea2data.imrbase.math.Calc;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.stox.bo.VariableWithEstimationLayer;
import no.imr.sea2data.imrbase.util.ExportUtil;
import no.imr.stox.functions.utils.Functions;
import no.imr.sea2data.imrbase.matrix.IMetaMatrix;
import no.imr.sea2data.imrbase.util.ImrIO;

/**
 *
 * @author aasmunds
 */
public class DensityStorage extends FileDataStorage {

    @Override
    public <T> void asTable(T data, Integer level, Writer wr, Boolean withUnits) {
        asTable((VariableWithEstimationLayer) data, level, wr);
    }

    public void asTable(VariableWithEstimationLayer mtrx, Integer level, Writer wr) {
        //Matrix[GROUP~Species / ROW~SampleUnit / COL~Layer / CELL~LengthGroup / VAR~Density]
        String groupHdr = mtrx.getData().getMetaMatrix().getHeader(IMetaMatrix.GROUP);
        String rowHdr = mtrx.getSUHdr();
        String colHdr = Functions.DIM_LAYER;
        String col2Hdr = Functions.DIM_ESTLAYER;
        String cellHdr = mtrx.getData().getMetaMatrix().getHeader(IMetaMatrix.CELL);
        String var = mtrx.getData().getMetaMatrix().getVariable();
        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(groupHdr,
                Functions.RES_SAMPLEUNITTYPE, rowHdr, "SampleSize", "PosSampleSize", "Distance",
                Functions.RES_LAYERTYPE, colHdr, col2Hdr, "EstLayerDef",
                cellHdr, Functions.RES_LENGTHINTERVAL,
                var)));
        String sampleUnitType = (String) mtrx.getResolutionMatrix().getRowValue(Functions.RES_SAMPLEUNITTYPE);
        String layerType = (String) mtrx.getResolutionMatrix().getRowValue(Functions.RES_LAYERTYPE);
        Boolean isEstLayer = layerType == null;
        Double lengthInterval = mtrx.getResolutionMatrix().getRowValueAsDouble(Functions.RES_LENGTHINTERVAL);
        for (String species : mtrx.getData().getSortedKeys()) {
            for (String su : mtrx.getData().getSortedGroupRowKeys(species)) {
                Integer sampleSize = mtrx.getSampleSizeMatrix().getRowValueAsInteger(su);
                Double distance = Calc.roundTo(mtrx.getDistanceMatrix().getRowValueAsDouble(su), 3);
                for (String layerKey : mtrx.getData().getSortedGroupRowColKeys(species, su)) {
                    String layer = isEstLayer ? null : layerKey;
                    String estLayer = isEstLayer ? layerKey : null;
                    String estLayerDef = mtrx.getEstLayerDefMatrix() != null ? (String) mtrx.getEstLayerDefMatrix().getRowValue(estLayer) : null;
                    Integer posSampleSize = mtrx.getPosSampleSizeMatrix().getGroupRowColValueAsInteger(species, su, layerKey);
                    for (String lGrp : mtrx.getData().getSortedGroupRowColCellKeys(species, su, layerKey)) {
                        Double value = mtrx.getData().getGroupRowColCellValueAsDouble(species, su, layerKey, lGrp);
                        //value = Calc.roundTo(value, tz + 3);
                        String s = ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(species,
                                sampleUnitType, su, sampleSize, posSampleSize, Conversion.formatDoubletoDecimalString(distance, 3),
                                layerType, layer, estLayer, estLayerDef,
                                lGrp, lGrp == null ? null : lengthInterval,
                                Conversion.formatDoubletoDecimalStringWithTrailingZeros(value, 3)));
                        ImrIO.write(wr, s);
                    }
                }
            }
        }
    }
}
