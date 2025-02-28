package jy.practice.utils.store;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import lombok.experimental.UtilityClass;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

@UtilityClass
public class FontStore {

  private static final Font font;
  public static int TITLE_FONT_SIZE = 25;
  public static int BODY_FONT_SIZE = 15;

  public static int LARGE_CATEGORY_FONT_SIZE = 20;
  public static int SMALL_CATEGORY_FONT_SIZE = 17;
  public static final String pdFontPath = "example/fonts/Pretendard-Regular.ttf";
  public static final String pdBoldFontPath = "example/fonts/Pretendard-Bold.ttf";

  static {
    try {
      font = Font.createFont(Font.TRUETYPE_FONT, FontStore.class.getClassLoader().getResourceAsStream(pdFontPath));
    } catch (IOException | FontFormatException e) {
      throw new RuntimeException("Cannot read Font file, name=" + pdFontPath, e);
    }
  }

  public static PDType0Font getPdFont(PDDocument document) {
    try {
      return PDType0Font.load(document, FontStore.class.getClassLoader().getResourceAsStream(pdFontPath));
    } catch (IOException e) {
      throw new RuntimeException("Cannot read Font file, name=" + pdFontPath, e);
    }
  }

  public static PDType0Font getPdBoldFont(PDDocument document) {
    try {
      return PDType0Font.load(document, FontStore.class.getClassLoader().getResourceAsStream(pdBoldFontPath));
    } catch (IOException e) {
      throw new RuntimeException("Cannot read Font file, name=" + pdFontPath, e);
    }
  }

  public static Font getFont() {
    return font;
  }

}
