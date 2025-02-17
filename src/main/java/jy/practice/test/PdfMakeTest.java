package jy.practice.test;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.PDFTextStripperByArea;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.DialChart;
import org.knowm.xchart.DialChartBuilder;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieSeries.PieSeriesRenderStyle;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.PieStyler;
import org.knowm.xchart.style.Styler.ChartTheme;

public class PdfMakeTest {
  private static final float MARGIN = 50;
  private static final float LINE_HEIGHT = 15;
  private static final float IMAGE_WIDTH = 500;
  private static final float IMAGE_HEIGHT = 300;

  public static void main(String[] args) throws IOException {

    // PDF 문서 생성
    try (PDDocument document = new PDDocument()) {
      PDPage page = new PDPage(PDRectangle.A4);
      document.addPage(page);

      PDType0Font font = PDType0Font.load(document, new File("./font/Pretendard-Regular.ttf"));
      PDType0Font boldFont = PDType0Font.load(document, new File("./font/Pretendard-Bold.ttf"));

      try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
        // 초기 Y 위치 계산
        float startY = page.getMediaBox().getHeight() - MARGIN;

        // 1. 제목 추가
        startY = addTitle(contentStream, page, boldFont, "2025년 월별 데이터 리포트", startY);

        // 1-1. 선 추가
        contentStream.moveTo(MARGIN, startY);
        contentStream.lineTo(page.getMediaBox().getWidth() - MARGIN, startY);
        contentStream.setLineWidth(1f);
        contentStream.setStrokingColor(Color.BLUE);
        contentStream.stroke();

        // 2. 서문 추가
        contentStream.setLeading(25f); // 줄간격 설정
        startY = addParagraphAutoLine(page, document, contentStream,
            font,
            "본 보고서는 2025년 1월부터 12월까지의 주요 지표를 시각화하여 제공합니다."
                + " 근데 이 내용이 길어지면 표시가 어떻게 되는지 궁금합니다. 그냥 밖으로 빠져나가려나요?",
            startY - LINE_HEIGHT * 2);

        // 3. 첫 번째 차트 (꺾은선 그래프)
        startY = addSectionTitle(contentStream, font, "월별 사용자 접속 추이", startY - LINE_HEIGHT * 2);
//        BufferedImage lineChartImage = generateLineChart();
//        startY = addImage(document, contentStream, lineChartImage, startY - LINE_HEIGHT);
//        Chart lineChart = generateLineChart();
//        startY = addImage(document, contentStream, lineChart, startY - LINE_HEIGHT);
        Chart dialChart = generateDialChart();
        startY = addImage(document, contentStream, dialChart, startY - LINE_HEIGHT);

        // 4. 두 번째 차트 (막대 그래프)
        startY = addSectionTitle(contentStream, font, "카테고리별 매출 현황", startY - LINE_HEIGHT * 2);
//        BufferedImage barChartImage = generateBarChart();
//        addImage(document, contentStream, barChartImage, startY - LINE_HEIGHT);
//        Chart barChart = generateBarChart();
//        addImage(document, contentStream, barChart, startY - LINE_HEIGHT);
        PieChart pieChart = generateDonutChart();
        addImage(document, contentStream, pieChart, startY - LINE_HEIGHT);
      }

      // PDF 저장
      document.save(new File("report.pdf"));
    }
  }

  // 제목 추가 메서드
  private static float addTitle(PDPageContentStream contentStream, PDPage page, PDType0Font font, String title, float y)
      throws IOException {
    contentStream.beginText();
    contentStream.setFont(font, 22);
    contentStream.newLineAtOffset(MARGIN, y);
    contentStream.showText(title);
    contentStream.endText();
    return y - 30;
  }

  // 섹션 제목 추가
  private static float addSectionTitle(PDPageContentStream contentStream, PDType0Font font, String title, float y)
      throws IOException {
    contentStream.beginText();
    contentStream.setFont(font, 14);
    contentStream.newLineAtOffset(MARGIN, y);
    contentStream.showText(title);
    contentStream.endText();
    return y - 20;
  }

  // 본문 문단 추가
  private static float addParagraph(PDPageContentStream contentStream, PDType0Font font, String text, float y)
      throws IOException {
    contentStream.beginText();
    contentStream.setFont(font, 12);
    contentStream.newLineAtOffset(MARGIN, y);

    String[] words = text.split(" ");
    StringBuilder line = new StringBuilder();
    for (String word : words) {
      if (line.length() + word.length() < 80) {
        line.append(word).append(" ");
      } else {
        contentStream.showText(line.toString());
        contentStream.newLineAtOffset(0, -LINE_HEIGHT);
        y -= LINE_HEIGHT;
        line = new StringBuilder(word + " ");
      }
    }
    contentStream.showText(line.toString());
    contentStream.endText();
    return y - LINE_HEIGHT;
  }

  private static float addParagraphAutoLine(PDPage page, PDDocument document, PDPageContentStream contentStream,
      PDType0Font font, String text, float y) throws IOException {
    float pageWidth = page.getMediaBox().getWidth();
    float pageHeight = page.getMediaBox().getHeight();
    float minY = MARGIN;
    float maxWidth = pageWidth - 2 * MARGIN;
    int fontSize = 12;
    float leading = 14;

    List<String> lines = new ArrayList<>();

    for (String paragraph : text.split("\n")) {
      String[] words = paragraph.split("\\s+");
      StringBuilder currentLine = new StringBuilder();

      for (String word : words) {
        if (currentLine.length() > 0) {
          float lineWidth = fontSize * font.getStringWidth(currentLine + " " + word) / 1000;
          if (lineWidth <= maxWidth) {
            currentLine.append(" ").append(word);
          } else {
            lines.add(currentLine.toString());
            currentLine = new StringBuilder(word);
          }
        } else {
          currentLine.append(word);
        }
      }

      if (currentLine.length() > 0) {
        lines.add(currentLine.toString());
      }
    }

    for (String line : lines) {
      if (y < minY) {
        contentStream.endText();
        contentStream.close();
        PDPage newPage = new PDPage(page.getMediaBox());
        document.addPage(newPage);
        contentStream = new PDPageContentStream(document, newPage);
        y = pageHeight - MARGIN;
      }

      contentStream.beginText();
      contentStream.setFont(font, fontSize);
      contentStream.newLineAtOffset(MARGIN, y);
      contentStream.showText(line);
      contentStream.endText();

      y -= leading;
    }

    return y;
  }


  // 이미지 추가
  private static float addImage(PDDocument document, PDPageContentStream contentStream,
      Chart chart, float y) throws IOException {
    PDImageXObject pdImage = PDImageXObject.createFromByteArray(
        document,
        BitmapEncoder.getBitmapBytes(chart, BitmapEncoder.BitmapFormat.PNG),
        "chart"
    );
    contentStream.drawImage(pdImage, MARGIN, y - IMAGE_HEIGHT, IMAGE_WIDTH, IMAGE_HEIGHT);
    return y - IMAGE_HEIGHT;
  }

  // 예제 차트 생성 메서드 1
  private static BufferedImage generateLineChartBufferedImage() {
    XYChart chart = new XYChartBuilder().width(800).height(600).title("월별 접속자 수").build();
    chart.addSeries("접속자",
        Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12),
        Arrays.asList(150,420,380,510,490,620,580,730,650,810,790,920)
    );
    return BitmapEncoder.getBufferedImage(chart);
  }

  // 예제 차트 생성 메서드 1-1
  private static Chart generateLineChart() {
    XYChart chart = new XYChartBuilder().width(800).height(600).title("월별 접속자 수").build();
    chart.addSeries("접속자",
        Arrays.asList(1,2,3,4,5,6,7,8,9,10,11,12),
        Arrays.asList(150,420,380,510,490,620,580,730,650,810,790,920)
    );
    return chart;
  }

  // 예제 차트 생성 메서드 2
  private static BufferedImage generateBarChartBufferedImage() {
    XYChart chart = new XYChartBuilder().width(800).height(600).title("카테고리별 매출").build();
    chart.addSeries("매출액",
        Arrays.asList("전자제품", "의류", "식품", "가구"),
        Arrays.asList(2450, 1800, 3200, 1550)
    );
    return BitmapEncoder.getBufferedImage(chart);
  }

  // 예제 차트 생성 메서드 2
  private static CategoryChart generateBarChart() {
    // 1. CategoryChart 생성
    CategoryChart chart = new CategoryChartBuilder()
        .width(800)
        .height(600)
        .title("카테고리별 매출")
        .xAxisTitle("카테고리")
        .yAxisTitle("매출액")
        .build();

    // 2. 카테고리 데이터 추가
    chart.addSeries("매출액",
        Arrays.asList("전자제품", "의류", "식품", "가구"),  // X 축: 문자열 카테고리
        Arrays.asList(2450, 1800, 3200, 1550)           // Y 축: 숫자 값
    );

    // 3. 차트 스타일 설정 (옵션)
    chart.getStyler().setPlotGridVerticalLinesVisible(false); // 수직 그리드 제거

    return chart;
  }

  private static DialChart generateDialChart() {
    ChartTheme xChartTheme = ChartTheme.XChart;

    DialChart dialChart =
        new DialChartBuilder().title("title").height(250).width(200).theme(xChartTheme).build();

    dialChart.getStyler().setLabelVisible(false);
    dialChart.getStyler().setChartBackgroundColor(Color.WHITE);
    dialChart.getStyler().setPlotBackgroundColor(Color.WHITE);
    dialChart.getStyler().setCircular(true);
    dialChart.getStyler().setAxisTitlePadding(5);

    dialChart.getStyler().setAxisTickValues(new double[] { .33, .45, .79});
    dialChart.getStyler().setAxisTickLabels(new String[] { "min", "average", "max"});

    dialChart.addSeries("series-name", 0.925, "label");

    return dialChart;
  }

  private static PieChart generateDonutChart() {
    ChartTheme matlabChartTheme = ChartTheme.Matlab; // 라벨 이름이 차트 내에 들어감

//    PieChart pieChart = new PieChart(500, 500, matlabChartTheme);
    PieChart pieChart = new PieChart(500, 500, matlabChartTheme) {
      // 가운데 숫자 % 표시
      @Override
      public void paint(Graphics2D g, int width, int height) {
        super.paint(g, width, height);

        String centerText = "60%"; // 표시할 텍스트
        g.setColor(Color.BLACK);
        g.setFont(new Font("Arial", Font.BOLD, 30));

        FontMetrics metrics = g.getFontMetrics();
        int x = (width - metrics.stringWidth(centerText)) / 2;
        int y = (height - metrics.getHeight()) / 2 + metrics.getAscent();

        g.drawString(centerText, x, y);
      }
    };

    PieStyler pieChartStyler = pieChart.getStyler();

    pieChartStyler.setLegendVisible(false); // 범례
    pieChartStyler.setPlotContentSize(0.7); // 전체 중 차트 크기 비율
    pieChartStyler.setDefaultSeriesRenderStyle(PieSeriesRenderStyle.Donut);
    pieChartStyler.setCircular(true); // ?? 잘 모르겠음

    // 값은 비율로 알아서 계산되어 할당됨
    pieChart.addSeries(" ", 15); // 데이터의 값 표시
    pieChart.addSeries("  ", 10);
    Color[] colors = new Color[2];
    colors[0] = Color.RED;
    colors[1] = new Color(242, 242, 242);
    pieChartStyler.setSeriesColors(colors);

    return pieChart;
  }
}
