/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.individualdata;

import java.util.Map;
import no.imr.stox.bo.AbundanceMatrix;
import no.imr.stox.bo.IndividualDataStationsMatrix;
import no.imr.stox.functions.AbstractFunction;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.log.ILogger;

/**
 * This function class is used to describes from which trawl stations the
 * individual samples can be found for any give combination of estimation layer
 * and stratum.
 *
 * @author aasmunds
 */
public class IndividualDataStations extends AbstractFunction {

    /**
     * @param input contains PROCESSDATA
     * @return Matrix object of type INDIVIDUALDATASTATIONS_MATRIX - see
     * DataTypeDescription.txt
     * @throws no.imr.stox.exception.UserErrorException
     */
    @Override
    public Object perform(Map<String, Object> input) {
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);

        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_INDIVIDUALDATASTATIONS_PROCESSDATA);
        MatrixBO sampleUnitAssignment = AbndEstProcessDataUtil.getSUAssignments(pd);
        // ModelParameters with est layers definition
        // To do: get estimation layer definition from Abundance datatype which inherits this from SumDensity
        AbundanceMatrix abndMatrix = (AbundanceMatrix) input.get(Functions.PM_INDIVIDUALDATASTATIONS_ABUNDANCE);
        IndividualDataStationsMatrix result = new IndividualDataStationsMatrix();
        // Set the resolution:
        result.getResolutionMatrix().setRowValue(Functions.RES_SAMPLEUNITTYPE, Functions.SAMPLEUNIT_STRATUM);
        result.getResolutionMatrix().setRowValue(Functions.RES_OBSERVATIONTYPE, Functions.OBSERVATIONTYPE_STATION);
        // Transfer the est.layer
        result.setEstLayerDefMatrix(abndMatrix.getEstLayerDefMatrix());
        // Inherit the length interval resolution from abundance 
        result.getResolutionMatrix().setRowValue(Functions.RES_LENGTHINTERVAL,
                abndMatrix.getResolutionMatrix().getRowValue(Functions.RES_LENGTHINTERVAL));
        for (String su : sampleUnitAssignment.getRowKeys()) {
            // Achieve a psu from sampleunit
            String psu = AbndEstProcessDataUtil.getPSUBySampleUnit(pd, su);
            String stratum = AbndEstProcessDataUtil.getPSUStratum(pd, psu);
            MatrixBO row = (MatrixBO) sampleUnitAssignment.getRowValue(su);
            for (String estLayer : row.getKeys()) {
                String assignment = (String) row.getValue(estLayer);
                MatrixBO stations = (MatrixBO) AbndEstProcessDataUtil.getBioticAssignments(pd).getRowValue(assignment);
                if (stations == null) {
                    continue;
                }
                for (String stationKey : stations.getKeys()) {
                    Double stationWeight = stations.getValueAsDouble(stationKey);
                    if (stationWeight == null || stationWeight == 0d) {
                        continue; // Filter 0 weighted assignments
                    }
                    result.getData().setRowColCellValue(stratum, estLayer, stationKey, true);
                }
            }
        }

        return result;
    }
}
