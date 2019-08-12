package no.imr.stox.bo;

import no.imr.stox.util.matrix.MatricesBO;
import no.imr.stox.util.matrix.MatrixBO;
import com.vividsolutions.jts.geom.MultiPolygon;
import com.vividsolutions.jts.io.WKTWriter;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.util.base.ExportUtil;
import no.imr.stox.util.base.ImrIO;
import no.imr.stox.functions.utils.Functions;

/**
 * Generic Process data object with matrices holding manual data for processes.
 * Use table key to access each matrix.
 *
 * @author aasmunds
 */
public class ProcessDataBO extends MatricesBO {

    public ProcessDataBO() {
        // Assignment datatype
        getMatrix(AbndEstProcessDataUtil.TABLE_BIOTICASSIGNMENT).setMetaMatrix(Functions.MM_BIOTICASSIGNMENT_MATRIX);
        getMatrix(AbndEstProcessDataUtil.TABLE_SUASSIGNMENT).setMetaMatrix(Functions.MM_SUASSIGNMENT_MATRIX);
        getMatrix(AbndEstProcessDataUtil.TABLE_ASSIGNMENTRESOLUTION).setMetaMatrix(Functions.MM_VARIABLE_MATRIX);
        
        // SampleUnit datatype
        getMatrix(AbndEstProcessDataUtil.TABLE_EDSUPSU).setMetaMatrix(Functions.MM_EDSUPSU_MATRIX);
        getMatrix(AbndEstProcessDataUtil.TABLE_PSUSTRATUM).setMetaMatrix(Functions.MM_PSUSTRATUM_MATRIX);
        
        // StratumPolygon datatype
        getMatrix(AbndEstProcessDataUtil.TABLE_STRATUMPOLYGON).setMetaMatrix(Functions.MM_POLYGON_MATRIX);

        // Covariate datatype
        getMatrix(AbndEstProcessDataUtil.TABLE_TEMPORAL).setMetaMatrix(Functions.MM_COVARIATE_MATRIX);
        getMatrix(AbndEstProcessDataUtil.TABLE_GEARFACTOR).setMetaMatrix(Functions.MM_COVARIATE_MATRIX);
        getMatrix(AbndEstProcessDataUtil.TABLE_SPATIAL).setMetaMatrix(Functions.MM_COVARIATE_MATRIX);
        getMatrix(AbndEstProcessDataUtil.TABLE_PLATFORM).setMetaMatrix(Functions.MM_COVARIATE_MATRIX);
        getMatrix(AbndEstProcessDataUtil.TABLE_COVPARAM).setMetaMatrix(Functions.MM_COVARIATEPARAMETER_MATRIX);
        
        // Age error datatype
        getMatrix(AbndEstProcessDataUtil.TABLE_AGEERROR).setMetaMatrix(Functions.MM_AGEERROR_MATRIX);
        
        // Stratum neighbour datatype
        getMatrix(AbndEstProcessDataUtil.TABLE_STRATUMNEIGHBOUR).setMetaMatrix(Functions.MM_VARIABLE_MATRIX);
    }

    /**
     * Get output order.
     *
     * @return
     */
    @Override
    public List<String> getOutputOrder() {
        return Arrays.asList(AbndEstProcessDataUtil.TABLE_BIOTICASSIGNMENT,
                AbndEstProcessDataUtil.TABLE_SUASSIGNMENT,
                AbndEstProcessDataUtil.TABLE_ASSIGNMENTRESOLUTION,
                AbndEstProcessDataUtil.TABLE_EDSUPSU,
                AbndEstProcessDataUtil.TABLE_PSUSTRATUM,
                AbndEstProcessDataUtil.TABLE_STRATUMPOLYGON,
                AbndEstProcessDataUtil.TABLE_TEMPORAL,
                AbndEstProcessDataUtil.TABLE_GEARFACTOR,
                AbndEstProcessDataUtil.TABLE_SPATIAL,
                AbndEstProcessDataUtil.TABLE_PLATFORM,
                AbndEstProcessDataUtil.TABLE_COVPARAM,
                AbndEstProcessDataUtil.TABLE_AGEERROR,
                AbndEstProcessDataUtil.TABLE_STRATUMNEIGHBOUR
                );
    }

    @Override
    protected void asTable(String table, Writer wr) {
        if (table.equals(AbndEstProcessDataUtil.TABLE_STRATUMPOLYGON)) {
            ImrIO.write(wr, ExportUtil.tabbedCRLF("Stratum", "IncludeInTotal", "Polygon"));
            MatrixBO polygons = AbndEstProcessDataUtil.getStratumPolygons(this);
            WKTWriter wktWriter = new WKTWriter();
            for (String stratum : polygons.getSortedRowKeys()) {
                String includeInTotal = (String) polygons.getRowColValue(stratum, Functions.COL_POLVAR_INCLUDEINTOTAL);
                MultiPolygon mp = (MultiPolygon) polygons.getRowColValue(stratum, Functions.COL_POLVAR_POLYGON);
                ImrIO.write(wr, ExportUtil.tabbedCRLF(stratum, includeInTotal, wktWriter.write(mp)));

            }
            return;
        }
        super.asTable(table, wr); //To change body of generated methods, choose Tools | Templates.
    }

}
