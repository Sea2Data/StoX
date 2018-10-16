/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HierarchicalData.RelationalConversion;

import java.util.Set;

/**
 * Used for adapting the generic relational converter TableMaker to specific hierarchical formats.
 * @author Edvin Fuglebakk edvin.fuglebakk@imr.no
 */
public interface ILeafNodeHandler <T extends Object>{
    
    /***
     * Extracts the value of a leaf node class.
     * Needs to handle all class representation of leaf nodes that can be encountered in the conversion to relational model
     * Must also handle the case when node is null.
     * @param node object representing leaf node in Hierarchical model
     * @throws ClassCastException, if handler can not handle given node.
     * @return value of node or null if no value is assigned.
     */
    public T extractValue(Object node) throws ClassCastException;
    
    /**
     * Returns a list of complexTypes that can be found as leaf nodes in the Hierarchical model
     * @return 
     */
    public Set<String> getLeafNodeComplexTypes();
    
}