package no.imr.sea2data.stox.file.filters;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 * Creates a simple filte filter for showing only spx files.
 *
 * @author kjetilf
 */
public class StoxProjectFileFilter extends FileFilter {

    /**
     * File ending constant.
     */
    private static final String ENDS_WITH = "spx";

    @Override
    public boolean accept(final File pathname) {
        boolean valid;
        if (pathname.isDirectory()) {
            return true;
        }
        if (pathname.isFile() && pathname.getName().endsWith(ENDS_WITH)) {
            valid = true;
        } else {
            valid = false;
        }
        return valid;
    }

    @Override
    public String getDescription() {
        return "STOX project files (*." + ENDS_WITH + ")";
    }
}
