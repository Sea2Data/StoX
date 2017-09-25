package no.imr.sea2data.imrbase.math;

/**
 * Generic calculation functions
 *
 * @author atlet
 */
public class Calc {

    /**
     * round double to numdec decimals
     *
     * @param d the number to round
     * @param numdec number of decimals
     * @return number rounded
     */
    public static Double roundTo(Double d, int numdec) {
        if (d == null) {
            return null;
        }
        long i = (long) Math.pow(10, numdec);
        //return ((double) ((int) (d * i))) / i;
        return (double) Math.round(d * i) / i;
    }

    /**
     * return true if a double has decimals
     *
     * @param d
     * @param endDecPos . the last position to check for decimals. decimals
     * right for this are ignored.
     * @return
     */
    public static Boolean hasDecimals(Double d, int endDecPos) {
        if (d == null) {
            return null;
        }
        d += 0.00000000001;
        return roundTo(d - d.intValue(), endDecPos) != 0d;
    }
}
