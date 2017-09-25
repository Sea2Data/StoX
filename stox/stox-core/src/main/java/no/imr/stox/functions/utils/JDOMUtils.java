/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import no.imr.stox.bo.HeaderElm;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.jdom2.util.IteratorIterable;

/**
 *
 * @author aasmunds
 */
public class JDOMUtils {

    public static Supplier<Stream<Element>> getElementStreamByTagName(Element parent, String nodeName) {
        IteratorIterable<Element> it = parent.getDescendants(new ElementFilter(nodeName));
        return () -> StreamSupport.stream(it.spliterator(), false);
    }

    public static List<Element> getElementsByTagName(Element parent, String nodeName) {
        return getElementStreamByTagName(parent, nodeName).get().collect(Collectors.toList());
    }

    public static Boolean hasChildElements(Element e) {
        return !e.getChildren().isEmpty();
    }

    public static Stream<Element> getLeafElementStream(Element parent) {
        return parent.getChildren().stream()
                .filter(n -> !hasChildElements(n));
    }

    /* Predicate to include an element if children exists**/
    public static Predicate<Element> defaultBranchPredicate = e -> hasChildElements(e) || e.getAttributes().size() > 0;

    public static Stream<Element> getBranchElementStream(Element parent) {
        return getBranchElementStream(parent, defaultBranchPredicate);
    }

    public static Stream<Element> getBranchElementStream(Element parent, Predicate<Element> p) {
        return parent.getChildren().stream()
                .filter(p == null ? defaultBranchPredicate : p);
    }

    public static void consumeTree(Stream<Element> ns, Consumer<? super Attribute> attAction, Consumer<? super Element> elmAction) {
        consumeTree(ns, attAction, elmAction, null);
    }

    public static void consumeTree(Stream<Element> ns, Consumer<? super Attribute> attAction, Consumer<? super Element> elmAction, String leafLevel) {
        consumeTree(ns, attAction, elmAction, leafLevel, defaultBranchPredicate);
    }

    public static void consumeTree(Stream<Element> ns, Consumer<? super Attribute> attAction, Consumer<? super Element> elmAction, String leafLevel,
            Predicate<Element> branchPredicate) {
        ns.forEachOrdered(n -> {
            if (elmAction != null) {
                if (leafLevel == null || leafLevel.equals(n.getName())) {
                    elmAction.accept(n);
                }
            }
            if (attAction != null) {
                n.getAttributes().stream().forEach(c -> attAction.accept(c));
            }

            if (leafLevel == null || !leafLevel.equals(n.getName())) {
                consumeTree(getBranchElementStream(n, branchPredicate), attAction, elmAction, leafLevel);
            }
        });
    }

    public static void consumeTree(Stream<Element> ns, Consumer<? super Object> nodeAction) {
        consumeTree(ns, nodeAction, nodeAction);
    }

    public static List<String> getBranchNames(Stream<Element> st) {
        List<String> l = new ArrayList<>();
        JDOMUtils.consumeTree(st, null, elm -> {
            if (!JDOMUtils.hasChildElements(elm)) {
                return;
            }
            l.add(elm.getName());
        });
        return l;
    }

    public static void printTree(Stream<Element> e) {
        consumeTree(e, node -> {
            printNode(node);
        });
    }

    public static void printNode(Object a) {
        String name = a instanceof Attribute ? ((Attribute) a).getName() : ((Element) a).getName();
        String value = a instanceof Attribute ? ((Attribute) a).getValue() : ((Element) a).getText().trim();
        if (value == null || value.isEmpty()) {
            return;
        }
        System.out.println(name + ":" + value);
    }

    public static List<Element> readXML(String fileName, String rootLevel) {
        try {
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(new File(fileName));
            Element docRoot = doc.getRootElement();
            if (rootLevel == null) {
                rootLevel = docRoot.getName();
            }
            return JDOMUtils.getElementsByTagName(docRoot, rootLevel);
        } catch (JDOMException | IOException ex) {
            Logger.getLogger(JDOMUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static void getDataHeadersFromAttributes(Element elm, String topLevel, Collection<HeaderElm> set) {
        if (elm == null) {
            return;
        }
        if (!elm.getName().equals(topLevel)) {
            getDataHeadersFromAttributes(elm.getParentElement(), topLevel, set);
        }
        elm.getAttributes().stream().forEach(a -> {
            set.add(new HeaderElm(elm.getName(), a.getName()));
        });
    }

    public static void getDataHeaders(Stream<Element> list, String topLevel, String level, Collection<HeaderElm> set) {
        list.forEach(elm -> {
            if (elm.getName().equals(level)) {
                getDataHeadersFromAttributes(elm, topLevel, set);
                getLeafElementStream(elm)
                        .filter(e -> !e.getValue().trim().isEmpty())
                        .forEach(e -> set.add(new HeaderElm(elm.getName(), e.getName())));
            } else {
                getDataHeaders(getBranchElementStream(elm), topLevel, level, set);
            }
        });
    }

    public static void insertElement(Element elm, int idx, Element source) {
        if (source == null) {
            return;
        }
        insertElement(elm, idx, source.getName(), source.getText());
    }

    public static void insertElement(Element elm, int idx, String name, String text) {
        if (name == null || text == null || name.isEmpty() || text.isEmpty()) {
            return;
        }
        if (elm == null) {
            return;
        }
        Element c = new Element(name, elm.getNamespace());
        c.setText(text);
        if (idx == -1) {
            elm.addContent(c);
        } else {
            elm.addContent(idx, c);
        }
    }

    public static Element getTargetElm(Element elm, String elmLevel) {
        if (elm == null) {
            return null;
        }
        if (elm.getName().equals(elmLevel)) {
            return elm;
        }
        return getTargetElm(elm.getParentElement(), elmLevel);
    }

    public static String getNodeValue(Element node, String nodeName) {
        return getNodeValue(node, nodeName, "");
    }

    public static String getNodeValue(Element node, String nodeName, String missingStr) {
        String res;
        if (node == null) {
            return null;
        }
        Attribute a = node.getAttribute(nodeName);
        if (a != null) {
            res = a.getValue();
        } else {
            res = node.getChildText(nodeName, node.getNamespace());
            if (res == null || res.isEmpty()) {
                res = missingStr;
            }
        }
        return res;
    }

    public static Element getChild(Element node, String nodeName) {
        return node.getChild(nodeName, node.getNamespace());
    }

    public static void setAttribute(Element elm, boolean atStart, String name, String text) {
        List<Attribute> atts = new ArrayList<>(elm.getAttributes());
        if (atStart) {
            atts.stream().forEach(a -> elm.removeAttribute(a));
        }
        elm.setAttribute(name, text);
        if (atStart) {
            atts.stream().forEach(a -> elm.setAttribute(a.getName(), a.getValue()));
        }
    }

    public static String getAttributeValue(Element elm, String name) {
        return elm.getAttributeValue(name);
    }
}
