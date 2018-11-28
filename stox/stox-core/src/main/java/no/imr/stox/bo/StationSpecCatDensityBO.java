/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.bo;

/**
 *
 * @author aasmunds
 */
public class StationSpecCatDensityBO  {
    ProcessDataBO processData;
    BioticData bioticData;
    DensityMatrix density;

    public StationSpecCatDensityBO(ProcessDataBO processData, BioticData bioticData, DensityMatrix density) {
        this.processData = processData;
        this.bioticData = bioticData;
        this.density = density;
    }

    public ProcessDataBO getProcessData() {
        return processData;
    }

    public BioticData getBioticData() {
        return bioticData;
    }

    public DensityMatrix getDensity() {
        return density;
    }
    
  
}
