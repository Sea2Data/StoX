/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.guibase.node;

import org.openide.nodes.ChildFactory;

/**
 *
 * @author Åsmund
 */
public abstract class RefreshableFactory<T> extends ChildFactory<T> {

    public void refresh() {
        super.refresh(true);
    }
}
