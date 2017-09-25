/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.library;

import java.util.List;

/**
 * Function library for meta functions, meta data types and meta data sources
 *
 * @author Åsmund
 */
public interface ILibrary {

    /**
     * setup library from a from a library resource file
     * @param modelName modelName
     * @param tags a string to tag each function, i.e with model name in order to separate in different categories.
     */
    void readFromResource(String modelName);

    /**
     *
     * @return list of meta functions
     */
    List<IMetaFunction> getMetaFunctions();

    /**
     *
     * @return list of meta data types
     */
    List<IMetaDataType> getMetaDataType();

    /**
     *
     * @param name
     * @return meta data type from name
     */
    IMetaDataType findMetaDataType(String name);

    /**
     *
     * @param name
     * @return meta function
     */
    IMetaFunction findMetaFunction(String name);

    List getMetaFunctionsByCategory(String category);

}
