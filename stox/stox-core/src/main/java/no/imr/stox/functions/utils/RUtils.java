/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.utils;

import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.io.WKTWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.stox.bo.PolygonAreaMatrix;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author aasmunds
 */
public class RUtils {

    /**
     * Adds the specified path to the java library path
     *
     * @param pathToAdd the path to add
     */
    public static void addLibraryPath(File pathToAdd) {
        try {
            if (pathToAdd == null) {
                return;
            }
            final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
            usrPathsField.setAccessible(true);

            //get array of paths
            final String[] paths = (String[]) usrPathsField.get(null);

            //check if the path to add is already present
            for (String path : paths) {
                if (path.equals(pathToAdd.getPath())) {
                    return;
                }
            }

            //add the new path
            final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
            newPaths[newPaths.length - 1] = pathToAdd.getPath();
            usrPathsField.set(null, newPaths);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
        }
    }

    /**
     * Return the first safe file from a list of arrays
     *
     * @param files
     * @return
     */
    public static File safeFileArr(String... files) {
        for (String fn : files) {
            File f = safeFile(fn);
            if (f != null) {
                return f;
            }
        }
        return null;
    }

    /**
     * create a safe file from filename
     *
     * @param fileName
     * @return
     */
    public static File safeFile(String fileName) {
        File f = new File(fileName);
        if (!f.exists()) {
            return null;
        }
        return f;
    }

    /**
     * Create a safe file from base and sub folder
     *
     * @param base
     * @param subFolder
     * @return
     */
    public static File safeFile(File base, String subFolder) {
        if (base == null || subFolder == null) {
            return null;
        }
        return safeFile(base.getPath() + subFolder);
    }

    /**
     * Search a sub directory for a given file
     *
     * @param base
     * @param name
     * @return
     */
    public static File searchSubDir(File base, String name) {
        if (base == null) {
            return null;
        }
        File[] lf = base.listFiles();
        for (File rf : lf) {
            if (rf.isDirectory() && rf.getPath().contains(name)) {
                return rf;
            }
        }
        return null;
    }

    /**
     * Get R folder
     *
     * @param errors
     * @return
     */
    public static File getRFolder(List<String> errors) {
        // For mac:
        String osName = System.getProperty("os.name").toLowerCase();
        switch (osName) {
            case "mac os x":
                return new File("/Library/Frameworks/R.framework/Versions/Current/Resources/bin");
            default:
                // For windows, maybe linux:
                String[] files = new String[]{"C:/Program Files", "C:/Program Files (x86)", System.getenv("ProgramFiles"), System.getenv("ProgramFiles(X86)"),
                    System.getProperty("user.home"), "C:", "/usr/bin"};
                for (String fn : files) {
                    File f = safeFile(fn + "/R");
                    if (f == null) {
                        if (errors != null) {
                            errors.add(fn + "/R" + " not found");
                        }
                        continue;
                    }
                    if (errors != null) {
                        errors.clear();
                    }
                    File[] fl = f.listFiles();
                    if (fl == null) {
                        continue;
                    }
                    List<File> lf = Arrays.asList(f.listFiles());
                    Collections.sort(lf, (File o1, File o2) -> {
                        return -Integer.compare(getRVersionByPath(o1.getPath()), getRVersionByPath(o2.getPath()));
                    });
                    for (File rf : lf) {
                        if (rf.isDirectory() && rf.getPath().contains("R-")) {
                            File rBin = getRBinFolderByRoot(rf);
                            if (rBin != null) {
                                if (errors != null) {
                                    errors.clear();
                                }
                                return rf;
                            } else if (errors != null) {
                                errors.add(rf.getPath() + "/bin/" + getJREArchID() + " not found in ");
                            }
                        } else if (errors != null) {
                            errors.add(rf.getPath() + " is not a valid R installation");
                        }
                    }
                }
        }
        return null;
    }

    public static File getRBinFolder(List<String> errors) {
        File f = getRBinFolderByRoot(getRFolder(errors));
        return f;
    }

    public static File getRBinFolderByRoot(File root) {
        File f = safeFile(root, "/bin/" + getJREArchID());
        return f;
    }

    public static String getJREArchID() {
        String res = getJREArchN() == 64 ? "x64" : "i386";
        return res;
    }

    /**
     * Determine if running JVM 32 or 64 bit from the program. R installation
     * should correspond to this.
     *
     * @return
     */
    public static Integer getJREArchN() {
        String res = System.getProperty("sun.arch.data.model");
        return res.contains("64") ? 64 : 32;
    }

    public static String getRVersion(String rFolder) {
        return callR(rFolder, "rVersion.R", "paste(sessionInfo()[1]$R.version$major, sessionInfo()[1]$R.version$minor, sep='.')");
    }

    public static String getRstoxVersion(String rFolder) {
        return callR(rFolder, "rStoxVersion.r", "suppressMessages(library(Rstox));sessionInfo()$otherPkgs$Rstox$Version");
    }

    public static String getTmpDir() {
        String tmpDir = System.getProperty("java.io.tmpdir");
        if (tmpDir != null && !(tmpDir.endsWith("/") || tmpDir.endsWith("\\"))) {
            tmpDir += "/";
        }
        return tmpDir;
    }

    public static String callR(String rFolder, String fileName, String cmd) {
        try {
            String triggerFile = getTmpDir() + fileName;
            try (PrintWriter pw = new PrintWriter(triggerFile)) {
                pw.println(cmd);
            }
            Process proc = callR(rFolder, triggerFile, false);
            if (proc != null) {
                java.io.InputStream is = proc.getInputStream();
                java.util.Scanner s = new java.util.Scanner(is).useDelimiter(";");
                while (s.hasNext()) {
                    String str = s.next();
                    return StringUtils.substringBetween(str, "\"", "\"");
                }
            }
        } catch (IOException ex) {
        }
        return "";
    }

    public static Boolean installRstox(String ftpPath, String rFolder) {
        try {
            String triggerFile = getTmpDir() + "installRstox.R";
            try (PrintWriter pw = new PrintWriter(triggerFile)) {
                //pw.println("Sys.setenv(JAVA_HOME = \"\")");
                if (ftpPath.startsWith("ftp.")) {
                    ftpPath = "ftp://" + ftpPath;
                }
                ftpPath = ftpPath + "/" + "README";
                pw.println("source(\"" + ftpPath + "\")");
                //pw.println("install.packages(pkgFile, repos=NULL, type=\"source\", lib=.libPaths()[1])");
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
            callR(rFolder, triggerFile, true);
            if (!new File(triggerFile + "out").exists()) {
                return false;
            }
            List<String> lines = Files.readAllLines(Paths.get(triggerFile + "out"), Charset.forName("UTF-8"));
            lines = lines.stream().filter(s -> s.contains("DONE")).collect(Collectors.toList());
            return !lines.isEmpty();
        } catch (IOException ex) {
            Logger.getLogger(RUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * build a command for trigger file
     *
     * @param rFolder
     * @param fileName
     * @param batch true if the output should be sent to file (batch). false if
     * slave only.
     * @return
     */
    public static String[] getTriggerCommand(String rFolder, String fileName, boolean batch) {
        String r = rFolder + "/R";
        return batch ? new String[]{r, "CMD", "BATCH", "--vanilla", "--slave", fileName}
                : new String[]{r, "--vanilla", "--slave", "-f", fileName};
    }

    public static java.lang.Process callR(String rFolder, String triggerFile, boolean batch) {
        try {
            File f = new File(triggerFile);
            if (!f.exists()) {
                return null;
            }
            java.lang.Process p = Runtime.getRuntime().exec(getTriggerCommand(rFolder, f.getName(), batch), null, f.getParentFile());
            if (p != null) {
                try {
                    p.waitFor();
                } catch (InterruptedException ex) {
                    Logger.getLogger(RUtils.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            return p;
        } catch (IOException ex) {
        }
        return null;
    }

    private static String getPolygonWKT(Geometry gm) {
        GeometryFactory gf = new GeometryFactory();
        StringWriter writer = new StringWriter();
        WKTWriter wktWriter = new WKTWriter(2);
        try {
            wktWriter.write(gm, writer);
        } catch (IOException e) {
        }
        String wkt = writer.toString();
        return wkt;
    }

    /**
     * calculate accurate polygons by use of r in a separate process.
     *
     * @param polygons
     * @return
     */
    static public PolygonAreaMatrix getAccuratePolygons(String rFolder, MatrixBO polygons) {
        PolygonAreaMatrix res = new PolygonAreaMatrix();
        String fileName = getTmpDir() + "area.txt";
        try (PrintWriter pw = new PrintWriter(fileName)) {
            pw.println("Sys.setenv(JAVA_HOME = \"\")");
            pw.println("library(Rstox)");
//            pw.println("source('" + ProjectUtils.getSystemRFolder() + "/rstox_spatial.r" + "')");
            pw.print("pol <- cbind(");
            List<String> rowKeys = polygons.getRowKeys();
            for (int i = 0; i < rowKeys.size(); i++) {
                String strata = rowKeys.get(i);
                //polygons.getRowKeys()
                pw.print("c('" + strata + "', '" + getPolygonWKT((Geometry) polygons.getRowColValue(strata, Functions.COL_POLVAR_POLYGON)) + "')");
                if (i < rowKeys.size() - 1) {
                    pw.print(",");
                } else {
                    pw.print(")");
                }
                pw.println();
            }
            pw.println("invisible(apply(cbind(cbind(pol[1,], apply(cbind(pol[2,]), MARGIN=1,");
            pw.println("FUN=function(p) polyArea(p)))), MARGIN=1,");
            pw.println("FUN=function(x) cat(x[1],':', x[2],sep='', ';')))");
        } catch (FileNotFoundException ex) {
            throw new UncheckedIOException(ex);
        }
        Process proc = callR(rFolder, fileName, false);
        java.io.InputStream is = proc.getInputStream();
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter(";");
        while (s.hasNext()) {
            String[] str = s.next().split(":");
            res.getData().setRowValue(str[0], Double.valueOf(str[1]));
            //System.out.println(s.next());
        }
        return res;
    }

    private static Integer getRVersionByPath(String path) {
        int pos = path.indexOf("R-");
        if (pos == -1) {
            return 0;
        }
        String s = Conversion.safeSubstring(path, pos + 2, path.length());
        String[] st = s.split("\\.");
        if (st.length != 3) {
            return 0;
        }
        return Conversion.safeStringtoInteger(st[0]) * 10000 + Conversion.safeStringtoInteger(st[1]) * 100 + Conversion.safeStringtoInteger(st[2]) * 1;
    }
}
