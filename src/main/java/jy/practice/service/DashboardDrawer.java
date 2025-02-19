package jy.practice.service;

import static jy.practice.store.ColorStore.GRAY_DARK;
import static jy.practice.store.ColorStore.GRAY_LIGHT_3;

import java.awt.Color;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import jy.practice.dto.material.BodyDrawMaterial;
import jy.practice.dto.material.DashboardComponentElement;
import jy.practice.dto.material.DashboardDrawMaterial;
import jy.practice.dto.material.StringValueFont;
import jy.practice.dto.material.TitleDrawMaterial;
import jy.practice.store.FontStore;
import jy.practice.store.PDPageStore;
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

public class DashboardDrawer implements Drawer<DashboardDrawMaterial>, TitleDrawer, BodyDrawer {

  private final static String DASHBOARD_TITLE_VALUE = "2. 중대재해처벌법 의무 이행 현황 - 요약";

  @Override
  public void draw(PDDocument document, DashboardDrawMaterial drawMaterial, float pageBorderMargin)
      throws IOException {

    BorderStyle borderStyle = BorderStyle.SOLID;
    Color borderColor = GRAY_LIGHT_3;
    int componentCountPerPage = 4; // 한 페이지에 가로로 component 4개씩

    final PDPage page = PDPageStore.generateHorizontalPage();
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


    int componentIndex = 0;
    float nextY = TITLE_Y + bodyStartY;

    // 대시보드 그리기
    for (DashboardComponentElement dashboardComponentElement : drawMaterial.getDashboardComponentElementList()) {
      componentIndex = drawMaterial.getDashboardComponentElementList().indexOf(dashboardComponentElement);
      try (PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, true, true)) {
        Table table = generate(
            drawMaterial,
            borderStyle,
            borderColor,
            dashboardComponentElement.getTitle(),
            dashboardComponentElement.getOkCount(),
            dashboardComponentElement.getChartImage(),
            dashboardComponentElement.getTagImage());
        TableDrawer tableDrawer = TableDrawer.builder()
            .contentStream(contentStream)
            .startX(calculateStartX(drawMaterial, pageBorderMargin, componentCountPerPage, componentIndex))
            .startY(calculateStartY(drawMaterial, pageBorderMargin, page, componentIndex, nextY))
            .table(table)
            .build();
        tableDrawer.draw();
      }
    }

    // 마지막에 미분류파일 그리기
    componentIndex++;
    try (PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, true, true)) {
      Table unclassifiedTable = generate(
          drawMaterial,
          borderStyle,
          borderColor,
          drawMaterial.getUnclassifiedElement().getTitle(),
          drawMaterial.getUnclassifiedElement().getOkCount(),
          drawMaterial.getUnclassifiedChartImage(),
          null);
      TableDrawer unclassifiedTableDrawer = TableDrawer.builder()
          .contentStream(contentStream)
          .startX(calculateStartX(drawMaterial, pageBorderMargin, componentCountPerPage,componentIndex))
          .startY(calculateStartY(drawMaterial, pageBorderMargin, page, componentIndex, nextY))
          .table(unclassifiedTable)
          .build();
      unclassifiedTableDrawer.draw();
    }
  }

  private static float calculateStartY(DashboardDrawMaterial drawMaterial, float pageBorderMargin,
      PDPage pdPage, int componentIndex, float titleY) {
    return pdPage.getMediaBox().getUpperRightY() - pageBorderMargin - (
        drawMaterial.getComponentInterval() * 8 * (componentIndex / 4) // 정수부만 활용
            + drawMaterial.getComponentHeight() * (componentIndex / 4))
        - titleY;
  }

  private Table generate(
      DashboardDrawMaterial dashboardDrawMaterial,
      BorderStyle borderStyle,
      Color borderColor,
      String title,
      int documentCount,
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
                .textColor(GRAY_DARK)
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

  private TextCell spaceCell(int colSpan) {
    return TextCell.builder().text("").colSpan(colSpan).build();
  }

  private TextCell spaceLeftCell(int colSpan, float borderWidth, BorderStyle borderStyle, Color borderColor) {
    return TextCell.builder().text("").colSpan(colSpan).borderWidthLeft(borderWidth).borderStyleLeft(borderStyle).borderColor(borderColor).build();
  }

  private TextCell spaceRightCell(int colSpan, float borderWidth, BorderStyle borderStyle, Color borderColor) {
    return TextCell.builder().text("").colSpan(colSpan).borderWidthRight(borderWidth).borderStyleRight(borderStyle).borderColor(borderColor).build();
  }

  private Row spaceRow(float height, int colSpan) {
    return Row.builder().add(TextCell.builder().text("").colSpan(colSpan).build()).height(height).build();
  }

  private Row spaceTopRow(float height, int colSpan, float borderWidth, BorderStyle borderStyle, Color borderColor) {
    return Row.builder()
        .add(TextCell.builder().text("").colSpan(1).borderWidthLeft(borderWidth).borderWidthTop(borderWidth).borderStyleLeft(borderStyle).borderStyleTop(borderStyle).build())
        .add(TextCell.builder().text("").colSpan(colSpan - 2).borderWidthTop(borderWidth).borderStyleTop(borderStyle).build())
        .add(TextCell.builder().text("").colSpan(1).borderWidthRight(borderWidth).borderWidthTop(borderWidth).borderStyleRight(borderStyle).borderStyleTop(borderStyle).build())
        .borderColor(borderColor)
        .height(height).build();
  }

  private Row spaceBottomRow(float height, int colSpan, float borderWidth, BorderStyle borderStyle, Color borderColor) {
    return Row.builder()
        .add(TextCell.builder().text("").colSpan(1).borderWidthLeft(borderWidth).borderWidthBottom(borderWidth).borderStyleLeft(borderStyle).borderStyleBottom(borderStyle).build())
        .add(TextCell.builder().text("").colSpan(colSpan - 2).borderWidthBottom(borderWidth).borderStyleBottom(borderStyle).build())
        .add(TextCell.builder().text("").colSpan(1).borderWidthRight(borderWidth).borderWidthBottom(borderWidth).borderStyleRight(borderStyle).borderStyleBottom(borderStyle).build())
        .borderColor(borderColor)
        .height(height).build();
  }

  private static float calculateStartX(DashboardDrawMaterial drawMaterial, float pageBorderMargin,
      int componentCountPerPage, int index) {
    return pageBorderMargin + drawMaterial.getComponentInterval() * (index % componentCountPerPage)
        + drawMaterial.getComponentWidth() * (index % componentCountPerPage);
  }
}
