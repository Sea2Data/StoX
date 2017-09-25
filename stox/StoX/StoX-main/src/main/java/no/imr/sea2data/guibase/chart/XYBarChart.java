/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package no.imr.sea2data.guibase.chart;

import java.awt.Color;
import java.awt.Font;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.RectangleEdge;

/**
 *
 * @author aasmunds
 */
public class XYBarChart implements IXYBarChart {

    private final ChartPanel chartPanel;
    private final XYSeriesCollection dataset;
    XYPlot plot;
    TextTitle trendLegend = new TextTitle("");
    TextTitle trendLegend2 = new TextTitle("");
    double intv = 0;

    public XYBarChart() {
        dataset = new XYSeriesCollection(new XYSeries("Series 1"));
        JFreeChart chart = ChartFactory.createXYBarChart("", "X axis", false, "Y axis", dataset,
                PlotOrientation.VERTICAL, false, true, false);
        chart.setBackgroundPaint(Color.white);
        chart.getTitle().setPaint(Color.black);
        chart.getTitle().setFont(new Font("SansSerif", Font.PLAIN, 12));
        chart.addSubtitle(trendLegend);
        chart.addSubtitle(trendLegend2);
        trendLegend.setPosition(RectangleEdge.TOP);
        trendLegend2.setPosition(RectangleEdge.TOP);
        plot = (XYPlot) chart.getPlot();
        XYBarRenderer renderer = (XYBarRenderer) plot.getRenderer();
        renderer.setShadowVisible(false);
        chartPanel = new ChartPanel(chart);
        plot.getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
    }

    @Override
    public ChartPanel getPanel() {
        return chartPanel;
    }

    @Override
    public void clear() {
        dataset.getSeries(0).clear();
    }

    @Override
    public void repaint() {
        chartPanel.repaint();
    }

    @Override
    public void setXAxisInterval(double intv) {
        this.intv = intv;
        dataset.setIntervalWidth(intv * 0.95); // make bar gap
    }

    @Override
    public void setTrend1(String trend) {
        trendLegend.setText(trend);
    }

    @Override
    public void setTrend2(String trend) {
        trendLegend2.setText(trend);
    }

    @Override
    public void setXY(double x, double y) {
        dataset.getSeries(0).add(x, y);
    }

    @Override
    public void updateXAxisRange(int numSnapIntervals) {
        double maxv = dataset.getSeries(0).getMaxX();
        double snapSize = numSnapIntervals * intv;
        double upper = ((int) (maxv / snapSize) + 1) * snapSize;
        plot.getDomainAxis().setRange(0, upper);
    }

    @Override
    public void setYAxisLabel(String label) {
        plot.getRangeAxis().setLabel(label);
    }

    @Override
    public void setXAxisLabel(String label) {
        plot.getDomainAxis().setLabel(label);
    }
    @Override
    public void setSeriesName(String name) {
        dataset.getSeries(0).setKey(name);
    }
}
