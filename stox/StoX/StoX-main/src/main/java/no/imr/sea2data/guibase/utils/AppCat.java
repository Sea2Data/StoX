/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.sea2data.guibase.utils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import no.imr.stox.util.http.URLUtil;
import no.imr.stox.util.base.Workspace;
import org.openide.util.Exceptions;

/**
 * Manage automatic installation of netbeans platform products
 *
 * @author aasmunds
 */
public class AppCat {

    String root;
    String appFilter;

    private static final String TEMP = "temp";
    private static final String INSTALL = "install";
    private static final String APPS = "apps";
    private static final String TOOLS = "tools";
    private static final String APPSURL = "ftp://ftp.imr.no/nmd" + "/" + APPS;
    private static final String TOOLSURL = APPSURL + "/" + TOOLS;
    private static final String APPCATURL = APPSURL + "/appcat.txt";
    //private static final String APPCATURL = "file:///E:/install/appcat.txt";

    private static String getWSInstallDir() {
        return Workspace.getDir(Workspace.getDefaultWorkspace(), TEMP + "/" + INSTALL);
    }

    private static String getWSAppsDir() {
        return Workspace.getDir(getWSInstallDir(), APPS);
    }

    private static String getWSToolsDir() {
        return Workspace.getDir(getWSInstallDir(), TOOLS);
    }

    private AppCatEntry create(String line) {
        String[] elms = line.split("\t");
        if (elms.length == 4) {
            try {
                AppCatEntry e = new AppCatEntry(elms[0], elms[1], elms[2], elms[3]);
                if (e.getUrlPath().startsWith("file://") && !Files.isReadable(Paths.get(new URL(e.getUrlPath()).toURI()))) {
                    // No Read access at file systems (i.e limited access for users to alpha directories)
                    return null;
                }
                return e;
            } catch (MalformedURLException | URISyntaxException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return null;
    }

    public class AppCatEntry {

        String app;
        String version;
        String urlDir;
        String urlName;

        public AppCatEntry(String app, String version, String urlDir, String urlName) {
            this.app = app;
            this.version = version;
            this.urlDir = urlDir;
            this.urlName = urlName;
        }

        public String getApp() {
            return app;
        }

        public String getVersion() {
            return version;
        }

        public String getUrlDir() {
            return urlDir;
        }

        public String getUrlName() {
            return urlName;
        }

        @Override
        public String toString() {
            return app + " " + version;
        }

        public String getUrlPath() {
            return urlDir + "/" + urlName;
        }

        public void install() {
            if (root == null) {
                return;
            }
            // Write the param.txt
            List<String> lines = new ArrayList<>();
            lines.add("appname=" + app);
            lines.add("approot=" + root);
            String installerDir = Workspace.getDir(getWSAppsDir(), app + "/" + version);
            String installerFile = installerDir + "/" + urlName;
            String paramFile = installerDir + "/param.txt";
            URLUtil.copyFile(getUrlPath(), installerFile);
            lines.add("installer=" + installerFile);
            lines.add("etcconfig=" + getWSToolsDir() + "/etcconfig.jar");
            try {
                Files.write(Paths.get(paramFile), lines, StandardCharsets.UTF_8);
                String installCmd = "\"" + getWSToolsDir() + "/launchsync.exe\" \"" + getWSToolsDir() + "/updateapp.cmd\" \"" + paramFile + "\"";
                Runtime.getRuntime().exec(installCmd);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }
    }

    public AppCat(String root, String appFilter) {
        this.root = root;
        this.appFilter= appFilter; 
    }

    public AppCat(String appFilter) {
        this(getProgramFilesFolder(), appFilter);
    }

    public static String getProgramFilesFolder() {
        String res = System.getenv("ProgramFiles");
        if (res == null) {
            return null;
        }
        String runningJVM = System.getProperty("sun.arch.data.model");
        if (runningJVM != null && !runningJVM.contains("64")) {
            // Running 32 bit JVM
            String res2 = System.getenv("ProgramFiles(x86)");
            if (res2 != null) {
                // Running 32 bit JVM on 64 bit pc.
                // 32 bit JRE on 64 bit PC has the ProgramFiles(x86) set
                res = res2;
            }
        }
        return res;
    }

    public List<AppCatEntry> getEntries() {
        return URLUtil.getLines(APPCATURL).stream()
                .map(s -> create(s))
                .filter(s -> s != null && (appFilter == null || s.getApp().equals(appFilter)))
                .collect(Collectors.toList());
    }

    public void downloadTools() {
        URLUtil.copyFiles(TOOLSURL, getWSToolsDir(), true);
    }
}
