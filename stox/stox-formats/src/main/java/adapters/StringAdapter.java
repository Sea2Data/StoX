/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adapters;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Force a string to be unmarshalled as string and vice versa
 * @author aasmunds
 */
public class StringAdapter extends XmlAdapter<String, String> {

    @Override
    public String unmarshal(String val) throws Exception {
        return val;
    }

    @Override
    public String marshal(String val) throws Exception {
        return val;
    }
}
