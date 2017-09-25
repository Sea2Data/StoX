/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.datastorage;

import java.io.Writer;
import no.imr.sea2data.imrbase.math.Calc;
import no.imr.stox.bo.NASCMatrix;
import no.imr.sea2data.imrbase.util.ExportUtil;
import no.imr.stox.functions.utils.Functions;
import no.imr.sea2data.imrbase.matrix.IMetaMatrix;
import no.imr.sea2data.imrbase.util.ImrIO;

/**
 *
 * @author aasmunds
 */
public class NASCMatrixStorage extends FileDataStorage {

    @Override
    public <T> void asTable(T data, Integer level, Writer wr, Boolean withUnits) {
        asTable((NASCMatrix) data, level, wr);
    }

    public String getSUHdr(NASCMatrix nascMatrix) {
        return (String) nascMatrix.getResolutionMatrix().getRowValue(Functions.RES_SAMPLEUNITTYPE);
    }

    public void asTable(NASCMatrix nascMatrix, Integer level, Writer wr) {
        String groupHdr = nascMatrix.getData().getMetaMatrix().getHeader(IMetaMatrix.GROUP);
        String colHdr = nascMatrix.getData().getMetaMatrix().getHeader(IMetaMatrix.COL);
        String var = nascMatrix.getData().getMetaMatrix().getVariable();
        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(groupHdr,
                Functions.RES_SAMPLEUNITTYPE, "SampleUnit", "SampleSize", "PosSampleSize", "Distance",
                Functions.RES_LAYERTYPE, colHdr, var)));
        String sampleUnitType = (String) nascMatrix.getResolutionMatrix().getRowValue(Functions.RES_SAMPLEUNITTYPE);
        String layerType = (String) nascMatrix.getResolutionMatrix().getRowValue(Functions.RES_LAYERTYPE);
        for (String acoCat : nascMatrix.getData().getSortedKeys()) {
            for (String dist : nascMatrix.getData().getSortedGroupRowKeys(acoCat)) {
                Integer sampleSize = nascMatrix.getSampleSizeMatrix().getRowValueAsInteger(dist);
                Integer posSampleSize = nascMatrix.getPosSampleSizeMatrix().getRowValueAsInteger(dist);
                Double distance = nascMatrix.getDistanceMatrix().getRowValueAsDouble(dist);
                for (String layer : nascMatrix.getData().getSortedColKeys(acoCat, dist)) {
                    Double nascVal = nascMatrix.getData().getGroupRowColValueAsDouble(acoCat, dist, layer);
                    if(nascVal != null) {
                        nascVal = Calc.roundTo(nascVal, 5);
                    }
                    String s = ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(acoCat, 
                            sampleUnitType, dist, sampleSize, posSampleSize, distance,
                            layerType, layer, nascVal));
                    ImrIO.write(wr, s);
                }
            }
        }
    }
}
