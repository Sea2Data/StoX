/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.model;

import java.util.Map;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.library.ILibrary;

/**
 *
 * @author Åsmund
 */
public interface IProject {

    /**
     * Set working directory
     *
     * @param projectname
     */
    void setProjectName(String projectname);

    String getProjectName();

    void openProject();

    IModel getModel(String modelName);

    IModel getBaseline();

    IModel getRModel();

    IModel getBaselineReport();

    IModel getRModelReport();

    void save();

    String getProjectFolder();

    String getRootFolder();

    /**
     * return true if one of the models has been changed (i.e a parameter value)
     *
     * @return
     */
    Boolean isDirty();

    ILibrary getLibrary();

    Map<String, IModel> getModels();

    /**
     * find process in all models for the project
     *
     * @param procName
     * @return
     */
    IProcess findProcess(String procName);

    ProcessDataBO getProcessData();

    Double getResourceVersion();

    void setResourceVersion(Double version);

    void setRFolder(String rFolder);

    String getRFolder();

    void setStoxVersion(String implementationBuild);

    String getStoxVersion();

    String getTemplate();

    void setTemplate(String template);

    int getPrecisionLevel();

    void setPrecisionLevel(int precisionLevel);

}
