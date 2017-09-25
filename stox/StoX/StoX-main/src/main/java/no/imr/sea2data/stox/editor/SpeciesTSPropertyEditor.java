/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.sea2data.stox.editor;

import java.awt.Component;
import java.beans.PropertyEditorSupport;
import no.imr.stox.dlg.SpeciesTSDlg;

/**
 *
 * @author aasmunds
 */
public class SpeciesTSPropertyEditor extends PropertyEditorSupport {

    String text;

    public SpeciesTSPropertyEditor(String text) {
        this.text = text;
    }

    @Override
    public void setAsText(String text) {
        setValue(text);
    }

    @Override
    public boolean supportsCustomEditor() {
        return true;
    }

    @Override
    public Component getCustomEditor() {
        return new SpeciesTSDlg(this);
    }

}
