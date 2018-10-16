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
import java.util.List;
import java.util.Map;

/**
 * Relational Converter that produces flat tables by merging with all ancestral objects.
 * 
 * Assumptions about structure of hierarchical model is as for TableMaker.
 *
 * @author Edvin Fuglebakk edvin.fuglebakk@imr.no
 */
public class MergedTableMaker extends TableMaker {

    public MergedTableMaker(SchemaReader schemaReader, ILeafNodeHandler<String> leafNodeHandler) {
        super(schemaReader, leafNodeHandler);
    }

    //get column names for argument and all parents formatted with current naming convention in order
    protected List<String> getParentColumnNames(HierarchicalData parent) throws ITableMakerNamingConvention.NamingException {

        if (parent == null) {
            return new ArrayList<>();
        }
        List<String> parentColumNames = this.getParentColumnNames(parent.getParent());

        String typeName = this.javaToXmlTypeMap.get(parent.getClass().getSimpleName());
        for (SchemaReader.SchemaNode node : this.leafNodes.get(typeName)) {
            parentColumNames.add(this.namingConvention.getColumnName(typeName, node.getName()));
        }
        return parentColumNames;
    }

    //get column values for argument and all parents in order
    protected List<String> getParentColumnValues(HierarchicalData parent) throws ITableMakerNamingConvention.NamingException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (parent == null) {
            return new ArrayList<>();
        }
        List<String> keyValues = this.getParentColumnValues(parent.getParent());

        String typeName = this.javaToXmlTypeMap.get(parent.getClass().getSimpleName());

        for (SchemaReader.SchemaNode node : this.leafNodes.get(typeName)) {
            String methodname = NameConversions.getter(node.getName());
            Method getter = parent.getClass().getMethod(methodname, null);
            keyValues.add(this.leafNodeHandler.extractValue(getter.invoke(parent, null)));
        }
        return keyValues;
    }

    /**
     * Get headers for this table Include also all columns for ancesteral
     * levels. Ordered as in getRow
     *
     * @param data
     * @return
     * @throws
     * HierarchicalData.RelationalConversion.NamingConventions.ITableMakerNamingConvention.NamingException
     */
    @Override
    public List<String> getHeaders(HierarchicalData data) throws ITableMakerNamingConvention.NamingException {
        String typeName = this.javaToXmlTypeMap.get(data.getClass().getSimpleName());
        List<String> headers = this.getParentColumnNames(data.getParent());
        for (SchemaReader.SchemaNode node : this.leafNodes.get(typeName)) {
            headers.add(this.namingConvention.getColumnName(typeName, node.getName()));
        }
        return headers;
    }

    /**
     * Get the row for this data. Include also all columns for ancesteral
     * levels. Ordered as in getHeaders.
     *
     * @param data
     * @return
     * @throws
     * HierarchicalData.RelationalConversion.NamingConventions.ITableMakerNamingConvention.NamingException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @Override
    public List<String> getRow(HierarchicalData data) throws ITableMakerNamingConvention.NamingException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String typeName = this.javaToXmlTypeMap.get(data.getClass().getSimpleName());
        List<String> values = this.getParentColumnValues(data.getParent());
        for (SchemaReader.SchemaNode node : this.leafNodes.get(typeName)) {
            String methodname = NameConversions.getter(node.getName());
            Method getter = data.getClass().getMethod(methodname, null);
            values.add(this.leafNodeHandler.extractValue(getter.invoke(data, null)));
        }
        return values;
    }

    /**
     * Constructs all tables with headers. Including table for root type. Tables
     * and columns are named according to the current naming convention.
     *
     * Tables are constructed for all levels, even if those of lower levels
     * contain all data for higher levels as well
     *
     *
     * @param root
     * @return A map from table names to A list of table rows, with the first
     * table row a header.
     * @throws
     * HierarchicalData.RelationalConversion.NamingConventions.ITableMakerNamingConvention.NamingException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     */
    @Override
    public Map<String, List<List<String>>> getAllTables(HierarchicalData root) throws ITableMakerNamingConvention.NamingException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, RelationalConvertionException {
        return super.getAllTables(root);
    }
}
