/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.library;

import no.imr.stox.functions.utils.ProjectUtils;
import org.junit.Test;

/**
 * /**
 *
 * @author aasmunds
 */
public class TestLibrary {

    @Test
    public void test() {
        Library lbr = new Library();
        lbr.readFromResource(ProjectUtils.R);
    }
}
