package jy.practice.dto.material;

import java.util.List;
import jy.practice.component.test.dto.SafetyHealthItem;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SapaCriteriaDrawMaterial implements DrawMaterial {

  private float[] columnsOfWidth;
  private String[] headerValue;
  private PDType0Font font;
  private PDType0Font boldFont;
  private int bodyFontSize;
  private int titleFontSize;
  private List<SafetyHealthItem> safetyHealthItemList;

}
