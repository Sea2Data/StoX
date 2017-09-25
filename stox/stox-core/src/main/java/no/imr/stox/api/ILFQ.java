/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.api;

import java.util.Map;

/**
 * TODO: hva er dette?
 *
 * @author Åsmund
 */
public interface ILFQ {

    String getLengthDistType();

    Double getLengthInterval();

    Map<String, Double> getLFQ();
}
