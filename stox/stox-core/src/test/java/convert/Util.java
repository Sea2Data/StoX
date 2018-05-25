/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package convert;

/**
 *
 * @author aasmunds
 */
public class Util {

    public static String getMissionStratumKey(Integer mission, String stratum) {
        return "Oppdrag" + mission + "_" + getStratumEng(stratum);
    }

    public static String getStratumEng(String stratum) {
        return stratum.replace("æ", "ae").replace("Æ", "Ae")
                .replace("å", "aa").replace("Å", "Aa")
                .replace("ø", "oe").replace("Ø", "Oe").replace("/", "_");
    }

}
