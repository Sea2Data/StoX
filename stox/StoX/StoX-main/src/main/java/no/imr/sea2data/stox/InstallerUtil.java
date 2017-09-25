/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.sea2data.stox;

import java.awt.Cursor;
import java.awt.Window;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.imr.sea2data.imrbase.exceptions.XMLReaderException;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrbase.util.Workspace;
import no.imr.stox.factory.Factory;
import no.imr.stox.factory.FactoryUtil;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.ProjectUtils;
import no.imr.stox.functions.utils.RUtils;
import no.imr.stox.model.IModel;
import no.imr.stox.model.IProject;
import no.imr.stox.model.Project;
import no.imr.stox.model.ProjectXMLReader;
import no.imr.sea2data.core.util.FTPUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;

/**
 *
 * @author aasmunds
 */
public class InstallerUtil {

    /**
     * Extract file to workspace folder.
     */
    /**
     * Initalize.
     *
     * @param resTemplate
     * @param targetFolder
     * @param subFolder
     * @param fileName
     * @param force - force a new copy replacement
     */
    public static void extractToTargetFolder(String resTemplate, String targetFolder, String subFolder, String fileName, boolean force) {
        File fFile = new File(Workspace.getDir(targetFolder, subFolder) + "/" + fileName);
        if (force || !fFile.exists()) {
            try {
                // Extract the resource to file if not exist.
                String fs = ProjectUtils.STOX_SYSTEM_FOLDER + "/" + resTemplate + "/" + subFolder + "/" + fileName;
                InputStream st = Workspace.getResourceAsStream(fs);
                if (st == null) {
                    return;
                }
                IOUtils.copy(st, new FileOutputStream(fFile));
            } catch (IOException ex) {
            }
        }
    }

    /**
     * Create a test model
     *
     * @return a model object representing this folder.
     */
    public static final String TESTPROJECT = "Tobis-2013842-test";

    /**
     * Apply test model
     *
     * @param bl
     */
    public static void applyTestModelBaseline(IProject p) {
        IModel bl = p.getBaseline();
        IModel blr = p.getBaselineReport();
// Apply additional parameters:
        bl.getProcessByFunctionName(Functions.FN_READACOUSTICXML).
                setParameterValue(Functions.PM_READACOUSTICXML_FILENAME + 1, "input/acoustic/Luf20_2013842.xml");
        bl.getProcessByFunctionName(Functions.FN_FILTERACOUSTIC).
                setParameterValue(Functions.PM_FILTERACOUSTIC_DISTANCEEXPR, "log >= 287.9 and log <= 409.9").
                setParameterValue(Functions.PM_FILTERACOUSTIC_FREQEXPR, "frequency == 38000 and transceiver == 2").
                setParameterValue(Functions.PM_FILTERACOUSTIC_NASCEXPR, "acocat == 27 and chtype == 'P'");
        bl.getProcessByFunctionName(Functions.FN_READBIOTICXML).
                setParameterValue(Functions.PM_READACOUSTICXML_FILENAME + 1, "input/biotic/4-2013-3317-1.xml");
        bl.getProcessByFunctionName(Functions.FN_FILTERBIOTIC).
                setParameterValue(Functions.PM_FILTERBIOTIC_CATCHEXPR, "noname == 'HAVSIL'");
        bl.getProcessByFunctionName(Functions.FN_DEFINESTRATA).
                setParameterValue(Functions.PM_DEFINESTRATA_USEPROCESSDATA, true);
//                setParameterValue(Functions.PM_DEFINESTRATA_FILENAME, "polygon/tobis.txt");
        bl.getProcessByFunctionName(Functions.FN_ACOUSTICDENSITY).
                setParameterValue(Functions.PM_ACOUSTICDENSITY_A, -93);
        // Test accurate area method:
        bl.getProcessByFunctionName(Functions.FN_STRATUMAREA).
                setParameterValue(Functions.PM_STRATUMAREA_AREAMETHOD, Functions.AREAMETHOD_SIMPLE);
        blr.getProcessByFunctionName(Functions.FN_ESTIMATEBYPOPULATIONCATEGORY).
                setParameterValue(Functions.PM_ESTIMATEBYPOPCATEGORY_SCALE, 1000);

    }

    /**
     * Extract test model
     *
     * @return
     */
    public static IProject extractTestModel() {
        IProject p = FactoryUtil.acquireProject(ProjectUtils.getSystemProjectRoot(), TESTPROJECT, Factory.TEMPLATE_ACOUSTICABUNDANCETRANSECT);
        applyTestModelBaseline(p);
        // Apply process data from resource into project xml
        try (InputStream stream = InstallerUtil.class.getResourceAsStream("/stox/test/process/processdata.xml")) {
            new ProjectXMLReader(p).readXML(stream);
        } catch (IOException | XMLReaderException ex) {
            Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);
        }
        p.save();
        // Attempt to copy data files to the test project (if not existing)
        String resTemplate = "test";
        String projectFolder = p.getProjectFolder();
        extractToTargetFolder(resTemplate, projectFolder, ProjectUtils.PROJECT_BIOTICINPUT_FOLDER, "4-2013-3317-1.xml", false);
        extractToTargetFolder(resTemplate, projectFolder, ProjectUtils.PROJECT_ACOUSTICINPUT_FOLDER, "Luf20_2013842.xml", false);
        return p;
    } // createTestModel

    private static Double getVersionFromFile(File ver) {
        if (!ver.exists()) {
            return null;
        }
        Double res = null;
        try {
            String line = null;
            BufferedReader br = null;
            br = new BufferedReader(new FileReader(ver));
            while ((line = br.readLine()) != null) {
                res = Conversion.safeStringtoDoubleNULL(line);
            }
            br.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return res;
    }

    private static void printSystemVersionToFile(File ver, double systemVersion) {
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(ver);
            printWriter.println(systemVersion);
        } catch (FileNotFoundException ex) {
        } finally {
            if (printWriter != null) {
                printWriter.close();
            }
        }
    }

    public static File getSystemVersionFile() {
        return new File(ProjectUtils.getSystemFolder() + "/version.txt");
    }

    public static Double getSystemVersionFromResource() {
        File ver = getSystemVersionFile();
        return getVersionFromFile(ver);
    }

    private static String rStoX = "Rstox.tar.gz";

    /*public static void retrieveRstox(String ftpPath, String pkgFile) throws IOException {
        boolean fromResource = false;
        if (fromResource) {
            // Extract the resource to file if not exist.
            String fs = "stox/system/r/" + rStoX;
            InputStream st = Workspace.getResourceAsStream(fs);
            if (st == null) {
                return;
            }
            IOUtils.copy(st, new FileOutputStream(new File(pkgFile)));
        } else {
            // From FTP
            retrieveTARGZFromFTP(ftpPath, pkgFile);
        }
    }*/
    public static final String IMR_FTP = "ftp.imr.no";
    public static final String FTP_STOX = IMR_FTP + "/" + "StoX";
    public static final String FTP_STOXDOWNLOAD = FTP_STOX + "/" + "Download";
    public static final String RSTOX = "Rstox";
    public static final String REFERENCE = "reference";
    public static final String README = "README";
    public static final String FTP_STOXDOWNLOAD_RSTOX = FTP_STOXDOWNLOAD + "/" + RSTOX;
    public static final String FTP_STOXDOWNLOAD_REFERENCE = "StoX/Download/reference";

    public static Boolean retrieveReadMeFromFTP(String ftpPath, String outFile) {
        return retrieveFromFTP(ftpPath, outFile, (FTPFile ftpFile) -> ftpFile.isFile() && ftpFile.getName().startsWith(README));
    }

    /*public static Boolean retrieveTARGZFromFTP(String ftpPath, String outFile) throws IOException {
        return retrieveFromFTP(ftpPath, outFile, (FTPFile ftpFile) -> ftpFile.isFile() && ftpFile.getName().endsWith(TARGZ));
    }*/
    public static Boolean retrieveFromFTP(String ftpPath, String outFile, FTPFileFilter filter) {
        try {
            FTPClient ftpClient = new FTPClient();
            // pass directory path on server to connect
            if (ftpPath == null) {
                return false;
            }
            ftpPath = ftpPath.replace("ftp://", "");
            String[] s = ftpPath.split("/", 2);
            if (s.length != 2) {
                return false;
            }
            String server = s[0];
            String subPath = s[1];
            ftpClient.connect(server);
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            if (!ftpClient.login("anonymous", "")) {
                return false;
            }
            ftpClient.enterLocalPassiveMode(); // bug : mac doesnt allow ftp server connect through through local firewall - thus use passive server
            try (FileOutputStream fos = new FileOutputStream(outFile)) {
                try {
                    String path = subPath + "/";
                    FTPFile[] files = ftpClient.listFiles(path, filter);
                    Optional<FTPFile> opt = Arrays.stream(files).sorted((f1, f2)
                            -> Integer.compare(f1.getName().length(), f2.getName().length())).findFirst();
                    if (opt.isPresent()) {
                        ftpClient.retrieveFile(path + opt.get().getName(), fos);
                    }
                } finally {
                    ftpClient.logout();
                    ftpClient.disconnect();
                }
            }
            return true;
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    public static boolean retrieveReferenceFromFTP() {
        FTPClient ftpClient = new FTPClient();
        try {
            // pass directory path on server to connect  
            ftpClient.connect("ftp.imr.no");
            ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
            // successful  
            if (!ftpClient.login("anonymous", "")) {
                return false;
            }
            ftpClient.enterLocalPassiveMode();
            FTPUtil.retrieveDir(ftpClient, FTP_STOXDOWNLOAD_REFERENCE, ProjectUtils.getSystemReferenceFolder());
        } catch (Exception e) {
            return false;
        } finally {
            try {
                ftpClient.disconnect();
            } catch (IOException e) {
            }
        }
        return true;
    }

    public static String getIOTempDirFile(String fileName) {
        String tmpDir = System.getProperty("java.io.tmpdir");
        tmpDir = new File(tmpDir).getPath().replace("\\", "/");
        return tmpDir + "/" + fileName;
    }

    public static boolean installRstox(Window wnd, String ftpPath, String rFolder) {
//        String pkgFile = getIOTempDirFile(RSTOX + TARGZ);
//        retrieveRstox(ftpPath, pkgFile);
        wnd.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        try {
            return RUtils.installRstox(ftpPath, rFolder);
        } finally {
            wnd.setCursor(Cursor.getDefaultCursor());
        }
    }

    public static void extractSystemFromResource() {
        // Extract strata files
        Double d = getSystemVersionFromResource();
        if (d != null && d >= Project.RESOURCE_VERSION) {
            return;
        }
        // System version
        File ver = getSystemVersionFile();
        printSystemVersionToFile(ver, Project.RESOURCE_VERSION);

        /*// Polygons
        Workspace.getDir(ProjectUtils.getSystemPolygonFolder(), null);
        String systemFolder = ProjectUtils.getSystemFolder();
        String[] pls = {"kolmule", "norwegian_sea", "norwegian_sea2014", "tobis", "herring_spawning2015", "vintertokt_barentshav", "herringlarvae"};
        for (String pl : pls) {
            extractToTargetFolder("system", systemFolder, ProjectUtils.SYSTEM_POLYGON_FOLDER, ProjectUtils.txt(pl), true);
        }*/
        // Reference
        retrieveReferenceFromFTP();
        // Extract test model
        extractTestModel();
    }

}
