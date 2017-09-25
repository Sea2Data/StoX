/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.acoustic;

import no.imr.stox.functions.acoustic.FilterAcoustic;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.imr.stox.functions.utils.Functions;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.echosounderbo.FrequencyBO;
import no.imr.sea2data.echosounderbo.SABO;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author esmaelmh
 */
public class FilterAcousticTest {

    public FilterAcousticTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    /**
     * Test of perform method, of class FilterAcoustic.
     */
    @Test
    public void testPerform() throws Exception {
        System.out.println("perform");
        FilterAcoustic instance = new FilterAcoustic();

        List<DistanceBO> distances = new ArrayList<DistanceBO>();

        DistanceBO d = new DistanceBO();
        d.setLog_start(new BigDecimal(2276.0));
        distances.add(d);
        FrequencyBO frequencyBO = new FrequencyBO();
        frequencyBO.setFreq(38000);
        d.getFrequencies().add(frequencyBO);

        addSA(frequencyBO, "PLANK", 1, "B", 2.07937);
        addSA(frequencyBO, "SAITH", 1, "B", 0.207939);
        addSA(frequencyBO, "HADDO", 1, "B", 0.866414);
        addSA(frequencyBO, "COD", 1, "B", 0.519848);
        addSA(frequencyBO, "BOTT", 1, "B", 1.95975);
        addSA(frequencyBO, "HERR", 3, "P", 5.11842);

        Map<String, Object> input = new HashMap<String, Object>();
        input.put(Functions.PM_FILTERACOUSTIC_NASCEXPR, "(species = 'COD') or (acocat = 'BOT') or (acocat = 'HER')");
        input.put(Functions.PM_FILTERACOUSTIC_DISTANCEEXPR, "(log = 2276.0) or (log = 2277.0) or (log = 2278.0)");
        input.put(Functions.PM_FILTERACOUSTIC_FREQEXPR, "frequency = 38000");
        input.put(Functions.PM_FILTERACOUSTIC_ACOUSTICDATA, distances);

        List filtered = (List) instance.perform(input);

        assertTrue(!filtered.isEmpty());
    }

    private void addSA(FrequencyBO frequencyBO, String acocat, Integer ch, String chtype, Double sa) {
        SABO saBO = new SABO();
        saBO.setAcoustic_category(acocat);
        saBO.setCh(ch);
        saBO.setCh_type(chtype);
        saBO.setSa(sa);
        frequencyBO.getSa().add(saBO);
    }
}
