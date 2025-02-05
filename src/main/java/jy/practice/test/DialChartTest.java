package jy.practice.test;

import java.awt.Color;
import org.knowm.xchart.DialChart;
import org.knowm.xchart.DialChartBuilder;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.style.Styler.ChartTheme;

public class DialChartTest {

  public static void main(String[] args) throws Exception {
    DialChartBuilder dialChartBuilder = new DialChartBuilder();
    ChartTheme xChartTheme = ChartTheme.XChart;
    ChartTheme ggPlot2ChartTheme = ChartTheme.GGPlot2;
    ChartTheme matlabChartTheme = ChartTheme.Matlab;
    DialChart dialChart =
        dialChartBuilder.title("title").height(500).width(500).theme(xChartTheme).build();

    dialChart.getStyler().setLabelVisible(false);
    dialChart.getStyler().setChartBackgroundColor(Color.WHITE);
    dialChart.getStyler().setPlotBackgroundColor(Color.WHITE);
    dialChart.getStyler().setCircular(true);
    dialChart.getStyler().setAxisTitlePadding(5);

    dialChart.getStyler().setAxisTickValues(new double[] { .33, .45, .79});
    dialChart.getStyler().setAxisTickLabels(new String[] { "min", "average", "max"});

    dialChart.addSeries("series-name", 0.925, "label");

    new SwingWrapper<DialChart>(dialChart).displayChart();
  }
}
