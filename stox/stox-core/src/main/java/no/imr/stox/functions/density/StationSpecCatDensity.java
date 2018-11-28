/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.density;

import java.util.Map;
import no.imr.stox.bo.BioticData;
import no.imr.stox.bo.DensityMatrix;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.bo.StationSpecCatDensityBO;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.Functions;

/**
 *
 * @author aasmunds
 */
public class StationSpecCatDensity extends AbstractFunction {

    @Override
    public Object perform(Map<String, Object> input) {
        ProcessDataBO processData = (ProcessDataBO) input.get(Functions.PM_STATIONSPECCATDENSITY_PROCESSDATA);
        BioticData bioticData = (BioticData) input.get(Functions.PM_STATIONSPECCATDENSITY_BIOTICDATA);
        DensityMatrix density = (DensityMatrix) input.get(Functions.PM_STATIONSPECCATDENSITY_DENSITY);
        return new StationSpecCatDensityBO(processData, bioticData, density);
    }
}
