package jy.practice.drawer.impl;

import static org.vandeseer.easytable.settings.HorizontalAlignment.CENTER;
import static org.vandeseer.easytable.settings.VerticalAlignment.MIDDLE;

import java.awt.Color;
import java.io.IOException;
import jy.practice.drawer.Drawer;
import jy.practice.utils.factory.RowFactory;
import jy.practice.drawer.material.impl.CoverDrawMaterial;
import jy.practice.drawer.extension.PageCountTableDrawer;
import jy.practice.utils.store.FontStore;
import jy.practice.utils.factory.PDPageFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.Table.TableBuilder;
import org.vandeseer.easytable.structure.cell.ImageCell;
import org.vandeseer.easytable.structure.cell.TextCell;

public class CoverDrawer implements Drawer<CoverDrawMaterial> {

  private static final int COLUMNS_OF_WIDTH = 750;
  private static final int DEFAULT_FONT_SIZE = 8;


  @Override
  public int draw(PDDocument document, CoverDrawMaterial coverDrawMaterial, float pageBorderMargin) throws IOException {
    PDPage page = PDPageFactory.generateHorizontalPage();
    document.addPage(page);

    final TableBuilder tableBuilder = Table.builder()
        .addColumnsOfWidth(COLUMNS_OF_WIDTH)
        .fontSize(DEFAULT_FONT_SIZE)
        .font(FontStore.getPdBoldFont(document))
        .backgroundColor(Color.WHITE)
          .borderColor(Color.WHITE)
//        .borderColor(Color.BLACK).borderWidth(1) // 디버깅용
        .horizontalAlignment(CENTER)
        .verticalAlignment(MIDDLE)
        .padding(5f) // 표안에서의 padding
        ;

    tableBuilder
        .addRow(
            Row.builder()
                .add(ImageCell.builder().image(coverDrawMaterial.getLogoImage()).scale(0.4f).build())
                .build())
        .addRow(RowFactory.spaceRow(50f))
        .addRow(
            Row.builder()
                .add(TextCell.builder().text(coverDrawMaterial.getTitle().getValue()).build())
                .font(coverDrawMaterial.getTitle().getFont())
                .fontSize(coverDrawMaterial.getTitle().getFontSize())
                .horizontalAlignment(coverDrawMaterial.getTitle().getHorizontalAlignment())
                .verticalAlignment(coverDrawMaterial.getTitle().getVerticalAlignment())
                .build())
        .addRow(RowFactory.spaceRow(10f))
        .addRow(
            Row.builder()
                .add(TextCell.builder().text(coverDrawMaterial.getTitle2().getValue()).build())
                .font(coverDrawMaterial.getTitle2().getFont())
                .fontSize(coverDrawMaterial.getTitle2().getFontSize())
                .horizontalAlignment(coverDrawMaterial.getTitle2().getHorizontalAlignment())
                .verticalAlignment(coverDrawMaterial.getTitle2().getVerticalAlignment())
                .build())
        .addRow(RowFactory.spaceRow(80f))
        .addRow(
            Row.builder()
                .add(TextCell.builder().text(coverDrawMaterial.getCompanyName().getValue()).build())
                .font(coverDrawMaterial.getCompanyName().getFont())
                .fontSize(coverDrawMaterial.getCompanyName().getFontSize())
                .horizontalAlignment(coverDrawMaterial.getCompanyName().getHorizontalAlignment())
                .verticalAlignment(coverDrawMaterial.getCompanyName().getVerticalAlignment())
                .build())
        .addRow(RowFactory.spaceRow(10f))
        .addRow(
            Row.builder()
                .add(TextCell.builder().text(coverDrawMaterial.getPeriod().getValue()).build())
                .font(coverDrawMaterial.getPeriod().getFont())
                .fontSize(coverDrawMaterial.getPeriod().getFontSize())
                .horizontalAlignment(coverDrawMaterial.getPeriod().getHorizontalAlignment())
                .verticalAlignment(coverDrawMaterial.getPrintDate().getVerticalAlignment())
                .build())
        .addRow(RowFactory.spaceRow(130f))
        .addRow(
            Row.builder()
                .add(TextCell.builder().text(coverDrawMaterial.getPrintDate().getValue()).build())
                .font(coverDrawMaterial.getPrintDate().getFont())
                .fontSize(coverDrawMaterial.getPrintDate().getFontSize())
                .horizontalAlignment(coverDrawMaterial.getPrintDate().getHorizontalAlignment())
                .verticalAlignment(coverDrawMaterial.getPrintDate().getVerticalAlignment())
                .build()
    );

    Table coverTable = tableBuilder.build();

    float startY = page.getMediaBox().getHeight() - pageBorderMargin;

    try (final PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
      PageCountTableDrawer tableDrawer = PageCountTableDrawer.builder()
          .page(page)
          .contentStream(contentStream)
          .table(coverTable)
          .startX(pageBorderMargin)
          .startY(startY)
          .endY(pageBorderMargin)
          .build();
      tableDrawer.draw();
      return tableDrawer.getPagesDrawn();
    }
  }

//  @Override
  public void close(PDDocument document) throws IOException {
    document.close();
  }
}
