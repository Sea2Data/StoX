package no.imr.sea2data.imrbase.util;

import com.luckycatlabs.sunrisesunset.SunriseSunsetCalculator;
import com.luckycatlabs.sunrisesunset.Zenith;
import com.luckycatlabs.sunrisesunset.calculator.SolarEventCalculator;
import com.luckycatlabs.sunrisesunset.dto.Location;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DateTimeException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 *
 * @author aasmunds
 */
public final class IMRdate {

    // pre java 8
    public static Calendar gmtCalendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    public static Calendar currentCalendar = Calendar.getInstance();
    // Java 8
    public static ZoneId utcZoneId = ZoneId.of("GMT");

    public static Calendar getCalendar(boolean gmt) {
        return gmt ? gmtCalendar : currentCalendar;
    }

    public static Long getTimeMillis(Date date, boolean gmt) {
        getCalendar(gmt).setTime(date);
        return getCalendar(gmt).getTimeInMillis();
    }

    public static Integer getUTCFieldFromTimeMillis(int fieldtype, Double timeinmillis) {
        return getField(fieldtype, new Date(timeinmillis.longValue()), true);
    }

    public static Integer getField(int fieldtype, Date date, boolean gmt) {
        if (date == null) {
            return null;
        }
        getCalendar(gmt).setTime(date);
        return getCalendar(gmt).get(fieldtype);
    }

    public static Integer getYear(Date d, boolean gmt) {
        return getField(Calendar.YEAR, d, gmt);
    }

    public static Integer getMonth(Date d, boolean gmt) {
        if (d == null) {
            return null;
        }
        return getField(Calendar.MONTH, d, gmt) + 1;
    }

    public static Integer getWeek(Date d, boolean gmt) {
        if (d == null) {
            return null;
        }
        return getField(Calendar.WEEK_OF_YEAR, d, gmt);
    }

    public static Integer getDayOfMonth(Date d, boolean gmt) {
        return getField(Calendar.DAY_OF_MONTH, d, gmt);
    }

    public static Integer getHourOfDay(Date d, boolean gmt) {
        return getField(Calendar.HOUR_OF_DAY, d, gmt);
    }

    public static Integer getMinute(Date d, boolean gmt) {
        return getField(Calendar.MINUTE, d, gmt);
    }

    public static Integer getSecond(Date d, boolean gmt) {
        return getField(Calendar.SECOND, d, gmt);
    }

    public static DateFormat getDateFormat(String format) {
        return getDateFormat(format, true);
    }

    public static DateFormat getDateFormat(String format, boolean gmt) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        if (gmt) {
            sdf.setTimeZone(gmtCalendar.getTimeZone());
        }
        return sdf;
    }

    public static String formatDate(Date d, String format, boolean gmt) {
        if (d == null) {
            return null;
        }
        return getDateFormat(format, gmt).format(d);
    }

    public static String formatDate(Date d, String format) {
        return formatDate(d, format, true);
    }
    public static final String DATE_FORMAT_DMY = "dd/MM/yyyy";
    public static final String TIME_FORMAT_HMS = "HH:mm:ss";

    public static String formatDate(Date d) {
        return formatDate(d, DATE_FORMAT_DMY);
    }

    public static String formatTime(Date d) {
        return formatDate(d, TIME_FORMAT_HMS);
    }

    public static Date getStartOfDay(Date d, boolean gmt) {
        return encodeDate(getYear(d, gmt), getMonth(d, gmt), getDayOfMonth(d, gmt), 0, 0, 0, gmt);
    }

    public static Date getEndOfDay(Date d, boolean gmt) {
        return encodeDate(getYear(d, gmt), getMonth(d, gmt), getDayOfMonth(d, gmt), 23, 59, 59, gmt);
    }

    /**
     *
     * @param year
     * @param month 1,2,3...12 based month
     * @param day
     * @param hour
     * @param minute
     * @param sec
     * @param gmt
     * @return
     */
    public static Date encodeDate(Integer year, Integer month, Integer day, Integer hour, Integer minute, Integer sec, boolean gmt) {
        return encodeDate(year, month, day, hour, minute, sec, 0, gmt);
    }

    /**
     * encode date
     *
     * @param year
     * @param month
     * @param day
     * @param hour
     * @param minute
     * @param sec
     * @param ms
     * @param gmt
     * @return
     */
    public static Date encodeDate(Integer year, Integer month, Integer day, Integer hour, Integer minute, Integer sec, Integer ms, boolean gmt) {
        if (year == null || month == null || day == null || hour == null || minute == null || sec == null || ms == null) {
            return null;
        }
        // Code for using ZonedDateTime instead of Calendar:
        //ZonedDateTime zdt = ZonedDateTime.of(year, month, day, hour, minute, sec, ms * 1000, utcZoneId);
        //return Date.from(zdt.toInstant());
        Calendar cl = getCalendar(gmt);
        cl.set(Calendar.YEAR, year);
        cl.set(Calendar.MONTH, month - 1); // converts to 0,1,2...12-1
        cl.set(Calendar.DAY_OF_MONTH, day);
        cl.set(Calendar.HOUR_OF_DAY, hour);
        cl.set(Calendar.MINUTE, minute);
        cl.set(Calendar.SECOND, sec);
        cl.set(Calendar.MILLISECOND, ms);
        return cl.getTime();
        // Removed following bottleneck. Found by evaluation trial of YourKit Java Profiler.
        /*try {
         return getDateFormat("yyyyMMddHHmmss", true).parse(String.format("%04d%02d%02d%02d%02d%02d", year, month, day, hour, minute, sec));
         } catch (ParseException ex) {
         return null;
         }*/
    }

    public static Date encodeDate(Integer year, Integer month, Integer day, Integer hour, Integer minute, Integer sec) {
        return encodeDate(year, month, day, hour, minute, sec, 0, true);
    }

    public static Date encodeDate(int year, int month, int day) {
        return encodeDate(year, month, day, true);
    }

    public static Date encodeDate(int year, int month, int day, boolean gmt) {
        return encodeDate(year, month, day, 0, 0, 0, 0, gmt);
    }

    public static LocalDateTime encodeLocalDateTime(Date date, Date time) {
        if (date == null || time == null) {
            return null;
        }
        return getLocalTime(time).atDate(getLocalDate(date));
    }
    public static Date encodeDate(Date date, Date time) {
        if (date == null || time == null) {
            return null;
        }
        return Date.from(encodeLocalDateTime(date, time).toInstant(ZoneOffset.UTC));
        /*Integer year = date != null ? getYear(date, true) : 1900;
        Integer month = date != null ? getMonth(date, true) : 1;
        Integer day = date != null ? getDayOfMonth(date, true) : 1;
        Integer hour = time != null ? getHourOfDay(time, true) : 0;
        Integer minute = time != null ? getMinute(time, true) : 0;
        Integer sec = time != null ? getSecond(time, true) : 0;
        return encodeDate(year, month, day, hour, minute, sec, 0, true);*/
    }

    private static final String DEFAULT_DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String DATE_TIME_WITH_MS_PATTERN = DATE_FORMAT_DMY + " " + TIME_FORMAT_HMS + ".SSS";
    private static final DateTimeFormatter DMY_FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT_DMY);
    private static final DateFormat DEFAULT_DATE_TIME_FORMAT = getDateFormat(DEFAULT_DATE_TIME_PATTERN, true);
    private static final DateFormat DATE_TIME_WITH_MS_FORMAT = getDateFormat(DATE_TIME_WITH_MS_PATTERN, true);

    public static DateFormat getDefaultDateTimeFormat() {
        return DEFAULT_DATE_TIME_FORMAT;
    }

    public static DateFormat getDateTimeWithMSFormat() {
        return DATE_TIME_WITH_MS_FORMAT;
    }

    /**
     * return parse standard date+time to date (i.e. from Luf20 report time)
     *
     * @param s
     * @return
     */
    public static Date strToDateTime(String s) {
        try {
            return getDefaultDateTimeFormat().parse(s);
        } catch (ParseException ex) {
            return null;
        }
    }

    public static LocalDate strToLocalDate(String s) {
        return strToLocalDate(s, "yyyy-MM-dd");
    }

    public static LocalDate strToLocalDate(String s, String format) {
        try {
            if (s == null) {
                return null;
            }
            return LocalDate.parse(s, DateTimeFormatter.ofPattern(format));
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    public static Date strToDateTimeWithMS(String s) {
        try {
            return getDateTimeWithMSFormat().parse(s);
        } catch (ParseException ex) {
            return null;
        }
    }

    public static Date strToDate(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        String sep = "";
        if (0 <= s.indexOf("-")) {
            sep = "-";
        } else if (0 <= s.indexOf(".")) {
            sep = "\\.";
        } else if (0 <= s.indexOf("/")) {
            sep = "/";
        }
        boolean hassep = !sep.isEmpty();
        Integer dd = null, mm = null, yyyy = null;
        if (hassep) {
            String[] parts = s.split(sep);
            dd = Conversion.safeStringtoIntegerNULL(parts[0]);
            mm = Conversion.safeStringtoIntegerNULL(parts[1]);
            if (parts.length == 3) {
                yyyy = Conversion.safeStringtoIntegerNULL(parts[2]);
            }
        } else {
            if (s.length() > 4) {
                yyyy = Conversion.safeStringtoInteger(s.substring(4, s.length()));
            }
            if (s.length() > 2) {
                mm = Conversion.safeStringtoInteger(s.substring(2, Math.min(4, s.length())));
            }
            dd = Conversion.safeStringtoInteger(s.substring(0, Math.min(2, s.length())));
        }
        if (dd == null) {
            dd = IMRdate.getDayOfMonth(new Date(), true);
        }
        if (mm == null) {
            mm = IMRdate.getMonth(new Date(), true);
        }
        if (yyyy == null) {
            yyyy = IMRdate.getYear(new Date(), true);
        }
        if (yyyy < 100) {
            yyyy += 2000; // pivot 2000 support simple year form.
        }
        return encodeDate(yyyy, mm, dd, true);
    }

    public static Date strToTime(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        String sep = "";
        if (0 <= s.indexOf(":")) {
            sep = ":";
        }
        boolean hassep = !sep.isEmpty();
        Integer hh = null, mm = null, ss = null, ms = 0;
        if (hassep) {
            String[] parts = s.split(sep);
            hh = Conversion.safeStringtoIntegerNULL(parts[0]);
            mm = Conversion.safeStringtoIntegerNULL(parts[1]);
            if (parts.length == 3) {
                String[] parts2 = parts[2].split("\\.");
                ss = Conversion.safeStringtoIntegerNULL(parts2[0]);
                if (parts2.length == 2) {
                    ms = Conversion.safeStringtoIntegerNULL(parts2[1]);
                }
            }
        } else {
            s = s.trim();
            switch(s.length()) {
                case 1:
                case 2:
                    hh = Conversion.safeStringtoInteger(s);
                    break;
                case 3:
                    hh = Conversion.safeStringtoInteger(s.substring(0, 1));
                    mm = Conversion.safeStringtoInteger(s.substring(1, 3));
                    break;
                case 4:
                    hh = Conversion.safeStringtoInteger(s.substring(0, 2));
                    mm = Conversion.safeStringtoInteger(s.substring(2, 4));
                    break;
                case 5:
                    hh = Conversion.safeStringtoInteger(s.substring(0, 1));
                    mm = Conversion.safeStringtoInteger(s.substring(1, 3));
                    ss = Conversion.safeStringtoInteger(s.substring(3, 5));
                    break;
                case 6:
                    hh = Conversion.safeStringtoInteger(s.substring(0, 2));
                    mm = Conversion.safeStringtoInteger(s.substring(2, 4));
                    ss = Conversion.safeStringtoInteger(s.substring(4, 6));
                    break;
            }
        }
        if (hh == null) {
            hh = 0;
        }
        if (mm == null) {
            mm = 0;
        }
        if (ss == null) {
            ss = 0;
        }
        return encodeDate(1900, 1, 1, hh, mm, ss, ms, true);
    }

    /**
     * Returns true if a given date is valid in a period of time where start and
     * end dates are given.
     *
     * @param from - is start date.
     * @param to - is end date.
     * @param validate - is date to validate.
     */
    public static boolean isValidDate(Date from, Date to, Date validate) {
        boolean isValid = false;

        if (from != null && to != null && validate != null) {
            if ((from.before(validate) && validate.before(to)) || from.equals(validate) || to.equals(validate)) {
                isValid = true;
            }
        }

        return isValid;
    }

    public static int minutesDiff(Date d1, Date d2) {
        return (int) minutesDiffD(d1, d2);
    }

    public static double minutesDiffD(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            return 0;
        }
        return (d2.getTime() - d1.getTime()) / 60000;
    }

    public static Integer daysBetween(Date d1, Date d2) {
        if (d1 == null || d2 == null) {
            return null;
        }
        return Math.abs((int) ((d2.getTime() - d1.getTime()) / (1000 * 60 * 60 * 24)));
    }

    public static String hoursAndMinutesDiff(Date d1, Date d2) {
        int minutes = minutesDiff(d1, d2);
        return String.format("%02d:%02d", (int) (minutes / 60), minutes % 60);
    }

    /**
     * Calculate if a given date is daytime according to nautical official
     * zenith (90.5) (+-15 minutes - ICES standard) and by using known
     * SunSet/Rise algorithm
     *
     * @param datetime the time to decide
     * @param lat latitude
     * @param lon longitude
     * @param extendedDayMinutes
     * @return
     */
    public static Boolean isDayTime(Date datetime, double lat, double lon) {
        return isDayTimeByZenith(datetime, lat, lon, new Zenith(90.5), 15d, false);
    }

    public static Boolean isDayTimeByZenith(Date datetime, double lat, double lon, Zenith zenith, double extendedDayMinutes, boolean out) {
        SolarEventCalculator sec = new SolarEventCalculator(new Location(lat, lon), "GMT+0000");
        if (out) {
            System.out.print("Lat: " + lat + ", Lon:" + lon + ", zenith=" + zenith.degrees() + ", ");
        }
        Calendar cl = getCalendar(true);

        int year = IMRdate.getYear(datetime, true);
        int month = IMRdate.getMonth(datetime, true);
        int dayOfMonth = IMRdate.getDayOfMonth(datetime, true);
        cl.set(year, month - 1, dayOfMonth);
        Calendar crise = sec.computeSunriseCalendar(zenith, cl);

        Calendar cset = sec.computeSunsetCalendar(zenith, cl);
        if (crise == null || cset == null) {
            // For the non-shift situation (no set or rise) Compute roughly the state by equinoxes and latitude. (This is 99% ok)
            Boolean inTheMiddleOfYear = datetime.after(IMRdate.encodeDate(year, 3, 20)) && datetime.before(IMRdate.encodeDate(year, 9, 22));
            Boolean isDay = inTheMiddleOfYear && lat > 0;
            if (out) {
                System.out.println(isDay ? "Day" : "Night");
            }
            return isDay;
        }
        Date srise = crise.getTime();
        Date sset = cset.getTime();

        // Test the date time against limits on sset and srise:
        if (Math.abs(IMRdate.minutesDiffD(datetime, srise)) < extendedDayMinutes
                || Math.abs(IMRdate.minutesDiffD(datetime, sset)) < extendedDayMinutes) {
            return true;
        }
        // Test the date time against sset and srise
        if (sset.before(srise)) {
            if (out) {
                System.out.println("Day before " + IMRdate.formatTime(sset) + " and after " + IMRdate.formatTime(srise));
            }
            return datetime.before(sset) && datetime.after(sset);
        } else {
            if (out) {
                System.out.println("Day after " + IMRdate.formatTime(srise) + " and before " + IMRdate.formatTime(sset));
            }
            return datetime.after(srise) && datetime.before(sset);
        }
    }

    public static Date add(Date d, int field, int amount) {
        Calendar cal = getCalendar(true);
        if (d == null) {
            return null;
        }
        cal.setTime(d);
        cal.add(field, amount);
        return cal.getTime();
    }

    public static Integer getQuarter(Date d) {
        return getQuarter(getMonth(d, true));
    }

    public static Integer getQuarter(Integer month) {
        return month == null ? null : (month - 1) / 3 + 1;
    }

    public static Integer getWeek(Date d) {
        if (d == null) {
            return null;
        }
        return getField(Calendar.WEEK_OF_YEAR, d, true);
    }

    public static Boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR)
                && cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    public static String encodeDateStr(Integer dd, Integer mm, Integer YYYY) {
        StringBuilder sb = new StringBuilder();
        if (dd != null) {
            sb.append(String.format("%02d", dd));
        }
        if (mm != null) {
            sb.append("/").append(String.format("%02d", mm));
        }
        if (YYYY != null) {
            sb.append("/").append(YYYY);
        }
        return sb.toString();
    }

    /**
     * Get actual maximum day of month. If year == null, ensure 29 days is
     * returned for february
     *
     * @param m
     * @param year
     * @return
     */
    public static Integer getActualMaximumDayOfMonth(Integer m, Integer year) {
        if (m == null) {
            return null;
        }
        Calendar cl = IMRdate.getCalendar(true);
        cl.set(Calendar.YEAR, year == null ? 2016 : year);
        cl.set(Calendar.MONTH, m - 1);
        return cl.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    /**
     * Database contains date redundancy. In most udp list cases 01/01/1900 is
     * an alias for no beginning determined 01/01/7777 is an alias for no ending
     * determined Those 2 cases wraps to null, which is better code for
     * indetermination
     *
     * @param d
     * @return
     */
    public static Date checkDateRedundancy(Date d) {
        if (d == null) {
            return null;
        }
        Date d1 = IMRdate.encodeDate(1900, 1, 2);
        if (d.before(d1)) {
            return null;
        }
        Date d2 = IMRdate.encodeDate(7000, 1, 1);
        if (d.after(d2)) {
            return null;
        }
        return d;
    }

    /**
     * converter between instant util date (epoch based) and java 8 zoned date
     * time.
     *
     * @param d
     * @return
     */
    public static ZonedDateTime getZonedDateTime(Date d) {
        if (d == null) {
            return null;
        }
        return ZonedDateTime.ofInstant(d.toInstant(), utcZoneId);
    }

    public static LocalDate getLocalDate(Date d) {
        if (d == null) {
            return null;
        }
        return getZonedDateTime(d).toLocalDate();
    }

    public static LocalDateTime getLocalDateTime(Date d) {
        if (d == null) {
            return null;
        }
        return getZonedDateTime(d).toLocalDateTime();
    }

    public static LocalTime getLocalTime(Date d) {
        if (d == null) {
            return null;
        }
        return getZonedDateTime(d).toLocalTime();
    }

    public static Boolean isBetween(LocalDate d, LocalDate from, LocalDate to) {
        if (d == null || from == null || to == null) {
            return false;
        }
        return d.equals(from) || d.equals(to) || (d.isAfter(from) && d.isBefore(to));
    }

    /**
     * converter between java 8 zoned date time and instant util date (epoch
     * based).
     *
     * @param d
     * @return
     */
    public static Date getDate(ZonedDateTime d) {
        if (d == null) {
            return null;
        }
        return Date.from(d.toInstant());
    }

    public static Boolean overlap(LocalDate f1, LocalDate t1, LocalDate f2, LocalDate t2) {
        if (f1 == null) {
            f1 = LocalDate.of(0, Month.JANUARY, 1);
        }
        if (f2 == null) {
            f2 = LocalDate.of(0, Month.JANUARY, 1);
        }
        if (t1 == null) {
            t1 = LocalDate.of(9999, Month.JANUARY, 1);
        }
        if (t2 == null) {
            t2 = LocalDate.of(9999, Month.JANUARY, 1);
        }
        return f2.isAfter(f1) && (f2.isBefore(t1) || f2.isEqual(t1))
                || (t2.isAfter(f1) || t2.isEqual(f1)) && t2.isBefore(t1)
                || (f2.isBefore(f1) || f2.isEqual(f1)) && (t2.isAfter(t1) || t2.isEqual(t1));
    }

    public static LocalDate maxDate(LocalDate d1, LocalDate d2) {
        if (d1 == null || d2 == null) {
            return null;
        }
        return d1.isBefore(d2) ? d2 : d1;
    }

    public static Date asDate(LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(utcZoneId).toInstant());
    }

    public static Date asTime(LocalDate localDate, LocalTime localTime) {
        Instant instant = localTime.atDate(localDate).atZone(utcZoneId).toInstant();
        return Date.from(instant);
    }

    /**
     * converts an instant to
     *
     * @param in
     * @return
     */
    public static String instantToStr(Instant in) {
        try {
            return in != null ? in.toString() : null;
        } catch (DateTimeException e) {
            return null;
        }
    }

    public static Instant strToInstant(String str) {
        try {
            return str != null ? Instant.parse(str) : null;
        } catch (DateTimeException e) {
            return null;
        }
    }

    public static LocalDateTime instantToLocalDateTime(Instant in) {
        try {
            return in != null ? LocalDateTime.ofInstant(in, ZoneOffset.UTC) : null;
        } catch (DateTimeException e) {
            return null;
        }
    }

    public static LocalDateTime dateTimeStrToLocalDateTime(String iso8601Str) {
        return instantToLocalDateTime(strToInstant(iso8601Str));
    }

    public static LocalDate dateTimeStrToLocalDate(String iso8601Str) {
        LocalDateTime ldt = dateTimeStrToLocalDateTime(iso8601Str);
        return ldt != null ? ldt.toLocalDate() : null;
    }

    public static String dateTimeStrToLocalDateStr(String iso8601Str, DateTimeFormatter dtf) {
        try {
            LocalDate ldt = dateTimeStrToLocalDate(iso8601Str);
            return ldt != null ? dtf != null ? ldt.format(dtf) : ldt.toString() : null;
        } catch (DateTimeException e) {
            return null;
        }
    }

    /**
     * Converts a ISO 8601 date time string to a DMY Date String
     *
     * @param iso8601Str
     * @param dtf
     * @return
     */
    public static String dateTimeStrToLocalDateStrDMY(String iso8601Str) {
        return dateTimeStrToLocalDateStr(iso8601Str, DMY_FORMATTER);
    }
}
