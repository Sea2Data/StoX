package Examples.EchosounderExample;

import Echosounder.Echosounder1.Echosounder1Handler;
import Echosounder.Echosounder1.Echosounder1LeafNodeHandler;
import HierarchicalData.HierarchicalData;
import HierarchicalData.RelationalConversion.DelimitedOutputWriter;
import javax.xml.bind.JAXBException;
import HierarchicalData.RelationalConversion.NamingConventions.ITableMakerNamingConvention;
import HierarchicalData.RelationalConversion.NamingConventions.SuffixOnKeysAndConflictsNamingConvention;
import HierarchicalData.RelationalConversion.RelationalConvertionException;
import HierarchicalData.RelationalConversion.TableMaker;
import XMLHandling.SchemaReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Edvin Fuglebakk edvin.fuglebakk@imr.no
 */
public class ConvertXMLToCSV {

    public final static void main(String[] args) throws JAXBException, IOException, ClassNotFoundException, InstantiationException, IllegalAccessException, ParserConfigurationException, ITableMakerNamingConvention.NamingException, NoSuchMethodException, IllegalArgumentException, InvocationTargetException, RelationalConvertionException, XMLStreamException, SAXException {

        if (args.length < 2) {
            System.out.println("Converts echosounder xml to flat csv files.");
            System.out.println("Usage: <xmlfile> <target directory for csv files>");
            System.exit(0);
        }
        String filename = args[0];
        String targetDir = args[1];

        DelimitedOutputWriter writer = new DelimitedOutputWriter("\t", "\\", "#", ".csv", "");

        Map<String, String> naming;
        naming = new HashMap<>();
        naming.put("acocatType", "Acocat");
        naming.put("acocatListType", "AcocatList");
        naming.put("chTypeType", "ChType");
        naming.put("distance", "Distance");
        naming.put("distanceList", "DistanceList");
        naming.put("echosounderDatasetType", "EchosounderDataset");
        naming.put("frequencyType", "Frequency");
        naming.put("saByAcocatType", "SaByAcocat");
        
        TableMaker tablemaker = new TableMaker(new SchemaReader(ConvertXMLToCSV.class.getClassLoader().getResourceAsStream("echov1.xsd")), new Echosounder1LeafNodeHandler());
        tablemaker.setNamingConvention(new SuffixOnKeysAndConflictsNamingConvention(new SchemaReader(ConvertXMLToCSV.class.getClassLoader().getResourceAsStream("echov1.xsd")), naming, naming));

        Echosounder1Handler handler = new Echosounder1Handler();
        HierarchicalData data = handler.read(new FileInputStream(filename));
        Map<String, List<List<String>>> tables = tablemaker.getAllTables(data);
        writer.writeDelimitedFiles(tables, new File(targetDir), tablemaker.getNamingConvention().getDescription());

    }
}
