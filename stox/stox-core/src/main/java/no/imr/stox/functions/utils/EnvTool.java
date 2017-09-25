/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aasmunds
 * @deprecated Using full path to R in command line
 */
public class EnvTool {

    private static final String REGQUERY_UTIL = "reg query ";
    private static final String REGSTR_TOKEN = "REG_SZ";

    private static final String PATH_FOLDER_CMD = REGQUERY_UTIL
            + "HKCU\\Environment /v Path";

    public static String getPathFromRegistry() {
        try {
            Process process = Runtime.getRuntime().exec(PATH_FOLDER_CMD);
            StreamReader reader = new StreamReader(process.getInputStream());

            reader.start();
            process.waitFor();
            reader.join();

            String result = reader.getResult();
            int p = result.indexOf(REGSTR_TOKEN);

            if (p == -1) {
                return null;
            }
            process.destroy();
            return result.substring(p + REGSTR_TOKEN.length()).trim();
        } catch (Exception e) {
            return null;
        }
    }

    static class StreamReader extends Thread {

        private final InputStream is;
        private final StringWriter sw;

        StreamReader(InputStream is) {
            this.is = is;
            sw = new StringWriter();
        }

        @Override
        public void run() {
            try {
                int c;
                while ((c = is.read()) != -1) {
                    sw.write(c);
                }
            } catch (IOException e) {
            }
        }

        String getResult() {
            return sw.toString();
        }
    }
    public static boolean addToPath(String newPath) {
        try {
            String value = getPathFromRegistry();
            if(value == null || value.isEmpty()) {
                value = newPath;
            } else if(!value.contains(newPath)) {
                value += ";" + newPath;
            } else {
                return false;
            }
            Runtime rt = Runtime.getRuntime();
            // modify the user environment path with system notification included.
            Process p = rt.exec("setx Path \"" + value + "\"");
            p.waitFor();
            p.destroy();
         } catch (InterruptedException | IOException ex) {
            Logger.getLogger(EnvTool.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
    public static void main(String s[]) {
        addToPath("C:\\Program Files\\R\\R-3.1.2\\bin\\x64");
    }
}
