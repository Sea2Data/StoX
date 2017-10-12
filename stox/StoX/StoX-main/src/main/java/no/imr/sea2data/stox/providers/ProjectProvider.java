package no.imr.sea2data.stox.providers;

import java.awt.Window;
import no.imr.stox.api.IProjectProvider;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import no.imr.sea2data.core.util.IMRXML;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrbase.util.Workspace;
import no.imr.sea2data.stox.DirtyChecker;
import no.imr.sea2data.stox.InstallerUtil;
import static no.imr.sea2data.stox.InstallerUtil.installRstox;
import no.imr.stox.api.IProcessViewer;
import no.imr.stox.model.IModel;
import no.imr.stox.factory.FactoryUtil;
import no.imr.stox.dlg.NewProjectDialog;
import no.imr.stox.dlg.SaveAsDlg;
import no.imr.stox.dlg.SelectWorkspaceProjectDialog;
import no.imr.stox.functions.utils.ProjectUtils;
import no.imr.stox.functions.utils.RUtils;
import no.imr.stox.model.IModelListenerService;
import no.imr.stox.model.IProject;
import no.imr.stox.model.ModelListenerAdapter;
import no.imr.stox.statusbar.StatusBarProvider;
import org.apache.commons.io.FileUtils;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ServiceProvider;

/**
 * Project provider. Contains all information about the project.
 *
 * @author kjetilf
 */
@ServiceProvider(service = IProjectProvider.class)
final public class ProjectProvider implements IProjectProvider {

    /**
     * Parameter object for the project.
     */
    private IProject project;
    private String rFolder;
    private String rStoxFTPPath;
    List<String> readMeLinesRstox;
    List<String> readMeLinesStoX;
    String workDir = ProjectUtils.getSystemProjectRoot();
    //String packagesStr = null;

    public ProjectProvider() {
        // Ensure that a test model is provided on workspace/stox called Tobis-2013842-test
        /*FactoryUtil.extractTestModel(false);
            FactoryUtil.extractSystemFromResource(false);*/
        // Initiate the model listener and create the roor node.
        IModelListenerService fls = (IModelListenerService) Lookup.getDefault().lookup(IModelListenerService.class);
        fls.getModelListeners().add(new ModelListenerAdapter() {
            @Override
            public void onModelStop(IModel model) {
                StatusBarProvider.updateBymodel(model);
            }

            @Override
            public void onModelStart(IModel model) {
                StatusBarProvider.updateBymodel(model);
            }

            @Override
            public void onReset(IModel m) {
            }

        });
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                saveConfig();
            }
        });
        readMeLinesStoX = getReadMeLinesStoX();
        setrStoxFTPPath(InstallerUtil.FTP_STOXDOWNLOAD_RSTOX);
        if (rFolder == null || rFolder.isEmpty()) {
            File f = RUtils.getRBinFolder(null);;
            if (f != null) {
                setRFolder(f.getPath());
            }
        }
    }

    @Override
    public String getWorkDir() {
        return workDir;
    }

    @Override
    public void setWorkDir(String workDir) {
        this.workDir = workDir;
    }

    @Override
    public void openProject() {
        if (!DirtyChecker.canContinueIfDirty()) {
            return;
        }
        SelectWorkspaceProjectDialog dlg = new SelectWorkspaceProjectDialog(workDir);
        dlg.setVisible(true);
        workDir = dlg.getWorkDir();
        if (dlg.getProject() == null) {
            return;
        }
        openProject(dlg.getProjectRoot(), dlg.getProject(), null);
    }

    @Override
    public void openProject(String projectRoot, String projectName, String template) {
        // CLear process viewer
        IProcessViewer viewer = (IProcessViewer) Lookup.getDefault().lookup(IProcessViewer.class);
        if (viewer != null) {
            viewer.clear();
        }
        project = FactoryUtil.acquireProject(projectRoot, projectName, template);
        project.setRFolder(rFolder); // Transfer rfolder to project.
        project.setStoxVersion(System.getProperty("netbeans.buildnumber"));
        IModelListenerService fls = (IModelListenerService) Lookup.getDefault().lookup(IModelListenerService.class);
        for (IModel model : project.getModels().values()) {
            model.setModellisteners(fls.getModelListeners());
        }
        if (template != null) {
            project.save(); // maybe not save here
        }
        ic.remove(project);
        ic.set(Collections.singleton(project), null);
    }

    @Override
    public void newProject() {
        if (!DirtyChecker.canContinueIfDirty()) {
            return;
        }
        NewProjectDialog dlg = new NewProjectDialog();
        dlg.setVisible(true);

        if (dlg.getProjectName() == null || dlg.getTemplate() == null || dlg.getProjectName().isEmpty()) {
            return;
        }
        /*        String resTemplate = dlg.getTemplate() == TEMPLATE_ABND_BY_TRANSECT ? "abndtransect"
         : dlg.getTemplate() == TEMPLATE_ABND_BY_RECTANGLE ? "abndrectangle" : null;
         if (resTemplate == null) {
         return;
         }*/
        // reminder: create replace warning here if existing.
        openProject(dlg.getProjectRoot(), dlg.getProjectName(), dlg.getTemplate());
    }

    @Override
    public void saveProject() {
        if (project != null) {
            project.save();
        }
    }

    @Override
    public void saveAsProject() {
        if (project == null) {
            return;
        }
        SaveAsDlg dlg = new SaveAsDlg(project);
        dlg.setVisible(true);
        if (!dlg.getProjectName().isEmpty()) {
            try {
                FileUtils.copyDirectory(new File(project.getProjectFolder()), new File(Workspace.getDir(project.getRootFolder(), dlg.getProjectName())));
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            openProject(project.getRootFolder(), dlg.getProjectName(), null);
        }
    }

    @Override
    public void runProject(IModel model, Integer iStart, Integer iStop, Boolean breakable) {
        if (model == null) {
            return;
        }
        if (model.getRunState() == IModel.RUNSTATE_RUNNING) {
            return;
        }
        // Transfer model listners
        //if (model.getModellisteners().isEmpty()) {
        //}
        String err = model.validate();
        if (err != null) {
            DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(err, NotifyDescriptor.INFORMATION_MESSAGE));
            return;
        }
        model.setRunState(IModel.RUNSTATE_RUNNING);
        if (iStart == null) {
            iStart = model.isFinished() ? 0 : model.getRunningProcessIdx() + 1;
        }
        if (iStop == null) {
            iStop = model.getProcessList().size() - 1;
        }
        if (iStart > iStop) {
            return;
        }
        if (breakable == null) {
            breakable = true;
        }
        model.setBreakable(breakable);
        model.run(iStart + 1, iStop + 1, true);
        //StatusBarProvider.updateBymodel();
    }

    @Override
    public IProject getProject() {
        return project;
    }

    /*private IModel getModel() {
     return project != null ? project.getBaseline() : null;
     }

     private IModel getRModel() {
     return project != null ? project.getRModel() : null;
     }*/
    private final InstanceContent ic = new InstanceContent();
    private final Lookup projectLookup = new AbstractLookup(ic);

    @Override
    public Lookup getProjectLookup() {
        return projectLookup;
    }

    /**
     *
     * @param rFolder
     */
    @Override
    public void setRFolder(String rFolder) {
        this.rFolder = rFolder;

        if (project != null) {
            project.setRFolder(rFolder);
        } // Transfer rfolder to project.
    }

    /**
     *
     * @return
     */
    @Override
    public String getRFolder() {
        return rFolder;
    }

    private String getConfigPath() {
        return System.getProperty("user.home") + "/.stox.config.xml";
    }

    /**
     * Load the configuration file from the user home.
     */
    @Override
    public void loadConfig() {
        File xml = new File(getConfigPath());
        if (xml.exists()) {
            try {
                SAXReader reader = new SAXReader();
                Document doc = reader.read(xml);
                Element stox = doc.getRootElement();
                loadConfig(stox);
            } catch (DocumentException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    // Search for version string:
    // # Rstox version: 1.2 (latest, 2016-04-12)
    // # R version: 3.3.1
    // Latest StoX version: 2.1.1 (latest, 22-05-2016)
    private String getREADMEVersion(List<String> readMeLines, final String app) {
        Optional<String> str = readMeLines.stream()
                .map(s -> {
                    Pattern p = Pattern.compile(app + "\\s+version\\:?\\s*(\\d+\\.\\d+\\.?\\d*)");
//                    Pattern p = Pattern.compile("(\\d+\\.\\d+\\.?\\d*)");
                    Matcher m = p.matcher(s);
                    //return m.find() ? m.group(0) : null;
                    return m.find() ? m.group(1) : null;
                }).filter(s -> s != null).findFirst();
        if (str.isPresent()) {
            return str.get();
        }
        return "";
    }

    boolean isVersionOutdated(String verInstalled, String verAvailable) {
        if (verAvailable == null || verAvailable.isEmpty()) {
            return false;
        }
        if (verInstalled == null || verInstalled.isEmpty()) {
            return true;
        }
        String[] verInst = verInstalled.split("\\.");
        String[] verAvail = verAvailable.split("\\.");
        for (int i = 0; i < 4; i++) {
            String vI = i < verInst.length ? verInst[i] : null;
            String vA = i < verAvail.length ? verAvail[i] : null;
            Integer vIi = Conversion.safeStringtoInteger(vI);
            Integer vAi = Conversion.safeStringtoInteger(vA);
            if (vIi > vAi) {
                return false;
            }
            if (vIi < vAi) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getInstalledRStoxVersion() {
        return RUtils.getRstoxVersion(rFolder);
    }

    @Override
    public void checkRstox(Window wnd, boolean force) {
        String verRstoxReadMe = getREADMEVersion(readMeLinesRstox, "Rstox");
        String verRstoxInstalled = getInstalledRStoxVersion();

        String verRReadMe = getREADMEVersion(readMeLinesRstox, "R");
        String verRInstalled = RUtils.getRVersion(rFolder);
        if (isVersionOutdated(verRInstalled, verRReadMe)) {
            JOptionPane.showMessageDialog(wnd, "RStox " + verRstoxReadMe + " requires installed R " + verRReadMe
                    + ". Please also update Tools->R connection in StoX to the path of the R Bin. "
                    + "This path can be found by running the following command in R: file.path(Sys.getenv(\"R_HOME\"), \"bin\")\".",
                    "R installation", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (isVersionOutdated(verRstoxInstalled, verRstoxReadMe) || force) {
            if (JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(wnd, "Do you want to install RStox "
                    + verRstoxReadMe + " available at " + rStoxFTPPath + "?", "RStoX - install from FTP",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE)) {
                boolean success = installRstox(wnd, rStoxFTPPath, rFolder);
                JOptionPane.showMessageDialog(null, "Installation of RStox " + (success ? "succeeded" : "failed"));
            }
        }
    }

    public void checkStox() {
        String verReadMe = getREADMEVersion(readMeLinesStoX, "StoX");
        String verInstalled = System.getProperty("netbeans.buildnumber");
        if (isVersionOutdated(verInstalled, verReadMe)) {
            JOptionPane.showMessageDialog(null, "StoX " + verReadMe + " is available at ftp://ftp.imr.no/StoX/Download/");
        }
    }

    @Override
    public void checkSystem(Window wnd, boolean force) {
        try {
            // if (packagesStr != null) {
            // Update R packages if necessary
            //RUtils.installRPackages(getRFolder(), packagesStr);
            checkRstox(wnd, force);
            checkStox();
            //}
            // Check other resources
            InstallerUtil.extractSystemFromResource();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, new JScrollPane(new JTextArea("Error when running R package check: " + ex.getMessage()
                    + "\r\nThe path " + rFolder + " may be wrong" + "\r\nSpecify R connection in Tools->R connection")), "Warning:", JOptionPane.WARNING_MESSAGE);
        }
    }

    /*private void checkJavaVersion() {
     Integer osArch = RUtils.getJREArchN();
     Integer jArch = Conversion.safeStringtoIntegerNULL(System.getProperty("sun.arch.data.model"));
     if(jArch == null || osArch == null) {
     return;
     }
     if (jArch < osArch) {
     JOptionPane.showMessageDialog(null, "Java and OS architechture differs. Update Java");
     }
     }*/
    private void loadConfig(Element stox) {
        String projectName = (String) IMRXML.safeAttr(stox, "project", "");
        String projectRoot = (String) IMRXML.safeAttr(stox, "projectroot", "");
        if (projectName != null && projectRoot != null && !(projectName.isEmpty() || projectRoot.isEmpty())) {
            openProject(projectRoot, projectName, null);
        }
        setrStoxFTPPath((String) IMRXML.safeAttr(stox, "rStoxFTPPath", rStoxFTPPath));
        setRFolder((String) IMRXML.safeAttr(stox, "rfolder", rFolder));
    }

    @Override
    public void saveConfig() {
        FileOutputStream fos = null;
        try {
            Document doc = DocumentFactory.getInstance().createDocument();
            Element stox = doc.addElement("stox");
            if (getProject() != null) {
                stox.addAttribute("projectroot", getProject().getRootFolder());
                stox.addAttribute("project", getProject().getProjectName());
                stox.addAttribute("rfolder", getRFolder());
                stox.addAttribute("rStoxFTPPath", rStoxFTPPath);

            }
            fos = new FileOutputStream(getConfigPath());
            OutputFormat format = OutputFormat.createPrettyPrint();
            XMLWriter writer = new XMLWriter(fos, format);
            writer.write(doc);
            writer.flush();
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    public List<String> getReadMeLinesRstox() {
        return readMeLinesRstox;
    }

    public String getrStoxFTPPath() {
        return rStoxFTPPath;
    }

    public List<String> getReadMeLinesRstox(String ftpPath) {
        return getReadMeLines(ftpPath, "Rstox");
    }

    public List<String> getReadMeLinesStoX() {
        return getReadMeLines(InstallerUtil.FTP_STOXDOWNLOAD, "StoX");
    }

    public List<String> getReadMeLines(String ftpPath, String app) {
        try {
            String tempFile = InstallerUtil.getIOTempDirFile(app + "." + InstallerUtil.README);
            if (InstallerUtil.retrieveReadMeFromFTP(ftpPath, tempFile)) {
                return FileUtils.readLines(new File(tempFile));
            }
        } catch (UncheckedIOException | IOException ex) {
            //Exceptions.printStackTrace(ex);
            System.out.println("Exception getting ReadMe " + ex.getMessage());
        }
        return new ArrayList<>();
    }

    public void setrStoxFTPPath(String rStoxFTPPath) {
        if (rStoxFTPPath != null && rStoxFTPPath.equals(this.rStoxFTPPath)) {
            return;
        }
        this.rStoxFTPPath = rStoxFTPPath;
        readMeLinesRstox = getReadMeLinesRstox(rStoxFTPPath);
        // Set package str
        // String str = readMeLinesRstox.stream().filter(s -> s.contains("dep.pck <-")).findFirst().get();
        /*if (str != null) {
            packagesStr = StringUtils.substringBetween(str, "(", ")");
        } else {
            packagesStr = null;
        }*/
    }

}
