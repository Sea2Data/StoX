/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.guibase.node;

import java.beans.PropertyVetoException;
import no.imr.guibase.node.ChildFactoryNode;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Åsmund
 */
public class NodeUtil {

    public static Node getSubNode(ChildFactoryNode parent, Object obj) {
        if (parent == null || obj == null) {
            return null;
        }
        Node subNode = parent.getSubNode(obj);
        if (subNode == null) {
            // Need to refresh parent to ensure the sub node is made available.
            parent.getFactory().refresh();
            subNode = parent.getSubNode(obj);
        }
        return subNode;
    }

    public static void selectNode(ExplorerManager em, Node n) {
        if (n == null) {
            return;
        }
        try {
            em.setSelectedNodes(new Node[]{n});
        } catch (PropertyVetoException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

}
