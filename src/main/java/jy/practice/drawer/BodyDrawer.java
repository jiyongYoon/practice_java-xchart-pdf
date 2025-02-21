package jy.practice.drawer;

import java.io.IOException;
import jy.practice.drawer.material.BodyDrawMaterial;
import jy.practice.drawer.material.StringValueFont;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.vandeseer.easytable.TableDrawer;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.Table.TableBuilder;
import org.vandeseer.easytable.structure.cell.TextCell;

// 현재는 Body는 페이지가 넘어가는것을 가정하지 않음
// 넘어가면 PageCountTableDrawer를 활용하여 count도 추가해주어야하며 draw() -> 에 데이터도 넘겨야 함
public interface BodyDrawer {

  default void drawBody(PDDocument document, BodyDrawMaterial drawMaterial, float pageBorderMargin, float extraStartY)
      throws IOException {
    PDPage page = drawMaterial.getPage();

    final TableBuilder bodyTableBuilder = Table.builder()
        .addColumnsOfWidth(drawMaterial.getBodyColumnsOfWidth())
        .font(drawMaterial.getBodyFont())
        .fontSize(drawMaterial.getBodyFontSize())
        .textColor(drawMaterial.getBodyTextColor())
        .borderColor(drawMaterial.getBorderColor())
//        .borderColor(Color.BLACK).borderWidth(1f) // 디버깅용
        .backgroundColor(drawMaterial.getBackgroundColor());

    for (StringValueFont stringValueFont : drawMaterial.getBodyElementList()) {
      bodyTableBuilder.addRow(Row.builder()
          .add(TextCell.builder()
              .text(stringValueFont.getValue())
              .font(getFont(drawMaterial, stringValueFont))
              .fontSize(getFontSize(drawMaterial, stringValueFont))
              .horizontalAlignment(stringValueFont.getHorizontalAlignment())
              .verticalAlignment(stringValueFont.getVerticalAlignment())
              .build())
          .build());
    }

    Table bodyTable = bodyTableBuilder.build();

    try (final PDPageContentStream contentStream = new PDPageContentStream(document, page,
        AppendMode.APPEND, true, true)) {
      TableDrawer.builder()
          .page(page)
          .table(bodyTable)
          .contentStream(contentStream)
          .startX(pageBorderMargin)
          .startY(page.getMediaBox().getHeight() - pageBorderMargin - extraStartY)
          .endY(pageBorderMargin) // 테이블이 이 Y 좌표에 도달하면 새 페이지를 시작합니다
          .build()
          .draw();
    }
  }

  default PDType0Font getFont(BodyDrawMaterial drawMaterial, StringValueFont stringValueFont) {
    return stringValueFont.getFont() == null
        ? drawMaterial.getBodyFont()
        : stringValueFont.getFont();
  }

  default int getFontSize(BodyDrawMaterial drawMaterial, StringValueFont stringValueFont) {
    return stringValueFont.getFontSize() == 0
        ? drawMaterial.getBodyFontSize()
        : stringValueFont.getFontSize();
  }

}
