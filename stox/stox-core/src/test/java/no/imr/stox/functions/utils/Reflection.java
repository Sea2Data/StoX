/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.utils;

import BioticTypes.v3.CatchsampleType;
import BioticTypes.v3.FishstationType;
import LandingsTypes.v2.ArtType;
import LandingsTypes.v2.DellandingType;
import LandingsTypes.v2.FangstdataType;
import LandingsTypes.v2.FartøyType;
import LandingsTypes.v2.FiskerType;
import LandingsTypes.v2.KvoteType;
import LandingsTypes.v2.LandingOgProduksjonType;
import LandingsTypes.v2.MottakendeFartøyType;
import LandingsTypes.v2.MottakerType;
import LandingsTypes.v2.ProduktType;
import LandingsTypes.v2.RedskapType;
import LandingsTypes.v2.SalgslagdataType;
import LandingsTypes.v2.SeddellinjeType;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.annotation.XmlElement;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
public class Reflection {

    @Test
    public void test() {
        List<Field> fields = ReflectionUtil.getCompoundFields(SeddellinjeType.class);

        SeddellinjeType sl = new SeddellinjeType();
        sl.setArtKode("a");
        Field f1 = fields.stream().filter(f->f.getName().equalsIgnoreCase("artkode")).findAny().orElse(null);
        Assert.assertEquals(ReflectionUtil.invoke(f1, sl), "a");
        
        ArtType a = new ArtType();
        sl.setArt(a);
        a.setArtBokmål("b");
        Field f2 = fields.stream().filter(f->f.getName().equalsIgnoreCase("artbokmål")).findAny().orElse(null);
        Assert.assertEquals(ReflectionUtil.invoke(f2, sl), "b");
        

        
        FishstationType fs = new FishstationType();
        fs.setNation("no");
        fields = ReflectionUtil.getFields(CatchsampleType.class);
        fields = ReflectionUtil.getFields(FishstationType.class);
        fields.forEach(f -> {
            Method getter = ReflectionUtil.getGetter(f);
            System.out.println(ReflectionUtil.invoke(getter, fs));
        });
    }
}
