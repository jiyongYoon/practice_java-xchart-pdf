package jy.practice.drawer.impl;

import static org.vandeseer.easytable.settings.HorizontalAlignment.CENTER;
import static org.vandeseer.easytable.settings.HorizontalAlignment.LEFT;
import static org.vandeseer.easytable.settings.VerticalAlignment.MIDDLE;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import jy.practice.drawer.Drawer;
import jy.practice.drawer.TitleDrawer;
import jy.practice.utils.store.FontStore;
import jy.practice.drawer.extension.PageCountRepeatedHeaderTableDrawer;
import jy.practice.drawer.material.impl.SapaCriteriaDepth1Element;
import jy.practice.drawer.material.impl.SapaCriteriaDepth2Element;
import jy.practice.drawer.material.impl.SapaCriteriaDrawMaterial;
import jy.practice.drawer.material.impl.TitleDrawMaterial;
import jy.practice.utils.factory.PDPageFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.Table.TableBuilder;
import org.vandeseer.easytable.structure.cell.TextCell;

// 페이지 넘어감 대응됨
public class SapaCriteriaDrawer implements Drawer<SapaCriteriaDrawMaterial>, TitleDrawer {

  public final static String SAPA_TITLE_VALUE = "1. 중대재해처벌법 의무 이행 기준";
  private final static int DEFAULT_PADDING = 5;

  @Override
  public int draw(PDDocument document, SapaCriteriaDrawMaterial drawMaterial, float pageBorderMargin) throws IOException {
    PDPage page = PDPageFactory.generateHorizontalPage();
    document.addPage(page);

    // 제목 그리기
    TitleDrawMaterial titleDrawMaterial = TitleDrawMaterial.builder()
        .page(page)
        .titleColumnsOfWidth(750)
        .titleFont(drawMaterial.getFont())
        .titleFontSize(FontStore.TITLE_FONT_SIZE)
        .titleTextColor(Color.BLACK)
        .borderColor(Color.WHITE)
        .titleName(SAPA_TITLE_VALUE)
        .build();

    drawTitle(document, titleDrawMaterial, pageBorderMargin);

    // 본문
    final TableBuilder tableBuilder = Table.builder()
        .addColumnsOfWidth(drawMaterial.getColumnsOfWidth())
        .font(drawMaterial.getFont())
        .fontSize(drawMaterial.getBodyFontSize())
        .borderColor(Color.WHITE)
        .padding(DEFAULT_PADDING);

    String[] headers = drawMaterial.getHeaderValue();

    Row header = Row.builder()
        .add(TextCell.builder().text(headers[0]).borderWidth(1).build())
        .add(TextCell.builder().text(headers[1]).borderWidth(1).build())
        .add(TextCell.builder().text(headers[2]).borderWidth(1).build())
        .add(TextCell.builder().text(headers[3]).borderWidth(1).build())
        .add(TextCell.builder().text(headers[4]).borderWidth(1).build())
        .backgroundColor(drawMaterial.getHeaderBackgroundColor())
        .textColor(drawMaterial.getHeaderTextColor())
        .font(drawMaterial.getBoldFont())
        .fontSize(drawMaterial.getTitleFontSize())
        .horizontalAlignment(CENTER)
        .build();

    tableBuilder.addRow(header);

    generateBodyTable(
        drawMaterial.getSapaCriteriaDepth1ElementList(),
        drawMaterial.getBoldFont(),
        drawMaterial.getFont(),
        tableBuilder,
        1);

    Table sapaCriteriaTable = tableBuilder.build();

    float startY = page.getMediaBox().getHeight() - pageBorderMargin;

    try (final PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, true, true)) {
      PageCountRepeatedHeaderTableDrawer tableDrawer = PageCountRepeatedHeaderTableDrawer.builder()
          .page(page)
          .table(sapaCriteriaTable)
          .contentStream(contentStream)
          .startX(pageBorderMargin)
          .startY(startY - TITLE_Y)
          .endY(pageBorderMargin) // 테이블이 이 Y 좌표에 도달하면 새 페이지를 시작합니다
          .build();

      tableDrawer.draw(
              () -> document,
              PDPageFactory::generateHorizontalPage,
              page.getMediaBox().getHeight() - (startY)); // 제목 공간 없이 위로 붙여서

      return tableDrawer.getPagesDrawn();
    }
  }

  private static void generateBodyTable(
      List<SapaCriteriaDepth1Element> sapaCriteriaDepth1ElementList,
      PDType0Font boldFont,
      PDType0Font font,
      TableBuilder tableBuilder,
      int rowIndex) {
    for (SapaCriteriaDepth1Element sapaCriteriaDepth1Element : sapaCriteriaDepth1ElementList) {
      int rowSpanSize = Math.max(1, sapaCriteriaDepth1Element.getSapaCriteriaDepth2ElementList().size());

      // 첫 번째 열 추가 (항목 이름)
      Row.RowBuilder firstRowBuilder = Row.builder()
          .add(TextCell.builder().text(sapaCriteriaDepth1Element.getName())
              .rowSpan(rowSpanSize)
              .lineSpacing(1f)
              .borderWidthTop(1)
              .textColor(Color.BLACK)
              .backgroundColor(sapaCriteriaDepth1Element.getFirstRowBackgroundColor())
              .fontSize(10)
              .font(boldFont)
              .borderWidth(1)
              .verticalAlignment(MIDDLE)
              .horizontalAlignment(LEFT)
              .build());

      // 요소들을 동적으로 추가
      for (int i = 0; i < sapaCriteriaDepth1Element.getSapaCriteriaDepth2ElementList().size(); i++) {
        SapaCriteriaDepth2Element element = sapaCriteriaDepth1Element.getSapaCriteriaDepth2ElementList().get(i);
        Color backgroundColor = (rowIndex++ % 2 == 0)
            ? sapaCriteriaDepth1Element.getRow1Color()
            : sapaCriteriaDepth1Element.getRow2Color();

        if (i == 0) {
          // 첫 번째 요소는 첫 번째 행에 추가
          firstRowBuilder
              .add(TextCell.builder().text(element.getName())
                  .borderWidth(1)
                  .backgroundColor(backgroundColor)
                  .verticalAlignment(MIDDLE)
                  .horizontalAlignment(LEFT)
                  .build())
              .add(TextCell.builder().text(element.getPeriod())
                  .borderWidth(1)
                  .backgroundColor(backgroundColor)
                  .build())
              .add(TextCell.builder()
                  .text(String.valueOf(element.getCount()))
                  .borderWidth(1)
                  .backgroundColor(backgroundColor)
                  .build())
              .add(TextCell.builder()
                  .text(String.valueOf(element.getValue()))
                  .borderWidth(1)
                  .backgroundColor(backgroundColor)
                  .build());

          tableBuilder.addRow(firstRowBuilder.build()).horizontalAlignment(CENTER).font(font);
        } else {
          // 나머지 요소들은 새로운 행으로 추가
          tableBuilder.addRow(
                  Row.builder()
                      .add(TextCell.builder().text(element.getName())
                          .borderWidth(1)
                          .backgroundColor(backgroundColor)
                          .verticalAlignment(MIDDLE)
                          .horizontalAlignment(LEFT)
                          .build())
                      .add(TextCell.builder().text(element.getPeriod())
                          .borderWidth(1)
                          .backgroundColor(backgroundColor)
                          .build())
                      .add(TextCell.builder()
                          .text(String.valueOf(element.getCount()))
                          .borderWidth(1)
                          .backgroundColor(backgroundColor)
                          .build())
                      .add(TextCell.builder()
                          .text(String.valueOf(element.getValue()))
                          .borderWidth(1)
                          .backgroundColor(backgroundColor)
                          .build())
                      .build())
              .horizontalAlignment(CENTER)
              .font(font);
        }
      }
    }
  }
}
