/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Echosounder.Echosounder1;

import EchoSounderTypes.v1.SaType;
import HierarchicalData.RelationalConversion.ILeafNodeHandler;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

/**
 * Works for biotic versions 1 through 1_4
 *
 * @author Edvin Fuglebakk edvin.fuglebakk@imr.no
 */
public class Echosounder1LeafNodeHandler implements ILeafNodeHandler {

    @Override
    public String extractValue(Object node) throws ClassCastException {
        if (node == null) {
            return "";
        }
        if (node instanceof SaType) {
            return (new Double(((SaType) node).getValue())).toString();
        }

        if (node instanceof String) {
            return (String) node;
        }
        if (node instanceof BigInteger) {
            return node.toString();
        }
        if (node instanceof Double) {
            return node.toString();
        }

        throw new UnsupportedOperationException("Type " + node.getClass().getSimpleName() + " not implemented");
    }

    @Override
    public Set getLeafNodeComplexTypes() {
        Set<String> leafComplexTypes = new HashSet<>();
        leafComplexTypes.add("SaType");
        return leafComplexTypes;
    }

}
