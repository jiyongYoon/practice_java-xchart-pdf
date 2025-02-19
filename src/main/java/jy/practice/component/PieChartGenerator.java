package jy.practice.component;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import jy.practice.store.ColorStore;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieSeries.PieSeriesRenderStyle;
import org.knowm.xchart.style.PieStyler;
import org.knowm.xchart.style.Styler.ChartTheme;

public class PieChartGenerator {

  public static PieChart generateDonutChart(int width, int height, int okCount, int totalCount, Font font) {
    ChartTheme matlabChartTheme = ChartTheme.Matlab; // 라벨 이름이 차트 내에 들어감
    long percent = Math.round((double) okCount / totalCount * 100.0);
    Color[] colors = new Color[]{ColorStore.pick(percent), ColorStore.getChartGray()};

    PieChart pieChart = new PieChart(width, height, matlabChartTheme) {
      // 가운데 숫자 % 표시
      @Override
      public void paint(Graphics2D g, int width, int height) {
        super.paint(g, width, height);

        String centerText = String.format("%d%%", percent); // 표시할 텍스트
        g.setColor(Color.BLACK);
        g.setFont(font);
//        g.setFont(new Font("Arial", Font.BOLD, 30));

        FontMetrics metrics = g.getFontMetrics();
        int x = (width - metrics.stringWidth(centerText)) / 2;
        int y = (height - metrics.getHeight()) / 2 + metrics.getAscent();

        g.drawString(centerText, x, y);
      }
    };
    // 값은 비율로 알아서 계산되어 할당됨
    if (percent == 0) {
      pieChart.addSeries(" ", 0);
      pieChart.addSeries("  ", 1);
    } else {
      pieChart.addSeries(" ", okCount); // 데이터의 값 표시
      pieChart.addSeries("  ", totalCount - okCount);
    }

    PieStyler pieChartStyler = pieChart.getStyler();
    pieChartStyler.setLegendVisible(false); // 범례
    pieChartStyler.setPlotContentSize(0.8); // 전체 중 차트 크기 비율
    pieChartStyler.setPlotBorderVisible(false); // 테두리 안보이게
    pieChartStyler.setSeriesColors(colors); // 색깔 주입
    pieChartStyler.setDefaultSeriesRenderStyle(PieSeriesRenderStyle.Donut);

    return pieChart;
  }

}
