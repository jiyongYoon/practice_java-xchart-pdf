package jy.practice.utils.factory;

import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.cell.TextCell;

public class RowFactory {

  public static Row spaceRow(float height) {
    return Row.builder().add(TextCell.builder().text("").build()).height(height).build();
  }

}
