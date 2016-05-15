package com.ceabie.Renderer;

import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRendererState;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;

import java.awt.*;
import java.awt.geom.Rectangle2D;

public class BarXYRenderer extends XYLineAndShapeRenderer {

    private XYDataset mData;

    public BarXYRenderer() {
        super();
    }

    public BarXYRenderer(boolean lines, boolean shapes) {
        super(lines, shapes);
    }

    @Override
    public XYItemRendererState initialise(Graphics2D g2, Rectangle2D dataArea, XYPlot plot, XYDataset data, PlotRenderingInfo info) {
        mData = data;
        return super.initialise(g2, dataArea, plot, data, info);
    }

    @Override
    public Paint getItemPaint(int row, int column) {
        double yValue = mData.getYValue(row, column);
        if (yValue < 0) {
            return Color.RED;
        }

        return super.getItemPaint(row, column);
    }
}
