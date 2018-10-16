/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HierarchicalData;

import java.util.LinkedList;
import java.util.List;
import javax.xml.bind.Unmarshaller;

/**
 * The purpose of this class is to provide a JAVA model of the data where parent
 * objects are directly accessible. The jaxb framework somehow registered the
 * java model classes as listeners during unmarshalling and the
 * afterUnmarshal(unmarshaller, Object) method is called after marshalling If
 * xjc is configured to make the unmarshalled classes inherit from this class,
 * they will be initialized with the desired values for parent at the end of
 * unmarshalling. this listener and makes sure the afterUnmarshal method is
 * called.
 *
 * @author a5362
 */
public abstract class HierarchicalData extends Unmarshaller.Listener {

    protected HierarchicalData parent;
    protected List<HierarchicalData> children;

    public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
        this.parent = (HierarchicalData) parent;
        if (this.parent != null && this.parent.children == null) {
            this.parent.children = new LinkedList<>();
        }
        if (this.parent != null) {
            this.parent.children.add((HierarchicalData) this);
        }
    }

    /**
     * @return The parent HierarchicalData object.
     */
    public HierarchicalData getParent() {
        return parent;
    }

    /**
     * Get all children of this HierarchicalData in no particular order. If the
     * HierachicalData object have several types of children, they are all
     * returned in the same list.
     *
     * @return list of children, null if lead node.
     */
    public List<HierarchicalData> getChildren() {
        return this.children;
    }
}
