package no.imr.stox.api;

import java.awt.Window;
import java.util.List;
import no.imr.stox.model.IModel;
import no.imr.stox.model.IProject;
import org.openide.util.Lookup;

/**
 * Project service provider containing information about the current project.
 *
 * @author kjetilf
 */
public interface IProjectProvider {

    /**
     * Open project.
     */
    void openProject();

    void openProject(String projectRoot, String projectName, String template);

    /**
     * Reset current project.
     */
    void newProject();

    /**
     * Save current project.
     */
    void saveProject();

    /**
     * Save project with new name.
     */
    void saveAsProject();

    /**
     * Run Project
     *
     * @param model
     * @param iStart
     * @param iStop
     * @param breakable
     * @throws no.imr.stox.exception.UserErrorException
     */
    void runProject(IModel model, Integer iStart, Integer iStop, Boolean breakable);

    Lookup getProjectLookup();

    IProject getProject();

    void setRFolder(String text);

    String getRFolder();

    String getInstalledRStoxVersion();

    void checkRstox(Window wnd, boolean force);

    void checkSystem(Window wnd, boolean force);

    void loadConfig();

    void saveConfig();

    List<String> getReadMeLinesRstox();

    List<String> getReadMeLinesRstox(String ftpPath);

    void setrStoxFTPPath(String rStoxFTPPath);

    String getrStoxFTPPath();

    String getWorkDir();

    void setWorkDir(String workDir);
}
