/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.project;

import no.imr.stox.factory.FactoryUtil;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import no.imr.sea2data.biotic.bo.FishstationBO;
import static no.imr.stox.factory.FactoryUtil.acquireProject;
import no.imr.stox.bo.PSUAssignmentBO;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;
import no.imr.stox.functions.utils.ProjectUtils;
import no.imr.stox.model.IModel;
import no.imr.stox.model.IProcess;
import no.imr.stox.model.IProject;
import no.imr.stox.model.ModelListenerAdapter;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author esmaelmh
 */
@Ignore
public class ProjectTest {

    /**
     * Test of bootstrap method, of class FactoryUtil.
     *
     * @throws org.apache.commons.configuration.ConfigurationException
     * @throws java.io.IOException
     */
    @Test
    public void test() throws IOException {
        //FactoryUtil.extractTestModel();
//        run(false,  "Swept area torsk vinter 2015", true, false);
        Functions.XMLDATA = true;
        IProject pr = acquireProject(ProjectUtils.getSystemProjectRoot(), "Tobis-2013842-test", null);
        IProcess p = pr.getBaseline().getProcessByFunctionName(Functions.FN_READBIOTICXML);
        p.setFileOutput(Boolean.TRUE);
        pr.getBaseline().run(1, pr.getBaseline().getProcessList().indexOf(p) + 1, Boolean.FALSE);
        
        //run(false,  "Tobis-2013842-test", true, false);

        //run(false, "Tobis_main_area_2014807", true, false);
        //run(false, "SildGytetokt2015_CorrIngerH _Correct", true, false);
        //run(false, "DATRAS_IBTS", true, false);
        // run(false, "IESSNS2015", true, false);
    }
        /**
     * Create and run a model
     *
     * @param projectName - workspace folder relative to c:/users/[user], i.e.
     * stox/process
     * @param inBackground
     * @param runBaseline
     * @param runRModel
     */
    void run(Boolean inBackground, String projectName, Boolean runBaseline, Boolean runRModel) {
        /*if (projectName == null) {
         projectName = TESTPROJECT;
         }*/
        IProject pr = acquireProject(ProjectUtils.getSystemProjectRoot(), projectName, null);
        if (runBaseline) {
            for (IModel model : new IModel[]{pr.getBaseline(), pr.getBaselineReport()}) {
                model.setBreakable(false);
                //model.setExportCSV(false);
                model.addModellistener(new TestFuncListener());
                model.run(1, model.getProcessList().size(), inBackground);
                if (inBackground) {
                    waitForModelToFinish(model);
                }
            }
        }
        if (runRModel) {
            for (IModel model : new IModel[]{pr.getRModel(), pr.getRModelReport()}) {
                model.run(null, null, inBackground);
                if (inBackground) {
                    waitForModelToFinish(model);
                }
            }
        }
        pr.save();
        //model.save();
    }

    /**
     * Wait in a console main thread for the background process to finish.
     *
     * @param model
     */
    public void waitForModelToFinish(IModel model) {
        model.setRunState(IModel.RUNSTATE_RUNNING);
        while (model.getRunState() != IModel.RUNSTATE_STOPPED) {
            try {
                // wait for the model to finish thread
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(FactoryUtil.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }


    /**
     * Listener class to test listening on functions is o.k.
     */
    class TestFuncListener extends ModelListenerAdapter {

        @Override
        public void onProcessEnd(IProcess process) {
            if (process.getMetaFunction().getName().equals(Functions.FN_FILTERBIOTIC)) {
                List<FishstationBO> fStations = process.getOutput();
                if (fStations != null && fStations.size() > 0) {
                    System.out.println("Fish stations ok in listener");
                }
            } else if (process.getMetaFunction().getName().equals(Functions.FN_READPROCESSDATA)) {
                ProcessDataBO pd = process.getOutput();
                if (pd != null) {
                    final List<PSUAssignmentBO> assignments = AbndEstProcessDataUtil.getTransectAssignments(pd);
                    System.out.println("PSU Assignment ok in listener:" + assignments.size());
                }
            }
        }

        @Override
        public void onProcessLog(IModel model, String msg) {
            System.out.println(msg);
        }

    }


}
