/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.api;

import java.util.List;

/**
 * Interface to listen on changes in process data.
 *
 * @author aasmunds
 */
public interface IProcessDataListener {

    default void onDistanceTagsChanged(List<String> distances, Boolean on) {
    }
    default void onPSUViewRequest(String psu) {
    }
}
