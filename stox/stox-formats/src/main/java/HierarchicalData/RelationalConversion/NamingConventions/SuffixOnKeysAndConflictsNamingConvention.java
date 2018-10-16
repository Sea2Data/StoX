/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HierarchicalData.RelationalConversion.NamingConventions;

import HierarchicalData.RelationalConversion.RelationalConvertionException;
import XMLHandling.SchemaReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Naming convention that puts a suffix on all keys and all columns with
 * potential naming conflicts upon merge.
 *
 * @author Edvin Fuglebakk edvin.fuglebakk@imr.no
 */
public class SuffixOnKeysAndConflictsNamingConvention implements ITableMakerNamingConvention {

    protected SchemaReader schema;
    protected Map<String, String> columnSuffixMap;
    protected Map<String, String> typeTableMap;
    protected Set<String> conflicts;
    protected String separator = ".";

    /**
     *
     * @param schema A schemareader for the format this Naming convention
     * applies to
     * @param columnSuffixMap maps complex types in schema to suffix to use for
     * columns in tables corresponding to that complexType
     * @param typeTableMap maps complex types in schema to table names to use
     * for the corresponding tables.
     */
    public SuffixOnKeysAndConflictsNamingConvention(SchemaReader schema, Map<String, String> columnSuffixMap, Map<String, String> typeTableMap) throws RelationalConvertionException {
        this.schema = schema;
        this.columnSuffixMap = columnSuffixMap;
        this.typeTableMap = typeTableMap;
        this.conflicts = findNamingConflicts();
        this.checkTypes();
    }

    /**
     *
     * @param schema A schemareader for the format this Naming convention
     * applies to.
     *
     * Table names will be determined by removing the the substring "Type" from
     * the end of the corresponding typename. These table names will be used as
     * suffixes for columns (if conflict, or key)
     */
    public SuffixOnKeysAndConflictsNamingConvention(SchemaReader schema) throws RelationalConvertionException {
        this.schema = schema;
        this.typeTableMap = makeTypeTableMap();
        this.columnSuffixMap = this.typeTableMap;
        this.conflicts = findNamingConflicts();
        this.checkTypes();
    }

    @Override
    public String getTableName(String typeName) throws NamingException {
        String tableName = this.typeTableMap.get(typeName);
        if (tableName == null) {
            throw new NamingException("Type: " + typeName + " is not mapped to table name");
        }
        return tableName;
    }

    @Override
    public String getTypeName(String tableName) throws NamingException {
        for (Entry<String, String> e : this.typeTableMap.entrySet()) {
            if (e.getValue().equals(tableName)) {
                return e.getKey();
            }
        }
        throw new NamingException("Table name: " + tableName + " not mapped to Type.");
    }

    @Override
    public String getColumnName(String typename, String nodeName) throws NamingException {
        if (this.conflicts.contains(nodeName) || this.schema.getKeys(typename).contains(nodeName)) {
            return (nodeName + this.separator + this.columnSuffixMap.get(typename));
        }
        return nodeName;
    }

    @Override
    public String getNodeName(String tablename, String columnName) throws NamingException {
        if (columnName.lastIndexOf(this.separator) == -1) {
            return columnName;
        }
        if (this.conflicts.contains(columnName.substring(0, columnName.lastIndexOf(this.separator)))) {
            return columnName.substring(0, columnName.lastIndexOf(this.separator));
        } else {
            String typeName = this.getTypeName(tablename);
            for (String key : this.schema.getKeys(typeName)) {
                if (columnName.equals(this.getColumnName(typeName, key))) {
                    return key;
                }
            }
        }
        return columnName;
    }

    @Override
    public String getDescription() {
        String docstring = "Names are derived from the hiearchical model defined by an xsd-schema with target namespace: " + this.schema.getTargetNameSpace() + "\n"
                + "For each ComplexType in the xsd-schema a corresponding table is constructed named as follows:\n";
        for (Map.Entry<String, String> e : this.typeTableMap.entrySet()) {
            docstring += e.getKey() + " : " + e.getValue() + "\n";
        }

        docstring += "The columns are named as the corresponding nodes in the hierarchical model, but with a suffix identifying the type for keys and for potential naming conflicts:\n";
        for (Map.Entry<String, String> e : this.columnSuffixMap.entrySet()) {
            docstring += e.getKey() + " : " + this.separator + e.getValue() + "\n";
        }
        return docstring;
    }

    private Set<String> findNamingConflicts() {
        Set<String> columns = new HashSet<>();
        Set<String> columnConflicts = new HashSet<>();
        for (String type : this.schema.getComplextypes()) {
            List<SchemaReader.SchemaNode> nodes = this.schema.getNodes(type);
            for (SchemaReader.SchemaNode n : nodes) {
                if (columns.contains(n.getName())) {
                    columnConflicts.add(n.getName());
                }
                columns.add(n.getName());
            }
        }
        return columnConflicts;
    }

    private void checkTypes() throws RelationalConvertionException {
        List<String> schematypes = this.schema.getComplextypes();

        if (!this.typeTableMap.keySet().equals(this.columnSuffixMap.keySet())) {
            throw new RelationalConvertionException("Provided types are not the same for table and column conversion");
        }

        for (String t : this.typeTableMap.keySet()) {
            if (!schematypes.contains(t)) {
                throw new RelationalConvertionException("Type " + t + " not in schema.");
            }
        }
    }

    String getSeparator() {
        return this.separator;
    }

    private Map<String, String> makeTypeTableMap() throws RelationalConvertionException {
        Map<String, String> tableMap = new HashMap<>();
        List<String> types = this.schema.getComplextypes();
        for (String s : types) {
            if (s.endsWith("Type")) {
                tableMap.put(s, s.substring(0, s.length() - "Type".length()));
            }
            else{
                throw new RelationalConvertionException("Naming convention not followed in schema: complexType " + s + " does not end with \'Type\'");
            }
        }
        return tableMap;
    }

}
