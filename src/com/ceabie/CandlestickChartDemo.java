package com.ceabie;

import bolts.Task;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.CandlestickRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.io.CSV;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.FileReader;
import java.text.SimpleDateFormat;

/**
 * Created by Administrator on 2016/5/9.
 */
public class CandlestickChartDemo extends ApplicationFrame implements ActionListener {

    private static final String ACTION_CMD_SWITCH_CAND = "switch-cand";

    static {
        // set a theme using the new shadow generator feature available in
        // 1.0.14 - for backwards compatibility it is not enabled by default
        ChartFactory.setChartTheme(new StandardChartTheme("JFree/Shadow",
                true));
    }

    private JScrollBar mJScrollBar;
    private JButton panButton;
    private CandlestickRenderer mCandlestickRenderer;
    private XYLineAndShapeRenderer mLineRenderer;
    private JFreeChart mChart;


    private DataSet loadData() {
        OHLCSeriesCollection dataset = new OHLCSeriesCollection();
        dataset.setXPosition(TimePeriodAnchor.START);

        TimeSeries highSeries = new TimeSeries("high");
        TimeSeries lowSeries = new TimeSeries("low");

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        CSV csv = new CSV();
        try {
            FileReader fileReader = new FileReader("000001.csv");

            CategoryDataset categoryDataset = csv.readCategoryDataset(fileReader);

            int rowCount = categoryDataset.getRowCount();
            for (int i = 0; i < rowCount; i++) {
                Comparable rowKey = categoryDataset.getRowKey(i);
                OHLCSeries s1 = new OHLCSeries(rowKey);

                Day period = new Day(simpleDateFormat.parse(rowKey.toString()));
                Number high = categoryDataset.getValue(i, 1);
                Number open = categoryDataset.getValue(i, 3);
                Number close = categoryDataset.getValue(i, 0);
                Number low = categoryDataset.getValue(i, 2);
                s1.add(period,
                        open.doubleValue(),
                        high.doubleValue(),
                        low.doubleValue(),
                        close.doubleValue());

                highSeries.add(period, high);
                lowSeries.add(period, low);
                dataset.addSeries(s1);
            }
            fileReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        TimeSeriesCollection TdcDataset = new TimeSeriesCollection();
        TdcDataset.addSeries(highSeries);
        TdcDataset.addSeries(lowSeries);

        DataSet dataSet = new DataSet();
        dataSet.dataset = dataset;
        dataSet.TdcDataset = TdcDataset;

        return dataSet;
    }

    private void updateData(DataSet dataSet) {
        XYPlot plot = (XYPlot) mChart.getPlot();

        plot.setDataset(0, dataSet.dataset);
        plot.mapDatasetToRangeAxis(0, 0);

        XYItemRenderer r = plot.getRenderer(0);
        if (r instanceof CandlestickRenderer) {
            mCandlestickRenderer = (CandlestickRenderer) r;
            mCandlestickRenderer.setBaseToolTipGenerator(CandlestickToolTipGenerator.getSeriesInstance());
        }

        int index = 1;

        mLineRenderer = new XYLineAndShapeRenderer(true, false);
        mLineRenderer.setBaseShapesVisible(false);
        mLineRenderer.setBaseShapesFilled(true);
        mLineRenderer.setDrawSeriesLineAsPath(true);
        mLineRenderer.setSeriesPaint(0, Color.RED);
        mLineRenderer.setSeriesPaint(1, Color.GREEN);
        plot.setRenderer(index, mLineRenderer);
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);

        plot.setDataset(index, dataSet.TdcDataset);
        plot.mapDatasetToRangeAxis(index, 0);

        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd"));
    }

    private JFreeChart createChart() {
        mChart = ChartFactory.createCandlestickChart(
                "Legal & General Unit Trust Prices",  // title
                "Date",             // x-axis label
                "Price Per Unit",   // y-axis label
                null,           // data
                false
        );

        mChart.setBackgroundPaint(Color.white);

        XYPlot plot = (XYPlot) mChart.getPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.white);
        plot.setRangeGridlinePaint(Color.white);
        plot.setAxisOffset(new RectangleInsets(5.0, 5.0, 5.0, 5.0));

        plot.setDomainCrosshairVisible(true);
        plot.setRangeCrosshairVisible(true);

        plot.setDomainPannable(true);
        plot.setRangePannable(true);

//        plot.setRangeCrosshairLockedOnData(false);
//        plot.setDomainCrosshairLockedOnData(false);

        Task.callInBackground(() -> {
            DataSet dataSet = loadData();
            updateData(dataSet);
            return null;
        });
        return mChart;
    }

    public JPanel createDemoPanel() {
        JFreeChart chart = createChart();
//        ChartScrollBar chart1 = new ChartScrollBar(ChartScrollBar.HORIZONTAL, chart);

        ChartPanel panel = new ChartPanel(chart);
        panel.setMouseWheelEnabled(true);
//        panel.setFillZoomRectangle(true);
//        panel.setVerticalAxisTrace(true);
//        panel.setHorizontalAxisTrace(true);
        return panel;
    }

    private JToolBar createToolbar() {
        final JToolBar toolbar = new JToolBar();

        panButton = new JButton();
        prepareButton(panButton, ACTION_CMD_SWITCH_CAND, " switch Cand ", "switch Cand");
//        groupedButtons.add(panButton);
        toolbar.add(panButton);

        toolbar.addSeparator();

        this.mJScrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
        //   int ht = (int) zoomButton.getPreferredSize().getHeight();
        //   mJScrollBar.setPreferredSize(new Dimension(0, ht));
        this.mJScrollBar.setModel(new DefaultBoundedRangeModel());

        toolbar.add(this.mJScrollBar);

        this.mJScrollBar.setEnabled(false);

        toolbar.setFloatable(false);
        return toolbar;
    }

    private ChartPanel createChartPanel() {
        ChartPanel chartPanel = (ChartPanel) createDemoPanel();
        chartPanel.setPreferredSize(new Dimension(800, 600));
//        chartPanel.setRangeZoomable(false);
        chartPanel.setPopupMenu(null);

        return chartPanel;
    }

    @Override
    public void windowClosed(WindowEvent event) {
        super.windowClosed(event);
        System.exit(0);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            final String acmd = actionEvent.getActionCommand();
            if (acmd.equals(ACTION_CMD_SWITCH_CAND)) {
                boolean seriesVisible = mCandlestickRenderer.getBaseSeriesVisible();
                mCandlestickRenderer.setBaseSeriesVisible(!seriesVisible);
                mLineRenderer.setBaseShapesVisible(seriesVisible);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void prepareButton(final AbstractButton button,
                               final String actionKey,
                               final String buttonLabelText,
                               final String toolTipText) {

        button.setActionCommand(actionKey);
        button.setText(buttonLabelText);
        button.setToolTipText(toolTipText);
        button.addActionListener(this);
    }

    public CandlestickChartDemo(String title) {
        super(title);
        Container contentPane = getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(createToolbar(), BorderLayout.SOUTH);
        contentPane.add(createChartPanel());
    }

    public static void main(String[] args) {
        CandlestickChartDemo demo = new CandlestickChartDemo("Candlestick");
        demo.pack();
        RefineryUtilities.centerFrameOnScreen(demo);
        demo.setVisible(true);
    }

}
