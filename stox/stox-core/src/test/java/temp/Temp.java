/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package temp;

import BioticTypes.v3.FishstationType;
import BioticTypes.v3.MissionType;
import LandingsTypes.v2.SeddellinjeType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import no.imr.sea2data.biotic.bo.BaseBO;
import no.imr.sea2data.imrbase.util.Conversion;
import no.imr.sea2data.imrbase.util.ExportUtil;
import no.imr.sea2data.imrbase.util.ImrIO;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
//@Ignore
public class Temp {

    @Test
    public void test() {
        System.out.println(ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(BaseBO.csvHdr(SeddellinjeType.class, true, false))));        
        System.out.println(ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(BaseBO.csvHdr(SeddellinjeType.class, true, true))));        
        System.out.println(ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(BaseBO.csvHdr(FishstationType.class, true, false))));        
        System.out.println(ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(BaseBO.csvHdr(FishstationType.class, true, true))));        
        System.out.println(ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(BaseBO.csvHdr(MissionType.class, null, true), BaseBO.csvHdr(FishstationType.class, null, null))));        
        System.out.println(ExportUtil.carrageReturnLineFeed(ExportUtil.tabbed(
                BaseBO.csvHdr(MissionType.class, null, true), 
                BaseBO.csvHdr(FishstationType.class, null, null)
        )));        
        /*BigDecimal val = new BigDecimal(Conversion.safeStringtoDouble("100"));
        val = val.setScale(1, RoundingMode.HALF_UP);
        System.out.println("=" + val);*/
    }
}
