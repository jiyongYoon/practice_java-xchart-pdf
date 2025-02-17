package jy.practice.component.test;

import java.awt.Font;
import java.io.IOException;
import jy.practice.component.PDImageGenerator;
import jy.practice.store.DangerTagStore;
import jy.practice.component.DashboardTableComponent;
import jy.practice.component.PieChartGenerator;
import jy.practice.store.FontStore;
import jy.practice.store.PDPageStore;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
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

        PDImageXObject documentImage = PDImageGenerator.generate("my/tag/document.png");

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
            final PDPage page = PDPageStore.generateHorizontalPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                int chatsWidthAndHeight = 150;
                int okCount = 7;
                int totalCount = 12;

                PieChart pieChart = PieChartGenerator.generateDonutChart(chatsWidthAndHeight, chatsWidthAndHeight, okCount, totalCount, font);
                PDImageXObject pdImageXObject = PDImageGenerator.generate(pieChart);
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

            document.save("dashboard-test.pdf");
        }
    }

}