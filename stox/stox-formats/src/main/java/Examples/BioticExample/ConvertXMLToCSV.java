package Examples.BioticExample;

import Biotic.Biotic1.Biotic1Handler;
import Biotic.Biotic1.Biotic1LeafNodeHandler;
import Biotic.Biotic1.Biotic1NamingConvention;
import HierarchicalData.HierarchicalData;
import HierarchicalData.RelationalConversion.DelimitedOutputWriter;
import javax.xml.bind.JAXBException;
import HierarchicalData.RelationalConversion.NamingConventions.ITableMakerNamingConvention;
import HierarchicalData.RelationalConversion.RelationalConvertionException;
import HierarchicalData.RelationalConversion.TableMaker;
import XMLHandling.SchemaReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
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
            System.out.println("Converts biotic xml to flat csv files.");
            System.out.println("Usage: <xmlfile> <target directory for csv files>");
            System.exit(0);
        }
        String filename = args[0];
        String targetDir = args[1];

        DelimitedOutputWriter writer = new DelimitedOutputWriter("\t", "\\", "#", ".csv", "");

        TableMaker tablemaker = new TableMaker(new SchemaReader(ConvertXMLToCSV.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), new Biotic1LeafNodeHandler());
        tablemaker.setNamingConvention(new Biotic1NamingConvention(ConvertXMLToCSV.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")));

        Biotic1Handler handler = new Biotic1Handler();
        HierarchicalData missions = handler.read(new FileInputStream(filename));
        Map<String, List<List<String>>> tables = tablemaker.getAllTables(missions);
        tables.remove("Missions");
        writer.writeDelimitedFiles(tables, new File(targetDir), tablemaker.getNamingConvention().getDescription());

    }
}
