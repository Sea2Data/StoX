/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package test;
import java.math.BigDecimal;
import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
public class TestMath {
    //@Test 
    public void TestMath() {
           BigDecimal bd = new BigDecimal(34000000000.0);
           System.out.println(bd.precision() - bd.scale());
           bd = new BigDecimal(.00000000034);
           System.out.println(bd.precision() - bd.scale());
           bd = new BigDecimal(530000.00223232323233);
           System.out.println(bd.precision() - bd.scale());
    }
}
