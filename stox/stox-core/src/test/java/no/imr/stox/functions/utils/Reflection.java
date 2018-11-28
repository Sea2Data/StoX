/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.utils;

import BioticTypes.v3.CatchsampleType;
import BioticTypes.v3.FishstationType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
public class Reflection {

    @Test
    public void test() {
        FishstationType fs = new FishstationType();
        fs.setNation("no");
        List<Field> fields = ReflectionUtil.getFields(CatchsampleType.class);
        fields = ReflectionUtil.getFields(FishstationType.class);
        fields.forEach(f -> {
            Method getter = ReflectionUtil.getGetter(f);
            System.out.println(ReflectionUtil.invoke(getter, fs));
        });
    }
}
