package jy.practice.store;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class PDPageStore {

  private static final PDRectangle horizontalPdRectangle =
      new PDRectangle(PDRectangle.A4.getHeight(), PDRectangle.A4.getWidth());

  public static PDPage generateHorizontalPage() {
    return new PDPage(horizontalPdRectangle);
  }
}
