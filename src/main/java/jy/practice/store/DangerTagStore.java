package jy.practice.store;

import java.io.IOException;
import java.util.Objects;
import jy.practice.Main;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

public class DangerTagStore {
  private static final PDDocument PD_DOCUMENT_FOR_IMAGES = new PDDocument();
  private static final String veryDanger = "매우위험";
  private static final String danger = "위험";
  private static final String normal = "보통";
  private static final String safe = "안전";
  private static final String verySafe = "매우안전";

  private static final String extension = ".png";
  private static final String prefix = "step=";

  public static PDImageXObject pick(long percent) throws IOException {
    String resourceName = "my/tag/" + pickString(percent);

    return PDImageXObject.createFromByteArray(
        PD_DOCUMENT_FOR_IMAGES,
        IOUtils.toByteArray(
            Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream(resourceName))),
        "my/tag");
  }

  private static String pickString(long percent) {
    String filename = "";
    if (0 <= percent && percent < 20) {
      filename = veryDanger;
    } else if (20 <= percent && percent < 40) {
      filename = danger;
    } else if (40 <= percent && percent < 60) {
      filename = normal;
    } else if (60 <= percent && percent < 80) {
      filename = safe;
    } else if (80 <= percent && percent <= 100) {
      filename = verySafe;
    } else {
      filename = null;
    }

    return prefix + filename + extension;
  }
}
