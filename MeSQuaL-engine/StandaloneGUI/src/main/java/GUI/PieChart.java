/*
 *     This file is part of MeSQuaL2.
 *
 *     Foobar is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     MeSQuaL2 is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with MeSQuaL2. If not, see <https://www.gnu.org/licenses/>.
 */

package GUI;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.PiePlot3D;
import org.jfree.chart.util.Rotation;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import javax.swing.*;

public class PieChart extends JPanel {

    private static final long serialVersionUID = 1L;

    private ChartPanel chartPanel;

    public PieChart(String chartTitle) {
        // This will create the dataset
        PieDataset dataset = createDataset();
        // based on the dataset we create the chart
        JFreeChart chart = createChart(dataset, chartTitle);
        // we put the chart into a panel
        this.chartPanel = new ChartPanel(chart);
        // default size
        this.chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
    }

    public PieChart(PieDataset dataset, String chartTitle) {
        // based on the dataset we create the chart
        JFreeChart chart = createChart(dataset, chartTitle);
        // we put the chart into a panel
        this.chartPanel = new ChartPanel(chart);
        // default size
        this.chartPanel.setPreferredSize(new java.awt.Dimension(500, 270));
    }

    public ChartPanel getChartPanel(){
        return this.chartPanel;
    }

    /**
     * Creates a sample dataset
     */
    private PieDataset createDataset() {
        DefaultPieDataset result = new DefaultPieDataset();
        result.setValue("Linux", 29);
        result.setValue("Mac", 20);
        result.setValue("Windows", 51);
        return result;

    }

    /**
     * Creates a chart
     */
    private JFreeChart createChart(PieDataset dataset, String title) {

        JFreeChart chart = ChartFactory.createPieChart(
                title,                  // chart title
                dataset,                // data
                true,                   // include legend
                true,
                false
        );

        PiePlot plot = (PiePlot) chart.getPlot();
        plot.setStartAngle(290);
        plot.setDirection(Rotation.CLOCKWISE);
        //plot.setForegroundAlpha(0.5f);
        return chart;

    }

}
