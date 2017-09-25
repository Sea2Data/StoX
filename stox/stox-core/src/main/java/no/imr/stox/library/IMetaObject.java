/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.library;

import java.util.List;

/**
 * A meta object is a object with name and description
 *
 * @author aasmunds
 */
public interface IMetaObject {

    /**
     * Get the library the function belongs to
     *
     * @return
     */
    ILibrary getLibrary();

    /**
     * Get name of metqa object
     *
     * @return
     */
    String getName();

    /**
     * return description of meta object
     *
     * @return
     */
    String getDescription();

    /**
     * Set name
     *
     * @param name
     */
    void setName(String name);

    /**
     * set description
     *
     * @param description
     */
    void setDescription(String description);
    
    List<String> getAliases();
}
