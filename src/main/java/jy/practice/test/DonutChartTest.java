package jy.practice.test;

import java.awt.Color;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieSeries.PieSeriesRenderStyle;
import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.style.PieStyler;
import org.knowm.xchart.style.Styler.ChartTheme;

public class DonutChartTest {

  public static void main(String[] args) {
    ChartTheme xChartTheme = ChartTheme.XChart; // 라벨 값이 차트 내에 들어감
    ChartTheme ggPlot2ChartTheme = ChartTheme.GGPlot2; // 라벨 값과 이름이 모두 차트 내에 들어감
    ChartTheme matlabChartTheme = ChartTheme.Matlab; // 라벨 이름이 차트 내에 들어감

    PieChart pieChart = new PieChart(500, 500, matlabChartTheme);
    PieStyler pieChartStyler = pieChart.getStyler();

    pieChartStyler.setLegendVisible(false); // 범례
    pieChartStyler.setPlotContentSize(0.7); // 전체 중 차트 크기 비율
    pieChartStyler.setDefaultSeriesRenderStyle(PieSeriesRenderStyle.Donut);
    pieChartStyler.setCircular(true); // ?? 잘 모르겠음

    // 값은 비율로 알아서 계산되어 할당됨
    pieChart.addSeries(" ", 15);
    pieChart.addSeries("  ", 10);
    Color[] colors = new Color[2];
    colors[0] = Color.RED;
    colors[1] = new Color(242, 242, 242);
    pieChartStyler.setSeriesColors(colors);

    new SwingWrapper<PieChart>(pieChart).displayChart();
  }
}
