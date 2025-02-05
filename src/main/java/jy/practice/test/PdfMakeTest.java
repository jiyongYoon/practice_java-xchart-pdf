package jy.practice.test;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.CategoryChart;
import org.knowm.xchart.CategoryChartBuilder;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.internal.chartpart.Chart;

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

      PDType0Font font = PDType0Font.load(document, new File("NanumGothic.ttf"));
      PDType0Font boldFont = PDType0Font.load(document, new File("NanumGothicBold.ttf"));

      try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
        // 초기 Y 위치 계산
        float startY = page.getMediaBox().getHeight() - MARGIN;

        // 1. 제목 추가
        startY = addTitle(contentStream, page, boldFont, "2025년 월별 데이터 리포트", startY);

        // 2. 서문 추가
        startY = addParagraph(contentStream,
            font,
            "본 보고서는 2025년 1월부터 12월까지의 주요 지표를 시각화하여 제공합니다.",
            startY - LINE_HEIGHT * 2);

        // 3. 첫 번째 차트 (꺾은선 그래프)
        startY = addSectionTitle(contentStream, font, "월별 사용자 접속 추이", startY - LINE_HEIGHT * 2);
//        BufferedImage lineChartImage = generateLineChart();
//        startY = addImage(document, contentStream, lineChartImage, startY - LINE_HEIGHT);
        Chart lineChart = generateLineChart();
        startY = addImage(document, contentStream, lineChart, startY - LINE_HEIGHT);

        // 4. 두 번째 차트 (막대 그래프)
        startY = addSectionTitle(contentStream, font, "카테고리별 매출 현황", startY - LINE_HEIGHT * 2);
//        BufferedImage barChartImage = generateBarChart();
//        addImage(document, contentStream, barChartImage, startY - LINE_HEIGHT);
        Chart barChart = generateBarChart();
        addImage(document, contentStream, barChart, startY - LINE_HEIGHT);
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
}
