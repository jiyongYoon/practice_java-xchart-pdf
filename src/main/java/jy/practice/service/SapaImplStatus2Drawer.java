package jy.practice.service;

import static jy.practice.store.ColorStore.BLUE_DARK;
import static jy.practice.store.ColorStore.GRAY_LIGHT_1;
import static jy.practice.store.ColorStore.GRAY_LIGHT_2;

import java.awt.Color;
import java.io.IOException;
import java.util.Arrays;
import jy.practice.dto.material.BodyDrawMaterial;
import jy.practice.dto.material.SapaImplStatus2DrawMaterial;
import jy.practice.dto.material.Status2Depth0Element;
import jy.practice.dto.material.Status2Depth1Element;
import jy.practice.dto.material.Status2Depth2Element;
import jy.practice.dto.material.Status2Depth2FileTableElement;
import jy.practice.dto.material.StringValueFont;
import jy.practice.dto.material.TitleDrawMaterial;
import jy.practice.store.FontStore;
import jy.practice.store.PDPageStore;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.vandeseer.easytable.RepeatedHeaderTableDrawer;
import org.vandeseer.easytable.settings.HorizontalAlignment;
import org.vandeseer.easytable.settings.VerticalAlignment;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.Table.TableBuilder;
import org.vandeseer.easytable.structure.cell.TextCell;

public class SapaImplStatus2Drawer implements Drawer<SapaImplStatus2DrawMaterial>, TitleDrawer, BodyDrawer {

  private final static String SapaImplStatus2_TITLE_VALUE = "2. 중대재해처벌법 의무 이행 현황 - 세부(2/2)";
  private final static int DEFAULT_TABLE_PADDING = 5;

  @Override
  public void draw(PDDocument document, SapaImplStatus2DrawMaterial drawMaterial, float pageBorderMargin)
      throws IOException {

    PDPage page = PDPageStore.generateHorizontalPage();
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
        .titleName(SapaImplStatus2_TITLE_VALUE)
        .build();

    drawTitle(document, titleDrawMaterial, pageBorderMargin);

    for (Status2Depth0Element status2Depth0Element : drawMaterial.getStatus2Depth0ElementList()) {
      int largeCategoryIndex = drawMaterial.getStatus2Depth0ElementList().indexOf(status2Depth0Element);
      System.out.println("largeCategoryIndex = " + largeCategoryIndex);
      int largeBodyStartY = 40;
      if (largeCategoryIndex != 0) {
        // 두 번째 대분류부터 새로운 페이지 생성
        page = PDPageStore.generateHorizontalPage();
        document.addPage(page);
        largeBodyStartY = 0;
      }

      // 대분류
      BodyDrawMaterial largeCategoryBodyDrawMaterial = BodyDrawMaterial.builder()
          .page(page)
          .bodyColumnsOfWidth(pageWidth)
          .bodyFont(status2Depth0Element.getFont())
          .bodyFontSize(status2Depth0Element.getFontSize())
          .bodyTextColor(Color.BLACK)
          .borderColor(Color.WHITE)
          .bodyElementList(Arrays.asList(
              StringValueFont.builder()
                  .value(status2Depth0Element.getValue())
                  .font(status2Depth0Element.getFont())
                  .fontSize(status2Depth0Element.getFontSize())
                  .horizontalAlignment(HorizontalAlignment.LEFT)
                  .build()
          ))
          .build();

      drawBody(document, largeCategoryBodyDrawMaterial, pageBorderMargin, largeBodyStartY);

      // 소분류 및 파일테이블 세트 그리기 (새로운 소분류는 새로운 페이지에)
      for (Status2Depth1Element status2Depth1Element : status2Depth0Element.getStatus1Depth1ElementList()) {
        int smallCategoryIndex = status2Depth0Element.getStatus1Depth1ElementList().indexOf(status2Depth1Element);
        System.out.println("smallCategoryIndex = " + smallCategoryIndex);
        int smallBodyStartY = 40;
        if (smallCategoryIndex != 0) {
          // 두 번째 소분류부터 새로운 페이지 생성
          page = PDPageStore.generateHorizontalPage();
          document.addPage(page);
          largeBodyStartY = 0;
          smallBodyStartY = 0;
        }

        // 소분류
        BodyDrawMaterial smallCategoryBodyDrawMaterial = BodyDrawMaterial.builder()
            .page(page)
            .bodyColumnsOfWidth(pageWidth)
            .bodyFont(status2Depth1Element.getFont())
            .bodyFontSize(status2Depth1Element.getFontSize())
            .bodyTextColor(Color.BLACK)
            .borderColor(Color.WHITE)
            .bodyElementList(Arrays.asList(
                StringValueFont.builder()
                    .value(status2Depth1Element.getValue() + String.format(" (총 %3d 개)",
                        status2Depth1Element.getStatusDepth2Element().getStatus2Depth2FileTableElementList().size()))
                    .font(status2Depth1Element.getFont())
                    .fontSize(status2Depth1Element.getFontSize())
                    .horizontalAlignment(HorizontalAlignment.LEFT)
                    .build()
            ))
            .build();

        drawBody(document, smallCategoryBodyDrawMaterial, pageBorderMargin, largeBodyStartY + smallBodyStartY);

        // 파일테이블 세트 그리기
        Status2Depth2Element fileTableInfo = status2Depth1Element.getStatusDepth2Element();
        drawFileTable(document, page, fileTableInfo, largeBodyStartY, smallBodyStartY, pageBorderMargin);
      }
    }
  }

  private void drawFileTable(PDDocument document, PDPage page, Status2Depth2Element fileTableInfo,
      int largeBodyStartY, int smallBodyStartY, float pageBorderMargin) throws IOException {
    try (PDPageContentStream contentStream = new PDPageContentStream(document, page,
        AppendMode.APPEND, true, true)) {

      final TableBuilder tableBuilder = Table.builder()
          .addColumnsOfWidth(fileTableInfo.getColumnsOfWidth())
          .font(fileTableInfo.getTableFont())
          .fontSize(fileTableInfo.getTableFontSize())
          .borderColor(Color.WHITE)
          .borderWidth(1)
          .horizontalAlignment(HorizontalAlignment.CENTER)
          .verticalAlignment(VerticalAlignment.MIDDLE)
          .padding(DEFAULT_TABLE_PADDING);

      // 헤더 추가
      String[] headers = fileTableInfo.getHeaderValue();
      Row header = Row.builder()
          .add(TextCell.builder().text(headers[0]).build())
          .add(TextCell.builder().text(headers[1]).build())
          .add(TextCell.builder().text(headers[2]).build())
          .add(TextCell.builder().text(headers[3]).build())
          .add(TextCell.builder().text(headers[4]).build())
          .add(TextCell.builder().text(headers[5]).build())
          .backgroundColor(BLUE_DARK)
          .textColor(Color.WHITE)
          .font(fileTableInfo.getHeaderFont())
          .fontSize(fileTableInfo.getHeaderFontSize())
          .horizontalAlignment(HorizontalAlignment.CENTER)
          .build();
      tableBuilder.addRow(header);

      // 실제 데이터 추가
      int rowIndex = 0;
      for (Status2Depth2FileTableElement fileData : fileTableInfo.getStatus2Depth2FileTableElementList()) {
        Color backgroundColor = (++rowIndex % 2 == 0) ? GRAY_LIGHT_1 : GRAY_LIGHT_2;
        tableBuilder.addRow(Row.builder()
            .add(TextCell.builder().text(String.valueOf(rowIndex)).build())
            .add(TextCell.builder().text(fileData.getFileName()).horizontalAlignment(HorizontalAlignment.LEFT).build())
            .add(TextCell.builder().text(fileData.getLastUploadUser()).build())
            .add(TextCell.builder().text(fileData.getLastUploadDate()).build())
            .add(TextCell.builder().text(fileData.getClassifyDate()).build())
            .add(TextCell.builder().text(String.format("%dMB", Math.round(fileData.getSize()))).horizontalAlignment(HorizontalAlignment.RIGHT).build())
            .backgroundColor(backgroundColor)
            .build());
      }

      Table table = tableBuilder.build();

      float startY = page.getMediaBox().getHeight() - pageBorderMargin;
      int tableStartY = 40;
      RepeatedHeaderTableDrawer tableDrawer = RepeatedHeaderTableDrawer.builder()
          .contentStream(contentStream)
          .startX(pageBorderMargin)
          .startY(page.getMediaBox().getHeight() - pageBorderMargin - (largeBodyStartY + smallBodyStartY + tableStartY))
          .endY(pageBorderMargin)
          .table(table)
          .build();
      tableDrawer.draw(
          () -> document,
          PDPageStore::generateHorizontalPage,
          page.getMediaBox().getHeight() - (startY)); // 추가페이지는 제목 없이 테이블만
    }
  }
}
