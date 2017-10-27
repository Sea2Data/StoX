/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.utils;

import no.imr.sea2data.imrbase.util.ExportUtil;

/**
 *
 * @author aasmunds
 */
public class ReportUtil {

    public static String key(int mask, int i, String key) {
        return (i & mask) != 0 ? "TOTAL" : key;
    }

    public static String heading(String estLayer, String stratum, String dim3, String dim4, String dim5) {
        return ExportUtil.separated('/', estLayer, stratum, dim3, dim4, dim5);
    }

    public static final String UNKNOWN_VALUE = "-";
    public static final String TOTAL = "TOTAL";

    public static String formatNumber(Double val, int numDec, int width) {
        if (val == null) {
            return String.format("%" + width + "s", UNKNOWN_VALUE);
        } else {
            return String.format("%" + width + "." + numDec + "f", val);
        }
    }

    public static String getScaleString(Integer scale) {
        if (scale == null) {
            scale = 1;
        }
        String scaleStr = "";
        switch (scale) {
            case 1000:
                scaleStr = "1E3";
                break;
            case 1000000:
                scaleStr = "1E6";
                break;
            default:
                scaleStr = scale.toString();
        }
        return scaleStr;
    }
    
    public static String asgExpr(String key, Object value) {
        return key + "=" + (value != null ? value.toString() : "");
    }
}
