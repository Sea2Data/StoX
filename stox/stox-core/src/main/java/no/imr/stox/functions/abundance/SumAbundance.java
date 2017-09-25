package no.imr.stox.functions.abundance;

import java.util.Collection;
import java.util.Map;
import no.imr.stox.bo.AbundanceMatrix;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.EchosounderUtils;
import no.imr.stox.functions.utils.Functions;

/**
 * TODO: what does this class do?
 *
 * @author aasmunds
 */
public class SumAbundance extends AbstractFunction {

    /**
     * @param input contains matrix DENSITY_MATRIX
     * @return Matrix object of type AGGREGATEVERTICAL_MATRIX - see
     * DataTypeDescription.txt
     */
    @Override
    public Object perform(Map<String, Object> input) {
        // Set how to translate the SampleUnit in the result, i.e labels in csv outpur
        //result.getMetaMatrix().getHeaders().put(result.getMetaMatrix().getDimensionByName(Functions.DIM_SAMPLEUNIT), Functions.DIM_STRATUM);
        // Acoustic data used in channel to depth calculation (formula with upper int.dep. and pel.thickness)
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_SUMABUNDANCE_PROCESSDATA);
        // abundance= Matrix[GROUP~Species / ROW~SampleUnit / COL~EstLayer / CELL~LengthGroup / VAR~Abundance]
        AbundanceMatrix abundance = (AbundanceMatrix) input.get(Functions.PM_SUMABUNDANCE_ABUNDANCE);

        AbundanceMatrix result = new AbundanceMatrix();
        // Define estimation layer from densities
        result.setEstLayerDefMatrix(abundance.getEstLayerDefMatrix());

        // Define resolution:
        result.getResolutionMatrix().setRowValue(Functions.RES_SAMPLEUNITTYPE, Functions.SAMPLEUNIT_STRATUM);
        result.getResolutionMatrix().setRowValue(Functions.RES_LAYERTYPE, abundance.getResolutionMatrix().getRowValue(Functions.RES_LAYERTYPE));
        result.getResolutionMatrix().setRowValue(Functions.RES_LENGTHINTERVAL, abundance.getResolutionMatrix().getRowValue(Functions.RES_LENGTHINTERVAL));

        // For rectangle PSU, the area must be summed as well.
        // target sample units is a list of target keys to aggregate sources into.
        // For each target sample unit
        for (String strataSU : AbndEstProcessDataUtil.getStrata(pd)) {
            Collection<String> PSUs = EchosounderUtils.getSourceSUs(pd, strataSU, false);
            // For each source sample unit
            for (String psu : PSUs) {
                // Sum up the psu area into strata area (should be about the same as calculated stata area)
                result.getAreaMatrix().addDoubleValue(psu, abundance.getAreaMatrix().getValueAsDouble(psu));
                // For each species
                for (String species : abundance.getData().getKeys()) {
                    // For each channel layer
                    for (String estLayer : abundance.getData().getGroupColKeys(species)) {
                        // Handle source data
                        MatrixBO abndCell = (MatrixBO) abundance.getData().getGroupRowColValue(species, psu, estLayer);
                        if (abndCell == null) {
                            continue;
                        }
                        for (String lenGrp : abndCell.getKeys()) {
                            Double abnd = abndCell.getValueAsDouble(lenGrp);
                            result.getData().addGroupRowColCellValue(species, strataSU, estLayer, lenGrp, abnd);
                        }
                    }
                }
            }
        }
        return result;
    }

}
