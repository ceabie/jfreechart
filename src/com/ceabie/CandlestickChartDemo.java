package com.ceabie;

import bolts.Task;
import com.ceabie.util.FileUtil;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.plot.DatasetRenderingOrder;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.*;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RectangleInsets;
import org.jfree.ui.RefineryUtilities;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * CandlestickChart.
 *
 * @author ceabie
 */
public class CandlestickChartDemo extends ApplicationFrame implements ActionListener {

    private static final String ACTION_CMD_SWITCH_CAND = "switch-cand";
    private static final String ACTION_CMD_SWITCH_LOW = "switch-low";
    public static final int COLUMN_HIGH = 1;
    public static final int COLUMN_OPEN = 3;
    public static final int COLUMN_CLOSE = 0;
    public static final int COLUMN_LOW = 2;

    static {
        // set a theme using the new shadow generator feature available in
        // 1.0.14 - for backwards compatibility it is not enabled by default
        ChartFactory.setChartTheme(new StandardChartTheme("JFree/Shadow", true));
    }

    private JScrollBar mJScrollBar;
    private JButton mPanButton;
    private CandlestickRenderer mCandlestickRenderer;
    private XYLineAndShapeRenderer mLineRenderer;
    private JFreeChart mChart;
    private JButton mLowButton;


    /**
     * Process data data set.
     *
     * @return the data set
     * @throws ParseException the parse exception
     */
    private DataSet processData() throws ParseException {
        String dataFile = "000001.csv";
        CategoryDataset categoryDataset = FileUtil.getCategoryDataset(dataFile);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        OHLCSeriesCollection dataset = new OHLCSeriesCollection();
        dataset.setXPosition(TimePeriodAnchor.START);

        TimeSeries highSeries = new TimeSeries("high");
        TimeSeries lowSeries = new TimeSeries("low");
        TimeSeries highDiffSeries = new TimeSeries("highDiff");
//        TimeSeries lowDiffSeries = new TimeSeries("lowDiff");

        int rowCount = categoryDataset.getRowCount();
        int upHigh = 0;
        int downHigh = 0;
        int stopHigh = 0;

        int upLow = 0;
        int downLow = 0;
        int stopLow = 0;

        int lastIndex = rowCount - 1;
        double startHigh = categoryDataset.getValue(lastIndex, COLUMN_HIGH).doubleValue();
        double startLow = categoryDataset.getValue(lastIndex, COLUMN_LOW).doubleValue();
        double lastHigh = startHigh;
        double lastLow = startLow;

        for (int i = lastIndex; i >= 0; i--) {
            Comparable rowKey = categoryDataset.getRowKey(i);
            OHLCSeries s1 = new OHLCSeries(rowKey);

            Day period = new Day(simpleDateFormat.parse(rowKey.toString()));
            Number high = categoryDataset.getValue(i, COLUMN_HIGH);
            Number open = categoryDataset.getValue(i, COLUMN_OPEN);
            Number close = categoryDataset.getValue(i, COLUMN_CLOSE);
            Number low = categoryDataset.getValue(i, COLUMN_LOW);
            double doubleHigh = high.doubleValue();
            double doubleLow = low.doubleValue();

            s1.add(period,
                    open.doubleValue(),
                    doubleHigh,
                    doubleLow,
                    close.doubleValue());
            dataset.addSeries(s1);

            highSeries.add(period, high);
            lowSeries.add(period, low);
            double diffHigh = doubleHigh - lastHigh;
            if (diffHigh > 0) {
                upHigh++;
            } else if (diffHigh < 0) {
                downHigh++;
            } else {
                stopHigh++;
            }

//            double diffLow = doubleLow - lastLow;
//            if (diffLow > 0) {
//                upLow++;
//            } else if (diffLow < 0) {
//                downLow++;
//            } else {
//                stopLow++;
//            }

            highDiffSeries.add(period, diffHigh + 30);
//            lowDiffSeries.add(period, diffLow - 300);
            lastHigh = doubleHigh;
//            lastLow = doubleLow;
        }

        float to = upHigh + downHigh;
        System.out.println(String.format("upHigh: %d [%.3f]", upHigh, upHigh / to));
        System.out.println(String.format("downHigh: %d [%.3f]", downHigh, downHigh / to));
        System.out.println("stopHigh: " + stopHigh);
        System.out.println("diff: " + (upHigh / to - downHigh / to));

//        to = upLow + downLow;
//        System.out.println(String.format("upLow: %d [%.3f]", upLow, upLow / to));
//        System.out.println(String.format("downLow: %d [%.3f]", downLow, downLow / to));
//        System.out.println("stopLow: " + stopLow);
//        System.out.println("diff: " + (upLow / to - downLow / to));

        TimeSeriesCollection TdcDataset = new TimeSeriesCollection();
        TdcDataset.setXPosition(TimePeriodAnchor.START);
        TdcDataset.addSeries(highSeries);
        TdcDataset.addSeries(lowSeries);

        TimeSeriesCollection diffDataset = new TimeSeriesCollection();
        diffDataset.addSeries(highDiffSeries);
        diffDataset.setXPosition(TimePeriodAnchor.START);
//        TdcDataset.addSeries(lowDiffSeries);

        DataSet dataSet = new DataSet();
        dataSet.dataset = dataset;
        dataSet.TdcDataset = TdcDataset;
        dataSet.diffDataset = diffDataset;

        return dataSet;
    }

    /**
     * Update data.
     *
     * @param dataSet the data set
     */
    private void updateView(DataSet dataSet) {
        XYPlot plot = (XYPlot) mChart.getPlot();
        plot.setDatasetRenderingOrder(DatasetRenderingOrder.FORWARD);
        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd"));

        // tipTool
        CandlestickToolTipGenerator toolTipGenerator = CandlestickToolTipGenerator.getSeriesInstance();

        // candlestick
        int candlestickIndex = 0;
        XYItemRenderer r = plot.getRenderer(candlestickIndex);
        if (r instanceof CandlestickRenderer) {
            mCandlestickRenderer = (CandlestickRenderer) r;
            mCandlestickRenderer.setBaseToolTipGenerator(toolTipGenerator);
        }

        plot.setDataset(candlestickIndex, dataSet.dataset);
//        plot.mapDatasetToRangeAxis(candlestickIndex, 0);

        // anylsis
        int index = 1;
        mLineRenderer = new XYLineAndShapeRenderer(true, false);
        mLineRenderer.setBaseShapesVisible(false);
        mLineRenderer.setBaseShapesFilled(true);
        mLineRenderer.setDrawSeriesLineAsPath(true);
        mLineRenderer.setSeriesPaint(0, Color.RED);
        mLineRenderer.setSeriesPaint(1, Color.GREEN);
        mLineRenderer.setBaseShapesVisible(false);
        mLineRenderer.setDrawSeriesLineAsPath(false);
        mLineRenderer.setBaseToolTipGenerator(toolTipGenerator);

        addToPlot(mChart, index, mLineRenderer, dataSet.TdcDataset);
//        plot.mapDatasetToRangeAxis(index, 0);

        // anylsis
        final int diffIndex = 2;
        XYBarRenderer barRenderer = new XYBarRenderer() {
            @Override
            public Paint getItemPaint(int row, int column) {
                double yValue = getPlot().getDataset(diffIndex).getYValue(row, column);
                if (yValue < 0) {
                    return Color.RED;
                } else {
                    return Color.GREEN;
                }
            }
        };
        barRenderer.setBarPainter(new StandardXYBarPainter());
        barRenderer.setDrawBarOutline(false);
        barRenderer.setShadowVisible(false);
        barRenderer.setDefaultShadowsVisible(false);

        addToPlot(mChart, diffIndex, barRenderer, dataSet.diffDataset);
    }

    private void addToPlot(JFreeChart jFreeChart, int index, XYItemRenderer renderer, XYDataset dataSet) {
        XYPlot plot = jFreeChart.getXYPlot();
        plot.setRenderer(index, renderer);
        plot.setDataset(index, dataSet);
    }

    /**
     * Create chart j free chart.
     *
     * @return the j free chart
     */
    private JFreeChart createChart() {
        mChart = ChartFactory.createCandlestickChart(
                "Candlestick",  // title
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
            DataSet dataSet = processData();
            updateView(dataSet);
            return null;
        });
        return mChart;
    }

    public JPanel createDemoPanel() {
        JFreeChart chart = createChart();
        chart.setAntiAlias(true);
        chart.getRenderingHints().put(RenderingHints.KEY_INTERPOLATION,
                RenderingHints.VALUE_INTERPOLATION_BICUBIC);

        ChartPanel panel = new ChartPanel(chart);
        panel.setMouseWheelEnabled(true);
//        panel.setFillZoomRectangle(true);
//        panel.setVerticalAxisTrace(true);
//        panel.setHorizontalAxisTrace(true);
        return panel;
    }

    /////////////////////////////// Tool bar ////////////////////////////////////

    private JToolBar createToolbar() {
        final JToolBar toolbar = new JToolBar();

        mPanButton = new JButton();
        prepareButton(mPanButton, ACTION_CMD_SWITCH_CAND, " switch Cand ", "switch Cand");
//        groupedButtons.add(mPanButton);
        toolbar.add(mPanButton);

        toolbar.addSeparator();

        mLowButton = new JButton();
        prepareButton(mLowButton, ACTION_CMD_SWITCH_LOW, " switch Low ", "switch Low");
        toolbar.add(mLowButton);

        toolbar.addSeparator();

        mJScrollBar = new JScrollBar(JScrollBar.HORIZONTAL);
        //   int ht = (int) zoomButton.getPreferredSize().getHeight();
        //   mJScrollBar.setPreferredSize(new Dimension(0, ht));
        mJScrollBar.setModel(new DefaultBoundedRangeModel());

        toolbar.add(mJScrollBar);

        mJScrollBar.setEnabled(false);

        toolbar.setFloatable(false);
        return toolbar;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            final String acmd = actionEvent.getActionCommand();
            switch (acmd) {
                case ACTION_CMD_SWITCH_CAND:
                    boolean seriesVisible = mCandlestickRenderer.getBaseSeriesVisible();
                    mCandlestickRenderer.setBaseSeriesVisible(!seriesVisible);
//                mLineRenderer.setBaseShapesVisible(seriesVisible);
                    break;

                case ACTION_CMD_SWITCH_LOW:
                    Boolean visible = mLineRenderer.getSeriesLinesVisible(1);
                    if (visible == null) {
                        visible = true;
                    }
                    mLineRenderer.setSeriesLinesVisible(1, !visible);
                    break;
            }
        } catch (Exception e) {
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

    /////////////////////////////// Main ////////////////////////////////////
//    @Override
//    public void windowClosed(WindowEvent event) {
//        super.windowClosed(event);
//        System.exit(0);
//    }

    private ChartPanel createChartPanel() {
        ChartPanel chartPanel = (ChartPanel) createDemoPanel();
        chartPanel.setPreferredSize(new Dimension(1200, 800));
//        chartPanel.setRangeZoomable(false);
        chartPanel.setPopupMenu(null);

        return chartPanel;
    }

    public CandlestickChartDemo(String title) {
        super(title);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

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
