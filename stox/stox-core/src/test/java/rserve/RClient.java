/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rserve;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;
import org.rosuda.REngine.REXP;
import org.rosuda.REngine.REXPMismatchException;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

/**
 *
 * @author aasmunds
 */
public class RClient {

    @Test
    public void test() {
        try {
            // Run in R
            // install.packages("Rserve")
            // install.packages("forecast")
            
            String cmd = "C:\\Users\\aasmunds\\Documents\\R\\R-3.5.2\\bin\\x64\\R.exe" + " -e " + "\"library(Rserve);Rserve(port=6311)\"";
            Runtime.getRuntime().exec(cmd);
            RConnection con = new RConnection();
            // Run function 1
            con.voidEval("library(Rstox)");
            con.voidEval("templ <- createProject()");
            // Break in gui... and run function 2 later. the R variable a is available on the r server connection
            REXP r = con.eval("names(templ)");
            System.out.println("result: " + r.asStrings());
            con.shutdown();
        } catch (RserveException | REXPMismatchException | IOException ex) {
            Logger.getLogger(RClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
