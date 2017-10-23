/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.factory;

import java.util.List;
import no.imr.stox.functions.utils.ProjectUtils;
import no.imr.stox.library.IMetaFunction;

/**
 *
 * @author aasmunds
 */
public class TestFactory {

    //@Test
    public void test() {
        List<String> l = FactoryUtil.getTemplateProcessNamesByModel(Factory.TEMPLATE_SWEPTAREA, ProjectUtils.BASELINE);
        l.stream().forEach(System.out::println);
    }
}
