/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.library;

import no.imr.sea2data.imrbase.matrix.IMetaMatrix;
import java.util.List;

/**
 * Meta parameter is a meta object with data type and a possible meta matrix
 *
 * @author aasmunds
 */
public interface IMetaParameter extends IMetaObject {

    /**
     *
     * @return meta function
     */
    IMetaFunction getMetaFunction();

    String resolveParameterValue(String value);

    /**
     *
     * @return is required
     */
    Boolean isRequired();

    /**
     * set required
     *
     * @param required
     */
    void setRequired(Boolean required);

    Boolean isDeprecated();

    void setDeprecated(Boolean deprecated);

    Boolean isFileRef();

    void setFileRef(Boolean fileRef);

    /**
     *
     * @return meta data type
     */
    IMetaDataType getMetaDataType();

    /**
     * Set meta data type
     *
     * @param metaDataType
     */
    void setMetaDataType(IMetaDataType metaDataType);

    /**
     *
     * @return meta matrix
     */
    IMetaMatrix getMetaMatrix();

    /**
     * set meta matrix
     *
     * @param metaMatrix
     */
    void setMetaMatrix(IMetaMatrix metaMatrix);

    Object getDefaultValue();

    String getDataTypeName();

    List<String> getValues();

    public List getValidValues();

    void setValues(List<String> values);

    List<String> getParentTags();

    void setParentTags(List<String> parentTags);

    Boolean isParentParameter();

    Boolean isCompatible(IMetaParameter mp);

}
