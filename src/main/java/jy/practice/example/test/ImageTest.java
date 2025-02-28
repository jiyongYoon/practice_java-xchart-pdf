package jy.practice.example.test;

import static org.vandeseer.easytable.settings.HorizontalAlignment.CENTER;
import static org.vandeseer.easytable.settings.VerticalAlignment.MIDDLE;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Objects;
import jy.practice.Main;
import jy.practice.utils.factory.PDImageFactory;
import jy.practice.utils.store.FontStore;
import jy.practice.utils.factory.PDPageFactory;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.vandeseer.easytable.TableDrawer;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.Table.TableBuilder;
import org.vandeseer.easytable.structure.cell.ImageCell;
import org.vandeseer.easytable.structure.cell.TextCell;

public class ImageTest {

  private static final PDDocument PD_DOCUMENT_FOR_IMAGES = new PDDocument();

  public static final PDRectangle horizontalPdRectangle = new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth());

  public static void main(String[] args) throws IOException {
    float PADDING = 50f;

    try (PDDocument document = new PDDocument()) {
      PDPage page = PDPageFactory.generateHorizontalPage();
      document.addPage(page);

      String logoFilePath = "example/logo/SafelyLogo.be87d102.png";

      byte[] logoByteArray = IOUtils.toByteArray(Objects.requireNonNull(
          Main.class.getClassLoader().getResourceAsStream(logoFilePath)));

      PDImageXObject logoImage = PDImageFactory.generate(logoByteArray, logoFilePath.split("\\.")[0]);

      PDType0Font font = FontStore.getPdFont(document);
      PDType0Font boldFont = FontStore.getPdBoldFont(document);

      final TableBuilder tableBuilder = Table.builder()
          .addColumnsOfWidth(750)
          .fontSize(8)
          .font(boldFont)
          .backgroundColor(Color.WHITE)
//          .borderColor(Color.WHITE)
          .borderColor(Color.BLACK)
          .horizontalAlignment(CENTER)
          .verticalAlignment(MIDDLE);
//          .padding(15f);

      tableBuilder.addRow(
          Row.builder()
              .add(ImageCell.builder().image(logoImage).scale(0.4f).build())
              .build())
          .addRow(spaceRow(30f))
          .addRow(
              Row.builder()
                  .add(TextCell.builder().text("중대재해처벌법").build())
                  .font(boldFont)
                  .fontSize(45)
                  .build())
          .addRow(spaceRow(3f))
          .addRow(
              Row.builder()
                  .add(TextCell.builder().text("의무 이행 보고서").build())
                  .font(boldFont)
                  .fontSize(45)
                  .build())
          .addRow(spaceRow(60f))
          .addRow(
              Row.builder()
                  .add(TextCell.builder().text("위즈코어").build())
                  .font(boldFont)
                  .fontSize(20)
                  .build())
          .addRow(
              Row.builder()
                  .add(TextCell.builder().text("[2차] 2024-01-01 ~ 2024-12-31").build())
                  .font(font)
                  .fontSize(20)
                  .build())
          .addRow(spaceRow(100f))
          .addRow(
              Row.builder()
                  .add(TextCell.builder().text(LocalDate.now().toString()).build())
                  .font(font)
                  .fontSize(15)
                  .build()
          );

      Table myTable = tableBuilder.build();

      float startY = page.getMediaBox().getHeight() - PADDING;

      try (final PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
        TableDrawer.builder()
            .page(page)
            .contentStream(contentStream)
            .table(myTable)
            .startX(PADDING)
            .startY(startY)
            .endY(PADDING)
            .build()
            .draw(() -> document, PDPageFactory::generateHorizontalPage, PADDING);
      }

      document.save(new File("./outputs/image-test.pdf"));
    }
  }

  private static Row spaceRow(float height) {
    return Row.builder().add(TextCell.builder().text("").build()).height(height).build();
  }

}
