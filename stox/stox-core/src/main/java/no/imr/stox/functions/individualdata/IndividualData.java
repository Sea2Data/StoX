package no.imr.stox.functions.individualdata;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import no.imr.stox.functions.AbstractFunction;
import no.imr.sea2data.biotic.bo.CatchBO;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO; 
import no.imr.sea2data.biotic.bo.SampleBO;
import no.imr.stox.bo.IndividualDataMatrix;
import no.imr.stox.bo.IndividualDataStationsMatrix;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.functions.utils.BioticUtils;
import no.imr.stox.functions.utils.Functions;

/**
 * This function class is used to link individual data spatially, get a list of
 * individuals linked to estlayer and stratum
 *
 * @author aasmunds
 */
public class IndividualData extends AbstractFunction {

    /**
     * @param input contains matrix INDIVIDUALDATASTATIONS_MATRIX
     * @return Matrix object of type INDIVIDUALDATA_MATRIX - see
     * DataTypeDescription.txt
     * @throws no.imr.stox.exception.UserErrorException
     */
    @Override
    public Object perform(Map<String, Object> input) {
        // result = Matrix[GROUP~Species / ROW~Stratum / COL~EstLayer / CELL~LengthGroup / VAR~IndividualList]
        // Fish stations
        List<FishstationBO> fishStations = (List<FishstationBO>) input.get(Functions.PM_INDIVIDUALDATA_BIOTICDATA);
        // indDataSel = Matrix[ROW~Stratum / COL~EstLayer / CELL~Station / VAR~Included]
        IndividualDataStationsMatrix indDataSel = (IndividualDataStationsMatrix) input.get(Functions.PM_INDIVIDUALDATA_INDIVIDUALDATASTATIONS);
        IndividualDataMatrix result = new IndividualDataMatrix();
        // Set the resolution:
        result.getResolutionMatrix().setRowValue(Functions.RES_SAMPLEUNITTYPE, 
                indDataSel.getResolutionMatrix().getRowValue(Functions.RES_SAMPLEUNITTYPE));
/*        result.getResolutionMatrix().setRowValue(Functions.RES_LAYERTYPE, 
                indDataSel.getResolutionMatrix().getRowValue(Functions.RES_LAYERTYPE));
        */
        // Inherit the length interval resolution from abundance 
        Double lenInterval = (Double) indDataSel.getResolutionMatrix().getRowValue(Functions.RES_LENGTHINTERVAL);
        result.getResolutionMatrix().setRowValue(Functions.RES_LENGTHINTERVAL, lenInterval);
        

        for (String stratumKey : indDataSel.getData().getRowKeys()) {
            MatrixBO stratum = (MatrixBO) indDataSel.getData().getRowValue(stratumKey);
            for (String estLayerKey : stratum.getKeys()) {
                MatrixBO estLayer = (MatrixBO) stratum.getValue(estLayerKey);
                List<String> stations = estLayer.getKeys();
                for (FishstationBO fs : fishStations) {
                    if (!stations.contains(fs.getKey())) {
                        continue;
                    }
                    for (CatchBO c : fs.getCatchBOs()) {
                        String specCatKey = c.getSpeciesCatTableKey();
                        // To do: check species against SpeciesDef in resolution if available. Otherwise this relies on filterbiotic.
                        for (SampleBO s : c.getSampleBOs()) {
                            for (IndividualBO i : s.getIndividualBOs()) {
                                /*if (i.getWeight() == null) {
                                    continue;
                                }*/
                                String lenGrpKey = BioticUtils.getLenGrp(i.getLength(), lenInterval);
                                List<IndividualBO> indList = (List<IndividualBO>) result.getData().getGroupRowColCellValue(specCatKey, stratumKey, estLayerKey, lenGrpKey);
                                if (indList == null) {
                                    indList = new ArrayList<>();
                                    result.getData().setGroupRowColCellValue(specCatKey, stratumKey, estLayerKey, lenGrpKey, indList);
                                }
                                indList.add(i);
                                if(i.getSample() == null) {
                                    System.out.println("Error in individual " + i);
                                }
                                
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
}
