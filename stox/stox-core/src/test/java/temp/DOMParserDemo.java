/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package temp;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import no.imr.stox.util.base.Conversion;
import no.imr.stox.functions.utils.JDOMUtils;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.input.SAXBuilder;
import org.junit.Test;

public class DOMParserDemo {


    void printNode(Object a) {
        String name = a instanceof Attribute ? ((Attribute) a).getName() : ((Element) a).getName();
        String value = a instanceof Attribute ? ((Attribute) a).getValue() : ((Element) a).getText();
        if (value == null || value.isEmpty()) {
            return;
        }
        System.out.println(name + ":" + value);
    }

    @Test
    public void test() {

        try {
            File inputFile = new File("C:/Users/aasmunds/workspace/stox/project/Capelin BS 2015-old/Capelin BS 2015/input/biotic/Vilnyus.xml");
            SAXBuilder builder = new SAXBuilder();
            Document doc = builder.build(inputFile);
            String dataType = "fishstation";
            Supplier<Stream<Element>> fs = JDOMUtils.getElementStreamByTagName(doc.getRootElement(), dataType);
            Element clonedRoot = doc.getRootElement().clone();
            Supplier<Stream<Element>> fsCloned = JDOMUtils.getElementStreamByTagName(clonedRoot, dataType);
            //Supplier<Stream<Element>> fs = getElementStreamByTagName(doc.getRootElement(), dataType);
            
            Set<String> s = new HashSet<>();
            JDOMUtils.consumeTree(fs.get(), null, elm -> {
                //    printNode(a);
                if(!JDOMUtils.hasChildElements(elm)) {
                    return;
                }
                // branch element names set
                s.add(elm.getName());
            });
            List<Element> detached = new ArrayList<>();
            fsCloned.get().forEach(elm -> {
                String ser = elm.getAttributeValue("serialno");
                int i = Conversion.safeStringtoIntegerNULL(ser);
                if (i <= 248) {
                    detached.add(elm);
                    return;
                }
                elm.setAttribute("MyAttribute", ser);
            });
            detached.stream().forEach(elm -> {
                elm.detach();
            });
            JDOMUtils.printTree(fsCloned.get());
            fsCloned.get().forEach(elm -> {
                String ser = elm.getAttributeValue("serialno");
                System.out.println(ser); //
            });
            fs.get().forEach(elm -> {
                String ser = elm.getAttributeValue("serialno");
                System.out.println(ser); //
            });
            /*IteratorIterable<Element> it = doc.getRootElement().getDescendants(new ElementFilter("fishstation"));
            StreamSupport.stream(it.spliterator(), true);
            for (Content descendant : it) {
                if (descendant.getCType().equals(Content.CType.Element)) {
                    Element elm = (Element) descendant;
                    String ser = elm.getAttributeValue("serialno");
                    System.out.println(ser); //
                    if (ser.equals("1")) {
                        elm.detach();
                    }
                }
            }
            it = doc.getRootElement().getDescendants(new ElementFilter("fishstation"));
            for (Content descendant : it) {
                if (descendant.getCType().equals(Content.CType.Element)) {
                    Element elm = (Element) descendant;
                    String ser = elm.getAttributeValue("serialno");
                    System.out.println(ser); //
                    if (ser.equals("1")) {
                        elm.detach();
                    }
                }
            }*/
 /*            
            DocumentBuilderFactory dbFactory
                    = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            //doc.getDocumentElement().normalize();
            String dataType = "fishstation";
            Stream<Element> fs = getElementStreamByTagName(doc.getDocumentElement(), dataType);
            consumeTree(fs, a -> {
                printNode(a);
            });
             */
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
