/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.processdata;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiPolygon;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.sea2data.imrmap.utils.JTSUtils;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.EchosounderUtils;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.log.ILogger;
import no.imr.stox.model.IModel;
import no.imr.stox.model.IProcess;

/**
 * TODO: what does this class do?
 *
 * @author aasmunds
 */
public class DefineAcousticPSU extends AbstractFunction {

    /**
     *
     * @param input contains Polygon file name
     * @return Matrix object of type POLYGON_MATRIX - see
     * DataTypeDescription.txt
     */
    @Override
    public Object perform(Map<String, Object> input) {
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_DEFINEACOUSTICPSU_PROCESSDATA);
        List<DistanceBO> acousticData = (List<DistanceBO>) input.get(Functions.PM_DEFINEACOUSTICPSU_ACOUSTICDATA);
        String definitionMethod = (String) input.get(Functions.PM_DEFINEACOUSTICPSU_DEFINITIONMETHOD);
        Boolean useProcessData = pd != null && Functions.ACOUSTICPSU_DEFINITIONMETHOD_USEPROCESSDATA.equals(definitionMethod);
        Boolean edsuToPsu = pd != null && Functions.ACOUSTICPSU_DEFINITIONMETHOD_EDSUTOPSU.equals(definitionMethod);
        if (!useProcessData) {
            // Clear the psu definitions.
            AbndEstProcessDataUtil.getEDSUPSUs(pd).clear();
            AbndEstProcessDataUtil.getPSUStrata(pd).clear();
        } 
        if (edsuToPsu) {
            MatrixBO stratumPlgs = AbndEstProcessDataUtil.getStratumPolygons(pd);
            int psu = 1;
            for (String stratum : stratumPlgs.getRowKeys()) {
                MultiPolygon stratumPol = (MultiPolygon) stratumPlgs.getRowColValue(stratum, Functions.COL_POLVAR_POLYGON);
                List<DistanceBO> fd = acousticData.parallelStream()
                        .filter(d -> {
                            Coordinate fPos = new Coordinate(d.getLon_start(), d.getLat_start());
                            return JTSUtils.within(fPos, stratumPol);
                        }).collect(Collectors.toList());
                for (DistanceBO fdd : fd) {
                    String psus = "E" + psu++;
                    AbndEstProcessDataUtil.setPSUStratum(pd, psus, stratum);
                    AbndEstProcessDataUtil.setEDSUPSU(pd, fdd.getKey(), psus);
                }
            }
        }

        // check EDSU-PSU keys up against acoustic data.
        String errorStr = AbndEstProcessDataUtil.getEDSUPSUs(pd).getRowKeys().parallelStream()
                .map(edsu -> EchosounderUtils.findDistance(acousticData, edsu) == null ? edsu : null)
                .filter(d -> d != null).findFirst().orElse(null);
        if (errorStr != null && !errorStr.isEmpty()) {
            logger.log("Warning: The following EDSU in the EDSUPSU processdata table is not found in the data:\n" + errorStr + ".");
        }
        // Break in gui at onEndProcess to redefine...
        return pd;
    }
}
