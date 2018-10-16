/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HierarchicalData.RelationalConversion;

import HierarchicalData.RelationalConversion.NamingConventions.SuffixOnAllColumnsNamingConvention;
import HierarchicalData.RelationalConversion.NamingConventions.ITableMakerNamingConvention;
import BioticTypes.v1_4.FishstationType;
import BioticTypes.v1_4.MissionsType;
import BioticTypes.v1_4.StringDescriptionType;
import HierarchicalData.IO;
import XMLHandling.SchemaReader;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Edvin Fuglebakk edvin.fuglebakk@imr.no
 */
public class TableMakerTest {

    TableMaker tablemaker;

    public TableMakerTest() {
    }

    @BeforeClass
    public static void setUpClass() {

    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws JAXBException, ParserConfigurationException {
        ILeafNodeHandler dummyLeafNodeHandler = new DummyLeafNodeHandler();
        this.tablemaker = new TableMaker(new SchemaReader(TableMakerTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), dummyLeafNodeHandler);
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of getNamingConvention method, of class TableMaker.
     */
    @Test
    public void testGetNamingConvention() {
        System.out.println("getNamingConvention");
        SuffixOnAllColumnsNamingConvention rf = new SuffixOnAllColumnsNamingConvention();
        this.tablemaker.setNamingConvention(rf);
        ITableMakerNamingConvention result = this.tablemaker.getNamingConvention();
        assertTrue(result == rf);
    }

    /**
     * Test of setNamingConvention method, of class TableMaker.
     */
    @Test
    public void testSetNamingConvention() {
        System.out.println("setNamingConvention");
        ITableMakerNamingConvention namingConvention = new SuffixOnAllColumnsNamingConvention();
        this.tablemaker.setNamingConvention(namingConvention);
    }

    /**
     * Test of getTypes method, of class TableMaker.
     */
    @Test
    public void testGetTypes() throws JAXBException, ParserConfigurationException {
        TableMaker makerWSDT = new TableMaker(new SchemaReader(TableMakerTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), new DummyLeafNodeHandler("StringDescriptionType"));
        TableMaker makerWoSDT = new TableMaker(new SchemaReader(TableMakerTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), new DummyLeafNodeHandler());

        assertTrue(makerWSDT.getTypes().size() > 0);
        assertTrue(makerWoSDT.getTypes().size() > 0);
        assertTrue(makerWoSDT.getTypes().contains("StringDescriptionType"));
        assertTrue(!makerWSDT.getTypes().contains("StringDescriptionType"));
    }

    /**
     * Test of getHeaders method, of class TableMaker.
     */
    @Test
    public void testGetHeadersDefaultNamingConvention() throws JAXBException, ITableMakerNamingConvention.NamingException, ParserConfigurationException, XMLStreamException, RelationalConvertionException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        System.out.println("getHeaders");
        MissionsType missions = IO.parse(TableMakerTest.class.getClassLoader().getResourceAsStream("test.xml"), MissionsType.class);
        FishstationType st = missions.getMission().get(0).getFishstation().get(0);

        TableMaker makerWSDT = new TableMaker(new SchemaReader(TableMakerTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), new DummyLeafNodeHandler("StringDescriptionType"));
        TableMaker makerWoSDT = new TableMaker(new SchemaReader(TableMakerTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), new DummyLeafNodeHandler());

        List<String> result = makerWSDT.getHeaders(st);
        assertTrue(result.contains("platform"));
        assertTrue(result.contains("serialno"));
        assertTrue(result.contains("trawlquality"));
        assertTrue(!result.contains("catchsample"));

        List<String> resultWo = makerWoSDT.getHeaders(st);
        assertTrue(resultWo.contains("platform"));
        assertTrue(!resultWo.contains("trawlquality"));
    }

    /**
     * Test of getHeaders method, of class TableMaker.
     */
    @Test
    public void testGetHeadersDefaultCustomNamingConvention() throws JAXBException, ITableMakerNamingConvention.NamingException, ParserConfigurationException, XMLStreamException, RelationalConvertionException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        System.out.println("getHeaders");
        MissionsType missions = IO.parse(TableMakerTest.class.getClassLoader().getResourceAsStream("test.xml"), MissionsType.class);
        FishstationType st = missions.getMission().get(0).getFishstation().get(0);

        TableMaker makerWSDT = new TableMaker(new SchemaReader(TableMakerTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), new DummyLeafNodeHandler("StringDescriptionType"));
        makerWSDT.setNamingConvention(new DummyNamingConvention());

        TableMaker makerWoSDT = new TableMaker(new SchemaReader(TableMakerTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), new DummyLeafNodeHandler());
        makerWoSDT.setNamingConvention(new DummyNamingConvention());

        List<String> result = makerWSDT.getHeaders(st);
        assertTrue(result.contains("ColumnFromTypeMissionTypeAndNodeplatform"));
        assertTrue(result.contains("ColumnFromTypeFishstationTypeAndNodeplatform"));
        assertTrue(result.contains("ColumnFromTypeFishstationTypeAndNodeserialno"));
        assertTrue(result.contains("ColumnFromTypeFishstationTypeAndNodetrawlquality"));
        assertTrue(!result.contains("ColumnFromTypeFishstationTypeAndNodecatchsample"));

        List<String> resultWo = makerWoSDT.getHeaders(st);
        assertTrue(resultWo.contains("ColumnFromTypeMissionTypeAndNodeplatform"));
        assertTrue(resultWo.contains("ColumnFromTypeFishstationTypeAndNodeserialno"));
        assertTrue(!resultWo.contains("ColumnFromTypeFishstationTypeAndNodeplatform"));
        assertTrue(!resultWo.contains("ColumnFromTypeFishstationTypeAndNodetrawlquality"));
    }

    /**
     * Test of getRow method, of class TableMaker.
     */
    @Test
    public void testGetRow() throws JAXBException, ParserConfigurationException, ITableMakerNamingConvention.NamingException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, XMLStreamException, RelationalConvertionException {
        System.out.println("getRow");
        MissionsType missions = IO.parse(TableMakerTest.class.getClassLoader().getResourceAsStream("test.xml"), MissionsType.class);
        FishstationType st = missions.getMission().get(0).getFishstation().get(0);

        ILeafNodeHandler bioticHandler = new BioticHandler();
        TableMaker bioticTableMaker = new TableMaker(new SchemaReader(TableMakerTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), bioticHandler);
        bioticTableMaker.setNamingConvention(new DummyNamingConvention());

        List<String> result = bioticTableMaker.getRow(st);
        List<String> headers = bioticTableMaker.getHeaders(st);
        assertTrue(headers.size() == result.size());

    }

    /**
     * Test of getRow method, of class TableMaker.
     */
    @Test
    public void testGetRowDummy() throws JAXBException, ParserConfigurationException, ITableMakerNamingConvention.NamingException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, XMLStreamException, RelationalConvertionException {
        System.out.println("getRow");
        MissionsType missions = IO.parse(TableMakerTest.class.getClassLoader().getResourceAsStream("dummy.xml"), MissionsType.class);
        FishstationType st = missions.getMission().get(0).getFishstation().get(0);

        ILeafNodeHandler bioticHandler = new BioticHandler();
        TableMaker bioticTableMaker = new TableMaker(new SchemaReader(TableMakerTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), bioticHandler);
        bioticTableMaker.setNamingConvention(new DummyNamingConvention());

        Map<String, String> correctValues = new HashMap<>();//hardcoded
        correctValues.put(bioticTableMaker.getNamingConvention().getColumnName("FishstationType", "platform"), "9416");
        correctValues.put(bioticTableMaker.getNamingConvention().getColumnName("MissionType", "platform"), "3654");
        correctValues.put(bioticTableMaker.getNamingConvention().getColumnName("FishstationType", "fishingdepthmin"), "80.0");
        correctValues.put(bioticTableMaker.getNamingConvention().getColumnName("FishstationType", "trawlquality"), "8");
        correctValues.put(bioticTableMaker.getNamingConvention().getColumnName("FishstationType", "landingsite"), "310");
        List<String> result = bioticTableMaker.getRow(st);
        List<String> headers = bioticTableMaker.getHeaders(st);
        for (String node : correctValues.keySet()) {
            String value = correctValues.get(node);
            assertEquals(result.get(headers.indexOf(node)), value);
        }
    }

    @Test
    public void testGetTable() throws JAXBException, ParserConfigurationException, ITableMakerNamingConvention.NamingException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, XMLStreamException, RelationalConvertionException {
        System.out.println("getTable");
        MissionsType missions = IO.parse(TableMakerTest.class.getClassLoader().getResourceAsStream("test.xml"), MissionsType.class);
        List<FishstationType> st = missions.getMission().get(0).getFishstation();

        ILeafNodeHandler bioticHandler = new BioticHandler();
        TableMaker bioticTableMaker = new TableMaker(new SchemaReader(TableMakerTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), bioticHandler);
        bioticTableMaker.setNamingConvention(new DummyNamingConvention());

        List<List<String>> result = bioticTableMaker.getTableContent(st);
        assertEquals(result.size(), st.size());
        assertEquals(result.get(0).size(), bioticTableMaker.getRow(st.get(0)).size());
    }

    @Test
    public void testGetAllTables() throws JAXBException, ParserConfigurationException, ITableMakerNamingConvention.NamingException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, RelationalConvertionException, XMLStreamException {
        System.out.println("getAllTables");
        MissionsType missions = IO.parse(TableMakerTest.class.getClassLoader().getResourceAsStream("test.xml"), MissionsType.class);
        List<FishstationType> st = missions.getMission().get(0).getFishstation(); //this file contain only one mission

        ILeafNodeHandler<String> bioticHandler = new BioticHandler();
        TableMaker bioticTableMaker = new TableMaker(new SchemaReader(TableMakerTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), bioticHandler);
        bioticTableMaker.setNamingConvention(new DummyNamingConvention());

        Map<String, List<List<String>>> tables = bioticTableMaker.getAllTables(missions);
        assert tables.containsKey(bioticTableMaker.getNamingConvention().getTableName("FishstationType"));

        List<List<String>> result = bioticTableMaker.getTableContent(st);
        List<List<String>> fstable = tables.get(bioticTableMaker.getNamingConvention().getTableName("FishstationType"));
        fstable.remove(0);
        assertEquals(result.size(), fstable.size());

    }

    @Test
    public void testGetAllTablesNonRoot() throws JAXBException, ParserConfigurationException, ITableMakerNamingConvention.NamingException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, RelationalConvertionException, XMLStreamException {
        System.out.println("getAllTables non root");
        MissionsType missions = IO.parse(TableMakerTest.class.getClassLoader().getResourceAsStream("test.xml"), MissionsType.class);
        List<FishstationType> st = missions.getMission().get(0).getFishstation(); //this file contain only one mission

        ILeafNodeHandler<String> bioticHandler = new BioticHandler();
        TableMaker bioticTableMaker = new TableMaker(new SchemaReader(TableMakerTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), bioticHandler);
        bioticTableMaker.setNamingConvention(new DummyNamingConvention());

        Map<String, List<List<String>>> tables = bioticTableMaker.getAllTables(missions.getMission().get(0));
        assert tables.containsKey(bioticTableMaker.getNamingConvention().getTableName("FishstationType"));

        List<List<String>> result = bioticTableMaker.getTableContent(st);
        List<List<String>> fstable = tables.get(bioticTableMaker.getNamingConvention().getTableName("FishstationType"));
        fstable.remove(0);
        assertEquals(result.size(), fstable.size());

    }

    @Test
    public void testDropColumnGetRowAndHeader() throws JAXBException, ParserConfigurationException, ITableMakerNamingConvention.NamingException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, RelationalConvertionException, XMLStreamException {
        System.out.println("dropColumn");
        MissionsType missions = IO.parse(TableMakerTest.class.getClassLoader().getResourceAsStream("test.xml"), MissionsType.class);
        FishstationType st = missions.getMission().get(0).getFishstation().get(0);

        ILeafNodeHandler bioticHandler = new BioticHandler();
        TableMaker bioticTableMaker = new TableMaker(new SchemaReader(TableMakerTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), bioticHandler);
        bioticTableMaker.setNamingConvention(new DummyNamingConvention());

        List<String> result = bioticTableMaker.getRow(st);
        List<String> headers = bioticTableMaker.getHeaders(st);
        assertEquals(headers.size(), result.size());

        bioticTableMaker.dropNode("FishstationType", "platform");
        List<String> resultDropped = bioticTableMaker.getRow(st);
        List<String> headersDropped = bioticTableMaker.getHeaders(st);
        assertEquals(headersDropped.size(), resultDropped.size());
        assertEquals(headersDropped.size(), headers.size() - 1);
        assertEquals(resultDropped.size(), result.size() - 1);

    }

    @Test
    public void testDropColumnGetTable() throws JAXBException, ParserConfigurationException, ITableMakerNamingConvention.NamingException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, RelationalConvertionException, XMLStreamException {
        System.out.println("dropColumn");
        MissionsType missions = IO.parse(TableMakerTest.class.getClassLoader().getResourceAsStream("test.xml"), MissionsType.class);
        FishstationType st = missions.getMission().get(0).getFishstation().get(0);

        ILeafNodeHandler bioticHandler = new BioticHandler();
        TableMaker bioticTableMaker = new TableMaker(new SchemaReader(TableMakerTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), bioticHandler);
        bioticTableMaker.setNamingConvention(new DummyNamingConvention());

        Map<String, List<List<String>>> result = bioticTableMaker.getAllTables(missions);
        bioticTableMaker.dropNode("FishstationType", "platform");
        Map<String, List<List<String>>> resultDropped = bioticTableMaker.getAllTables(missions);
        String tabName = bioticTableMaker.getNamingConvention().getTableName("FishstationType");
        assertEquals(resultDropped.get(tabName).get(0).size(), result.get(tabName).get(0).size() - 1);
    }

    public void testDropColumnKey() throws JAXBException, ParserConfigurationException, ITableMakerNamingConvention.NamingException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, RelationalConvertionException, XMLStreamException {
        System.out.println("dropColumn");
        MissionsType missions = IO.parse(TableMakerTest.class.getClassLoader().getResourceAsStream("test.xml"), MissionsType.class);
        FishstationType st = missions.getMission().get(0).getFishstation().get(0);

        ILeafNodeHandler bioticHandler = new BioticHandler();
        TableMaker bioticTableMaker = new TableMaker(new SchemaReader(TableMakerTest.class.getClassLoader().getResourceAsStream("bioticv1_4.xsd")), bioticHandler);
        bioticTableMaker.setNamingConvention(new DummyNamingConvention());

        try {
            bioticTableMaker.dropNode("FishstationType", "serialno");
            fail("Exception Expected");
        } catch (RelationalConvertionException e) {

        }

    }

    protected static class DummyLeafNodeHandler implements ILeafNodeHandler {

        protected Set<String> leafNodes;

        public DummyLeafNodeHandler() {
            leafNodes = new HashSet<>();
        }

        public DummyLeafNodeHandler(String leafnode) {
            leafNodes = new HashSet<>();
            leafNodes.add(leafnode);
        }

        @Override
        public String extractValue(Object node) throws ClassCastException {
            return null;
        }

        @Override
        public Set getLeafNodeComplexTypes() {
            return this.leafNodes;
        }
    }

    protected static class DummyNamingConvention implements ITableMakerNamingConvention {

        public DummyNamingConvention() {
        }

        @Override
        public String getTableName(String typeName) throws NamingException {
            return "TableFromType" + typeName;
        }

        @Override
        public String getTypeName(String tableName) {
            return "TypeFromTable" + tableName;
        }

        @Override
        public String getColumnName(String typename, String nodeName) throws NamingException {
            return "ColumnFromType" + typename + "AndNode" + nodeName;
        }

        @Override
        public String getNodeName(String tablename, String columnName) throws NamingException {
            return "NodeFromTable" + tablename + "AndColumn" + columnName;
        }

        @Override
        public String getDescription() {
            return "Dummy Naming convention for testing purposes";
        }
    }

    protected static class BioticHandler<T extends Object> implements ILeafNodeHandler {

        public BioticHandler() {
        }

        @Override
        public String extractValue(Object node) throws ClassCastException {
            if (node == null) {
                return "";
            }
            if (node instanceof StringDescriptionType) {
                return ((StringDescriptionType) node).getValue();
            } else {
                return node.toString();
            }
        }

        @Override
        public Set getLeafNodeComplexTypes() {
            Set<String> leafComplexTypes = new HashSet<>();
            leafComplexTypes.add("StringDescriptionType");
            return leafComplexTypes;
        }
    }

}
