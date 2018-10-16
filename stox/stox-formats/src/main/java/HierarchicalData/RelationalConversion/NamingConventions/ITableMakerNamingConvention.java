/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HierarchicalData.RelationalConversion.NamingConventions;

/**
 * Interface for encoding a naming convention for Relational Converter
 * Defines how table names are constructed from type names in the hierarchical model and how field names are constructed from node names in the hierarchical model
 * @author Edvin Fuglebakk edvin.fuglebakk@imr.no
 */
public interface ITableMakerNamingConvention {
    
    public String getTableName(String typeName) throws NamingException;
    public String getTypeName(String tableName) throws NamingException;
    public String getColumnName(String typename, String nodeName) throws NamingException;
    public String getNodeName(String tablename, String columnName)throws NamingException;
    
    /**
     * @return Informal description of the naming convention for documentation purposes.
     */
    public String getDescription();

    public static class NamingException extends Exception {

        public NamingException() {
        }
        public NamingException(String m) {
            super(m);
        }
    }

}
