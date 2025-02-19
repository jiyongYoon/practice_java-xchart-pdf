package jy.practice.component;

import org.vandeseer.easytable.structure.Row;
import org.vandeseer.easytable.structure.cell.TextCell;

public class RowCellFactory {

  public static Row spaceRow(float height) {
    return Row.builder().add(TextCell.builder().text("").build()).height(height).build();
  }

}
