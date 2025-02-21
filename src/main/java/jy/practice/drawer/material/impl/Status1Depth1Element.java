package jy.practice.drawer.material.impl;

import java.awt.Color;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Status1Depth1Element {
  private int id;
  private String name;
  private int count;
  private Color row1Color;
  private Color row2Color;
  private Color firstRowBackgroundColor;
  private List<Status1Depth2Element> statusDept2ElementList;
}
