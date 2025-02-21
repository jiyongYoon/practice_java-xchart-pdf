package jy.practice.drawer.impl;

import java.io.IOException;
import jy.practice.drawer.Drawer;
import jy.practice.drawer.material.impl.PageDrawMaterial;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;

public class PageNumberDrawer implements Drawer<PageDrawMaterial> {

  @Override
  public int draw(PDDocument document, PageDrawMaterial drawMaterial, float pageBorderMargin) throws IOException {
    PDAcroForm acroForm = new PDAcroForm(document);
    document.getDocumentCatalog().setAcroForm(acroForm);

    for (PDPage page : document.getPages()) {
      if (document.getPages().indexOf(page) == 0) {
        continue;
      }

      PDRectangle mediaBox = page.getMediaBox();
      String pageNumber = String.valueOf(document.getPages().indexOf(page));
      float textWidth = drawMaterial.getFont().getStringWidth(pageNumber) / 1000 * drawMaterial.getFontSize();
      float xPosition = drawMaterial.getX() == 0 ? (mediaBox.getWidth() - textWidth) / 2 : drawMaterial.getX();

      PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, true, true);
      contentStream.beginText();
      contentStream.setFont(drawMaterial.getFont(), drawMaterial.getFontSize());
      contentStream.newLineAtOffset(xPosition, drawMaterial.getY());
      contentStream.showText(pageNumber);
      contentStream.endText();
      contentStream.close();
    }

    return document.getNumberOfPages();
  }

  public int drawWithTitle(PDDocument document, PageDrawMaterial drawMaterial, float titlePageNumberInterval) throws IOException {
    PDAcroForm acroForm = new PDAcroForm(document);
    document.getDocumentCatalog().setAcroForm(acroForm);

    for (PDPage page : document.getPages()) {
      if (document.getPages().indexOf(page) == 0) {
        continue;
      }

      StringBuilder space = new StringBuilder();
      for (int i = 0; i < titlePageNumberInterval; i++) {
        space.append(" ");
      }

      PDRectangle mediaBox = page.getMediaBox();
      int pageNumber = document.getPages().indexOf(page);
      String TitleWithPageNumber =
          String.format("%s%330s%s", drawMaterial.getTitleList()[pageNumber - 1], " ", String.valueOf(pageNumber));
      float textWidth = drawMaterial.getFont().getStringWidth(TitleWithPageNumber) / 1000 * drawMaterial.getFontSize();
      float xPosition = drawMaterial.getX() == 0 ? (mediaBox.getWidth() - textWidth) / 2 : drawMaterial.getX();

      PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND, true, true);
      contentStream.beginText();
      contentStream.setFont(drawMaterial.getFont(), drawMaterial.getFontSize());
      contentStream.newLineAtOffset(xPosition, drawMaterial.getY());
      contentStream.showText(TitleWithPageNumber);
      contentStream.endText();
      contentStream.close();
    }

    return document.getNumberOfPages();
  }
}
