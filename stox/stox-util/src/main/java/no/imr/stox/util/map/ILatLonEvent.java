/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.util.map;

/**
 * Interface to position with latitude longitude (WGS84)
 * @author aasmunds
 */
public interface ILatLonEvent {

    String getKey();
    
    Double getStartLat();

    Double getStartLon();

    Double getStopLat();

    Double getStopLon();
}
