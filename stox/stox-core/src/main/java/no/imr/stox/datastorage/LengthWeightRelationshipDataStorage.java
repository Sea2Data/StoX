/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.datastorage;

import java.io.Writer;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrbase.util.ExportUtil;
import no.imr.sea2data.imrbase.util.ImrIO;
import no.imr.stox.bo.SingleMatrix;
import no.imr.stox.functions.utils.Functions;

/**
 *
 * @author aasmunds
 */
public class LengthWeightRelationshipDataStorage extends FileDataStorage {

    @Override
    public <T> void asTable(T data, Integer level, Writer wr, Boolean withUnits) {
        asTable((SingleMatrix) data, level, wr);
    }

    public void asTable(SingleMatrix mtrx, Integer level, Writer wr) {
        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(Functions.COL_IND_APHIA, Functions.SAMPLEUNIT_STRATUM, Functions.LENGTHWEIGHT_COEFF_A, Functions.LENGTHWEIGHT_COEFF_B, Functions.LENGTHWEIGHT_COEFF_R2)));
        for (String aphia : mtrx.getData().getSortedKeys()) {
            for (String stratum : mtrx.getData().getSortedGroupRowKeys(aphia)) {
                Double a = mtrx.getData().getGroupRowColValueAsDouble(aphia, stratum, Functions.LENGTHWEIGHT_COEFF_A);
                Double b = mtrx.getData().getGroupRowColValueAsDouble(aphia, stratum, Functions.LENGTHWEIGHT_COEFF_B);
                Double r2 = mtrx.getData().getGroupRowColValueAsDouble(aphia, stratum, Functions.LENGTHWEIGHT_COEFF_R2);
                String s = ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(aphia, stratum,
                        Conversion.formatDoubletoDecimalString(a, 5),
                        Conversion.formatDoubletoDecimalString(b, 3),
                        Conversion.formatDoubletoDecimalString(r2, 3)));
                ImrIO.write(wr, s);
            }
        }
    }
}
