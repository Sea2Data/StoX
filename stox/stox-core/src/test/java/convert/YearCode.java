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
public class YearCode {

    Integer year;
    String code;
    String cruise;

    public YearCode(Integer year, String code, String cruise) {
        this.year = year;
        this.code = code;
        this.cruise = cruise;
    }

    public Integer getYear() {
        return year;
    }

    public String getCode() {
        return code;
    }

    public String getCruise() {
        return cruise;
    }

}
