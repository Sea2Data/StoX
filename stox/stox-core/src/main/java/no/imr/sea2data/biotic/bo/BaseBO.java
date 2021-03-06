/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.sea2data.biotic.bo;

import java.util.Objects;
import java.util.stream.Collectors;
import no.imr.stox.functions.utils.ReflectionUtil;

/**
 *
 * @author aasmunds
 */
public class BaseBO {

    BaseBO parent;
    protected Object bo;
    String key;

    public BaseBO(BaseBO parent, Object bo) {
        this.parent = parent;
        this.bo = bo;
    }

    public BaseBO getParent() {
        return parent;
    }

    public String getKey() {
        if (key == null) {
            String parentprefix = "";
            if (parent != null) {
                parentprefix = parent.getKey() + "/";
            }
            key = parentprefix + getInternalKey();
        }
        return key;
    }

    @Override
    public String toString() {
        return getKey();
    }

    protected String getInternalKey() {
        return null;
    }

    public static String csvHdr(Class clz, Boolean includeCompoundFields, Boolean includeAttributes) {
        return ReflectionUtil.getFields(clz, includeCompoundFields, includeAttributes).stream().map(f -> f.getName()).collect(Collectors.joining("\t"));
    }

    public String csv(Boolean includeCompoundFields, Boolean includeAttributes) {
        return csv(bo, includeCompoundFields, includeAttributes);
    }

    public static String csv(Object bo, Boolean includeCompoundFields, Boolean includeAttributes) {
        return ReflectionUtil.getCompoundFields(bo.getClass(), includeAttributes).stream()
                .map(f -> ReflectionUtil.invoke(f, bo, includeCompoundFields))
                .map(o -> o == null ? "" : Objects.toString(o)).collect(Collectors.joining("\t"));
    }
}
