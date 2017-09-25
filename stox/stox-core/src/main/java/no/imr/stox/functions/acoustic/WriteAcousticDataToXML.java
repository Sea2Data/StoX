/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.acoustic;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.Functions;

/**
 *
 * @author aasmunds
 */
public class WriteAcousticDataToXML extends AbstractFunction {

    @Override
    public Object perform(Map<String, Object> input) {
        List<DistanceBO> distances = (List<DistanceBO>) input.get(Functions.PM_WRITEACOUSTICDATATOXML_ACOUSTICDATA);
        String cruise = getCruiseFromNASC(distances);
        if (cruise == null) {
            return null;
        }
        String fileName = (String) input.get(Functions.PM_WRITEACOUSTICDATATOXML_FILENAME);
        fileName = (String) input.get(Functions.PM_PROJECTFOLDER) + "/" + fileName; // Make relative to project folder
        ListUser20Writer.export(cruise, null, null, fileName, distances, true);
        return null;
    }

    /**
     * Extract cruise from NASC dataset
     *
     * @param nascMatrix
     * @return
     */
    private String getCruiseFromNASC(List<DistanceBO> distances) {
        Set<String> cruises = new HashSet<>();
        for (DistanceBO bo : distances) {
            cruises.add(bo.getCruise());
        }
        return cruises.size() == 1 ? cruises.iterator().next() : null;
    }
}
