/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.library;

/**
 * A meta data type is a meta object used with data types on meta functions and
 * meta parameters
 *
 * @author aasmunds
 */
public interface IMetaDataType extends IMetaObject {

    /**
     * if the data type is not primitive (string, int, ..) but a referenced
     * object
     *
     * @return
     */
    Boolean isReference();
}
