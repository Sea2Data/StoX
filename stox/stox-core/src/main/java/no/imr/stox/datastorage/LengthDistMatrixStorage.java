/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.datastorage;

import java.io.Writer;
import no.imr.sea2data.imrbase.math.Calc;
import no.imr.stox.bo.LengthDistMatrix;
import no.imr.sea2data.imrbase.util.ExportUtil;
import no.imr.stox.functions.utils.Functions;
import no.imr.sea2data.imrbase.matrix.IMetaMatrix;
import no.imr.sea2data.imrbase.util.ImrIO;

/**
 *
 * @author aasmunds
 */
public class LengthDistMatrixStorage extends FileDataStorage {

    @Override
    public <T> void asTable(T data, Integer level, Writer wr, Boolean withUnits) {
        asTable((LengthDistMatrix) data, level, wr, withUnits);
    }

    public String getObservationHdr(LengthDistMatrix ldMatrix) {
        return (String) ldMatrix.getResolutionMatrix().getRowValue(Functions.RES_OBSERVATIONTYPE);
    }

    public void asTable(LengthDistMatrix ldMatrix, Integer level, Writer wr, Boolean withUnits) {
        String groupHdr = ldMatrix.getData().getMetaMatrix().getHeader(IMetaMatrix.GROUP);
        String rowHdr = getObservationHdr(ldMatrix);
        String cellHdr = ldMatrix.getData().getMetaMatrix().getHeader(IMetaMatrix.CELL);
        String var = ldMatrix.getData().getMetaMatrix().getVariable();
        String lDistType = (String) ldMatrix.getResolutionMatrix().getRowValue(Functions.RES_LENGTHDISTTYPE);
        String unitAdd = withUnits ? " (cm)" : "";
        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(groupHdr, rowHdr, cellHdr + unitAdd, Functions.RES_LENGTHINTERVAL + unitAdd, var, "LengthDistType")));
        // Length dist: Matrix Matrix[GROUP~Species / ROW~Observation / CELL~LengthGroup / VAR~WeightedCount]
        Double lengthInterval = ldMatrix.getResolutionMatrix().getRowValueAsDouble(Functions.RES_LENGTHINTERVAL);
        int precisionLevel = getProcess().getModel().getProject().getPrecisionLevel();
        for (String species : ldMatrix.getData().getKeys()) {
            for (String observation : ldMatrix.getData().getSortedGroupRowKeys(species)) {
                for (String lengthGroup : ldMatrix.getData().getSortedGroupRowCellKeys(species, observation)) {
                    Double wCount = ldMatrix.getData().getGroupRowCellValueAsDouble(species, observation, lengthGroup);
                    wCount = precisionLevel >= 1 ? Calc.roundToWithTrailingZeros(wCount, 5) : Calc.roundTo(wCount, 5);
                    ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(species, observation, lengthGroup, lengthInterval, wCount, lDistType)));
                }
            }
        }
    }
}
