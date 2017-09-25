package no.imr.sea2data.imrbase.matrix;

import java.util.Map;

/**
 * Meta matrix that describes content and dimensions in a matrix
 *
 * @author aasmunds
 */
public interface IMetaMatrix {

    static final String GROUP = "GROUP";
    static final String ROW = "ROW";
    static final String COL = "COL";
    static final String CELL = "CELL";
    static final String VAR = "VAR";

    /**
     * Get the content variable from the matrix
     *
     * @return
     */
    String getVariable();

    void setVariable(String variable);

    /**
     *
     * @return dimensions
     */
    Map<String, String> getDimensions();

    /**
     *
     * @param name
     * @return Dimension by name
     */
    String getDimensionByName(String name);

    /**
     *
     * @return matrix headers map
     */
    Map<String, String> getHeaders();

    /**
     *
     * @param dim GROUP, ROW, COL, CELL
     * @return header by dimension
     */
    String getHeader(String dim);

    /**
     * @return number of decimals in export
     */
    Integer getNumDecimalsInExports();

    /**
     * Set number of decimals in export.
     *
     * @param numDecimalsInExports
     */
    void setNumDecimalsInExports(Integer numDecimalsInExports);
}
