/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.acoustic;

import no.imr.stox.functions.acoustic.NASC;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import no.imr.sea2data.echosounderbo.DistanceBO;
import no.imr.sea2data.echosounderbo.FrequencyBO;
import no.imr.sea2data.echosounderbo.SABO;
import no.imr.stox.util.base.IMRdate;
import no.imr.stox.bo.NASCMatrix;
import no.imr.stox.functions.utils.Functions;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
public class NASCTest {

    @Test
    public void test() {
        assertEquals(perform(Functions.LAYERTYPE_PCHANNEL).getData().getGroupRowColValueAsDouble("12", "2013101/100.0/2013-01-01/12:00:00", "20"), 7.0, 0);
        // Here we miss the estimation layer aggregations, ask Atle
        assertEquals(perform(Functions.LAYERTYPE_DEPTHLAYER).getData().getGroupRowColValueAsDouble("12", "2013101/100.0/2013-01-01/12:00:00", "PEL"), 3.0, 0);
    }

    NASCMatrix perform(String layerType) {
        return (NASCMatrix) (new NASC()).perform(getInput(layerType));
    }

    private Map<String, Object> getInput(String layerType) {
        Map<String, Object> input = new HashMap<>();
        input.put(Functions.PM_SUMNASC_ACOUSTICDATA, getEchoDS());
        input.put(Functions.PM_SUMNASC_LAYERTYPE, layerType);
        return input;
    }

    List<DistanceBO> getEchoDS() {
        List<DistanceBO> distances = new ArrayList<>();
        String cruise = "2013101";
        DistanceBO d = new DistanceBO();
        d.setCruise(cruise);
        d.setLog_start(100d);
        d.setStart_time(IMRdate.encodeDate(2013, 1, 1, 12, 0, 0, true));
        distances.add(d);
        FrequencyBO f = new FrequencyBO();
        f.setNum_pel_ch(20);
        f.setNum_bot_ch(1);
        d.getFrequencies().add(f);
        // Setup channels PB (P in data)
        SABO sabo = new SABO();
        sabo.setAcoustic_category(12 + "");
        sabo.setCh_type("P");
        sabo.setCh(10);
        sabo.setSa(5.0);
        f.getSa().add(sabo);
        sabo = new SABO();
        sabo.setAcoustic_category(12 + "");
        sabo.setCh_type("P");
        sabo.setCh(20);
        sabo.setSa(7.0);
        f.getSa().add(sabo);
        // Setup channels B
        sabo = new SABO();
        sabo.setAcoustic_category(12 + "");
        sabo.setCh_type("B");
        sabo.setCh(1);
        sabo.setSa(9.0);
        f.getSa().add(sabo);
        // From this channel layer P is given by PB - P = (5+7)-9 = 3
        return distances;
    }

}
