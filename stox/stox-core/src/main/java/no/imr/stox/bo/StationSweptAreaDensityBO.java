/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.bo;

import java.util.Arrays;
import java.util.List;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;

/**
 *
 * @author aasmunds
 */
public class StationSweptAreaDensityBO  {
    SampleUnitBO sampleUnit;
    BioticData bioticData;
    DensityMatrix density;

    public StationSweptAreaDensityBO(SampleUnitBO sampleUnit, BioticData bioticData, DensityMatrix density) {
        this.sampleUnit = sampleUnit;
        this.bioticData = bioticData;
        this.density = density;
    }

    public SampleUnitBO getSampleUnit() {
        return sampleUnit;
    }

    public BioticData getBioticData() {
        return bioticData;
    }

    public DensityMatrix getDensity() {
        return density;
    }
    
  
}
