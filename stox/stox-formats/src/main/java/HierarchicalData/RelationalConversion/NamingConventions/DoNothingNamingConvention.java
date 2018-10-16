/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HierarchicalData.RelationalConversion.NamingConventions;

/**
 *
 * @author Edvin Fuglebakk edvin.fuglebakk@imr.no
 */
public class DoNothingNamingConvention implements ITableMakerNamingConvention{

    @Override
    public String getTableName(String typeName) throws NamingException {
        return typeName;
    }

    @Override
    public String getTypeName(String tableName) {
        return tableName;
    }

    @Override
    public String getColumnName(String typename, String nodeName) throws NamingException {
        return nodeName;
    }

    @Override
    public String getNodeName(String tablename, String fieldname) throws NamingException {
        return fieldname;
    }

    @Override
    public String getDescription() {
        return "Names are derived from the hiearchical model defined by an xsd-schema.\n" + 
        "For each ComplexType in the xsd-schema a corresponding table is constructed with the same name." +
        "The columns are named as the corresponding nodes in the hierarchical model";
    }
    
}
