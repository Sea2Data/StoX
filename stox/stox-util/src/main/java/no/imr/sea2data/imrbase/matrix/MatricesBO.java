package no.imr.sea2data.imrbase.matrix;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.imr.sea2data.imrbase.util.ExportUtil;
import no.imr.sea2data.imrbase.util.ImrIO;

/**
 * Data structure with collection of independent matrices.
 *
 * @author Åsmund
 */
public class MatricesBO {

    private final Map<String, MatrixBO> matrices = new HashMap<>();

    /**
     * get matrix with given table name. If not existing, create it.
     *
     * @param table - the name of the matrix
     * @return a new matrix bo
     */
    public MatrixBO getMatrix(String table) {
        MatrixBO m = matrices.get(table);
        if (m == null) {
            m = createMatrix(table);
            matrices.put(table, m);
        }
        return m;
    }

    protected MatrixBO createMatrix(String table) {
        return new MatrixBO();
    }

    /**
     * get matrices
     *
     * @return matrices
     */
    public Map<String, MatrixBO> getMatrices() {
        return matrices;
    }

    /**
     * @return output order. override this to define in which order to write out
     * the matrices
     */
    public List<String> getOutputOrder() {
        return new ArrayList(matrices.keySet());
    }

    /**
     * @param wr
     */
    public void asTable(Writer wr) {
        int size = getOutputOrder().size();
        Boolean found = false;
        for (String table : getOutputOrder()) {
            if (getMatrix(table).isEmpty()) {
                continue;
            }
            if (found) {
                ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(""));
            }
            ImrIO.write(wr, size > 1 ? ExportUtil.carrageReturnLineFeed("TABLE:" + table.toUpperCase()) : "");
            asTable(table, wr);
            found = true;
        }
    }

    public String asTable() {
        StringWriter sw = new StringWriter();
        asTable(sw);
        return sw.toString();
    }

    public String asTable(String table) {
        StringWriter sw = new StringWriter();
        asTable(table, sw);
        return sw.toString();
    }

    protected void asTable(String table, Writer wr) {
        MatrixBO m = getMatrix(table);
        if (m.isEmpty()) {
            return;
        }
        m.asTable(wr);
    }

    @Override
    public String toString() {
        return asTable();
    }

}
