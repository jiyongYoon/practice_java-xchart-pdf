package jy.practice.utils.store;

import lombok.experimental.UtilityClass;
import org.apache.pdfbox.pdmodel.PDDocument;

@UtilityClass
public class PDDocumentSingletonStore {

  private static final PDDocument PD_DOCUMENT_FOR_IMAGES_AND_FONTS = new PDDocument();

  public static PDDocument getInstance() {
    return PD_DOCUMENT_FOR_IMAGES_AND_FONTS;
  }

}
