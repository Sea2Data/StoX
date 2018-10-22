/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adapters;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author aasmunds
 */
public class TimeAdapter extends XmlAdapter<String, LocalTime> {

    static DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss[.SSS]'Z'");

    protected DateTimeFormatter getDTF() {
        return timeFormatter;
    }

    @Override
    public LocalTime unmarshal(String val) throws Exception {
        return LocalTime.parse(val, getDTF());
    }

    @Override
    public String marshal(LocalTime val) throws Exception {
        return val != null ? val.format(getDTF()) : null;
    }
}
