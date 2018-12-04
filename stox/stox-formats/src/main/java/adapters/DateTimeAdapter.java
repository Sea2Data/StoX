/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adapters;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author aasmunds
 */
public class DateTimeAdapter extends XmlAdapter<String, LocalDateTime> {

    DateTimeFormatter dateTimFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    protected DateTimeFormatter getDTF() {
        return dateTimFormatter;
    }

    @Override
    public LocalDateTime unmarshal(String val) throws Exception {
        return LocalDateTime.parse(val, getDTF());
    }

    @Override
    public String marshal(LocalDateTime val) throws Exception {
        return val != null ? val.format(getDTF()) : null;
    }
}