/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adapters;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 *
 * @author aasmunds
 */
public class DateStringDMYAdapter extends XmlAdapter<String, LocalDate> {

    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    protected DateTimeFormatter getDTF() {
        return dateFormatter;
    }

    @Override
    public LocalDate unmarshal(String val) throws Exception {
        return LocalDate.parse(val, getDTF());
    }

    @Override
    public String marshal(LocalDate val) throws Exception {
        return val != null ? val.format(getDTF()) : null;
    }
}
