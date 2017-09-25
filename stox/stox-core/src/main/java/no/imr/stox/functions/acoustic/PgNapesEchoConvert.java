/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.acoustic;

/**
 *
 * @author aasmunds
 */
public final class PgNapesEchoConvert {

    /**
     * get acoustic category from pgnapes code Query: select pgnapescode,
     * acousticcategory from nmdreference.acousticcategory where pgnapescode is
     * not null
     *
     * @param spec
     * @return
     */
    public static Integer getAcoCatFromPgNapesSpecies(String spec) {
        switch (spec) {
            case "PLK":
                return 6;
            case "MYX":
                return 7;
            case "POC":
                return 8;
            case "HER":
                return 12;
            case "CAP":
                return 16;
            case "WHG":
                return 18;
            case "MAC":
                return 21;
            case "POK":
                return 22;
            case "WHB":
                return 24;
            case "USK":
                return 25;
            case "SAN":
                return 27;
            case "NOP":
                return 28;
            case "RED":
                return 29;
            case "HAD":
                return 30;
            case "COD":
                return 31;
            case "KRZ":
                return 77;
            case "HOM":
                return 5003;
            case "PLS":
                return 5005;
            case "HKE":
                return 5010;
            case "MES":
                return 5013;
        }
        return null;
    }

    public static String getPgNapesSpeciesFromAcoCat(Integer acoCat) {
        switch (acoCat) {
            case 6:
                return "PLK";
            case 7:
                return "MYX";
            case 8:
                return "POC";
            case 12:
                return "HER";
            case 16:
                return "CAP";
            case 18:
                return "WHG";
            case 21:
                return "MAC";
            case 22:
                return "POK";
            case 24:
                return "WHB";
            case 25:
                return "USK";
            case 27:
                return "SAN";
            case 28:
                return "NOP";
            case 29:
                return "RED";
            case 30:
                return "HAD";
            case 31:
                return "COD";
            case 77:
                return "KRZ";
            case 5003:
                return "HOM";
            case 5005:
                return "PLS";
            case 5010:
                return "HKE";
            case 5013:
                return "MES";
        }
        return null;
    }
}
