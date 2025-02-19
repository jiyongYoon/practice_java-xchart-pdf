package jy.practice.store;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.File;
import java.io.IOException;
import jy.practice.component.PDDocumentSingleton;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

public class FontStore {

  private static final PDDocument PD_DOCUMENT_FOR_Fonts = PDDocumentSingleton.getSingleton();

  private static final PDType0Font pdFont;
  private static final PDType0Font pdBoldFont;
  private static final Font font;
  public static int TITLE_FONT_SIZE = 25;
  public static int BODY_FONT_SIZE = 15;

  public static int LARGE_CATEGORY_FONT_SIZE = 20;
  public static int SMALL_CATEGORY_FONT_SIZE = 17;

  static {
    String pdFontName = "Pretendard-Regular.ttf";
    String pdFontBoldName = "Pretendard-Bold.ttf";
    try {
      pdFont = PDType0Font.load(PD_DOCUMENT_FOR_Fonts, new File("./font/" + pdFontName));
      font = Font.createFont(Font.TRUETYPE_FONT, new File("./font/" + pdFontName));
    } catch (IOException | FontFormatException e) {
      throw new RuntimeException("Cannot read Font file, name=" + pdFontName, e);
    }

    try {
      pdBoldFont = PDType0Font.load(PD_DOCUMENT_FOR_Fonts, new File("./font/" + pdFontBoldName));
    } catch (IOException e) {
      throw new RuntimeException("Cannot read Font file, name=" + pdFontBoldName, e);
    }
  }

  public static PDType0Font getPdFont() {
    return pdFont;
  }

  public static PDType0Font getPdBoldFont() {
    return pdBoldFont;
  }

  public static Font getFont() {
    return font;
  }

}
