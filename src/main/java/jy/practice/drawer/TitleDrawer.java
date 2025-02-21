package jy.practice.drawer;

import java.io.IOException;
import jy.practice.drawer.material.TitleDrawMaterial;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.vandeseer.easytable.TableDrawer;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.Table.TableBuilder;
import org.vandeseer.easytable.structure.cell.TextCell;

// 현재는 Title은 페이지가 넘어가는것을 가정하지 않음
// 넘어가면 TableDrawer 대신 extension.PageCountTableDrawer를 활용하여 count도 추가해주어야하며 draw() -> 에 데이터도 넘겨야 함
public interface TitleDrawer {
  float TITLE_Y = 50f; // title 높이 폭 -> 아래 컴포넌트의 startY에서 해당 값을 추가로 빼줘야함

  default void drawTitle(PDDocument document, TitleDrawMaterial drawMaterial, float pageBorderMargin) throws IOException {
    PDPage page = drawMaterial.getPage();

    final TableBuilder titleTableBuilder = Table.builder()
        .addColumnsOfWidth(drawMaterial.getTitleColumnsOfWidth())
        .font(drawMaterial.getTitleFont())
        .fontSize(drawMaterial.getTitleFontSize())
        .textColor(drawMaterial.getTitleTextColor())
        .borderColor(drawMaterial.getBorderColor())
        .backgroundColor(drawMaterial.getBackgroundColor());

    titleTableBuilder.addRow(Row.builder()
        .add(TextCell.builder().text(drawMaterial.getTitleName()).build()).build());

    Table titleTable = titleTableBuilder.build();

    try (final PDPageContentStream contentStream = new PDPageContentStream(document, page,
        AppendMode.APPEND, true, true)) {
      TableDrawer.builder()
          .page(page)
          .table(titleTable)
          .contentStream(contentStream)
          .startX(pageBorderMargin)
          .startY(page.getMediaBox().getHeight() - pageBorderMargin)
          .endY(pageBorderMargin) // 테이블이 이 Y 좌표에 도달하면 새 페이지를 시작합니다
          .build()
          .draw();
    }
  }
}
