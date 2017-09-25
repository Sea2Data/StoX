/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.stox.functions.processdata;

import java.util.Map;
import no.imr.stox.bo.ProcessDataBO;
import no.imr.stox.functions.AbstractFunction;
import no.imr.stox.functions.utils.AbndEstProcessDataUtil;
import no.imr.stox.functions.utils.Functions;

/**
 * TODO: what does this class do?
 *
 * @author aasmunds
 */
public class RectangleArea extends AbstractFunction {

    /**
     *
     * @param input contains Polygon file name
     * @return Matrix object of type POLYGONAREA_MATRIX - see
     * DataTypeDescription.txt
     */
    @Override
    public Object perform(Map<String, Object> input) {
        ProcessDataBO pd = (ProcessDataBO) input.get(Functions.PM_RECTANGLEAREA_PROCESSDATA);
        return AbndEstProcessDataUtil.getPolygonArea(AbndEstProcessDataUtil.getRectanglePolygons(pd));
    }
}
