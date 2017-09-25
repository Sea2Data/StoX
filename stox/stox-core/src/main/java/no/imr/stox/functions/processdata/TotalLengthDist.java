package no.imr.stox.functions.processdata;

import java.util.Map;
import no.imr.stox.bo.LengthDistMatrix;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil; 
import no.imr.stox.functions.utils.StoXMath;

/**
 * Implements total length distribution by percent.
 *
 * @author aasmunds
 */
public class TotalLengthDist extends AbstractFunction {

    /**
     * @param input contains matrix STATIONLENGTHDIST, PROCESSDATA, and logger
     * @return Matrix object of type TOTALLENGTHDIST_MATRIX - see
     * DataTypeDescription.txt
     */
    @Override
    public Object perform(Map<String, Object> input) {
        LengthDistMatrix lengthDistMatrix = (LengthDistMatrix) input.get(Functions.PM_TOTALLENGTHDIST_LENGTHDIST);
        MatrixBO lengthDist = lengthDistMatrix.getData();
        Double lenInterval = lengthDistMatrix.getResolutionMatrix().getRowValueAsDouble(Functions.RES_LENGTHINTERVAL);

        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_BIOSTATIONASSIGNMENT_PROCESSDATA);
        MatrixBO trawlAsg = AbndEstProcessDataUtil.getBioticAssignments(pd);

        LengthDistMatrix result = new LengthDistMatrix();
        result.getResolutionMatrix().setRowValue(Functions.RES_OBSERVATIONTYPE, Functions.OBSERVATIONTYPE_ASSIGNMENT);
        result.getResolutionMatrix().setRowValue(Functions.RES_LENGTHINTERVAL, lenInterval);
        String lenDistType = (String) lengthDistMatrix.getResolutionMatrix().getRowValue(Functions.RES_LENGTHDISTTYPE);
        result.getResolutionMatrix().setRowValue(Functions.RES_LENGTHDISTTYPE, lenDistType);
        
//        String aggregation = (String) input.get(Functions.PM_TOTALLENGTHDIST_AGGREGATION);
        Boolean asAverage = lenDistType != null && lenDistType.equals(Functions.LENGTHDISTTYPE_PERCENTLENGHTDIST);//aggregation != null ? aggregation.equals(Functions.AGGREGATION_AVERAGE) : true;
        for (String specCat : lengthDist.getKeys()) {
            // For all assignments
            //System.out.println("Sp\tAs\tSt\tLG\tLD\tWC\tRW\tW\tSW");
            for (String assignment : trawlAsg.getRowKeys()) {
                double sumWeights = 0d;
                MatrixBO assignRow = (MatrixBO) trawlAsg.getRowValue(assignment);
                if (asAverage) {
                    // For all stations .. sum up the assignment weights
                    for (String station : assignRow.getKeys()) {
                        if (lengthDist.getGroupRowValue(specCat, station) != null) {
                            Double val = assignRow.getValueAsDouble(station);
                            if(val == null) {
                                continue;
                            }
                            sumWeights += val;
                        }
                    }
                    if (sumWeights == 0d) {
                        continue;
                    }
                }
                // Again, For all stations .. calculate the relative assignment weight
                for (String station : assignRow.getKeys()) {
                    Double weight = assignRow.getValueAsDouble(station);
                    if(weight == null || weight == 0) {
                        continue;
                    }
                    Double relweight = asAverage ? StoXMath.relativeToTotal(weight, sumWeights) : weight;
                    MatrixBO stationRow = (MatrixBO) lengthDist.getGroupRowValue(specCat, station);
                    if (stationRow == null) {
                        continue;
                    }
                    // Length distribution variable
                    MatrixBO cell = stationRow.getDefaultValueAsMatrix();
                    // For all Length groups.. combine relative assignment weight with length distribution
                    for (String lengthGroup : cell.getKeys()) {
                        Double weightedCount = cell.getValueAsDouble(lengthGroup);
                        weightedCount = StoXMath.combineWFac(weightedCount, relweight);
                        result.getData().addGroupRowCellValue(specCat, assignment, lengthGroup, weightedCount);
                        /*if(assignment.equals("VSW_T2")) {
                         System.out.println(species + "\t" + assignment + "\t" + station + "\t" + lengthGroup + "\t" + cell.getValueAsDouble(lengthGroup) + "\t" + 
                         weightedCount + "\t" + relweight + "\t" + weight + "\t" + sumWeights);
                         }*/
                    }
                }
            }
        }
        return result;
    }
}
