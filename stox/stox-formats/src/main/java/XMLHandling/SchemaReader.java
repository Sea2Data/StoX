/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package XMLHandling;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import javax.xml.transform.stream.StreamSource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import DocumentationTypes.ImrDocType;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAnnotated;
import org.apache.ws.commons.schema.XmlSchemaAppInfo;
import org.apache.ws.commons.schema.XmlSchemaAttribute;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaDocumentation;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaObjectTable;
import org.apache.ws.commons.schema.XmlSchemaParticle;
import org.apache.ws.commons.schema.XmlSchemaSequence;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * Reads information from xsd schema. Current implementation is not properly
 * namespace-aware. Will extract data for first occurance  of each complex type.
 *
 * @author Edvin Fuglebakk edvin.fuglebakk@imr.no
 */
public class SchemaReader {

    protected XmlSchema schema;
    protected List<String> complexTypes;
    protected Map<String, List<SchemaNode>> nodes;
    protected Map<String, Set<String>> keys;
    protected Set<XmlSchemaElement> repeatedComplexTypes; // complex type names that may occur on several levels in schema
    protected Map<String, Map<String, ImrDocType>> documentation;

    //add check namespaces for repeated complexTypes
    public SchemaReader(InputStream xsdStream) throws JAXBException, ParserConfigurationException {
        this.complexTypes = new ArrayList<>();
        this.nodes = new HashMap<>();
        this.keys = new HashMap<>();
        this.repeatedComplexTypes = new HashSet<>();
        this.documentation = new HashMap<>();
        XmlSchemaCollection schemaCol = new XmlSchemaCollection();
        this.schema = schemaCol.read(new StreamSource(xsdStream), null);
        this.processSchema();
    }

    private void processSchema() throws JAXBException, ParserConfigurationException {
        XmlSchemaObjectTable schemaObjectTable = this.schema.getElements();
        Iterator iterator = schemaObjectTable.getValues();
        while (iterator.hasNext()) {
            XmlSchemaElement element = (XmlSchemaElement) iterator.next();
            XmlSchemaType schemaType = element.getSchemaType();
            if (schemaType instanceof XmlSchemaComplexType) {
                processComplexType(element);
            } else if (schemaType instanceof XmlSchemaSimpleType) {

            } else {
                assert false;
            }
        }
    }

    private void processComplexType(XmlSchemaElement element) throws JAXBException, ParserConfigurationException {
        String elementName = element.getName();
        String typeName = element.getSchemaTypeName().getLocalPart();

        if (this.complexTypes.contains(typeName)) {
            this.repeatedComplexTypes.add(element);
            return;
        } else {
            this.complexTypes.add(typeName);
        }

        List<SchemaNode> nodes = new ArrayList<>();
        Set<String> keys = new HashSet<>();
        Map<String, ImrDocType> documentation = new HashMap<>();

        XmlSchemaSequence schemaSequence;

        //process subelements
        XmlSchemaParticle particle = ((XmlSchemaComplexType) element.getSchemaType()).getParticle();
        if (particle instanceof XmlSchemaSequence) {
            schemaSequence = (XmlSchemaSequence) particle;

            Iterator iterator = schemaSequence.getItems().getIterator();
            while (iterator.hasNext()) {
                XmlSchemaElement subElement = (XmlSchemaElement) iterator.next();
                XmlSchemaType subType = subElement.getSchemaType();
                if (subType instanceof XmlSchemaComplexType) {
                    processComplexType(subElement);
                    nodes.add(new SchemaNode(subElement.getName(), subElement.getSchemaTypeName().getLocalPart()));

                } else if (subType instanceof XmlSchemaSimpleType) {
                    if (this.isKey(subElement)) {
                        keys.add(subElement.getName());
                    }
                    nodes.add(new SchemaNode(subElement.getName(), subElement.getSchemaTypeName().getLocalPart()));

                }
                if (subElement.getAnnotation() != null) {
                    documentation.put(subElement.getName(), this.getDocumentation(subElement));
                }

            }
        }

        //process attributes
        XmlSchemaObjectCollection attributes = ((XmlSchemaComplexType) element.getSchemaType()).getAttributes();
        Iterator iterator = attributes.getIterator();
        while (iterator.hasNext()) {
            XmlSchemaAttribute attr = (XmlSchemaAttribute) iterator.next();
            nodes.add(new SchemaNode(attr.getName(), attr.getSchemaTypeName().getLocalPart()));
            if (this.isKey(attr)) {
                keys.add(attr.getName());
            }
            if (attr.getAnnotation() != null) {
                documentation.put(attr.getName(), this.getDocumentation(attr));
            }
        }

        this.nodes.put(typeName, nodes);
        this.keys.put(typeName, keys);
        this.documentation.put(typeName, documentation);

    }

    /**
     * Get all complex types defined for the schema.
     *
     * @return
     */
    public List<String> getComplextypes() {
        return this.complexTypes;
    }

    /**
     * Get all nodes (elements and attributes) declared on the given complexType
     *
     * @param complexType
     * @return
     */
    public List<SchemaNode> getNodes(String complexType) {
        return this.nodes.get(complexType);
    }

    /**
     * Determines which nodes on the given complexTypes are keys (this
     * information is extracted from annotations).
     *
     * @param complexType
     * @return
     */
    public Set<String> getKeys(String complexType) {
        return this.keys.get(complexType);
    }

    /**
     * Extracts annotated documentation from schema
     *
     * @param complexType for which documentation will be extracted
     * @return
     */
    public Map<String, ImrDocType> getDocumentation(String complexType) {
        return this.documentation.get(complexType);
    }

    //determine if element is key (generalize to elements ?
    private boolean isKey(XmlSchemaAnnotated annotatedElement) {
        assert annotatedElement != null;
        if (annotatedElement.getAnnotation() == null) {
            return false;
        }
        Iterator annotationIterator = annotatedElement.getAnnotation().getItems().getIterator();
        while (annotationIterator.hasNext()) {
            Object o = annotationIterator.next();
            if (o instanceof XmlSchemaAppInfo) {
                XmlSchemaAppInfo ai = (XmlSchemaAppInfo) o;
                NodeList appinfoNodes = ai.getMarkup();
                for (int i = 0; i < appinfoNodes.getLength(); i++) {
                    if (appinfoNodes.item(i) != null && appinfoNodes.item(i).getLocalName() != null && appinfoNodes.item(i).getLocalName().equals("imrApp")) {
                        NodeList imrappNodes = appinfoNodes.item(i).getChildNodes();
                        for (int j = 0; j < imrappNodes.getLength(); j++) {
                            if (imrappNodes.item(i).getLocalName().equals("key")) {
                                return true;
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    private ImrDocType getDocumentation(XmlSchemaAnnotated annotatedElement) throws JAXBException, ParserConfigurationException {
        Iterator it = annotatedElement.getAnnotation().getItems().getIterator();
        org.w3c.dom.Node docnode = null;
        while (it.hasNext()) {
            Object next = it.next();
            if (next instanceof XmlSchemaDocumentation) {
                NodeList markup = ((XmlSchemaDocumentation) next).getMarkup();
                for (int i = 0; i < markup.getLength(); i++) {
                    if (markup.item(i) != null && markup.item(i).getLocalName() != null && markup.item(i).getLocalName().equals("imrDoc")) {
                        docnode = (org.w3c.dom.Node) markup.item(i);
                    }
                }
            }
        }
        if (docnode == null) {
            DocumentationTypes.ObjectFactory of = new DocumentationTypes.ObjectFactory();
            return of.createImrDocType();
        }

        JAXBContext jc = JAXBContext.newInstance(ImrDocType.class);
        Unmarshaller u = jc.createUnmarshaller();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();
        org.w3c.dom.Node copyNode = doc.importNode(docnode, true);
        doc.appendChild(copyNode);

        JAXBElement<ImrDocType> root = u.unmarshal(doc, ImrDocType.class);
        ImrDocType doctype = root.getValue();
        return doctype;
    }

    public String getTargetNameSpace(){
        return this.schema.getTargetNamespace();
    }
    
    public static class SchemaNode {

        private String name;
        private String type;

        public SchemaNode(String name, String type) {
            this.name = name;
            this.type = type;
        }

        /**
         * Name of node
         *
         * @return
         */
        public String getName() {
            return name;
        }

        /**
         * type of node
         *
         * @return
         */
        public String getType() {
            return type;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 41 * hash + Objects.hashCode(this.name);
            hash = 41 * hash + Objects.hashCode(this.type);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final SchemaNode other = (SchemaNode) obj;
            if (!Objects.equals(this.name, other.name)) {
                return false;
            }
            if (!Objects.equals(this.type, other.type)) {
                return false;
            }
            return true;
        }
    }
}
