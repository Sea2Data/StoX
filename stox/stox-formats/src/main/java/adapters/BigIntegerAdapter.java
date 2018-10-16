/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adapters;

import java.math.BigInteger;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author kjetilf
 */
public class BigIntegerAdapter extends XmlAdapter<String, BigInteger> {

    @Override
    public BigInteger unmarshal(String val) throws Exception {
        try {
            return new BigInteger(val);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    @Override
    public String marshal(BigInteger val) throws Exception {
        if (val != null) {
            return val.toString();
        } else {
            return null;
        }
    }

}
