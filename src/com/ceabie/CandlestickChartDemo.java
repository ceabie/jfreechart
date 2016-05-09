package com.ceabie;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.io.CSV;
import org.jfree.data.time.Day;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.OHLCDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import java.awt.*;
import java.io.FileReader;
import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2016/5/9.
 */
public class CandlestickChartDemo extends ApplicationFrame {

    static {
        // set a theme using the new shadow generator feature available in
        // 1.0.14 - for backwards compatibility it is not enabled by default
        ChartFactory.setChartTheme(new StandardChartTheme("JFree/Shadow",
                true));
    }

    private static JFreeChart createChart(OHLCDataset dataset) {

        JFreeChart chart = ChartFactory.createCandlestickChart(
                "Legal & General Unit Trust Prices",  // title
                "Date",             // x-axis label
                "Price Per Unit",   // y-axis label
                dataset,           // data
                false
        );

        chart.setBackgroundPaint(Color.white);

        XYPlot plot = (XYPlot) chart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));
        plot.setDomainCrosshairVisible(false);
        plot.setRangeCrosshairVisible(true);

        plot.setDomainPannable(true);

//        plot.setRangeCrosshairLockedOnData(false);
        plot.setDomainCrosshairLockedOnData(false);

        XYItemRenderer r = plot.getRenderer();
        if (r instanceof CandlestickRenderer) {
            CandlestickRenderer renderer = (CandlestickRenderer) r;
            renderer.setBaseToolTipGenerator(CandlestickToolTipGenerator.getSeriesInstance());
        }

        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("MM-yyyy"));


        //Add the otherDataSet to the plot and map it to the same axis at the original plot
//        int index = 1;
//        plot.setDataset(index, otherDataSet);
//        plot.mapDatasetToRangeAxis(index, 0);
//
//        XYItemRenderer renderer2 = new XYLineAndShapeRenderer();
//        plot.setRenderer(1, renderer2);
//        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

        return chart;

    }


    private static OHLCDataset createDataset() {
        OHLCSeriesCollection dataset = new OHLCSeriesCollection();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");

        CSV csv = new CSV();
        try {
            FileReader fileReader = new FileReader("000001.csv");

            CategoryDataset categoryDataset = csv.readCategoryDataset(fileReader);

            int rowCount = categoryDataset.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                Comparable rowKey = categoryDataset.getRowKey(i);
                OHLCSeries s1 = new OHLCSeries(rowKey);

                s1.add(new Day(simpleDateFormat.parse(rowKey.toString())),
                        categoryDataset.getValue(i, 3).doubleValue(),
                        categoryDataset.getValue(i, 1).doubleValue(),
                        categoryDataset.getValue(i, 2).doubleValue(),
                        categoryDataset.getValue(i, 0).doubleValue());

                dataset.addSeries(s1);
            }
            fileReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return dataset;
    }

    public static JPanel createDemoPanel() {
        JFreeChart chart = createChart(createDataset());
        ChartPanel panel = new ChartPanel(chart);
//        panel.setFillZoomRectangle(true);
        panel.setMouseWheelEnabled(true);
//        panel.setVerticalAxisTrace(true);
//        panel.setHorizontalAxisTrace(true);
        return panel;
    }


    public CandlestickChartDemo(String title) {
        super(title);
        ChartPanel chartPanel = (ChartPanel) createDemoPanel();
        chartPanel.setPreferredSize(new java.awt.Dimension(800, 500));
//        chartPanel.setDomainZoomable(false);
        chartPanel.setRangeZoomable(false);
        setContentPane(chartPanel);
    }

    public static void main(String[] args) {

        CandlestickChartDemo demo = new CandlestickChartDemo(
                "Time Series Chart Demo 1");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);

    }
}
