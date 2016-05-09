/* ===========================================================
 * JFreeChart : a free chart library for the Java(tm) platform
 * ===========================================================
 *
 * (C) Copyright 2000-2013, by Object Refinery Limited and Contributors.
 *
 * Project Info:  http://www.jfree.org/jfreechart/index.html
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301,
 * USA.
 *
 * [Oracle and Java are registered trademarks of Oracle and/or its affiliates. 
 * Other names may be trademarks of their respective owners.]
 *
 * -------------------------------
 * StandardXYToolTipGenerator.java
 * -------------------------------
 * (C) Copyright 2004-2008, by Object Refinery Limited.
 *
 * Original Author:  David Gilbert (for Object Refinery Limited);
 * Contributor(s):   -;
 *
 * Changes
 * -------
 * 12-May-2004 : Version 1 (DG);
 * ------------- JFREECHART 1.0.x ---------------------------------------------
 * 25-Jan-2007 : Added new constructor - see bug 1624067 (DG);
 *
 */

package com.ceabie;

import org.jfree.chart.labels.AbstractXYItemLabelGenerator;
import org.jfree.chart.labels.XYToolTipGenerator;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.util.PublicCloneable;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CandlestickToolTipGenerator extends AbstractXYItemLabelGenerator
        implements XYToolTipGenerator, Cloneable, PublicCloneable,
                   Serializable {

    private static final long serialVersionUID = -3564164459039540784L;

    public static final String DEFAULT_TOOL_TIP_FORMAT = "{0}: [{1}, {2}]-({3}, {4})";

    public static CandlestickToolTipGenerator getSeriesInstance() {
        return new CandlestickToolTipGenerator(DEFAULT_TOOL_TIP_FORMAT,
                new SimpleDateFormat("yyyy-MM-dd"), NumberFormat.getInstance());
    }

    public CandlestickToolTipGenerator(String formatString, DateFormat xFormat,
                                       NumberFormat yFormat) {

        super(formatString, xFormat, yFormat);

    }

    @Override
    public String generateToolTip(XYDataset dataset, int series, int item) {
        if (dataset instanceof OHLCSeriesCollection) {
            OHLCSeriesCollection d = (OHLCSeriesCollection) dataset;

            double x = dataset.getXValue(series, item);
            DateFormat xDateFormat = getXDateFormat();
            if (xDateFormat != null) {
                return MessageFormat.format(DEFAULT_TOOL_TIP_FORMAT,
                        xDateFormat.format(new Date((long) x)),
                        d.getOpenValue(series, item), d.getCloseValue(series, item),
                        d.getHighValue(series, item), d.getLowValue(series, item));
            }
        }

        return generateLabelString(dataset, series, item);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof CandlestickToolTipGenerator)) {
            return false;
        }
        return super.equals(obj);
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

}
