/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.datastorage;

import java.io.Writer;
import no.imr.sea2data.imrbase.math.Calc;
import no.imr.sea2data.imrbase.matrix.IMetaMatrix;
import no.imr.sea2data.imrbase.util.ExportUtil;
import no.imr.sea2data.imrbase.util.ImrIO;
import no.imr.stox.bo.LengthDistMatrix;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;

/**
 *
 * @author aasmunds
 */
public class SampleUnitStorage extends FileDataStorage {

    @Override
    public <T> void asTable(T data, Integer level, Writer wr, Boolean withUnits) {
        asTable(wr);
    }

    public void asTable(Writer wr) {
        ProcessDataBO pd = (ProcessDataBO) super.getProcess().getActualValue(Functions.PM_SAMPLEUNIT_PROCESSDATA);
        String rowHdr = Functions.DIM_STRATUM;
        String colHdr = Functions.DIM_PSU;
        String varHdr = Functions.DIM_EDSU;
        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(rowHdr, colHdr, varHdr)));
        AbndEstProcessDataUtil.getStrata(pd).forEach((stratum) -> {
            AbndEstProcessDataUtil.getPSUsByStratum(pd, stratum).forEach((psu) -> {
                AbndEstProcessDataUtil.getEDSUsByPSU(pd, psu).forEach((edsu) -> {
                    ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(stratum, psu, edsu)));
                });
            });
        });
    }
}
