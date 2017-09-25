/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.guibase.node;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Åsmund
 */
public class BaseNode extends AbstractNode {

    Object obj;

    public BaseNode(Object obj, Children children) {
        super(children, Lookups.singleton(obj));
        this.obj = obj;
    }

    public Object getObj() {
        return obj;
    }
}
