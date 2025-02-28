package jy.practice.example.test;

import static org.vandeseer.easytable.settings.HorizontalAlignment.CENTER;
import static org.vandeseer.easytable.settings.HorizontalAlignment.LEFT;
import static org.vandeseer.easytable.settings.VerticalAlignment.MIDDLE;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import jy.practice.utils.store.FontStore;
import jy.practice.utils.factory.PDPageFactory;
import jy.practice.example.dto.Element;
import jy.practice.example.dto.SafetyHealthItem;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.vandeseer.easytable.RepeatedHeaderTableDrawer;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.Table.TableBuilder;
import org.vandeseer.easytable.structure.cell.TextCell;

public class TableTest {

  private final static Color BLUE_DARK = new Color(76, 129, 190);
  private final static Color BLUE_LIGHT_1 = new Color(186, 206, 230);
  private final static Color BLUE_LIGHT_2 = new Color(218, 230, 242);

  private final static Color GRAY_LIGHT_1 = new Color(245, 245, 245);
  private final static Color GRAY_LIGHT_2 = new Color(240, 240, 240);
  private final static Color GRAY_LIGHT_3 = new Color(216, 216, 216);
  private final static int DEFAULT_PADDING = 5;

  private final static String[] headers = new String[]{
      "중대재해처벌법 항목",
      "세이플리 대응 문서",
      "목표 업로드 주기",
      "파일 개수",
      "총 목표 파일개수"
  };

  private static int ROW_INDEX = 1;

  public static void main(String[] args) throws IOException {

    float PADDING = 50f;

    BufferedReader br = new BufferedReader(new FileReader("./example-data/criteria.json"));
    StringBuilder sb = new StringBuilder();
    String data;
    while ((data = br.readLine()) != null) {
      sb.append(data);
    }

    ObjectMapper objectMapper = new ObjectMapper();
    List<SafetyHealthItem> safetyHealthItems = objectMapper.readValue(sb.toString(), new TypeReference<List<SafetyHealthItem>>() {});

    try (PDDocument document = new PDDocument()) {
      PDPage page = PDPageFactory.generateHorizontalPage();
      document.addPage(page);

      PDType0Font font = FontStore.getPdFont(document);
      PDType0Font boldFont = FontStore.getPdBoldFont(document);

      final TableBuilder tableBuilder = Table.builder()
          .addColumnsOfWidth(200, 250, 100, 100, 100)
          .fontSize(8)
          .font(font)
          .padding(DEFAULT_PADDING)
          .borderColor(Color.WHITE);

      Row header = Row.builder()
          .add(TextCell.builder().text(headers[0]).borderWidth(1).build())
          .add(TextCell.builder().text(headers[1]).borderWidth(1).build())
          .add(TextCell.builder().text(headers[2]).borderWidth(1).build())
          .add(TextCell.builder().text(headers[3]).borderWidth(1).build())
          .add(TextCell.builder().text(headers[4]).borderWidth(1).build())
          .backgroundColor(BLUE_DARK)
          .textColor(Color.WHITE)
          .font(boldFont)
          .fontSize(10)
          .horizontalAlignment(CENTER)
          .build();

      // add header
      tableBuilder.addRow(header);

      // body
      generateBodyTable(safetyHealthItems, boldFont, font, tableBuilder);
      generateBodyTable(safetyHealthItems, boldFont, font, tableBuilder);

      Table myTable = tableBuilder.build();

      float startY = page.getMediaBox().getHeight() - PADDING;
      float titleY = 50f;

      try (final PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
        RepeatedHeaderTableDrawer.builder()
            .page(page)
            .table(myTable)
            .contentStream(contentStream)
            .startX(PADDING)
            .startY(startY - titleY)
            .endY(PADDING) // 테이블이 이 Y 좌표에 도달하면 새 페이지를 시작합니다
            .build()
            .draw(
                () -> document,
                PDPageFactory::generateHorizontalPage,
                page.getMediaBox().getHeight() - (startY - titleY));
      }

      document.save(new File("./outputs/table-test.pdf"));
    }
  }

  private static void generateBodyTable(
      List<SafetyHealthItem> safetyHealthItems,
      PDType0Font boldFont,
      PDType0Font font,
      TableBuilder tableBuilder) {
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
        Color backgroundColor = (ROW_INDEX++ % 2 == 0) ? GRAY_LIGHT_1 : GRAY_LIGHT_2;

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
