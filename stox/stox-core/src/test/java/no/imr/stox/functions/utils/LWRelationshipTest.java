/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.utils;

import no.imr.sea2data.imrbase.math.LWRelationship;
import org.junit.Test;
import static org.junit.Assert.assertEquals;

/**
 *
 * @author Åsmund
 */
public class LWRelationshipTest {

    @Test
    public void test() {
        Double[] len = new Double[]{8.1, 9.1, 10.2, 11.9, 12.2, 13.8, 14.8, 15.7, 16.6, 17.7, 18.7, 19.0, 20.6, 21.9, 22.9, 23.5, null};
        Double[] wgt = new Double[]{6.3, 9.6, 11.6, 18.5, 26.2, 36.1, 40.1, 47.3, 65.6, 69.4, 76.4, 82.5, 106.6, 119.8, 169.2, 173.3, null};
        LWRelationship lwr = LWRelationship.getLWRelationship(len, wgt);
        Double w = lwr.getWeight(15.0);
        assertEquals(w, 42.3, 0.1);
        //f(x)=0.0106962726 x^3.0573499301
    }
}
