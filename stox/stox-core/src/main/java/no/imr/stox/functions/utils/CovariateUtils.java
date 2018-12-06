/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.utils;

import java.time.LocalDate;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrbase.util.IMRdate;
import no.imr.stox.bo.landing.SeddellinjeBO;
import org.apache.commons.lang.StringUtils;

/**
 * Utils class to covariates
 *
 * @author aasmunds
 */
public class CovariateUtils {

    /**
     * Get temporal full covariate key from a date and temporal with year
     *
     * @param covSourceType
     * @param d
     * @param tempM
     * @return
     */
    public static String getTemporalFullKey(String covSourceType, LocalDate d, MatrixBO tempM) {
        if (d == null) {
            return null;
        }
        String timeInterval = null;
        for (String cov : tempM.getRowColKeys(covSourceType)) {
            timeInterval = getTimeIntervalByCov(cov);
            break;
        }
        if (timeInterval == null) {
            return null;
        }
        // Find if any of the covariates relates to year
        Boolean seasonal = true;
        for (String cov : tempM.getRowColKeys(covSourceType)) {
            seasonal = CovariateUtils.isCovariateSeasonal(cov);
            break;
        }
        String year = Conversion.safeIntegertoString(d.getYear());
        switch (timeInterval) {
            case Functions.COVARIATETIMEINTERVAL_YEAR:
                return year;
            case Functions.COVARIATETIMEINTERVAL_PERIOD:
                for (String cov : tempM.getRowColKeys(covSourceType)) {
                    String def = (String) tempM.getRowColValue(covSourceType, cov);
                    if (isInPeriod(d, def)) {
                        return (seasonal ? year + "." : "") + cov;
                    }
                }
                break;
            default:
                Integer season = CovariateUtils.getSeasonByDate(d, timeInterval);
                String seasonType = CovariateUtils.getSeasonTypeByTimeInterval(timeInterval);
                String code = seasonType + season;
                if (!seasonal) {
                    code = year + "." + code;
                }
                return code;
        }
        return null;
    }

    public static String getTimeIntervalByCov(String cov) {
        if (cov == null) {
            return null;
        }
        return cov.contains("Q") ? Functions.COVARIATETIMEINTERVAL_QUARTER
                : cov.contains("M") ? Functions.COVARIATETIMEINTERVAL_MONTH
                : cov.contains("W") ? Functions.COVARIATETIMEINTERVAL_WEEK
                : cov.contains("P") ? Functions.COVARIATETIMEINTERVAL_PERIOD : Functions.COVARIATETIMEINTERVAL_YEAR;
    }

    public static String getSeasonTypeByTimeInterval(String timeInterval) {
        if (timeInterval == null) {
            return null;
        }
        switch (timeInterval) {
            case Functions.COVARIATETIMEINTERVAL_QUARTER:
                return "Q";
            case Functions.COVARIATETIMEINTERVAL_MONTH:
                return "M";
            case Functions.COVARIATETIMEINTERVAL_WEEK:
                return "W";
            case Functions.COVARIATETIMEINTERVAL_PERIOD:
                return "P";
        }
        return null;
    }

    public static Integer getMaxSeasonByTimeInterval(String timeInterval) {
        if (timeInterval == null) {
            return null;
        }
        switch (timeInterval) {
            case Functions.COVARIATETIMEINTERVAL_QUARTER:
                return 4;
            case Functions.COVARIATETIMEINTERVAL_MONTH:
                return 12;
            case Functions.COVARIATETIMEINTERVAL_WEEK:
                return 52;
        }
        return null;
    }

    public static Integer getSeasonByDate(LocalDate d, String timeInterval) {
        switch (timeInterval) {
            case Functions.COVARIATETIMEINTERVAL_YEAR:
                return IMRdate.getYear(d);
            case Functions.COVARIATETIMEINTERVAL_QUARTER:
                return IMRdate.getQuarter(d);
            case Functions.COVARIATETIMEINTERVAL_MONTH:
                return IMRdate.getMonth(d);
            case Functions.COVARIATETIMEINTERVAL_WEEK:
                return IMRdate.getWeek(d);
            case Functions.COVARIATETIMEINTERVAL_PERIOD:
                return null; // must use the covariate definition to determine the period.
        }
        return null;
    }

    private static Integer getSeasonByCovariate(String cov, Integer iIntv) {
        if (iIntv < 0 || iIntv > cov.length() - 1) {
            return null;
        }
        String s = Conversion.safeSubstring(cov, iIntv + 1, cov.length());
        return Conversion.safeStringtoIntegerNULL(s);
    }

    public static Integer getSeasonByCovariateAndSeasonType(String cov, String seasonType) {
        if (cov == null || seasonType == null) {
            return null;
        }
        return getSeasonByCovariate(cov, cov.indexOf(seasonType));
    }

    public static Integer getSeasonByCovariate(String cov) {
        return getSeasonByCovariateAndSeasonType(cov, getSeasonTypeByCov(cov));
    }

    public static String getSeasonTypeByCov(String cov) {
        return getSeasonTypeByTimeInterval(getTimeIntervalByCov(cov));
    }

    public static String getFullPeriod(String period, Integer year) {
        Integer n = StringUtils.countMatches(period, "/");
        switch (n) {
            case 1:
                if (!(year % 400 == 0 || year % 4 == 0 && year % 100 != 0)) {
                    // Reduce february max from 29 to 28 on max date
                    Integer ns = period.indexOf("/");
                    Integer day = Conversion.safeSubstringToIntegerNULL(period, 0, ns);
                    Integer month = Conversion.safeSubstringToIntegerNULL(period, ns + 1, period.length());
                    if (month == 2 && day == 29) {
                        return IMRdate.encodeDateStr(day, month - 1, year);
                    }
                }
                return period + "/" + year;
            case 2:
                return period;
        }
        return null;
    }

    public static Boolean isInPeriod(LocalDate date, String period) {
        if (date == null || period == null) {
            return false;
        }
        String[] s = period.split("-");
        if (s.length == 2) {
            Integer year = date.getYear();
            LocalDate from = IMRdate.strToLocalDate(getFullPeriod(s[0], year), IMRdate.DATE_FORMAT_DMY);
            LocalDate to = IMRdate.strToLocalDate(getFullPeriod(s[1], year), IMRdate.DATE_FORMAT_DMY);
            if (IMRdate.isSameDay(date, from) || IMRdate.isSameDay(date, to) || date.isAfter(from) && date.isBefore(to)) {
                return true;
            }
        }
        return false;
    }

/*    public static String getSpatialCovValue(SluttSeddel sl, String var1, String var2) {
        if (var1 != null && var1.equals(Functions.SPATIALVARIABLE_NONE)) {
            var1 = null;
        }
        if (var2 != null && var2.equals(Functions.SPATIALVARIABLE_NONE)) {
            var2 = null;
        }
        String val1 = null;
        String val2 = null;
        if (var1 != null) {
            val1 = getSpatialCovValue(sl, var1);
            if (val1 == null || val1.isEmpty()) {
                return null;
            }
        }
        if (var2 != null) {
            val2 = getSpatialCovValue(sl, var2);
            if (val2 == null || val2.isEmpty()) {
                return null;
            }
        }
        if (var1 != null && var2 != null) {
            return val1 + "_" + val2;
        }
        if (var1 != null) {
            return val1;
        }
        if (var1 != null) {
            return val2;
        }
        return null;
    }*/

    public static String getSpatialCovValue(SeddellinjeBO sl/*, String dim*/) {
                return sl.getStratum();
        /*if (dim == null) {
            return null;
        }
        switch (dim) {
            case Functions.SPATIALVARIABLE_STRATUM:
                return sl.getStratum();
            case Functions.SPATIALVARIABLE_MAINAREA:
                return sl.getFangstHomr() != null ? sl.getFangstHomr() + "" : null;
            case Functions.SPATIALVARIABLE_LOCATION:
                return sl.getFangstLok();
            case Functions.SPATIALVARIABLE_LANDINGSITE:
                return sl.getLandingsMottak();
        }
        return null;*/
    }

    /*public static String getSpatialCovValue(FishstationBO fs, String var1, String var2) {
        if (var1 != null && var1.equals(Functions.SPATIALVARIABLE_NONE)) {
            var1 = null;
        }
        if (var2 != null && var2.equals(Functions.SPATIALVARIABLE_NONE)) {
            var2 = null;
        }
        String val1 = null;
        String val2 = null;
        if (var1 != null) {
            val1 = getSpatialCovValue(fs, var1);
            if (val1 == null || val1.isEmpty()) {
                return null;
            }
        }
        if (var2 != null) {
            val2 = getSpatialCovValue(fs, var2);
            if (val2 == null || val2.isEmpty()) {
                return null;
            }
        }
        if (var1 != null && var2 != null) {
            return val1 + "_" + val2;
        }
        if (var1 != null) {
            return val1;
        }
        if (var1 != null) {
            return val2;
        }
        return null;
    }*/

    public static String getSpatialCovValue(FishstationBO fs/*, String dim*/) {
                return fs.getStratum();
/*        if (dim == null) {
            return null;
        }
        switch (dim) {
            case Functions.SPATIALVARIABLE_STRATUM:
                return fs.getStratum();
            case Functions.SPATIALVARIABLE_MAINAREA:
                return fs.getArea() != null ? fs.getArea() + "" : null;
            case Functions.SPATIALVARIABLE_LOCATION:
                return fs.getLocation() != null ? fs.getLocation() + "" : null;
            case Functions.SPATIALVARIABLE_LANDINGSITE:
                return null; // Not supported
        }
        return null;*/
    }

    public static String getCovKeyByDefElm(String covSourceType, String elm, MatrixBO covM, boolean startsWidth) {
        if (elm == null) {
            return null;
        }
        for (String covKey : covM.getRowColKeys(covSourceType)) {
            String def = (String) covM.getRowColValue(covSourceType, covKey);
            if (def == null) {
                continue;
            }
            String[] selm = def.split(",");
            for (String e : selm) {
                if (startsWidth && elm.startsWith(e) || !startsWidth && elm.equals(e)) {
                    return covKey;
                }
            }
        }
        return null;
    }

    public static Boolean isCovariateSeasonal(String cov) {
        return getSeasonTypeByCov(cov) != null && !cov.contains(".");
    }

}
