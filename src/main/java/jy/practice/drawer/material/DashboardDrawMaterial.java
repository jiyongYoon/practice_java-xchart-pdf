package jy.practice.drawer.material;

import java.awt.Color;
import java.awt.Font;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardDrawMaterial implements DrawMaterial {

  protected Font insideChartFont; // 차트안에 들어갈 폰트

  protected PDType0Font font;
  protected PDType0Font boldFont;
  protected int fontSize;
  protected int boldFontSize;

  protected int roundIndex; // n차 할때 n
  protected String roundStartDate; // 2024-01-01
  protected String roundEndDate; // 2024-12-31

  protected float xColSize; // 열 가로 사이즈
  protected int xColCount; // 열 갯수

  protected float rowHeight; // row 한 칸 높이
  protected float componentInterval; // 컴포넌트들끼리의 간격
  protected PDImageXObject documentImage; // 문서모양 이미지

  protected List<DashboardComponentElement> dashboardComponentElementList; // 각 컴포넌트 요소 데이터들

//  protected DashboardComponentElement unclassifiedElement; // 미분류파일
  protected PDImageXObject unclassifiedChartImage; // 미분류파일 차트모양 이미지

  public float getComponentWidth() {
    return this.xColCount * this.xColSize;
  }

  public float getComponentHeight() {
    return this.getRowHeight() * 7; // 전체 테이블 행의 갯수
  }

}
