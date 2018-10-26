package no.imr.stox.functions.individualdata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import no.imr.stox.functions.AbstractFunction;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.imrbase.math.ImrMath;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.stox.bo.AbundanceMatrix;
import no.imr.stox.bo.IndividualDataMatrix;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.functions.utils.Functions;
import no.imr.sea2data.imrbase.math.LWRelationship;
import no.imr.stox.functions.utils.StoXMath;

/**
 * All individual samples from the survey in the given length group and
 * estimation layer is applied if a length group in a stratum and estimation
 * layer lacks individual samples.
 *
 * @author aasmunds
 */
public class CorrectForInnsufficientSampling extends AbstractFunction {

    @Override
    public Object perform(Map<String, Object> input) {
        // result = Matrix[GROUP~Species / ROW~Stratum / COL~EstLayer / CELL~LengthGroup / VAR~IndividualList]
        // indData = DataType=Matrix[GROUP~Species / ROW~Stratum / COL~EstLayer / CELL~LengthGroup / VAR~Individuals]
        IndividualDataMatrix indData = (IndividualDataMatrix) input.get(Functions.PM_CORRECTFORINNSUFFICIENTSAMPLING_INDIVIDUALDATA);
        IndividualDataMatrix result = new IndividualDataMatrix();
        result.setResolutionMatrix(indData.getResolutionMatrix().copy());
        // abundance = DataType=Matrix[GROUP~Species / ROW~SampleUnit / COL~EstLayer / CELL~LengthGroup / VAR~Abundance]
        AbundanceMatrix abundance = (AbundanceMatrix) input.get(Functions.PM_CORRECTFORINNSUFFICIENTSAMPLING_ABUNDANCE);
        // Maybe check thath sampleunit is stratum here.
        Double lenInterval = abundance.getResolutionMatrix().getRowValueAsDouble(Functions.RES_LENGTHINTERVAL);
        for (String species : abundance.getData().getKeys()) {
            MatrixBO group = (MatrixBO) abundance.getData().getValue(species);
            for (String stratum : group.getKeys()) {
                MatrixBO row = (MatrixBO) group.getValue(stratum);
                for (String estLayer : row.getKeys()) {
                    MatrixBO cell = (MatrixBO) row.getValue(estLayer);
                    for (String lenGrp : cell.getKeys()) {
                        List<IndividualBO> indList = (List<IndividualBO>) indData.getData().getGroupRowColCellValue(species, stratum, estLayer, lenGrp);
                        if (indList == null) {
                            indList = new ArrayList<>();
                        }
                        // Use individuals from the other strata for same species, estlayer and lenGrp
                        // Issue: Using from other estLayers requires an extra estloop here as well. Not sure abt. this.

                        if (indList.isEmpty()) {
                            for (String str : group.getKeys()) {
                                List<IndividualBO> indL = (List<IndividualBO>) indData.getData().getGroupRowColCellValue(species, str, estLayer, lenGrp);
                                if (indL != null) {
                                    indList.addAll(indL);
                                }
                            }
                        }
                        // If no other strata contains individual, estimate it by le-we regression:
                        if (indList.isEmpty()) {
                            IndividualBO i = new IndividualBO();
                            Double length = StoXMath.getLength(Conversion.safeStringtoDoubleNULL(lenGrp), lenInterval);
                            i.setLength(ImrMath.safeMult(0.01, length));
                            // We need to estimate the biomass for this representation with linear regression from all length groups within estimation layer:
                            i.setIndividualweight(ImrMath.safeMult(0.001, getEstimatedWeight(indData.getData(), species, estLayer, length)));
                            indList.add(i);
                        }
                        result.getData().setGroupRowColCellValue(species, stratum, estLayer, lenGrp, indList);
                    }
                }
            }
        }
        return result;
    }

    /**
     * Estimate
     *
     * @param indData
     * @param abndByLength
     * @param species
     * @param estLayer
     * @param length
     * @return estimated weight by L/W relationship
     */
    private Double getEstimatedWeight(MatrixBO indData, String species, String estLayer, Double length) {
        List<IndividualBO> indList = new ArrayList<>();
        MatrixBO group = (MatrixBO) indData.getValue(species);
        for (String stratum : group.getKeys()) {
            MatrixBO row = (MatrixBO) group.getValue(stratum);
            MatrixBO cell = (MatrixBO) row.getValue(estLayer);
            for (String lenGrp : cell.getKeys()) {
                List<IndividualBO> indL = (List<IndividualBO>) indData.getGroupRowColCellValue(species, stratum, estLayer, lenGrp);
                if (indL != null) {
                    indList.addAll(indL);
                }
            }
        }
        if (indList.size() > 2) {
            Double[] lenInCM = new Double[indList.size()];
            Double[] wInGrams = new Double[indList.size()];
            for (int i = 0; i < indList.size(); i++) {
                IndividualBO bo = indList.get(i);
                lenInCM[i] = bo.getLengthCM();
                wInGrams[i] = bo.getIndividualweightG();
            }
            LWRelationship lwr = LWRelationship.getLWRelationship(lenInCM, wInGrams);
            return lwr.getWeight(length);
        }
        return null;

    }
}
