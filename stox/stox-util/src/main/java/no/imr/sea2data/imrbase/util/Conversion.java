package no.imr.sea2data.imrbase.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;

/**
 *
 * @author aasmunds
 */
public final class Conversion {

    private static final NumberFormat CURRENT_NUMBER_FORMAT = NumberFormat.getInstance();

    static {
        // Set the current local number format to integeronly
        // The nf.format(1234.00) gives 1234 instead of 1234.0
        // This is the default in other languages.
        CURRENT_NUMBER_FORMAT.setParseIntegerOnly(true);
        CURRENT_NUMBER_FORMAT.setMaximumFractionDigits(11);
        CURRENT_NUMBER_FORMAT.setGroupingUsed(false);
    }

    /**
     *
     * @param value
     * @return
     */
    public static Boolean safeObjectToBoolean(Object value) {
        try {
            return Boolean.valueOf(value != null ? value.toString() : null);
        } catch (Exception e) {
        }
        return null;
    }

    public static Double safeObjectToDouble(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj instanceof Integer) {
            return ((Integer) obj).doubleValue();
        } else if (obj instanceof Double) {
            return ((Double) obj);
        }
        return Conversion.safeStringtoDouble(obj.toString());
    }

    private Conversion() {
    }

    /**
     * Returns the substring of a text.
     *
     * TODO: how is this more safe then actually throwing an error when a
     * substring is outside of the range of the string? Wouldn't it be better to
     * give the user information about missing data?
     *
     * @param txt
     * @param startindx
     * @param stopindx
     * @return
     */
    public static String safeSubstring(String txt, Integer startindx, Integer stopindx) {
        // aasmunds: Fix bug: when stopindx > txt.length, the substring is between startindx and min(txt.length, stopindx)
        // Discovered when importing a V line with length 88 where vekstsone 81-100 was retrieved.
        stopindx = Math.min(stopindx, txt.length());
        if (stopindx <= startindx) {
            return "";
        }
        // Fabricate a new referenced object to avoid the substring memory blooper
        // This is done to avoid the long string to be hold.
        return new String(txt.substring(startindx, stopindx));
    }

    public static Integer safeSubstringToIntegerNULL(String txt, Integer startindx, Integer stopindx) {
        return Conversion.safeStringtoIntegerNULL(safeSubstring(txt, startindx, stopindx));
    }

    public static BigInteger safeStringToBigIntegerNULL(String s) {
        Integer i = safeStringtoIntegerNULL(s);
        return i != null ? BigInteger.valueOf(i) : null;
    }

    public static BigDecimal safeStringToBigDecimalNULL(String s) {
        Double d = safeStringtoDoubleNULL(s);
        return d != null ? BigDecimal.valueOf(d) : null;
    }

    /**
     * Returns an integer representation of the substring.
     *
     * TODO: how is this more safe then actually throwing an error when a
     * substring is outside of the range of the string? Wouldn't it be better to
     * give the user information about missing data?
     *
     * @param txt
     * @param startindx
     * @param stopindx
     * @return
     */
    public static Integer safeIntegerFromSubstring(String txt, Integer startindx, Integer stopindx) {
        int length = txt.length();
        if (length >= stopindx) {
            return safeStringtoInteger(txt.substring(startindx, stopindx));
        } else {
            return null;
        }
    }

    /**
     * returns a Double representation of the substring
     *
     * TODO: how is this more safe then actually throwing an error when a
     * substring is outside of the range of the string? Wouldn't it be better to
     * give the user information about missing data?
     *
     * @param txt
     * @param startindx
     * @param stopindx
     * @return
     */
    public static Double safeDoubleFromSubstring(String txt, Integer startindx, Integer stopindx) {
        int length = txt.length();
        if (length >= stopindx) {
            return safeStringtoDouble(txt.substring(startindx, stopindx));
        } else {
            return null;
        }
    }

    /**
     * Returns a String representation of a double
     *
     * @param value
     * @return
     */
    public static String safeDoubletoString(Double value) {
        try {
            return Double.isNaN(value) ? "nan" : Double.isInfinite(value) ? "inf" : CURRENT_NUMBER_FORMAT.format(value);
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * Returns a Double representation of a string
     *
     * @param value
     * @return
     */
    public static Double safeStringtoDouble(String value) {
        return safeStringtoDoubleDef(value, 0.0);
    }

    /**
     * Returns a Double representation of the string, or null if something went
     * wrong.
     *
     * @param value
     * @return
     */
    public static Double safeStringtoDoubleNULL(String value) {
        try {
            return Double.valueOf(safeStr(value).trim());
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static Double safeStringtoDoubleNULLEnglishFormat(String value) {
        if (value != null && value.contains(",")) {
            value = value.replace(",", ".");
        }
        return safeStringtoDoubleNULL(value);
    }

    /**
     * Returns a Double representation of a string or def if an exception is
     * thrown while running Double.valueOf(string)
     *
     * @param value
     * @param def
     * @return
     */
    public static Double safeStringtoDoubleDef(String value, Double def) {
        try {
            return Double.valueOf(safeStr(value).replace(',', '.'));
        } catch (NumberFormatException ex) {
            return def;
        }
    }

    /**
     * Returns a Integer representation of the string, or 0 if something went
     * wrong.
     *
     * @param value
     * @return
     */
    public static Integer safeStringtoInteger(String value) {
        try {
            return Integer.valueOf(safeStr(value).trim());
        } catch (NumberFormatException ex) {
            return 0;
        }
    }

    /**
     * Returns a Integer representation of the string, or null if something went
     * wrong.
     *
     * @param value
     * @return
     */
    public static Integer safeStringtoIntegerNULL(String value) {
        try {
            if (value == null) {
                return null;
            }
            String s = safeStr(value).trim();
            if (s.isEmpty()) {
                return null;
            }
            return Integer.valueOf(s);
        } catch (NumberFormatException ex) {
            return null;
        }
    }

    public static Integer safeStringtoIntegerDef(String value, Integer def) {
        try {
            return Integer.valueOf(safeStr(value).trim());
        } catch (NumberFormatException ex) {
            return def;
        }
    }

    /**
     * Returns a empty string if str = null otherwise returns str.trim()
     *
     * @param str
     * @return
     */
    public static String safeStr(String str) {
        return (str == null ? "" : str.trim());
    }

    /**
     * Returns 0 if value = 0 otherwise returns value
     *
     * @param value
     * @return
     */
    public static Integer safeInteger(Integer value) {
        return value == null ? 0 : value;
    }

    /**
     * Returns 0.0 if value = null otherwise returns value
     *
     * @param value
     * @return
     */
    public static Double safeDouble(Double value) {
        return value == null ? 0.0 : value;
    }

    /**
     * Returns a String representation of the integer object or "" if something
     * goes wrong
     *
     * @param value
     * @return
     */
    public static String safeIntegertoString(Integer value) {
        return value != null ? value.toString() : "";
    }

    private static DecimalFormat getDecFormat(String pattern) {
        DecimalFormat df = pattern != null ? new DecimalFormat(pattern) : new DecimalFormat();
        DecimalFormatSymbols dfs = df.getDecimalFormatSymbols();
        dfs.setDecimalSeparator('.');
        df.setDecimalFormatSymbols(dfs);
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df;
    }

    /**
     * Formats a double into the representation of the input format or an empty
     * string if an exception is thrown
     *
     * @param value
     * @param pattern
     * @return
     */
    public static String formatDoubletoDecimalString(Double value, String pattern) {
        try {
            return getDecFormat(pattern).format(value);
        } catch (Exception ex) {
            return "";
        }
    }

    /**
     * If number is less than one, treat numdecimals as # significant digits.
     * Otherwise as number of decimals. This is useful when scaling numbers with
     * respect to unit.
     *
     * @param value
     * @param numdecimals
     * @return
     */
    public static String formatDoubletoDecimalString(Double value, int numdecimals) {
        if (value == null) {
            return "";
        }
//        String s = String.format("%.18f", value);
//        s = s.replace(',', '.');
//        int idec = s.indexOf('.');
//        if (idec == 1 && s.charAt(0) == '0' || idec == 2 && s.charAt(0) == '-' && s.charAt(1) == '0') {
//            int n = 0;
//            while (idec < s.length() - 1) {
//                idec++;
//                if (s.charAt(idec) != '0') {
//                    break;
//                }
//                n++;
//            }
//            numdecimals += n;
//        }
        String s = String.format("%." + numdecimals + "f", value);
        s = s.replace(',', '.');
//        while (s.endsWith("0")) {
//            s = (s.substring(0, s.length() - 1));
//        }
//        if (s.endsWith(".")) {
//            s = (s.substring(0, s.length() - 1));
//        }
        return s;
    }

    /**
     * Format a double into the representation of the input format or null if
     * something goes wrong
     *
     * @param value
     * @param format
     * @return
     */
    public static Double formatDouble(Double value, String format) {
        // "String format" example: "#.#" or "0.0"
        try {
            DecimalFormat df = new DecimalFormat(format);
            return safeStringtoDouble(df.format(value));
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * returns -1 if s == null || s.trim().isEmpty(), otherwise returns
     * Integer.parseInt(s)
     *
     * @param s
     * @return
     */
    public static Integer imrParseInt(String s) {
        return imrParseInt(s, -1);
    }

    /**
     * returns defValue if s == null || s.trim().isEmpty(), otherwise returns
     * Integer.parseInt(s)
     *
     * @param s
     * @param defValue
     * @return
     */
    private static Integer imrParseInt(String s, Integer defValue) {
        return (s == null || s.trim().isEmpty()) ? defValue : Integer.parseInt(s);
    }

    public static Double safeIntegerToDouble(Integer i) {
        return i != null ? i.doubleValue() : null;
    }

    public static Integer safeDoubleToInteger(Double d) {
        return d != null ? d.intValue() : null;
    }
}
