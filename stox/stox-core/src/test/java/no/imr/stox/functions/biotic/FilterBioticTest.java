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
        MissionType mt = new MissionType();
        FishstationBO st1 = new FishstationBO(mt);
        st1.getFs().setGearcondition("1");
        st1.getFs().setSamplequality("1");
        st1.getFs().setStationtype("4");
        st1.getFs().setFishingdepthmax(200.0);
        CatchSampleBO sample11 = st1.addCatchSample(null);
        sample11.getCs().setCatchcategory("SILD");
        sample11.getCs().setGroup("1");
        sample11.getCs().setSampletype("5");
        IndividualBO individ11 = sample11.addIndividual(null);
        IndividualBO individ12 = sample11.addIndividual(null);
        IndividualBO individ13 = sample11.addIndividual(null);
        individ11.setLength(0.02);
        individ12.setLength(0.03);
        individ13.setLength(0.05);

        FishstationBO st2 = new FishstationBO(mt);
        st2.getFs().setGearcondition("4");
        st2.getFs().setSamplequality("5");
        st2.getFs().setStationtype("2");
        st2.getFs().setFishingdepthmax(400.0);
        CatchSampleBO sample12 = st2.addCatchSample(null);
        sample12.getCs().setCatchcategory("HYSE");
        sample12.getCs().setGroup("2");
        sample12.getCs().setSampletype("8");
        IndividualBO individ21 = sample12.addIndividual(null);
        IndividualBO individ22 = sample12.addIndividual(null);
        IndividualBO individ23 = sample12.addIndividual(null);
        individ21.setLength(0.01);
        individ22.setLength(0.03);
        individ23.setLength(0.06);

        List<FishstationBO> fishstations = new ArrayList<>(Arrays.asList(st1, st2));

        String stationExpr = "gearcondition eq 1 and trawlquality eq 1";
        String catchExpr = "species eq 'SILD'";
        String sampleExpr = "group eq '1' and sampletype eq 5";
        String individualExpr = "length > 3";

        Map<String, Object> input = new HashMap<>();
        input.put(Functions.PM_FILTERBIOTIC_FISHSTATIONEXPR, stationExpr);
        input.put(Functions.PM_FILTERBIOTIC_CATCHEXPR, catchExpr);
        input.put(Functions.PM_FILTERBIOTIC_SAMPLEEXPR, sampleExpr);
        input.put(Functions.PM_FILTERBIOTIC_INDEXPR, individualExpr);
        input.put(Functions.PM_FILTERBIOTIC_BIOTICDATA, fishstations);

        List l = (List) instance.perform(input);

        assertTrue(!l.isEmpty());
    }
}
