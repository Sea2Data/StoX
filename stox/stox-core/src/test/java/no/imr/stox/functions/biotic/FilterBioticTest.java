package no.imr.stox.functions.biotic;

import BioticTypes.v3.MissionType;
import no.imr.stox.functions.biotic.FilterBiotic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.imr.stox.functions.utils.Functions;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.biotic.bo.CatchSampleBO;
import no.imr.sea2data.biotic.bo.MissionBO;
import no.imr.stox.bo.BioticData;
import static org.junit.Assert.*;
import org.junit.Test;

/**
 *
 * @author esmaelmh
 */
//@Ignore
public class FilterBioticTest {

    /*@Test
    public void test() {
        JexlEngine engine = new JexlEngine();
        engine.setLenient(false);
        engine.setSilent(false);
        Expression exp = engine.createExpression("a > 4");
        JexlContext ctx = new MapContext();
        //ctx.set("a", 3);
        try {
            System.out.println(exp.evaluate(ctx));
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }*/

    /**
     * Test of perform method, of class FilterBiotic.
     *
     * @throws org.apache.commons.configuration.ConfigurationException
     * @throws no.imr.stox.exception.UserErrorException
     */
    @Test
    public void testPerform() {
        System.out.println("perform");
        FilterBiotic instance = new FilterBiotic();
//        instance.perform();
        // TODO review the generated test code and remove the default call to fail.
//        fail("The test case is a prototype.");
        MissionBO ms = new MissionBO();
        FishstationBO st1 = ms.addFishstation();
        st1.bo().setGearcondition("1");
        st1.bo().setSamplequality("1");
        st1.bo().setStationtype("4");
        st1.bo().setFishingdepthmax(200.0);
        CatchSampleBO sample11 = st1.addCatchSample();
        sample11.bo().setCatchcategory("SILD");
        sample11.bo().setGroup("1");
        sample11.bo().setSampletype("5");
        IndividualBO individ11 = sample11.addIndividual();
        IndividualBO individ12 = sample11.addIndividual();
        IndividualBO individ13 = sample11.addIndividual();
        individ11.setLengthCentimeter(2d);
        individ12.setLengthCentimeter(3d);
        individ13.setLengthCentimeter(5d);

        FishstationBO st2 = ms.addFishstation();
        st2.bo().setGearcondition("4");
        st2.bo().setSamplequality("5");
        st2.bo().setStationtype("2");
        st2.bo().setFishingdepthmax(400.0);
        CatchSampleBO sample12 = st2.addCatchSample();
        sample12.bo().setCatchcategory("HYSE");
        sample12.bo().setGroup("2");
        sample12.bo().setSampletype("8");
        IndividualBO individ21 = sample12.addIndividual();
        IndividualBO individ22 = sample12.addIndividual();
        IndividualBO individ23 = sample12.addIndividual();
        individ21.setLengthCentimeter(1d);
        individ22.setLengthCentimeter(3d);
        individ23.setLengthCentimeter(6d);

        BioticData missions = new BioticData();
        missions.getMissions().add(ms);
        String stationExpr = "gearcondition eq 1 and trawlquality eq 1";
        String catchExpr = "species eq 'SILD'";
        String sampleExpr = "group eq '1' and sampletype eq 5";
        String individualExpr = "length > 3";

        Map<String, Object> input = new HashMap<>();
        input.put(Functions.PM_FILTERBIOTIC_FISHSTATIONEXPR, stationExpr);
        input.put(Functions.PM_FILTERBIOTIC_CATCHEXPR, catchExpr);
        input.put(Functions.PM_FILTERBIOTIC_SAMPLEEXPR, sampleExpr);
        input.put(Functions.PM_FILTERBIOTIC_INDEXPR, individualExpr);
        input.put(Functions.PM_FILTERBIOTIC_BIOTICDATA, missions);

        BioticData l = (BioticData) instance.perform(input);

        assertTrue(!l.getMissions().isEmpty());
    }
}
