package jy.practice.component;

import java.awt.BorderLayout;
import java.awt.Color;
import lombok.Builder;
import lombok.Getter;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.vandeseer.easytable.settings.BorderStyle;
import org.vandeseer.easytable.settings.BorderStyleInterface;
import org.vandeseer.easytable.settings.HorizontalAlignment;
import org.vandeseer.easytable.settings.VerticalAlignment;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.Row.RowBuilder;
import org.vandeseer.easytable.structure.Table;
import org.vandeseer.easytable.structure.Table.TableBuilder;
import org.vandeseer.easytable.structure.cell.ImageCell;
import org.vandeseer.easytable.structure.cell.TextCell;

@Getter
public class DashboardTableComponent {

  protected PDType0Font font;
  protected PDType0Font boldFont;
  protected int fontSize;
  protected int boldFontSize;

  protected float xColSize;
  protected int xColCount;
  protected float yColSize;
  protected int yColCount;

  protected float rowHeight;
  protected PDImageXObject documentImage;

  private final static Color BLUE_LIGHT_1 = new Color(186, 206, 230);
  private final static Color GRAY_LIGHT_3 = new Color(216, 216, 216);
  private final static Color GRAY_DARK = new Color(130, 130, 130);

  protected Table dashboardTableComponent;

  @Builder
  public DashboardTableComponent(PDType0Font font, PDType0Font boldFont, int fontSize,
      int boldFontSize, float xColSize, int xColCount, float yColSize, int yColCount,
      float rowHeight,
      PDImageXObject documentImage) {
    this.font = font;
    this.boldFont = boldFont;
    this.fontSize = fontSize;
    this.boldFontSize = boldFontSize;
    this.xColSize = xColSize;
    this.xColCount = xColCount;
    this.yColSize = yColSize;
    this.yColCount = yColCount;
    this.rowHeight = rowHeight;
    this.documentImage = documentImage;
  }

  public float getComponentWidth() {
    return xColSize * xColCount;
  }

  public float getComponentHeight() {
    return this.getRowHeight() * 7;
  }

  public TableBuilder generateBuilder() {
    float[] addColumnsOfWidthArr = new float[xColCount];
    for (int i = 0; i < xColCount - 1; i++) {
      addColumnsOfWidthArr[i] = xColSize;
    }
    addColumnsOfWidthArr[xColCount - 1] = xColSize / 2;

    return Table.builder()
        .addColumnsOfWidth(addColumnsOfWidthArr)
        .backgroundColor(Color.WHITE)
        .horizontalAlignment(HorizontalAlignment.LEFT)
        .verticalAlignment(VerticalAlignment.MIDDLE)
        .font(font)
        .fontSize(fontSize)
//        .borderWidth(1).borderStyle(BorderStyle.SOLID) // 디버깅용
        .padding(2);
  }

  public Table generate(String title, int documentCount, PDImageXObject pieChart, PDImageXObject tagImage) {
    int totalColSpan = xColCount;
    float totalHeight = rowHeight;

    TableBuilder tableBuilder = generateBuilder();
    tableBuilder
        .addRow(spaceTopRow(totalHeight, totalColSpan, 1f, BorderStyle.SOLID, GRAY_LIGHT_3))
        .addRow(Row.builder()
            .add(spaceLeftCell(10, 1f, BorderStyle.SOLID, GRAY_LIGHT_3))
            .add(ImageCell.builder().image(pieChart).colSpan(5).rowSpan(5).build())
            .add(spaceRightCell(1, 1f, BorderStyle.SOLID, GRAY_LIGHT_3))
            .build())
        .addRow(Row.builder()
            .add(spaceLeftCell(1, 1f, BorderStyle.SOLID, GRAY_LIGHT_3))
            .add(TextCell.builder()
                .text(title)
                .colSpan(9)
                .font(boldFont).fontSize(boldFontSize)
                .build())
            .add(spaceRightCell(1, 1f, BorderStyle.SOLID, GRAY_LIGHT_3))
            .build())
        .addRow(Row.builder()
            .add(spaceLeftCell(10, 1f, BorderStyle.SOLID, GRAY_LIGHT_3))
            .add(spaceRightCell(1, 1f, BorderStyle.SOLID, GRAY_LIGHT_3))
            .build())
        .addRow(Row.builder()
            .add(spaceLeftCell(1, 1f, BorderStyle.SOLID, GRAY_LIGHT_3))
            .add(ImageCell.builder().image(documentImage).colSpan(1).rowSpan(1).build())
            .add(TextCell.builder()
                .text(String.format("%d개", documentCount))
                .textColor(GRAY_DARK)
                .colSpan(2)
                .build())
            .add(spaceCell(1))
            .add(ImageCell.builder().image(tagImage).colSpan(3).build())
            .add(spaceCell(2))
            .add(spaceRightCell(1, 1f, BorderStyle.SOLID, GRAY_LIGHT_3))
            .build())
        .addRow(Row.builder()
            .add(spaceLeftCell(10, 1f, BorderStyle.SOLID, GRAY_LIGHT_3))
            .add(spaceRightCell(1, 1f, BorderStyle.SOLID, GRAY_LIGHT_3))
            .build())
        .addRow(spaceBottomRow(totalHeight, totalColSpan, 1f, BorderStyle.SOLID, GRAY_LIGHT_3))
        .build();

    return tableBuilder.build();
  }

  private TextCell spaceCell(int colSpan) {
    return TextCell.builder().text("").colSpan(colSpan).build();
  }

  private TextCell spaceLeftCell(int colSpan, float borderWidth, BorderStyle borderStyle, Color borderColor) {
    return TextCell.builder().text("").colSpan(colSpan).borderWidthLeft(borderWidth).borderStyleLeft(borderStyle).borderColor(borderColor).build();
  }

  private TextCell spaceRightCell(int colSpan, float borderWidth, BorderStyle borderStyle, Color borderColor) {
    return TextCell.builder().text("").colSpan(colSpan).borderWidthRight(borderWidth).borderStyleRight(borderStyle).borderColor(borderColor).build();
  }

  private Row spaceRow(float height, int colSpan) {
    return Row.builder().add(TextCell.builder().text("").colSpan(colSpan).build()).height(height).build();
  }
  
  private Row spaceTopRow(float height, int colSpan, float borderWidth, BorderStyle borderStyle, Color borderColor) {
    return Row.builder()
        .add(TextCell.builder().text("").colSpan(1).borderWidthLeft(borderWidth).borderWidthTop(borderWidth).borderStyleLeft(borderStyle).borderStyleTop(borderStyle).build())
        .add(TextCell.builder().text("").colSpan(colSpan - 2).borderWidthTop(borderWidth).borderStyleTop(borderStyle).build())
        .add(TextCell.builder().text("").colSpan(1).borderWidthRight(borderWidth).borderWidthTop(borderWidth).borderStyleRight(borderStyle).borderStyleTop(borderStyle).build())
        .borderColor(borderColor)
        .height(height).build();
  }

  private Row spaceBottomRow(float height, int colSpan, float borderWidth, BorderStyle borderStyle, Color borderColor) {
    return Row.builder()
        .add(TextCell.builder().text("").colSpan(1).borderWidthLeft(borderWidth).borderWidthBottom(borderWidth).borderStyleLeft(borderStyle).borderStyleBottom(borderStyle).build())
        .add(TextCell.builder().text("").colSpan(colSpan - 2).borderWidthBottom(borderWidth).borderStyleBottom(borderStyle).build())
        .add(TextCell.builder().text("").colSpan(1).borderWidthRight(borderWidth).borderWidthBottom(borderWidth).borderStyleRight(borderStyle).borderStyleBottom(borderStyle).build())
        .borderColor(borderColor)
        .height(height).build();
  }
}
