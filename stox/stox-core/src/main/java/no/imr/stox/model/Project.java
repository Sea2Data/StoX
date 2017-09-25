package no.imr.stox.model;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import no.imr.sea2data.imrbase.exceptions.XMLReaderException;
import no.imr.sea2data.imrbase.util.Workspace;
import no.imr.sea2data.imrbase.util.XMLWriter;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.ProjectUtils;
import no.imr.stox.functions.utils.RFolder;
import no.imr.stox.functions.utils.RUtils;
import no.imr.stox.library.ILibrary;
import no.imr.stox.library.Library;

/**
 *
 * @author Åsmund
 */
public class Project implements IProject {

    private String rootFolder;
    private String projectName;
    public static final double RESOURCE_VERSION = 1.72; // System version at current program
    private Double resourceVersion = 1.00;                   // System version at program used to create the project file
    private final Map<String, IModel> models = new HashMap<>();
    private final ILibrary library;
    private ProcessDataBO processData = new ProcessDataBO();
    private String rFolder = "";
    private String rVersion = "";
    private String rStoxVersion = "";
    private String stoxVersion = "";
    private String template = "";

    private static final String[] modelNames = new String[]{
        ProjectUtils.BASELINE,
        ProjectUtils.R,
        ProjectUtils.BASELINE_REPORT,
        ProjectUtils.R_REPORT};

    private static String getProcessFileFromModel(String model) {
        switch (model) {
            case ProjectUtils.BASELINE:
                return ProjectUtils.PROCESS_FILE;
            case ProjectUtils.R:
                return ProjectUtils.RPROCESS_FILE;
            case ProjectUtils.BASELINE_REPORT:
                return ProjectUtils.REPORTBASELINE_FILE;
            case ProjectUtils.R_REPORT:
                return ProjectUtils.REPORTRMODEL_FILE;
        }
        return "";
    }

    public Project() {
        this(ProjectUtils.getSystemProjectRoot());
    }

    public Project(String rootFolder) {
        this(rootFolder, null);
    }

    /**
     * Use this constructor to create aproject with all models available I.E
     * from stox gui
     *
     * @param rootFolder
     * @param projectName
     */
    public Project(String rootFolder, String projectName) {
        this(rootFolder, projectName, modelNames);
    }

    /**
     * Use this constructor to createa a single model project I.E Helping
     * project from R trigger when a specific model is parsed (Includes alias
     * translation)
     *
     * @param rootFolder
     * @param projectName
     * @param modelName
     */
    public Project(String rootFolder, String projectName, String modelName) {
        this(rootFolder, projectName, new String[]{modelName});
    }

    private Project(String rootFolder, String projectName, String[] modelNames) {
        this.rootFolder = rootFolder;
        this.projectName = projectName;
        // Create models and connect to corresponding function library
        library = new Library();

        for (String modelName : modelNames) {
            String processFile = getProcessFileFromModel(modelName);
            library.readFromResource(modelName);
            Model m;
            switch (modelName) {
                case ProjectUtils.R:
                case ProjectUtils.R_REPORT:
                    m = new RModel(this, modelName, processFile);
                    break;
                default:
                    m = new Model(this, modelName, processFile);
            }
            models.put(modelName, m);
        }
    }

    @Override
    public void setProjectName(String projectName) {
        this.projectName = projectName;
        // update R setParameters rootFolder+projectName parameter.
    }

    @Override
    public void openProject() {
        // Read model files
        //if project.xml exists, read it, otherwise read model files and process data the old way:
        String prFile = ProjectUtils.getProcessFolderFile(getProjectFolder(), ProjectUtils.PROJECT, false);
        if ((new File(prFile)).exists()) {
            try (FileInputStream stream = new FileInputStream(prFile)) {
                new ProjectXMLReader(this).readXML(stream);
            } catch (IOException | XMLReaderException ex) {
                Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        // Initialize R folder from temp RFolder file
        if(rFolder == null || rFolder != null && rFolder.isEmpty()) {
            rFolder = RFolder.getRFolderFromFile();
        }
    }

    @Override
    public String getProjectName() {
        return projectName;
    }

    @Override
    public IModel getModel(String name) {
        return models.get(name);
    }

    @Override
    public IModel getBaseline() {
        return models.get(ProjectUtils.BASELINE);
    }

    @Override
    public IModel getRModel() {
        return models.get(ProjectUtils.R);
    }

    @Override
    public IModel getBaselineReport() {
        return models.get(ProjectUtils.BASELINE_REPORT);
    }

    @Override
    public IModel getRModelReport() {
        return models.get(ProjectUtils.R_REPORT);
    }

    @Override
    public void save() {
        // Save to xml file:
        String prFile = ProjectUtils.getProcessFolderFile(getProjectFolder(), ProjectUtils.PROJECT, false);
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(prFile))) {
            XMLStreamWriter xmlw = XMLOutputFactory.newInstance().createXMLStreamWriter(bos, "UTF-8");
            try {
                xmlw.writeStartDocument("UTF-8", "1.0");
                xmlw.writeCharacters("\n");
                int level = 0;
                Map<String, String> attr = new HashMap<>();
                attr.put("xmlns", "http://www.imr.no/formats/stox/v1");
                //attr.put("xmlversion", String.valueOf(SYSTEM_VERSION));     1.0
                //attr.put("rstoxversion", String.valueOf(SYSTEM_VERSION));   2
                attr.put("template", template);
                attr.put("stoxversion", stoxVersion);
                attr.put("resourceversion", String.valueOf(RESOURCE_VERSION));
                attr.put("rversion", rVersion);
                attr.put("rstoxversion", rStoxVersion);
                DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(Locale.UK).withZone(ZoneOffset.UTC);
                attr.put("lastmodified", formatter.format(Instant.now()));
                XMLWriter.writeXMLElementStart(level++, xmlw, "project", attr);
                for (String modelname : modelNames) {
                    IModel m = getModel(modelname);
                    m.save(level, xmlw);
                }
                // Call save on process data, if writeprocessdata is available in the model:
                AbndEstProcessDataUtil.save(level, xmlw, processData, getProjectFolder());
                XMLWriter.writeXMLElementEnd(--level, xmlw); // item
                xmlw.writeEndDocument();
            } finally {
                xmlw.close();
            }
        } catch (XMLWriter.StAXWriterException | IOException | XMLStreamException ex) {
            return;
        }
        // Clean up old files
        for (String modelName : modelNames) {
            File file = new File(ProjectUtils.getProcessFolderFile(getProjectFolder(), getProcessFileFromModel(modelName), true));
            if (file.exists()) {
                file.delete();
            }
        }
        File file = new File(ProjectUtils.getProcessFolderFile(getProjectFolder(), ProjectUtils.PROCESSDATAFILE, true));
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public String getRootFolder() {
        return rootFolder;
    }

    @Override
    public String getProjectFolder() {
        if (getRootFolder() == null) {
            return null;
        }
        return Workspace.getDir(getRootFolder(), getProjectName());
    }

    @Override
    public Boolean isDirty() {
        for (IModel m : models.values()) {
            if (m == null) {
                continue;
            }
            if (m.isDirty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public ILibrary getLibrary() {
        return library;
    }

    @Override
    public String toString() {
        String res = "";
        res += getProjectName();
        res += "\n" + getBaseline();
        return res;
    }

    @Override
    public Map<String, IModel> getModels() {
        return models;
    }

    @Override
    public IProcess findProcess(String procName) {
        for (IModel m : getModels().values()) {
            IProcess p = m.findProcess(procName);
            if (p != null) {
                return p;
            }
        }
        return null;
    }

    @Override
    public ProcessDataBO getProcessData() {
        return processData;
        /*
         IProcess readPro = findProcess(Functions.FN_READPROCESSDATA);
         if (readPro != null && readPro.getOutput() != null) {
         return (ProcessDataBO) readPro.getOutput();
         }
         return null;*/
    }

    @Override
    public Double getResourceVersion() {
        return resourceVersion;
    }

    @Override
    public void setResourceVersion(Double resourceVersion) {
        this.resourceVersion = resourceVersion;
    }

    @Override
    public void setRFolder(String rFolder) {
        this.rFolder = rFolder;
        if (rFolder != null) {
            // Transfer latest R folder used to from temp RFolder file.
            // Makes the RFolder known to openProject if used by i.e RStoX
            RFolder.writeRFolderToFile(rFolder);
            rVersion = RUtils.getRVersion(rFolder);
            rStoxVersion = RUtils.getRstoxVersion(rFolder);
        }
    }

    @Override
    public String getRFolder() {
        return rFolder;
    }

    @Override
    public String getStoxVersion() {
        return stoxVersion;
    }

    @Override
    public void setStoxVersion(String stoxVersion) {
        this.stoxVersion = stoxVersion;
    }

    @Override
    public String getTemplate() {
        return template;
    }

    @Override
    public void setTemplate(String template) {
        this.template = template;
    }

}
