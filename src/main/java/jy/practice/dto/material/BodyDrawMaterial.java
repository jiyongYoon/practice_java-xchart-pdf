package jy.practice.dto.material;

import java.awt.Color;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BodyDrawMaterial implements DrawMaterial {
  private PDPage page; // 호출부에서 생성한 page, 즉 Body를 작성할 페이지

  // defalut값 - 따로 bodyElementList 안에 없으면 이 값을 꺼내서 사용함
  private int bodyColumnsOfWidth;
  private PDType0Font bodyFont;
  private int bodyFontSize;
  private Color bodyTextColor;
  private Color borderColor;
  private Color backgroundColor;

  private List<StringValueFont> bodyElementList; // 한 줄에 하나씩 들어갈 내용들
}
