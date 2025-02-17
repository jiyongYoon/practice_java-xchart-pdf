package jy.practice.component;

import java.io.IOException;
import java.util.Objects;
import jy.practice.Main;
import org.apache.pdfbox.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.knowm.xchart.BitmapEncoder;
import org.knowm.xchart.BitmapEncoder.BitmapFormat;
import org.knowm.xchart.internal.chartpart.Chart;

public class PDImageGenerator {

  private static final PDDocument PD_DOCUMENT_FOR_IMAGES = PDDocumentSingleton.getSingleton();

  public static PDImageXObject generate(String resourceFullPath) throws IOException {
    return PDImageXObject.createFromByteArray(
        PD_DOCUMENT_FOR_IMAGES,
        IOUtils.toByteArray(Objects.requireNonNull(Main.class.getClassLoader().getResourceAsStream(resourceFullPath))),
        resourceFullPath.split("\\.")[0]);
  }

  public static PDImageXObject generate(Chart chart) throws IOException {
    return PDImageXObject.createFromByteArray(
        PD_DOCUMENT_FOR_IMAGES,
        BitmapEncoder.getBitmapBytes(chart, BitmapFormat.PNG),
        "chart");
  }

}
