/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HierarchicalData.RelationalConversion;

import HierarchicalData.RelationalConversion.NamingConventions.ITableMakerNamingConvention;
import HierarchicalData.RelationalConversion.NamingConventions.DoNothingNamingConvention;
import HierarchicalData.HierarchicalData;
import XMLHandling.SchemaReader;
import JaxbReflection.NameConversions;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Handles conversion to untyped relational data model.
 *
 * Maps each complexType to a table, except those explicitly handled as
 * leafNodes, by leafNodehandler.
 *
 * The converter is designed to be generic, but needs information about keys and
 * what to consider leaf nodes (leafNodeTypes), and how to handle value
 * extraction from leaf nodes (leafNodeHandler). Key values must be identified
 * for all types that may have non-leaf children in hierarchical model (xsd
 * annotations).
 *
 * Assumes that each data type occurs either as leaf nodes only, or always at
 * the same depth and lateral position in the tree. Assumes each complex type in
 * the xsd-schema is represented by a class with the same name (except that first letter may be upper case). And that getters
 * exists for all non-leaf levels, such that the getter for a node "nodename" is
 * named getNodename (note capitalization of first character).
 *
 * Each type is mapped to a table, and each node to a column Foreign keys
 * referring tables representing higher levels in the tree are added to each
 * table. Naming schemes for the resulting tables and columns are controlled by
 * a naming convention (INamingConvention). This is sometimes necessary to avoid
 * column name conflicts with the added foreign keys.
 *
 * @author Edvin Fuglebakk edvin.fuglebakk@imr.no
 */
public class TableMaker {

    protected Map<String, List<SchemaReader.SchemaNode>> leafNodes;
    protected Map<String, List<String>> keys;
    protected ILeafNodeHandler<String> leafNodeHandler;
    protected ITableMakerNamingConvention namingConvention;
    protected Map<String, List<SchemaReader.SchemaNode>> tree; // encodes the hiearchy of complex types. Maps name of compex type to complex type children.

    protected Map<String, String> xmlToJavaTypeMap;
    protected Map<String, String> javaToXmlTypeMap;
    
    /**
     * creates TableMaker from XSD.
     *
     * @param schemaReader schemareader instance for extracting information from
     * xsd
     * @param leafNodeHandler Used to extract column value from leaf nodes.
     */
    public TableMaker(SchemaReader schemaReader, ILeafNodeHandler<String> leafNodeHandler) {

        this.namingConvention = new DoNothingNamingConvention();
        this.leafNodeHandler = leafNodeHandler;

        this.xmlToJavaTypeMap = new HashMap<>();
        this.javaToXmlTypeMap = new HashMap();
        
        List<String> complexTypes = schemaReader.getComplextypes();
        for (String xmlType: complexTypes){
            String javaType = xmlType.substring(0, 1).toUpperCase() + xmlType.substring(1);
            xmlToJavaTypeMap.put(xmlType, javaType);
            javaToXmlTypeMap.put(javaType, xmlType);
        }
        
        for (String type : leafNodeHandler.getLeafNodeComplexTypes()) {
            if (complexTypes.contains(type)) {
                complexTypes.remove(type);
            }
        }

        this.leafNodes = new HashMap<>();
        for (String ct : complexTypes) {
            List<SchemaReader.SchemaNode> nodes = schemaReader.getNodes(ct);
            List<SchemaReader.SchemaNode> leafNodes = new ArrayList<>();
            for (SchemaReader.SchemaNode node : nodes) {
                if (!complexTypes.contains(node.getType())) {
                    leafNodes.add(node);
                }
            }
            this.leafNodes.put(ct, leafNodes);
        }

        this.keys = new HashMap<>();
        for (String ct : complexTypes) {
            Set<String> keys = schemaReader.getKeys(ct);
            List<String> orderedKeys = new ArrayList<>();
            for (String k : keys) {
                orderedKeys.add(k);
            }
            this.keys.put(ct, orderedKeys);
        }

        // allows for explicit tree traversel using reflection. Can be used to get rid of list of children in the class HierarchicalData.
        // is otherwise usefule for ensuring that children are processed in order.
        this.tree = new HashMap<>();
        for (String ct : complexTypes) {
            List<SchemaReader.SchemaNode> nodes = schemaReader.getNodes(ct);
            List<SchemaReader.SchemaNode> subTypes = new ArrayList<>();
            for (SchemaReader.SchemaNode node : nodes) {
                if (complexTypes.contains(node.getType()) && !leafNodeHandler.getLeafNodeComplexTypes().contains(node.getType())) {
                    subTypes.add(node);
                }
            }
            this.tree.put(ct, subTypes);
        }

    }

    /**
     * @return The naming convention used
     */
    public ITableMakerNamingConvention getNamingConvention() {
        return namingConvention;
    }

    /**
     * @param namingConvention The naming convention to use.
     */
    public void setNamingConvention(ITableMakerNamingConvention namingConvention) {
        this.namingConvention = namingConvention;
    }

    /**
     * @return A set of java type names in the hieararchical model (corresponding to
     * complex types in the xsd schema) for which this TableMaker can construct
     * tables.
     */
    public Set<String> getTypes() {
        Set<String> javaTypes = new HashSet<>();
        for (String s: leafNodes.keySet()){
            javaTypes.add(this.xmlToJavaTypeMap.get(s));
        }
        return javaTypes;
    }

    /**
     * Get headers for the table. Includes also keys from parent levels. Ordered
     * as in getRow
     *
     * @param data
     * @return
     * @throws
     * HierarchicalData.RelationalConversion.NamingConventions.ITableMakerNamingConvention.NamingException
     */
    public List<String> getHeaders(HierarchicalData data) throws ITableMakerNamingConvention.NamingException, RelationalConvertionException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        String typeName = this.javaToXmlTypeMap.get(data.getClass().getSimpleName());
        List<String> headers = this.getParentKeyNames(data.getParent());
        for (SchemaReader.SchemaNode node : this.leafNodes.get(typeName)) {
            headers.add(this.namingConvention.getColumnName(typeName, node.getName()));
        }
        return headers;
    }

    //get keyValues for argument and all parents formatted with current naming convention in order
    protected List<String> getParentKeyNames(HierarchicalData parent) throws ITableMakerNamingConvention.NamingException {
        if (parent == null) {
            return new ArrayList<>();
        }
        List<String> keys = this.getParentKeyNames(parent.getParent());
        String typeName = this.javaToXmlTypeMap.get(parent.getClass().getSimpleName());
        for (String key : this.keys.get(typeName)) {
            keys.add(this.namingConvention.getColumnName(typeName, key));
        }
        return keys;
    }

    //get keyValues values for argument and all parents in order
    protected List<String> getParentKeyValues(HierarchicalData parent) throws ITableMakerNamingConvention.NamingException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (parent == null) {
            return new ArrayList<>();
        }
        List<String> keyValues = this.getParentKeyValues(parent.getParent());

        String typeName = this.javaToXmlTypeMap.get(parent.getClass().getSimpleName());

        for (String key : this.keys.get(typeName)) {
            String methodname = NameConversions.getter(key);
            Method getter = parent.getClass().getMethod(methodname, null);
            keyValues.add(this.leafNodeHandler.extractValue(getter.invoke(parent, null)));
        }
        return keyValues;
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
     */
    public List<String> getRow(HierarchicalData data) throws ITableMakerNamingConvention.NamingException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, RelationalConvertionException {
        String typeName = this.javaToXmlTypeMap.get(data.getClass().getSimpleName());
        
        List<String> values = this.getParentKeyValues(data.getParent());
        for (SchemaReader.SchemaNode node : this.leafNodes.get(typeName)) {
            String methodname = NameConversions.getter(node.getName());
            Method getter = data.getClass().getMethod(methodname, null);
            values.add(this.leafNodeHandler.extractValue(getter.invoke(data, null)));
        }
        return values;
    }

    /**
     * Returns a list of rows for a data table. Excluding header.
     *
     * @param data
     * @return
     */
    public <T extends HierarchicalData> List<List<String>> getTableContent(List<T> data) throws ITableMakerNamingConvention.NamingException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, RelationalConvertionException {
        List<List<String>> table = new LinkedList<>();
        Iterator<List<String>> it = this.getTableContentIterator(data);
        while(it.hasNext()){
            table.add(it.next());
        }
        return table;
    }

    /**
     * Creates an iterator to extract a row for each object in data
     * @param <T>
     * @param data
     * @return 
     */
    public <T extends HierarchicalData> Iterator<List<String>> getTableContentIterator(List<T> data) {
        return new TableContentIterator(this, data);
    }

    //gathers all decendants in list accordint to their class, and organises these lists so that they are mapped from the name of corresponding complexType
    protected Map<String, List<HierarchicalData>> getAllData(HierarchicalData data, Map<String, List<HierarchicalData>> mapping) throws RelationalConvertionException {

        String className = data.getClass().getSimpleName();
        String ctname = this.javaToXmlTypeMap.get(className);
        if (!mapping.containsKey(ctname)) {
            if (!this.getTypes().contains(className)) {
                throw new RelationalConvertionException("Can not deal with Data of type: " + className);
            }
            List<HierarchicalData> list = new LinkedList<>();
            mapping.put(ctname, list);
        }

        mapping.get(ctname).add(data);

        List<HierarchicalData> children = data.getChildren();
        if (children == null) {
            return mapping;
        }

        for (HierarchicalData c : children) {
            mapping = getAllData(c, mapping);
        }
        return mapping;
    }

    /**
     * Constructs all tables with headers. Including table for root type. Tables
     * and columns are named according to the current naming convention.
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
    public Map<String, List<List<String>>> getAllTables(HierarchicalData root) throws ITableMakerNamingConvention.NamingException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, RelationalConvertionException {

        Map<String, List<HierarchicalData>> tableObjects = new HashMap<>();
        tableObjects = this.getAllData(root, tableObjects);

        Map<String, List<List<String>>> tables = new HashMap<>();
        for (String ct : tableObjects.keySet()) {
            List<HierarchicalData> d = tableObjects.get(ct);
            List<List<String>> data = new LinkedList<>();
            if (d.size() > 0) {
                data.add(this.getHeaders(d.get(0)));
                data.addAll(this.getTableContent(d));
            }

            tables.put(this.namingConvention.getTableName(ct), data);
        }
        return tables;
    }

    /**
     * Removes the identified node from any subsequently produced relational
     * representations.
     *
     * @param type complexType as defined in schema
     * @param nodeName name of node as defined in schema.
     * @throws RelationalConvertionException
     */
    public void dropNode(String type, String nodeName) throws RelationalConvertionException {
        for (String ct : this.leafNodes.keySet()) {
            for (SchemaReader.SchemaNode node : this.leafNodes.get(ct)) {
                if (node.getName().equals(nodeName)) {
                    if (this.keys.get(ct).contains(node)) {
                        throw new RelationalConvertionException("Can not remove key node");
                    }
                    this.leafNodes.get(ct).remove(node);
                    break;
                }
            }
        }
    }

    private static class TableContentIterator<T extends HierarchicalData> implements Iterator{

        protected Iterator<T> it;
        protected TableMaker owner;
        
        public TableContentIterator(TableMaker owner, List<T> data) {
            this.it = data.iterator();
            this.owner = owner;
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public List<String> next() {
            try {
                return this.owner.getRow(it.next());
            } catch (Exception ex) {
                ex.printStackTrace();
                throw new NoSuchElementException();
            }
        }
    }

}