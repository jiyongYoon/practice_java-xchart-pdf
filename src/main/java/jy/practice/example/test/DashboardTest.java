package jy.practice.example.test;

import java.awt.Font;
import java.io.IOException;
import java.util.Objects;
import jy.practice.Main;
import jy.practice.utils.factory.PDImageFactory;
import jy.practice.utils.store.ColorStore;
import jy.practice.utils.store.DangerTagStore;
import jy.practice.example.test.component.DashboardTableComponent;
import jy.practice.utils.factory.PieChartFactory;
import jy.practice.utils.store.FontStore;
import jy.practice.utils.factory.PDPageFactory;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.PieChart;
import org.vandeseer.easytable.TableDrawer;
import org.vandeseer.easytable.structure.Table;

public class DashboardTest {

    private final static float DEFAULT_MARGIN = 35f;

    public static void main(String[] args) throws IOException {

        PDType0Font pdFont = FontStore.getPdFont();
        PDType0Font pdBoldFont = FontStore.getPdBoldFont();

        Font font = FontStore.getFont();
        font = font.deriveFont(25f);

        String documentFilePath = "example/tag/document.png";
        byte[] documentByteArray = IOUtils.toByteArray(Objects.requireNonNull(
            Main.class.getClassLoader().getResourceAsStream(documentFilePath)));

        PDImageXObject documentImage = PDImageFactory.generate(documentByteArray, documentFilePath.split("\\.")[0]);

        DashboardTableComponent dashboardTableComponent = DashboardTableComponent.builder()
            .font(pdFont)
            .fontSize(8)
            .boldFont(pdBoldFont)
            .boldFontSize(8)
            .xColSize(12)
            .xColCount(16)
            .rowHeight(10f)
            .documentImage(documentImage)
            .build();

        float componentWidth = dashboardTableComponent.getComponentWidth();
        float componentHeight = dashboardTableComponent.getComponentHeight();

        try (PDDocument document = new PDDocument()) {
            final PDPage page = PDPageFactory.generateHorizontalPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                int chartsWidthAndHeight = 150;
                int okCount = 7;
                int totalCount = 12;
                long percent = Math.round((double) okCount / totalCount * 100.0);

                PieChart donutChart = PieChartFactory.generateDonutChart(
                    chartsWidthAndHeight, chartsWidthAndHeight, okCount, totalCount, font,
                    ColorStore.pick(percent), ColorStore.getChartGray());
                byte[] donutChartByteArray = BitmapEncoder.getBitmapBytes(donutChart, BitmapFormat.PNG);
                PDImageXObject pdImageXObject = PDImageFactory.generate(donutChartByteArray, "chart");
                PDImageXObject tagImage = DangerTagStore.pick(Math.round((double) okCount / totalCount * 100.0));

                float componentPeriod = 2.5f;

                Table myTable = dashboardTableComponent.generate("안전보건 목표 및 경영방침", 3, pdImageXObject, tagImage);
                TableDrawer tableDrawer = TableDrawer.builder()
                        .contentStream(contentStream)
                        .startX(DEFAULT_MARGIN)
                        .startY(page.getMediaBox().getUpperRightY() - DEFAULT_MARGIN)
                        .table(myTable)
                        .build();
                tableDrawer.draw();

                Table myTable2 = dashboardTableComponent.generate("안전보건 전담조직", 2, pdImageXObject, tagImage);
                TableDrawer tableDrawer2 = TableDrawer.builder()
                    .contentStream(contentStream)
                    .startX(DEFAULT_MARGIN + componentPeriod * 1 + componentWidth * 1)
                    .startY(page.getMediaBox().getUpperRightY() - DEFAULT_MARGIN)
                    .table(myTable2)
                    .build();
                tableDrawer2.draw();

                Table myTable3 = dashboardTableComponent.generate("유해-위험요인 개선 조치", 1, pdImageXObject, tagImage);
                TableDrawer tableDrawer3 = TableDrawer.builder()
                    .contentStream(contentStream)
                    .startX(DEFAULT_MARGIN + componentPeriod * 2 + componentWidth * 2)
                    .startY(page.getMediaBox().getUpperRightY() - DEFAULT_MARGIN)
                    .table(myTable3)
                    .build();
                tableDrawer3.draw();

                Table myTable4 = dashboardTableComponent.generate("안전-보건 예산 편성 및 집행", 3, pdImageXObject, tagImage);
                TableDrawer tableDrawer4 = TableDrawer.builder()
                    .contentStream(contentStream)
                    .startX(DEFAULT_MARGIN + componentPeriod * 3 + componentWidth * 3)
                    .startY(page.getMediaBox().getUpperRightY() - DEFAULT_MARGIN)
                    .table(myTable4)
                    .build();
                tableDrawer4.draw();

                Table myTable5 = dashboardTableComponent.generate("안전보건 목표 및 경영방침", 3, pdImageXObject, tagImage);
                TableDrawer tableDrawer5 = TableDrawer.builder()
                    .contentStream(contentStream)
                    .startX(DEFAULT_MARGIN)
                    .startY(page.getMediaBox().getUpperRightY() - DEFAULT_MARGIN - componentPeriod * 8 - componentHeight * 1)
                    .table(myTable5)
                    .build();
                tableDrawer5.draw();

                Table myTable6 = dashboardTableComponent.generate("안전보건 전담조직", 2, pdImageXObject, tagImage);
                TableDrawer tableDrawer6 = TableDrawer.builder()
                    .contentStream(contentStream)
                    .startX(DEFAULT_MARGIN + componentPeriod * 1 + componentWidth * 1)
                    .startY(page.getMediaBox().getUpperRightY() - DEFAULT_MARGIN - componentPeriod * 8 - componentHeight * 1)
                    .table(myTable6)
                    .build();
                tableDrawer6.draw();

                Table myTable7 = dashboardTableComponent.generate("유해-위험요인 개선 조치", 1, pdImageXObject, tagImage);
                TableDrawer tableDrawer7 = TableDrawer.builder()
                    .contentStream(contentStream)
                    .startX(DEFAULT_MARGIN + componentPeriod * 2 + componentWidth * 2)
                    .startY(page.getMediaBox().getUpperRightY() - DEFAULT_MARGIN - componentPeriod * 8 - componentHeight * 1)
                    .table(myTable7)
                    .build();
                tableDrawer7.draw();

                Table myTable8 = dashboardTableComponent.generate("안전-보건 예산 편성 및 집행", 3, pdImageXObject, tagImage);
                TableDrawer tableDrawer8 = TableDrawer.builder()
                    .contentStream(contentStream)
                    .startX(DEFAULT_MARGIN + componentPeriod * 3 + componentWidth * 3)
                    .startY(page.getMediaBox().getUpperRightY() - DEFAULT_MARGIN - (componentPeriod * 8) * 1 - componentHeight * 1)
                    .table(myTable8)
                    .build();
                tableDrawer8.draw();

                Table myTable9 = dashboardTableComponent.generate("안전-보건 예산 편성 및 집행", 3, pdImageXObject, tagImage);
                TableDrawer tableDrawer9 = TableDrawer.builder()
                    .contentStream(contentStream)
                    .startX(DEFAULT_MARGIN)
                    .startY(page.getMediaBox().getUpperRightY() - DEFAULT_MARGIN - (componentPeriod * 8) * 2 - componentHeight * 2)
                    .table(myTable9)
                    .build();
                tableDrawer9.draw();
            }

            document.save("./outputs/dashboard-test.pdf");
        }
    }

}