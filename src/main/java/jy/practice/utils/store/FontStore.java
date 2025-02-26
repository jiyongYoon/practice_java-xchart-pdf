package jy.practice.utils.store;

import java.awt.Font;
import java.awt.FontFormatException;
import java.io.IOException;
import java.io.InputStream;
import lombok.experimental.UtilityClass;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

@UtilityClass
public class FontStore {

  private static final PDDocument PD_DOCUMENT_FOR_Fonts = PDDocumentSingletonStore.getInstance();

  private static final PDType0Font pdFont;
  private static final PDType0Font pdBoldFont;
  private static final Font font;
  public static int TITLE_FONT_SIZE = 25;
  public static int BODY_FONT_SIZE = 15;

  public static int LARGE_CATEGORY_FONT_SIZE = 20;
  public static int SMALL_CATEGORY_FONT_SIZE = 17;

  static {
    String pdFontPath = "example/fonts/Pretendard-Regular.ttf";
    String pdBoldFontPath = "example/fonts/Pretendard-Bold.ttf";
    try {
      pdFont = PDType0Font.load(PD_DOCUMENT_FOR_Fonts, FontStore.class.getClassLoader().getResourceAsStream(pdFontPath));
      font = Font.createFont(Font.TRUETYPE_FONT, FontStore.class.getClassLoader().getResourceAsStream(pdFontPath));
    } catch (IOException | FontFormatException e) {
      throw new RuntimeException("Cannot read Font file, name=" + pdFontPath, e);
    }

    try {
      InputStream boldFontStream = FontStore.class.getClassLoader().getResourceAsStream(pdBoldFontPath);
      pdBoldFont = PDType0Font.load(PD_DOCUMENT_FOR_Fonts, boldFontStream);
    } catch (IOException e) {
      throw new RuntimeException("Cannot read Font file, name=" + pdBoldFontPath, e);
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
