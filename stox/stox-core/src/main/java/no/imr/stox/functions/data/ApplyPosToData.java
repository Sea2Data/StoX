/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.data;

import java.awt.geom.Point2D;
import java.util.List;
import java.util.Map;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.stox.bo.landing.SluttSeddel;
import no.imr.stox.datastorage.BioticDataStorage;
import no.imr.stox.datastorage.IDataStorage;
import no.imr.stox.datastorage.LandingDataStorage;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.ProjectUtils;
import no.imr.stox.functions.utils.StratumUtils;

/**
 *
 * @author aasmunds
 */
public class ApplyPosToData extends AbstractFunction {

    String dataSource;

    @Override
    public Object perform(Map<String, Object> input) {
        String fileName = ProjectUtils.resolveParameterFileName((String) input.get(Functions.PM_APPLYPOSTODATA_FILENAME),
                (String) input.get(Functions.PM_PROJECTFOLDER));
        if (fileName == null) {
            return null;
        }
        MatrixBO posMap = StratumUtils.getAreaLocationPositionByFile(fileName);
        if (posMap == null) {
            return null;
        }
        dataSource = (String) input.get(Functions.PM_APPLYPOSTODATA_SOURCETYPE);
        if (dataSource == null) {
            return null;
        }
        List<SluttSeddel> landing = (List<SluttSeddel>) input.get(Functions.PM_APPLYPOSTODATA_LANDINGDATA);
        List<FishstationBO> biotic = (List) input.get(Functions.PM_APPLYPOSTODATA_BIOTICDATA);
        switch (dataSource) {
            case Functions.SOURCETYPE_LANDING:
                for (SluttSeddel sl : landing) {
                    if (sl.getFangstHomr() == null || sl.getFangstLok() == null) {
                        continue;
                    }
                    String area = sl.getFangstHomr() + "";
                    Integer loc = Conversion.safeStringtoIntegerNULL(sl.getFangstLok());
                    String stratum = area != null && loc != null ? area + "_" + loc : null;
                    Point2D.Double pt = (Point2D.Double) posMap.getRowValue(stratum);
                    if (pt == null) {
                        continue;
                    }
                    sl.setLongitude(pt.x);
                    sl.setLatitude(pt.y);
                }
                return landing;
            case Functions.SOURCETYPE_BIOTIC:
                for (FishstationBO fs : biotic) {
                    if (fs.getFs().getLatitudestart() != null && fs.getFs().getLongitudestart() != null) {
                        continue;
                    }
                    String area = fs.getFs().getArea() != null ? fs.getFs().getArea() + "" : null;
                    Point2D.Double pt = (Point2D.Double) posMap.getRowColValue(area, fs.getFs().getLocation());
                    if (pt == null) {
                        continue;
                    }
                    fs.getFs().setLatitudestart(pt.y);
                    fs.getFs().setLongitudestart(pt.x);
                }
                return biotic;
        }

        return null;
    }

    @Override
    public IDataStorage getDataStorage() {
        if (dataSource == null) {
            return null;
        }
        switch (dataSource) {
            case Functions.SOURCETYPE_LANDING:
                return new LandingDataStorage();
            case Functions.SOURCETYPE_BIOTIC:
                return new BioticDataStorage();
        }

        return null;
    }
}
