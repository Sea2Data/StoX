/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.datastorage;

import java.io.Writer;
import no.imr.sea2data.imrbase.math.Calc;
import no.imr.sea2data.imrbase.matrix.IMetaMatrix;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.sea2data.imrbase.util.ExportUtil;
import no.imr.sea2data.imrbase.util.ImrIO;
import no.imr.stox.bo.LengthDistMatrix;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.bo.SampleUnitBO;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;

/**
 *
 * @author aasmunds
 */
public class SampleUnitStorage extends FileDataStorage {

    @Override
    public <T> void asTable(T data, Integer level, Writer wr, Boolean withUnits) {
        asTable((SampleUnitBO) data, wr);
    }

    public void asTable(SampleUnitBO data, Writer wr) {
        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(Functions.DIM_EDSU, Functions.DIM_PSU, Functions.DIM_STRATUM)));
        data.getEDSUPSU().getSortedRowKeys().forEach((edsu) -> {
            String psu = (String) data.getEDSUPSU().getRowValue(edsu);
            String stratum = (String) data.getPSUStratum().getRowValue(psu);
            if (psu != null && stratum != null) {
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(edsu, psu, stratum)));
            }
        });
    }
}
