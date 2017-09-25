/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.library;

import java.util.Arrays;
import no.imr.stox.functions.utils.Functions;

/**
 * meta data type
 *
 * @author aasmunds
 */
public class MetaDataType extends MetaObject implements IMetaDataType {

    public MetaDataType(ILibrary library) {
        super(library);
    }

    public MetaDataType(ILibrary library, String name, String description) {
        super(library, name, description);
    }

    @Override
    public Boolean isReference() {
        return !Arrays.asList(Functions.DT_BOOLEAN, Functions.DT_STRING, Functions.DT_INTEGER, Functions.DT_DOUBLE).contains(getName());
    }

}
