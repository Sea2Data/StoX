/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package no.imr.stox.exception;

/**
 *
 * @author Åsmund
 */
public class UserErrorException extends RuntimeException {
        public UserErrorException(Throwable e) {
        super(e);
    }
    public UserErrorException(final String message) {
        super(message);
    }
}
