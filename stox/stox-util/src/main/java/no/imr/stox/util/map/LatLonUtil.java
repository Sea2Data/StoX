package no.imr.stox.util.map;

import java.util.Locale;
import no.imr.stox.util.math.Calc;
import no.imr.stox.util.base.Conversion;

/**
 * Helper functions for lat lon
 *
 * @author Åsmund
 */
public class LatLonUtil {

    /**
     * Get degree from latorlon
     *
     * @param latlon
     * @return
     */
    public static Integer degFromPos(Double latlon) {
        return (int) latlon.doubleValue();
    }

    /**
     * Get decimal minute from position
     *
     * @param latlon
     * @return
     */
    public static Double decMinFromPos(Double latlon, Integer deg) {
        return Math.abs(latlon - deg) * 60;
    }

    public static Double posToDouble(String value, boolean isLat) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        // [-]DD.dd
        if (value.contains(".")) {
            Double res = Conversion.safeStringtoDoubleNULL(value);
            if (res != null) {
                // A floating number with decimal point
                return res;
            }
        }
        // Continue to analyse DDMMm../DDDMMm.. or DD MM.mm with negation and E/V
        int neg = 1;
        if (value.startsWith("-")) {
            neg = -1;
            if (value.length() > 1) {
                value = value.substring(1);
            }
        }
        boolean isLon = !isLat;
        String[] s = value.split("[\\u00b0\\s']+");
        Double deg = null;
        Double min = 0d;
        if (s.length == 1 && !s[0].isEmpty()) {
            // DDMMm../DDDMMm..
            String degs = s[0];
            int numDegDigits = isLat ? 2 : 3;
            String degPart = degs.substring(0, Math.min(numDegDigits, degs.length()));
            deg = Conversion.safeStringtoDoubleNULL(degPart);
            if (deg == null) {
                return null;
            }
            Double minPartD = 0d;
            Double minDecPartD = 0d;
            if (degs.length() > numDegDigits) {
                String minPart = degs.substring(numDegDigits, degs.length());
                String minPartMM = minPart.substring(0, Math.min(2, minPart.length()));
                minPartD = Conversion.safeStringtoDoubleNULL(minPartMM);
                if (minPartD == null) {
                    return null;
                }
                if (minPart.length() > 2) {
                    String minDecPart = minPart.substring(2, minPart.length());
                    minDecPartD = Conversion.safeStringtoDoubleNULL(minDecPart);
                    if (minDecPartD == null) {
                        return null;
                    }
                    Double frac = Math.pow(10, minDecPart.length());
                    minDecPartD = minDecPartD / frac;
                }
            }
            min = minPartD + minDecPartD;
        } else {
            // DD MM.mm
            deg = Conversion.safeStringtoDoubleNULL(s[0]);
        }
        if (s.length > 1) {
            min = Conversion.safeStringtoDoubleNULL(s[1]);
        }
        if (s.length > 2) {
            char c = s[2].toUpperCase().charAt(0);
            if (isLon && (c == 'W' || c == 'V') || (isLat && c == 'S')) {
                neg = -1;
            }
        }
        if (deg == null || min == null) {
            return null;
        }
        if (isLat && (deg < -90 || deg > 90) || isLon && (deg < -180 || deg > 180)) {
            return null;
        }
        if (min > 60) {
            return null;
        }
        return neg * (deg + min / 60.0);
    }

    public static String doubleToPos(Double latlon, boolean isLat) {
        Boolean isNo = Locale.getDefault().getLanguage().equals("no");
        String dir[] = isLat ? new String[]{"S", "N"} : (new String[]{isNo ? "V" : "W", isNo ? "Ø" : "E"});
        return getDegreeDecimalDescr(latlon, dir, isLat ? 2 : 3, true);
    }

    public static String latLonToStr(Double lat, Double lon) {
        return "lat  " + LatLonUtil.doubleToPos(lat, true) + "  lon  " + LatLonUtil.doubleToPos(lon, false);
    }

    public static String getDegreeDecimalDescr(Double latlon, String[] dir, Integer numDegDig, boolean withMinute) {
        return getDegreeDecimalDescr(latlon, dir, numDegDig, true, 2);
    }

    /**
     * Get decimal minute from position
     *
     * @param latlon
     * @return
     */
    public static String getDegreeDecimalDescr(Double latlon, String[] dir, Integer numDegDig, boolean withMinute, Integer numMinDec) {
        if (latlon == null) {
            return "";
        }
        Integer deg = degFromPos(latlon);
        Double min = decMinFromPos(latlon, deg);
        if (dir != null) {
            deg = Math.abs(deg);
        }
        String sneg = dir != null ? (latlon < 0 ? dir[0] : dir[1]) : "";
        if (withMinute) {
            return String.format(Locale.UK, "%0" + numDegDig + "d\u00b0%0" + (3 + numMinDec) + "." + numMinDec + "f' %s", deg, min, sneg);
        } else {
            return String.format(Locale.UK, "%" + numDegDig + "d%s", deg, sneg);
        }
    }

    public static String getDegreeDecimalDescr(Double latlon, Integer numDegDig) {
        return getDegreeDecimalDescr(latlon, new String[]{"", ""}, numDegDig, true, 2);
    }

    public static String getDegreeDecimalDescr(Double latlon, Integer numDegDig, Integer numMinDec) {
        return getDegreeDecimalDescr(latlon, new String[]{"", ""}, numDegDig, true, numMinDec);
    }

    /**
     * get latitude or longitude from deg, min, decmin and negative parameters
     *
     * @param neg true if at negative scale
     * @param deg degree (60 minute)
     * @param min minute
     * @param decmin decimal minute
     * @return the latitude or longitude (neg ? -1 : 1) * (deg + ((min + decmin
     * / 10) / 60)) with numdec decimals;
     */
    public static Double getLatOrLon(Boolean neg, Integer deg, Integer min, Integer decmin, Integer numdec) {
        Double d = getLatOrLon(neg, deg, min + decmin / 10.0);
        return Calc.roundTo(d, numdec);
    }

    public static Double getLatOrLon(Boolean neg, Integer deg, Double min) {
        return (neg ? -1 : 1) * (deg + min / 60.0);
    }

    public static Integer getSystem3Area(Double lat) {
        if (lat == null) {
            return null;
        }
        return (int) ((lat - 35.5) * 2);
    }

    /**
     * calculate system 3
     *
     * @param lon
     * @return
     */
    public static String getSystem3Location(Double lon) {
        if (lon == null || lon < -180 || lon > 180 || lon > -150 && lon < -50) {
            return null;
        }
        if (lon < -150) {
            lon += 360;
        }
        char s1 = (char) ((int) ((lon + 50) / 10) + (int) 'A');
        int s2 = (int) ((lon % 10 + 10)) % 10;
        return "" + s1 + s2;
    }
}
