package jy.practice.utils.factory;

import java.io.IOException;
import jy.practice.utils.store.PDDocumentSingletonStore;
import lombok.experimental.UtilityClass;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

@UtilityClass
public class PDImageFactory {

  public static PDImageXObject generate(byte[] imageSourceByteArray, String pdImageXObjectName) throws IOException {
    return PDImageXObject.createFromByteArray(
        PDDocumentSingletonStore.getInstance(),
        imageSourceByteArray,
        pdImageXObjectName
    );
  }
}
