/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Biotic.Biotic1;

import HierarchicalData.RelationalConversion.NamingConventions.ITableMakerNamingConvention;
import HierarchicalData.RelationalConversion.NamingConventions.SuffixOnKeysAndConflictsNamingConvention;
import HierarchicalData.RelationalConversion.RelationalConvertionException;
import XMLHandling.SchemaReader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;

/**
 *
 * @author Edvin Fuglebakk edvin.fuglebakk@imr.no
 */
public class Biotic1NamingConvention implements ITableMakerNamingConvention{
    
    protected ITableMakerNamingConvention namingConvention;
    
    public Biotic1NamingConvention(InputStream schemaStream) throws RelationalConvertionException, JAXBException, ParserConfigurationException{
    Map<String, String> typeMap = new HashMap<>();
        typeMap.put("MissionsType", "Missions");
        typeMap.put("MissionType", "Mission");
        typeMap.put("FishstationType", "Station");
        typeMap.put("CatchsampleType", "Catch");
        typeMap.put("IndividualType", "Individual");
        typeMap.put("PreyType", "Prey");
        typeMap.put("TagType", "Tag");
        typeMap.put("PreylengthType", "Preylength");
        typeMap.put("AgedeterminationType", "Age");
        typeMap.put("CopepodedevstageType", "Copepodestage");

    Map <String, String> colMap = new HashMap<>();
        colMap.put("MissionsType", "Missions");
        colMap.put("MissionType", "Mi");
        colMap.put("FishstationType", "St");
        colMap.put("CatchsampleType", "Ca");
        colMap.put("IndividualType", "Ind");
        colMap.put("PreyType", "Prey");
        colMap.put("TagType", "Tag");
        colMap.put("PreylengthType", "Plength");
        colMap.put("AgedeterminationType", "Age");
        colMap.put("CopepodedevstageType", "Copep");
        
        namingConvention = new SuffixOnKeysAndConflictsNamingConvention(new SchemaReader(schemaStream), colMap, typeMap);
    }

    @Override
    public String getTableName(String typeName) throws NamingException {
        return namingConvention.getTableName(typeName);
    }

    @Override
    public String getTypeName(String tableName) throws NamingException {
        return namingConvention.getTypeName(tableName);
    }

    @Override
    public String getColumnName(String typename, String nodeName) throws NamingException {
        return namingConvention.getColumnName(typename, nodeName);
    }

    @Override
    public String getNodeName(String tablename, String columnName) throws NamingException {
        return namingConvention.getNodeName(tablename, columnName);
    }

    @Override
    public String getDescription() {
        return namingConvention.getDescription();
    }
    
}
