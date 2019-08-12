package no.imr.stox.util.base;

import java.io.File;
import java.io.InputStream;

/**
 *
 * @author aasmunds
 */
public class Workspace {

    /**
     * move this to a new module imr base
     *
     * @param base
     * @param dir
     * @return
     */
    public static String getDir(String base, String dir) {
        File f = new File(base);
        if (!f.exists()) {
            f.mkdir();
            if (!f.exists()) {
                return null;
            }
        }
        if(dir == null) {
            return base;
        }
        String spl[] = dir.split("/");
        return getDir(base + "/" + spl[0], spl.length > 1 ? dir.substring(spl[0].length() + 1) : null);
    }

    public static String getDefaultWorkspace() {
        return getDir(System.getProperty("user.home"), "workspace").replace("\\", "/");
    }

    /**
     * get workspace sub folder based on user home and a workspace catalog
     *
     * @param workspaceFolder
     * @return
     */
    public static String getWorkspaceFolder(String workspaceFolder) {
        String spl[] = workspaceFolder.split("/");
        String res = getDefaultWorkspace();
        for (String s : spl) {
            res = getDir(res, s);
        }
        return res;
    }

    public static InputStream getResourceAsStream(String ResName) {
        return Thread.currentThread().getContextClassLoader().getResourceAsStream(ResName);
    }
}
