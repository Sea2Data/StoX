/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.datastorage;

import java.io.Writer;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.stox.bo.AbundanceMatrix;
import no.imr.sea2data.imrbase.util.ExportUtil;
import no.imr.stox.functions.utils.Functions;
import no.imr.sea2data.imrbase.matrix.IMetaMatrix;
import no.imr.sea2data.imrbase.util.ImrIO;

/**
 *
 * @author aasmunds
 */
public class AbundanceStorage extends FileDataStorage {

    @Override
    public <T> void asTable(T data, Integer level, Writer wr, Boolean withUnits) {
        asTable((AbundanceMatrix) data, level, wr);
    }

    public void asTable(AbundanceMatrix mtrx, Integer level, Writer wr){
        //Matrix[GROUP~Species / ROW~SampleUnit / COL~Layer / CELL~LengthGroup / VAR~Density]
        String groupHdr = mtrx.getData().getMetaMatrix().getHeader(IMetaMatrix.GROUP);
        String rowHdr = mtrx.getSUHdr();
        String colHdr = mtrx.getData().getMetaMatrix().getHeader(IMetaMatrix.COL);
        String cellHdr = mtrx.getData().getMetaMatrix().getHeader(IMetaMatrix.CELL);
        String var = mtrx.getData().getMetaMatrix().getVariable();
        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(groupHdr,
                Functions.RES_SAMPLEUNITTYPE, rowHdr, "Area",
                colHdr, "EstLayerDef",
                cellHdr, Functions.RES_LENGTHINTERVAL,
                var)));
        String sampleUnitType = (String) mtrx.getResolutionMatrix().getRowValue(Functions.RES_SAMPLEUNITTYPE);
        Double lengthInterval = mtrx.getResolutionMatrix().getRowValueAsDouble(Functions.RES_LENGTHINTERVAL);
        for (String species : mtrx.getData().getSortedKeys()) {
            for (String su : mtrx.getData().getSortedGroupRowKeys(species)) {
                Double area = mtrx.getAreaMatrix().getRowValueAsDouble(su);
                for (String estLayer : mtrx.getData().getSortedGroupRowColKeys(species, su)) {
                    String estLayerDef = mtrx.getEstLayerDefMatrix() != null ? (String) mtrx.getEstLayerDefMatrix().getRowValue(estLayer) : null;
                    for (String lGrp : mtrx.getData().getSortedGroupRowColCellKeys(species, su, estLayer)) {
                        Double value = mtrx.getData().getGroupRowColCellValueAsDouble(species, su, estLayer, lGrp);
                        String s = ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(species,
                                sampleUnitType, su,
                                Conversion.formatDoubletoDecimalString(area, 3),
                                estLayer, estLayerDef,
                                lGrp, lengthInterval,
                                Conversion.formatDoubletoDecimalString(value, 3)));
                        ImrIO.write(wr, s);
                    }
                }
            }
        }
    }
}
