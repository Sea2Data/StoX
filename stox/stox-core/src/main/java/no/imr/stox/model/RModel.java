/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.model;

import java.io.PrintWriter;
import java.util.Date;
import no.imr.stox.util.base.Conversion;
import no.imr.stox.util.base.IMRdate;
import no.imr.stox.functions.utils.ProjectUtils;
import no.imr.stox.functions.utils.RUtils;
import no.imr.stox.library.IMetaFunction;
import no.imr.stox.library.IMetaParameter;

/**
 *
 * @author aasmunds
 */
public class RModel extends Model {

    public RModel(IProject project, String modelName, String processFilename) {
        super(project, modelName, processFilename);
    }

    @Override
    protected void runModel() {
        int l = getProcessList().size();
        if (l == 0) {
            return;
        }
        start();
        runRProcess();
        // Current process is the last one
        setRunningProcess(getProcessList().size() - 1);
        // Notify model listeners to update procss badge icon and runModel/end notifications
        for (IProcess process : getProcessList()) {
            onStart(((Process) process));
            onEnd(((Process) process));
        }
        // Stop model.
        stop();
    }

    public void runRProcess() {
        generateRTrigger();
        RUtils.callR(getProject().getRFolder(), getTriggerRFilename(this), true);
    }

    public static String getTriggerFilename(IModel model, String ext) {
        return ProjectUtils.getProjectSubFilePath(model.getProject().getProjectFolder(), ProjectUtils.PROJECT_OUTPUTR, model.getModelName() + "." + ext);
    }

    public static String getTriggerRFilename(IModel model) {
        return getTriggerFilename(model, "R");
    }

    public static String getTriggerOutFilename(IModel model) {
        return getTriggerFilename(model, "ROut");
    }

    public void generateRTrigger() {
        generateRTriggerByFilename(this, getTriggerRFilename(this));
    }

    public static void generateRTriggerByFilename(IModel model, String fileName) {

        try (PrintWriter pw = new PrintWriter(fileName)) {
            // The following env clearance prepares for loading of rjava.dll in CMD BATCH R session
            pw.println("#Trigger script automatically created by StoX at " + model.getModelName());
            pw.println("#Create time: " + IMRdate.formatDate(new Date(), "dd/MM/yyyy HH:mm:ss"));
            /* pw.println("#Unset JAVA_HOME to run in BATCH mode (I.e from StoX)");
            pw.println("Sys.setenv(JAVA_HOME = \"\")");*/
            pw.println("#Load Rstox");
            pw.println("library(Rstox)");
            /*            pw.println("rootFolder <- '" + getProject().getRootFolder() + "'");
             pw.println("projectName <- '" + getProject().getProjectName() + "'");
             pw.println("modelName <- '" + getModelName() + "'");*/
 /*if (model.getProcessByFunctionName(Functions.FN_GETBASELINE) != null || model.getProcessByFunctionName(Functions.FN_BOOTSTRAPBIOTICACOUSTIC) != null) {
                pw.println("#Rstox java class path");
                pw.println("Rstox.init()");
            }*/
            //pw.println("#Set working directory, some parameters relates to the project by .");
            //pw.println("setwd('" + model.getProject().getProjectFolder() + "')"); // This is used by i.e report plot functions
            createProcessScript(pw, model);
            //pw.println("rstox.runRProcessFile(rootFolder, projectName, modelName)");
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    private static String getRelativeProjectFolder(IProject pr) {
        // return project name.
        String res = pr.getProjectFolder().replace("\\", "/");
        String root = ProjectUtils.getSystemProjectRoot();
        if (res.contains(root)) {
            res = res.substring(root.length() + 1, res.length());
        }
        return res;
    }

    /**
     * Create process script in r from java
     *
     * @param pw - print writer
     * @param rootFolder the root folder
     * @param projectName project name
     * @param modelName the model name
     */
    private static void createProcessScript(PrintWriter pw, IModel m) {
        /* Project prj = new Project(rootFolder, projectName, modelName);
         prj.openProject();
         IModel m = prj.getModel(modelName);*/
        String projectName = getRelativeProjectFolder(m.getProject());
        for (IProcess p : m.getProcessList()) {
            if (!p.isEnabled()) {
                continue;
            }
            String processName = p.getProcessName();
            IMetaFunction fnc = p.getMetaFunction();
            if (fnc == null) {
                continue;
            }
            String functionName = fnc.getName();
            String functionCall = functionName + "(";
            // Add hidden projectName parameter for all r calls.
            functionCall += "projectName=\"" + projectName + "\"";
            Boolean subSequentArguments = true;
            for (IMetaParameter pm : fnc.getMetaParameters()) {
                String paramName = pm.getName();
                Object paramValue = p.getValue(pm);
                if (paramValue == null) {
                    continue;
                }
                // Primitive actualization
                Object actualValue = paramValue;
                if (paramValue.toString().contains("Process(")) {
                    String processValue = ((String) paramValue).substring("Process(".length(), ((String) paramValue).length() - 1);
                    // Reference actualization
                    actualValue = "\"" + processValue + "\"";
                } else if (actualValue.toString().equalsIgnoreCase("false") || actualValue.toString().equalsIgnoreCase("true")) {
                    // The value is a boolean
                    actualValue = actualValue.toString().toUpperCase();
                } else if (Conversion.safeStringtoDoubleNULL(actualValue.toString()) == null) {
                    // The value is a string (non numeric) -> enquote
                    // If the value itself contains quotes, escape it according to R quote escapement rule
                    actualValue = "\"" + actualValue.toString().replace("\"", "\\\"") + "\"";
                }
                if (subSequentArguments) {
                    // Separate arguments with comma
                    functionCall = functionCall + ", ";
                }
                functionCall = functionCall + paramName;
                functionCall = functionCall + "=";
                functionCall = functionCall + actualValue;
                subSequentArguments = true;
            }
            // Store parameter dynamically in environment after function call
            functionCall = functionCall + ")";
            pw.println(processName + ".out" + " <- " + functionCall);
        }
    }

    public void runR1() {
    }
}
