/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.sea2data.guibase.chart;

import org.jfree.chart.ChartPanel;

/**
 * XY Bar chart interface for a xy plot with bar support on a scaled x axis.
 * @author aasmunds
 */
public interface IXYBarChart {

    ChartPanel getPanel();

    void setXY(double x, double y);

    void setXAxisInterval(double intv);

    void clear();

    void repaint();

    void setTrend1(String trend);

    void setTrend2(String trend);

    /**
     * Used to stabilise the axis range into 'snap ranges'. Useful when comparing succeeding plots 
     * @param numSnapIntervals 
     */
    void updateXAxisRange(int numSnapIntervals);

    void setYAxisLabel(String label);

    void setXAxisLabel(String label);

    void setSeriesName(String name);
}
