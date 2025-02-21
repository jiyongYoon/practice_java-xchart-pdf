package jy.practice.drawer.material;

import java.awt.Color;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardComponentElement {

  private Color componentBorderColor;

  private String title;
  private int okCount;
  private int totalCount;
  private Color countTextColor;

  private PDImageXObject chartImage;
  private PDImageXObject tagImage;

  public static int calculateTotalCount(int okCount, int rate) {
    if (rate == 0 || rate == 100) {
      return okCount;
    }
    return Math.round((float)okCount * 100 / rate);
  }

}
