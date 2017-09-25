/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.api;

import javax.swing.JTabbedPane;
import no.imr.stox.model.IProcess;

/**
 *
 * @author aasmunds
 */
public interface IProcessViewer {

    public void openProcess(IProcess p, boolean activate);

    public void setTabbedPane(JTabbedPane jTabbedPane1);
    public void clear();
}
