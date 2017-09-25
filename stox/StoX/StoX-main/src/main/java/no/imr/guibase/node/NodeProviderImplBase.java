/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.guibase.node;

import org.openide.nodes.Node;

/**
 *
 * @author aasmunds
 */
public class NodeProviderImplBase implements INodeProvider {

    @Override
    public void select(Node node) {
    }

    @Override
    public void refresh(Node node) {
        ((ChildFactoryNode) node).getFactory().refresh();
    }
}
