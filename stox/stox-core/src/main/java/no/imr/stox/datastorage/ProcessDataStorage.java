/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.datastorage;

import no.imr.stox.functions.utils.ProjectUtils;

/**
 * TODO: what does this class do?
 *
 * @author aasmunds
 */
public class ProcessDataStorage extends MatricesStorage {

    @Override
    public String getOutputFolder() {
        return ProjectUtils.PROCESS_FOLDER;
    }

    @Override
    public String getOutputFileName() {
        return ProjectUtils.PROCESSDATAFILE;
    }

/*    @Override
    public void writeToFile() {
        // Write to XML file.
        AbndEstProcessDataUtil.save((ProcessDataBO) getData(), getProcess().getModel().getProject().getProjectFolder());
    }*/
    
}
