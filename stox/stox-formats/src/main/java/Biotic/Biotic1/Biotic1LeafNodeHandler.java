/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Biotic.Biotic1;

import BioticTypes.v1_4.StringDescriptionType;
import HierarchicalData.RelationalConversion.ILeafNodeHandler;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.Set;

/**
 * Works for biotic versions 1 through 1_4
 * @author Edvin Fuglebakk edvin.fuglebakk@imr.no
 */
public class Biotic1LeafNodeHandler implements ILeafNodeHandler{

    @Override
    public String extractValue(Object node) throws ClassCastException {
        if (node == null){
            return "";
        }
        if (node instanceof StringDescriptionType){
            return ((StringDescriptionType)node).getValue();
        }
        if (node instanceof String){
            return (String)node;
        }
        if (node instanceof BigInteger){
            return node.toString();
        }
        if (node instanceof BigDecimal){
            return node.toString();
        }

        throw new UnsupportedOperationException("Type " + node.getClass().getSimpleName() + " not implemented");
    }

    @Override
    public Set getLeafNodeComplexTypes() {
        Set<String> leafComplexTypes = new HashSet<>();
        leafComplexTypes.add("StringDescriptionType");
        return leafComplexTypes;
    }
    
}
