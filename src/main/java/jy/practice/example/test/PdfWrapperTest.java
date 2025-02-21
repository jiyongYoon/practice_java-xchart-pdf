package jy.practice.example.test;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import jy.practice.example.test.component.PdfContentStreamWrapper;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.PieChart;
import org.knowm.xchart.PieSeries.PieSeriesRenderStyle;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.PieStyler;
import org.knowm.xchart.style.Styler.ChartTheme;

public class PdfWrapperTest {
    public static final String dummy = "수정된 코드에서는 다음과 같은 점이 핵심입니다.\n"
        + "라인 분리 옵션: addText()와 addImage() 메서드는 네 번째 인자로 줄 바꿈 여부(newLineAfter)를 받아, true인 경우 새 줄에서 컨텐츠를 추가하고 false인 경우 현재 라인 오른쪽에 이어서 추가합니다.\n"
        + "자동 페이지 전환: ensureVerticalSpace() 메서드가 현재 좌표와 추가될 컨텐츠의 높이를 비교하여, 마진 이하가 되면 현재 contentStream을 종료한 후 새 페이지를 생성하고 좌표를 초기화합니다.\n"
        + "이와 같이 객체 내부에서 좌표와 페이지 상태를 관리하면, 보다 객체지향적이고 확장 가능한 PDF 생성 로직을 구성할 수 있습니다.";
    public static void main(String[] args) throws Exception {
        PDRectangle a4 = PDRectangle.A4;
        // PDF 문서를 생성하고 페이지 추가
        PDDocument document = new PDDocument();
        PDPage page = new PDPage(a4);
        document.addPage(page);

        // 폰트 로드 (폰트 파일 경로를 실제 경로로 맞춰주세요)
        PDType0Font font = PDType0Font.load(document, new File("./font/Pretendard-Regular.ttf"));

        // 래퍼 클래스 생성 (마진값 50)
        PdfContentStreamWrapper pdfWrapper = new PdfContentStreamWrapper(document, page, a4, 50);

        PDImageXObject chartImage = makeImage(document, generateDonutChart());

        // 제목 추가, 선 그리기, 본문 텍스트 추가
        pdfWrapper.addText("2025년 월별 데이터 리포트", font, 22)
            .addLineSeparator(Color.BLUE, 1f)
            .newLine(20)
            .addText("본 보고서는 2025년 1월부터 12월까지의 주요 지표를 시각화하여 제공합니다.", font, 12)
            .newLine(20)
            .addImage(chartImage, 100, 100)
            .addText(dummy, font, 12)
            .newLine(20)
            .addImage(chartImage, 200, 200)
            .addText(dummy, font, 12)
            .addLineSeparator(Color.BLACK, 2f)
            .addImage(chartImage, 300, 300)
            .newLine(20);

        pdfWrapper
            .addText(dummy, font, 15)
            .addImage(chartImage, 300, 300)
            .addImage(chartImage, 100, 100)
            .addImage(chartImage, 100, 100)
            .newLine(20)
            .addImage(chartImage, 100, 100)
            .newLine(20)
            .addImage(chartImage, 100, 100)
            .newLine(20)
            .addImage(chartImage, 100, 100)
            .addImage(chartImage, 100, 100)
            .addImage(chartImage, 100, 100)
            .addImage(chartImage, 100, 100)
            .addImage(chartImage, 100, 100)
            .addImage(chartImage, 100, 100)
        ;

        // contentStream 닫기 및 PDF 저장
        pdfWrapper.close();
        document.save(new File("./outputs/pdf-wrapper-test.pdf"));
        document.close();
    }

    private static PieChart generateDonutChart() {
        ChartTheme matlabChartTheme = ChartTheme.Matlab; // 라벨 이름이 차트 내에 들어감

        PieChart pieChart = new PieChart(250, 200, matlabChartTheme);
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
        pieChartStyler.setPlotBorderVisible(false); // 차트 테두리 없음

        return pieChart;
    }

    private static PDImageXObject makeImage(PDDocument document, Chart chart)
        throws IOException {
        return PDImageXObject.createFromByteArray(
            document,
            BitmapEncoder.getBitmapBytes(chart, BitmapFormat.PNG),
            "chart-name");
    }
}
