/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions;

import no.imr.stox.datastorage.IDataStorage;

/**
 * Base class for functions
 *
 * @author kjetilf
 */
public abstract class AbstractFunction implements IFunction {

    @Override
    public IDataStorage getDataStorage() {
        return null;
    }

}
