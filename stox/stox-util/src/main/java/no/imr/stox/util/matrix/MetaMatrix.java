package no.imr.stox.util.matrix;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * meta matrix object
 *
 * @author aasmunds
 */
public class MetaMatrix implements IMetaMatrix {

    private String variable;
    // Dimension for Group, Row, Col, Cell
    private final Map<String, String> dimensions = new HashMap<>();
    // Output headers for Group, Row, Col, Cell
    private final Map<String, String> headers = new HashMap<>();

// export formatting properties
    private Integer numDecimalsInExports = 12;

    public MetaMatrix(String metaMatrixDef) {
        // Matrix[GROUP~Taxa / ROW~Station / COL~LD / CELL~LenGrp]
        String metaInfo = metaMatrixDef.substring("Matrix[".length(), metaMatrixDef.length() - 1);
        // GROUP~Taxa / ROW~Station / COL~LD / CELL~LenGrp
        String[] dimvalues = metaInfo.split("/");
        for (String dimValue : dimvalues) {
            // GROUP~Taxa
            String[] elms = dimValue.split("~");
            String key = elms[0].trim();
            String value = elms[1].trim();
            if (key.equals(IMetaMatrix.VAR)) {
                variable = value;
            } else {
                dimensions.put(key, value);
            }
        }
    }

    @Override
    public String getVariable() {
        return variable;
    }

    @Override
    public void setVariable(String variable) {
        this.variable = variable;
    }

    @Override
    public Map<String, String> getDimensions() {
        return dimensions;
    }

    @Override
    public String getDimensionByName(String name) {
        for (String key : dimensions.keySet()) {
            if (name.equalsIgnoreCase(dimensions.get(key))) {
                return key;
            }
        }
        return null;
    }

    @Override
    public Map<String, String> getHeaders() {
        return headers;
    }

    @Override
    public String getHeader(String dim) {
        String res = headers.get(dim);
        if (res == null) {
            res = dimensions.get(dim);
        }
        return res;
    }

    /**
     *
     * @return a Matrix meta description with dimensions and content
     */
    @Override
    public String toString() {
        String res = "";
        for (String dim : Arrays.asList(IMetaMatrix.GROUP, IMetaMatrix.ROW, IMetaMatrix.COL, IMetaMatrix.CELL)) {
            String val = dimensions.get(dim);
            if (val != null) {
                if (!res.isEmpty()) {
                    res += " / ";
                }
                res += dim + "~" + val;
            }
        }
        if (variable != null) {
            res += " / VAR~" + variable;
        }
        return "Matrix[" + res + "]";
    }

    @Override
    public Integer getNumDecimalsInExports() {
        return numDecimalsInExports;
    }

    @Override
    public void setNumDecimalsInExports(Integer numDecimalsInExports) {
        this.numDecimalsInExports = numDecimalsInExports;
    }

}
