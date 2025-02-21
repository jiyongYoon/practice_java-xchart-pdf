package jy.practice.utils.factory;

import lombok.experimental.UtilityClass;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

@UtilityClass
public class PDPageFactory {

  private static final PDRectangle horizontalPdRectangle =
      new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth());

  public static PDPage generateHorizontalPage() {
    return new PDPage(horizontalPdRectangle);
  }
}
