/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.density;

import java.util.Map;
import no.imr.stox.bo.BioticData;
import no.imr.stox.bo.DensityMatrix;
import no.imr.stox.bo.SampleUnitBO;
import no.imr.stox.bo.StationSweptAreaDensityBO;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.Functions;

/**
 *
 * @author aasmunds
 */
public class StationSweptAreaDensity extends AbstractFunction {

    @Override
    public Object perform(Map<String, Object> input) {
        SampleUnitBO sampleUnit = (SampleUnitBO) input.get(Functions.PM_STATIONSWEPTAREADENSITY_SAMPLEUNIT);
        BioticData bioticData = (BioticData) input.get(Functions.PM_STATIONSWEPTAREADENSITY_BIOTICDATA);
        DensityMatrix density = (DensityMatrix) input.get(Functions.PM_STATIONSWEPTAREADENSITY_DENSITY);
        return new StationSweptAreaDensityBO(sampleUnit, bioticData, density);
    }
}
