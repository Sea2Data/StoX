/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adapters;

import java.time.format.DateTimeFormatter;

/**
 *
 * @author aasmunds
 */
public class OldDateAdapter extends DateAdapter {

    static DateTimeFormatter oldDateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    protected DateTimeFormatter getDTF() {
        return oldDateFormatter;
    }
    
}
