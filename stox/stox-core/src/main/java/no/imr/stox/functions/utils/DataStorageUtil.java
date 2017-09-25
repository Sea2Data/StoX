/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.utils;

import java.io.Writer;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;
import no.imr.sea2data.imrbase.util.ExportUtil;
import no.imr.stox.bo.HeaderElm;
import org.jdom2.Element;

/**
 *
 * @author aasmunds
 */
public class DataStorageUtil {

    private static String getValuesFromHeader(Element elm, Collection<HeaderElm> header) {
        return header.stream().map(hdrelm -> {
            return JDOMUtils.getNodeValue(JDOMUtils.getTargetElm(elm, hdrelm.getLevel()), hdrelm.getName(), "-");
        }).collect(Collectors.joining("\t"));
    }

    public static void asTable(List<Element> l, String elmLevel, Writer wr) {
        if(l.isEmpty()) {
            return;
        }
        String topLevel = l.get(0).getName();
        Collection<HeaderElm> headers = new LinkedHashSet<>();
        // Write csv header
        JDOMUtils.getDataHeaders(l.stream(), topLevel, elmLevel, headers);
        String hdr = headers.stream()
                .map(HeaderElm::getName)
                .collect(Collectors.joining("\t"));
        ExportUtil.writeln(wr, hdr);
        // Write csv values parallell-consumed recursively with the encounter order given by fsList iterable.
        JDOMUtils.consumeTree(l.stream(), null, fs -> {
            ExportUtil.writeln(wr, getValuesFromHeader(fs, headers));
        }, elmLevel);
    }
}
