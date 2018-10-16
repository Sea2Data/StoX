/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HierarchicalData.RelationalConversion.NamingConventions;

/**
 * Naming convention for conversion from NMD formats that follow xml convention and has all types constructed with the suffix Type
 * The convention avoids duplicate column names across all tables, and identifies foreign keys and facilitates simple syntax for merging in R.
 * @author Edvin Fuglebakk edvin.fuglebakk@imr.no
 */
public class SuffixOnAllColumnsNamingConvention implements ITableMakerNamingConvention {
    
    @Override
    /**
     * return typeName with suffix "Type" removed
     */
    public String getTableName(String typeName) throws NamingException{
        if (!typeName.endsWith("Type")){
            throw new NamingException();
        }
        return typeName.substring(0, typeName.length()-"Type".length());
    }

    @Override
    /**
     * return tableName with suffix "Type" added
     */
    public String getTypeName(String tableName) {
        return(tableName+"Type");
    }

    @Override
    /**
     * return nodename.tablename where tablename is obtained with getTableName
     */
    public String getColumnName(String typename, String nodeName) throws NamingException {
        return nodeName + "." + getTableName(typename);
    }

    @Override
    public String getNodeName(String tablename, String columnname) throws NamingException {
        if (!columnname.endsWith("."+tablename)){
            throw new NamingException();
        }
        return columnname.substring(0, columnname.length() - ("."+tablename).length());
    }

    @Override
    public String getDescription() {
        return "Names are derived from the hiearchical model defined by an xsd-schema.\n" + 
        "For each ComplexType in the xsd-schema a corresponding table is constructed. This table is named after the complex type, but without the suffix \'Type\'\n " +
        "The columns are named as the corresponding nodes in the hierarchical model, but has with the suffix \'.<table name>\' added.";
    }
    
}
