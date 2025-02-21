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
public class SapaCriteriaDepth1Element {
    private String name;
    private Color row1Color;
    private Color row2Color;
    private Color firstRowBackgroundColor;
    private List<SapaCriteriaDepth2Element> sapaCriteriaDepth2ElementList;

}
