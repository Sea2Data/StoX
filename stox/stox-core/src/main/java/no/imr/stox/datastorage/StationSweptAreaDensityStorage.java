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
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.MissionBO;
import no.imr.sea2data.imrbase.math.Calc;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.sea2data.imrbase.util.ExportUtil;
import no.imr.sea2data.imrbase.util.ImrIO;
import no.imr.stox.bo.SampleUnitBO;
import no.imr.stox.bo.StationSweptAreaDensityBO;
import no.imr.stox.functions.utils.BioticUtils;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.ReflectionUtil;

/**
 *
 * @author aasmunds
 */
public class StationSweptAreaDensityStorage extends FileDataStorage {

    @Override
    public <T> void asTable(T data, Integer level, Writer wr, Boolean withUnits) {
        asTable((StationSweptAreaDensityBO) data, wr);
    }

    public void asTable(StationSweptAreaDensityBO data, Writer wr) {
        List<String> specHdrs = data.getDensity().getData().getSortedKeys();
        MatrixBO edsuPsu = data.getSampleUnit().getEDSUPSU();
        List<Field> missionMethods = ReflectionUtil.getFields(MissionType.class);
        List<Field> fsMethods = ReflectionUtil.getFields(FishstationType.class);
        String hdr = ExportUtil.tabbed(
                missionMethods.stream().map(Field::getName).collect(Collectors.joining("\t")),
                fsMethods.stream().map(Field::getName).collect(Collectors.joining("\t")),
                specHdrs.stream().collect(Collectors.joining("\t"))
        );
        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(hdr));

        data.getSampleUnit().getPSUStratum().getSortedRowKeys().forEach(psu -> {
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
                MissionBO m = fs.getMission();
                String s = ExportUtil.tabbed(
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
                                    List<String> layers = data.getDensity().getData().getGroupRowColKeys(spec, psu);
                                    if (layers.size() != 1) {
                                        return null;
                                    }
                                    MatrixBO densitiesOverLength = data.getDensity().getData().getGroupRowColValueAsMatrix(spec, psu, layers.get(0));
                                    if (densitiesOverLength == null) {
                                        return null;
                                    }
                                    Double sum = densitiesOverLength.getSum();
                                    if (sum == null) {
                                        return null;
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
