package jy.practice.drawer;

import static jy.practice.utils.factory.CellFactory.spaceBottomRow;
import static jy.practice.utils.factory.CellFactory.spaceCell;
import static jy.practice.utils.factory.CellFactory.spaceLeftCell;
import static jy.practice.utils.factory.CellFactory.spaceRightCell;
import static jy.practice.utils.factory.CellFactory.spaceTopRow;

import java.awt.Color;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import jy.practice.utils.store.FontStore;
import jy.practice.drawer.material.BodyDrawMaterial;
import jy.practice.drawer.material.DashboardComponentElement;
import jy.practice.drawer.material.DashboardDrawMaterial;
import jy.practice.drawer.material.StringValueFont;
import jy.practice.drawer.material.TitleDrawMaterial;
import jy.practice.utils.factory.PDPageFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.vandeseer.easytable.TableDrawer;
import org.vandeseer.easytable.settings.BorderStyle;
import org.vandeseer.easytable.settings.HorizontalAlignment;
import org.vandeseer.easytable.settings.VerticalAlignment;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.Table.TableBuilder;
import org.vandeseer.easytable.structure.cell.ImageCell;
import org.vandeseer.easytable.structure.cell.TextCell;

// 페이지 넘어감 대응됨 -> 현재 Dashboard Component 크기에서만
public class DashboardDrawer implements Drawer<DashboardDrawMaterial>, TitleDrawer, BodyDrawer {

  public final static String DASHBOARD_TITLE_VALUE = "2. 중대재해처벌법 의무 이행 현황 - 요약";
  private final static int componentXCount = 4; // 한 페이지에 가로로 component 4개씩
  private final static int componentTotalCountFirstPage = 16; // 첫 페이지에는 16개만
  private final static int componentTotalCountAfterSecondPage = 20; // 두번째 페이지서부터는 20개만

  @Override
  public int draw(PDDocument document, DashboardDrawMaterial drawMaterial, float pageBorderMargin)
      throws IOException {

    int pagesDrawn = 1;

    BorderStyle borderStyle = BorderStyle.SOLID;

    PDPage page = PDPageFactory.generateHorizontalPage();
    document.addPage(page);
    int pageWidth = 770; // 좌우 가장자리 마진을 50씩 줬을때 꽉차도록

    // 제목 그리기
    TitleDrawMaterial titleDrawMaterial = TitleDrawMaterial.builder()
        .page(page)
        .titleColumnsOfWidth(pageWidth)
        .titleFont(drawMaterial.getFont())
        .titleFontSize(FontStore.TITLE_FONT_SIZE)
        .titleTextColor(Color.BLACK)
        .borderColor(Color.WHITE)
        .titleName(DASHBOARD_TITLE_VALUE)
        .build();

    drawTitle(document, titleDrawMaterial, pageBorderMargin);

    // 본문 그리기
    BodyDrawMaterial bodyDrawMaterial = BodyDrawMaterial.builder()
        .page(page)
        .bodyColumnsOfWidth(pageWidth)
        .bodyFont(drawMaterial.getFont())
        .bodyFontSize(FontStore.BODY_FONT_SIZE)
        .bodyTextColor(Color.BLACK)
        .borderColor(Color.WHITE)
        .bodyElementList(Arrays.asList(
            StringValueFont.builder()
                .value(
                    String.format("[%d차] %s ~ %s", drawMaterial.getRoundIndex(),
                        drawMaterial.getRoundStartDate(), drawMaterial.getRoundEndDate()))
            .build(),
            StringValueFont.builder().value(" ").fontSize(5).build(),
            StringValueFont.builder().value("보고서 출력일: " + LocalDate.now().toString()).fontSize(10) // TODO 전달받을수도 있음
                .horizontalAlignment(HorizontalAlignment.RIGHT).build()
        ))
        .build();
    int bodyStartY = 70;
    drawBody(document, bodyDrawMaterial, pageBorderMargin, bodyStartY);

    float nextY = TITLE_Y + bodyStartY;

    boolean pageCreateFlag = false;

    // 대시보드 그리기
    for (DashboardComponentElement dashboardComponentElement : drawMaterial.getDashboardComponentElementList()) {
      int componentIndex = drawMaterial.getDashboardComponentElementList().indexOf(dashboardComponentElement);
      int calculateComponentIndex = componentIndex;
      float startY = calculateStartY(drawMaterial, pageBorderMargin, page, calculateComponentIndex, nextY);
      // 두번째 페이지부터는 startY 다시 계산
      if (isAfterFirstPage(componentIndex)) {
        calculateComponentIndex = isSecondPage(componentIndex)
            ? componentIndex - componentTotalCountFirstPage // 두번째 페이지
            : (componentIndex - componentTotalCountFirstPage) % componentTotalCountAfterSecondPage; // 세번째 페이지 ~
        startY = calculateAfterFirstPageStartY(drawMaterial, pageBorderMargin, page, calculateComponentIndex);

        // 페이지 다시 만들도록 trigger
        pageCreateFlag = isNewPage(calculateComponentIndex);
      }
      // 두번째 페이지부터 새로 페이지를 만들면 그 페이지동안은 다시 안만들기
      if (pageCreateFlag && isAfterFirstPage(componentIndex)) {
        page = PDPageFactory.generateHorizontalPage();
        document.addPage(page);
        pagesDrawn++;
        pageCreateFlag = false;
      }

      try (PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, true, true)) {
        Table table = generate(
            drawMaterial,
            borderStyle,
            dashboardComponentElement.getComponentBorderColor(),
            dashboardComponentElement.getTitle(),
            dashboardComponentElement.getOkCount(),
            dashboardComponentElement.getCountTextColor(),
            dashboardComponentElement.getChartImage(),
            dashboardComponentElement.getTagImage());

        TableDrawer.builder()
            .contentStream(contentStream)
            .startX(calculateStartX(drawMaterial, pageBorderMargin, componentXCount, componentIndex))
            .startY(startY)
            .table(table)
            .build()
            .draw();
      }
    }

    return pagesDrawn;
  }

  private static boolean isNewPage(int calculateComponentIndex) {
    return calculateComponentIndex == 0;
  }

  private static boolean isAfterFirstPage(int componentIndex) {
    return componentIndex >= componentTotalCountFirstPage;
  }

  private static boolean isSecondPage(int componentIndex) {
    return componentIndex < componentTotalCountFirstPage + componentTotalCountAfterSecondPage;
  }

  private static float calculateStartY(
      DashboardDrawMaterial drawMaterial,
      float pageBorderMargin,
      PDPage pdPage,
      int calculateComponentIndex,
      float titleY) {
    return pdPage.getMediaBox().getUpperRightY() - pageBorderMargin - (
        drawMaterial.getComponentInterval() * 8 * (calculateComponentIndex / 4) // 일부러 정수부만 활용
            + drawMaterial.getComponentHeight() * (calculateComponentIndex / 4)
    )
        - titleY; // 첫 페이지는 제목만큼 더 빼기
  }

  private static float calculateAfterFirstPageStartY(
      DashboardDrawMaterial drawMaterial,
      float pageBorderMargin,
      PDPage pdPage,
      int calculateComponentIndex) {
    return pdPage.getMediaBox().getUpperRightY() - pageBorderMargin - (
        drawMaterial.getComponentInterval() * 8 * (calculateComponentIndex / 4) // 일부러 정수부만 활용
            + drawMaterial.getComponentHeight() * (calculateComponentIndex / 4)
    );
  }

  private Table generate(
      DashboardDrawMaterial dashboardDrawMaterial,
      BorderStyle borderStyle,
      Color borderColor,
      String title,
      int documentCount,
      Color countTextColor,
      PDImageXObject pieChart,
      PDImageXObject tagImage) {
    int totalColSpan = dashboardDrawMaterial.getXColCount();
    float totalHeight = dashboardDrawMaterial.getRowHeight();

    TableBuilder tableBuilder = generateBuilder(
        dashboardDrawMaterial.getXColCount(),
        dashboardDrawMaterial.getXColSize(),
        dashboardDrawMaterial.getFont(),
        dashboardDrawMaterial.getFontSize());

    tableBuilder
        .addRow(spaceTopRow(totalHeight, totalColSpan, 1f, borderStyle, borderColor))
        .addRow(Row.builder()
            .add(spaceLeftCell(1, 1f, borderStyle, borderColor))
            .add(TextCell.builder()
                .text(title)
                .colSpan(8)
                .rowSpan(2)
                .font(dashboardDrawMaterial.getBoldFont())
                .fontSize(dashboardDrawMaterial.getBoldFontSize())
                .build())
            .add(spaceCell(1))
            .add(pieChart == null
                ? TextCell.builder().text("").colSpan(5).rowSpan(5).minHeight(60).build() // Image Cell이랑 높이를 갖게 하기 위해서
                : ImageCell.builder().image(pieChart).colSpan(5).rowSpan(5).scale(0.5f).minHeight(60)
                    .horizontalAlignment(HorizontalAlignment.CENTER)
                    .verticalAlignment(VerticalAlignment.MIDDLE)
                    .build())
            .add(spaceRightCell(1, 1f, borderStyle, borderColor))
            .build())
        .addRow(Row.builder()
            .add(spaceLeftCell(1, 1f, borderStyle, borderColor))
            .add(spaceCell(1))
            .add(spaceRightCell(1, 1f, borderStyle, borderColor))
            .build())
        .addRow(Row.builder()
            .add(spaceLeftCell(10, 1f, borderStyle, borderColor))
            .add(spaceRightCell(1, 1f, borderStyle, borderColor))
            .build())
        .addRow(Row.builder()
            .add(spaceLeftCell(1, 1f, borderStyle, borderColor))
            .add(ImageCell.builder().image(dashboardDrawMaterial.getDocumentImage()).colSpan(1).rowSpan(2).build())
            .add(TextCell.builder()
                .text(String.format("%d개", documentCount))
                .textColor(countTextColor)
                .colSpan(2)
                .rowSpan(2)
                .build())
            .add(spaceCell(1))
            .add(tagImage == null
                ? TextCell.builder().text("").colSpan(4).rowSpan(2).build()
                : ImageCell.builder().image(tagImage).colSpan(4).rowSpan(2).scale(0.1f).build())
            .add(spaceCell(1))
            .add(spaceRightCell(1, 1f, borderStyle, borderColor))
            .build())
        .addRow(Row.builder()
            .add(spaceLeftCell(1, 1f, borderStyle, borderColor))
            .add(spaceCell(1))
            .add(spaceCell(1))
            .add(spaceRightCell(1, 1f, borderStyle, borderColor))
            .build())
        .addRow(spaceBottomRow(totalHeight, totalColSpan, 1f, borderStyle, borderColor))
        .build();

    return tableBuilder.build();
  }

  public TableBuilder generateBuilder(int xColCount, float xColSize, PDType0Font font, int fontSize) {
    float[] addColumnsOfWidthArr = new float[xColCount];
    for (int i = 0; i < xColCount - 1; i++) {
      addColumnsOfWidthArr[i] = xColSize;
    }
    addColumnsOfWidthArr[xColCount - 1] = xColSize / 2; // 마지막만 좀 작게

    return Table.builder()
        .addColumnsOfWidth(addColumnsOfWidthArr)
        .backgroundColor(Color.WHITE)
        .horizontalAlignment(HorizontalAlignment.LEFT)
        .verticalAlignment(VerticalAlignment.MIDDLE)
        .font(font)
        .fontSize(fontSize)
//        .borderWidth(1).borderStyle(BorderStyle.SOLID) // 디버깅용
        .padding(2);
  }

  private static float calculateStartX(DashboardDrawMaterial drawMaterial, float pageBorderMargin,
      int componentCountPerPage, int index) {
    return pageBorderMargin + drawMaterial.getComponentInterval() * (index % componentCountPerPage)
        + drawMaterial.getComponentWidth() * (index % componentCountPerPage);
  }
}
