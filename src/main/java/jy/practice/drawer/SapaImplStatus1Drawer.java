package jy.practice.drawer;

import static org.vandeseer.easytable.settings.HorizontalAlignment.CENTER;
import static org.vandeseer.easytable.settings.HorizontalAlignment.LEFT;
import static org.vandeseer.easytable.settings.VerticalAlignment.MIDDLE;

import java.awt.Color;
import java.io.IOException;
import java.util.List;
import jy.practice.utils.store.FontStore;
import jy.practice.drawer.extension.PageCountRepeatedHeaderTableDrawer;
import jy.practice.drawer.material.SapaImplStatus1DrawMaterial;
import jy.practice.drawer.material.Status1Depth1Element;
import jy.practice.drawer.material.Status1Depth2Element;
import jy.practice.drawer.material.TitleDrawMaterial;
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
public class SapaImplStatus1Drawer implements Drawer<SapaImplStatus1DrawMaterial>, TitleDrawer {

  public final static String SapaImplStatus1_TITLE_VALUE = "2. 중대재해처벌법 의무 이행 현황 - 세부(1/2)";
  private final static int DEFAULT_TABLE_PADDING = 5;

  @Override
  public int draw(PDDocument document, SapaImplStatus1DrawMaterial drawMaterial, float pageBorderMargin) throws IOException {
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
        .titleName(SapaImplStatus1_TITLE_VALUE)
        .build();

    drawTitle(document, titleDrawMaterial, pageBorderMargin);

    // 본문
    final TableBuilder tableBuilder = Table.builder()
        .addColumnsOfWidth(drawMaterial.getColumnsOfWidth())
        .font(drawMaterial.getFont())
        .fontSize(drawMaterial.getBodyFontSize())
        .borderColor(Color.WHITE)
        .borderWidth(1)
        .padding(DEFAULT_TABLE_PADDING);

    String[] headers = drawMaterial.getHeaderValue();

    Row header = Row.builder()
        .add(TextCell.builder().text(headers[0]).borderWidth(1).build())
        .add(TextCell.builder().text(headers[1]).borderWidth(1).build())
        .add(TextCell.builder().text(headers[2]).borderWidth(1).build())
        .add(TextCell.builder().text(headers[3]).borderWidth(1).build())
        .add(TextCell.builder().text(headers[4]).borderWidth(1).build())
        .add(TextCell.builder().text(headers[5]).borderWidth(1).build())
        .backgroundColor(drawMaterial.getHeaderBackgroundColor())
        .textColor(drawMaterial.getHeaderTextColor())
        .font(drawMaterial.getBoldFont())
        .fontSize(drawMaterial.getHeaderFontSize())
        .horizontalAlignment(CENTER)
        .build();

    tableBuilder.addRow(header);

    generateBodyTable(
        drawMaterial.getStatus1Depth1ElementList(),
        drawMaterial.getBoldFont(),
        drawMaterial.getFont(),
        tableBuilder,
        1);

    Table sapaCriteriaTable = tableBuilder.build();

    float startY = page.getMediaBox().getHeight() - pageBorderMargin;

    try (final PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, true, true)) {
      PageCountRepeatedHeaderTableDrawer pageCountRepeatedHeaderTableDrawer = PageCountRepeatedHeaderTableDrawer.builder()
          .page(page)
          .table(sapaCriteriaTable)
          .contentStream(contentStream)
          .startX(pageBorderMargin)
          .startY(startY - TITLE_Y)
          .endY(pageBorderMargin) // 테이블이 이 Y 좌표에 도달하면 새 페이지를 시작합니다
          .build();

      pageCountRepeatedHeaderTableDrawer.draw(
          () -> document,
          PDPageFactory::generateHorizontalPage,
          page.getMediaBox().getHeight() - (startY));

      return pageCountRepeatedHeaderTableDrawer.getPagesDrawn();
    }
  }

  private static void generateBodyTable(
      List<Status1Depth1Element> status1Depth1ElementList,
      PDType0Font boldFont,
      PDType0Font font,
      TableBuilder tableBuilder,
      int rowIndex) {
    for (Status1Depth1Element status1Depth1Element : status1Depth1ElementList) {
      int rowSpanSize = Math.max(1, status1Depth1Element.getStatusDept2ElementList().size());

      // 첫 번째 열 추가 (항목 이름)
      Row.RowBuilder firstRowBuilder = Row.builder()
          .add(TextCell.builder().text(status1Depth1Element.getName())
              .rowSpan(rowSpanSize)
              .lineSpacing(1f)
              .borderWidthTop(1)
              .textColor(Color.BLACK)
              .backgroundColor(status1Depth1Element.getFirstRowBackgroundColor())
              .fontSize(10)
              .font(boldFont)
              .borderWidth(1)
              .verticalAlignment(MIDDLE)
              .horizontalAlignment(LEFT)
              .build());

      // 요소들을 동적으로 추가
      for (int i = 0; i < status1Depth1Element.getStatusDept2ElementList().size(); i++) {
        Status1Depth2Element status1Depth2Element = status1Depth1Element.getStatusDept2ElementList()
            .get(i);
        Color backgroundColor = (rowIndex++ % 2 == 0)
            ? status1Depth1Element.getRow1Color()
            : status1Depth1Element.getRow2Color();

        if (i == 0) {
          // 첫 번째 요소는 첫 번째 행에 추가
          firstRowBuilder
              .add(TextCell.builder().text(status1Depth2Element.getCorrespondenceDocument())
                  .backgroundColor(backgroundColor)
                  .verticalAlignment(MIDDLE)
                  .horizontalAlignment(LEFT)
                  .build())
              .add(TextCell.builder().text(status1Depth2Element.getLastUploadUser())
                  .backgroundColor(backgroundColor)
                  .build())
              .add(TextCell.builder()
                  .text(String.valueOf(status1Depth2Element.getLastUploadDate()))
                  .backgroundColor(backgroundColor)
                  .build())
              .add(TextCell.builder()
                  .text(String.valueOf(status1Depth2Element.getUploadDestination()))
                  .backgroundColor(backgroundColor)
                  .build())
              .add(TextCell.builder()
                  .text(String.valueOf(status1Depth2Element.getAchievementRate()) + "%")
                  .backgroundColor(backgroundColor)
                  .build());

          tableBuilder.addRow(firstRowBuilder.build()).horizontalAlignment(CENTER).font(font);
        } else {
          // 나머지 요소들은 새로운 행으로 추가
          tableBuilder.addRow(
                  Row.builder()
                      .add(TextCell.builder().text(status1Depth2Element.getCorrespondenceDocument())
                          .backgroundColor(backgroundColor)
                          .verticalAlignment(MIDDLE)
                          .horizontalAlignment(LEFT)
                          .build())
                      .add(TextCell.builder().text(status1Depth2Element.getLastUploadUser())
                          .backgroundColor(backgroundColor)
                          .build())
                      .add(TextCell.builder()
                          .text(String.valueOf(status1Depth2Element.getLastUploadDate()))
                          .backgroundColor(backgroundColor)
                          .build())
                      .add(TextCell.builder()
                          .text(String.valueOf(status1Depth2Element.getUploadDestination()))
                          .backgroundColor(backgroundColor)
                          .build())
                      .add(TextCell.builder()
                          .text(String.valueOf(status1Depth2Element.getAchievementRate()) + "%")
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
