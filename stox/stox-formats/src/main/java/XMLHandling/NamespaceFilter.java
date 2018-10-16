/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package XMLHandling;

import java.util.Set;
import org.xml.sax.*;
import org.xml.sax.helpers.XMLFilterImpl;

/**
 * Filter to use for unmarshalling older formats to backwards compatible
 * formats. A namespace should be considered backwards compatible in this sense
 * only if it contains only addition of non-mandatory nodes (No structural
 * changes, changes of names etc).
 *
 * @author Edvin Fuglebakk edvin.fuglebakk@imr.no
 */
public class NamespaceFilter extends XMLFilterImpl {

    private final String namespace;
    private final Set<String> compatible;

    /**
     * Constructs a filter which allows formats in namespaces specified by
     * compatibleNamespaces to be unmarshalled into types corresponding to
     * assumedNamespace.
     *
     * @param assumedNamespace
     * @param compatibleNamespaces
     */
    public NamespaceFilter(String assumedNamespace, Set<String> compatibleNamespaces) {
        this.namespace = assumedNamespace;
        this.compatible = compatibleNamespaces;
    }

    @Override
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        if (uri.length()==0){
            throw new SAXException("empty uri (Parser not namespace aware?)");
        }
        if (!this.canFilter(uri)) {
            throw new SAXException("Namespace " + uri + " is not recognized by this NamespaceFilter.");
        }
        super.endElement(this.namespace, localName, qName);
    }

    @Override
    public void startElement(String uri, String localName, String qName,
            Attributes atts) throws SAXException {
        if (uri.length()==0){
            throw new SAXException("empty uri (Parser not namespace aware?)");
        }
        if (!this.canFilter(uri)) {
            throw new SAXException("Namespace " + uri + " is not recognized by this NamespaceFilter.");
        }
        super.startElement(this.namespace, localName, qName, atts);
    }
    
    public boolean canFilter(String namespace){
        return namespace.equals(this.namespace) || this.compatible.contains(namespace);
    }
}
