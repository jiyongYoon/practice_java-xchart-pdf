package jy.practice.service;

import static jy.practice.store.ColorStore.BLUE_DARK;
import static jy.practice.store.ColorStore.GRAY_LIGHT_1;
import static jy.practice.store.ColorStore.GRAY_LIGHT_2;
import static jy.practice.store.ColorStore.GRAY_LIGHT_3;
import static org.vandeseer.easytable.settings.HorizontalAlignment.CENTER;
import static org.vandeseer.easytable.settings.HorizontalAlignment.LEFT;
import static org.vandeseer.easytable.settings.VerticalAlignment.MIDDLE;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import jy.practice.component.test.dto.Element;
import jy.practice.component.test.dto.SafetyHealthItem;
import jy.practice.dto.material.SapaCriteriaDrawMaterial;
import jy.practice.dto.material.TitleDrawMaterial;
import jy.practice.store.FontStore;
import jy.practice.store.PDPageStore;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.vandeseer.easytable.RepeatedHeaderTableDrawer;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.Table.TableBuilder;
import org.vandeseer.easytable.structure.cell.TextCell;

public class SapaCriteriaDrawer implements Drawer<SapaCriteriaDrawMaterial>, TitleDrawer {

  private final static String SAPA_TITLE_VALUE = "1. 중대재해처벌법 의무 이행 기준";
  private final static int DEFAULT_PADDING = 5;

  @Override
  public void draw(PDDocument document, SapaCriteriaDrawMaterial drawMaterial, float pageBorderMargin) throws IOException {
    PDPage page = PDPageStore.generateHorizontalPage();
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
        .backgroundColor(BLUE_DARK)
        .textColor(Color.WHITE)
        .font(drawMaterial.getBoldFont())
        .fontSize(drawMaterial.getTitleFontSize())
        .horizontalAlignment(CENTER)
        .build();

    tableBuilder.addRow(header);

    generateBodyTable(
        drawMaterial.getSafetyHealthItemList(),
        FontStore.getPdBoldFont(),
        FontStore.getPdFont(),
        tableBuilder,
        1);

    Table sapaCriteriaTable = tableBuilder.build();

    float startY = page.getMediaBox().getHeight() - pageBorderMargin;

    try (final PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, true, true)) {
      RepeatedHeaderTableDrawer.builder()
          .page(page)
          .table(sapaCriteriaTable)
          .contentStream(contentStream)
          .startX(pageBorderMargin)
          .startY(startY - TITLE_Y)
          .endY(pageBorderMargin) // 테이블이 이 Y 좌표에 도달하면 새 페이지를 시작합니다
          .build()
          .draw(
              () -> document,
              PDPageStore::generateHorizontalPage,
              page.getMediaBox().getHeight() - (startY - TITLE_Y));
    }
  }

  private static void generateBodyTable(
      List<SafetyHealthItem> safetyHealthItems,
      PDType0Font boldFont,
      PDType0Font font,
      TableBuilder tableBuilder,
      int rowIndex) {
    for (SafetyHealthItem safetyHealthItem : safetyHealthItems) {
      int rowSpanSize = Math.max(1, safetyHealthItem.getElements().size());

      // 첫 번째 열 추가 (항목 이름)
      Row.RowBuilder firstRowBuilder = Row.builder()
          .add(TextCell.builder().text(safetyHealthItem.getName())
              .rowSpan(rowSpanSize)
              .lineSpacing(1f)
              .borderWidthTop(1)
              .textColor(Color.BLACK)
              .backgroundColor(GRAY_LIGHT_3)
              .fontSize(10)
              .font(boldFont)
              .borderWidth(1)
              .verticalAlignment(MIDDLE)
              .horizontalAlignment(LEFT)
              .build());

      // 요소들을 동적으로 추가
      for (int i = 0; i < safetyHealthItem.getElements().size(); i++) {
        Element element = safetyHealthItem.getElements().get(i);
        Color backgroundColor = (rowIndex++ % 2 == 0) ? GRAY_LIGHT_1 : GRAY_LIGHT_2;

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
