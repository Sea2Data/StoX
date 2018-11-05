/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.sea2data.biotic.bo;

/**
 *
 * @author aasmunds
 */
public class BaseBO {

    BaseBO parent;
    protected Object bo;

    public BaseBO(BaseBO parent, Object bo) {
        this.parent = parent;
        this.bo = bo;
    }

    public BaseBO getParent() {
        return parent;
    }

}
