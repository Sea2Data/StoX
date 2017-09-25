package no.imr.sea2data.core.util;

import org.dom4j.Element;

/**
 *
 * @author aasmunds
 */
public final class IMRXML {

    /**
     * Private constructor
     */
    private IMRXML() {
    }

    /**
     * Return attributeValue from element or def if attributeValue == null
     *
     * @param elm
     * @param attrName
     * @param def
     * @return
     */
    public static String safeAttr(org.dom4j.Element elm, String attrName, String def) {
        String res = elm.attributeValue(attrName);
        return res != null ? res : def;
    }

    public static String safeAttr(org.dom4j.Element elm, String attrName, Integer def) {
        String res = elm.attributeValue(attrName);
        return res != null ? res : (def != null ? def.toString() : null);
    }

    /**
     * Safely add attribute to element. check on null
     *
     * @param parent
     * @param name
     * @param value
     * @return
     */
    public static Element safeAddAttribute(Element parent, String name, Object value) {
        if (value != null) {
            return parent.addAttribute(name, value.toString());
        }
        return null;
    }
}
