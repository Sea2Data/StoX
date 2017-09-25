/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package temp;

import java.util.List;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.stox.factory.Factory;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.model.IProcess;
import no.imr.stox.model.IProject;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
@Ignore
public class TestModel {

    @Test
    public void testModel() {
        IProject p = Factory.getTemplateProject(Factory.TEMPLATE_STATIONLENGTHDIST);
        
        // Modify parameters
        // Set filename
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
