package jy.practice.utils.factory;

import java.awt.Color;
import org.vandeseer.easytable.settings.BorderStyle;
import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.cell.TextCell;

public class CellFactory {

  public static TextCell spaceCell(int colSpan) {
    return TextCell.builder().text("").colSpan(colSpan).build();
  }

  public static TextCell spaceLeftCell(int colSpan, float borderWidth, BorderStyle borderStyle, Color borderColor) {
    return TextCell.builder().text("").colSpan(colSpan).borderWidthLeft(borderWidth).borderStyleLeft(borderStyle).borderColor(borderColor).build();
  }

  public static TextCell spaceRightCell(int colSpan, float borderWidth, BorderStyle borderStyle, Color borderColor) {
    return TextCell.builder().text("").colSpan(colSpan).borderWidthRight(borderWidth).borderStyleRight(borderStyle).borderColor(borderColor).build();
  }

  public static Row spaceTopRow(float height, int colSpan, float borderWidth, BorderStyle borderStyle, Color borderColor) {
    return Row.builder()
        .add(TextCell.builder().text("").colSpan(1).borderWidthLeft(borderWidth).borderWidthTop(borderWidth).borderStyleLeft(borderStyle).borderStyleTop(borderStyle).build())
        .add(TextCell.builder().text("").colSpan(colSpan - 2).borderWidthTop(borderWidth).borderStyleTop(borderStyle).build())
        .add(TextCell.builder().text("").colSpan(1).borderWidthRight(borderWidth).borderWidthTop(borderWidth).borderStyleRight(borderStyle).borderStyleTop(borderStyle).build())
        .borderColor(borderColor)
        .height(height).build();
  }

  public static Row spaceBottomRow(float height, int colSpan, float borderWidth, BorderStyle borderStyle, Color borderColor) {
    return Row.builder()
        .add(TextCell.builder().text("").colSpan(1).borderWidthLeft(borderWidth).borderWidthBottom(borderWidth).borderStyleLeft(borderStyle).borderStyleBottom(borderStyle).build())
        .add(TextCell.builder().text("").colSpan(colSpan - 2).borderWidthBottom(borderWidth).borderStyleBottom(borderStyle).build())
        .add(TextCell.builder().text("").colSpan(1).borderWidthRight(borderWidth).borderWidthBottom(borderWidth).borderStyleRight(borderStyle).borderStyleBottom(borderStyle).build())
        .borderColor(borderColor)
        .height(height).build();
  }

}
