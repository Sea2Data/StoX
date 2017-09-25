/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.r;
//import org.rosuda.JRI.Rengine;

/**
 *
 * @author aasmunds
 */
public class RGUI/* implements RMainLoopCallbacks*/ {

  /*  @Override
    public void rWriteConsole(Rengine re, String text, int oType) {
        System.out.print(text);
    }

    @Override
    public void rBusy(Rengine re, int which) {
        //System.out.println("rBusy(" + which + ")");
    }

    @Override
    public String rReadConsole(Rengine re, String prompt, int addToHistory) {
        System.out.print(prompt);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            String s = br.readLine();
            return (s == null || s.length() == 0) ? s : s + "\n";
        } catch (IOException e) {
            System.out.println("jriReadConsole exception: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void rShowMessage(Rengine re, String message) {
        System.out.println("rShowMessage \"" + message + "\"");
    }

    @Override
    public String rChooseFile(Rengine re, int newFile) {
        FileDialog fd = new FileDialog(new Frame(), (newFile == 0) ? "Select a file" : "Select a new file", (newFile == 0) ? FileDialog.LOAD : FileDialog.SAVE);
        fd.show();
        String res = null;
        if (fd.getDirectory() != null) {
            res = fd.getDirectory();
        }
        if (fd.getFile() != null) {
            res = (res == null) ? fd.getFile() : (res + fd.getFile());
        }
        return res;
    }

    @Override
    public void rFlushConsole(Rengine re) {
    }

    @Override
    public void rLoadHistory(Rengine re, String filename) {
    }

    @Override
    public void rSaveHistory(Rengine re, String filename) {
    }

    public static void main(String[] args) {
        // just making sure we have the right version of everything
        if (!Rengine.versionCheck()) {
            System.err.println("** Version mismatch - Java files don't match library version.");
            System.exit(1);
        }
        System.out.println("Creating Rengine (with arguments)");
        // 1) we pass the arguments from the command line
        // 2) we won't use the main loop at first, we'll start it later
        //    (that's the "false" as second argument)
        // 3) the callbacks are implemented by the TextConsole class above
        Rengine re = new Rengine(args, false, new RGUI());
        System.out.println("Rengine created, waiting for R...");
        // the engine creates R is a new thread, so we should wait until it's ready
        if (!re.waitForR()) {
            System.out.println("Cannot load R");
            return;
        }

        /* High-level API - do not use RNI methods unless there is no other way
         to accomplish what you want */
        // now we start the loop, so the user can use the console
    //    re.startMainLoop();
   // }

}
