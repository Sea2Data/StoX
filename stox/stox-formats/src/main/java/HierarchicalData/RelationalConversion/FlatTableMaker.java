/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HierarchicalData.RelationalConversion;

import HierarchicalData.HierarchicalData;
import HierarchicalData.RelationalConversion.NamingConventions.ITableMakerNamingConvention;
import XMLHandling.SchemaReader;
import JaxbReflection.NameConversions;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Relational Converter that produces flat tables for objects correspdonding to
 * nodes whose decendants are all non-repeating elements.
 *
 * The corresponding node may allow repeating elements in the Hierarchical model
 * (xsd), as long as none is actually present in the object.
 *
 * Otherwise, assumptions about the hierarchical model should be treated as for
 * TableMaker for now, although they can be relaxed a bit.
 *
 * @author Edvin Fuglebakk edvin.fuglebakk@imr.no
 */
public class FlatTableMaker extends TableMaker {

    public FlatTableMaker(SchemaReader schemaReader, ILeafNodeHandler<String> leafNodeHandler) {
        super(schemaReader, leafNodeHandler);
    }

    /**
     * Get headers for the table. Includes also keys from parent levels. Ordered
     * as in getRowDownwards
     *
     * @param data
     * @return
     * @throws
     * HierarchicalData.RelationalConversion.NamingConventions.ITableMakerNamingConvention.NamingException
     * @throws
     * HierarchicalData.RelationalConversion.RelationalConvertionException if
     * data has repeated complexType elements.
     */
    public List<String> getHeaders(HierarchicalData data) throws ITableMakerNamingConvention.NamingException, RelationalConvertionException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        return this.getHeadersDownWards(data, true);
    }

    protected List<String> getHeadersDownWards(HierarchicalData data, boolean getparentkeysOnFirst) throws ITableMakerNamingConvention.NamingException, RelationalConvertionException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String typeName = data.getClass().getSimpleName();
        List<String> headers;
        if (getparentkeysOnFirst) {
            headers = this.getParentKeyNames(data.getParent());
        } else {
            headers = new ArrayList<>();
        }

        for (SchemaReader.SchemaNode node : this.leafNodes.get(typeName)) {
            headers.add(this.namingConvention.getColumnName(typeName, node.getName()));
        }
        for (SchemaReader.SchemaNode subType : this.tree.get(data.getClass().getSimpleName())) {
            String getterName = NameConversions.getter(subType.getName());
            Method getter = data.getClass().getMethod(getterName, null);
            Object child = getter.invoke(data, null);
            if (child instanceof List) {
                List childCollection = (List) child;
                if (childCollection.size() == 1) {
                    child = childCollection.get(0);
                } else if (childCollection.size() == 0) {
                    child = null;
                } else {
                    throw new RelationalConvertionException("Criteria for flat conversion not met, child represented by collection obtained by: " + getterName);
                }

            }
            if (child != null) {
                HierarchicalData childc = (HierarchicalData) child;
                headers.addAll(this.getHeadersDownWards(childc, false));
            }
        }
        return headers;
    }

    /**
     * Return data as a row Each row contains keys from all ancestor levels.
     * ordered as in getHeader
     *
     * @param data
     * @return
     * @throws
     * HierarchicalData.RelationalConversion.NamingConventions.ITableMakerNamingConvention.NamingException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws
     * HierarchicalData.RelationalConversion.RelationalConvertionException if
     * data has repeated complexType elements.
     */
    public List<String> getRow(HierarchicalData data) throws ITableMakerNamingConvention.NamingException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, RelationalConvertionException {
        return this.getRowDownwards(data, true);
    }

    protected List<String> getRowDownwards(HierarchicalData data, boolean getParentKeysOnFirst) throws ITableMakerNamingConvention.NamingException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, RelationalConvertionException {
        String typeName = data.getClass().getSimpleName();
        List<String> values;
        if (getParentKeysOnFirst) {
            values = this.getParentKeyValues(data.getParent());
        } else {
            values = new ArrayList<>();
        }

        for (SchemaReader.SchemaNode node : this.leafNodes.get(typeName)) {
            String methodname = NameConversions.getter(node.getName());
            Method getter = data.getClass().getMethod(methodname, null);
            values.add(this.leafNodeHandler.extractValue(getter.invoke(data, null)));
        }
        for (SchemaReader.SchemaNode subType : this.tree.get(data.getClass().getSimpleName())) {
            String getterName = NameConversions.getter(subType.getName());
            Method getter = data.getClass().getMethod(getterName, null);
            Object child = getter.invoke(data, null);
            if (child instanceof List) {
                List childCollection = (List) child;
                if (childCollection.size() == 1) {
                    child = childCollection.get(0);
                } else if (childCollection.size() == 0) {
                    child = null;
                } else {
                    throw new RelationalConvertionException("Criteria for flat conversion not met, child represented by collection obtained by: " + getterName);
                }

            }
            if (child != null) {
                HierarchicalData childc = (HierarchicalData) child;
                values.addAll(this.getRowDownwards(childc, false));
            }
        }
        return values;
    }

    /**
     * Constructs single flat table with headers. Tables and columns are named
     * according to the current naming convention.
     *
     * Tables are constructed for this and all decendant levels, even if those
     * of lower levels contain all data for higher levels as well
     *
     *
     * @param root element whoose children should be used to populate a flat table.
     * @return A map from table names to A list of table rows, with the first
     * table row a header. Contains only one table, but formulated like this for
     * consitency with TableMaker.
     * @throws
     * HierarchicalData.RelationalConversion.NamingConventions.ITableMakerNamingConvention.NamingException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @Override
    public Map<String, List<List<String>>> getAllTables(HierarchicalData root) throws ITableMakerNamingConvention.NamingException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, RelationalConvertionException {

        Map<String, List<List<String>>> tables = new HashMap<>();

        List<HierarchicalData> d = new ArrayList<>();
        d.addAll(root.getChildren());
        List<List<String>> data = new LinkedList<>();
        if (d.size() > 0) {
            data.add(this.getHeaders(d.get(0)));
            data.addAll(this.getTableContent(d));
        }

        tables.put(this.namingConvention.getTableName(root.getClass().getSimpleName()), data);

        return tables;
    }

}
