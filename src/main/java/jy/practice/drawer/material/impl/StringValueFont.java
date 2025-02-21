package jy.practice.drawer.material.impl;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.vandeseer.easytable.settings.HorizontalAlignment;
import org.vandeseer.easytable.settings.VerticalAlignment;

@Getter
@Setter(AccessLevel.PACKAGE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StringValueFont {

  private String value;
  private PDType0Font font;
  private int fontSize;
  @Default
  private HorizontalAlignment horizontalAlignment = HorizontalAlignment.CENTER;
  @Default
  private VerticalAlignment verticalAlignment = VerticalAlignment.MIDDLE;

}
