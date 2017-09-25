/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.sea2data.guibase.utils;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.WordUtils;

/**
 *
 * @author aasmunds
 */
public class IMRtooltip {

    public static String wrap(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }
        // Wrap string into html with breaks
        if (!(s.contains("<br/>") || s.contains("<li>") || s.contains("<p>"))) {
            // Wrap a text which is not nautrally html breaked into html breaks.
            s = WordUtils.wrap(s, 75, "<br/>", false);
        }
        s = "<html>" + s + "</html>";
        return s;

    }
}
