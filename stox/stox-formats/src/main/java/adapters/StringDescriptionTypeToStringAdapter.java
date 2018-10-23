/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adapters;

import BioticTypes.v1_4.StringDescriptionType;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author aasmunds
 */
public class StringDescriptionTypeToStringAdapter extends XmlAdapter<StringDescriptionType, String> {

    @Override
    public String unmarshal(StringDescriptionType val) throws Exception {
        return val != null ? val.getValue() : null;
    }

    @Override
    public StringDescriptionType marshal(String val) throws Exception {
        StringDescriptionType sd = new StringDescriptionType();
        sd.setValue(val);
        return sd;
    }
}
