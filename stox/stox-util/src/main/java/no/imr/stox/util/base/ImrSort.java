package no.imr.stox.util.base;

import java.math.BigInteger;
import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author aasmunds
 */
public class ImrSort {

    /**
     * Basic generic comparator class with sort direction and null handling.
     * Extend this class to make specific comparator
     *
     * @param <T>
     */
    public static abstract class BaseComparator<T> implements Comparator<T> {

        private boolean asc;

        public BaseComparator(boolean asc) {
            this.asc = asc;
        }

        /**
         * Number of tokens to split the object in
         *
         * @return
         */
        protected int numTokens() {
            return 1;
        }

        protected Object getToken(T o, int token) {
            return o;
        }

        @Override
        public int compare(T o1, T o2) {
            int res = 0;
            for (int token = 0; token < numTokens(); token++) {
                res = compareToken(getToken(o1, token), getToken(o2, token));
                if (res != 0) {
                    break;
                }
            }
            return res;
        }

        /**
         * Number of tokens to split the object in
         *
         * @param o1
         * @param o2
         * @return
         */
        public int compareToken(Object o1, Object o2) {
            int res;
            if (o1 == null && o2 == null) {
                res = 0;
            } else if (o1 == null) {
                res = -1;
            } else if (o2 == null) {
                res = 1;
            } else {
                res = compareInternal(o1, o2);
            }
            return (asc ? 1 : -1) * res;
        } // compare

        protected abstract int compareInternal(Object o1, Object o2);
    } // BaseComparator

    /**
     * Lexical comparator. sorting numbers as 1, 11, 2, 22...
     *
     * @param <T>
     */
    public static class LexicalComparator<T> extends BaseComparator<T> {

        public LexicalComparator(boolean asc) {
            super(asc);
        }

        @Override
        protected int compareInternal(Object o1, Object o2) {
            return compareString(o1.toString(), o2.toString());
        } // compareInternal
    } // LexicalComparator

    /**
     * Numeric comparator. sorting numbers as 1, 2, 11, 22...
     *
     * @param <T>
     */
    public static class NumericComparator<T> extends BaseComparator<T> {

        public NumericComparator(boolean asc) {
            super(asc);
        }

        @Override
        protected int compareInternal(Object o1, Object o2) {
            Double d1 = Conversion.safeStringtoDouble(o1.toString());
            Double d2 = Conversion.safeStringtoDouble(o2.toString());
            return d1.compareTo(d2);
        } // NumericComparator
    } // NumericComparator

    /**
     * Compares mixed lists with numerical and lexical parts: 1, 2, 3, A, B, C
     *
     * @param <T>
     */
    private static Collator collator = Collator.getInstance(new Locale("no"));

    {
        collator.setStrength(Collator.PRIMARY);
    }

    public static int compareString(String s1, String s2) {
        return collator.compare(s1, s2);
    }

    static Pattern textNumPattern = Pattern.compile("^(\\D+)(\\d+)$");

    public static int compareTranslative(Object o1, Object o2) {
        return compareTranslative(o1, o2, false);
    }

    public static int compareTranslative(Object o1, Object o2, boolean groupMode) {
        if (o1 instanceof Integer && o2 instanceof Integer) {
            // Integer sort without text conversion
            return ((Integer) o1).compareTo(((Integer) o2));
        } else if (o1 instanceof Double && o2 instanceof Double) {
            // Double sort without text conversion
            return ((Double) o1).compareTo(((Double) o2));
        } else if (o1 instanceof Date && o2 instanceof Date) {
            // Date sort without text conversion
            return ((Date) o1).compareTo((Date) o2);
        } else {
            Double d1 = Conversion.safeStringtoDoubleNULL(o1.toString());
            Double d2 = Conversion.safeStringtoDoubleNULL(o2.toString());
            if (d1 != null && d2 != null) {
                // Numeric sort with text conversion
                return d1.compareTo(d2);
            } else {
                String s1 = o1.toString();
                String s2 = o2.toString();
                // number text relation:
                if (d2 != null) {
                    // d1 == null
                    // Empty string before d2 and Non empty string after d2
                    return s1.isEmpty() ? -1 : 1; // Letters compared to numbers
                } else if (d1 != null) {
                    // d2 == null
                    // Empty string before d1 and Non empty string after d2
                    return s2.isEmpty() ? 1 : -1; // Numbers compared to letters
                }
                if (groupMode) {
                    // Text sort Sort T1,T2,T11 (letter+numeric sort i.e for numbered objects)
                    Matcher m1 = textNumPattern.matcher(s1);
                    Matcher m2 = textNumPattern.matcher(s2);
                    if (m1.find() && m2.find()) {
                        String t1 = m1.group(1);
                        String n1 = m1.group(2);
                        String t2 = m2.group(1);
                        String n2 = m2.group(2);
                        int res = compareString(t1, t2);
                        if (res == 0) {
                            Double nd1 = Conversion.safeStringtoDoubleNULL(n1);
                            Double nd2 = Conversion.safeStringtoDoubleNULL(n2);
                            if (nd1 != null && nd2 != null) {
                                return nd1.compareTo(nd2);
                            }
                        }
                    }
                }
                // generic alphanumeric text sort with collator
                // 1, 1A, 11, 2 
                return compareString(s1, s2);
            }
        }
    }

    public static class TranslativeComparator<T> extends BaseComparator<T> {

        boolean groupMode = false;

        public TranslativeComparator(boolean asc, boolean groupMode) {
            super(asc);
            this.groupMode = groupMode;
        }

        public TranslativeComparator(boolean asc) {
            this(asc, false);
        }

        @Override
        protected int compareInternal(Object o1, Object o2) {
            return compareTranslative(o1, o2, groupMode);
        }

    }

    public static class TranslativeComparatorWithToken<T> extends TranslativeComparator<T> {

        int nTokens = 1;
        String split = "/";

        public TranslativeComparatorWithToken(boolean asc, String split, int nTokens) {
            super(asc);
            this.nTokens = nTokens;
            this.split = split;
        }

        /**
         * analyze a collection and determine the number of tokens
         *
         * @param asc
         * @param split
         * @param keys
         */
        public TranslativeComparatorWithToken(boolean asc, String split, List<String> keys) {
            super(asc, true);
            if (keys != null && keys.size() > 0 && keys.get(0) != null) {
                String s[] = keys.get(0).split(split);
                nTokens = s.length == 0 ? 1 : s.length;
            }
        }

        @Override
        protected int numTokens() {
            return nTokens; //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        protected Object getToken(T o, int token) {
            if (o != null || o instanceof String) {
                String s[] = ((String) o).split(split);
                if (token < s.length) {
                    return s[token];
                }
            }
            return o;
        }

    }
}
