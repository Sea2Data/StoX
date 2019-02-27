/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.datastorage;

import BioticTypes.v3.FishstationType;
import BioticTypes.v3.MissionType;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.MissionBO;
import no.imr.sea2data.imrbase.math.Calc;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.sea2data.imrbase.util.ExportUtil;
import no.imr.sea2data.imrbase.util.ImrIO;
import no.imr.stox.bo.StationSpecCatDensityBO;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.BioticUtils;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.ReflectionUtil;

/**
 *
 * @author aasmunds
 */
public class StationSpecCatDensityStorage extends FileDataStorage {

    @Override
    public <T> void asTable(T data, Integer level, Writer wr, Boolean withUnits) {
        asTable((StationSpecCatDensityBO) data, wr);
    }

    public void asTable(StationSpecCatDensityBO data, Writer wr) {
        List<String> specHdrs = data.getDensity().getData().getKeys().stream()
                .sorted((s1, s2) -> {
                    Boolean s1FirstGroup = s1.equals(Functions.SPECCAT_EMPTYSPECCATREF) || s1.equals(Functions.SPECCAT_NOTINSPECVARREF);
                    Boolean s2FirstGroup = s2.equals(Functions.SPECCAT_EMPTYSPECCATREF) || s2.equals(Functions.SPECCAT_NOTINSPECVARREF);
                    int res = s2FirstGroup.compareTo(s1FirstGroup);
                    if (res == 0) {
                        res = s1.compareTo(s2);
                    }
                    return res;
                }).collect(Collectors.toList());
        MatrixBO edsuPsu = data.getProcessData().getMatrix(AbndEstProcessDataUtil.TABLE_EDSUPSU);
        MatrixBO psuStratum = data.getProcessData().getMatrix(AbndEstProcessDataUtil.TABLE_PSUSTRATUM);
        List<Field> missionMethods = ReflectionUtil.getFields(MissionType.class);
        List<Field> fsMethods = ReflectionUtil.getFields(FishstationType.class);
        String hdr = ExportUtil.tabbed(Functions.SAMPLEUNIT_STRATUM,
                missionMethods.stream().map(Field::getName).collect(Collectors.joining("\t")),
                fsMethods.stream().map(Field::getName).collect(Collectors.joining("\t")),
                specHdrs.stream().collect(Collectors.joining("\t"))
        );
        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(hdr));

        data.getProcessData().getMatrix(AbndEstProcessDataUtil.TABLE_PSUSTRATUM).getSortedRowKeys().forEach(psu -> {
            // For each psu
            // Check that the psu is connected by one edsu
            List<String> edsus = edsuPsu.getRowKeys().stream()
                    .filter(edsu -> Objects.equals(edsuPsu.getRowValue(edsu), psu))
                    .collect(Collectors.toList());
            if (edsus.size() == 1) {
                String edsu = edsus.get(0);
                // Assume that the edsu is a station
                String stationKey = edsu;
                FishstationBO fs = BioticUtils.findStation(data.getBioticData(), edsu);
                if (fs == null) {
                    return;
                }
                String stratum = (String) psuStratum.getRowValue(psu);

                MissionBO m = fs.getMission();
                String s = ExportUtil.tabbed(stratum,
                        missionMethods.stream()
                                .map(f -> ReflectionUtil.invoke(f, m.bo()))
                                .map(o -> o != null ? o.toString() : "")
                                .collect(Collectors.joining("\t")),
                        fsMethods.stream()
                                .map(f -> ReflectionUtil.invoke(f, fs.bo()))
                                .map(o -> o != null ? o.toString() : "")
                                .collect(Collectors.joining("\t")),
                        specHdrs.stream()
                                .map(spec -> {
                                    // The density is marked na if the underlying info is not fulfilled (catch vs count)
                                    boolean isna = fs.getCatchSampleBOs().stream().filter(c -> c.getSpecCat() != null && c.getSpecCat().equals(spec)
                                            && ((c.bo().getCatchweight() != null && c.bo().getCatchcount() == null)
                                            || (c.bo().getCatchweight() == null && c.bo().getCatchcount() != null))).count() > 0;
                                    if (isna) {
                                        return "NA";
                                    }
                                    List<String> layers = data.getDensity().getData().getGroupRowColKeys(spec, psu);
                                    if (layers.isEmpty()) {
                                        return 0;
                                    } else if (layers.size() > 1) {
                                        return "ERROR-LAYER>1"; // not supported.
                                    }
                                    MatrixBO densitiesOverLength = data.getDensity().getData().getGroupRowColValueAsMatrix(spec, psu, layers.get(0));
                                    if (densitiesOverLength == null) {
                                        return 0;
                                    }
                                    Double sum = densitiesOverLength.getSum();
                                    if (sum == null) {
                                        return 0;
                                    }
                                    return Calc.roundTo(sum, 1);
                                })
                                .map(o -> o != null ? o.toString() : "")
                                .collect(Collectors.joining("\t"))
                );
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(s));

            }
        });
        /*data.getEDSUPSU().getRowKeys().forEach((edsu) -> {
            String psu = (String) data.getEDSUPSU().getRowValue(edsu);
            String stratum = (String) data.getPSUStratum().getRowValue(psu);
            if (psu != null && stratum != null) {
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(stratum, psu, edsu)));
            }
        });*/
    }

}
