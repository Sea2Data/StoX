/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package temp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.stox.factory.Factory;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.ProjectUtils;
import no.imr.stox.model.IProcess;
import no.imr.stox.model.IProject;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
//@Ignore
public class TestModel {

    @Test
    public void templatesToR() {
        String[] newmodelNames = new String[]{
            "Baseline",
            "Statistics",
            "Report"};
        String[] modelNames = new String[]{
            ProjectUtils.BASELINE,
            ProjectUtils.R,
            ProjectUtils.BASELINE_REPORT,
            ProjectUtils.R_REPORT};

        System.out.print("stoxTemplates <- list(");
        String str2
                = Factory.getAvailableTemplates().stream().map(a -> {
                    String as = a;
                    if (!as.endsWith("Template")) {
                        as += "Template";
                    }
                    String res = "\n    #### " + as + ": " + Factory.getTemplateDescription(a)
                            + "\n    " + as + " = list("
                            + "\n        description = \"" + Factory.getTemplateDescription(a) + "\"";
                    IProject p = Factory.getTemplateProject(a);
                    String str1 = Arrays.stream(newmodelNames).map(m -> {
                        List<IProcess> procList = null;
                        switch (m) {
                            case "Baseline":
                                procList = p.getModel(m.toLowerCase()).getProcessList();
                                break;
                            case "Statistics":
                                procList = p.getModel(ProjectUtils.R).getProcessList();
                                break;
                            case "Report": {
                                procList = new ArrayList(p.getModel(ProjectUtils.BASELINE_REPORT).getProcessList());
                                procList.addAll(p.getModel(ProjectUtils.R_REPORT).getProcessList());
                                break;
                            }
                        }
                        String str = procList.stream().map(pr -> {
                            String proParams = "\n                ProcessParameters = list("
                                    + "\n                    FileOutput = " + (pr.getFileOutput() + "").toUpperCase() + ","
                                    + "\n                    BreakInGUI = " + (pr.isBreakInGUI() + "").toUpperCase()
                                    + "\n                )";
                            String funInputs = pr.getMetaFunction().getMetaParameters().stream()
                                    .filter(mp -> mp.getMetaDataType().isReference() && pr.getProcessNameFromParameter(mp) != null)
                                    .map(mp -> "\n                    " + mp.getName() + " = \"" + pr.getProcessNameFromParameter(mp) + "\"")
                                    .collect(Collectors.joining(","));
                            if (!funInputs.isEmpty()) {
                                funInputs = "\n                FunctionInputs = list(" + funInputs + "\n                )";
                            }
                            String funParams = pr.getMetaFunction().getMetaParameters().stream()
                                    .filter(mp -> !mp.getMetaDataType().isReference() && pr.getParameterValue(mp.getName()) != null)
                                    .map(mp -> {

                                        String val = Objects.toString(pr.getParameterValue(mp.getName()));
                                        if (mp.getDataTypeName().equalsIgnoreCase("string")) {
                                            val = "\"" + val + "\"";
                                        } else if (mp.getDataTypeName().equalsIgnoreCase("boolean")){
                                            val = val.toUpperCase(); // true->TRUE
                                        }
                                        return "\n                    " + mp.getName() + " = " + val;
                                    })
                                    .collect(Collectors.joining(","));
                            if (!funParams.isEmpty()) {
                                funParams = "\n                FunctionParameters = list("
                                        + funParams
                                        + "\n                )";
                            }
                            String params = Stream.of(proParams, funInputs, funParams).filter(s -> !s.isEmpty()).collect(Collectors.joining(","));
                            String s
                                    = "\n            " + pr.getName() + " = list("
                                    + "\n                ProcessName = \"" + pr.getName() + "\","
                                    + "\n                FunctionName = \"" + pr.getMetaFunction().getName() + "\","
                                    + params
                                    + "\n            )";
                            return s;
                        }).collect(Collectors.joining(","));
                        if (!str.isEmpty()) {
                            str = "\n        " + m + " = list(" + str + "\n        )";
                        }
                        return str;
                    }).filter(s -> !s.isEmpty())
                            .collect(Collectors.joining(","));
                    if(!str1.isEmpty()){
                        str1 = "," + str1;
                    }
                    return res + str1 + "\n    )";
                }).collect(Collectors.joining(","));
        System.out.println(str2);
        System.out.println(")");
    }

    public void testModel() {
        // Modify parameters
        // Set filename
        IProject p = Factory.getTemplateProject(Factory.TEMPLATE_STATIONLENGTHDIST);
        String fileName = "C:/Users/aasmunds/workspace/stox/project/Tobis-2013842-test/input/biotic/4-2013-3317-1.xml";
        IProcess readFile = p.getBaseline().getProcessByFunctionName(Functions.FN_READBIOTICXML);
        readFile.setParameterValue(Functions.PM_READBIOTICXML_FILENAME + 1, fileName);
        // Set species
        IProcess filterBiotic = p.getBaseline().getProcessByFunctionName(Functions.FN_FILTERBIOTIC);
        filterBiotic.setParameterValue(Functions.PM_FILTERBIOTIC_CATCHEXPR, "species eq 'HAVSIL'");

        // Set lengthdist type
        IProcess stationLengthDist = p.getBaseline().getProcessByFunctionName(Functions.FN_STATIONLENGTHDIST);
        stationLengthDist.setParameterValue(Functions.PM_STATIONLENGTHDIST_LENGTHDISTTYPE, Functions.LENGTHDISTTYPE_NORMLENGHTDIST);

        // Set length group
        IProcess regroupLengthDist = p.getBaseline().getProcessByFunctionName(Functions.FN_REGROUPLENGTHDIST);
        regroupLengthDist.setParameterValue(Functions.PM_REGROUPLENGTHDIST_LENGTHINTERVAL, 1.0);

        p.getBaseline().run(1, p.getBaseline().getProcessList().size(), false);
        List<FishstationBO> fl = readFile.getOutput();
        System.out.println("" + regroupLengthDist.getOutput());

        System.out.println("Fishstation count: " + fl.size());
    }
}
