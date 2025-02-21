package jy.practice.drawer.material;

import java.awt.Color;
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
public class TitleDrawMaterial implements DrawMaterial {
  private PDPage page; // 호출부에서 생성한 page, 즉 제목을 작성할 페이지

  private int titleColumnsOfWidth;
  private PDType0Font titleFont;
  private int titleFontSize;
  private Color titleTextColor;
  private Color borderColor;
  private Color backgroundColor;

  private String titleName;
}
