/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.guibase.node;

import org.openide.nodes.Node;

/**
 *
 * @author Åsmund
 */
public interface INodeProvider {

    void select(Node node);
    void refresh(Node node);
}
