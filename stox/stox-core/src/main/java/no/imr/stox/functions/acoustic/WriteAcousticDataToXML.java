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
import java.util.stream.Collectors;
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
        Map<String, List<DistanceBO>> cruises = distances.parallelStream()
                .collect(Collectors.groupingBy(DistanceBO::getCruise));
        cruises.entrySet().parallelStream()
                .forEach(e -> {
                    String fileName = e.getKey() + "_" + (String) input.get(Functions.PM_WRITEACOUSTICDATATOXML_FILENAME);
                    fileName = (String) input.get(Functions.PM_PROJECTFOLDER) + "/" + fileName; // Make relative to project folder
                    ListUser20Writer.export(e.getKey(), null, null, fileName, e.getValue(), true);
                });
        return null;
    }
}
