/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package temp;

import java.math.BigDecimal;
import java.math.RoundingMode;
import no.imr.sea2data.imrbase.util.Conversion;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
@Ignore
public class Temp {

    @Test
    public void test() {
        BigDecimal val = new BigDecimal(Conversion.safeStringtoDouble("100"));
        val = val.setScale(1, RoundingMode.HALF_UP);
        System.out.println("=" + val);
    }
}
