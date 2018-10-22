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
public class OldTimeAdapter extends TimeAdapter {
    static DateTimeFormatter oldTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");

    @Override
    protected DateTimeFormatter getDTF() {
        return oldTimeFormatter;
    }

}

    

