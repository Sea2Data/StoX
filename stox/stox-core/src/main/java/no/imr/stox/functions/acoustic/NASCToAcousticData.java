/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.acoustic;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.echosounderbo.FrequencyBO;
import no.imr.sea2data.echosounderbo.SABO;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.bo.NASCMatrix;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.Functions;

/**
 *
 * @author aasmunds
 */
public class NASCToAcousticData extends AbstractFunction {

    @Override
    public Object perform(Map<String, Object> input) {
        List<DistanceBO> allDistances = (List<DistanceBO>) input.get(Functions.PM_NASCTOACOUSTICDATA_ACOUSTICDATA);
        NASCMatrix nascMatrix = (NASCMatrix) input.get(Functions.PM_NASCTOACOUSTICDATA_NASC);
        MatrixBO nascData = nascMatrix.getData();
        List<DistanceBO> distances = new ArrayList<>();//FilterUtils.copyBOList((List)allDistances, null);
        for (DistanceBO ds : allDistances) {
            DistanceBO dsF = new DistanceBO(ds);
            distances.add(dsF);
            if (ds.getFrequencies().size() != 1) {
                // According to Espen, only one freq (38000) should be available in the filtered frequencies.
                continue;
            }
            FrequencyBO fr = ds.getFrequencies().get(0);
            FrequencyBO frF = new FrequencyBO(dsF, fr);
            dsF.getFrequencies().add(frF);
            for (String acoCat : nascData.getKeys()) {
                for (String edsu : nascData.getGroupRowKeys(acoCat)) {
                    if (!edsu.equals(ds.getKey())) {
                        continue;
                    }
                    for (String channel : nascData.getSortedGroupRowColKeys(acoCat, edsu)) {
                        Double nasc = nascData.getGroupRowColValueAsDouble(acoCat, edsu, channel);
                        Integer ch = Conversion.safeStringtoIntegerNULL(channel);
                        if (ch == null) {
                            continue;
                        }
                        if (nasc != null) {
                            SABO sabo = new SABO();
                            frF.getSa().add(sabo);
                            sabo.setFrequency(frF);
                            // According to Espen, this is O.K Bottom zone is not used.
                            sabo.setAcoustic_category(acoCat);
                            sabo.setCh_type("P"); 
                            sabo.setCh(ch);
                            sabo.setSa(nasc);
                        }
                    }
                }
            }
        }
        return distances;
    }

}
