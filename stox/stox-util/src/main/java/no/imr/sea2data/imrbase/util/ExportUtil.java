package no.imr.sea2data.imrbase.util;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Export util class for csv export
 *
 * @author Åsmund
 */
public final class ExportUtil {

    public static final char FILE_SEP = '\t';

    /**
     * Hidden constructor
     */
    private ExportUtil() {
    }

    public static String carrageReturnLineFeed(String tabbed) {
        // include carriage return to support notepad
        return tabbed + "\r\n";
    }

    public static String separated(char sep, Object... params) {
        if (params.length == 1 && params[0] instanceof List) {
            List l = (List) params[0];
            params = new Object[l.size()];
            for (int i = 0; i < l.size(); i++) {
                params[i] = l.get(i);
            }
        }
        String res = "";
        for (int i = 0; i < params.length; i++) {
            Object p = params[i];
            if (!res.isEmpty()) {
                res += sep;
            }
            if (p == null || p.toString().isEmpty()) {
                p = "-";
            }
            res += p;
        }
        // include carriage return to support notepad
        return res;
    }

    /**
     * Encodes a tabbed string from dynamic list of parameters
     *
     * @param params
     * @return
     */
    public static String tabbed(Object... params) {
        return separated(FILE_SEP, params);
    }

    public static String csv(Object... params) {
        return separated(',', params);
    }

    public static String trailed(Object... params) {
        return separated('_', params);
    }

    /**
     *
     * @param params
     * @return tabbed carrageReturnLineFeed
     */
    public static String tabbedCRLF(Object... params) {
        return carrageReturnLineFeed(tabbed(params));
    }

    /**
     *
     * @param key
     * @return prepare key
     */
    public static String prepareKey(String key) {
        return key.replace('/', FILE_SEP);
    }

    public static void writeln(Writer wr, String val) {
        try {
            wr.write(ExportUtil.carrageReturnLineFeed(val));
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }
}
