package jy.practice.component;

import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.COSObjectable;
import org.apache.pdfbox.pdmodel.common.PDRectangle;

public class PDPageComponent {

  public static PDPage generateA4() {
    return new PDPage(PDRectangle.A4);
  }



}
