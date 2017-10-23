/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package convert;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiPolygon;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.imrbase.map.ILatLonEvent;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.sea2data.imrmap.utils.JTSUtils;
import no.imr.stox.bo.ProcessDataBO;
import static no.imr.stox.factory.FactoryUtil.acquireProject;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.BioticUtils;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.ProjectUtils;
import no.imr.stox.model.IModel;
import no.imr.stox.model.IProcess;
import no.imr.stox.model.IProject;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
@Ignore
public class ConvertBarentsHavTransects {

    @Test
    public void test() {
        //convertVinterTokt();
        IntStream
                .range(1994, 2013 + 1).boxed()
                .sorted(Collections.reverseOrder())
                .forEach(year -> {
                    //   convertKystTokt(year, 6, "Sei");
                    appendCatchability(year);
                });
    }

    public void appendCatchability(Integer year) {
        IProject prj = acquireProject("R:\\alle\\stox\\Akustikk Vintertokt\\Modified\\", "AkustikkTorskVinter" + year + "ed", null);
        IModel bl = prj.getBaseline();
        IProcess lDistPr = bl.getProcessByFunctionName(Functions.FN_STATIONLENGTHDIST);
        lDistPr.setParameterValue(Functions.PM_STATIONLENGTHDIST_LENGTHDISTTYPE, Functions.LENGTHDISTTYPE_NORMLENGHTDIST);
        IProcess rgrpPr = bl.getProcessByFunctionName(Functions.FN_REGROUPLENGTHDIST);
        IProcess cPr = prj.getBaseline().getProcessByFunctionName(Functions.FN_CATCHABILITY);
        if (cPr == null) {
            cPr = prj.getBaseline().insertProcess(Functions.FN_CATCHABILITY, Functions.FN_CATCHABILITY, prj.getBaseline().getProcessList().indexOf(rgrpPr) + 1);
        }
        cPr.setParameterProcessValue(Functions.PM_CATCHABILITY_LENGTHDIST, rgrpPr.getName()).
                setParameterValue(Functions.PM_CATCHABILITY_CATCHABILITYMETHOD, Functions.CATCHABILITYMETHOD_LENGTHDEPENDENTSWEEPWIDTH).
                setParameterValue(Functions.PM_CATCHABILITY_TABLE, ";5.91;0.43;15;62");
        IProcess relPr = prj.getBaseline().getProcessByFunctionName(Functions.FN_RELLENGTHDIST);
        if (relPr == null) {
            relPr = prj.getBaseline().insertProcess(Functions.FN_RELLENGTHDIST, Functions.FN_RELLENGTHDIST, prj.getBaseline().getProcessList().indexOf(cPr) + 1);
        }
        relPr.setParameterProcessValue(Functions.PM_RELLENGTHDIST_LENGTHDIST, rgrpPr.getName());
        IProcess bswPr = prj.getBaseline().getProcessByFunctionName(Functions.FN_BIOSTATIONWEIGHTING);
        bswPr.setParameterValue(Functions.PM_BIOSTATIONWEIGHTING_WEIGHTINGMETHOD, Functions.WEIGHTINGMETHOD_SUMWEIGHTEDCOUNT);

        IProcess totPr = prj.getBaseline().getProcessByFunctionName(Functions.FN_TOTALLENGTHDIST);
        totPr.setParameterProcessValue(Functions.PM_TOTALLENGTHDIST_LENGTHDIST, relPr.getName());
        prj.save();

    }

    public void convertKystTokt(Integer year, Integer areaColumn, String species) {
        try {
            String pName = "Akustikk" + species + "Kyst" + year;
            IProject pr = acquireProject(ProjectUtils.getSystemProjectRoot(), pName, null);
            IModel bl = pr.getBaseline();
            IProcess p = bl.getProcessByFunctionName(Functions.FN_FILTERBIOTIC);
            p.setParameterValue(Functions.PM_FILTERBIOTIC_FISHSTATIONEXPR, "fs.getLengthSampleCount('SEI') > 5");
            p = bl.getProcessByFunctionName(Functions.FN_STRATUMAREA);
            p.setParameterValue(Functions.PM_STRATUMAREA_AREAMETHOD, Functions.AREAMETHOD_ACCURATE);
            bl.run(1, pr.getBaseline().getProcessList().indexOf(pr.getBaseline().getProcessByFunctionName(Functions.FN_DEFINESTRATA)) + 1, Boolean.FALSE);
            Map<String, String> ycMap = Files.readAllLines(Paths.get("E:/SigbjørnMehl/Kysttokt/tokt.txt")).stream().sequential().skip(1)
                    .map(s -> {
                        String[] str = s.split("\t");
                        return new YearCode(Integer.valueOf(str[0]), str[1], str[7]);
                    })
                    .filter(yc -> yc.getYear().equals(year))
                    .collect(Collectors.toMap(YearCode::getCode, YearCode::getCruise));
            Set<String> includedStrata = Files.readAllLines(Paths.get("E:/SigbjørnMehl/Kysttokt/" + year + "_areal.txt")).stream().sequential().skip(1)
                    .map(s -> {
                        s = s.trim();
                        if (s.isEmpty()) {
                            return null;
                        }
                        String[] str = s.split("\t");
                        if (areaColumn > str.length - 1) {
                            System.out.println("");
                        }
                        return Integer.valueOf(str[areaColumn]) == 1 ? Util.getMissionStratumKey(Integer.valueOf(str[0]), str[3]) : null;
                    })
                    .filter(t -> t != null)
                    .collect(Collectors.toSet());
            List<Transect> tlist = Files.readAllLines(Paths.get("E:/SigbjørnMehl/Kysttokt/" + year + "_transekt.txt")).stream().sequential().skip(1)
                    .map(s -> {
                        String[] str = s.split("\t");
                        if (str.length < 7) {
                            return null;
                        }
                        Integer from = Integer.valueOf(str[5]);
                        Integer to = Integer.valueOf(str[6]);
                        if (from == 99999 || to == 99999) {
                            return null;
                        }
                        return new Transect(Integer.valueOf(str[0]), Integer.valueOf(str[1]), str[2], Integer.valueOf(str[1]), str[4].trim(), from, to);
                    })
                    .filter(t -> t != null)
                    .collect(Collectors.toList());
            Map<String, List<Integer>> asgMap = Files.readAllLines(Paths.get("E:/SigbjørnMehl/Kysttokt/" + year + "_trål.txt")).stream().sequential().skip(1)
                    .map(s -> {
                        String[] str = s.split("\t");
                        return str.length >= 4 && !str[3].isEmpty() ? new StratumAsg(Integer.valueOf(str[0]), str[2], Arrays.asList(Arrays.copyOfRange(str, 3, str.length))) : null;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toMap(StratumAsg::getStratumKey, StratumAsg::getSerialNo));
            ProcessDataBO pd = pr.getProcessData();
            MatrixBO polygons = AbndEstProcessDataUtil.getStratumPolygons(pd);
            polygons.getRowKeys().stream().forEach(stratum -> {
                polygons.setRowColValue(stratum, Functions.COL_POLVAR_INCLUDEINTOTAL, includedStrata.contains(stratum) ? "true" : "false");
            });

            AbndEstProcessDataUtil.setAssignmentResolution(pd, Functions.SAMPLEUNIT_PSU);
            MatrixBO edsuPsu = AbndEstProcessDataUtil.getEDSUPSUs(pd);
            MatrixBO psuStrata = AbndEstProcessDataUtil.getPSUStrata(pd);
            MatrixBO suAsg = AbndEstProcessDataUtil.getSUAssignments(pd);
            MatrixBO bAsg = AbndEstProcessDataUtil.getBioticAssignments(pd);
            psuStrata.clear();
            edsuPsu.clear();
            suAsg.clear();
            bAsg.clear();
            List<DistanceBO> distList = bl.getProcessByFunctionName(Functions.FN_FILTERACOUSTIC).getDataStorage().getData();
            List<FishstationBO> fList = bl.getProcessByFunctionName(Functions.FN_READBIOTICXML).getDataStorage().getData();
            Map<String, Set<Transect>> trgrp = tlist.stream().
                    collect(Collectors.groupingBy(Transect::getTransectKey, Collectors.toSet()));

            trgrp.entrySet().forEach(tre -> {
                tre.getValue().stream()
                        .filter(tr -> includedStrata.contains(tr.getMissionStratumKey()))
                        .forEach(tr -> {
                            int i = psuStrata.getRowKeys().size() + 1;
                            String asgId = "" + i;
                            String psu = "T" + i;
                            String stratum = tr.getMissionStratumKey();
                            if (!AbndEstProcessDataUtil.getStratumPolygons(pr.getProcessData()).getRowKeys().contains(stratum)) {
                                //System.out.println("Year " + year + " Stratum " + stratum + " not found in process data");
                                throw new RuntimeException("Stratum " + stratum + " not found in process data");
                            }
                            String cruise = ycMap.get(tr.getShip());
                            Integer startLog = tr.getFrom();
                            Integer toLog = tr.getTo();
                            psuStrata.setRowValue(psu, stratum);
                            List<DistanceBO> dTr = distList.stream().filter(d -> d.getCruise().equals(cruise) && startLog <= Math.round(d.getLog_start().doubleValue())
                                    && Math.round(d.getLog_start().doubleValue()) <= toLog).collect(Collectors.toList());
                            if (dTr.isEmpty()) {
                                throw new RuntimeException("Transect " + cruise + ":" + startLog + "-" + toLog + "not found in data");
                            }
                            // Check if used before

                            dTr.forEach(d -> {
                                AbndEstProcessDataUtil.setEDSUPSU(pr.getProcessData(), d.getKey(), psu);
                            });
                            List<Integer> serialNo = asgMap.get(stratum);
                            if (serialNo != null) {
                                suAsg.setRowColValue(psu, "1", asgId);
                                serialNo.stream().forEach(ser -> {
                                    if (ser < 10000 || ser > 99999) {
                                        throw new RuntimeException("Feil serienr " + ser);
                                    }
                                    FishstationBO bo = BioticUtils.findStationBySerialNo(fList, ser);
                                    if (bo != null) {
                                        bAsg.setRowColValue(asgId, bo.getKey(), 1);
                                    }
                                });
                            }
                        });
            });
            AbndEstProcessDataUtil.regroupAssignments(pd); // Optimize assignments
            bl.getProcessByFunctionName(Functions.FN_DEFINEACOUSTICPSU).setParameterValue(Functions.PM_DEFINEACOUSTICPSU_USEPROCESSDATA, String.valueOf(true));
            pr.save();
        } catch (IOException ex) {
            Logger.getLogger(ConvertBarentsHavTransects.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static String getStrataByDistance(ProcessDataBO pd, DistanceBO d) {
        MatrixBO stratumPlgs = AbndEstProcessDataUtil.getStratumPolygons(pd);
        for (String stratum : AbndEstProcessDataUtil.getStrata(pd)) {
            MultiPolygon stratumPol = (MultiPolygon) stratumPlgs.getRowColValue(stratum, Functions.COL_POLVAR_POLYGON);
            Coordinate dPos = new Coordinate(d.getLon_start(), d.getLat_start());
            if (JTSUtils.within(dPos, stratumPol)) {
                return stratum;
            }
        }
        return null;
    }

    private static void createTransect(ProcessDataBO pd, String stratum, List<DistanceBO> distList, Integer[] tr) {
        int limit = distList.size() > 0 && distList.get(0).getIntegrator_dist() > 4d ? 2 : 5;
        if (distList.size() >= limit) { // minimum 3 edsu per transect
            int transect = ++tr[0];
            String trKey = "T" + transect;
            AbndEstProcessDataUtil.setPSUStratum(pd, trKey, stratum);
            for (DistanceBO d : distList) {
                AbndEstProcessDataUtil.setEDSUPSU(pd, d.getKey(), trKey);
            }
        }
        distList.clear();
    }

    private static void convertVinterTokt() {
        Double radius = 2.5d;
        String pName = "AkustikkTorskVinter2007";
        IProject pr = acquireProject(ProjectUtils.getSystemProjectRoot(), pName, null);
        IModel bl = pr.getBaseline();
        bl.run(1, pr.getBaseline().getProcessList().indexOf(pr.getBaseline().getProcessByFunctionName(Functions.FN_DEFINESTRATA)) + 1, Boolean.FALSE);
        /**
         * Append process data transects from algorithm here
         */
        List<DistanceBO> aco = bl.getProcessByFunctionName(Functions.FN_FILTERACOUSTIC).getDataStorage().getData();
        List<FishstationBO> st = bl.getProcessByFunctionName(Functions.FN_READBIOTICXML).getDataStorage().getData();
        List<DistanceBO> acoSorted = aco.stream().sorted((d1, d2) -> {
            int res = d1.getCruise().compareTo(d2.getCruise());
            if (res == 0) {
                res = d1.getStart_time().compareTo(d2.getStart_time());
            }
            return res;
        }).collect(Collectors.toList());
        HashMap<DistanceBO, Coordinate> distPos = new HashMap<>();
        for (int i = 0; i < acoSorted.size(); i++) {
            DistanceBO p = acoSorted.get(i);
            Double lon1 = p.getStartLon();
            Double lat1 = p.getStartLat();
            if (lon1 == null | lat1 == null) {
                continue;
            }
            Double lon2 = p.getStopLon();
            Double lat2 = p.getStopLat();
            if (lon2 == null || lat2 == null) {
                if (i < acoSorted.size() - 1) {
                    // Use start of next
                    ILatLonEvent p1 = acoSorted.get(i + 1);
                    lon2 = p1.getStartLon();
                    lat2 = p1.getStartLat() + 0.000000001;
                } else {
                    // Last point use the point only
                    lon2 = lon1 + 0.000000001;
                    lat2 = lat1 + 0.000000001;
                }
            }
            if (lon2 == null) {
                continue;
            }
            distPos.put(p, new Coordinate((lon1 + lon2) * 0.5, (lat1 + lat2) * 0.5));
        }
        List<DistanceBO> acoSplit = aco
                .parallelStream()
                .filter(d -> {
                    return st.stream().filter(fs -> {
                        // return true if station makes the distance not account for in transects.
                        if (fs.getTrawlQuality() != null && fs.getTrawlQuality().equals("2")) {
                            // exception for trawling by registration
                            return false;
                        }
                        Coordinate fPos = new Coordinate(fs.getLongitudeStart(), fs.getLatitudeStart());
                        Coordinate dPos = distPos.get(d);
                        Double gcDist = JTSUtils.gcircledist(fPos, dPos);
                        // inside short radius (2-3 nm)
                        return gcDist < radius;
                    }).findAny().isPresent();
                })
                .collect(Collectors.toList());
        DistanceBO prev = null;
        String prevStrata = null;
        AbndEstProcessDataUtil.getEDSUPSUs(pr.getProcessData()).clear();
        AbndEstProcessDataUtil.getPSUStrata(pr.getProcessData()).clear();
        Integer[] tr = new Integer[]{0};
        List<DistanceBO> distList = new ArrayList<>();
        for (DistanceBO d : aco) {
            boolean isSplitDist = acoSplit.contains(d);
            String strata = getStrataByDistance(pr.getProcessData(), d);
            if (prevStrata != null && prev != null) {
                boolean newTransect;
                if (strata == null || !strata.equals(prevStrata)) {
                    newTransect = true;
                } else {
                    // strata == prevStrata
                    // Check distance to previous to check long breaks.
                    double fac = prev.getIntegrator_dist() != null ? prev.getIntegrator_dist() : 1;
                    Coordinate pPos = distPos.get(prev);
                    Coordinate dPos = distPos.get(d);
                    Double gcDist = JTSUtils.gcircledist(pPos, dPos);
                    newTransect = gcDist >= radius * fac;
                }
                if (!newTransect && isSplitDist) {
                    newTransect = true;
                }
                if (newTransect) {
                    createTransect(pr.getProcessData(), prevStrata, distList, tr);
                }
            }
            if (!isSplitDist && strata != null) {
                distList.add(d);
                prev = d;
            } else {
                prev = null;
            }
            prevStrata = strata;
        }
        // Fill in the rest
        if (!distList.isEmpty()) {
            createTransect(pr.getProcessData(), prevStrata, distList, tr);
        }
        // Set transect definition method to "use process data"
        bl.getProcessByFunctionName(Functions.FN_DEFINEACOUSTICPSU).setParameterValue(Functions.PM_DEFINEACOUSTICPSU_USEPROCESSDATA, String.valueOf(true));
        pr.save();
    }

}
