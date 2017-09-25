package no.imr.sea2data.imrbase.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * StrUtils class. Place handy string utility functions in this class
 *
 * @author aasmunds
 */
public class StrUtils {

    /**
     * Ensure a non-null string from a string.
     *
     * @param s
     * @return
     * @author aasmunds
     */
    public static Object makeObj(final String s) {
        return new Object() {
            @Override
            public String toString() {
                return s;
            }
        };
    }

    private static String xetter(String x, String name) {
        return x + "et" + capitalizeFirstLetter(name);
    }

    public static String getter(String name) {
        return xetter("g", name);
    }

    public static String setter(String name) {
        return xetter("s", name);
    }

    public static String capitalizeFirstLetter(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    public static String replaceCharAt(String s, int pos, char c) {
        return s.substring(0, pos) + c + s.substring(pos + 1);
    }

    public static String replaceStringAt(String s, int pos, String c) {
        return s.substring(0, pos) + c + s.substring(pos + c.length());
    }

    public static String removeChar(String s, char c) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            if (s.charAt(i) != c) {
                buf.append(s.charAt(i));
            }
        }
        return buf.toString();
    }

    public static String removeCharAt(String s, int pos) {
        return s.substring(0, pos) + s.substring(pos + 1);
    }

    public static String substring(String S, int beginIndex, int endIndex) {
        if (beginIndex < endIndex && beginIndex >= 0 && endIndex <= S.length()) {
            return S.substring(beginIndex, endIndex);
        } else {
            return "";
        }
    }

    public static String substring(String S, int beginIndex) {
        return substring(S, beginIndex, S.length());
    }

    public static String leftStr(String S, int count) {
        return midStr(S, 1, count);
    }

    public static String rightStr(String S, int count) {
        return midStr(S, S.length() - count + 1, count);
    }

    public static String midStr(String S, int iStart, int count) {
        return substring(S, iStart - 1, iStart - 1 + count);
    }

    public static Boolean enclosedBy(String s, String begin, String end) {
        return leftStr(s, begin.length()).equals(begin) && rightStr(s, end.length()).equals(end);
    }

    public static Boolean enclosedBy(String s, String e) {
        return enclosedBy(s, e, e);
    }

    public static String inflateString(String s, Integer offset) {
        return midStr(s, offset + 1, s.length() - 2 * offset);
    }

    public static String inflateString(String s) {
        return inflateString(s, 1);
    }

    public static String replaceControlChars(String s, String r) {
        return s.replaceAll("\\p{Cntrl}", r);
    }

    public static String leftPad(Object obj, int size) {
        return leftPad(obj, size, ' ');
    }

    public static String leftPad(Object obj, int size, char padChar) {
        return padStr(obj, size, padChar, false);
    }

    public static String leftPad0(Object obj, int size) {
        return leftPad(obj, size, '0');
    }

    public static String rightPad(String obj, int size) {
        return rightPad(obj, size, ' ');
    }

    public static String rightPad(Object obj, int size, char padChar) {
        return padStr(obj, size, padChar, true);
    }

    /**
     * pad a string from one side. handle null object and too long strings are
     * cut
     *
     * @param obj
     * @param size
     * @param padChar
     * @param padAtRightSide
     * @return
     */
    public static String padStr(Object obj, int size, char padChar, boolean padAtRightSide) {
        if (size < 0) {
            String msg = "Invalid length " + (obj == null ? obj : obj.toString()) + ", " + size + ", " + padChar;
            Logger.getLogger(StrUtils.class.getName()).log(Level.SEVERE, msg);
        }
        if (size <= 0) {
            return "";
        }
        if (obj == null) {
            obj = "";
            padChar = ' ';
        }
        String s = obj.toString();
        if (s.length() == size) {
            return s;
        }
        if (s.length() > size) {
            return Conversion.safeSubstring(s, 0, size); // cut the end.
        }
        String res = String.format("%" + (padAtRightSide ? "-" : "") + size + "s", s);
        if (padChar != ' ') {
            res = res.replace(' ', padChar);
        }
        return res;
    }

    public static String listCSVStr(Collection objs) {
        String s = "";
        if (objs != null) {
            for (Object obj : objs) {
                if (!s.isEmpty()) {
                    s = s + ',';
                }
                s = s + obj.toString();
            }
        }
        return s;
    }

    /**
     * Convert a url content to a string. used when a resource is converted into
     * a string
     *
     * @param clazz
     * @param url
     * @return
     */
    public static String urlContentToString(Class clazz, String url) {
        return convertStreamToString(clazz.getResourceAsStream(url));
    }

    public static String convertStreamToString(InputStream is) {
        /*
         * To convert the InputStream to String we use the
         * BufferedReader.readLine() method. We iterate until the BufferedReader
         * return null which means there's no more data to read. Each line will
         * appended to a StringBuilder and returned as String.
         */
        if (is != null) {
            StringBuilder sb = new StringBuilder();
            String line;
            {
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    is.close();
                } catch (Exception ex) {
                    //Exceptions.printStackTrace(ex);
                } finally {
                    try {
                        reader.close();
                    } catch (IOException ex) {
                        //Exceptions.printStackTrace(ex);
                    }
                }
            }
            return sb.toString();
        } else {
            return "";
        }
    }

    /**
     * Strip off ending and return the first part of the string
     *
     * @param s the string to cut
     * @param nCut n characters to cut
     * @return the stripped string
     */
    public static String stripEndN(String s, Integer nCut) {
        return s.substring(0, s.length() - nCut);
    }

    /**
     * Strip one character from the string
     *
     * @param s
     * @return the stripped string
     */
    public static String stripEnd1(String s) {
        return stripEndN(s, 1);
    }

    public static String replaceString(String str, String r, String search, int idx) {
        if (search != null) {
            String substr = Conversion.safeSubstring(str, idx, idx + r.length());
            if (!substr.equals(search)) {
                return str;
            }
        }
        if (idx + r.length() - 1 > str.length() - 1) {
            return str;
        }
        return new StringBuffer(str).replace(idx, idx + r.length(), r).toString();
    }
}
