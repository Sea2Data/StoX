package no.imr.stox.functions.processdata;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.geom.Polygon;
import java.util.List;
import java.util.Map;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.imrmap.utils.JTSUtils;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions; 
import no.imr.stox.functions.utils.RectangleUtil;
import no.imr.stox.log.ILogger;

/**
 * TODO: what does this class do?
 *
 * @author aasmunds
 */
public class DefineRectangle extends AbstractFunction {

    /**
     *
     * @param input
     * @return
     * @throws no.imr.stox.exception.UserErrorException
     */
    @Override
    public Object perform(Map<String, Object> input) {
        List<DistanceBO> distances = (List<DistanceBO>) input.get(Functions.PM_DEFINERECTANGLE_ACOUSTICDATA);
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_DEFINERECTANGLE_PROCESSDATA);
        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        Double w = (Double) input.get(Functions.PM_DEFINERECTANGLE_WIDTH);
        Double h = (Double) input.get(Functions.PM_DEFINERECTANGLE_HEIGHT);
        if (w == null || h == null) {
            logger.error("Width or height of rectangle is missing.", null);
        }
        MatrixBO polygons = AbndEstProcessDataUtil.getStratumPolygons(pd);
        AbndEstProcessDataUtil.getPSUStrata(pd).clear();
        AbndEstProcessDataUtil.getEDSUPSUs(pd).clear();
        for (String stratum : polygons.getRowKeys()) {
            MultiPolygon pol = (MultiPolygon) polygons.getRowColValue(stratum, Functions.COL_POLVAR_POLYGON);
            if (pol == null) {
                continue;
            }
            Polygon env = (Polygon) pol.getEnvelope();
            Double minlon = Double.MAX_VALUE;
            Double minlat = Double.MAX_VALUE;
            Double maxlon = -Double.MAX_VALUE;
            Double maxlat = -Double.MAX_VALUE;

            for (Coordinate c : env.getCoordinates()) {
                minlon = Math.min(minlon, c.x);
                minlat = Math.min(minlat, c.y);
                maxlon = Math.max(maxlon, c.x);
                maxlat = Math.max(maxlat, c.y);
            }
            minlat = RectangleUtil.floorInt(minlat + 0.000000001, h);
            minlon = RectangleUtil.floorInt(minlon + 0.000000001, w);
            maxlat = RectangleUtil.ceilInt(maxlat - 0.000000001, h);
            maxlon = RectangleUtil.ceilInt(maxlon - 0.000000001, w);
            int xc = (int) ((maxlon - minlon + 0.000000001) / w);
            int yc = (int) ((maxlat - minlat + 0.000000001) / h);

            for (int y = 0; y < yc; y++) {
                Double lat = minlat + y * h;
                for (int x = 0; x < xc; x++) {
                    Double lon = minlon + x * w;
                    String psu = RectangleUtil.getPSUByPosition(lon, lat, w, h);
                    MultiPolygon gmi = RectangleUtil.getPSUPolygon(psu, pol);
                    if (gmi == null) {
                        continue;
                    }
                    AbndEstProcessDataUtil.setPSUStratum(pd, psu, stratum);
                    for (DistanceBO d : distances) {
                        if (JTSUtils.within(new Coordinate(d.getLon_start(), d.getLat_start()), gmi)) {
                            AbndEstProcessDataUtil.setEDSUPSU(pd, d.getKey(), psu);
                        }
                    }

                }
            }
        }
        return null;
    }
}
