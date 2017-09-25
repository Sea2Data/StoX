/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.utils;

import java.io.File;
import no.imr.sea2data.imrbase.util.Workspace;

/**
 *
 * @author aasmunds
 */
public class ProjectUtils {

    public static final String DATA = "data";
    public static final String REPORT = "report";
    public static final String TRASH = "trash";
    public static final String BASELINE = "baseline";
    public static final String R = "r";
    public static final String BASELINE_REPORT = "baseline-report";
    public static final String R_REPORT = "r-report";
    public static final String INPUT = "input";
    public static final String OUTPUT = "output";
    
    public static final String PROJECT = "project";
    public static final String PROJECT_INPUT_FOLDER = INPUT;
    public static final String PROJECT_ACOUSTICINPUT_FOLDER = INPUT + "/" + "acoustic";
    public static final String PROJECT_BIOTICINPUT_FOLDER = INPUT + "/" + "biotic";
    public static final String PROJECT_LANDINGINPUT_FOLDER = INPUT + "/" + "landing";

    public static final String PROJECT_OUTPUTBASELINE = OUTPUT + "/" + BASELINE;
    public static final String PROJECT_OUTPUTBASELINE_DATA = PROJECT_OUTPUTBASELINE + "/" + DATA;
    public static final String PROJECT_OUTPUTBASELINE_DATA_TRASH = PROJECT_OUTPUTBASELINE_DATA + "/" + TRASH;
    public static final String PROJECT_OUTPUTBASELINE_REPORT = PROJECT_OUTPUTBASELINE + "/" + REPORT;
    public static final String PROJECT_OUTPUTBASELINE_REPORT_TRASH = PROJECT_OUTPUTBASELINE_REPORT + "/" + TRASH;

    public static final String PROJECT_OUTPUTR = OUTPUT + "/" + R;
    public static final String PROJECT_OUTPUTR_DATA = PROJECT_OUTPUTR + "/" + DATA;
    public static final String PROJECT_OUTPUTR_DATA_TRASH = PROJECT_OUTPUTR_DATA + "/" + TRASH;
    public static final String PROJECT_OUTPUTR_REPORT = PROJECT_OUTPUTR + "/" + REPORT;
    public static final String PROJECT_OUTPUTR_REPORT_TRASH = PROJECT_OUTPUTR_REPORT + "/" + TRASH;
    
    public static final String PROCESS_FOLDER = "process";
    public static final String PROCESS_FILE = "process";
    public static final String RPROCESS_FILE = "rprocess";
    public static final String REPORTBASELINE_FILE = "report_baseline";
    public static final String REPORTRMODEL_FILE = "report_rmodel";
    public static final String PROCESSDATA = "processdata";
    public static final String PROCESSDATAFILE = "process_data";
    public static final String STOX_SYSTEM_FOLDER = "stox";

    public static final String SYSTEM_PROJECT_FOLDER = PROJECT;
    public static final String SYSTEM_REFERENCE_FOLDER = "reference";
    public static final String SYSTEM_STRATUM_FOLDER = SYSTEM_REFERENCE_FOLDER + "/stratum";
//    public static final String RDATAFILE = "./" + PROJECT_RMODEL_OUTPUT_FOLDER + "/" + R + ".RData";
//    public static final String IMPUTEBYAGEFILE = "./" + PROJECT_REPORT_OUTPUT_FOLDER + "/" + "ImputeByAge" + ".RData";

    public static String txt(String f) {
        return f + ".txt";
    }

    public static String xml(String f) {
        return f + ".xml";
    }

    public static String getProcessPath(String projectFolder) {
        return projectFolder + "/" + PROCESS_FOLDER;
    }

    public static String getProjectSubFilePath(String projectFolder, String subFolder, String fileName) {
        return projectFolder + "/" + subFolder + "/" + fileName;
    }

    public static String getProcessFolderFile(String projectFolder, String file, Boolean isTxt) {
        return getProjectSubFilePath(projectFolder, PROCESS_FOLDER, isTxt ? txt(file) : xml(file));
    }

    public static File getProcessFile(String projectFolder, String processFileName, Boolean isTxt) {
        File f = new File(getProcessFolderFile(projectFolder, processFileName, isTxt));
        if (f.exists()) {
            return f;
        }
        return null;
    }

    /**
     *
     * @param projectFolder
     * @param isTxt True if a .txt file, false if xml
     * @return
     */
    public static String getProcessDataFile(String projectFolder) {
        File f = new File(getProcessFolderFile(projectFolder, PROCESSDATAFILE, true));
        if (f.exists()) {
            return f.getPath();
        }
        f = new File(getProcessFolderFile(projectFolder, PROCESSDATA, true));
        if (f.exists()) {
            return f.getPath();
        }
        return null;
    }

    public static String getSystemFolder() {
        return Workspace.getDir(Workspace.getDefaultWorkspace(), STOX_SYSTEM_FOLDER);
    }

    /**
     * Get the system project folder normally:
     * C:/Users/<User>/workspace/stox/project
     *
     * @return
     */
    public static String getSystemProjectRoot() {
        return getSystemSubFolder(SYSTEM_PROJECT_FOLDER);
    }

    public static String getSystemStratumFolder() {
        return getSystemSubFolder(SYSTEM_STRATUM_FOLDER);
    }

    public static String getSystemReferenceFolder() {
        return getSystemSubFolder(SYSTEM_REFERENCE_FOLDER);
    }

    public static String getSystemSubFolder(String subFolder) {
        return Workspace.getDir(getSystemFolder(), subFolder);
    }

    public static String getOutVar(String var) {
        return var + ".out";
    }

    public static String resolveParameterFileName(String fileName, String projectFolder) {
        if (fileName == null) {
            return null;
        }
        // Try the project folder
        String fullPath = fileName;
        if (!new File(fullPath).exists()) {
            if (fileName.contains("${STOX}")) {
                // System reference:
                fullPath = fileName.replace("${STOX}", ProjectUtils.getSystemFolder());
            } else {
                // Relative to project:
                fullPath = projectFolder + "/" + fileName;
            }
            if (!new File(fullPath).exists()) {
                // Relative to system
                fullPath = ProjectUtils.getSystemFolder() + "/" + fileName;
            }
            if (!new File(fullPath).exists()) {
                return null;
            }
        }
        return fullPath;
    }
}
