package jy.practice.drawer.extension;

import java.util.function.Supplier;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.vandeseer.easytable.TableDrawer;

@SuperBuilder
public class PageCountTableDrawer extends TableDrawer {

  @Getter
  @Builder.Default
  private int pagesDrawn = 0;

  @Override
  protected PDPage determinePageToDraw(int index, PDDocument document,
      Supplier<PDPage> pageSupplier) {
    pagesDrawn++;
    return super.determinePageToDraw(index, document, pageSupplier);
  }
}
