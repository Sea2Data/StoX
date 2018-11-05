/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package convert;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import no.imr.sea2data.biotic.bo.CatchSampleBO;
import no.imr.sea2data.biotic.bo.FilterField;
import no.imr.sea2data.biotic.bo.FishstationBO;
import no.imr.sea2data.biotic.bo.MissionBO;
import no.imr.stox.functions.biotic.BioticConverter;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
//@Igore
public class ConvertBiotic {

    @Test
    public void convert() {
        String fName = "C:\\Users\\aasmunds\\workspace\\stox\\project\\Barents Sea Beaked redfish and Sebastes sp in Subareas I and II bottom trawl index in winter_2009\\input\\biotic\\biotic_cruiseNumber_0104_2009_UANA_NANSE_Fridtjof+Nansen.xml";
        BioticConverter.convertBioticFileToV3(fName, "C:\\temp\\test.xml");
        //fName = "C:\\Users\\aasmunds\\workspace\\stox\\project\\ECA_sild_2015\\input\\biotic\\2015_biotic.xml";
        BioticConverter.convertBioticFileToV3(fName, "C:\\temp\\2015_biotic3.xml");
        BioticConverter.convertBioticFileToV3("C:\\temp\\4-2010-1173-4.xml", "C:\\temp\\4-2010-1173-4(v3).xml");
        BioticConverter.convertBioticFileToV3("C:\\temp\\4-2010-1173-4.xml", "C:\\temp\\4-2010-1173-4(v3).xml");
        BioticConverter.convertBioticFileToV1_4("C:\\temp\\4-2010-1173-4(v3).xml", "C:\\temp\\4-2010-1173-4(v1_4-2).xml");
        BioticConverter.convertBioticFileToV1_4("C:\\temp\\4-2010-1173-4.xml", "C:\\temp\\4-2010-1173-4(v1_4).xml");
    }

    @Test
    public void test() {
        Stream.of(/*MissionBO.class, */FishstationBO.class, CatchSampleBO.class).forEach(objClass -> {
                    try {
                        Method met = objClass.getMethod("bo");
                        Class boClass = met.getReturnType();
                        /*Arrays.stream(boClass.getMethods())
                        .filter(me -> me.getName().startsWith("set"))
                        .map(me -> me.getName().substring(3).toLowerCase())
                        .forEach(System.out::println);*/
                        Arrays.stream(objClass.getMethods())
                                .filter(me -> {
                                    return me.getAnnotation(FilterField.class) != null;
                                })
                                .map(me->me.getName())
                                .forEach(System.out::println);
                    } catch (NoSuchMethodException | SecurityException | IllegalArgumentException ex) {
                        Logger.getLogger(ConvertBiotic.class.getName()).log(Level.SEVERE, null, ex);
                    }
                });
    }
}
