/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.library;

/**
 * A meta reflection object is a meta object that can be instantiated by
 * reflection
 *
 * @author aasmunds
 */
public interface IMetaReflObject extends IMetaObject {

    /**
     *
     * @return class to construct the object
     */
    String getClazz();

    /**
     * set class to construct the object
     *
     * @param clazz
     */
    void setClazz(String clazz);
}
