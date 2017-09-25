/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.library;

import java.util.List;

/**
 * A Meta function is a meta reflection object with meta parameters and Meta
 * datatypes as output.
 *
 * @author aasmunds
 */
public interface IMetaFunction extends IMetaReflObject {

    /**
     * A list of parameters
     *
     * @return
     */
    List<IMetaParameter> getMetaParameters();

    /**
     * Map from datatype to output parameter
     *
     * @return
     */
    IMetaParameter getMetaOutput();

    /**
     * set output
     *
     * @param output
     */
    void setOutput(IMetaParameter output);

    /**
     * Find meta parameter
     *
     * @param name
     * @return
     */
    IMetaParameter findMetaParameter(String name);

    /**
     *
     * @return output datatype name
     */
    String getOutputDataTypeName();

    void setDataStorage(String dataStorage);

    String getDataStorage();

    Boolean isRespondable();

    void setRespondable(Boolean respondable);

    String getTags();

    void setTags(String tags);

    String getCategory();

    void setCategory(String Category);
    
}
