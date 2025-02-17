package jy.practice.component;

import org.apache.pdfbox.pdmodel.PDDocument;

public class PDDocumentSingleton {

  private static final PDDocument PD_DOCUMENT_FOR_IMAGES_AND_FONTS = new PDDocument();

  public static PDDocument getSingleton() {
    return PD_DOCUMENT_FOR_IMAGES_AND_FONTS;
  }

}
