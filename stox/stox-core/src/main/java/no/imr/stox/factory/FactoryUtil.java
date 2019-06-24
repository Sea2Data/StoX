package no.imr.stox.factory;

import java.util.List;
import java.util.stream.Collectors;
import no.imr.sea2data.imrbase.matrix.MatrixBO;
import no.imr.sea2data.imrbase.util.Workspace;
import no.imr.stox.bo.CatchabilityParam;
import no.imr.stox.bo.SpeciesTSMix;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.ProjectUtils;
import no.imr.stox.library.IMetaFunction;
import no.imr.stox.library.IMetaParameter;
import no.imr.stox.model.IModel;
import no.imr.stox.model.IProcess;
import no.imr.stox.model.IProject;
import no.imr.stox.model.Project;

/**
 * Run in batch mode.
 *
 * @author atlet
 * @author esmaelmh
 */
public final class FactoryUtil {

    /**
     * Acquire means to open or create a new project If template is given,
     * create a new one, otherwise attempt to open it.
     *
     * @param projectRoot
     * @param projectName
     * @param template
     * @return
     */
    public static IProject openProject(String projectRoot, String projectName) {
        return acquireProject(projectRoot, projectName, null);
    }

    public static IProject createUserDefinedProject(String projectRoot, String projectName) {
        return createProject(projectRoot, projectName, null);
    }

    public static IProject createProject(String projectRoot, String projectName, String template) {
        if (template == null) {
            template = Factory.TEMPLATE_USERDEFINED;
        }
        return acquireProject(projectRoot, projectName, template);
    }

    public static String[] getTemplateProcessNamesByModel(String template, String model) {
        IProject project = new Project();
        Factory.applyProjectTemplate(project, template);
        IModel m = project.getModel(model);
        if (m != null) {
            return m.getProcessList().stream().map(p -> p.getName()).collect(Collectors.toList()).toArray(new String[]{});
        }
        return null;
    }

    public static IProject acquireProject(String projectRoot, String projectName, String template) {
        //File f = new File(projectRoot + "/" + projectname);
        //boolean existingProject = f.exists();
        IProject project = new Project(projectRoot);
        project.setProjectName(projectName);
        if (template == null) {
            // Open existing project
            project.openProject();
            updateProjectToLatestSystemVersion(project);
        } else {
            // Create a new project
            // Apply the model template
            Factory.applyProjectTemplate(project, template);
        }
        // Extract folders into project
        String targetFolder = project.getProjectFolder();
        // create directories on open/new projects.
        Workspace.getDir(targetFolder, ProjectUtils.PROCESS_FOLDER);
        Workspace.getDir(targetFolder, ProjectUtils.PROJECT_BIOTICINPUT_FOLDER);
        Workspace.getDir(targetFolder, ProjectUtils.PROJECT_LANDINGINPUT_FOLDER);
        Workspace.getDir(targetFolder, ProjectUtils.PROJECT_ACOUSTICINPUT_FOLDER);
        Workspace.getDir(targetFolder, ProjectUtils.PROJECT_OUTPUTBASELINE_DATA_TRASH);
        Workspace.getDir(targetFolder, ProjectUtils.PROJECT_OUTPUTBASELINE_REPORT_TRASH);
        Workspace.getDir(targetFolder, ProjectUtils.PROJECT_OUTPUTR_DATA_TRASH);
        Workspace.getDir(targetFolder, ProjectUtils.PROJECT_OUTPUTR_REPORT_TRASH);
        return project;
    }

    /**
     * Make old projects compatible to new system versions.
     */
    private static void updateProjectToLatestSystemVersion(IProject prj) {
        if (prj.getResourceVersion() < 1.09) {

        }

        if (prj.getResourceVersion() < 1.33) {
            IProcess prw = prj.getBaseline().findProcessByFunction(Functions.FN_SUPERINDABUNDANCE);
            if (prw != null) {
                prw.setProcessName(Functions.FN_SUPERINDABUNDANCE);
            }
            prj.getBaselineReport().getProcessList().clear();
            Factory.createAbundanceReport(prj.getBaselineReport(), true, 1.0, 1000, Functions.COL_IND_AGE);
            replaceParameter(prj.getBaseline(), Functions.FN_DEFINESTRATA, Functions.PM_DEFINESTRATA_FILENAME, "polygon/", "${STOX}/reference/stratum/");
            replaceParameter(prj.getBaseline(), Functions.FN_FILTERBIOTIC, Functions.PM_FILTERBIOTIC_FISHSTATIONEXPR, "fs.getCount('SILDG03')", "fs.getCountBySpecies('161722.G03')");
            replaceParameter(prj.getBaseline(), Functions.FN_FILTERBIOTIC, Functions.PM_FILTERBIOTIC_CATCHEXPR, "noname eq 'SILDG03'", "species eq '161722.G03'");

        }

        if (prj.getResourceVersion() < 1.65) {
            for (String fn : new String[]{Functions.FN_BIOSTATIONASSIGNMENT/*, Functions.FN_RECTANGLEASSIGNMENT*/}) {
                IProcess prw = prj.getBaseline().findProcessByFunction(fn);
                if (prw != null) {
                    prw.setParameterValue("EstLayers", "1" + "~" + Functions.WATERCOLUMN_PELBOT);
                }
            }
        }

        if (prj.getResourceVersion() < 1.71) {
            boolean hasSweptarea = prj.getBaseline().getProcessByFunctionName(Functions.FN_SWEPTAREADENSITY) != null;
            setParameterValue(prj.getBaseline(), Functions.FN_RUNBOOTSTRAP, Functions.PM_RUNBOOTSTRAP_BIOTICMETHOD, "PSU~Stratum");
            if (hasSweptarea) {
                setParameterValue(prj.getBaseline(), Functions.FN_RUNBOOTSTRAP, Functions.PM_RUNBOOTSTRAP_ACOUSTICMETHOD, "");
            }
        }
        if (prj.getResourceVersion() < 1.73) {
            // Split Nasc mix acocat is moved to speciesTS parameter
            List<IProcess> pr = prj.getBaseline().getProcessesByFunctionName(Functions.FN_SPLITNASC);
            pr.stream().forEach(p -> {
                String mixAco = (String) p.getParameterValue(Functions.PM_SPLITNASC_MIXACOCAT);
                String specTs = (String) p.getParameterValue(Functions.PM_SPLITNASC_SPECIESTS);
                List<SpeciesTSMix> l = SpeciesTSMix.fromString(specTs);
                if (l != null) {
                    l.stream().forEach(s -> s.setMixAcoCat(mixAco));
                    specTs = SpeciesTSMix.toString(l);
                    p.setParameterValue(Functions.PM_SPLITNASC_SPECIESTS, specTs);
                }
                p.setParameterValue(Functions.PM_SPLITNASC_MIXACOCAT, null);
            });

        }
        if (prj.getResourceVersion() < 1.74) {
            // Resolve deprecation <-1.72v: swept area swept area sweep width method length dependent into catchability length dependent sweep width.
            //----------------------------------
            IProcess fbPr = prj.getBaseline().getProcessByFunctionName(Functions.FN_FILTERBIOTIC);
            IProcess swPr = prj.getBaseline().getProcessByFunctionName(Functions.FN_SWEPTAREADENSITY);
            if (swPr != null) {
                if (fbPr != null) {
                    String bioticData = (String) swPr.getParameterValue(Functions.PM_SWEPTAREADENSITY_BIOTICDATA);
                    if (bioticData == null) {
                        swPr.setParameterProcessValue(Functions.PM_SWEPTAREADENSITY_BIOTICDATA, fbPr.getName());
                    }
                }
            }
        }
        if (prj.getResourceVersion() < 1.75) {
            // Old Table aliased with sweepwidth should be associated with selectivity if method=selectivity
            // This is true for only some few projects created by elena in alpha version.
            // remove this conversion later
            //----------------------------------
            IProcess pr = prj.getBaseline().getProcessByFunctionName(Functions.FN_CATCHABILITY);
            if (pr != null) {
                String met = (String) pr.getParameterValue(Functions.PM_CATCHABILITY_CATCHABILITYMETHOD);
                String par = (String) pr.getParameterValue(Functions.PM_CATCHABILITY_PARLENGTHDEPENDENTSWEEPWIDTH);
                if (par != null) {
                    par = CatchabilityParam.toString(CatchabilityParam.fromString(par));
                    switch (met) {
                        case Functions.CATCHABILITYMETHOD_LENGTHDEPENDENTSELECTIVITY: {
                            if (par != null && met != null && met.equals(Functions.CATCHABILITYMETHOD_LENGTHDEPENDENTSELECTIVITY)) {
                                pr.setParameterValue(Functions.PM_CATCHABILITY_PARLENGTHDEPENDENTSWEEPWIDTH, null);
                                pr.setParameterValue(Functions.PM_CATCHABILITY_PARLENGTHDEPENDENTSELECTIVITY, par);
                            }
                            break;
                        }
                        case Functions.CATCHABILITYMETHOD_LENGTHDEPENDENTSWEEPWIDTH: {
                            pr.setParameterValue(Functions.PM_CATCHABILITY_PARLENGTHDEPENDENTSWEEPWIDTH, par);
                            break;
                        }
                    }
                }
            }
            pr = prj.getBaseline().getProcessByFunctionName(Functions.FN_SPLITNASC);
            if (pr != null) {
                String par = (String) pr.getParameterValue(Functions.PM_SPLITNASC_SPECIESTS);
                if (par != null) {
                    par = SpeciesTSMix.toString(SpeciesTSMix.fromString(par));
                    pr.setParameterValue(Functions.PM_SPLITNASC_SPECIESTS, par);
                }
            }
        }
        if (prj.getResourceVersion() < 1.87) {
            replaceParameter(prj.getBaseline(), Functions.FN_FILTERBIOTIC, Functions.PM_FILTERBIOTIC_FISHSTATIONEXPR, "sild\\'G0", "sildG0");
            replaceParameter(prj.getBaseline(), Functions.FN_FILTERBIOTIC, Functions.PM_FILTERBIOTIC_FISHSTATIONEXPR, "SILD\\'G0", "SILDG0");
            replaceParameter(prj.getBaseline(), Functions.FN_FILTERBIOTIC, Functions.PM_FILTERBIOTIC_CATCHEXPR, "sild\\'G0", "sildG0");
            replaceParameter(prj.getBaseline(), Functions.FN_FILTERBIOTIC, Functions.PM_FILTERBIOTIC_CATCHEXPR, "SILD\\'G0", "SILDG0");
            replaceParameter(prj.getBaseline(), Functions.FN_SPLITNASC, Functions.PM_SPLITNASC_SPECIESTS, "sild\\'G0", "sildG0");
            replaceParameter(prj.getBaseline(), Functions.FN_FILTERBIOTIC, Functions.PM_SPLITNASC_SPECIESTS, "SILD\\'G0", "SILDG0");
        }

        if (prj.getResourceVersion() < 1.89) {
            replaceParameter(prj.getBaseline(), Functions.FN_FILTERBIOTIC, Functions.PM_FILTERBIOTIC_INDEXPR, "lengthunit", "tinuhtgnel"); // avoid replacinging length in lengthunit by trick
            replaceParameter(prj.getBaseline(), Functions.FN_FILTERBIOTIC, Functions.PM_FILTERBIOTIC_INDEXPR, "length", "lengthcm"); // use lengthcm instead of length
            replaceParameter(prj.getBaseline(), Functions.FN_FILTERBIOTIC, Functions.PM_FILTERBIOTIC_INDEXPR, "tinuhtgnel", "lengthunit"); // restore lengthunit by trick
            /*IProcess readB = prj.getBaseline().findProcessByFunction(Functions.FN_READBIOTICXML);
            if (readB != null) {
                xint idxReadB = prj.getBaseline().getProcessList().indexOf(readB);
                IProcess defLength = prj.getBaseline().insertProcess(Functions.FN_DEFINELENGTHCENTIMETER, Functions.FN_DEFINELENGTHCENTIMETER, idxReadB + 1)
                        .setParameterProcessValue(Functions.PM_DEFINELENGTHCENTIMETER_BIOTICDATA, readB.getName()).setFileOutput(false);
                IProcess defWeight = prj.getBaseline().insertProcess(Functions.FN_DEFINEINDIVIDUALWEIGHTGRAM, Functions.FN_DEFINEINDIVIDUALWEIGHTGRAM, idxReadB + 2)
                        .setParameterProcessValue(Functions.PM_DEFINEINDIVIDUALWEIGHTGRAM_BIOTICDATA, defLength.getName()).setFileOutput(false);
                IProcess defAge = prj.getBaseline().insertProcess(Functions.FN_MERGEAGEDETERMINATIONTOINDIVIDUAL, Functions.FN_MERGEAGEDETERMINATIONTOINDIVIDUAL, idxReadB + 3)
                        .setParameterProcessValue(Functions.PM_MERGEAGEDETERMINATIONTOINDIVIDUAL_BIOTICDATA, defWeight.getName()).setFileOutput(false);
                IProcess spec = prj.getBaseline().findProcess(Functions.FN_REDEFINESPECCAT);
                IProcess defSpecCat = prj.getBaseline().insertProcess(Functions.FN_REDEFINESPECCAT + (spec == null ? "" : "Var"), Functions.FN_REDEFINESPECCAT, idxReadB + 4)
                        .setParameterProcessValue(Functions.PM_REDEFINESPECCAT_BIOTICDATA, defAge.getName())
                        .setParameterValue(Functions.PM_REDEFINESPECCAT_SPECCATMETHOD, Functions.SPECCATMETHOD_SELECTVAR)
                        .setParameterValue(Functions.PM_REDEFINESPECCAT_SPECVARBIOTIC, "commonname");
                defSpecCat.setFileOutput(false);
                for (int i = idxReadB + 5; i < prj.getBaseline().getProcessList().size(); i++) {
                    IProcess pr = prj.getBaseline().getProcessList().get(i);
                    if (pr.getMetaFunction() != null && pr.getMetaFunction().getName().equals(Functions.FN_READBIOTICXML)) {
                        continue;
                    }
                    IMetaParameter mep = pr.getMetaFunction().getMetaParameters().stream()
                            .filter(mp -> mp.getMetaDataType().getName().equals(Functions.DT_BIOTICDATA)).findFirst().orElse(null);
                    if (mep != null && pr.getProcessNameFromParameter(mep) != null && pr.getProcessNameFromParameter(mep).equals(readB.getName())) {
                        pr.setParameterProcessValue(mep.getName(), defSpecCat.getName());
                    }
                }
            }*/
        }
        if (prj.getResourceVersion() == 1.89) {
            IProcess readB = prj.getBaseline().findProcessByFunction(Functions.FN_REDEFINESPECCAT);
            if (readB != null) {
                readB.setParameterProcessValue(Functions.PM_REDEFINESPECCAT_BIOTICDATA, defAge.getName());
            }
        }
        // Remove processes not pointing to functions
        for (IModel m : prj.getModels().values()) {
            for (int i = m.getProcessList().size() - 1; i >= 0; i--) {
                IProcess p = m.getProcessList().get(i);
                if (p.getMetaFunction() == null) {
                    p.getModel().getProcessList().remove(p);
                }
            }
        }
        /*prj.getModels().values().stream()
                .filter(m -> {
                    return !m.getProcessList().isEmpty();
                })
                .flatMap(m -> m.getProcessList().stream())
                .filter(p -> {
                    return p.getMetaFunction() == null;
                })
                .forEach(p -> {
                    p.getModel().getProcessList().remove(p);
                });
         */
 /*if (prj.getStoxVersion().equals("2.2")) {
                IProcess prw = prj.getRModel().findProcessByFunction(Functions.);
            prj.getRModel().get
        }*/

    }

    /*    private static void checkRDataVariable(IProject prj, String functionName, String variableParam, String functionRef) {
        IProcess prw = prj.getRModelReport().findProcessByFunction(functionName);
        if (prw != null) {
            IProcess p = prj.getRModel().findProcessByFunction(functionRef);
            Object value = p != null ? ProjectUtils.getOutVar(p.getProcessName()) : null;
            setParameterValueIfNULL(prj.getRModelReport(), functionName, variableParam, value);
        }
    }
     */
    private static void setParameterValueIfNULL(IModel model, String functionName, String param, Object value) {
        IProcess prw = model.findProcessByFunction(functionName);
        if (prw != null) {
            String bVar = (String) prw.getParameterValue(param);
            if (bVar == null && value != null) {
                prw.setParameterValue(param, value);
            }
        }
    }

    private static void setParameterValue(IModel model, String functionName, String param, Object value) {
        IProcess prw = model.findProcessByFunction(functionName);
        if (prw != null) {
            prw.setParameterValue(param, value);
        }
    }

    private static void replaceParameter(IModel model, String func, String param, String text, String newText) {
        IProcess prw = model.findProcessByFunction(func);
        replaceParameter(prw, param, text, newText);
    }

    private static void replaceParameter(IProcess prw, String param, String text, String newText) {
        if (prw != null) {
            String pval = (String) prw.getParameterValue(param);
            if (pval != null) {
                pval = pval.replace(text, newText);
                prw.setParameterValue(param, pval);
            }
        }
    }
}
