/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.processdata;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.MissionBO;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrbase.util.IMRdate;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.bo.landing.SluttSeddel;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.CovariateUtils;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.log.ILogger;

/**
 *
 * @author aasmunds
 */
public class DefineTemporal extends AbstractFunction {

    LocalDate minDate = null;
    LocalDate maxDate = null;

    /**
     * define temporal covariates
     *
     * @param input contains Polygon file name DataTypeDescription.txt
     * @return
     */
    @Override
    public Object perform(Map<String, Object> input) {
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_DEFINETEMPORAL_PROCESSDATA);
        String defMethod = (String) input.get(Functions.PM_DEFINETEMPORAL_DEFINITIONMETHOD);
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        // Default handling (Define by given time interval:
        String sourceType = (String) input.get(Functions.PM_DEFINETEMPORAL_SOURCETYPE);
        String timeInterval = (String) input.get(Functions.PM_DEFINETEMPORAL_TIMEINTERVAL);
        Boolean seasonal = Conversion.safeObjectToBoolean((Boolean) input.get(Functions.PM_DEFINETEMPORAL_SEASONAL));
        List<SluttSeddel> landingData = (List) input.get(Functions.PM_DEFINETEMPORAL_LANDINGDATA);
        List<MissionBO> bioticData = (List) input.get(Functions.PM_DEFINETEMPORAL_BIOTICDATA);
        String covariateType = (String) input.get(Functions.PM_DEFINETEMPORAL_COVARIATETYPE);

        // Cov param
        MatrixBO covParam = AbndEstProcessDataUtil.getCovParam(pd);
        MatrixBO m = covParam.getRowValueAsMatrix(AbndEstProcessDataUtil.TABLE_TEMPORAL);
        if (m != null) {
            m.clear(); // Clear cov param
        }
        if (sourceType.equals(Functions.SOURCETYPE_BIOTIC)) {
            covParam.setRowColValue(AbndEstProcessDataUtil.TABLE_TEMPORAL, Functions.PM_DEFINETEMPORAL_COVARIATETYPE, covariateType);
        }

        // Covariate
        MatrixBO covM = AbndEstProcessDataUtil.getTemporal(pd);
        if (defMethod.equals(Functions.DEFINITIONMETHOD_USEPROCESSDATA)) {
            // Use existing, do not read from file.
            return pd;
        }
        m = covM.getRowValueAsMatrix(sourceType);
        if (m != null) {
            m.clear();
        }
        if (defMethod.equals(Functions.DEFINITIONMETHOD_COPYFROMLANDING)) {
            if (sourceType.equals(Functions.SOURCETYPE_LANDING)) {
                logger.error("Cannot inherit from landing when sourcetype=landing.\n", null);
            }
            // Copying cov key and definition from landing to biotic
            covM.getRowColKeys(Functions.SOURCETYPE_LANDING).stream().forEach((covKey) -> {
                covM.setRowColValue(sourceType, covKey, covM.getRowColValue(Functions.SOURCETYPE_LANDING, covKey));
            });
        } else if (defMethod.equals(Functions.DEFINITIONMETHOD_USEDATA)) {
            Set<String> covs = new HashSet<>();
            minDate = null;
            maxDate = null;
            if (seasonal) {
                switch (timeInterval) {
                    case Functions.COVARIATETIMEINTERVAL_PERIOD:
                        break;
                    case Functions.COVARIATETIMEINTERVAL_YEAR:
                        logger.error("Timeinterval " + timeInterval + " cannot be seasonal", null);
                        return null;
                    case Functions.COVARIATETIMEINTERVAL_QUARTER:
                    case Functions.COVARIATETIMEINTERVAL_MONTH:
                    case Functions.COVARIATETIMEINTERVAL_WEEK:
                        String seasonType = CovariateUtils.getSeasonTypeByTimeInterval(timeInterval);
                        Integer maxSeason = CovariateUtils.getMaxSeasonByTimeInterval(timeInterval);
                        if (seasonType != null && maxSeason != null) {
                            for (Integer season = 1; season <= maxSeason; season++) {
                                covs.add(seasonType + season);
                            }
                        }
                        break;
                }
            } else if (sourceType.equals(Functions.SOURCETYPE_LANDING)) {
                if (landingData != null) {
                    for (SluttSeddel sl : landingData) {
                        String cov = getCovariateFromDate(IMRdate.getLocalDate(sl.getSisteFangstDato()), timeInterval, seasonal);
                        if (cov != null) {
                            covs.add(cov);
                        }
                    }
                }
            } else if (sourceType.equals(Functions.SOURCETYPE_BIOTIC)) {
                if (bioticData != null) {
                    for (MissionBO ms : bioticData) {
                        for (FishstationBO fs : ms.getFishstationBOs()) {
                            String cov = getCovariateFromDate(fs.bo().getStationstartdate(), timeInterval, seasonal);
                            if (cov != null) {
                                covs.add(cov);
                            }
                        }
                    }
                }
            }
            switch (timeInterval) {
                case Functions.COVARIATETIMEINTERVAL_PERIOD:
                    // Add period
                    if (seasonal) {
                        covM.setRowColValue(sourceType, "P1", "01/01-31/12");
                    } else if (minDate != null && maxDate != null) {
                        covM.setRowColValue(sourceType, IMRdate.getYear(minDate) + ".P1", IMRdate.formatDate(minDate) + "-" + IMRdate.formatDate(maxDate));
                    }
                    break;
                default:
                    List<String> covsList = new ArrayList<>(covs);
                    if (!seasonal) {
                        sortCovariates(covsList);
                        fillInMissingIntervals(timeInterval, covsList);
                    }
                    // Create covariate definitions
                    for (int i = 0; i < covsList.size(); i++) {
                        String def = null;
                        String cov = covsList.get(i);//(i + 1) + "";
                        Integer covYear = seasonal ? null : Conversion.safeSubstringToIntegerNULL(cov, 0, 4);
                        switch (timeInterval) {
                            // Periodic definitions
                            case Functions.COVARIATETIMEINTERVAL_YEAR:
                                def = IMRdate.encodeDateStr(1, 1, covYear) + "-" + IMRdate.encodeDateStr(31, 12, covYear);
                                break;
                            // Annual season definitions
                            case Functions.COVARIATETIMEINTERVAL_QUARTER:
                                Integer qrt = CovariateUtils.getSeasonByCovariate(cov);
                                if (qrt >= 1 && qrt <= 4) {
                                    Integer startMonth = (qrt - 1) * 3 + 1;
                                    Integer endMonth = (qrt - 1) * 3 + 3;
                                    def = IMRdate.encodeDateStr(1, startMonth, covYear) + "-" + IMRdate.encodeDateStr(
                                            IMRdate.getActualMaximumDayOfMonth(endMonth, covYear), endMonth, covYear);
                                }
                                break;
                            case Functions.COVARIATETIMEINTERVAL_MONTH:
                                Integer month = CovariateUtils.getSeasonByCovariate(cov);
                                if (month >= 1 && month <= 12) {
                                    def = IMRdate.encodeDateStr(1, month, covYear) + "-" + IMRdate.encodeDateStr(
                                            IMRdate.getActualMaximumDayOfMonth(month, covYear), month, covYear);
                                }
                                break;
                            case Functions.COVARIATETIMEINTERVAL_WEEK:
                                Integer week = CovariateUtils.getSeasonByCovariate(cov);
                                if (week >= 1 && week <= 52) {
                                    def = "Week " + week;
                                }
                                break;
                        }
                        if (def == null) {
                            continue;
                        }
                        covM.setRowColValue(sourceType, cov, def);
                    }
            }
        }
        return pd;
    }

    private void fillInMissingIntervals(String timeInterval, List<String> covsList) {
        if (timeInterval.equals(Functions.COVARIATETIMEINTERVAL_PERIOD)) {
            return;
        }
        // Assume the covsList is sorted
        String startCov = covsList.get(0);
        String endCov = covsList.get(covsList.size() - 1);
        Integer yearStart = Conversion.safeStringtoIntegerNULL(Conversion.safeSubstring(startCov, 0, 4));
        Integer yearStop = Conversion.safeStringtoIntegerNULL(Conversion.safeSubstring(endCov, 0, 4));
        if (yearStart == null || yearStop == null) {
            return;
        }
        boolean needSort = false;
        switch (timeInterval) {
            case Functions.COVARIATETIMEINTERVAL_YEAR:
                for (Integer year = yearStart; year <= yearStop; year++) {
                    String c = year + "";
                    if (covsList.indexOf(c) < 0) {
                        covsList.add(c);
                        needSort = true;
                    }
                }
                break;
            case Functions.COVARIATETIMEINTERVAL_QUARTER:
            case Functions.COVARIATETIMEINTERVAL_MONTH:
            case Functions.COVARIATETIMEINTERVAL_WEEK:
                String sIntv = CovariateUtils.getSeasonTypeByTimeInterval(timeInterval);
                if (sIntv != null) {
                    for (Integer year = yearStart; year <= yearStop; year++) {
                        Integer iStart = year.equals(yearStart) ? CovariateUtils.getSeasonByCovariateAndSeasonType(startCov, sIntv) : 1;
                        Integer iEnd = year.equals(yearStop) ? CovariateUtils.getSeasonByCovariateAndSeasonType(endCov, sIntv) : (sIntv.equals("Q") ? 4 : sIntv.equals("M") ? 12 : 52);
                        for (Integer intv = iStart; intv <= iEnd; intv++) {
                            String c = year + "." + sIntv + intv;
                            if (covsList.indexOf(c) < 0) {
                                covsList.add(c);
                                needSort = true;
                            }
                        }
                    }
                }
        }
        if (needSort) {
            sortCovariates(covsList);
        }
    }

    private String getCovariateFromDate(LocalDate d, String timeInterval, Boolean seasonal) {
        if (d == null) {
            return null;
        }
        String cov = null;
        String year = Conversion.safeIntegertoString(IMRdate.getYear(d));
        Integer season = CovariateUtils.getSeasonByDate(d, timeInterval);
        switch (timeInterval) {
            case Functions.COVARIATETIMEINTERVAL_YEAR:
                return year;
            case Functions.COVARIATETIMEINTERVAL_QUARTER:
            case Functions.COVARIATETIMEINTERVAL_MONTH:
            case Functions.COVARIATETIMEINTERVAL_WEEK:
                String seasonType = CovariateUtils.getSeasonTypeByTimeInterval(timeInterval);
                String seasonS = Conversion.safeIntegertoString(season);
                if (seasonType != null) {
                    return (seasonal ? "" : year + ".") + seasonType + seasonS;
                }
                break;
            case Functions.COVARIATETIMEINTERVAL_PERIOD:
                if (minDate == null) {
                    minDate = d;
                } else {
                    minDate = minDate.isBefore(d) ? minDate : d;
                }
                if (maxDate == null) {
                    maxDate = d;
                } else {
                    maxDate = maxDate.isAfter(d) ? maxDate : d;
                }
                break;
        }
        return cov;
    }

    private void sortCovariates(List<String> covsList) {
        Collections.sort(covsList, (String o1, String o2) -> {
            Integer year1 = o1.contains(".") ? Conversion.safeSubstringToIntegerNULL(o1, 0, 4) : null;
            Integer year2 = o2.contains(".") ? Conversion.safeSubstringToIntegerNULL(o2, 0, 4) : null;
            Integer season1 = CovariateUtils.getSeasonByCovariate(o1);
            Integer season2 = CovariateUtils.getSeasonByCovariate(o2);
            if (year1 == null || year2 == null) {
                return 0;
            }
            int res = year1.compareTo(year2);
            if (res != 0) {
                return res;
            }
            if (season1 == null || season2 == null) {
                return 0;
            }
            return season1.compareTo(season2);
        });
    }
}
