/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package convert;

import no.imr.stox.functions.biotic.BioticConverter;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
public class ConvertBiotic {

    @Test
    public void convert() {
        BioticConverter.convertBioticFileToV1_4("C:\\temp\\4-2010-1173-4.xml", "C:\\temp\\4-2010-1173-4(v3).xml");
    }
}
