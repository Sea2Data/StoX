package no.imr.stox.functions.processdata;

import com.vividsolutions.jts.geom.MultiPolygon;
import java.util.Map;
import no.imr.stox.util.matrix.MatrixBO;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.ProjectUtils;
import no.imr.stox.functions.utils.StratumUtils;
import no.imr.stox.log.ILogger;

/**
 * Read stratum polygons file and create a stratum~polygon matrix.
 *
 * @author Åsmund
 */
public class DefineStrata extends AbstractFunction {

    /**
     * read polygon from file and apply it to strata polygons in process data
     *
     * @param input contains Polygon file name DataTypeDescription.txt
     * @return
     */
    @Override
    public Object perform(Map<String, Object> input) {
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_DEFINESTRATA_PROCESSDATA);
        Boolean useEx = (Boolean) input.get(Functions.PM_DEFINESTRATA_USEPROCESSDATA);
        if (useEx != null && useEx) {
            // Use existing, do not read from file.
            return pd;
        }

        ILogger logger = (ILogger) input.get(Functions.PM_LOGGER);
        AbndEstProcessDataUtil.getStratumPolygons(pd).clear();
        String fileName = ProjectUtils.resolveParameterFileName((String) input.get(Functions.PM_DEFINESTRATA_FILENAME), (String) input.get(Functions.PM_PROJECTFOLDER));
        if (fileName == null) {
            return pd;
        }
        MatrixBO pol = StratumUtils.getStratumPolygonByWKTFile(fileName);
        for (String name : pol.getRowKeys()) {
            AbndEstProcessDataUtil.setStratumPolygon(pd, name, true, (MultiPolygon) pol.getRowValue(name));
        }
        return pd;
    }
}
