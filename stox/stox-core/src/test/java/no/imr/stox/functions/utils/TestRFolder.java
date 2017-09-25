/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.utils;

import org.junit.Ignore;
import org.junit.Test;

/**
 *
 * @author aasmunds
 */
@Ignore
public class TestRFolder {

    @Test
    public void test() {
        RFolder.writeRFolderToFile("test");
        System.out.println(RFolder.getRFolderFromFile());
    }
}
