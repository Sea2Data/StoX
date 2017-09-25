package no.imr.sea2data.imrbase.matrix;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import no.imr.sea2data.imrbase.math.ImrMath;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrbase.util.ImrSort;
import no.imr.sea2data.imrbase.util.ExportUtil;
//import no.imr.stox.functions.utils.StoXMath;
import no.imr.sea2data.imrbase.matrix.IMetaMatrix;
import no.imr.sea2data.imrbase.matrix.MetaMatrix;
import no.imr.sea2data.imrbase.util.ImrIO;

/**
 * BO structure for sparse matrix data on 4 dimensions group, row, col, cell.
 * Relies on meta matrix when dimensions are defined. Default dimension is "1"
 * if not specified in the meta matrix.
 *
 * @author aasmunds
 */
public class MatrixBO {

    /**
     * Default dimension if not specified in meta matrix
     */
    private static final String DEF_DIM = "1";
    /**
     * Meta matrix defining dimensions.
     */
    private IMetaMatrix metaMatrix;
    /**
     * The dimension of this matrix
     */
    private String dimension;
    /**
     * inner matrix data map.
     */
    private Map<String, Object> matrix = new HashMap<>();

    /**
     * construct a matrix with default structure
     */
    public MatrixBO() {
        this("Matrix[ROW~Row / Col~Col / VAR~Data]");
    }

    /**
     * Construct a matrix with meta matrix
     *
     * @param metaMatrix the meta matrix
     */
    public MatrixBO(String metaMatrix) {
        this(IMetaMatrix.GROUP, new MetaMatrix(metaMatrix));
    }

    public MatrixBO(IMetaMatrix metaMatrix) {
        this(IMetaMatrix.GROUP, metaMatrix);
    }

    /**
     * Construct a matrix with dimension and meta matrix definition
     *
     * @param dimension dimension
     * @param metaMatrixDef meta matrix definition
     */
    public MatrixBO(String dimension, String metaMatrixDef) {
        this(dimension, new MetaMatrix(metaMatrixDef));
    }

    /**
     * Construct a matrix with dimension and meta matrix
     *
     * @param dimension dimension
     * @param metaMatrix meta matrix
     */
    public MatrixBO(String dimension, IMetaMatrix metaMatrix) {
        this.metaMatrix = metaMatrix;
        this.dimension = dimension;
    }

    /**
     * Remove key
     *
     * @param key key to remove
     */
    public void removeValue(String key) {
        matrix.remove(key);
    }

    /**
     * remove row key
     *
     * @param rowKey
     */
    public void removeRowKey(String rowKey) {
        if (getDefaultValueAsMatrix() != null) {
            getDefaultValueAsMatrix().removeValue(rowKey);
        }
    }

    /**
     * remove row key by row value
     *
     * @param rowValue
     */
    public void removeRowKeyByRowValue(String rowValue) {
        for (String rowKey : getRowKeys()) {
            Object val = getRowValue(rowKey);
            if (val.equals(rowValue)) {
                removeRowKey(rowKey);
            }
        }
    }

    /**
     * remove row key by row col value
     *
     * @param rowcolValue
     */
    public void removeRowKeyByRowColValue(String rowcolValue) {
        for (String rowKey : getRowKeys()) {
            MatrixBO row = getRowValueAsMatrix(rowKey);
            for (String colKey : row.getKeys()) {
                Object val = row.getValue(colKey);
                if (val.equals(rowcolValue)) {
                    removeRowKey(rowKey);
                }
            }
        }
    }

    /**
     * replace key
     *
     * @param oldKey
     * @param newKey
     */
    public void replaceKey(String oldKey, String newKey) {
        Object o = matrix.get(oldKey);
        matrix.remove(oldKey);
        matrix.put(newKey, o);
    }

    /**
     * replace row key
     *
     * @param oldRowKey
     * @param newRowKey
     */
    public void replaceRowKey(String oldRowKey, String newRowKey) {
        getDefaultValueAsMatrix().replaceKey(oldRowKey, newRowKey);
    }

    /**
     * replace row value
     *
     * @param oldValue
     * @param newValue
     */
    public void replaceRowValue(Object oldValue, Object newValue) {
        for (String rowKey : getRowKeys()) {
            Object val = getRowValue(rowKey);
            if (val != null && val.equals(oldValue)) {
                setRowValue(rowKey, newValue);
            }
        }
    }

    public void replaceRowColValue(Object oldValue, Object newValue) {
        for (String rowKey : getRowKeys()) {
            MatrixBO row = getRowValueAsMatrix(rowKey);
            if (row == null) {
                continue;
            }
            for (String colKey : row.getKeys()) {
                Object val = row.getValue(colKey);
                if (val != null && val.equals(oldValue)) {
                    row.setValue(colKey, newValue);
                }
            }
        }
    }

    /**
     *
     * @param o possible matrix
     * @return as matrix if possible
     */
    private static MatrixBO asMatrix(Object o) {
        if (o instanceof MatrixBO) {
            return (MatrixBO) o;
        }
        return null;
    }

    private static Double asDouble(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Double) {
            return (Double) o;
        } else if (o instanceof Integer) {
            return ((Integer) o).doubleValue();
        } else if (o instanceof String) {
            return Conversion.safeStringtoDoubleNULL(o.toString());
        }
        return null;
    }

    private static Integer asInteger(Object o) {
        if (o == null) {
            return null;
        }
        if (o instanceof Integer) {
            return (Integer) o;
        } else if (o instanceof Double) {
            return ((Double) o).intValue();
        } else if (o instanceof String) {
            return Conversion.safeStringtoIntegerNULL(o.toString());
        }
        return null;
    }

    /**
     *
     * @param o possible matrix
     * @param key key
     * @return as matrix value if possible
     */
    private static Object getMatrixValue(Object o, String key) {
        MatrixBO m = asMatrix(o);
        if (m != null) {
            return m.getValue(key);
        }
        return null;
    }

    /**
     * get default value. This means that i.e group is not an applicable
     * dimension and is coded to default "1".
     *
     * @return the default value, or the only value available. For group "1" a
     * matrix is returned for ROW/COL
     */
    public Object getDefaultValue() {
        return getValue(DEF_DIM);
    }

    /**
     * Get default value as matrix
     *
     * @return
     */
    public MatrixBO getDefaultValueAsMatrix() {
        return asMatrix(getDefaultValue());
    }

    /**
     * @param key key
     * @return value from key
     */
    public Object getValue(String key) {
        return matrix.get(key);
    }

    /**
     *
     * @param key
     * @return get value as matrix
     */
    public MatrixBO getValueAsMatrix(String key) {
        return asMatrix(getValue(key));
    }

    /**
     * @param key
     * @return value as double for given key
     */
    public Double getValueAsDouble(String key) {
        return asDouble(getValue(key));
    }

    /**
     *
     * @param groupKey group
     * @return group value
     */
    public Object getGroupValue(String groupKey) {
        return getValue(groupKey);
    }

    /**
     *
     * @param groupKey group
     * @param rowKey row
     * @return group/row value
     */
    public Object getGroupRowValue(String groupKey, String rowKey) {
        return getMatrixValue(getValue(groupKey), rowKey);
    }

    /**
     *
     * @param groupKey group
     * @param rowKey row
     * @return group/row value as matrix
     */
    public MatrixBO getGroupRowValueAsMatrix(String groupKey, String rowKey) {
        return asMatrix(getGroupRowValue(groupKey, rowKey));
    }

    public MatrixBO getGroupRowColValueAsMatrix(String groupKey, String rowKey, String colKey) {
        return asMatrix(getGroupRowColValue(groupKey, rowKey, colKey));
    }

    /**
     *
     * @param rowKey
     * @return row value as matrix
     */
    public MatrixBO getRowValueAsMatrix(String rowKey) {
        return asMatrix(getRowValue(rowKey));
    }

    /**
     *
     * @param rowKey
     * @param colKey
     * @return cell value as matrix
     */
    public MatrixBO getRowColValueAsMatrix(String rowKey, String colKey) {
        return asMatrix(getRowColValue(rowKey, colKey));
    }

    /**
     *
     * @param rowKey row
     * @return row value
     */
    public Object getRowValue(String rowKey) {
        return getMatrixValue(getDefaultValue(), rowKey);
    }

    /**
     *
     * @param rowKey row
     * @return row value as double
     */
    public Double getRowValueAsDouble(String rowKey) {
        return asDouble(getRowValue(rowKey));
    }

    public Integer getRowValueAsInteger(String rowKey) {
        return asInteger(getRowValue(rowKey));
    }

    /**
     *
     * @param groupKey group
     * @param rowKey row
     * @param colKey col
     * @return group/row/col value
     */
    public Object getGroupRowColValue(String groupKey, String rowKey, String colKey) {
        return getMatrixValue(getGroupRowValue(groupKey, rowKey), colKey);
    }

    /**
     *
     * @param groupKey group
     * @param rowKey row
     * @param colKey col
     * @return group/row/col value as double
     */
    public Double getGroupRowColValueAsDouble(String groupKey, String rowKey, String colKey) {
        return asDouble(getGroupRowColValue(groupKey, rowKey, colKey));
    }

    public Integer getGroupRowColValueAsInteger(String groupKey, String rowKey, String colKey) {
        return asInteger(getGroupRowColValue(groupKey, rowKey, colKey));
    }
    /**
     *
     * @param groupKey group
     * @param rowKey row
     * @return group/row default value
     */
    public Object getGroupRowDefaultValue(String groupKey, String rowKey) {
        return getGroupRowColValue(groupKey, rowKey, DEF_DIM);
    }

    /**
     *
     * @param groupKey group
     * @param rowKey row
     * @return group/row default value as matrix
     */
    public MatrixBO getGroupRowDefaultValueAsMatrix(String groupKey, String rowKey) {
        return asMatrix(getGroupRowDefaultValue(groupKey, rowKey));
    }

    /**
     *
     * @param rowKey row
     * @param colKey col
     * @param cellKey cell
     * @return row/col/cell value
     */
    public Object getRowColCellValue(String rowKey, String colKey, String cellKey) {
        return getGroupRowColCellValue(DEF_DIM, rowKey, colKey, cellKey);
    }

    /**
     *
     * @param rowKey row
     * @param colKey col
     * @return row/col value
     */
    public Object getRowColValue(String rowKey, String colKey) {
        return getMatrixValue(getGroupRowValue(DEF_DIM, rowKey), colKey);
    }

    /**
     *
     * @param rowKey row
     * @param colKey col
     * @return row/col value as double
     */
    public Double getRowColValueAsDouble(String rowKey, String colKey) {
        return asDouble(getRowColValue(rowKey, colKey));
    }

    public Integer getRowColValueAsInteger(String rowKey, String colKey) {
        return asInteger(getRowColValue(rowKey, colKey));
    }

    /**
     *
     * @param groupKey group
     * @param rowKey row
     * @param colKey col
     * @param cellKey cell
     * @return group/row/col/cell value
     */
    public Object getGroupRowColCellValue(String groupKey, String rowKey, String colKey, String cellKey) {
        return getMatrixValue(getGroupRowColValue(groupKey, rowKey, colKey), cellKey);
    }

    /**
     *
     * @param groupKey group
     * @param rowKey row
     * @param colKey col
     * @param cellKey cell
     * @return group/row/col/cell value as double
     */
    public Double getGroupRowColCellValueAsDouble(String groupKey, String rowKey, String colKey, String cellKey) {
        return asDouble(getGroupRowColCellValue(groupKey, rowKey, colKey, cellKey));
    }

    public Double getRowColCellValueAsDouble(String rowKey, String colKey, String cellKey) {
        return asDouble(getRowColCellValue(rowKey, colKey, cellKey));
    }

    /**
     *
     * @param groupKey group
     * @param rowKey row
     * @param cellKey cell
     * @return group/row/cell value
     */
    public Object getGroupRowCellValue(String groupKey, String rowKey, String cellKey) {
        return getMatrixValue(getGroupRowColValue(groupKey, rowKey, DEF_DIM), cellKey);
    }

    /**
     *
     * @param groupKey group
     * @param rowKey row
     * @param cellKey cell
     * @return group/row/cell value as double
     */
    public Double getGroupRowCellValueAsDouble(String groupKey, String rowKey, String cellKey) {
        return asDouble(getGroupRowCellValue(groupKey, rowKey, cellKey));
    }

    /**
     * @param keys dimension keys
     * @return value from key dimensions
     */
    public Object getValue(List<String> keys) {
        if (keys.isEmpty()) {
            return null;
        }
        Object o = getValue(keys.get(0));
        if (keys.size() > 1) {
            if (!(o instanceof MatrixBO)) {
                return null;
            }
            return ((MatrixBO) o).getValue(keys.subList(1, keys.size()));
        }
        return o;
    }

    /**
     * set value from keys
     *
     * @param keys keys
     * @param value value
     */
    public void setValue(String[] keys, Object value) {
        setValue(Arrays.asList(keys), value);
    }

    /**
     * Set a value into structure by creating sub array if more than one key
     * provided.
     *
     * @param value
     * @param keys
     */
    public void setValue(List<String> keys, Object value) {
        String key = keys.get(0);
        Object existing = matrix.get(key);
        Object o = value;
        if (keys.size() > 1) {

            MatrixBO a;
            if (existing != null) {
                a = (MatrixBO) existing;
            } else {
                String nextDim = getNextDimension(dimension);
                if (nextDim == null) {
                    return;
                }
                a = new MatrixBO(nextDim, metaMatrix);
            }
            a.setValue(keys.subList(1, keys.size()), value);
            o = a;
        }
        if (keys.size() == 1 || existing == null) {
            setValue(key, o);
        }
    }

    /**
     *
     * @param dimension dimension
     * @return next dimension
     */
    public static String getNextDimension(String dimension) {
        if (dimension.equals(IMetaMatrix.GROUP)) {
            return IMetaMatrix.ROW;
        } else if (dimension.equals(IMetaMatrix.ROW)) {
            return IMetaMatrix.COL;
        } else if (dimension.equals(IMetaMatrix.COL)) {
            return IMetaMatrix.CELL;
        }
        return null;
    }

    /**
     * Put a value into the matrix. Cannot be NULL since this is a sparse tree.
     *
     * @param key
     * @param value
     */
    public void setValue(String key, Object value) {
        matrix.put(key, value);
    }

    /**
     * set group/row/col/cell value
     *
     * @param groupKey group
     * @param rowKey row
     * @param colKey col
     * @param cellKey cell
     * @param value value
     */
    public void setGroupRowColCellValue(String groupKey, String rowKey, String colKey, String cellKey, Object value) {
        setValue(Arrays.asList(groupKey, rowKey, colKey, cellKey), value);
    }

    /**
     * set group/row/col value
     *
     * @param groupKey group
     * @param rowKey row
     * @param colKey col
     * @param value value
     */
    public void setGroupRowColValue(String groupKey, String rowKey, String colKey, Object value) {
        setValue(Arrays.asList(groupKey, rowKey, colKey), value);
    }

    /**
     * set group/row value
     *
     * @param groupKey group
     * @param rowKey row
     * @param value value
     */
    public void setGroupRowValue(String groupKey, String rowKey, Object value) {
        setValue(Arrays.asList(groupKey, rowKey), value);
    }

    /**
     * set group/row/cell value
     *
     * @param groupKey group
     * @param rowKey row
     * @param cellKey cell
     * @param value value
     */
    public void setGroupRowCellValue(String groupKey, String rowKey, String cellKey, Object value) {
        setGroupRowColCellValue(groupKey, rowKey, DEF_DIM, cellKey, value);
    }

    /**
     * set row/col/cell value
     *
     * @param rowKey row
     * @param colKey col
     * @param cellKey cell
     * @param value value
     */
    public void setRowColCellValue(String rowKey, String colKey, String cellKey, Object value) {
        setGroupRowColCellValue(DEF_DIM, rowKey, colKey, cellKey, value);
    }

    /**
     * set row/col value
     *
     * @param rowKey row
     * @param colKey col
     * @param value value
     */
    public void setRowColValue(String rowKey, String colKey, Object value) {
        setGroupRowColValue(DEF_DIM, rowKey, colKey, value);
    }

    /**
     * set row value
     *
     * @param rowKey row
     * @param value value
     */
    public void setRowValue(String rowKey, Object value) {
        setGroupRowValue(DEF_DIM, rowKey, value);
    }

    /**
     * add value by dimensions
     *
     * @param dims dimensions
     * @param value value
     */
    public void addDoubleValue(String[] dims, Double value) {
        addValue(Arrays.asList(dims), value);
    }

    /**
     * add value by dimensions
     *
     * @param dims dimensions
     * @param value value
     */
    public void addValue(List<String> dims, Double value) {
        Double val = (Double) getValue(dims);
        if (value != null) {
            val = val == null ? value : val + value;
        }
        setValue(dims, val);
    }

    public void addValue(List<String> dims, Integer value) {
        if (value == null) {
            return;
        }
        Integer val = (Integer) getValue(dims);
        if (val == null) {
            val = 0;
        }
        val += value;
        setValue(dims, val);
    }

    /**
     *
     * @return keys
     */
    public List<String> getKeys() {
        return new ArrayList<String>(matrix.keySet());
    }

    /**
     *
     * @return sorted keys
     */
    public List<String> getSortedKeys() {
        return sorted(getKeys());
    }

    private static List<String> asKeys(MatrixBO m) {
        return asKeys(m, false);
    }

    private static List<String> sorted(List<String> res) {
        Collections.sort(res, new ImrSort.TranslativeComparatorWithToken(true, "/", res));
        return res;
    }

    private static List<String> asKeys(MatrixBO m, boolean sorted) {
        if (m != null) {
            List<String> res = m.getKeys();
            if (sorted) {
                return sorted(res);
            }
            return res;
        }
        return new ArrayList<>();
    }

    /**
     *
     * @param groupKey group
     * @return row keys by group
     */
    public List<String> getGroupRowKeys(String groupKey) {
        return asKeys(getValueAsMatrix(groupKey));
    }

    /**
     *
     * @param groupKey group
     * @return sorted row keys by group
     */
    public List<String> getSortedGroupRowKeys(String groupKey) {
        return asKeys(getValueAsMatrix(groupKey), true);
    }

    /**
     *
     * @param groupKey group
     * @param rowKey row
     * @return group row keys
     */
    public List<String> getGroupRowColKeys(String groupKey, String rowKey) {
        return asKeys(getGroupRowValueAsMatrix(groupKey, rowKey));
    }

    public List<String> getGroupRowColCellKeys(String groupKey, String rowKey, String colKey) {
        return asKeys(getGroupRowColValueAsMatrix(groupKey, rowKey, colKey));
    }

    public List<String> getSortedGroupRowColKeys(String groupKey, String rowKey) {
        return asKeys(getGroupRowValueAsMatrix(groupKey, rowKey), true);
    }

    public List<String> getSortedRowColKeys(String rowKey) {
        return asKeys(getRowValueAsMatrix(rowKey), true);
    }

    public List<String> getSortedGroupRowColCellKeys(String groupKey, String rowKey, String colKey) {
        return asKeys(getGroupRowColValueAsMatrix(groupKey, rowKey, colKey), true);
    }

    public List<String> getSortedRowColCellKeys(String rowKey, String colKey) {
        return asKeys(getRowColValueAsMatrix(rowKey, colKey), true);
    }

    public List<String> getGroupRowCellKeys(String groupKey, String rowKey) {
        return getGroupRowCellKeys(groupKey, rowKey, false);
    }

    public List<String> getSortedGroupRowCellKeys(String groupKey, String rowKey) {
        return getGroupRowCellKeys(groupKey, rowKey, true);
    }

    public List<String> getGroupRowCellKeys(String groupKey, String rowKey, Boolean sorted) {
        MatrixBO m = getGroupRowValueAsMatrix(groupKey, rowKey);
        if (m == null) {
            return null;
        }
        return asKeys(m.getDefaultValueAsMatrix(), sorted);
    }

    /**
     *
     * @param rowKey return row col keys
     * @return
     */
    public List<String> getRowColKeys(String rowKey) {
        return asKeys(getRowValueAsMatrix(rowKey));
    }

    private Stream<Map.Entry<String, Object>> mapEntryToStream(Map.Entry<String, Object> e) {
        if (e.getValue() != null && e.getValue() instanceof MatrixBO) {
            return ((MatrixBO) e.getValue()).matrix.entrySet().stream();
        }
        return null;
    }

    private Stream<String> getRowKeysStream() {
        return matrix.entrySet().stream()
                .flatMap(e -> mapEntryToStream(e))
                .map(e -> e.getKey())
                .distinct();
    }

    private Stream<String> getColKeysStream() {
        return matrix.entrySet().stream()
                .flatMap(e -> mapEntryToStream(e))
                .flatMap(e -> mapEntryToStream(e))
                .map(e -> e.getKey())
                .distinct();
    }
    /**
     *
     * @return row keys
     */
    public List<String> getRowKeys() {
        return getRowKeysStream()
                .collect(Collectors.toList());
        //return asKeys(getDefaultValueAsMatrix());
    }
    

    /**
     *
     * @return row keys sorted
     */
    public List<String> getSortedRowKeys() {
        return sorted(getRowKeys());
    }

    /**
     *
     * @return col keys
     */
    public List<String> getColKeys() {
        return getColKeysStream()
                .collect(Collectors.toList());
        //return getGroupColKeys(DEF_DIM);
    }

    /**
     *
     * @return col keys sorted
     */
    public List<String> getSortedColKeys() {
        return sorted(getColKeys());

    }

    /**
     * Get sorted column keys by a given row
     *
     * @param rowKey
     * @return
     */
    public List<String> getSortedColKeys(String rowKey) {
        return asKeys(getRowValueAsMatrix(rowKey), true);
    }

    public List<String> getSortedColKeys(String groupKey, String rowKey) {
        return asKeys(getGroupRowValueAsMatrix(groupKey, rowKey), true);
    }

    /**
     * *
     *
     * @param groupKey
     * @return group/col keys
     */
    public List<String> getGroupColKeys(String groupKey) {
        Set<String> res = new HashSet<>();
        List<String> rowKeys = getGroupRowKeys(groupKey);
        for (String row : rowKeys) {
            MatrixBO m = getGroupRowValueAsMatrix(groupKey, row);
            if (m == null) {
                continue;
            }
            res.addAll(m.getKeys());
        }
        return new ArrayList<>(res);
    }

    /**
     *
     * @param groupKey
     * @return group cell keys
     */
    public List<String> getGroupCellKeys(String groupKey) {
        Set<String> res = new HashSet<>();
        MatrixBO group = getValueAsMatrix(groupKey);
        for (String rowKey : group.getKeys()) {
            MatrixBO row = group.getValueAsMatrix(rowKey);
            for (String colKey : row.getKeys()) {
                MatrixBO cell = row.getValueAsMatrix(colKey);
                res.addAll(cell.getKeys());
            }
        }
        return new ArrayList<>(res);
    }

    /**
     *
     * @param groupKey
     * @return group/col keys sorted
     */
    public List<String> getSortedGroupColKeys(String groupKey) {
        return sorted(getGroupColKeys(groupKey));
    }

    /**
     * add double value by key
     *
     * @param key key
     * @param value value
     */
    public void addDoubleValue(String key, Double value) {
        if (value == null) {
            return;
        }
        Double tot = (Double) matrix.get(key);
        if (tot == null) {
            tot = 0d;
        }
        setValue(key, (ImrMath.safePlus(value, tot)));
    }

    /**
     *
     * @return meta matrix
     */
    public IMetaMatrix getMetaMatrix() {
        return metaMatrix;
    }

    /**
     * set meta matrix
     *
     * @param metaMatrix meta matrix
     */
    public void setMetaMatrix(IMetaMatrix metaMatrix) {
        this.metaMatrix = metaMatrix;
    }

    /**
     * set meta matrix by definition
     *
     * @param metaMatrixDef definiton
     */
    public void setMetaMatrix(String metaMatrixDef) {
        this.metaMatrix = new MetaMatrix(metaMatrixDef);
    }

    /**
     *
     * @return matrix as string
     */
    @Override
    public String toString() {
        String res = "";
        String dimHdr = metaMatrix.getDimensions().get(dimension);
        boolean b = false;
        for (String key : matrix.keySet()) {
            if (b) {
                res += ", ";
            }
            if (dimHdr != null) {
                res += dimHdr + " " + key + "=[";
            }
            Object o = getValue(key);
            if (o == null) {
                continue;
            }
            String subStr = o.toString();
            if (!(o instanceof MatrixBO)) {
                subStr = metaMatrix.getVariable() + "=" + subStr;
            }
            res += subStr;
            if (dimHdr != null) {
                res += "]";
            }

            b = true;
        }
        return res; //To change body of generated methods, choose Tools | Templates.
    }

    /**
     * add value by group/row/col/cell
     *
     * @param group group
     * @param row row
     * @param col col
     * @param cell cell
     * @param v value
     */
    public void addGroupRowColCellValue(String group, String row, String col, String cell, Double v) {
        addValue(Arrays.asList(group, row, col, cell), v);
    }

    public void addGroupRowColCellValue(String group, String row, String col, String cell, Integer v) {
        addValue(Arrays.asList(group, row, col, cell), v);
    }

    /**
     * add value by group/row/cell
     *
     * @param group group
     * @param row row
     * @param cell cell
     * @param v value
     */
    public void addGroupRowCellValue(String group, String row, String cell, Double v) {
        addGroupRowColCellValue(group, row, DEF_DIM, cell, v);
    }

    public void addGroupRowCellValue(String group, String row, String cell, Integer v) {
        addGroupRowColCellValue(group, row, DEF_DIM, cell, v);
    }

    public void addGroupRowColValue(String group, String row, String col, Double v) {
        addValue(Arrays.asList(group, row, col), v);
    }

    /**
     * add value by row/col
     *
     * @param row row
     * @param col col
     * @param v value
     */
    public void addRowColValue(String row, String col, Double v) {
        addValue(Arrays.asList(DEF_DIM, row, col), v);
    }

    public void addRowColCellValue(String row, String col, String cell, Double v) {
        addValue(Arrays.asList(DEF_DIM, row, col, cell), v);
    }

    /**
     * add value by row
     *
     * @param row row
     * @param v value
     */
    public void addRowValue(String row, Double v) {
        addValue(Arrays.asList(DEF_DIM, row), v);
    }

    public void addRowValue(String row, Integer v) {
        addValue(Arrays.asList(DEF_DIM, row), v);
    }

    /**
     *
     * @return min and max key for groups of value.
     */
    public MatrixBO getGroupByValue() {
        MatrixBO bo = new MatrixBO();
        for (String key : getKeys()) {
            String value = getValue(key).toString();
            Integer c = (Integer) bo.getRowColValue(value, "count");
            bo.setRowColValue(value, "count", (c == null ? 0 : c) + 1);
            Integer i = Conversion.safeStringtoIntegerNULL(key);
            if (i == null) {
                continue;
            }
            Integer iMin = (Integer) bo.getRowColValue(value, "min");
            if (iMin == null || iMin > i) {
                bo.setRowColValue(value, "min", i);
            }
            Integer iMax = (Integer) bo.getRowColValue(value, "max");
            if (iMax == null || iMax < i) {
                bo.setRowColValue(value, "max", i);
            }
        }
        return bo;
    }

    public MatrixBO getRowKeyGroupValues() {
        MatrixBO bo = new MatrixBO();
        for (String key : getRowKeys()) {
            Integer i = Conversion.safeStringtoIntegerNULL(key);
            Integer c = (Integer) bo.getRowValue("count");
            bo.setRowValue("count", (c == null ? 0 : c) + 1);
            if (i == null) {
                continue;
            }
            Integer iMin = (Integer) bo.getRowValue("min");
            if (iMin == null || iMin > i) {
                bo.setRowValue("min", i);
            }
            Integer iMax = (Integer) bo.getRowValue("max");
            if (iMax == null || iMax < i) {
                bo.setRowValue("max", i);
            }
        }
        return bo;
    }

    /**
     *
     * @param wr
     * @return matrix as table as defined by meta matrix
     */
    public void asTable(Writer wr) {
        String variable = getMetaMatrix().getVariable();
        Integer nDec = getMetaMatrix().getNumDecimalsInExports();
        Boolean groupIsDimension = getMetaMatrix().getDimensions().get(IMetaMatrix.GROUP) != null;
        Boolean colIsDimension = getMetaMatrix().getDimensions().get(IMetaMatrix.COL) != null;
        Boolean cellIsDimension = getMetaMatrix().getDimensions().get(IMetaMatrix.CELL) != null;

        String hdr = null;
        for (String dim : Arrays.asList(IMetaMatrix.GROUP, IMetaMatrix.ROW, IMetaMatrix.COL, IMetaMatrix.CELL)) {
            if (getMetaMatrix().getDimensions().get(dim) != null) {
                if (hdr == null) {
                    hdr = getMetaMatrix().getHeader(dim);
                } else {
                    hdr = ExportUtil.tabbed(hdr, getMetaMatrix().getHeader(dim));
                }
            }
        }
        hdr = ExportUtil.tabbed(hdr, variable);
        // Add n significant decimals to meta matrix 
        addString(wr, ExportUtil.carrageReturnLineFeed(hdr));
        for (String groupKey : getSortedKeys()) {
            MatrixBO group = (MatrixBO) getValue(groupKey);
            String groupKeyValue = null;
            if (groupIsDimension) {
                groupKeyValue = groupKey;
            }
            for (String rowKey : group.getSortedKeys()) {
                if (rowKey == null) {
                    continue;
                }
                String rowKeyValue = "";
                if (groupIsDimension) {
                    rowKeyValue = ExportUtil.tabbed(groupKeyValue, rowKey);
                } else {
                    rowKeyValue = ExportUtil.tabbed(rowKey);
                }
                if (colIsDimension || cellIsDimension) {
                    MatrixBO row = (MatrixBO) group.getValue(rowKey);
                    for (String colKey : row.getSortedKeys()) {
                        String colKeyValue = rowKeyValue;
                        if (colIsDimension) {
                            colKeyValue = ExportUtil.tabbed(colKeyValue, colKey);
                        }
                        if (cellIsDimension) {
                            MatrixBO cell = (MatrixBO) row.getValue(colKey);
                            for (String cellKey : cell.getSortedKeys()) {
                                String cellKeyValue = ExportUtil.tabbed(colKeyValue, cellKey);
                                addString(wr, ExportUtil.tabbedCRLF(cellKeyValue, formatCell(cell, cellKey, nDec)));
                            }
                        } else {
                            addString(wr, ExportUtil.tabbedCRLF(colKeyValue, formatCell(row, colKey, nDec)));
                        }
                    }
                } else {
                    addString(wr, ExportUtil.tabbedCRLF(rowKeyValue, formatCell(group, rowKey, nDec)));
                }
            }
        }
    }

    public void addString(Writer wr, String append) {
        ImrIO.write(wr, append);
    }

    public void asPage(Writer wr) {
        List<String> cols = new ArrayList<>();
        for (String rowKey : getRowKeys()) {
            MatrixBO row = (MatrixBO) getRowValue(rowKey);
            for (String colKey : row.getSortedKeys()) {
                if (!cols.contains(colKey)) {
                    cols.add(colKey);
                }
            }
        }
        asPage(cols, wr);
    }

    public void asPage(List<String> cols, Writer wr) {
        String hdr = "Row";
        String res = "";
        for (String col : cols) {
            hdr = ExportUtil.tabbed(hdr, col);
        }
        ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(hdr));
        for (String rowKey : getSortedRowKeys()) {
            String s = rowKey;
            MatrixBO row = (MatrixBO) getRowValue(rowKey);
            for (String colKey : cols) {
                Object o = row.getValue(colKey);
                s = ExportUtil.tabbed(s, o);
            }
            ImrIO.write(wr, ExportUtil.carrageReturnLineFeed(s));
        }
    }

    private static Object formatCell(MatrixBO m, String key, Integer nDec) {
        Object value = m.getValue(key);
        if (value instanceof Double) {
            Double val = (Double) value;
            if (nDec == 0) {
                value = (Integer) val.intValue();
            } else {
                value = Conversion.formatDoubletoDecimalString(val, nDec);
            }
        }
        return value;
    }

    /**
     * Sum up over keys. if sub matrices, sum up recursively.
     *
     * @return
     */
    public Double getSum() {
        Double res = null;
        for (String key : getKeys()) {
            Object o = getValue(key);
            if (o == null) {
                continue;
            }
            if (o instanceof MatrixBO) {
                if (res == null) {
                    res = 0d;
                }
                Double s = ((MatrixBO) o).getSum();
                res += s != null ? s : 0d;
            }
            if (o instanceof Double) {
                if (res == null) {
                    res = 0d;
                }
                res += ((Double) o);
            }
        }
        return res;
    }

    public void clear() {
        matrix.clear();
    }

    public boolean isEmpty() {
        return getKeys().isEmpty();
    }

    /**
     *
     * @param copyFrom
     * @return a copy matrix
     */
    private static MatrixBO copy(MatrixBO copyFrom) {
        MatrixBO copyTo = new MatrixBO(copyFrom.getDimension(), copyFrom.getMetaMatrix());
        for (String key : copyFrom.getKeys()) {
            Object o = copyFrom.getValue(key);
            if (o != null && o instanceof MatrixBO) {
                o = copy((MatrixBO) o);
            }
            copyTo.setValue(key, o);
        }
        return copyTo;
    }

    public MatrixBO copy() {
        return copy(this);
    }

    public String getDimension() {
        return dimension;
    }

    public String asTable() {
        StringWriter sw = new StringWriter();
        asTable(sw);
        return sw.toString();
    }

}
