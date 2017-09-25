/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.api;

/**
 *
 * @author aasmunds
 */
public interface ICommand {

    void execute();

    boolean isEnabled();
}
