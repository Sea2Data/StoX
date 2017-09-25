package no.imr.stox.functions.biotic;

import no.imr.stox.functions.biotic.FilterBiotic;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.imr.stox.functions.utils.Functions;
import no.imr.sea2data.biotic.bo.CatchBO;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.IndividualBO;
import no.imr.sea2data.biotic.bo.SampleBO;
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

        FishstationBO st1 = new FishstationBO();
        st1.setGearCondition("1");
        st1.setTrawlQuality("1");
        st1.setStationType("4");
        st1.setFishingDepthMax(200.0);
        CatchBO c11 = new CatchBO();
        c11.setTaxa("SILD");
        st1.getCatchBOCollection().add(c11);
        SampleBO sample11 = new SampleBO();
        sample11.setGroup("1");
        sample11.setSampletype(5);
        c11.getSampleBOCollection().add(sample11);
        IndividualBO individ11 = new IndividualBO();
        IndividualBO individ12 = new IndividualBO();
        IndividualBO individ13 = new IndividualBO();
        individ11.setLength(2.0);
        individ12.setLength(3.0);
        individ13.setLength(5.0);
        sample11.getIndividualBOCollection().add(individ11);
        sample11.getIndividualBOCollection().add(individ12);
        sample11.getIndividualBOCollection().add(individ13);

        FishstationBO st2 = new FishstationBO();
        st2.setGearCondition("4");
        st2.setTrawlQuality("5");
        st2.setStationType("2");
        st2.setFishingDepthMax(400.0);
        CatchBO c21 = new CatchBO();
        c21.setTaxa("HYSE");
        st2.getCatchBOCollection().add(c21);
        SampleBO sample12 = new SampleBO();
        sample12.setGroup("2");
        sample12.setSampletype(8);
        c21.getSampleBOCollection().add(sample12);
        IndividualBO individ21 = new IndividualBO();
        IndividualBO individ22 = new IndividualBO();
        IndividualBO individ23 = new IndividualBO();
        individ21.setLength(1.0);
        individ22.setLength(3.0);
        individ23.setLength(6.0);
        sample12.getIndividualBOCollection().add(individ21);
        sample12.getIndividualBOCollection().add(individ22);
        sample12.getIndividualBOCollection().add(individ23);

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
