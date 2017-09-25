package no.imr.guibase.node;

import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author Åsmund
 */
public class ChildFactoryNode extends BaseNode {

    RefreshableFactory factory;

    public ChildFactoryNode(Object obj, RefreshableFactory factory) {
        super(obj, Children.create(factory, true));
        this.factory = factory;
    }

    public RefreshableFactory getFactory() {
        return factory;
    }

    public BaseNode getSubNode(Object obj) {
        if (obj == null) {
            return null;
        }
        for (Node c : getChildren().getNodes()) {
            if (c instanceof BaseNode && obj.equals(((BaseNode) c).getObj())) {
                return (BaseNode) c;
            }
        }
        return null;
    }
}
