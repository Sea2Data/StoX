package no.imr.stox.factory;

import no.imr.sea2data.imrbase.util.Workspace;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.ProjectUtils;
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

        /*if (prj.getResourceVersion() < 1.09) {
            IProcess prw = prj.getBaseline().findProcessByFunction(Functions.FN_INDIVIDUALDATASTATIONS);
            IProcess prw2 = prj.getBaseline().findProcessByFunction(Functions.FN_ABUNDANCE);
            if (prw != null && prw2 != null) {
                // Set abundance parameter to get access to estlayerdef.
                prw.setParameterValue(Functions.PM_INDIVIDUALDATASTATIONS_ABUNDANCE, "Process(" + prw2.getName() + ")");
            }
            prw = prj.getBaseline().findProcessByFunction(Functions.FN_FILTERACOUSTIC);
            if (prw != null && prw2 != null) {
                String s = (String) prw.getParameterValue(Functions.PM_FILTERACOUSTIC_FREQEXPR);
                if (s != null) {
                    prw.setParameterValue(Functions.PM_FILTERACOUSTIC_FREQEXPR, s.replace("tranceiver", "transceiver")); // Spell check
                }
            }
        }
        if (prj.getResourceVersion() < 1.17) {
            IProcess prw = prj.getBaseline().findProcessByFunction(Functions.FN_FILTERBIOTIC);
            if (prw != null) {
                String s = (String) prw.getParameterValue(Functions.PM_FILTERBIOTIC_CATCHEXPR);
                if (s != null) {
                    prw.setParameterValue(Functions.PM_FILTERBIOTIC_CATCHEXPR, s.replace("species", "noname")); // species as noname
                }
            }
        }

        if (prj.getResourceVersion() < 1.21) {
            IProcess prw = prj.getBaseline().findProcessByFunction(Functions.FN_SUPERINDABUNDANCE);
            if (prw != null) {
                prw.setParameterProcessValueByFunction(Functions.PM_SUPERINDABUNDANCE_PROCESSDATA, Functions.FN_READPROCESSDATA);
            }
        }
        if (prj.getResourceVersion() < 1.32) {
            IProcess prw = prj.getBaseline().findProcessByFunction(Functions.FN_SUPERINDABUNDANCE);
            if (prw != null) {
                prw.setParameterProcessValueByFunction(Functions.PM_SUPERINDABUNDANCE_INDIVIDUALDATA, Functions.FN_INDIVIDUALDATA);
            }
            prw = prj.getBaseline().findProcessByFunction(Functions.FN_CORRECTFORINNSUFFICIENTSAMPLING);
            if (prw != null) {
                prj.getBaseline().getProcessList().remove(prw);
            }
        }
        // Ensure that report processes belongs to the report model
        IProcess p = prj.getBaseline().getProcessByFunctionName(Functions.FN_ESTIMATEBYPOPULATIONCATEGORY);
        if (p != null) {
            p.moveTo(prj.getBaselineReport());
        }
        String[] s = {Functions.FN_PLOTNASCDISTRIBUTION, Functions.FN_PLOTABUNDANCE};
        for (String pr : s) {
            p = prj.getRModel().getProcessByFunctionName(pr);
            if (p != null) {
                p.moveTo(prj.getRModelReport());
            }
        }

        // Use FillMissingData in baseline report when < 1.33:
        if (prj.getResourceVersion() < 1.33) {
            IProcess prw = prj.getBaseline().findProcessByFunction(Functions.FN_SUPERINDABUNDANCE);
            if (prw != null) {
                prw.setProcessName(Functions.FN_SUPERINDABUNDANCE);
            }
            prj.getBaselineReport().getProcessList().clear();
            Factory.createAbundanceReport(prj.getBaselineReport(), true, 1.0, 1000, Functions.COL_IND_AGE);
        }
        
        Boolean hasAcousticDensity = prj.getBaseline().getProcessByFunctionName(Functions.FN_ACOUSTICDENSITY) != null;
        Boolean hasSweptarea = prj.getBaseline().getProcessByFunctionName(Functions.FN_SWEPTAREADENSITY) != null;
        if (prj.getResourceVersion() < 1.42 && (hasAcousticDensity || hasSweptarea)) {
            prj.getRModel().getProcessList().clear();
            prj.getRModelReport().getProcessList().clear();
            // Reset the R template 
            if (hasAcousticDensity) {
                Factory.createAcousticRWithUncertainty(prj.getRModel());
                Factory.createRReport(prj.getRModelReport());
            } else if (hasSweptarea) {
                Factory.createSweptareaRModelWithUncertainty(prj.getRModel());
                Factory.createSweptareaRReport(prj.getRModelReport(), true);
            }
        }

        if (prj.getResourceVersion() < 1.41) {
            IProcess prw = prj.getRModelReport().findProcessByFunction(Functions.FN_PLOTABUNDANCE);
            if (prw != null) {
                prw.setProcessName(Functions.FN_PLOTABUNDANCE);
            }
        }
        if (prj.getResourceVersion() < 1.67) {
            hasAcousticDensity = prj.getBaseline().getProcessByFunctionName(Functions.FN_ACOUSTICDENSITY) != null;
            hasSweptarea = prj.getBaseline().getProcessByFunctionName(Functions.FN_SWEPTAREADENSITY) != null
                    || prj.getBaseline().getProcessByFunctionName(Functions.FN_LARVAEDENSITY) != null;
            prj.getRModel().getProcessList().clear();
            prj.getRModelReport().getProcessList().clear();
            // Reset the R template 
            if (hasAcousticDensity) {
                Factory.createAcousticRWithUncertainty(prj.getRModel());
                Factory.createRReport(prj.getRModelReport());
            } else if (hasSweptarea) {
                Factory.createSweptareaRModelWithUncertainty(prj.getRModel());
                Factory.createSweptareaRReport(prj.getRModelReport(), true);
            }
        }
        if (prj.getResourceVersion() < 1.66) {
            IProcess prw = prj.getBaseline().findProcessByFunction(Functions.FN_FILTERBIOTIC);
            if (prw != null) {
                String str = (String) prw.getParameterValue(Functions.PM_FILTERBIOTIC_CATCHEXPR);
                if (str != null) {
                    if (str.contains("==") && !str.contains(" == ")) {
                        str = str.replace("==", " == ");
                    }
                    str = str.replace(" eq ", " == ");
                    str = str.replace("noname == 'SILD'", "species == '161722'");
                    str = str.replace("noname == 'SILDG05'", "species == '161722.G05'");
                    str = str.replace("noname == 'SILDG03'", "species == '161722.G03'");
                    str = str.replace("noname == 'HYSE'", "species == '164744'");
                    str = str.replace("noname == 'TORSK'", "species == '164712'");
                    str = str.replace("noname == 'SEI'", "species == '164727'");
                    str = str.replace("noname == 'MAKRELL'", "species == '172414'");
                    str = str.replace("noname == 'KOLMULE'", "species == '164774'");
                    str = str.replace("noname == 'LODDE'", "species == '162035'");
                    prw.setParameterValue(Functions.PM_FILTERBIOTIC_CATCHEXPR, str); // species as noname
                }
            }
        }
        if (prj.getResourceVersion() < 1.69) {
            hasAcousticDensity = prj.getBaseline().getProcessByFunctionName(Functions.FN_ACOUSTICDENSITY) != null;
            IProcess prw = prj.getRModelReport().findProcessByFunction(Functions.FN_PLOTABUNDANCE);
            if (prw != null) {
                String str = (String) prw.getParameterValue(Functions.PM_PLOTABUNDANCE_TYPE);
                if (str == null) {
                    prw.setParameterValue(Functions.PM_PLOTABUNDANCE_TYPE, hasAcousticDensity ? "Acoustic" : "SweptArea");
                }
            }
            prw = prj.getRModelReport().findProcessByFunction(Functions.FN_IMPUTEBYAGE);
            if (prw != null) {
                String str = (String) prw.getParameterValue(Functions.PM_IMPUTEBYAGE_TYPE);
                if (str == null) {
                    prw.setParameterValue(Functions.PM_IMPUTEBYAGE_TYPE, hasAcousticDensity ? "Acoustic" : "SweptArea");
                }
            }
        }
        if (prj.getResourceVersion() < 1.70) {
            // Set saveRImage parameters on report model.
            // Start using outputfolder and filenamebase
            // outputFolder="report", fileBaseName="impute.RData"
            IProcess prw = prj.getRModelReport().findProcessByFunction(Functions.FN_SAVERIMAGE);
            if (prw != null) {
                prw.setParameterValue(Functions.PM_SAVERIMAGE_OUTPUTFOLDER, "report");
                prw.setParameterValue(Functions.PM_SAVERIMAGE_FILEBASENAME, "impute.RData");
            }
        }*/
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
            for (String fn : new String[]{Functions.FN_BIOSTATIONASSIGNMENT, Functions.FN_RECTANGLEASSIGNMENT}) {
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
        /*if (prj.getResourceVersion() < 1.72) {
            // Resolve deprecation <-1.72v: swept area swept area sweep width method length dependent into catchability length dependent sweep width.
            //----------------------------------
            IProcess fbPr = prj.getBaseline().getProcessByFunctionName(Functions.FN_FILTERBIOTIC);
            IProcess cPr = prj.getBaseline().getProcessByFunctionName(Functions.FN_CATCHABILITY);
            IProcess totPr = prj.getBaseline().getProcessByFunctionName(Functions.FN_TOTALLENGTHDIST);
            IProcess swPr = prj.getBaseline().getProcessByFunctionName(Functions.FN_SWEPTAREADENSITY);
            if (swPr != null && totPr != null) {
                if (cPr == null) {
                    String sweepWidthMethod = (String) swPr.getParameterValue(Functions.PM_SWEPTAREADENSITY_SWEEPWIDTHMETHOD);
                    if (sweepWidthMethod != null && sweepWidthMethod.equals(Functions.SWEEPWIDTH_LENGTHDEPENDENT)) {
                        String lDistPName = (String) totPr.getProcessNameFromParameter(totPr.getMetaFunction().findMetaParameter(Functions.PM_TOTALLENGTHDIST_LENGTHDIST));
                        IProcess lDistPr = prj.getBaseline().getProcessFromName(lDistPName);
                        if (lDistPr != null) {
                            cPr = prj.getBaseline().insertProcess(Functions.FN_CATCHABILITY, Functions.FN_CATCHABILITY, prj.getBaseline().getProcessList().indexOf(lDistPr) + 1);
                            cPr.setParameterProcessValue(Functions.PM_CATCHABILITY_LENGTHDIST, lDistPr.getName()).
                                    setParameterValue(Functions.PM_CATCHABILITY_CATCHABILITYMETHOD, Functions.CATCHABILITYMETHOD_LENGTHDEPENDENTSWEEPWIDTH).
                                    setParameterValue(Functions.PM_CATCHABILITY_ALPHA, swPr.getParameterValue(Functions.PM_SWEPTAREADENSITY_ALPHA)).
                                    setParameterValue(Functions.PM_CATCHABILITY_BETA, swPr.getParameterValue(Functions.PM_SWEPTAREADENSITY_BETA)).
                                    setParameterValue(Functions.PM_CATCHABILITY_LMAX, swPr.getParameterValue(Functions.PM_SWEPTAREADENSITY_LMAX)).
                                    setParameterValue(Functions.PM_CATCHABILITY_LMIN, swPr.getParameterValue(Functions.PM_SWEPTAREADENSITY_LMIN));
                            swPr.setParameterValue(Functions.PM_SWEPTAREADENSITY_SWEEPWIDTHMETHOD, Functions.SWEEPWIDTH_PREDETERMINED);
                            totPr.setParameterProcessValue(Functions.PM_TOTALLENGTHDIST_LENGTHDIST, cPr.getName());
                        }
                    }
                }
                if (fbPr != null) {
                    String bioticData = (String) swPr.getParameterValue(Functions.PM_SWEPTAREADENSITY_BIOTICDATA);
                    if (bioticData == null) {
                        swPr.setParameterProcessValue(Functions.PM_SWEPTAREADENSITY_BIOTICDATA, fbPr.getName());
                    }
                }
            }
        }*/
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
        if (prw != null) {
            String pval = (String) prw.getParameterValue(param);
            if (pval != null) {
                pval = pval.replace(text, newText);
                prw.setParameterValue(param, pval);
            }
        }
    }
}
